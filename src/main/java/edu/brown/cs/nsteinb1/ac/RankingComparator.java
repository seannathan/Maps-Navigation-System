package edu.brown.cs.nsteinb1.ac;

import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Compares strings first by their bigram probability, then by their unigram
 * probability, and then alphabetically.
 *
 * @author nicole
 *
 */
public class RankingComparator implements Comparator<String> {

  private HashMap<String, Integer> bigrams;
  private Multiset<String> unigrams;
  private List<String> split;

  /**
   * Constructor for ranking comparator.
   *
   * @param bi
   *          bigram frequencies
   * @param uni
   *          unigram frequencies
   * @param splitWords
   *          strings separated by whitespace command (skip bigram)
   */
  public RankingComparator(HashMap<String, Integer> bi, Multiset<String> uni,
      List<String> splitWords) {
    bigrams = bi;
    unigrams = uni;
    split = splitWords;
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
    String[] splitA = a.split(" ");
    String[] splitB = b.split(" ");

    // Sort by bigram probability.
    BigramProbComparator biCom = new BigramProbComparator(bigrams);

    int biDiff = 0;

    String[] splitLastA = splitA;
    String[] splitLastB = splitB;

    // If string has been split by whitespace, compare bigram using first word
    // of split.
    if (split.contains(a)) {
      splitLastA = Arrays.copyOf(splitA, splitA.length - 1);
    }

    if (split.contains(b)) {
      splitLastB = Arrays.copyOf(splitA, splitA.length - 1);
    }

    // Get the last two words of each input if they exist.
    if (splitLastA.length > 1 && splitLastB.length > 1) {
      String bigramA = splitLastA[splitLastA.length - 2] + " "
          + splitLastA[splitLastA.length - 1];
      String bigramB = splitLastB[splitLastB.length - 2] + " "
          + splitB[splitLastB.length - 1];

      biDiff = biCom.compare(bigramA, bigramB);
    }

    if (biDiff == 0) {
      // If there's a tie in bigram probability, compare by unigram probability.
      UnigramProbComparator uniCom = new UnigramProbComparator(unigrams);

      // Get last word of input.
      String countA = splitA[splitA.length - 1];
      String countB = splitB[splitB.length - 1];

      // If either string has been split by whitespace, compare by the first
      // half of the new string.
      if (split.contains(a)) {
        countA = splitA[splitA.length - 2];
      }

      if (split.contains(b)) {
        countB = splitB[splitB.length - 2];
      }

      int uniDiff = uniCom.compare(countA, countB);

      if (uniDiff == 0) {
        // If there's a tie, return alphabetically.
        Ordering<String> natural = Ordering.natural();
        return natural.compare(a, b);
      } else {
        return uniDiff;
      }
    } else {
      return biDiff;
    }

  }

}
