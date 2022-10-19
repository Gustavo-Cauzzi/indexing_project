import utils.TdeUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class Main {
    private static final String fileName = System.getProperty("user.dir") + "\\src\\chessStats.csv";
    private static final String primaryKeyIndexFileName = System.getProperty("user.dir") + "\\src\\pkindex.csv";
    private static final int dataFileLineSize = 40;

    public static void main(String[] args) {
//        File indexFile = new File(primaryKeyIndexFileName);
//        indexFile.delete();

        Indexer indexer = new Indexer(fileName, primaryKeyIndexFileName, dataFileLineSize);
        RandomAccessFile randomAccessFile = indexer.loadPrimaryKeyIndex();
        if (randomAccessFile != null) {
            System.out.println("deu boa");
        } else {
            System.out.println("Deu ruim!");
        }
        System.out.println("a");
    }
}