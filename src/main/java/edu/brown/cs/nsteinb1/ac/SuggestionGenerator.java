package edu.brown.cs.nsteinb1.ac;

import edu.brown.cs.nsteinb1.trie.TrieNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Queries trie for autocorrect suggestions such as prefix, whitespace, and led.
 *
 * @author nicole
 *
 */
final class SuggestionGenerator {

  private SuggestionGenerator() {
  }

  /**
   * If separating the specified string by whitespace into two words is
   * possible, add it to the list.
   *
   * @param string
   *          input string
   * @param root
   *          root of trie
   * @return string split by whitespace
   */
  public static List<String> findWhitespace(String string, TrieNode root) {
    List<String> strings = new ArrayList<>();
    String firstHalf = "";
    TrieNode temp = root;
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);
      firstHalf = firstHalf + ch;

      // If we've reached a leaf, stop descending through the tree.
      if (temp.getChild(ch) == null) {
        break;
      } else if (temp.getChild(ch).isWordEnd()) {

        TrieNode inTemp = root;
        String secondHalf = "";

        // If we've found that the first part of the string is a valid word,
        // check if the second half is valid. If so, add it to the list.
        for (int j = i + 1; j < string.length(); j++) {

          char inCh = string.charAt(j);
          secondHalf = secondHalf + inCh;

          if (inTemp.getChild(inCh) == null) {
            break;
          } else if (j == string.length() - 1
              && inTemp.getChild(inCh).isWordEnd()) {
            strings.add(firstHalf + " " + secondHalf);
          } else {
            inTemp = inTemp.getChild(inCh);
          }

        }

        temp = temp.getChild(ch);
      } else {
        temp = temp.getChild(ch);
      }
    }

    // so if butterfly is in the trie, butter fly won't be returned

    return strings;
  }

  /**
   * Return all the words in the trie that when compared to the input word lie
   * within the specified LED.
   *
   * @param node
   *          - node to search descendants of
   * @param word
   *          - input word
   * @param compare
   *          - word in trie to compare input to
   * @param leds
   *          - list of valid words
   * @param led
   *          - specified LED
   * @return leds
   */
  public static List<String> findLeds(TrieNode node, String word,
      String compare, List<String> leds, int led) {
    String newWord;

    // For every descendant of the current node...
    for (TrieNode cc : node.getChildren().values()) {

      newWord = compare + cc.getChar();

      int currentLed = Led.getLed(word, newWord);

      // If the new word is valid and has an LED <= led, add it to return.
      if (cc.isWordEnd() && currentLed <= led && !word.equals(newWord)) {
        leds.add(newWord);
      }

      // If we haven't reached a leaf, go down every path of the tree finding
      // words until we decide to stop recurring. Stop recurring when we've
      // reached the word's length, since adding more characters cannot help in
      // this case.
      boolean recur = word.length() > compare.length() || currentLed < led;
      if (recur && !cc.getChildren().isEmpty()) {
        leds = findLeds(cc, word, newWord, leds, led);
      }
    }

    return leds;
  }

  /**
   * Returns all the words in the trie starting with the specified prefix.
   *
   * @param prefix
   *          prefix of possible words
   * @return words
   */
  public static List<String> getWordsWithPrefix(String prefix, TrieNode temp) {
    List<String> words = new ArrayList<>();

    if (prefix.equals("")) {
      return words;
    }

    // Search the tree to see if the prefix exists.
    for (int i = 0; i < prefix.length(); i++) {
      char ch = prefix.charAt(i);
      if (temp.getChild(ch) == null) {
        // If not, return empty list.
        return words;
      } else {
        temp = temp.getChild(ch);
      }
    }

    // Find the node corresponding to the last character in the prefix.
    // Iterate through its children in order to get all the words that could
    // follow it.
    words = recursiveGetSuffix(temp, prefix, new ArrayList<String>());
    return words;

  }

  /**
   * Iterates through all descendants of a node and returns all valid words
   * stemming from its prefix.
   *
   * @param ch
   *          "root" node
   * @param word
   *          prefix to find endings for
   * @return list of valid suffixes
   */
  private static List<String> recursiveGetSuffix(TrieNode ch, String word,
      List<String> words) {
    String newWord;

    // For every descendant of the current node...
    for (TrieNode cc : ch.getChildren().values()) {

      newWord = word + cc.getChar();

      if (cc.isWordEnd()) {
        words.add(newWord);
      }

      // If we haven't reached a leaf, go down every path of the tree finding
      // words. Add words to the list if you reach a word end.
      if (!cc.getChildren().isEmpty()) {
        words = recursiveGetSuffix(cc, newWord, words);
      }
    }

    return words;
  }

}
