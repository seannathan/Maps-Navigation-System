package edu.brown.cs.nsteinb1.bacon;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.brown.cs.ns.maps.MapsDatabase;
import edu.brown.cs.ns.maps.Node;
import edu.brown.cs.ns.maps.Way;
import edu.brown.cs.nsteinb1.dijkstra.Dijkstra;
import edu.brown.cs.nsteinb1.repl.Commands;

/**
 * Includes and executes all REPL commands that pertain solely to Bacon.
 *
 * @author nicole
 *
 */
public class BaconCommands implements Commands {

  private Connection conn = null;

  /**
   * Constructor for BaconCommands.
   */
  public BaconCommands() {
  }

  /**
   * Return if the command given is covered by Bacon.
   *
   * @param command
   *          - command
   * @return boolean
   */
  public boolean isCommand(String command) {
    return command.equals("mdb") || command.equals("connect");
  }

  /**
   * Process user input.
   *
   * @param command
   *          - A string from the command line.
   */
  public void processCommands(String command) {
    // Split command into individual strings.
    String[] splitCommand = command.split(" ");

    switch (splitCommand[0]) {
      case "mdb":
        this.loadDatabase(splitCommand);
        break;
      case "connect":
        splitCommand = this.parseQuotes(splitCommand);
        List<Way> stack = this.connectNodes(splitCommand, new ArrayList<>());

        if (stack == null) {
          return;
        }

        if (stack.isEmpty()) {
          System.out.printf("%s -/- %s%n", splitCommand[1], splitCommand[2]);
          return;
        }

        // Print the path if there is one.
        int last = stack.size() - 1;
        while (!stack.isEmpty()) {
          Way way = stack.get(last);
          System.out.printf("%s -> %s : %s%n", way.getStart().getName(),
              way.getEnd().getName(), way.getName());
          stack.remove(last);
          last--;
        }

        break;
      default:
        System.out.printf("ERROR: '%s' is not a valid command.%n",
            splitCommand[0]);
    }
  }

  /**
   * Loads specified database file.
   *
   * @param splitCommand
   *          split input from command line
   */
  public void loadDatabase(String[] splitCommand) {

    // mdb has only one argument.
    if (splitCommand.length != 2) {
      System.out.printf("ERROR: Command format is 'map <sql_db>'.%n");
      return;
    }

    // Argument must be .sqlite3.
    String[] sql = splitCommand[1].split("\\.");
    if (sql.length != 2 || !sql[1].equals("sqlite3")) {
      System.out.printf("ERROR: '%s' is not a valid sqlite3 file.%n",
          splitCommand[1]);
      return;
    }

    // Connects to a database.
    Connection newConn = MapsDatabase.load(splitCommand);

    // If an error occurred in database loader, do not create a new connection.
    if (newConn == null) {
      return;
    }

    // Store new connection and print to terminal.
    conn = newConn;
    MapsDatabase.loadStreetCorpus(conn);
    System.out.printf("map set to %s%n", splitCommand[1]);

  }

  /**
   * Print the shortest path between two nodes.
   *
   * @param splitCommand
   *          split input from command line
   * @param nearest
   *          list of start and end nodes (will be empty depending if route
   *          command is used with coordinates)
   * @return stack of ways
   */
  public List<Way> connectNodes(String[] splitCommand, List<Node> nearest) {
    boolean names = nearest.isEmpty();

    // Get rid of the starting quote of the first street, and the ending quote
    // of the last.
    Node startNode, endNode;
    if (names) {
      splitCommand[1] = splitCommand[1].substring(1);
      splitCommand[4] = splitCommand[4].substring(0,
          splitCommand[4].length() - 1);

      // Query the database for intersecting nodes.
      startNode = MapsDatabase.getIntersection(conn, splitCommand[1],
          splitCommand[2]);
      if (startNode == null) {
        System.out.printf("ERROR: %s and %s are not intersecting streets.%n",
            splitCommand[1], splitCommand[2]);
        return null;
      }

      endNode = MapsDatabase.getIntersection(conn, splitCommand[3],
          splitCommand[4]);
      if (endNode == null) {
        System.out.printf("ERROR: %s and %s are not intersecting streets.%n",
            splitCommand[3], splitCommand[4]);
        return null;
      }
    } else {
      startNode = nearest.get(0);
      endNode = nearest.get(1);
    }

    // If start node and end node are the same, return no path.
    if (startNode.getId().equals(endNode.getId())) {
      System.out.printf("%s -/- %s%n", startNode.getId(), endNode.getId());
      return null;
    }

    // Use Dijkstra to return a path.
    Dijkstra<Node, Way> di = new Dijkstra<>();
    List<Way> stack = di.search(startNode, endNode);

    if (stack.isEmpty()) {
      System.out.printf("%s -/- %s%n", startNode.getId(), endNode.getId());
      return null;
    }

    return stack;
  }

  /**
   * Accounts for multi-word names, counteracting issues that may arise from
   * splitting on spaces.
   *
   * @param splitCommand
   *          input from command line
   * @return same input but indices may now have multiple words
   */
  public String[] parseQuotes(String[] splitCommand) {
    String concat = "";
    // Recombine the words split by spaces.
    for (int i = 1; i < splitCommand.length; i++) {
      concat += splitCommand[i];

      if (i < splitCommand.length - 1) {
        concat += " ";
      }
    }

    // Split on " " to tell us when we've reached the second street's name.
    String[] splitConcat = concat.split("\" \"");

    // Add connect back as the first argument.
    List<String> listConcat = new ArrayList<>(Arrays.asList(splitConcat));
    listConcat.add(0, splitCommand[0]);

    return listConcat.toArray(new String[0]);
  }

  /**
   * Return connection for testing.
   *
   * @return conn
   */
  public Connection getConnection() {
    return conn;
  }

}
