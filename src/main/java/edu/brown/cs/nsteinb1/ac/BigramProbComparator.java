package edu.brown.cs.nsteinb1.ac;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Compares strings based on their bigram probability.
 *
 * @author nicole
 *
 */
public class BigramProbComparator implements Comparator<String> {

  private HashMap<String, Integer> bigrams;

  /**
   * Constructor for bigram probability comparator.
   *
   * @param bi
   *          bigram frequencies
   */
  public BigramProbComparator(HashMap<String, Integer> bi) {
    bigrams = bi;
  }

  /**
   * Compares two strings based on the bigram probability of the last two words.
   * If a is a bigram that occurs more frequently, -1 will be returned since we
   * want it to come first in the list. If they occur the same amount, use a
   * unigram probability comparator to break the tie.
   *
   * @param a
   *          first string
   * @param b
   *          second string
   * @return -1 if string a's frequency is higher
   */
  public int compare(String a, String b) {
    int diff = 0;

    // If the words are included as bigrams, compare based on difference in
    // frequency. If bigram doesn't exist, set its frequency to zero.
    int argA = 0;
    int argB = 0;
    if (bigrams.containsKey(a)) {
      argA = bigrams.get(a);
    }

    if (bigrams.containsKey(b)) {
      argB = bigrams.get(b);
    }

    diff = argA - argB;

    if (diff < 0) {
      return 1;
    } else if (diff == 0) {
      return 0;
    } else {
      return -1;
    }
  }

}
