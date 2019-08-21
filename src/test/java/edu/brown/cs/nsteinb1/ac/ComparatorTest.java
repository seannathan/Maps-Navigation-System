package edu.brown.cs.nsteinb1.ac;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Tests autocorrect's ranking comparator as well as the comparators it's made
 * up of (bigram, unigram, and natural ordering).
 *
 * @author nicole
 *
 */
public class ComparatorTest {

  /**
   * Test that comparator sorts according to bigram probability of strings.
   */
  @Test
  public void testBigram() {
    HashMap<String, Integer> bigrams = new HashMap<>();
    bigrams.put("this that", 4);
    bigrams.put("it i", 2);
    bigrams.put("w xyz", 1);
    bigrams.put("aa abc", 3);

    List<String> suggestions = new ArrayList<>(bigrams.keySet());

    Collections.sort(suggestions, new BigramProbComparator(bigrams));
    assertTrue(suggestions.get(0).equals("this that"));
    assertTrue(suggestions.get(1).equals("aa abc"));
    assertTrue(suggestions.get(2).equals("it i"));
    assertTrue(suggestions.get(3).equals("w xyz"));

  }

  /**
   * Test that comparator sorts according to unigram probability of strings.
   */
  @Test
  public void testUnigram() {
    Multiset<String> unigrams = HashMultiset.create();
    unigrams.add("why");
    unigrams.add("why");
    unigrams.add("why");
    unigrams.add("hey");
    unigrams.add("why");

    List<String> suggestions = new ArrayList<>(unigrams.elementSet());

    Collections.sort(suggestions, new UnigramProbComparator(unigrams));
    assertTrue(suggestions.get(0).equals("why"));
    assertTrue(suggestions.get(1).equals("hey"));

    // Test alphabetical.
    Collections.sort(suggestions);
    assertTrue(suggestions.get(0).equals("hey"));
  }

  /**
   * Test led comparator.
   */
  @Test
  public void testLed() {
    List<String> suggestions = new ArrayList<>();
    suggestions.add("dark");
    suggestions.add("durk");
    suggestions.add("hi");

    Collections.sort(suggestions, new LedComparator("durks"));
    assertTrue(suggestions.get(0).equals("durk"));
    assertTrue(suggestions.get(1).equals("dark"));
    assertTrue(suggestions.get(2).equals("hi"));

  }

  /**
   * Test for accurate behavior in the case of ties and split strings.
   */
  @Test
  public void testEdges() {
    Multiset<String> unigrams = HashMultiset.create();
    unigrams.add("hey");
    unigrams.add("h");
    unigrams.add("h");
    unigrams.add("ey");
    unigrams.add("ey");
    unigrams.add("hi");
    unigrams.add("said");
    unigrams.add("said");

    HashMap<String, Integer> bigrams = new HashMap<>();
    bigrams.put("said hi", 1);
    bigrams.put("said hey", 1);
    bigrams.put("h ey", 2);

    // Let's assume the input was "i said hey". We add exact match later so I
    // won't add that to suggestions.
    List<String> suggestions = new ArrayList<>();
    suggestions.add("i said hi");
    suggestions.add("i said h");
    suggestions.add("i said h ey");

    List<String> split = new ArrayList<>();
    split.add("i said h ey");

    Collections.sort(suggestions,
        new RankingComparator(bigrams, unigrams, split));

    assertTrue(suggestions.get(0).equals("i said hi"));
    assertTrue(suggestions.get(1).equals("i said h"));
    assertTrue(suggestions.get(2).equals("i said h ey"));

    // Test for one word inputs. Will not test by bigram - will sort by unigram
    // frequency and alphabet.
    suggestions = new ArrayList<>();
    suggestions.add("hi");
    suggestions.add("hey");
    suggestions.add("ey");
    suggestions.add("h");

    Collections.sort(suggestions,
        new RankingComparator(bigrams, unigrams, split));

    assertTrue(suggestions.get(0).equals("ey"));
    assertTrue(suggestions.get(1).equals("h"));
    assertTrue(suggestions.get(2).equals("hey"));
    assertTrue(suggestions.get(3).equals("hi"));

    // Tests for where one string has been split by whitespace and the other has
    // not. Should skip to unigram probability.
    suggestions = new ArrayList<>();
    suggestions.add("hey hi");
    suggestions.add("hey h i");
    bigrams.put("hey hi", 1);
    bigrams.put("h i", 2);
    split.add("hey h i");

    Collections.sort(suggestions,
        new RankingComparator(bigrams, unigrams, split));

    assertTrue(suggestions.get(0).equals("hey hi"));
    assertTrue(suggestions.get(1).equals("hey h i"));
  }
}
