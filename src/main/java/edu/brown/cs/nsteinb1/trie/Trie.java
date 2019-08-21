package edu.brown.cs.nsteinb1.trie;

/**
 * Implements a structure to hold letters so you can quickly query whether a
 * word is contained in the current corpus.
 *
 * @author nicole
 *
 */
public class Trie {

  private final TrieNode root;

  /**
   * Instantiate root field of Trie.
   */
  public Trie() {
    root = new TrieNode(Character.MIN_VALUE, false);
  }

  /**
   * Insert word into Trie.
   *
   * @param word
   *          word from corpus
   */
  public void insert(String word) {
    // Descend the trie adding the word's characters as needed.
    TrieNode temp = root;
    for (int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);

      // Check if ch is already in the node's children. If not, create a new
      // child. If it's the last letter in the word, pass in "true" for
      // isEndOfWord.
      boolean isEnd = i == word.length() - 1;
      if (temp.getChild(ch) == null) {
        temp.addChild(ch, new TrieNode(ch, isEnd));
      } else if (isEnd) {
        temp.getChild(ch).setEnd(isEnd);
      }

      // Descend another level.
      temp = temp.getChild(ch);
    }
  }

  /**
   * Searches that a word is contained in the trie (as a full word).
   *
   * @param word
   *          letters
   * @return if word is in trie or not
   */
  public boolean containsWord(String word) {

    if (word.equals("")) {
      return false;
    }

    TrieNode temp = root;

    // Descend the trie and check if the character exists at that level.
    // If not, return false.
    for (int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);
      if (temp.getChild(ch) == null) {
        return false;
      } else if (i == word.length() - 1 && !temp.getChild(ch).isWordEnd()) {
        return false;
      } else {
        temp = temp.getChild(ch);
      }
    }

    return true;
  }

  /**
   * Searches that a word is contained in the trie (as a prefix or full word).
   *
   * @param word
   *          letters
   * @return if prefix is in trie or not
   */
  public boolean containsPrefix(String word) {
    // If word is just empty string, return false.
    if (word.equals("")) {
      return false;
    }

    TrieNode temp = root;

    // Descend the trie and check if the character exists at that level.
    // If not, return false.
    for (int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);
      if (temp.getChild(ch) == null) {
        return false;
      } else {
        temp = temp.getChild(ch);
      }
    }

    return true;
  }

  /**
   * Return root of trie.
   *
   * @return root
   */
  public TrieNode getRoot() {
    return root;
  }

}
