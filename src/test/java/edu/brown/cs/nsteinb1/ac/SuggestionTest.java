package edu.brown.cs.nsteinb1.ac;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.brown.cs.nsteinb1.trie.Trie;
import edu.brown.cs.nsteinb1.trie.TrieNode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SuggestionTest {

  @Test
  // Test that all words with a specified prefix are returned.
  public void testGetWordsWithPrefix() {
    Trie trie = new Trie();
    trie.insert("hello");
    trie.insert("hi");
    trie.insert("nooo");

    TrieNode root = trie.getRoot();
    List<String> words = SuggestionGenerator.getWordsWithPrefix("h", root);
    assertTrue(words.contains("hello"));
    assertTrue(words.contains("hi"));
    assertFalse(words.contains("nooo"));
    assertTrue(SuggestionGenerator.getWordsWithPrefix("", root).isEmpty());
    assertTrue(SuggestionGenerator.getWordsWithPrefix("m", root).isEmpty());
  }

  @Test
  // Tests that all words that are within the specified led are returned.
  public void testFindLeds() {
    Trie trie = new Trie();
    trie.insert("hello");
    trie.insert("hi");
    trie.insert("hey");
    trie.insert("hoya");

    List<String> leds = SuggestionGenerator.findLeds(trie.getRoot(), "h", "",
        new ArrayList<>(), 2);

    // Test for addition.
    assertTrue(leds.contains("hi"));
    assertTrue(leds.contains("hey"));
    assertFalse(leds.contains("hello"));

    // Test for subtraction and replacement.
    leds = SuggestionGenerator.findLeds(trie.getRoot(), "hoya", "",
        new ArrayList<>(), 2);
    assertTrue(leds.contains("hey"));
    assertFalse(leds.contains("hi"));
    assertFalse(leds.contains("hoya"));

    // Test for empty.
    leds = SuggestionGenerator.findLeds(trie.getRoot(), "", "",
        new ArrayList<>(), 0);
    assertTrue(leds.isEmpty());
  }

  @Test
  // Tests that strings are correctly separated by whitespace.
  public void testWhitespace() {
    Trie trie = new Trie();
    trie.insert("butterfly");
    trie.insert("butter");
    trie.insert("fly");
    trie.insert("butt");
    trie.insert("b");
    trie.insert("utterfly");

    List<String> ws = SuggestionGenerator.findWhitespace("butterfly",
        trie.getRoot());

    // Should return separated strings even if original word exists in trie.
    assertTrue(ws.contains("butter fly"));

    // Should return multiple separated strings.
    assertTrue(ws.contains("b utterfly"));

    assertFalse(ws.contains("butt erfly"));
    assertFalse(ws.contains(" butterfly"));
  }

}
