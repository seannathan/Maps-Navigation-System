package edu.brown.cs.nsteinb1.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrieTest {

  @Test
  // Assert that trie fields have expected values on instantiation.
  public void testNew() {
    Trie trie = new Trie();
    assertTrue(trie != null);
    assertEquals(trie.getRoot().getChar(), Character.MIN_VALUE);
  }

  @Test
  // Tests that words get added to trie correctly.
  public void testInsert() {
    Trie trie = new Trie();
    trie.insert("hello");
    trie.insert("hilo");
    trie.insert("hi");

    assertTrue(trie.getRoot().getChildren().containsKey('h'));

    TrieNode h = trie.getRoot().getChild('h');
    assertTrue(h.getChildren().containsKey('e'));
    assertFalse(h.getChildren().containsKey('n'));

    assertTrue(h.getChildren().get('i').isWordEnd());
    assertFalse(h.getChildren().get('e').isWordEnd());

    assertTrue(trie.containsWord("hilo"));
    assertTrue(trie.containsWord("hi"));
  }

  @Test
  // Tests that search returns only if full word is in trie.
  // Also checks the containsPrefix works as expected.
  public void testSearch() {
    Trie trie = new Trie();
    trie.insert("hello");
    trie.insert("hi");
    trie.insert("Hey there!");

    assertFalse(trie.containsWord("hell"));
    assertFalse(trie.containsWord(""));
    assertFalse(trie.containsWord("helloy"));
    assertTrue(trie.containsWord("hello"));
    assertFalse(trie.containsWord("HELLO"));
    assertTrue(trie.containsWord("Hey there!"));
    assertFalse(trie.containsWord("Hey"));
    assertFalse(trie.containsWord("Hey there"));

    assertTrue(trie.containsPrefix("Hey "));
    assertTrue(trie.containsPrefix("hell"));
    assertTrue(trie.containsPrefix("h"));
    assertFalse(trie.containsPrefix("m"));
    assertFalse(trie.containsPrefix(""));
  }

}
