package cs3500.freecell.model.hw02;

/**
 * Represents a pile in the freecell game.
 */
public interface IPile {

  /**
   * Adds card to desired pile.
   * @param inCard card that will be added to the pile.
   * @param initial is it the first time cards are being added
   * @throws IllegalArgumentException if card cannot be added to a pile
   */
  void addToPile(ICard inCard, boolean initial) throws IllegalArgumentException;

  /**
   * Removes desired card from desired pile.
   * @param index index of card to be removed
   * @return ICard that was removed
   * @throws IllegalArgumentException if specified card is not the last in the pile
   */
  ICard removeFrom(int index) throws IllegalArgumentException;

  /**
   * Determines number of valid cards in given pile.
   */
  int numCards();

  /**
   * Retrieves desired card from pile.
   * @param cardIndex index of card in pile (starts at 0)
   * @return the desired card
   * @throws IllegalArgumentException if index is invalid
   */
  ICard getCard(int cardIndex) throws IllegalArgumentException;
}
