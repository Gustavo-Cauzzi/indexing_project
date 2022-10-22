import exceptions.TDEException;
import helpers.PartitionNode;
import models.ChessMatch;
import models.PkIndex;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public class PKIndexer {
    private static final String TMP_INDEX_FILE_NAME = System.getProperty("user.dir") + "\\src\\pkindexTmp.csv";
    private final String dataFileName;
    private final String primaryKeyIndexFileName;
    private final int dataFileLineSize;
    private static final long PARTITION_SIZE = 5;
    private int indexFileLineSize = 19;

    public PKIndexer(String fileName, String primaryKeyIndexFileName, int dataFileLineSize) {
        this.dataFileName = fileName;
        this.primaryKeyIndexFileName = primaryKeyIndexFileName;
        this.dataFileLineSize = dataFileLineSize;
    }

    public String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public List<PkIndex> loadPrimaryKeyIndex() {
        return this.loadPrimaryKeyIndex(false);
    }

    public List<PkIndex> loadPrimaryKeyIndex(boolean forceReexec) {
        try {
            File indexFile = new File(this.primaryKeyIndexFileName);

            if (!indexFile.exists() || forceReexec) {
                if (!indexFile.createNewFile()) {
                    throw new TDEException("Não foi possível criar o arquivo de índice de chave primária");
                }

                this.populatePrimaryIndexFile();
            }

            System.out.println("Construindo arquivo de índice primário em memória");
            try (RandomAccessFile indexFileReader = new RandomAccessFile(indexFile, "r")) {
                List<PkIndex> pkIndex = new ArrayList<>();
                this.iterateOverFile(indexFileReader, line -> pkIndex.add(new PkIndex(line)));
                return pkIndex;
            }
        } catch (IOException | TDEException e) {
            System.err.println("Não foi possível abrir o arquivo de índice primário");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void iterateOverFile(RandomAccessFile indexFileReader, Consumer<String> consumer) throws IOException {
        String line;
        do {
            line = indexFileReader.readLine();
            if (line != null) consumer.accept(line);
        } while (line != null);
    }

    private void populatePrimaryIndexFile() throws IOException, TDEException {
        System.out.println("Criando arquivo de índice primário");

        File indexTmpFileRef = new File(TMP_INDEX_FILE_NAME);

        //@formatter:off
        try (RandomAccessFile dataFile = new RandomAccessFile(dataFileName, "r"); RandomAccessFile indexTmpFile = new RandomAccessFile(indexTmpFileRef, "rw"); RandomAccessFile indexFile = new RandomAccessFile(this.primaryKeyIndexFileName, "rw")) { //@formatter:on
            final long lineQtd = dataFile.length() / dataFileLineSize;
            long partitionQtd = lineQtd / PARTITION_SIZE;

            for (int partitionIdx = 0; partitionIdx <= partitionQtd; partitionIdx++) {
                System.out.printf("Ordenando partição %d", partitionIdx);
                long initialLine = partitionIdx * PARTITION_SIZE;
                long finalLine = Math.min((partitionIdx + 1) * PARTITION_SIZE, lineQtd);
                long partitionInitialPosition = initialLine * dataFileLineSize;

                long lastAddedId = 0L;
                for (long i = initialLine; i <= finalLine; i++) {
                    long smallestIdSearch = Long.MAX_VALUE;
                    long smallestIdPosition = -1;
                    dataFile.seek(partitionInitialPosition);

                    for (long j = initialLine; j <= finalLine; j++) {
                        long currentPosition = dataFile.getFilePointer();
                        String line = dataFile.readLine();

                        if (line != null) {
                            long id = Long.parseLong(line.split(",")[0]);

                            if (id > lastAddedId && id < smallestIdSearch) {
                                smallestIdSearch = id;
                                smallestIdPosition = currentPosition;
                            }
                        }
                    }

                    if (smallestIdSearch == Long.MAX_VALUE)
                        throw new TDEException("Não há mais registros para serem pesquisados");

                    lastAddedId = smallestIdSearch;

                    indexTmpFile.write(String.format("%d,%s%n", smallestIdSearch, padLeftZeros(String.valueOf(smallestIdPosition), 10)).getBytes());
                }
            }

            this.indexFileLineSize = this.getLineLength(indexTmpFile);

            List<Long> addresses = LongStream.range(0L, partitionQtd + 1).map(n -> n * PARTITION_SIZE * indexFileLineSize).boxed().toList();

            System.out.println("Gerando árvore para junção de partições no arquivo de índice primário");
            PartitionNode root = new PartitionNode(indexFileLineSize, PARTITION_SIZE, addresses, indexTmpFile);

            System.out.println("Calculando indíces ordenados e guardando no arquivo");
            for (int i = 0; i < lineQtd; i++) {
                PkIndex smallest = root.getSmallest();
                indexFile.write(smallest.getLine().getBytes());
                root.refreshIfEqualTo(smallest.getId());
            }

            if (!indexTmpFileRef.delete()) {
                throw new TDEException("Não foi possível excluir o arquivo de indice temporário");
            }
        }
    }

    private int getLineLength(RandomAccessFile indexTmpFile) throws IOException, TDEException {
        long auxFilePointer = indexTmpFile.getFilePointer();
        indexTmpFile.seek(0);
        String line = indexTmpFile.readLine();
        if (line == null) {
            throw new TDEException("Não foi possível ler o conteúdo do arquivo de índice temporário");
        }
        indexTmpFile.seek(auxFilePointer);
        return line.length();
    }

    public ChessMatch findByPk(long code, List<PkIndex> indexList) {
        try (RandomAccessFile dataFile = new RandomAccessFile(dataFileName, "r"); RandomAccessFile indexFile = new RandomAccessFile(this.primaryKeyIndexFileName, "r")) {
            int lineQtd = (int) (indexFile.length() / this.indexFileLineSize);

            PkIndex index = this.binarySearch(indexList, 0, lineQtd, code);

            if (index == null) {
                return null;
            }

            dataFile.seek(index.getAddress());
            String dataLine = dataFile.readLine();
            if (dataLine == null) {
                throw new TDEException(String.format("Não foi possível encontrar dado original no arquivo de dados (%s)", index));
            }

            return new ChessMatch(dataLine);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PkIndex binarySearch(List<PkIndex> indexList, int start, int end, long idToSearch) throws IOException {
        if (end >= start) {
            int mid = start + (end - start) / 2;
            System.out.printf("Pesquisa binária de %d a %d (mid: %d)%n", start, end, mid);

            PkIndex data = indexList.get(mid);

            if (data.getId() == idToSearch) {
                return data;
            } else if (data.getId() > idToSearch) {
                return binarySearch(indexList, start, mid - 1, idToSearch);
            } else {
                return binarySearch(indexList, mid + 1, end, idToSearch);
            }
        }

        return null;
    }
}
