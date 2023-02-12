package cs3500.freecell.view;

import cs3500.freecell.model.FreecellModelState;
import java.io.IOException;

/**
 * Represents the text view of the game. The game is represented through text.
 */
public class FreecellTextView implements FreecellView {

  private final FreecellModelState<?> model;
  private Appendable ap;

  /**
   * Constructor for the text view of the freecell game.
   *
   * @param model is the current model of the game that is used to construct the view
   * @param ap    is the destination for the view
   */
  public FreecellTextView(FreecellModelState<?> model, Appendable ap) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    } else {
      this.model = model;
    }
    if (ap == null) {
      throw new IllegalArgumentException("Appendable cannot be null");
    } else {
      this.ap = ap;
    }
  }

  /**
   * Constructor for the text view of the game.
   *
   * @param model uses the current model to display the game state
   */
  public FreecellTextView(FreecellModelState<?> model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
    //assigned a null if appendable not provided in constructor
    this.ap = null;
  }

  @Override
  public String toString() {
    //use observer methods on the model to construct the string
    if (!gameBegun()) {
      return "";
    } else {
      return this.foundation() + this.open() + this.cascade();
    }
  }

  //from piazza, modify methods accordingly
  //IoException is when an appendable fails.
  // Printing to the console is when an appendable does not exist.

  @Override
  public void renderBoard() throws IOException {
    //check if appendable there, if not then system.out --> for case of other constructor
    if (this.ap == null) {
      System.out.print(this.toString());
    } else {
      ap.append(this.toString());
    }

  }

  @Override
  public void renderMessage(String message) throws IOException {
    //check if appendable there, if not then system.out --> for case of other constructor
    if (this.ap == null) {
      System.out.print(message);
    } else {
      ap.append(message);
    }

  }

  /**
   * Determines whether game has begun.
   *
   * @return boolean (true if yes, false if no)
   */
  private boolean gameBegun() {
    return !(this.model.getNumCascadePiles() < 4 && this.model.getNumOpenPiles() < 1);
  }

  /**
   * Creates foundation piles section of toString.
   *
   * @return string of all foundation piles
   */
  private String foundation() {
    String foundation = "";
    //for each of the piles
    for (int i = 1; i <= 4; i++) {
      foundation = foundation + "F" + i + ":";
      int cards = this.model.getNumCardsInFoundationPile(i - 1);
      //for each of the cards in a pile
      for (int j = 0; j < cards; j++) {
        foundation = foundation + " " + this.model.getFoundationCardAt(i - 1, j) + ",";
      }
      if (this.model.getNumCardsInFoundationPile(i - 1) > 0) {
        foundation = foundation.substring(0, foundation.length() - 1) + "\n";
      } else {
        foundation = foundation + "\n";
      }
    }
    return foundation.substring(0, foundation.length());
  }

  /**
   * Creates open piles section of toString.
   *
   * @return string of all open piles
   */
  private String open() {
    String open = "";
    //for each of the piles
    for (int i = 1; i <= this.model.getNumOpenPiles(); i++) {
      open = open + "O" + i + ":";
      int cards = this.model.getNumCardsInOpenPile(i - 1);
      for (int j = 0; j < cards; j++) {
        open = open + " " + this.model.getOpenCardAt(i - 1) + ",";
      }
      if (this.model.getNumCardsInOpenPile(i - 1) > 0) {
        open = open.substring(0, open.length() - 1) + "\n";
      } else {
        open = open + "\n";
      }
    }
    return open.substring(0, open.length());
  }

  /**
   * Creates cascade piles section of toString.
   *
   * @return string of all cascade piles
   */
  private String cascade() {
    String cascade = "";
    //for each of the piles
    for (int i = 1; i <= this.model.getNumCascadePiles(); i++) {
      cascade = cascade + "C" + i + ":";
      int cards = this.model.getNumCardsInCascadePile(i - 1);
      for (int j = 0; j < cards; j++) {
        cascade = cascade + " " + this.model.getCascadeCardAt(i - 1, j) + ",";
      }
      if (this.model.getNumCardsInCascadePile(i - 1) > 0) {
        cascade = cascade.substring(0, cascade.length() - 1) + "\n";
      } else {
        cascade = cascade + "\n";
      }
    }

    return cascade.substring(0, cascade.length() - 1);
  }

}
