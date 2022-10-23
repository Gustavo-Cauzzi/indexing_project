import models.TrieNode;
import utils.TdeUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Optional;

public class MemoryIndexers {
    private String dataFileName;

    public MemoryIndexers() {
    }

    public MemoryIndexers(String dataFileName) {
        this.dataFileName = dataFileName;
    }

    public Optional<TrieNode> loadUsernameIndex() {
        TrieNode trie = new TrieNode();
        System.out.println("Carregando árvore Trie de nomes");

        try (RandomAccessFile f = new RandomAccessFile(dataFileName, "r")) {
            TdeUtils.iterateOverFile(f, line -> {
                String[] split = line.split(",");
                trie.insert(split[split.length - 1].trim());
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(trie);
    }
}
