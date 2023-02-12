package cs3500.freecell.controller;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.hw02.ICard;
import cs3500.freecell.view.FreecellTextView;
import cs3500.freecell.view.FreecellView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The controller in the implementation of a simple freecell game with ICards. Interacts with the
 * model and the view to make the game cohesive.
 */
public class SimpleFreecellController implements FreecellController<ICard> {

  private final FreecellModel<ICard> model;
  private final Readable stringReader;
  private final Appendable ap;
  private final FreecellView view;

  /**
   * Constructs a controller for the implementation of freecell.
   *
   * @param model        which the controller is able to start the game with.
   * @param stringReader readable: is the user input
   * @param ap           appendable: is the program output Contains a view field allowing the view
   *                     to display what the controller deems necessary
   * @throws IllegalArgumentException if either the Readable stringReader or Appendable ap arguments
   *                                  are given nulls
   */
  public SimpleFreecellController(FreecellModel<ICard> model, Readable stringReader, Appendable ap)
      throws IllegalArgumentException {
    this.model = model;
    if (stringReader == null) {
      throw new IllegalArgumentException("stringReader can't be null");
    } else {
      this.stringReader = stringReader;
    }
    if (ap == null) {
      throw new IllegalArgumentException("out can't be null");
    } else {
      this.ap = ap;
    }
    this.view = new FreecellTextView(this.model, this.ap);
  }


  @Override
  public void playGame(List<ICard> deck, int numCascades, int numOpens, boolean shuffle)
      throws IllegalStateException, IllegalArgumentException {

    //checking if deck is null
    if (deck == null) {
      throw new IllegalArgumentException("Deck can't be null.");
    }

    //first checking if can start game (if can't return)
    try {
      model.startGame(deck, numCascades, numOpens, shuffle);
    } catch (IllegalArgumentException e) {
      writeMessage("Could not start game.");
      return;
    }

    //render initial board with new line after if game starts
    try {
      view.renderBoard();
      ap.append("\n");
      //ap.append(view.toString() + "\n");
    } catch (IOException e) {
      throw new IllegalStateException();
    }

    //will be filled with all valid strings
    List<String> moveInputs = new ArrayList<>();

    Scanner scanner = new Scanner(this.stringReader);

    //while game still going
    while (!model.isGameOver()) {

      //if no more inputs, throw illegal state exception
      if (!scanner.hasNext()) {
        throw new IllegalStateException("Ran out of input.");
      }
      //scanner has more so evaluate next
      String input = scanner.next();

      //checks whether user requested quit
      if (input.equalsIgnoreCase("q")) {
        writeMessage("\nGame quit prematurely.");
        return;
      }

      //need to determine whether string is valid or not
      boolean inputValid = valid(moveInputs.size(), input);
      //if input is valid add to list
      if (inputValid) {
        moveInputs.add(input);
      } else {
        //ask for input again
        writeMessage(moveMessage(moveInputs.size()) + "\n");
      }

      if (moveInputs.size() == 3) {
        PileType source = getPileType(moveInputs.get(0));
        PileType dest = getPileType(moveInputs.get(2));
        int sourceIndex = getPileNum(moveInputs.get(0));
        int destIndex = getPileNum(moveInputs.get(2));
        int cardIndex = Integer.parseInt(moveInputs.get(1));
        try {
          model.move(source, sourceIndex - 1, cardIndex - 1, dest, destIndex - 1);
        } catch (IllegalArgumentException e) {
          writeMessage("Invalid move. Try again." + e.getMessage());
        }
        transmitBoard();
        //clear move inputs for next move
        moveInputs.clear();
      }

    }
    //if game is over
    //transmit board to appendable
    try {
      view.renderBoard();
      ap.append("\nGame over.");
      // ap.append(view.toString() + "\nGame over.");
    } catch (IOException e) {
      throw new IllegalStateException();
    }
    //message "Game over." on new line
    return;
  }


  /**
   * Determines whether string is a valid input.
   *
   * @param moveInput integer that determines what number input the playGame is looking for
   *                  currently 0 being the source pile, 1 the card index of the source pile, and 2
   *                  the destination pile
   * @param input     current string being evaluated
   * @return boolean (true if valid, false if not)
   */
  private boolean valid(int moveInput, String input) {
    switch (moveInput) {
      //input is evaluated for source pile
      case 0:
        //input evaluated for destination pile
      case 2:
        return pileValid(input);
      //input evaluated for card index
      case 1:
        return isInt(input);
      //have enough inputs for a move
      default:
        throw new IllegalArgumentException("Should not have more than 3 inputs per move");
    }
  }

  /**
   * Determines whether pile index is valid, by checking if valid piletype indicator followed by an
   * integer.
   *
   * @param input String to be evaluated.
   * @return boolean (true if valid, false if not)
   */
  private boolean pileValid(String input) {
    switch (input.substring(0, 1).toLowerCase()) {
      case "c":
        return isInt(input.substring(1));
      case "o":
        return isInt(input.substring(1));
      case "f":
        return isInt(input.substring(1));
      default:
        //not valid pile if gets here
        return false;
    }
  }

  /**
   * Determines whether given string is an integer.
   *
   * @param possInt string that is being evaluated
   * @return boolean (true if it is an integer, false if not)
   */
  private boolean isInt(String possInt) {
    try {
      int value = Integer.parseInt(possInt);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }


  /**
   * Gets piletype from valid input for source or destination.
   *
   * @param input valid string
   * @return specified piletype
   */
  private PileType getPileType(String input) {
    switch (input.substring(0, 1).toLowerCase()) {
      case "c":
        return PileType.CASCADE;
      case "o":
        return PileType.OPEN;
      case "f":
        return PileType.FOUNDATION;
      default:
        throw new IllegalArgumentException("No other piletypes.");
    }
  }

  /**
   * Retrieves pile number from given string that has been verified as an integer.
   *
   * @param input string to be returned as integer
   * @return resulting integer
   */
  private int getPileNum(String input) {
    return Integer.parseInt(input.substring(1));
  }


  /**
   * Handles renderMessage IOException.
   *
   * @param message string to be appended to appendable
   */
  private void writeMessage(String message) {
    try {
      this.view.renderMessage(message);
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  /**
   * Handles renderBoard IOException.
   */
  private void transmitBoard() {
    try {
      this.view.renderBoard();
      ap.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  /**
   * Generates error message for invalid inputs dependent on what index is being searched for.
   *
   * @param movePart integer that determines what number input the playGame is looking for 0 being
   *                 the source pile, 1 the card index of the source pile, and 2 the destination
   *                 pile
   * @return String error message for invalid inputs in this controller
   * @throws IllegalArgumentException if looking for any input part other than the necessary three
   */
  private String moveMessage(int movePart) throws IllegalArgumentException {
    switch (movePart) {
      //trying to get source pile
      case 0:
        return "Enter a valid source pile.";
      //trying to get card index of source pile
      case 1:
        return "Enter a valid card index.";
      //trying to get destination pile
      case 2:
        return "Enter a valid destination pile.";
      default:
        throw new IllegalArgumentException("Move only requires 3 valid inputs.");
    }
  }


}
