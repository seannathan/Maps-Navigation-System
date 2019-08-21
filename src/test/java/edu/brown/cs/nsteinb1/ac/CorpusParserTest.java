package edu.brown.cs.nsteinb1.ac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

/**
 * Tests getters, multiple parse calls, and removal of non-letters.
 *
 * @author nicole
 *
 */
public class CorpusParserTest {

  @Test
  // Tests that all fields are instantiated as empty.
  public void testNew() {
    CorpusParser parser = new CorpusParser();
    assertTrue(parser != null);
    assertTrue(parser.getWords().isEmpty());
    assertTrue(parser.getUnigrams().isEmpty());
    assertTrue(parser.getBigrams().isEmpty());
  }

  @Test
  // Tests that parse method doesn't throw exception on valid files and returns
  // correct results. Also tests that multiple calls to parse don't overwrite.
  public void testParse() {
    CorpusParser parser = new CorpusParser();

    // Make sure parse call doesn't throw exception.
    int caught = 0;
    try {
      parser.parse("data/maps/small_streets.txt");
    } catch (IOException e) {
      caught = 1;
    }

    assertEquals(caught, 0);

    List<String> pwords = parser.getWords();

    // Correct words are returned.
    assertEquals("Chihiro Ave", pwords.get(0));
    assertEquals("Chihiro Ave", pwords.get(1));
    assertTrue(pwords.size() == 7);

    // Correct unigram probability is returned.
    assertEquals(parser.getUnigrams().count("Chihiro Ave"), 2);
    assertEquals(parser.getUnigrams().count("Kamaji Pl"), 1);

    // Correct bigrams are found.
    assertTrue(parser.getBigrams().get("Chihiro Ave") == 2);

    try {
      parser.parse("data/autocorrect/split.txt");
    } catch (IOException e) {
      caught = 1;
    }

    assertEquals(caught, 0);

    assertEquals(parser.getWords().get(0), "Chihiro Ave");
    assertEquals(parser.getWords().get(7), "split");

  }

  /**
   * Tests that IOException is thrown when parser is given an invalid file name.
   *
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testIoException() throws IOException {
    CorpusParser parser = new CorpusParser();
    parser.parse("txt");
  }

}
