import java.util.List;

public class TrieNode {
    char c;
    boolean isWord;
    int numberOfRepetitions;
    List<TrieNode> nodeList;

    public TrieNode(char c, boolean isWord, int numberOfRepetitions, List<TrieNode> nodeList) {
        this.c = c;
        this.isWord = isWord;
        this.numberOfRepetitions = numberOfRepetitions;
        this.nodeList = nodeList;
    }

    public TrieNode() {
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public boolean isWord() {
        return isWord;
    }

    public void setWord(boolean word) {
        isWord = word;
    }

    public int getNumberOfRepetitions() {
        return numberOfRepetitions;
    }

    public void setNumberOfRepetitions(int numberOfRepetitions) {
        this.numberOfRepetitions = numberOfRepetitions;
    }

    public List<TrieNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TrieNode> nodeList) {
        this.nodeList = nodeList;
    }
}
