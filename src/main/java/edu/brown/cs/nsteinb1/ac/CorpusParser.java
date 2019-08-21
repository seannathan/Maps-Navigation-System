package edu.brown.cs.nsteinb1.ac;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

/**
 * Reads in corpus files as UTF-8 encoded text and stores necessary data.
 *
 * @author nicole
 *
 */
public class CorpusParser {

  private List<String> words = new ArrayList<>();
  private HashMap<String, Integer> bigrams = new HashMap<>();
  private Multiset<String> unigrams = HashMultiset.create();

  /**
   * Constructor for corpus parser.
   *
   */
  public CorpusParser() {
  }

  /**
   * Parses passed-in file as UTF-8 encoded text.
   *
   * @param filename
   *          file to be parsed
   * @throws IOException
   *           exception
   */
  public void parse(String filename) throws IOException {

    try (Stream<String> lines = Files.lines(new File(filename).toPath())) {
      addStreets(lines::iterator);
    }

  }

  /**
   * Keeps track of street frequencies. Since ac only returns full street names,
   * only full street names are added as words.
   *
   * @param streets
   *          street names
   */
  public void addStreets(Iterable<String> streets) {
    for (String s : streets) {
      words.add(s);
      unigrams.add(s);
      String[] splitStreet = s.split(" ");

      String prev = "";
      for (String i : splitStreet) {
        // Keep track of bigram frequency.
        if (!prev.isEmpty()) {
          String bigram = prev + " " + i;
          int frequency = 1;
          if (bigrams.containsKey(bigram)) {
            frequency = bigrams.get(bigram) + 1;
          }
          bigrams.put(bigram, frequency);
        }

        prev = i;
      }
    }
  }

  /**
   * Getter for words.
   *
   * @return the words
   */
  public List<String> getWords() {
    List<String> copy = new ArrayList<>();
    for (String w : words) {
      copy.add(w);
    }
    return copy;
  }

  /**
   * Getter for bigrams.
   *
   * @return the bigrams
   */
  public ImmutableMap<String, Integer> getBigrams() {
    ImmutableMap<String, Integer> copy = ImmutableMap.<String, Integer>builder()
        .putAll(bigrams).build();
    return copy;
  }

  /**
   * Getter for unigrams.
   *
   * @return the unigrams
   */
  public Multiset<String> getUnigrams() {
    Multiset<String> copy = HashMultiset.create();
    for (String e : unigrams) {
      copy.add(e);
    }
    return copy;
  }

}
