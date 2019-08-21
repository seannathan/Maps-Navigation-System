package edu.brown.cs.nsteinb1.trie;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

/**
 * Nodes for a trie holding characters and children, as well as a boolean
 * representing if it's an end of a word.
 *
 * @author nicole
 *
 */
public class TrieNode {

  private char ch;
  private boolean endOfWord;
  private HashMap<Character, TrieNode> children;

  /**
   * Constructs trie node with specified fields.
   *
   * @param character
   *          ch
   * @param end
   *          end of word
   */
  public TrieNode(char character, boolean end) {
    ch = character;
    endOfWord = end;
    children = new HashMap<>();
  }

  /**
   * Add a character that could follow this node.
   *
   * @param character
   *          char
   * @param node
   *          node associated with char
   */
  public void addChild(char character, TrieNode node) {
    children.put(character, node);
  }

  /**
   * Change node to end of word if needed.
   *
   * @param isEnd
   *          if char is end of word
   */
  public void setEnd(boolean isEnd) {
    endOfWord = isEnd;
  }

  /**
   * Return the Trie node that corresponds to the specified character.
   *
   * @param character
   *          child's key
   * @return node
   */
  public TrieNode getChild(char character) {
    if (children.containsKey(character)) {
      return children.get(character);
    } else {
      return null;
    }
  }

  /**
   * Returns if the node marks the end of a word.
   *
   * @return endOfWord
   */
  public boolean isWordEnd() {
    return endOfWord;
  }

  /**
   * Return the character associated with the node.
   *
   * @return c
   */
  public char getChar() {
    return ch;
  }

  /**
   * Return the node's children in form of an immutable map.
   *
   * @return children
   */
  public ImmutableMap<Character, TrieNode> getChildren() {
    ImmutableMap<Character, TrieNode> copy = ImmutableMap
        .<Character, TrieNode>builder().putAll(children).build();
    return copy;
  }

}
