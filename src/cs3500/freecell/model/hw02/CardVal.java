package cs3500.freecell.model.hw02;

/**
 * Represents the 13 values that a card can possibly have.
 */
public enum CardVal {
  A(1),
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9),
  TEN(10),
  J(11),
  Q(12),
  K(13);

  private final int cardNum;

  CardVal(int cardNum) {
    this.cardNum = cardNum;
  }

  /**
   * Gets number value of a card.
   * @return int value
   */
  int getCardNum() {
    return this.cardNum;
  }

  /**
   * Returns the string value that would show up in the deck.
   * @return appropriate string value
   */
  @Override
  public String toString() {
    if (this.cardNum < 11 && this.cardNum > 1) {
      return this.cardNum + "";
    }
    if (this.cardNum == 1) {
      return "A";
    }
    if (this.cardNum == 11) {
      return "J";
    }
    if (this.cardNum == 12) {
      return "Q";
    }
    else {
      return "K";
    }

  }

}
