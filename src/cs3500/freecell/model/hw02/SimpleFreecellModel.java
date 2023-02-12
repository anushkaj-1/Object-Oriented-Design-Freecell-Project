package cs3500.freecell.model.hw02;


import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the specific implementation of freecell with {@code ICard}s. In this model,
 * only one-card moves are allowed. This class contains operations regarding the various states the
 * freecell model must support.
 */
public class SimpleFreecellModel implements FreecellModel<ICard> {

  //need constructor
  //can maybe add seed field to test random shuffle
  private final ArrayList<IPile> cascadePiles;
  private final ArrayList<IPile> openPiles;
  private final ArrayList<IPile> foundationPiles;
  private int numCascadePiles;
  private int numOpenPiles;
  private int numFoundationPiles;

  /**
   * Constructs a simple freecell model with fields representing the cascade, open, and foundation
   * piles in the game.
   */
  public SimpleFreecellModel() {
    this.cascadePiles = new ArrayList<>();
    this.openPiles = new ArrayList<>();
    this.foundationPiles = new ArrayList<>();
    this.numCascadePiles = 0;
    this.numOpenPiles = 0;
    this.numFoundationPiles = 0;

  }


  @Override
  public List<ICard> getDeck() {
    List<ICard> deck = new ArrayList<>();
    int index = 0;
    for (CardVal v : CardVal.values()) {
      for (Suit s : Suit.values()) {
        deck.add(index, new Card(v, s));
        index++;
      }
    }
    return deck;
  }

  @Override //I think need to fix or at least have HELPERS YES
  //I need to remove all filling with sentinel objects
  //and replace them with changing int fields to number of piles defined here
  public void startGame(List<ICard> deck, int numCascadePiles, int numOpenPiles, boolean shuffle)
      throws IllegalArgumentException {
    //if deck is not valid or num of cascade or open piles not valid
    if (!valid(deck) || numCascadePiles < 4 || numOpenPiles < 1) {
      throw new IllegalArgumentException("Cannot start game: invalid parameter exists");
    } else {
      this.resetGame();
      //deep copy of the deck
      List<ICard> copyDeck = new ArrayList<>(deck);
      //if needed to be shuffled, then shuffle the deck
      if (shuffle) {
        //shuffles copydeck and then deals
        Collections.shuffle(copyDeck);
      }
      //storing number of cascade piles
      this.numCascadePiles = numCascadePiles;

      //initializing cascade piles
      int numCards = deck.size();

      for (int i = 0; i < numCascadePiles; i++) { //for the number of piles
        int index = 1 + i; //card index in deck (index starting at 1)
        this.cascadePiles.add(new CascadePile());
        while (index <= numCards) { //making sure still in deck and not out of bounds
          this.cascadePiles.get(i).addToPile(copyDeck.get(index - 1), true);
          index = index + numCascadePiles;
        }
      }

      //initialize open piles and update number of open piles field
      this.initOpen(numOpenPiles);

      //initialize foundation piles and update number of foundation piles field
      this.initFoundation(4);
    }
  }

  /**
   * Determines whether a deck is valid.
   *
   * @param cards the deck of cards used in startGame
   * @return a boolean for whether the deck is valid (true for yes, false for no)
   */
  private boolean valid(List<ICard> cards) {
    //invalid if not 52 cards, has duplicate cards, has invalid suit
    // (not possible because suit checked in Card constructor)
    if (cards == null) {
      return false;
    } else {
      List<ICard> copyDeck = new ArrayList<>(cards); //deep copy (if changed, won't affect original)
      int index = 0;
      //noDup is true if there are no duplicates, false if there are
      boolean noDup = true;
      while (copyDeck.size() > 0) {
        ICard current = copyDeck.get(index);//getting the card at the index, loop for all cards
        copyDeck.remove(index);
        if (copyDeck.contains(current)) { //need to override equality in Card class
          noDup = false;
          break;
        }
      }
      return cards.size() == 52 && noDup;
    }
  }

  /**
   * Resets game when startGame is called midgame.
   */
  private void resetGame() {
    this.numOpenPiles = 0;
    this.numCascadePiles = 0;
    this.numFoundationPiles = 0;
    this.openPiles.clear();
    this.cascadePiles.clear();
    this.foundationPiles.clear();
  }

  /**
   * Helps startGame initialize open piles.
   *
   * @param numOpenPiles is the given number of open piles in a game
   */
  private void initOpen(int numOpenPiles) {
    //storing number of open piles
    this.numOpenPiles = numOpenPiles;
    //initializing open piles with empty piles
    for (int i = 0; i < numOpenPiles; i++) {
      this.openPiles.add(new OpenPile());
    }
  }

