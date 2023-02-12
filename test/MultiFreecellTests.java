import static org.junit.Assert.assertEquals;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.MultiFreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.hw02.ICard;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Class containing tests specific to MultiFreecellModel implementation.
 * Tests functionality not also present in SimpleFreecellModel.
 */
public class MultiFreecellTests {

  FreecellModel<ICard> model;
  List<ICard> deck;

  @Before
  public void setUp() {
    model = new MultiFreecellModel();
    deck = model.getDeck();
  }

  ///tests specific to multifreecellmodel here///


  //tests for constructor of multifreecellmodel//

  //checks that methods can be used
  @Test
  public void asExpected() {
    FreecellModel<ICard> multi = new MultiFreecellModel();
    assertEquals(52, multi.getDeck().size());
    multi.startGame(deck, 8, 4, true);
  }

  //tests for move method//

  //test move with game not started
  @Test (expected = IllegalStateException.class)
  public void notStarted() {
    model.move(PileType.CASCADE, 2, 4, PileType.CASCADE, 3);
  }

  //test move with attempting multi from cascade to foundation
  @Test (expected = IllegalArgumentException.class)
  public void toFoundation() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 2, 3, PileType.FOUNDATION, 1);
  }

  //test multi from cascade to open
  @Test (expected = IllegalArgumentException.class)
  public void toOpen() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 2, 3, PileType.OPEN, 1);
  }

  //test multi from foundation to cascade
  @Test (expected = IllegalArgumentException.class)
  public void fromFoundation() {
    model.startGame(deck, 52, 4, false);
    model.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 4, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.move(PileType.FOUNDATION, 0, 0, PileType.CASCADE, 0);
  }

  //test multi from foundation to open
  @Test (expected = IllegalArgumentException.class)
  public void foundationToOpen() {
    model.startGame(deck, 52, 4, false);
    model.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 4, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.move(PileType.FOUNDATION, 0, 0, PileType.CASCADE, 0);
  }


  //test valid multi from cascade to cascade
  //(different amounts of cards depending on empty open and cascade piles)
  //one empty open pile, no open cascade piles
  @Test
  public void validMove2() {
    model.startGame(deck, 8, 3, false);
    model.move(PileType.CASCADE, 0, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 5, 5, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 5, 4, PileType.CASCADE, 0);
    model.move(PileType.CASCADE, 0, 5, PileType.CASCADE, 6);
    assertEquals(8, model.getNumCardsInCascadePile(6));
  }

  //2 empty open piles, moving 2
  @Test
  public void validMoveOpen2() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 0, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 5, 5, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 5, 4, PileType.CASCADE, 0);
    model.move(PileType.CASCADE, 0, 5, PileType.CASCADE, 6);
    assertEquals(8, model.getNumCardsInCascadePile(6));
  }

  //one empty cascade pile, no empty opens, moving 2
  @Test
  public void validMoveCascade1() {
    model.startGame(deck, 13, 4, false);
    model.move(PileType.CASCADE, 0, 3, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 0, 2, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 0, 1, PileType.OPEN, 2);
    model.move(PileType.CASCADE, 0, 0, PileType.OPEN, 3);
    model.move(PileType.CASCADE, 4, 3, PileType.CASCADE, 6);
    model.move(PileType.CASCADE, 6, 3, PileType.CASCADE, 9);
    assertEquals(6, model.getNumCardsInCascadePile(9));
  }

  //test invalid multi from cascade to cascade, build doesn't exist between piles (not diff colors)
  @Test (expected = IllegalArgumentException.class)
  public void buildNotThereMoveColor() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 1, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 4, 5, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 4, 4, PileType.CASCADE, 1);
    //line below causes exception
    model.move(PileType.CASCADE, 1, 5, PileType.CASCADE, 5);
  }

  //test invalid multi from cascade to cascade, build doesn't exist bn piles (sequence not right)
  @Test (expected = IllegalArgumentException.class)
  public void buildNotThereMoveSeq() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 1, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 4, 5, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 4, 4, PileType.CASCADE, 1);
    //line below causes exception
    model.move(PileType.CASCADE, 1, 5, PileType.CASCADE, 0);
  }

  //test invalid multi from c to c, build doesn't exist in source cards
  @Test (expected = IllegalArgumentException.class)
  public void buildNotThereSource() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 2, 6, PileType.OPEN, 0);
    //line below causes exception
    model.move(PileType.CASCADE, 4, 4, PileType.CASCADE, 2);
  }

  //test invalid multi, trying to move too many cards
  @Test (expected = IllegalArgumentException.class)
  public void tooManyCard() {
    model.startGame(deck, 10, 3, false);
    model.move(PileType.CASCADE, 7, 4, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 3, 4, PileType.CASCADE, 6);
    model.move(PileType.CASCADE, 7, 3, PileType.CASCADE, 6);
    model.move(PileType.CASCADE, 9, 4, PileType.OPEN, 1);
    //line below causes exception
    model.move(PileType.CASCADE, 6, 4, PileType.CASCADE, 8);
  }


}
