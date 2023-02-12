package cs3500.freecell.model.hw02;

import static cs3500.freecell.model.hw02.Color.BLACK;
import static cs3500.freecell.model.hw02.Color.RED;

/**
 * Represents the 4 suits a card can possibly have.
 * String param for suit symbol, enum param for color
 */
public enum Suit {
  CLUB("♣", BLACK),
  DIAMOND("♦", RED),
  HEART("♥", RED),
  SPADE("♠", BLACK);

  private final String suit;
  private final Color color;

  Suit(String suit, Color color) {
    this.suit = suit;
    this.color = color;
  }

  public Color getColor() {
    return this.color;
  }

  /**
   * overrides toString for this enum.
   * @return suit symbol as a string
   */
  @Override
  public String toString() {
    return this.suit;
  }

  static int getLength() {
    return values().length;
  }
}
