import exceptions.TDEException;
import helpers.WinnersDTO;
import models.PkIndex;
import models.TrieNode;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = System.getProperty("user.dir") + "\\src\\data\\chessStats.csv";
    private static final FileIndexers fileIndexers = new FileIndexers(FILE_NAME);
    private static final MemoryIndexers secondaryIndexer = new MemoryIndexers(FILE_NAME);

    public static void main(String[] args) throws TDEException {
        int action;

        Map<String, List<String>> playerMatchesIndex = fileIndexers.loadPlayerMatchesIndex();
        TrieNode winnerNamesTrie = secondaryIndexer.loadUsernameIndex()
            .orElseThrow(() -> new TDEException("Não foi possível gerar o índice de nomes com árvore Trie"));
        List<PkIndex> pkIndex = fileIndexers.loadPrimaryKeyIndex();

        do {
            System.out.println("Qual ação você deseja executar?");
            System.out.println("0 - Sair");
            System.out.println("1 - Procurar por código");
            System.out.println("2 - Ver maiores ganhadores");
            System.out.println("3 - Procurar por nome");
            System.out.println("4 - Procurar partidas de um jogador");
            System.out.println("------------------------");
            System.out.println("9 - Recalcular arquivo de índice primário");
            action = scanner.nextInt();

            if (action == 1) {
                searchByPk(pkIndex);
            } else if (action == 2) {
                showTopWinners(winnerNamesTrie);
            } else if (action == 3) {
                findName(winnerNamesTrie);
            } else if (action == 4) {
                findPlayerMatches(playerMatchesIndex);
            } else if (action == 9) {
                pkIndex = fileIndexers.loadPrimaryKeyIndex(true);
            } else {
                System.err.println("Ação não reconhecida");
            }
        } while (action != 0);
    }

    private static void findPlayerMatches(Map<String, List<String>> playerMatchesIndex) {
        System.out.println("Digite o nome do jogador:");
        String name = scanner.next();
        if (!playerMatchesIndex.containsKey(name)) {
            System.out.println("Jogador não encontrado!");
            return;
        }
        List<String> addresses = playerMatchesIndex.get(name);
        fileIndexers.findByAdresses(addresses).forEach(System.out::println);
    }

    private static void findName(TrieNode winnerNamesTrie) {
        System.out.println("Digite o nome a ser pesquisado:");
        String name = scanner.next();
        System.out.println(
            winnerNamesTrie.find(name)
                .map(result -> String.format("Nome: %s%n Vitórias: %d", result.getName(), result.getWins()))
                .orElse("Não encontrado")
        );
    }

    private static void showTopWinners(TrieNode winnerNamesTrie) {
        System.out.println("Top de maiores ganhadores da plataforma:");
        List<WinnersDTO> topWinners = winnerNamesTrie.getTopWinners();
        topWinners.subList(0, 9)
            .forEach(winner -> System.out.printf("%s - %d%n", winner.getName(), winner.getWins()));
        System.out.println("Mostrar todos? (s/n)");
        String action = scanner.next();
        if (action.equalsIgnoreCase("s")) {
            topWinners.forEach(winner -> System.out.printf("%s - %d%n", winner.getName(), winner.getWins()));
        }
    }

    private static void searchByPk(List<PkIndex> pkIndex) {
        System.out.println("Digite o código do registro a ser procurado");
        long code = scanner.nextLong();
        System.out.println(
            fileIndexers.findByPk(code, pkIndex)
                .map(chessMatchData -> "Dado encontrado:\n" + chessMatchData)
                .orElse("Dado não encontrado")
        );
    }
}