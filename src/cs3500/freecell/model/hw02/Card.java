package cs3500.freecell.model.hw02;

import static java.util.Objects.hash;


/**
 * Cards represented with their value (13 possible values Ace to King) and suit (4 possible:
 *             Club, Diamond, Heart, Spade).
 */
public class Card implements ICard {

  private final CardVal value;
  private final Suit suit;

  /**
   * Constructs a card in a valid deck.
   *
   * @param value value of the card
   * @param suit  suit of the card
   * @throws NullPointerException when fields are instantiated with nulls
   */

  public Card(CardVal value, Suit suit) throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("Value given is null.");
    }
    else {
      this.value = value;
    }
    if (suit == null) {
      throw new IllegalArgumentException("Suit given is null.");
    }
    else {
      this.suit = suit;
    }
  }

  @Override
  public int getValue() {
    return this.value.getCardNum();
  }

  @Override
  public Suit getSuit() {
    return this.suit;
  }

  /**
   * overrides equals method to make a Card specific method.
   */
  @Override
  public boolean equals(Object o) {
    //find in lecture code
    if (this == o) {
      return true;
    }
    if (! (o instanceof Card)) {
      return false;
    }

    Card that = (Card) o;
    return this.value == that.value
        && this.suit == that.suit;
  }

  /**
   * overridden along with equals.
   * @return int hashcode of specific object
   */
  @Override
  public int hashCode() {
    return hash(this.value, this.suit);
  }

  /**
   * A method that will allow the printing of Cards.
   * @return string that represents the card in format value then suit; ex: "A♥"
   */
  public String toString() {
    if (this.value.toString().length() > 1) {
      return this.value.getCardNum() + "" + this.suit;
    }
    else {
      return this.value.toString() + this.suit;
    }
  }
}
