import models.ChessMatch;

import java.io.RandomAccessFile;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String fileName = System.getProperty("user.dir") + "\\src\\chessStats.csv";
    private static final String primaryKeyIndexFileName = System.getProperty("user.dir") + "\\src\\pkindex.csv";
    private static final int dataFileLineSize = 40;
    private static final PKIndexer indexer = new PKIndexer(fileName, primaryKeyIndexFileName, dataFileLineSize);

    public static void main(String[] args) {
//        File indexFile = new File(primaryKeyIndexFileName);
//        indexFile.delete();

        RandomAccessFile pkIndex = indexer.loadPrimaryKeyIndex();
        int action = 0;

        do {
            System.out.println("Qual ação você deseja executar?");
            System.out.println("0 - Sair");
            System.out.println("1 - Procurar por código");
            System.out.println("------------------------");
            System.out.println("9 - Recalcular arquivo de índice primário");
            action = scanner.nextInt();

            if (action == 1) {
                searchByPk();
            } else if (action == 9) {
                indexer.loadPrimaryKeyIndex(true);
            } else {
                System.err.println("Ação não reconhecida");
            }
        } while (action != 0);

        System.out.println("a");
    }

    private static void searchByPk() {
        System.out.println("Digite o código do registro a ser procurado");
        long code = scanner.nextLong();
        ChessMatch chessMatchData = indexer.findByPk(code);
        System.out.println(chessMatchData == null ? "Dado não encontrado" : "Dado encontrado:\n" + chessMatchData);
    }
}