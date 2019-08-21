package edu.brown.cs.nsteinb1.ac;

import java.util.Comparator;

/**
 * Compares strings based on their led from the original word.
 *
 * @author nicole
 *
 */
public class LedComparator implements Comparator<String> {

  private String orig;

  /**
   * Constructor for comparator.
   *
   * @param original
   *          original word
   */
  public LedComparator(String original) {
    orig = original;
  }

  /**
   * Compares two strings based on their led from the original world.
   *
   * @param a
   *          first string
   * @param b
   *          second string
   * @return int representing comparison
   */
  public int compare(String a, String b) {
    int diff = Led.getLed(orig, a) - Led.getLed(orig, b);

    if (diff > 0) {
      return 1;
    } else if (diff == 0) {
      return 0;
    } else {
      return -1;
    }
  }

}
