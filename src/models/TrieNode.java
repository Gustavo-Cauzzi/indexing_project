package models;

import helpers.WinnersDTO;

import java.util.*;
import java.util.stream.Stream;

public class TrieNode {
    private boolean isRoot = false;
    private Character c;
    private boolean isWord = false;
    private int numberOfRepetitions = 0;
    private Map<Character, TrieNode> childrenNodes = new HashMap<>();

    public TrieNode() {
        this.isRoot = true;
    }

    public TrieNode(char c) {
        this.c = c;
    }

    public void insert(String word) {
        TrieNode current = this;

        for (Character l : word.toCharArray()) {
            current = current.getChildrenNodes().computeIfAbsent(l, TrieNode::new);
        }

        current.setWord(true);
        current.setNumberOfRepetitions(current.getNumberOfRepetitions() + 1);
    }

    private List<WinnersDTO> getAllNames(String strBuilder) {
        String nameAux = strBuilder + (this.c == null ? "" : this.c);
        return Stream.concat(
            isWord ? Stream.of(new WinnersDTO(nameAux, numberOfRepetitions)) : Stream.empty(),
            this.getChildrenNodes()
                .values()
                .stream()
                .map(o -> o.getAllNames(nameAux))
                .flatMap(List::stream)
        ).toList();
    }

    public List<WinnersDTO> getTopWinners() {
        return this.getAllNames("")
            .stream()
            .sorted((a, b) -> b.getWins().compareTo(a.getWins()))
            .toList();
    }

    public Optional<WinnersDTO> find(String name) {
        TrieNode acc = this;
        for (Character c : name.toCharArray()) {
            TrieNode trieNode = acc.childrenNodes.get(c);
            if (trieNode == null) {
                return Optional.empty();
            }
            acc = trieNode;
        }

        if (acc.isWord()) {
            return Optional.of(new WinnersDTO(name, acc.numberOfRepetitions));
        }

        return Optional.empty();
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
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

    public Map<Character, TrieNode> getChildrenNodes() {
        return childrenNodes;
    }

    public void setChildrenNodes(Map<Character, TrieNode> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }
}
