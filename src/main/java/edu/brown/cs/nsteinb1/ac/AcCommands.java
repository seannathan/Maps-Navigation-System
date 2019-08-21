package edu.brown.cs.nsteinb1.ac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.nsteinb1.repl.Commands;
import edu.brown.cs.nsteinb1.trie.Trie;

/**
 * Includes and executes all REPL commands that pertain solely to autocorrect.
 *
 * @author nicole
 *
 */
public class AcCommands implements Commands {

  static final int SUGGEST_LENGTH = 8;

  private int led = 0;

  private Trie trie = new Trie();
  private HashMap<String, Integer> bigrams = new HashMap<>();
  private Multiset<String> unigrams = HashMultiset.create();
  private CorpusParser parser = new CorpusParser();

  /**
   * Instantiates AutocorrectCommands.
   *
   */
  public AcCommands() {
    led = 1;
  }

  /**
   * Return if the command given is covered by the class it's called on.
   *
   * @param command
   *          - command
   * @return boolean
   */
  public boolean isCommand(String command) {
    return command.equals("led") || command.equals("ac")
        || command.equals("corpus");
  }

  /**
   * Process user input.
   *
   * @param command
   *          - A string from the command line.
   */
  public void processCommands(String command) {
    // If there's trailing whitespace, don't return suggestions.
    int trailing = 0;
    if (command.charAt(command.length() - 1) == ' ') {
      trailing = 1;
    }

    // Split command into individual strings.
    String[] splitCommand = command.split(" ");

    switch (splitCommand[0]) {
      case "led":
        this.editDistance(splitCommand);
        break;
      case "ac":
        List<String> sugs = this.autocorrect(splitCommand, trailing);
        for (String s : sugs) {
          System.out.println(s);
        }
        break;
      case "corpus":
        this.parseCorpus(splitCommand);
        break;
      default:
        System.out.printf("ERROR: '%s' is not a valid command.%n",
            splitCommand[0]);
    }

  }

  /**
   * Executed led command.
   *
   * @param splitCommand
   *          string from command line.
   */
  public void editDistance(String[] splitCommand) {
    // Should have no or 1 arguments.
    if (splitCommand.length > 2) {
      System.out.printf("ERROR: Command format is 'led' or 'led <int>'.%n");
      return;
    }

    // If user requests status, return whether status is currently on or off.
    if (splitCommand.length == 1) {
      System.out.printf("led %d%n", led);
      return;
    }

    // If user changes edit distance, update accordingly. Return error if
    // argument is not an integer or is otherwise invalid.
    try {
      int i = Integer.parseInt(splitCommand[1]);
      if (i >= 0) {
        led = i;
      } else {
        System.out.println("ERROR: led must be a non-negative integer.");
        return;
      }
    } catch (Exception e) {
      System.out.println("ERROR: led must be an integer.");
      return;
    }

  }

  /**
   * Returns autocorrect suggestions to passed in text.
   *
   * @param splitCommand
   *          command to be parsed
   * @param trailing
   *          if original command had trailing whitespace
   * @return suggestions
   */
  public List<String> autocorrect(String[] splitCommand, int trailing) {

    // Start collecting suggestions to return.
    List<String> suggestions = new ArrayList<>();

    // ac must have an argument.
    if (splitCommand.length < 2) {
      System.out.println("ERROR: Command is 'suggest <word or phrase>'.");
      return suggestions;
    }

    // Reconcatenate input.
    String input = "";
    for (String s : splitCommand) {
      if (!input.equals("")) {
        input = input + " ";
      }
      input = input + s;
    }

    // If trailing space, don't generate suggestions.
    if (trailing == 1) {
      return suggestions;
    }

    // If corpus has an exact match to the last word, store the full input.
    String exact = input.substring(SUGGEST_LENGTH);

    // If prefix is on, store all words that follow the prefix.
    // Return prefixes.
    List<String> prefixes = SuggestionGenerator.getWordsWithPrefix(exact,
        trie.getRoot());
    for (String str : prefixes) {
      suggestions.add(str);
    }

    // If led is turned on, store all words that have an edit distance <= led.
    if (led > 0) {
      List<String> leds = SuggestionGenerator.findLeds(trie.getRoot(), exact,
          "", new ArrayList<>(), led);

      // Store all the found words.
      for (String s : leds) {
        suggestions.add(s);
      }
    }

    // If whitespace is on, store all two-word strings that it could be broken
    // into.
    List<String> split = new ArrayList<>();
    List<String> ws = SuggestionGenerator.findWhitespace(exact, trie.getRoot());
    for (String s : ws) {
      suggestions.add(s);
      split.add(s);
    }

    // Sort by ranking algorithm depending on if smart mode is toggled.
    Collections.sort(suggestions,
        new RankingComparator(bigrams, unigrams, split));

    if (trie.containsWord(exact)) {
      suggestions.add(0, exact);
    }

    // Return the first five suggestions if they exist.
    int limit;
    limit = Math.min(suggestions.size(), 5);
    HashSet<String> noDups = new LinkedHashSet<>(suggestions.subList(0, limit));
    List<String> ret = new ArrayList<>();
    ret.addAll(noDups);
    return ret;

  }

  /**
   * Parse corpus and create trie.
   *
   * @param splitCommand
   *          command to be parsed
   */
  public void parseCorpus(String[] splitCommand) {

    // Corpus should have one argument.
    if (splitCommand.length != 2) {
      System.out.println("ERROR: Command format is 'map <filename>'.");
      return;
    }

    // Argument must be .txt file and have readable data.
    String[] txt = splitCommand[1].split("\\.");
    if (txt.length != 2 || !txt[1].equals("txt")) {
      System.out.printf("ERROR: '%s' must be a .txt file.%n", splitCommand[1]);
      return;
    }

    // Check that corpus can be parsed and parse it.
    try {
      parser.parse(splitCommand[1]);
    } catch (IOException e) {
      System.out.println("ERROR: File not found.");
      return;
    }

    // Insert all parsed words into the trie.
    for (String word : parser.getWords()) {
      trie.insert(word);
    }

    // Store bigrams and unigrams.
    bigrams = new HashMap<String, Integer>(parser.getBigrams());
    unigrams = parser.getUnigrams();
  }

  /**
   * Return trie.
   *
   * @return private trie
   */
  public Trie getTrie() {
    return trie;
  }

}
