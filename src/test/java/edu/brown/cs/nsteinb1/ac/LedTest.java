package edu.brown.cs.nsteinb1.ac;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests Led class.
 *
 * @author nicole
 *
 */
public class LedTest {

  @Test
  // Tests that getLed returns the correct edit distance between two words.
  public void testGetLed() {

    // Tests for same word.
    assertEquals(Led.getLed("at", "at"), 0);

    // Tests for addition.
    assertEquals(Led.getLed("ha", "hah"), 1);

    // Tests for replacement.
    assertEquals(Led.getLed("hare", "hire"), 1);

    // Tests for deletion and replacement.
    assertEquals(Led.getLed("appply", "apple"), 2);

    // Tests for replacement, addition, and deletion.
    assertEquals(Led.getLed("hiya", "hyen"), 3);

    // Tests for empty string.
    assertEquals(Led.getLed("yay", ""), 3);
    assertEquals(Led.getLed("", "yw"), 2);
    assertEquals(Led.getLed("", ""), 0);

    // Tests durks.
    assertEquals(Led.getLed("durk", "durks"), 1);

    // Tests durks.
    assertEquals(Led.getLed("slit", "split"), 1);

  }
}
