package cs3500.freecell.model.hw02;

import java.util.ArrayList;

/**
 * APile represents a pile present in the game.
 */
public abstract class APile implements IPile {

  protected final ArrayList<ICard> pile;

  APile() {
    this.pile = new ArrayList<>();
  }

  /**
   * Adds card to desired pile.
   *
   * @param inCard  card that will be added to the pile.
   * @param initial is it the first time cards are being added
   * @throws IllegalArgumentException if card cannot be added to a pile
   */
  public abstract void addToPile(ICard inCard, boolean initial) throws IllegalArgumentException;

  /**
   * Removes desired card from desired pile.
   *
   * @param index index of card to be removed
   * @return ICard that was removed
   * @throws IllegalArgumentException if specified card is not the last in the pile
   */
  public ICard removeFrom(int index) throws IllegalArgumentException {
    ICard removed = this.pile.get(index);
    this.pile.remove(index);
    return removed;
  }

  /**
   * Determines number of valid cards in given pile.
   */
  public int numCards() {
    return this.pile.size();
  }


  /**
   * Retrieves desired card from pile.
   *
   * @param cardIndex index of card in pile (starts at 0)
   * @return the desired card
   * @throws IllegalArgumentException if index is invalid
   */
  public ICard getCard(int cardIndex) throws IllegalArgumentException {
    if (this.numCards() - cardIndex < 1 || cardIndex < 0) {
      throw new IllegalArgumentException(" Invalid index\n");
    } else {
      return this.pile.get(cardIndex);
    }
  }


}
