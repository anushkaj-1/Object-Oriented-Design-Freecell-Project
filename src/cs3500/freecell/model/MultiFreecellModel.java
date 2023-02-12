package cs3500.freecell.model;

import cs3500.freecell.model.hw02.Color;
import cs3500.freecell.model.hw02.ICard;
import cs3500.freecell.model.hw02.SimpleFreecellModel;
import java.util.ArrayList;

/**
 * This class represents the specific implementation of freecell with {@code ICard}s. In this model,
 * multiple card moves are allowed with restrictions (the multimove must be a compressed sequence
 * of filler moves to open piles and empty foundation piles.)
 * This class contains operations regarding the various states the freecell model must support.
 * This class extends the simple implementation as it contains the same
 * functionality with only its move method having changes.
 */
public class MultiFreecellModel extends SimpleFreecellModel implements FreecellModel<ICard> {

  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
      int destPileNumber) throws IllegalArgumentException, IllegalStateException {
    //TODO: this is the one to change implementation
    //if not last card in cascade pile --> check conditions and see if multimove possible
    //also check that destination pile is cascade
    //if anything else, just use regular move

    //if the game didn't start
    if (!super.didGameStart()) {
      throw new IllegalStateException("Game didn't start.");
    }
    //if game started
    else {
      //if card to move is in a cascade pile and not last card, normal move
      if (!attemptingMulti(source, destination, pileNumber, cardIndex)) {
        super.move(source, pileNumber, cardIndex, destination, destPileNumber);
      } else {
        //if multi move can happen
        if (checkConditions(pileNumber, cardIndex, destPileNumber)) {
          //do the multimove since valid
          multimove(pileNumber, cardIndex, destPileNumber);
        } else {
          throw new IllegalArgumentException("Move is invalid.");
        }
      }
    }
  }


  /**
   * Carries out the multimove.
   *
   * @param pileNumber     cascade pile to move from
   * @param cardIndex      card index at beginning of list of cards to move
   * @param destPileNumber cascade pile to move to
   */
  private void multimove(int pileNumber, int cardIndex, int destPileNumber) {
    ArrayList<ICard> toMove;
    int lastCard = this.getNumCardsInCascadePile(pileNumber);
    //for all cards to move
    for (int i = cardIndex; i < lastCard; i++) {
      super.removeAndAdd(pileNumber, cardIndex, destPileNumber);
      //should move all cards to desired pile
      //keeping cardindex same because as cards removed, each subsequent index decreases by 1
      //super.move(PileType.CASCADE, pileNumber, cardIndex, PileType.CASCADE, destPileNumber);
    }
  }


  /**
   * Determines whether multimove is being attempted: it is attempted if trying to move card in
   * cascade pile that is not the last one.
   *
   * @param source     PileType - multimove can only be from cascade to cascade
   * @param dest       PileType - used to determine that destination pile is cascade
   * @param pileNumber - pile to be moved from
   * @param cardIndex  - card to be moved
   * @return boolean - true if multimove, false if single move
   */
  private boolean attemptingMulti(PileType source, PileType dest, int pileNumber, int cardIndex) {
    return (source.equals(PileType.CASCADE) && dest.equals(PileType.CASCADE)
        && (cardIndex != this.getNumCardsInCascadePile(pileNumber) - 1));
  }

  /**
   * Checks whether attempted multimove can occur with specified cards.
   *
   * @param sourcePileNum index of source cascade pile
   * @param cardIndex     index of base card that is being moved in specified pile
   * @param destPileNum   index of destinatino cascade pile
   * @return boolean - true if multimove can happen, false if not
   */
  private boolean checkConditions(int sourcePileNum, int cardIndex, int destPileNum) {
    //conditions
    //cards from given index to last index have to form valid build (diff colors and one less)
    //bottom card needs to form build with top card of dest pile
    //max number of cards in a build = (freeopen + 1)*2^freecascade
    int numCardsToMove = this.getNumCardsInCascadePile(sourcePileNum) - cardIndex;
    //if number of cards requested to move are invalid
    int maxMove = (freePiles(PileType.OPEN) + 1) * (int) Math.pow(2, freePiles(PileType.CASCADE));
    if (numCardsToMove > maxMove || numCardsToMove < 0) {
      return false;
    }
    //num cards requested to move are valid
    else {
      //need to check if both situations have valid builds
      int lastCardDestIndex = this.getNumCardsInCascadePile(destPileNum) - 1;
      //if build can occur between dest pile top card and old bottom card and
      //also rest of cards in old pile good build
      return (validBuild(this.getCascadeCardAt(destPileNum, lastCardDestIndex),
          this.getCascadeCardAt(sourcePileNum, cardIndex)))
          && (sourceCardsBuild(sourcePileNum, cardIndex));
    }
  }

  /**
   * Determines whether sequence of cascade cards form valid build.
   *
   * @param sourcePileNum index of cascade pile being evaluated
   * @param cardIndex     index of first card beginning sequence to be evaluated
   * @return boolean, true if valid build, false if not
   */
  private boolean sourceCardsBuild(int sourcePileNum, int cardIndex) {
    int cardsToIterate = this.getNumCardsInCascadePile(sourcePileNum) - cardIndex;
    //initialize with first card color
    Color colorLastCard = this.getCascadeCardAt(sourcePileNum, cardIndex).getSuit().getColor();
    //initialize with first card value
    int valLastCard = this.getCascadeCardAt(sourcePileNum, cardIndex).getValue();
    //for each of the cards to evaluate
    for (int i = cardIndex + 1; i < this.getNumCardsInCascadePile(sourcePileNum); i++) {
      Color colorCurrCard = this.getCascadeCardAt(sourcePileNum, i).getSuit().getColor();
      int valCurrCard = this.getCascadeCardAt(sourcePileNum, i).getValue();
      //if color is different and value is one less
      if (colorLastCard != colorCurrCard && (valLastCard - valCurrCard) == 1) {
        colorLastCard = colorCurrCard;
        valLastCard = valCurrCard;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines whether two cards form a valid build.
   *
   * @param bottom the card that would be on the bottom part of this 2-card build
   * @param top    the card that would be on the top part
   * @return boolean, true if the build is valid, false if not
   */
  private boolean validBuild(ICard bottom, ICard top) {
    return (bottom.getSuit().getColor() != top.getSuit().getColor())
        && (bottom.getValue() == top.getValue() + 1);
  }

  /**
   * Calculates number of free cascade or open piles for temporary holding use for multimove.
   *
   * @param type PileType determines which type of pile is being counted
   * @return number of free piles of specified type
   * @throws IllegalArgumentException if given invalid PileType for method's purpose
   */
  private int freePiles(PileType type) throws IllegalArgumentException {
    switch (type) {
      case CASCADE:
        int numFreeCascade = 0;
        for (int i = 0; i < this.getNumCascadePiles(); i++) {
          if (this.getNumCardsInCascadePile(i) == 0) {
            numFreeCascade++;
          }
        }
        return numFreeCascade;
      case OPEN:
        int numFreeOpen = 0;
        for (int i = 0; i < this.getNumOpenPiles(); i++) {
          if (this.getNumCardsInOpenPile(i) == 0) {
            numFreeOpen++;
          }
        }
        return numFreeOpen;
      default:
        throw new IllegalArgumentException(" Not a valid pile type to hold cards temporarily.\n");
    }
  }

}
