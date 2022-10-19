import exceptions.TDEException;
import helpers.IndexLineDTO;
import helpers.PartitionNode;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Indexer {

    private final String dataFileName;
    private final String primaryKeyIndexFileName;
    private final int dataFileLineSize;
    private static final long PARTITION_SIZE = 5;

    public Indexer(String fileName, String primaryKeyIndexFileName, int dataFileLineSize) {
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

    public RandomAccessFile loadPrimaryKeyIndex() {
        try {
            File indexFile = new File(this.primaryKeyIndexFileName);

//            if (!indexFile.exists()) {
//                if (!indexFile.createNewFile()) {
//                    throw new Error("Não foi possível criar o arquivo de índice de chave primária");
//                }

                this.populatePrimaryIndexFile();
//            }

            return new RandomAccessFile(indexFile, "r");
        } catch (IOException | TDEException e) {
            System.err.println("Não foi possível abrir o arquivo de índice primário");
            e.printStackTrace();
            return null;
        }
    }

    private void populatePrimaryIndexFile() throws IOException, TDEException {
        System.out.println("Criando arquivo de índice primário");

        try (RandomAccessFile dataFile = new RandomAccessFile(dataFileName, "r"); RandomAccessFile indexFile = new RandomAccessFile(this.primaryKeyIndexFileName, "rw")) {
            final long lineQtd = dataFile.length() / dataFileLineSize;
            long partitionQtd = lineQtd / PARTITION_SIZE;
            final int expectedIndexFileLineSize = 19;

            /*for (int partitionIdx = 0; partitionIdx <= partitionQtd; partitionIdx++) {
                long initialLine = partitionIdx * PARTITION_SIZE;
                long finalLine = Math.min((partitionIdx + 1) * PARTITION_SIZE, lineQtd);
                long partitionInitialPosition = initialLine * dataFileLineSize;
                long partitionFinalPosition = finalLine * dataFileLineSize;

//                System.out.printf(
//                        "----- Partição %d (%d até %d)%n",
//                        partitionIdx,
//                        partitionInitialPosition,
//                        partitionFinalPosition
//                );

                long lastAddedId = 0L;
                for (long i = initialLine; i < finalLine; i++) {
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

                    String newIndexRegister = String.format(
                            "%d,%s%n",
                            smallestIdSearch,
                            padLeftZeros(
                                    String.valueOf(smallestIdPosition),
                                    10
                            )
                    );

//                    System.out.printf("%d = %s", i, newIndexRegister);
                    indexFile.write(newIndexRegister.getBytes());
                }
            }*/

            List<Long> addresses = LongStream.range(0L, partitionQtd - 1)
                    .map(n -> n * PARTITION_SIZE * expectedIndexFileLineSize)
                    .boxed()
                    .toList();

            PartitionNode root = new PartitionNode(expectedIndexFileLineSize, PARTITION_SIZE, addresses, indexFile);

            ArrayList<String> a = new ArrayList<>();
            for (int i = 0; i < lineQtd; i++) {
                IndexLineDTO smallest = root.getSmallest();
                a.add(smallest.getLine());
                root.refreshIf(smallest.getId());
            }
            System.out.println("dsadadsa" + a);
        }
    }
}
