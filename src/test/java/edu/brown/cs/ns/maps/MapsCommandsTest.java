package edu.brown.cs.ns.maps;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * JUnit tests for MapsCommands.
 *
 * @author nicole
 *
 */
public class MapsCommandsTest {

  /**
   * Tests that commands is not null after instantiation.
   */
  @Test
  public void testNew() {
    MapsCommands commands = new MapsCommands();
    assertTrue(commands != null);
  }

  /**
   * Tests that isCommand works as expected.
   */
  @Test
  public void testIsCommand() {
    List<String> commands = new ArrayList<>();
    commands.add("ways");
    commands.add("map");
    commands.add("nearest");
    commands.add("route");
    commands.add("suggest");

    MapsCommands mapsCommands = new MapsCommands();
    for (String c : commands) {
      assertTrue(mapsCommands.isCommand(c));
    }

    assertTrue(!mapsCommands.isCommand("prefix"));
    assertTrue(!mapsCommands.isCommand(" map"));
  }

  /**
   * Tests that route works correctly.
   */
  @Test
  public void testRoute() {
    MapsCommands mcommands = new MapsCommands();

    // Database has not been loaded.
    List<Way> stack = mcommands.route(
        "route \"Chihiro Ave\" \"Sootball Ln\" \"Yubaba St\" \"Sootball Ln\""
            .split(" "));
    assertTrue(stack == null);

    mcommands.processCommands("map data/maps/smallMaps.sqlite3");

    // Not enough arguments.
    stack = mcommands.route("route \"Chihiro Ave\" \"Sootball Ln\"".split(" "));
    assertTrue(stack == null);

    // Quotes not used and not numbers.
    stack = mcommands.route("route Chihiro Ave Sootball Ln".split(" "));
    assertTrue(stack == null);

    // Street names used correctly.
    stack = mcommands.route(
        "route \"Chihiro Ave\" \"Sootball Ln\" \"Yubaba St\" \"Sootball Ln\""
            .split(" "));

    assertTrue(stack.get(0).getStart().getName().equals("/n/1"));
    assertTrue(stack.get(0).getEnd().getName().equals("/n/4"));
    assertTrue(stack.get(0).getId().equals("/w/3"));
    assertTrue(stack.size() == 1);

    // Doubles used correctly.
    stack = mcommands.route("route -71.3 41.8 42 -72".split(" "));
    assertTrue(stack.get(0).getStart().getName().equals("/n/2"));
    assertTrue(stack.get(0).getEnd().getName().equals("/n/5"));
    assertTrue(stack.get(0).getId().equals("/w/4"));
    assertTrue(stack.size() == 3);
  }
}