  /**
   * Helps startGame initialize foundation piles.
   *
   * @param numFoundationPiles is the number of foundation piles in a game
   */
  private void initFoundation(int numFoundationPiles) {
    //storing number of open piles
    this.numFoundationPiles = 4;
    //initializing foundation piles with empty piles
    for (int j = 0; j < numFoundationPiles; j++) {
      this.foundationPiles.add(new FoundationPile());
    }
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
      int destPileNumber) throws IllegalArgumentException, IllegalStateException {

    //check for if a valid game has started
    if (!didGameStart()) {
      throw new IllegalStateException("A valid game has not started");
    } else {
      switch (source) {
        case OPEN:
          if (pileNumber > this.openPiles.size() - 1 || pileNumber < 0) {
            throw new IllegalArgumentException(" Open pile index out of bounds.\n");
          } else {
            if (checkCardIndex(source, pileNumber, cardIndex)) {
              ICard toMoveO = this.openPiles.get(pileNumber).removeFrom(cardIndex);
              this.moveTo(destination, destPileNumber, toMoveO);
            } else {
              throw new IllegalArgumentException(" Card invalid.\n");
            }
          }
          break;
        case FOUNDATION:
          if (pileNumber > this.foundationPiles.size() - 1 || pileNumber < 0) {
            throw new IllegalArgumentException(" Foundation pile index out of bounds.\n");
          } else {
            if (checkCardIndex(source, pileNumber, cardIndex)) {
              ICard toMoveF = this.foundationPiles.get(pileNumber).removeFrom(cardIndex);
              this.moveTo(destination, destPileNumber, toMoveF);
            } else {
              throw new IllegalArgumentException(" Card invalid.\n");
            }
          }
          break;
        case CASCADE:
          if (pileNumber > this.cascadePiles.size() - 1 || pileNumber < 0) {
            throw new IllegalArgumentException(" Cascade pile index out of bounds.\n");
          } else {
            if (checkCardIndex(source, pileNumber, cardIndex)) {
              ICard toMoveC = this.cascadePiles.get(pileNumber).removeFrom(cardIndex);
              this.moveTo(destination, destPileNumber, toMoveC);
            } else {
              throw new IllegalArgumentException(" Card invalid.\n");
            }
            /*if (cardIndex < 0 || cardIndex != this.cascadePiles.get(pileNumber).numCards() - 1) {
              throw new IllegalArgumentException(" Specified card index is not last card.\n");
            }
            else {
              ICard toMoveC = this.cascadePiles.get(pileNumber).removeFrom(cardIndex);
              this.moveTo(destination, destPileNumber, toMoveC);
            }*/
          }
          break;
        default:
          throw new IllegalArgumentException("Not a valid pile type.");
      }
    }

  }

  /**
   * Performs the move for multimove.
   *
   * @param sourcePile index of source cascade pile
   * @param cardIndex  card index of card to move
   * @param destPile   index of destination cascade pile
   */
  protected void removeAndAdd(int sourcePile, int cardIndex, int destPile) {
    ICard toMoveMulti = this.cascadePiles.get(sourcePile).removeFrom(cardIndex);
    this.moveTo(PileType.CASCADE, destPile, toMoveMulti);
  }

  /**
   * Determines whether card can be removed.
   *
   * @param source        piletype of source pile
   * @param sourcePileNum index of source pile
   * @param cardIndex     index of card to be removed
   * @return boolean, true if move can happen with specified indices
   * @throws IllegalArgumentException if given an invalid pile type
   */
  private boolean checkCardIndex(PileType source, int sourcePileNum, int cardIndex)
      throws IllegalArgumentException {
    switch (source) {
      case OPEN:
        int indexLastCardO = openPiles.get(sourcePileNum).numCards() - 1;
        return (cardIndex == indexLastCardO);
      case FOUNDATION:
        int indexLastCardF = foundationPiles.get(sourcePileNum).numCards() - 1;
        return (cardIndex == indexLastCardF);
      case CASCADE:
        int indexLastCardC = cascadePiles.get(sourcePileNum).numCards() - 1;
        return (cardIndex == indexLastCardC);
      default:
        throw new IllegalArgumentException("invalid pile type.");
    }
  }

