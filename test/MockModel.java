import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.hw02.ICard;
import java.io.IOException;
import java.util.List;

/**
 * Mock model used to enforce correct communication between the controller and model.
 * Used to prevent corruption of inputs.
 */
public class MockModel implements FreecellModel<ICard> {

  private final Appendable out;

  /**
   * Constructs a mock model to use to ensure that inputs are not altered in transmission.
   * @param out appendable that will be written to
   */
  public MockModel(Appendable out) {
    if (out == null) {
      throw new IllegalArgumentException("Can't be null.");
    }
    else {
      this.out = out;
    }
  }

  @Override
  public List<ICard> getDeck() {
    return null;
  }

  @Override
  public void startGame(List<ICard> deck, int numCascadePiles, int numOpenPiles, boolean shuffle)
      throws IllegalArgumentException {
    try {
      this.out.append(String.valueOf(deck)).append(" ").append(String.valueOf(numCascadePiles))
          .append(" ").append(String.valueOf(numOpenPiles)).append(" ")
          .append(String.valueOf(shuffle));
    }
    catch (IOException e) {
      throw new IllegalStateException("Could not write to appendable.");
    }
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
      int destPileNumber) throws IllegalArgumentException, IllegalStateException {
    try {
      this.out.append(String.valueOf(source)).append(" ").append(String.valueOf(pileNumber))
          .append(" ").append(String.valueOf(cardIndex)).append(" ")
          .append(String.valueOf(destination)).append(" ").append(String.valueOf(destPileNumber));
    }
    catch (IOException e) {
      throw new IllegalStateException("Could not write to appendable.");
    }
  }

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public int getNumCardsInFoundationPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public int getNumCascadePiles() {
    return 0;
  }

  @Override
  public int getNumCardsInCascadePile(int index)
      throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public int getNumCardsInOpenPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public int getNumOpenPiles() {
    return 0;
  }

  @Override
  public ICard getFoundationCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  @Override
  public ICard getCascadeCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  @Override
  public ICard getOpenCardAt(int pileIndex) throws IllegalArgumentException, IllegalStateException {
    return null;
  }
}
