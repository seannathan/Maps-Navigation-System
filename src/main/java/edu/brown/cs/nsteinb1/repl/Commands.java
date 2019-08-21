package edu.brown.cs.nsteinb1.repl;

/**
 * An interface for the different kinds of commands the REPL may accept.
 *
 * @author nicole
 *
 */
public interface Commands {

  /**
   * Return if the command given is covered by the class it's called on.
   *
   * @param command
   *          - command
   * @return boolean
   */
  boolean isCommand(String command);

  /**
   * Process user input.
   *
   * @param command
   *          - A string from the command line.
   */
  void processCommands(String command);

}
