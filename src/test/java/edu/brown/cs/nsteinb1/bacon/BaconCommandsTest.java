package edu.brown.cs.nsteinb1.bacon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests bacon commands functionality.
 *
 * @author nicole
 *
 */
public class BaconCommandsTest {

  @Test
  // Tests that is command accepts correct commands.
  public void testIsCommand() {
    BaconCommands bcommands = new BaconCommands();
    assertTrue(bcommands.isCommand("mdb"));
    assertTrue(bcommands.isCommand("connect"));
    assertFalse(bcommands.isCommand("stars"));
    assertFalse(bcommands.isCommand("connect;"));

    bcommands.processCommands("jup");
  }

  @Test
  // Tests that parse quotes works as expected, both on non-quoted and quoted
  // input.
  public void testParseQuotes() {
    String command = "connect steven bob";
    String[] splitCommand = command.split(" ");
    BaconCommands bcommands = new BaconCommands();
    splitCommand = bcommands.parseQuotes(splitCommand);

    // Check that there are only two arguments if there are no quotes.
    assertTrue(splitCommand[0].equals("connect"));
    assertTrue(splitCommand[1].equals("steven bob"));

    // Check if both actors are multi-word with quotes.
    command = "connect \"kevin bacon\" \"amy adams\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[1].equals("\"kevin bacon"));
    assertTrue(splitCommand[2].equals("amy adams\""));

    // Check if one actor is multi-word with quotes.
    command = "connect \"kevin bacon\" amy";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[0].equals("connect"));
    assertTrue(splitCommand[1].equals("\"kevin bacon\" amy"));

    // Check using one-word actors with quotes.
    command = "connect \"kevin\" \"amy\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[1].equals("\"kevin"));
    assertTrue(splitCommand[2].equals("amy\""));

    // Check passing in only one actor.
    command = "connect \"kevin bacon\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[0].equals("connect"));
    assertTrue(splitCommand[1].equals("\"kevin bacon\""));

    // Check passing in actors with middle initial.
    command = "connect \"kevin j. bacon\" \"amy a adams\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[1].equals("\"kevin j. bacon"));
    assertTrue(splitCommand[2].equals("amy a adams\""));

    // Check passing in actors with pseudonyms.
    command = "connect \"kevin \"skippy\" bacon\" \"amy a adams\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[1].equals("\"kevin \"skippy\" bacon"));
    assertTrue(splitCommand[2].equals("amy a adams\""));

    // Check passing in actors with one last quote.
    command = "connect \"kevin\"\" \"amy\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    assertTrue(splitCommand[1].equals("\"kevin\""));
    assertTrue(splitCommand[2].equals("amy\""));

    // Check passing in three actors.
    command = "connect \"kevin\"\" \"amy\" \"scott\"";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    // This is fine because connect only accepts two arguments.
    assertTrue(splitCommand[1].equals("\"kevin\""));
    assertTrue(splitCommand[2].equals("amy"));
    assertTrue(splitCommand[3].equals("scott\""));

    // Check passing in actors with quotes and then more inputs after.
    command = "connect \"kevin\"\" \"amy\" scott";
    splitCommand = command.split(" ");
    splitCommand = bcommands.parseQuotes(splitCommand);

    // This is fine because amy" scott won't show up as a valid name anyway.
    assertTrue(splitCommand[1].equals("\"kevin\""));
    assertTrue(splitCommand[2].equals("amy\" scott"));
  }

  @Test
  // Tests that load database works as expected.
  public void testLoadDatabase() {
    BaconCommands bcommands = new BaconCommands();
    bcommands.processCommands("mdb hi who");
    assertTrue(bcommands.getConnection() == null);
    bcommands.processCommands("mdb hi.sqlite2");
    assertTrue(bcommands.getConnection() == null);
    bcommands.loadDatabase("mdb hi.sqlite3".split(" "));
    assertTrue(bcommands.getConnection() == null);
    bcommands.loadDatabase("map data/maps/smallMaps.sqlite3".split(" "));
    assertTrue(bcommands.getConnection() != null);
    bcommands.loadDatabase("mdb hi who".split(" "));
    assertTrue(bcommands.getConnection() != null);
  }

  // /**
  // * Tests that connecting nodes works. This is already tested in maps
  // commands.
  // */
  // @Test
  // public void testConnectNodes() {
  // BaconCommands bcommands = new BaconCommands();
  // bcommands.loadDatabase("map data/maps/smallMaps.sqlite3".split(" "));
  //
  // List<Way> stack = bcommands.connectNodes(
  // bcommands.parseQuotes("route \"Chihiro Ave\" \"\"".split(" ")),
  // new ArrayList<>());
  //
  // assertTrue(stack.get(0).getStart().getName().equals("Samuel L. Jackson"));
  // assertTrue(stack.get(0).getEnd().getName().equals("John Travolta"));
  // assertTrue(stack.get(0).getName().equals("Pulp Fiction"));
  // assertTrue(stack.size() == 1);
  // }

}