  /**
   * Helper for move method, helps with moving a card to desired pile.
   *
   * @param dest        the PileType of the destination pile
   * @param destPileNum the pile number (index starting at 0)
   * @param toBeMoved   the card that is being moved
   */
  protected void moveTo(PileType dest, int destPileNum, ICard toBeMoved)
      throws IllegalArgumentException {
    switch (dest) {
      case OPEN:
        if (destPileNum >= this.openPiles.size() || destPileNum < 0) {
          throw new IllegalArgumentException(" Open pile index out of bounds.\n");
        } else {
          this.openPiles.get(destPileNum).addToPile(toBeMoved, false);
        }
        break;
      case FOUNDATION:
        if (destPileNum >= this.foundationPiles.size() || destPileNum < 0) {
          throw new IllegalArgumentException(" Foundation pile index out of bounds.\n");
        } else {
          this.foundationPiles.get(destPileNum).addToPile(toBeMoved, false);
        }
        break;
      case CASCADE:
        if (destPileNum >= this.cascadePiles.size() || destPileNum < 0) {
          throw new IllegalArgumentException(" Cascade pile index out of bounds.\n");
        } else {
          this.cascadePiles.get(destPileNum).addToPile(toBeMoved, false);
        }
        break;
      default:
        throw new IllegalArgumentException("Not a valid pile type.");
    }
  }

  /**
   * Determines whether game started.
   *
   * @return boolean false for no, true for yes
   */
  protected boolean didGameStart() {
    return !(this.numCascadePiles < 4 && this.numOpenPiles < 1 && this.numFoundationPiles < 4);
  }

  @Override
  public boolean isGameOver() {
    int numCardsInF = 0;
    if (!didGameStart()) {
      return false;
    } else {
      for (int i = 0; i < 4; i++) {
        numCardsInF = numCardsInF + getNumCardsInFoundationPile(i);
      }
      return (numCardsInF == 52 && didGameStart());
    }
  }

  @Override
  public int getNumCardsInFoundationPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game has not started.");
    } else if ((this.foundationPiles.size() - 1) - index < 0 || index < 0) {
      throw new IllegalArgumentException("Pile index is not valid.");
    } else {
      return this.foundationPiles.get(index).numCards();
    }
  }

  @Override
  public int getNumCascadePiles() {
    if (!didGameStart()) {
      return -1;
    } else {
      return this.cascadePiles.size();
    }
  }

  @Override
  public int getNumCardsInCascadePile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game has not started.");
    } else if ((this.cascadePiles.size() - 1) - index < 0 || index < 0) {
      throw new IllegalArgumentException("Pile index is not valid.");
    } else {
      return this.cascadePiles.get(index).numCards();
    }
  }

  @Override
  public int getNumCardsInOpenPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game has not started.");
    } else if ((this.openPiles.size() - 1) - index < 0 || index < 0) {
      throw new IllegalArgumentException("Pile index is not valid.");
    } else {
      return this.openPiles.get(index).numCards();
    }
  }

  @Override
  public int getNumOpenPiles() {
    if (!didGameStart()) {
      return -1;
    } else {
      return this.openPiles.size();
    }
  }

  @Override
  public ICard getFoundationCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game did not start.");
    }
    if (pileIndex < 0 || pileIndex > this.foundationPiles.size() - 1) {
      throw new IllegalArgumentException("Pile index is invalid.");
    } else {
      if (cardIndex > this.foundationPiles.get(pileIndex).numCards() - 1 || cardIndex < 0) {
        throw new IllegalArgumentException("Card index is invalid.");
      } else {
        return this.foundationPiles.get(pileIndex).getCard(cardIndex);
      }
    }
  }

  @Override
  public ICard getCascadeCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game did not start.");
    }
    if (pileIndex < 0 || pileIndex > this.cascadePiles.size() - 1) {
      throw new IllegalArgumentException("Pile index is invalid.");
    } else {
      if (cardIndex > this.cascadePiles.get(pileIndex).numCards() - 1 || cardIndex < 0) {
        throw new IllegalArgumentException("Card index is invalid.");
      } else {
        return this.cascadePiles.get(pileIndex).getCard(cardIndex);
      }
    }
  }

  @Override
  public ICard getOpenCardAt(int pileIndex) throws IllegalArgumentException, IllegalStateException {
    if (!didGameStart()) {
      throw new IllegalStateException("Game did not start.");
    }
    //if pile index is out of bounds
    if (pileIndex > this.openPiles.size() - 1 || pileIndex < 0) {
      throw new IllegalArgumentException("Pile index invalid.");
    } else {
      return this.openPiles.get(pileIndex).getCard(0);
    }
  }
}
