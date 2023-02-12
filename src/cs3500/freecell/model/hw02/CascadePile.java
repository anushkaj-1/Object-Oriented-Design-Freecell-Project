package cs3500.freecell.model.hw02;


/**
 * Represents a cascade pile.
 */
public class CascadePile extends APile {

  /**
   * Adds card to cascade pile.
   *
   * @param inCard  card that will be added to the pile.
   * @param initial is it the first time cards are being added
   */
  public void addToPile(ICard inCard, boolean initial) throws IllegalArgumentException {
    if (this.pile.size() == 0 || initial) {
      this.pile.add(inCard);
    } else {
      int indexLastCard = this.pile.size() - 1;
      int currLastVal = this.pile.get(indexLastCard).getValue();
      Suit currLastSuit = this.pile.get(indexLastCard).getSuit();
      if (inCard.getValue() - currLastVal == -1
          && inCard.getSuit().getColor() != currLastSuit.getColor()) {
        this.pile.add(inCard);
      } else {
        throw new IllegalArgumentException(" Card cannot be added to this cascade pile.\n");
      }
    }
  }




}
