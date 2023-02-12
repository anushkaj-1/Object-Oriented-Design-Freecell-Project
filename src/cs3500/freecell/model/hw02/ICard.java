package cs3500.freecell.model.hw02;

/**
 * Represents objects that can be in the game piles.
 */
public interface ICard {

  /**
   * Observes value of ICard in question.
   * @return int value
   */
  int getValue();

  /**
   * Observes suit of ICard in question.
   * @return string suit
   */
  Suit getSuit();

  /**
   * A method that will allow the printing of ICards.
   */
  String toString();

}
