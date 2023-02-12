package cs3500.freecell.model.hw02;

/**
 * Represents a foundation pile.
 */
public class FoundationPile extends APile {


  /**
   * Adds card to foundation pile.
   * @param inCard  card that will be added to the pile.
   * @param initial is it the first time cards are being added
   */
  public void addToPile(ICard inCard, boolean initial) throws IllegalArgumentException {
    //if not, check to see if value of inCard is ace, or 1
    int lastIndex = pile.size() - 1;
    //if first card in foundation pile and an ace add
    if (this.pile.size() == 0) {
      if (inCard.getValue() == 1) {
        this.pile.add(inCard);
      }
      else {
        throw new IllegalArgumentException(" First card needs to be an ace.\n");
      }
    }
    else {
      if ((this.pile.get(lastIndex).getValue() - inCard.getValue() == -1)
              && (this.pile.get(lastIndex).getSuit() == inCard.getSuit())) {
        this.pile.add(inCard);
      }
      else {
        throw new IllegalArgumentException(" Can't add this card to the foundation pile.\n");
      }
    }
  }

  @Override
  public ICard removeFrom(int index) throws IllegalArgumentException {
    throw new IllegalArgumentException(" Can't remove from a foundation pile.\n");
  }



}
