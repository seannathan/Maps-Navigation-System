package edu.brown.cs.nsteinb1.repl;

import edu.brown.cs.ns.maps.MapsCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * REPL to read in user input and execute the appropriate command.
 *
 * @author nicole
 *
 */
public class Repl {

  private MapsCommands mapsCommands = new MapsCommands();

  /**
   * Constructor for repl.
   */
  public Repl() {
  }

  /**
   * Start reading user input.
   */
  public void start() {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in))) {
      String command;
      while ((command = br.readLine()) != null) {
        processCommands(command);
      }
    } catch (IOException ioe) {
      // Not possible. No error message can make sense of this.
      ioe.printStackTrace();
    }
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

    // Make sure user has entered something.
    if (splitCommand.length == 0) {
      return;
    }

    // Send command to the appropriate Commands class.
    if (mapsCommands.isCommand(splitCommand[0])) {
      mapsCommands.processCommands(command);
    } else {
      System.out.printf("ERROR: '%s' is not a valid command.%n",
          splitCommand[0]);
    }
  }

  /**
   * For GUI access.
   *
   * @return mapsCommands
   */
  public MapsCommands getMapsCommands() {
    return mapsCommands;
  }

}
