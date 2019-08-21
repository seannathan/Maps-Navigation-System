package edu.brown.cs.nsteinb1.ac;

import com.google.common.collect.Multiset;

import java.util.Comparator;

/**
 * Compares strings based on their unigram probability.
 *
 * @author nicole
 *
 */
public class UnigramProbComparator implements Comparator<String> {

  private Multiset<String> unigrams;

  /**
   * Constructor for comparator.
   *
   * @param uni
   *          unigrams multiset
   */
  public UnigramProbComparator(Multiset<String> uni) {
    unigrams = uni;
  }

  /**
   * Compares two strings based on their unigram probability. If a is a unigram
   * that occurs more frequently, -1 will be returned. If they occur the same
   * amount, 0.
   *
   * @param a
   *          first string
   * @param b
   *          second string
   * @return int representing comparison
   */
  public int compare(String a, String b) {
    int diff = unigrams.count(a) - unigrams.count(b);

    if (diff < 0) {
      return 1;
    } else if (diff == 0) {
      return 0;
    } else {
      return -1;
    }
  }

}
