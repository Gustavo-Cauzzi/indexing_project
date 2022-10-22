import models.ChessMatch;
import models.PkIndex;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = System.getProperty("user.dir") + "\\src\\chessStats.csv";
    private static final String PRIMARY_KEY_INDEX_FILE_NAME = System.getProperty("user.dir") + "\\src\\pkindex.csv";
    private static final int DATA_FILE_LINE_SIZE = 40;
    private static final PKIndexer pkIndexer = new PKIndexer(FILE_NAME, PRIMARY_KEY_INDEX_FILE_NAME, DATA_FILE_LINE_SIZE);

    public static void main(String[] args) {
        int action;

        List<PkIndex> pkIndex = pkIndexer.loadPrimaryKeyIndex();

        do {
            System.out.println("Qual ação você deseja executar?");
            System.out.println("0 - Sair");
            System.out.println("1 - Procurar por código");
            System.out.println("------------------------");
            System.out.println("9 - Recalcular arquivo de índice primário");
            action = scanner.nextInt();

            if (action == 1) {
                searchByPk(pkIndex);
            } else if (action == 9) {
                pkIndexer.loadPrimaryKeyIndex(true);
            } else {
                System.err.println("Ação não reconhecida");
            }
        } while (action != 0);
    }

    private static void searchByPk(List<PkIndex> pkIndex) {
        System.out.println("Digite o código do registro a ser procurado");
        long code = scanner.nextLong();
        ChessMatch chessMatchData = pkIndexer.findByPk(code, pkIndex);
        System.out.println(chessMatchData == null ? "Dado não encontrado" : "Dado encontrado:\n" + chessMatchData);
    }
}