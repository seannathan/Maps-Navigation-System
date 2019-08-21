package edu.brown.cs.nsteinb1.ac;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.nsteinb1.trie.Trie;

/**
 * JUnit tests for AcCommands.
 *
 * @author nicole
 *
 */
public class AcCommandsTest {

  /**
   * Tests that commands is not null after instantiation.
   */
  @Test
  public void testNew() {
    AcCommands commands = new AcCommands();
    assertTrue(commands != null);
  }

  /**
   * Tests that isCommand works as expected.
   */
  @Test
  public void testIsCommand() {
    List<String> commands = new ArrayList<>();
    commands.add("led");
    commands.add("corpus");
    commands.add("ac");

    AcCommands acCommands = new AcCommands();
    for (String c : commands) {
      assertTrue(acCommands.isCommand(c));
    }

    assertTrue(!acCommands.isCommand("prefix"));
    assertTrue(!acCommands.isCommand(" led"));
  }

  /**
   * Tests that corpus is loaded into trie.
   */
  @Test
  public void testParseCorpus() {
    AcCommands acCommands = new AcCommands();

    // Tests that given an input of the wrong number of arguments, the trie is
    // empty.
    acCommands.parseCorpus("one".split(" "));
    assertTrue(acCommands.getTrie().getRoot().getChildren().isEmpty());

    // Tests that given a non-text file, the trie is empty.
    acCommands.processCommands("corpus this");
    assertTrue(acCommands.getTrie().getRoot().getChildren().isEmpty());

    // Tests that given a file with two periods, the trie is empty.
    acCommands.parseCorpus("corpus this.hi.txt".split(" "));
    assertTrue(acCommands.getTrie().getRoot().getChildren().isEmpty());

    // Tests that given a non-existent file, the trie is empty.
    acCommands.parseCorpus("corpus this.txt".split(" "));
    assertTrue(acCommands.getTrie().getRoot().getChildren().isEmpty());

    // Tests that given a correct file, the trie is filled correctly.
    acCommands.parseCorpus("corpus data/maps/small_streets.txt".split(" "));
    Trie trie = acCommands.getTrie();

    assertTrue(trie.containsWord("Chihiro Ave"));
    assertTrue(trie.containsWord("Radish Spirit Blvd"));
    assertFalse(trie.containsWord("Chihiro"));
  }

  /**
   * Test ac command.
   */
  @Test
  public void testAutocorrect() {
    AcCommands acCommands = new AcCommands();
    acCommands.parseCorpus("corpus data/maps/small_streets.txt".split(" "));

    // If ac has no arg, no suggestions are returned.
    assertTrue(acCommands.autocorrect("suggest".split(" "), 0).isEmpty());

    // Test various returns of ac.
    List<String> suggestions = acCommands.autocorrect("suggest C".split(" "),
        0);
    assertTrue(suggestions.get(0).equals("Chihiro Ave"));
    suggestions = acCommands.autocorrect("suggest c".split(" "), 0);
    assertTrue(suggestions.isEmpty());
    suggestions = acCommands.autocorrect("suggest ChihiroAve".split(" "), 0);
    assertTrue(suggestions.get(0).equals("Chihiro Ave"));
    suggestions = acCommands.autocorrect("suggest chihiro Ave".split(" "), 0);
    assertTrue(suggestions.get(0).equals("Chihiro Ave"));
    suggestions = acCommands.autocorrect("ac chihiro ave".split(" "), 0);
    assertTrue(suggestions.isEmpty());
  }

}
