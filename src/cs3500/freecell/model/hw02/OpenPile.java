package cs3500.freecell.model.hw02;

/**
 * Represents an open pile.
 */
public class OpenPile extends APile {

  /**
   * Adds card to open pile.
   * @param inCard card that will be added to the pile.
   * @param initial is it the first time cards are being added
   */
  public void addToPile(ICard inCard, boolean initial) throws IllegalArgumentException {
    if (this.pile.size() == 0) {
      this.pile.add(inCard);
    }
    else {
      throw new IllegalArgumentException(" Can't add a card to a full Open pile.\n");
    }
  }

  @Override
  public ICard getCard(int index) throws IllegalArgumentException {
    if (index == 0) {
      if (this.numCards() < 1) {
        return null;
      }
      else {
        return this.pile.get(index);
      }
    }
    else {
      throw new IllegalArgumentException(" Invalid index.\n");
    }
  }


}
