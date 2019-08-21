package edu.brown.cs.nsteinb1.repl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReplTest {

  @Test // (expected = IOException.class)
  // Tests that REPL does not equal null initially.
  public void testNew() {
    Repl repl = new Repl();
    assertTrue(repl != null);
  }

}
