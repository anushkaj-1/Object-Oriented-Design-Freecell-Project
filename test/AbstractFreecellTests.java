import cs3500.freecell.controller.FreecellController;
import cs3500.freecell.controller.SimpleFreecellController;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.MultiFreecellModel;
import cs3500.freecell.model.PileType;
import cs3500.freecell.model.hw02.Card;
import cs3500.freecell.model.hw02.CardVal;
import cs3500.freecell.model.hw02.CascadePile;
import cs3500.freecell.model.hw02.FoundationPile;
import cs3500.freecell.model.hw02.ICard;
import cs3500.freecell.model.hw02.IPile;
import cs3500.freecell.model.hw02.OpenPile;
import cs3500.freecell.model.hw02.SimpleFreecellModel;
import cs3500.freecell.model.hw02.Suit;
import cs3500.freecell.view.FreecellTextView;
import cs3500.freecell.view.FreecellView;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests in default folder for all Freecell game implementations.
 */
public abstract class AbstractFreecellTests {

  private FreecellModel<ICard> model;
  private ICard validCard;
  private List<ICard> deck;
  private FreecellView view;
  private IPile cascade;
  private IPile open;
  private IPile foundation;
  private ICard cardToFoundation;
  private ICard cardToFoundation2;
  private ICard cardToCascade2;
  private ICard cardToOpen;
  private FreecellController<ICard> controller;
  private Appendable app;
  private FreecellView viewApp;

  @Before
  public void init() {
    //call abstract method that creates different model types
    model = constructModel();
    validCard = new Card(CardVal.EIGHT, Suit.DIAMOND);
    deck = model.getDeck();
    view = new FreecellTextView(model);
    cascade = new CascadePile();
    open = new OpenPile();
    foundation = new FoundationPile();
    cardToFoundation = new Card(CardVal.A, Suit.CLUB);
    cardToFoundation2 = new Card(CardVal.TWO, Suit.CLUB);
    cardToCascade2 = new Card(CardVal.SEVEN, Suit.SPADE);
    cardToOpen = new Card(CardVal.K, Suit.DIAMOND);
    app = new StringBuilder();
    viewApp = new FreecellTextView(model, app);
  }

  /**
   * Method that creates a new model of either simple freecell or multi freecell.
   * @return respective model
   */
  protected abstract FreecellModel<ICard> constructModel();

  /**
   * Class made for testing purposes - eliminates need for repeated tests.
   * Allows mutual tests to be run with a simple freecell model.
   */
  public static class SimpleFreecellModelTests extends AbstractFreecellTests {

    @Override
    protected FreecellModel<ICard> constructModel() {
      return new SimpleFreecellModel();
    }
  }

  /**
   * Class made for testing purposes - eliminates need for repeated tests.
   * Allows mutual tests to be run with a multi freecell model.
   */
  public static class MultiFreecellModelTests extends AbstractFreecellTests {

    @Override
    protected FreecellModel<ICard> constructModel() {
      return new MultiFreecellModel();
    }
  }


  //mock to test connection between controller and view: startGame method
  @Test
  public void testMockStartGame() {
    Appendable modelLog = new StringBuilder();
    FreecellModel<ICard> modelMock = new MockModel(modelLog);

    Appendable stringBuilder = new StringBuilder();
    Readable stringReader = new StringReader("q");
    String result = deck.toString() + " 8 4 false";

    controller = new SimpleFreecellController(modelMock, stringReader, stringBuilder);
    controller.playGame(deck, 8, 4, false);
    assertEquals(result, modelLog.toString());
  }

  //mock to test connection between controller and view: move method
  @Test
  public void testMockMove() {
    Appendable modelLog = new StringBuilder();
    FreecellModel<ICard> modelMock = new MockModel(modelLog);

    Appendable stringBuilder = new StringBuilder();
    Readable stringReader = new StringReader("C2 hello 7 bye O1 q");
    //startGame input added because startgame needs to be called for move to be called in controller
    String result = deck.toString() + " 8 4 false" + "CASCADE 1 6 OPEN 0";

    controller = new SimpleFreecellController(modelMock, stringReader, stringBuilder);
    controller.playGame(deck, 8, 4, false);

    assertEquals(result, modelLog.toString());
  }

  //mock to test if multimove inputs don't get corrupted
  @Test
  public void testMockMoveMult() {
    Appendable modelLog = new StringBuilder();
    FreecellModel<ICard> modelMock = new MockModel(modelLog);

    Appendable stringBuilder = new StringBuilder();
    Readable stringReader = new StringReader("C2 hello 2 bye O1 q");
    //startGame input added because startgame needs to be called for move to be called in controller
    String result = deck.toString() + " 8 4 false" + "CASCADE 1 1 OPEN 0";

    controller = new SimpleFreecellController(modelMock, stringReader, stringBuilder);
    controller.playGame(deck, 8, 4, false);

    assertEquals(result, modelLog.toString());
  }


  //tests for toString in Card
  @Test
  public void testtoString() {
    assertEquals("8♦", validCard.toString());
  }

  //tests for getDeck()

  @Test
  public void getDeck4Diam() {
    assertEquals("4♦", deck.get(13).toString());
  }

  @Test
  public void getDeck7Spade() {
    assertEquals("7♠", model.getDeck().get(27).toString());
  }

  @Test
  public void getDeckSize() {
    assertEquals(52, model.getDeck().size());
  }

  //tests for startGame//

  //tests whether game resets when startGame called in middle of game
  @Test
  public void startInMiddle() {
    model.startGame(deck, 8, 4, false);
    assertEquals(8, model.getNumCascadePiles());
    model.move(PileType.CASCADE, 4, 5, PileType.OPEN, 0);
    model.startGame(deck, 12, 4, false);
    assertEquals(12, model.getNumCascadePiles());
    assertEquals(52, model.getDeck().size());
    assertEquals(0, model.getNumCardsInOpenPile(0));
  }

  //test valid start --> add lengths of cascade piles to see if 52
  @Test
  public void checkDeckShuff() {
    model.startGame(deck, 7, 2, true);
    int loop = model.getNumCascadePiles();
    int cards = 0;
    while (loop > 0) {
      cards = cards + model.getNumCardsInCascadePile(loop - 1);
      loop = loop - 1;
    }
    assertEquals(52, cards);
  }

  @Test
  public void checkDeckUnshuff() {
    model.startGame(deck, 9, 3, false);
    int loop = model.getNumCascadePiles();
    int cards = 0;
    while (loop > 0) {
      cards = cards + model.getNumCardsInCascadePile(loop - 1);
      loop = loop - 1;
    }
    assertEquals(52, cards);
  }

  //game already started --> open piles
  @Test
  public void checkOPiles() {
    model.startGame(deck, 9, 3, true);
    assertEquals(3, model.getNumOpenPiles());
  }

  //game already started --> cascade piles
  @Test
  public void checkCPiles() {
    model.startGame(deck, 9, 2, true);
    assertEquals(9, model.getNumCascadePiles());
  }

  //short deck
  @Test(expected = IllegalArgumentException.class)
  public void shortDeck() {
    deck.remove(5);
    deck.remove(9);
    //line below causes exception
    model.startGame(deck, 8, 4, true);
  }

  //long deck
  @Test(expected = IllegalArgumentException.class)
  public void longDeck() {
    ICard newCard = deck.get(5);
    deck.add(newCard);
    //line below causes exception
    model.startGame(deck, 8, 4, true);
  }

  //duplicate cards in deck
  @Test(expected = IllegalArgumentException.class)
  public void duplicate() {
    ICard dupCard = deck.get(23);
    deck.set(4, dupCard);
    //line below causes exception
    model.startGame(deck, 8, 4, false);
  }

  //normal game with valid deck --> check pile sizes
  @Test
  public void validPileCheck() {
    model.startGame(deck, 8, 4, false);
    assertEquals(7, model.getNumCardsInCascadePile(2));
    assertEquals(6, model.getNumCardsInCascadePile(6));
  }

  //normal game with valid deck --> check a card index
  @Test
  public void validIndex() {
    model.startGame(deck, 8, 4, false);
    assertEquals("5♥", model.getCascadeCardAt(2, 2).toString());
  }

  //deck is shuffled --> check it --> compare same index to ^
  @Test
  public void testShuff() {
    model.startGame(deck, 8, 4, true);
    assertEquals(false, model.getCascadeCardAt(4, 2).toString().equals("5♥"));
  }

  //invalid startgame with negative pile numbers
  @Test(expected = IllegalArgumentException.class)
  public void negStart() {
    model.startGame(deck, -4, 3, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negStart2() {
    model.startGame(deck, 5, -3, true);
  }

  //many cascade piles --> check that once deck runs out, piles are empty
  @Test
  public void lotCascade() {
    model.startGame(deck, 100, 4, false);
    assertEquals(0, model.getNumCardsInCascadePile(65));
    assertEquals(1, model.getNumCardsInCascadePile(23));
  }

  //no cascade or open piles
  @Test(expected = IllegalArgumentException.class)
  public void onlyFoundation() {
    model.startGame(deck, 0, 0, false);
  }

  //not enough cascade piles
  @Test(expected = IllegalArgumentException.class)
  public void notEnoughCascade() {
    model.startGame(deck, 3, 2, false);
  }

  //no open piles
  @Test(expected = IllegalArgumentException.class)
  public void noOpen() {
    model.startGame(deck, 5, 0, false);
  }

  //tests for move//

  //check whether pile number doesn't change if one becomes empty
  @Test
  public void keepPileNum() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 0);
    assertEquals("2♥", model.getCascadeCardAt(6, 0).toString());
  }

  //valid move from cascade to open
  @Test
  public void toOpen() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    assertEquals("K♠", model.getOpenCardAt(0).toString());
    assertEquals(1, model.getNumCardsInOpenPile(0));
  }

  //invalid move from cascade to open (already full)
  @Test(expected = IllegalArgumentException.class)
  public void toOpenFull() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    //line below causes exception
    model.move(PileType.CASCADE, 6, 5, PileType.OPEN, 0);
  }

  //valid move from cascade to foundation (first card)
  @Test
  public void toFoundation() {
    model.startGame(deck, 52, 4, false);
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    assertEquals("A♦", model.getFoundationCardAt(1, 0).toString());
  }

  //valid move to foundation (not first card)
  @Test
  public void toFoundationMult() {
    model.startGame(deck, 52, 4, false);
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 5, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 9, 0, PileType.FOUNDATION, 1);
    assertEquals(3, model.getNumCardsInFoundationPile(1));
    assertEquals("3♦", model.getFoundationCardAt(1, 2).toString());
  }

  //valid move from open to foundation, not first card
  @Test
  public void toFoundationNonempty() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 5, 0, PileType.FOUNDATION, 1);
    assertEquals(2, model.getNumCardsInFoundationPile(1));
    assertEquals("2♦", model.getFoundationCardAt(1, 1).toString());
  }

  //invalid move from cascade to foundation (not an ace for first card)
  @Test(expected = IllegalArgumentException.class)
  public void toFoundationNotAce() {
    model.startGame(deck, 52, 3, false);
    //line below causes exception
    model.move(PileType.CASCADE, 15, 0, PileType.FOUNDATION, 0);
  }

  //invalid move to foundation (not same suit as last, correct value)
  @Test(expected = IllegalArgumentException.class)
  public void toFoundationWrongSuit() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 0);
    assertEquals("2♥", model.getCascadeCardAt(6, 0).toString());
    //line below causes exception
    model.move(PileType.CASCADE, 6, 0, PileType.FOUNDATION, 0);
  }

  //invalid move to foundation (not +1 from last, correct suit)
  @Test(expected = IllegalArgumentException.class)
  public void toFoundationWrongVal() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 0);
    assertEquals("4♥", model.getCascadeCardAt(14, 0).toString());
    //line below causes exception
    model.move(PileType.CASCADE, 14, 0, PileType.FOUNDATION, 0);
  }

  //invalid move from foundation
  @Test(expected = IllegalArgumentException.class)
  public void fromFoundation() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    //line below causes exception
    model.move(PileType.FOUNDATION, 1, 0, PileType.CASCADE, 5);
  }

  //valid move from open to cascade (not empty)
  @Test
  public void toCascade() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 7, 5, PileType.OPEN, 1);
    model.move(PileType.OPEN, 1, 0, PileType.CASCADE, 1);
    assertEquals(8, model.getNumCardsInCascadePile(1));
    assertEquals("Q♠", model.getCascadeCardAt(1, 7).toString());
  }

  //valid move to cascade (initially empty)
  @Test
  public void toCascadeEmpty() {
    model.startGame(deck, 52, 2, false);
    model.move(PileType.CASCADE, 7, 0, PileType.OPEN, 1);
    assertEquals(0, model.getNumCardsInCascadePile(7));
    model.move(PileType.CASCADE, 25, 0, PileType.CASCADE, 7);
    assertEquals(1, model.getNumCardsInCascadePile(7));
  }

  //invalid move from open to cascade (not diff color)
  @Test(expected = IllegalArgumentException.class)
  public void toCascadeSuitBad() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 7, 5, PileType.OPEN, 1);
    //line below causes exception
    model.move(PileType.OPEN, 1, 0, PileType.CASCADE, 0);
  }

  // invalid move from cascade to cascade (not one less)
  @Test(expected = IllegalArgumentException.class)
  public void toCascadeNumBad() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 1, 6, PileType.CASCADE, 0);
  }

  //invalid move trying to move not last card in cascade pile
  @Test(expected = IllegalArgumentException.class)
  public void notLast() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 3, 3, PileType.OPEN, 2);
  }

  //invalid move pile out of bounds from cascade
  @Test(expected = IllegalArgumentException.class)
  public void outOfBoundsPileC() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 8, 3, PileType.OPEN, 2);
  }

  //invalid move card out of bounds from cascade
  @Test(expected = IllegalArgumentException.class)
  public void outOfBoundsCardC() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 3, 9, PileType.OPEN, 2);
  }

  //invalid move to open pile out of bounds
  @Test(expected = IllegalArgumentException.class)
  public void outOfBoundsToPileO() {
    model.startGame(deck, 8, 4, false);
    //line below causes exception
    model.move(PileType.CASCADE, 3, 4, PileType.OPEN, 4);
  }

  //invalid move pile out of bounds from open
  @Test(expected = IllegalArgumentException.class)
  public void outOfBoundsFromPileO() {
    model.startGame(deck, 52, 4, false);
    model.move(PileType.CASCADE, 2, 0, PileType.OPEN, 0);
    //line below causes exception
    model.move(PileType.OPEN, 5, 0, PileType.CASCADE, 2);
  }

  //valid move with shuffled
  @Test
  public void moveShuff() {
    model.startGame(deck, 52, 3, true);
    model.move(PileType.CASCADE, 24, 0, PileType.OPEN, 2);
    assertEquals(1, model.getNumCardsInOpenPile(2));
    assertEquals(false, model.getOpenCardAt(2).toString().equals("6♥"));
  }

  //invalid move where game hasn't started
  @Test(expected = IllegalStateException.class)
  public void gameDidntStart() {
    model.move(PileType.CASCADE, 2, 4,
        PileType.FOUNDATION, 3);
  }

  //tests for isGameOver//

  //game is over (all cards in foundation piles)
  @Test
  public void over() {
    model.startGame(deck, 52, 3, false);
    //clubs
    model.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 4, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 8, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 12, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 16, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 20, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 24, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 28, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 32, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 36, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 40, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 44, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 48, 0, PileType.FOUNDATION, 0);
    //diamonds
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 5, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 9, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 13, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 17, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 21, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 25, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 29, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 33, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 37, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 41, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 45, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 49, 0, PileType.FOUNDATION, 1);
    //hearts
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 6, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 10, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 14, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 18, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 22, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 26, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 30, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 34, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 38, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 42, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 46, 0, PileType.FOUNDATION, 2);
    model.move(PileType.CASCADE, 50, 0, PileType.FOUNDATION, 2);
    //spades
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 7, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 11, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 15, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 19, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 23, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 27, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 31, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 35, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 39, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 43, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 47, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 51, 0, PileType.FOUNDATION, 3);
    assertEquals(true, model.isGameOver());
  }

  //game is not over (not all cards in foundation piles)
  @Test
  public void notOver() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 3, 5, PileType.CASCADE, 5);
    model.move(PileType.CASCADE, 1, 6, PileType.OPEN, 1);
    assertEquals(false, model.isGameOver());
  }

  //game didn't start
  @Test
  public void didntStart() {
    assertEquals(false, model.isGameOver());
  }

  //tests for getNumCardsInFoundationPile//

  //no cards in foundation pile
  @Test
  public void noCardsF() {
    model.startGame(deck, 8, 4, true);
    model.move(PileType.CASCADE, 6, 5, PileType.OPEN, 0);
    assertEquals(0, model.getNumCardsInFoundationPile(1));
  }

  //valid with cards in foundation pile
  @Test
  public void cardsInF() {
    model.startGame(deck, 52, 3, false);
    //3 cards into clubs
    model.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 4, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 8, 0, PileType.FOUNDATION, 0);
    //5 cards in diamonds
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 5, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 9, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 13, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 17, 0, PileType.FOUNDATION, 1);
    //1 card in hearts
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 2);
    //9 cards into spades
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 7, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 11, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 15, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 19, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 23, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 27, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 31, 0, PileType.FOUNDATION, 3);
    model.move(PileType.CASCADE, 35, 0, PileType.FOUNDATION, 3);
    assertEquals(3, model.getNumCardsInFoundationPile(0));
    assertEquals(5, model.getNumCardsInFoundationPile(1));
    assertEquals(1, model.getNumCardsInFoundationPile(2));
    assertEquals(9, model.getNumCardsInFoundationPile(3));
  }

  //outside of index
  @Test(expected = IllegalArgumentException.class)
  public void outIndexF() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInFoundationPile(4);
  }

  //neg index
  @Test(expected = IllegalArgumentException.class)
  public void negIndexF() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInFoundationPile(-2);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedF() {
    model.getNumCardsInFoundationPile(2);
  }

  //tests for getNumCascadePiles//

  //game has started
  @Test
  public void gameStartedCPiles() {
    model.startGame(deck, 10, 4, true);
    assertEquals(10, model.getNumCascadePiles());
  }

  //game hasn't started
  @Test
  public void gameNotStartedCPiles() {
    assertEquals(-1, model.getNumCascadePiles());
  }

  //tests for getNumCardsInCascadePile//

  //no cards in cascade pile
  @Test
  public void noCardsC() {
    model.startGame(deck, 52, 3, false);
    model.move(PileType.CASCADE, 8, 0, PileType.CASCADE, 13);
    assertEquals(0, model.getNumCardsInCascadePile(8));
  }

  //valid with cards in cascade pile
  @Test
  public void cardsC() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 4, 5, PileType.CASCADE, 1);
    model.move(PileType.CASCADE, 2, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 2, 5, PileType.CASCADE, 1);
    assertEquals(9, model.getNumCardsInCascadePile(1));
    assertEquals(5, model.getNumCardsInCascadePile(2));
    assertEquals(5, model.getNumCardsInCascadePile(4));
    assertEquals(7, model.getNumCardsInCascadePile(0));
  }

  //outside of index
  @Test(expected = IllegalArgumentException.class)
  public void outIndexC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInCascadePile(8);
  }

  //neg index
  @Test(expected = IllegalArgumentException.class)
  public void negIndexC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInFoundationPile(-1);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedC() {
    model.getNumCardsInCascadePile(2);
  }

  //tests for getNumCardsInOpenPile//

  //no cards in open pile
  @Test
  public void noCardsO() {
    model.startGame(deck, 8, 4, true);
    assertEquals(0, model.getNumCardsInOpenPile(0));
    assertEquals(0, model.getNumCardsInOpenPile(2));
  }

  //valid with card in open pile
  @Test
  public void cardsInO() {
    model.startGame(deck, 8, 4, true);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 4, 5, PileType.OPEN, 1);
    assertEquals(1, model.getNumCardsInOpenPile(0));
    assertEquals(1, model.getNumCardsInOpenPile(1));
  }

  //outside of index
  @Test(expected = IllegalArgumentException.class)
  public void outOfIndexO() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInOpenPile(4);
  }

  //neg index
  @Test(expected = IllegalArgumentException.class)
  public void negIndexO() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getNumCardsInOpenPile(-1);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedO() {
    model.getNumCardsInOpenPile(2);
  }

  //tests for getNumOpenPiles//

  //game has started
  @Test
  public void startedNumOpen() {
    model.startGame(deck, 8, 4, true);
    assertEquals(4, model.getNumOpenPiles());
  }

  //game hasn't started
  @Test
  public void notStartedNumOpen() {
    assertEquals(-1, model.getNumOpenPiles());
  }

  //tests for getFoundationCardAt//

  //pile index out of bounds
  @Test(expected = IllegalArgumentException.class)
  public void invalidPileGetF() {
    model.startGame(deck, 52, 4, true);
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.getFoundationCardAt(4, 0);
  }

  //pile index neg
  @Test(expected = IllegalArgumentException.class)
  public void negPileGetF() {
    model.startGame(deck, 52, 4, true);
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.getFoundationCardAt(-1, 0);
  }

  //card index out of bounds
  @Test(expected = IllegalArgumentException.class)
  public void invalidCardGetF() {
    model.startGame(deck, 52, 4, true);
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.getFoundationCardAt(0, 1);
  }

  //card index neg
  @Test(expected = IllegalArgumentException.class)
  public void negCardGetF() {
    model.startGame(deck, 52, 4, true);
    model.move(PileType.CASCADE, 2, 0, PileType.FOUNDATION, 0);
    //line below causes exception
    model.getFoundationCardAt(0, -1);
  }

  //invalid from empty pile
  @Test(expected = IllegalArgumentException.class)
  public void emptyGetF() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getFoundationCardAt(0, 0);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedGetF() {
    model.getFoundationCardAt(2, 3);
  }

  //valid card getting
  @Test
  public void validGetF() {
    model.startGame(deck, 52, 4, false);
    System.out.println(model.getCascadeCardAt(3, 0));
    //5 cards into spades pile
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 7, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 11, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 15, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 19, 0, PileType.FOUNDATION, 0);
    assertEquals("4♠", model.getFoundationCardAt(0, 3).toString());
    assertEquals("A♠", model.getFoundationCardAt(0, 0).toString());
  }

  @Test
  public void validGetFMultPiles() {
    model.startGame(deck, 52, 4, false);
    //5 cards into spades pile
    model.move(PileType.CASCADE, 3, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 7, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 11, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 15, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 19, 0, PileType.FOUNDATION, 0);
    //3 cards into diamonds pile
    model.move(PileType.CASCADE, 1, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 5, 0, PileType.FOUNDATION, 1);
    model.move(PileType.CASCADE, 9, 0, PileType.FOUNDATION, 1);
    assertEquals("3♦", model.getFoundationCardAt(1, 2).toString());
    assertEquals("A♠", model.getFoundationCardAt(0, 0).toString());
  }

  //tests for getCascadeCardAt//

  //pile index invalid
  @Test(expected = IllegalArgumentException.class)
  public void invalidPileGetC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getCascadeCardAt(8, 3);
  }

  //neg pile index
  @Test(expected = IllegalArgumentException.class)
  public void negPileGetC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getCascadeCardAt(-1, 2);
  }

  //card index invalid
  @Test(expected = IllegalArgumentException.class)
  public void invalidCardGetC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getCascadeCardAt(3, 7);
  }

  //neg pile index
  @Test(expected = IllegalArgumentException.class)
  public void negCardGetC() {
    model.startGame(deck, 8, 4, true);
    //line below causes exception
    model.getCascadeCardAt(4, -1);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedGetC() {
    model.getCascadeCardAt(3, 4);
  }

  //valid card getting
  @Test
  public void validGetC() {
    model.startGame(deck, 8, 4, false);
    assertEquals("7♠", model.getCascadeCardAt(3, 3).toString());
  }

  //valid card getting after moving
  @Test
  public void validGetCMoves() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 4, 5, PileType.CASCADE, 1);
    model.move(PileType.CASCADE, 2, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 2, 5, PileType.CASCADE, 1);
    assertEquals("9♥", model.getCascadeCardAt(2, 4).toString());
    assertEquals("J♥", model.getCascadeCardAt(1, 8).toString());
  }

  //tests for getOpenCardAt//

  //pile index invalid
  @Test(expected = IllegalArgumentException.class)
  public void invalidPileGetO() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 3);
    model.getOpenCardAt(4);
  }

  //pile empty
  @Test
  public void emptyPileGetO() {
    model.startGame(deck, 8, 4, false);
    assertEquals(null, model.getOpenCardAt(0));
  }

  //

  //neg pile index
  @Test(expected = IllegalArgumentException.class)
  public void negPileGetO() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    //line below causes exception
    model.getOpenCardAt(-1);
  }

  //game hasn't started
  @Test(expected = IllegalStateException.class)
  public void notStartedGetO() {
    model.getOpenCardAt(2);
  }

  //valid card getting
  @Test
  public void validGetO() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 3, 6, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 6, 5, PileType.OPEN, 1);
    assertEquals("K♠", model.getOpenCardAt(0).toString());
    assertEquals("Q♥", model.getOpenCardAt(1).toString());
  }

  ///ICard methods tests///

  //tests for getValue//

  //card value
  @Test
  public void cardValue() {
    assertEquals(8, validCard.getValue());
  }

  //card from model
  @Test
  public void cardValGame() {
    model.startGame(deck, 8, 4, false);
    assertEquals(11, model.getCascadeCardAt(0, 5).getValue());
  }

  //tests for getSuit//

  //card suit
  @Test
  public void cardSuit() {
    assertEquals("♦", validCard.getSuit().toString());
  }

  //card from model
  @Test
  public void cardSuitGame() {
    model.startGame(deck, 8, 4, false);
    assertEquals("♣", model.getCascadeCardAt(0, 5).getSuit().toString());
  }

  //tests for equals//

  //two equal cards
  @Test
  public void equalCards() {
    model.startGame(deck, 8, 4, false);
    assertEquals(true, validCard.equals(model.getCascadeCardAt(5, 3)));
  }

  //two unequal cards
  @Test
  public void unequalCards() {
    model.startGame(deck, 8, 4, false);
    assertEquals(false, validCard.equals(model.getCascadeCardAt(5, 4)));
  }

  //tests for hashcode//

  //hashcode for two equal cards
  @Test
  public void hashEqualCards() {
    model.startGame(deck, 8, 4, false);
    assertEquals(true, validCard.hashCode() == model.getCascadeCardAt(5, 3).hashCode());
  }

  //hashcode for two unequal cards
  @Test
  public void hashUnequalCards() {
    model.startGame(deck, 8, 4, false);
    assertEquals(false, validCard.hashCode() == model.getCascadeCardAt(5, 4).hashCode());
  }

  //tests for toString//

  //card to string
  @Test
  public void cardString() {
    assertEquals("8♦", validCard.toString());
  }

  //card to string from model
  @Test
  public void cardStringGame() {
    model.startGame(deck, 8, 4, false);
    assertEquals("J♣", model.getCascadeCardAt(0, 5).toString());
  }

  ///IPile methods tests///

  //tests for addToPile//

  //add to cascade
  @Test
  public void addToCascade() {
    cascade.addToPile(validCard, false);
    assertEquals(1, cascade.numCards());
    assertEquals("8♦", cascade.getCard(0).toString());
    cascade.addToPile(cardToCascade2, false);
    assertEquals(2, cascade.numCards());
    assertEquals("7♠", cascade.getCard(1).toString());
  }

  //invalid
  @Test(expected = IllegalArgumentException.class)
  public void addToCascadeInvalid() {
    cascade.addToPile(cardToCascade2, false);
    //line below throws exception
    cascade.addToPile(validCard, false);
  }

  //add to open
  @Test
  public void addToOpen() {
    open.addToPile(cardToOpen, false);
    assertEquals("K♦", open.getCard(0).toString());
  }

  //invalid add to open
  @Test(expected = IllegalArgumentException.class)
  public void addToOpenInvalid() {
    open.addToPile(cardToOpen, false);
    //line below throws exception
    open.addToPile(validCard, false);
  }

  //add to foundation
  @Test
  public void addToFound() {
    foundation.addToPile(cardToFoundation, false);
    assertEquals(1, foundation.numCards());
    assertEquals("A♣", foundation.getCard(0).toString());
    foundation.addToPile(cardToFoundation2, false);
    assertEquals(2, foundation.numCards());
    assertEquals("2♣", foundation.getCard(1).toString());
  }

  //invalid add to foundation, next card
  @Test(expected = IllegalArgumentException.class)
  public void invalidAddToFound() {
    foundation.addToPile(cardToFoundation, false);
    //line below throws exception
    foundation.addToPile(validCard, false);
  }

  //invalid add to foundation, first card
  @Test(expected = IllegalArgumentException.class)
  public void invalidInitAddF() {
    foundation.addToPile(validCard, false);
  }

  //tests for removeFrom//

  //invalid from foundation
  @Test(expected = IllegalArgumentException.class)
  public void invalidRemoveFromF() {
    foundation.addToPile(cardToFoundation, false);
    foundation.addToPile(cardToFoundation2, false);
    //line below throws exception
    foundation.removeFrom(1);
  }

  //valid from cascade
  @Test
  public void validRemoveC() {
    cascade.addToPile(validCard, false);
    cascade.addToPile(cardToCascade2, false);
    cascade.removeFrom(1);
    assertEquals(1, cascade.numCards());
  }


  //valid from open
  @Test
  public void validRemoveO() {
    open.addToPile(cardToOpen, false);
    open.removeFrom(0);
    assertEquals(0, open.numCards());
  }

  //tests for numCards//

  //empty foundation
  @Test
  public void countF() {
    assertEquals(0, foundation.numCards());
  }

  //foundation
  @Test
  public void countFThere() {
    foundation.addToPile(cardToFoundation, false);
    foundation.addToPile(cardToFoundation2, false);
    assertEquals(2, foundation.numCards());
  }

  //empty open
  @Test
  public void countOEmpty() {
    assertEquals(0, open.numCards());
  }

  //full open
  @Test
  public void countO() {
    open.addToPile(cardToOpen, false);
    assertEquals(1, open.numCards());
  }

  //empty cascade
  @Test
  public void countCEmpty() {
    assertEquals(0, cascade.numCards());
  }

  //cascade
  @Test
  public void countC() {
    cascade.addToPile(validCard, false);
    cascade.addToPile(cardToCascade2, false);
    assertEquals(2, cascade.numCards());
  }

  //tests for getCard//

  //invalid cascade
  @Test(expected = IllegalArgumentException.class)
  public void getCInvalid() {
    cascade.getCard(0);
  }

  //valid cascade
  @Test
  public void getC() {
    cascade.addToPile(validCard, false);
    cascade.addToPile(cardToCascade2, false);
    assertEquals("8♦", cascade.getCard(0).toString());
  }

  //invalid foundation
  @Test(expected = IllegalArgumentException.class)
  public void getFInvalid() {
    foundation.getCard(0);
  }

  //valid foundation
  @Test
  public void getF() {
    foundation.addToPile(cardToFoundation, false);
    foundation.addToPile(cardToFoundation2, false);
    assertEquals("2♣", foundation.getCard(1).toString());
  }

  //invalid open
  @Test
  public void getOInvalid() {
    assertEquals(null, open.getCard(0));
  }

  //valid open
  @Test
  public void getO() {
    open.addToPile(cardToOpen, false);
    assertEquals("K♦", open.getCard(0).toString());
  }

  ///FreecellTextView methods tests///

  //tests for toString//

  //game hasn't started
  @Test
  public void notStarted() {
    assertEquals("", view.toString());
  }

  //unmoved started game
  @Test
  public void unMoved() {
    model.startGame(deck, 8, 4, false);
    assertEquals("F1:\nF2:\nF3:\nF4:\nO1:\nO2:\nO3:\nO4:\nC1: A♣, 3♣, 5♣, 7♣, 9♣, J♣, K♣\n"
        + "C2: A♦, 3♦, 5♦, 7♦, 9♦, J♦, K♦\nC3: A♥, 3♥, 5♥, 7♥, 9♥, J♥, K♥\nC4: A♠, 3♠, 5♠, 7♠, 9♠"
        + ", J♠, K♠\nC5: 2♣, 4♣, 6♣, 8♣, 10♣, Q♣\nC6: 2♦, 4♦, 6♦, 8♦, 10♦, Q♦\nC7: 2♥, 4♥, 6♥, 8♥,"
        + " 10♥, Q♥\nC8: 2♠, 4♠, 6♠, 8♠, 10♠, Q♠", view.toString());
  }

  //midgame
  @Test
  public void moved() {
    model.startGame(deck, 20, 4, false);
    model.move(PileType.CASCADE, 0, 2, PileType.CASCADE, 5);
    model.move(PileType.CASCADE, 6, 2, PileType.OPEN, 0);
    model.move(PileType.CASCADE, 0, 1, PileType.CASCADE, 6);
    model.move(PileType.CASCADE, 0, 0, PileType.FOUNDATION, 0);
    model.move(PileType.CASCADE, 4, 2, PileType.CASCADE, 0);
    // model.move(PileType.CASCADE, 4, 2, PileType.CASCADE, 0);
    model.move(PileType.CASCADE, 4, 1, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 4, 0, PileType.FOUNDATION, 0);
    assertEquals("F1: A♣, 2♣\nF2:\nF3:\nF4:\nO1: Q♥\nO2: 7♣\nO3:\nO4:\nC1: Q♣\nC2: A♦, 6♦, "
        + "J♦\nC3: A♥, 6♥, J♥\nC4: A♠, 6♠, J♠\nC5:\nC6: 2♦, 7♦, Q♦, J♣\nC7: 2♥, 7♥, 6♣\n"
        + "C8: 2♠, 7♠, Q♠\nC9: 3♣, 8♣, K♣\nC10: 3♦, 8♦, K♦\nC11: 3♥, 8♥, K♥\nC12: 3♠, 8♠, K♠\n"
        + "C13: 4♣, 9♣\nC14: 4♦, 9♦\nC15: 4♥, 9♥\nC16: 4♠, 9♠\nC17: 5♣, 10♣\nC18: 5♦, 10♦\n"
        + "C19: 5♥, 10♥\nC20: 5♠, 10♠", view.toString());
  }

  //tests for renderBoard//

  //test that IOException is thrown - throws IllegalStateException if IOException thrown
  @Test(expected = IllegalStateException.class)
  public void renderBoardError() {
    FreecellView view = new FreecellTextView(model, new IOErrorAppend());
    try {
      view.renderBoard();
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  //testing renderboard unmoved
  @Test
  public void renderBoardCheckUnmoved() {
    model.startGame(deck, 8, 4, false);

    try {
      viewApp.renderBoard();
    } catch (IOException e) {
      throw new IllegalStateException();
    }
    assertEquals("F1:\nF2:\nF3:\nF4:\nO1:\nO2:\nO3:\nO4:\nC1: A♣, 3♣, 5♣, 7♣, 9♣, J♣, K♣\n"
        + "C2: A♦, 3♦, 5♦, 7♦, 9♦, J♦, K♦\nC3: A♥, 3♥, 5♥, 7♥, 9♥, J♥, K♥\nC4: A♠, 3♠, 5♠, 7♠, 9♠"
        + ", J♠, K♠\nC5: 2♣, 4♣, 6♣, 8♣, 10♣, Q♣\nC6: 2♦, 4♦, 6♦, 8♦, 10♦, Q♦\nC7: 2♥, 4♥, 6♥, 8♥,"
        + " 10♥, Q♥\nC8: 2♠, 4♠, 6♠, 8♠, 10♠, Q♠", app.toString()); //check if right???
  }

  //testing renderboard with moves
  @Test
  public void renderBoardCheckMoved() {
    model.startGame(deck, 8, 4, false);
    model.move(PileType.CASCADE, 0, 6, PileType.OPEN, 1);
    model.move(PileType.CASCADE, 4, 5, PileType.OPEN, 0);
    try {
      viewApp.renderBoard();
    } catch (IOException e) {
      throw new IllegalStateException();
    }
    assertEquals("F1:\nF2:\nF3:\nF4:\nO1: Q♣\nO2: K♣\nO3:\nO4:\n"
        + "C1: A♣, 3♣, 5♣, 7♣, 9♣, J♣\nC2: A♦, 3♦, 5♦, 7♦, 9♦, J♦, K♦\n"
        + "C3: A♥, 3♥, 5♥, 7♥, 9♥, J♥, K♥\nC4: A♠, 3♠, 5♠, 7♠, 9♠, J♠, K♠\n"
        + "C5: 2♣, 4♣, 6♣, 8♣, 10♣\nC6: 2♦, 4♦, 6♦, 8♦, 10♦, Q♦\nC7: 2♥, 4♥, 6♥, 8♥, 10♥, Q♥\n"
        + "C8: 2♠, 4♠, 6♠, 8♠, 10♠, Q♠", app.toString());
  }

  //tests for renderMessage//

  //test that IOException is thrown - throws IllegalStateException if IOException thrown
  @Test(expected = IllegalArgumentException.class)
  public void renderMessageError() {
    FreecellView view = new FreecellTextView(model, new IOErrorAppend());
    try {
      view.renderMessage("hello");
    } catch (IOException e) {
      throw new IllegalArgumentException();
    }
  }

  ///SimpleFreecellController tests///

  //constructor tests

  //null model object
  @Test (expected = IllegalArgumentException.class)
  public void nullModel() {
    new SimpleFreecellController(null, new StringReader("hi"), app);
  }

  //null readable object
  @Test (expected = IllegalArgumentException.class)
  public void nullReadable() {
    new SimpleFreecellController(model, null, app);
  }

  //null appendable object
  @Test (expected = IllegalArgumentException.class)
  public void nullAppendable() {
    new SimpleFreecellController(model, new StringReader("hi"), null);
  }


  //tests for playGame//

  //invalid game parameters: not enough cascade piles
  @Test
  public void invalidCascadeStart() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    controller.playGame(deck, 3, 3, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: negative cascade piles
  @Test
  public void negCascadeStart() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    controller.playGame(deck, -1, 3, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: not enough open piles
  @Test
  public void invalidOpenStart() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    controller.playGame(deck, 6, 0, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: negative open piles
  @Test
  public void negOpenStart() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    controller.playGame(deck, 6, -1, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: empty deck
  @Test
  public void emptyDeck() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    List<ICard> empty = new ArrayList<>();
    controller.playGame(empty, 6, -1, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: deck more than 52 cards
  @Test
  public void longDeckController() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    ICard extra1 = deck.get(3);
    ICard extra2 = deck.get(32);
    deck.add(extra1);
    deck.add(extra2);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game params: deck has less than 52 cards
  @Test
  public void shortDeckController() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    deck.remove(9);
    deck.remove(39);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //invalid game parameters: deck has duplicates but deck is 52 cards
  @Test
  public void dupDeckController() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    ICard dup = deck.get(3);
    deck.set(33, dup);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("Could not start game."));
  }

  //deck given is null
  //should throw IllegalArgumentException
  @Test(expected = IllegalArgumentException.class)
  public void nullDeckController() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), app);
    //line below causes exception
    controller.playGame(null, 8, 4, true);
  }

  //writing to Appendable object fails
  //should throw IllegalStateException
  @Test(expected = IllegalStateException.class)
  public void appendFail() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 8 O2 q"), new IOErrorAppend());
    controller.playGame(deck, 8, 4, false);
  }


  //input of q for source pile
  @Test
  public void quitSource() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("q C4 k 49 o3"), app);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("\nGame quit prematurely."));
  }

  //input of q for card index
  @Test
  public void quitCard() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 q O2 q"), app);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("\nGame quit prematurely."));
  }

  //input of q for destination pile
  @Test
  public void quitDest() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C3 6 q F3"), app);
    controller.playGame(deck, 8, 4, true);
    String result = app.toString();
    assertTrue(result.endsWith("\nGame quit prematurely."));
  }

  //move from cascade to open
  @Test
  public void fullValidMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that moved card is shown in appendable
    assertTrue(result.contains("O3: K♣"));
  }

  //run out of input before a move made
  @Test(expected = IllegalStateException.class)
  public void noMove() {
    FreecellController<ICard> controller = new SimpleFreecellController(model,
        new StringReader("C1 7"), app);
    //line below causes exception
    controller.playGame(deck, 8, 4, false);
  }

  //run out of input after a valid move
  @Test(expected = IllegalStateException.class)
  public void moveThenRunOut() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 o3 "), app);
    //line below causes exception
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //tests that move still happened
    assertTrue(result.contains("O3: K♣"));
  }

  //valid move with invalid card index present
  //actual move is C1 7 O3
  @Test
  public void validMoveInvalidCard() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 F1 7 o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that moved card is shown in appendable
    assertTrue(result.contains("O3: K♣"));
    //checks that message to enter again also present
    assertTrue(result.contains("Enter a valid card index."));
  }

  //valid move with invalid source pile index present
  //actual move is C1 7 O3
  @Test
  public void validMoveInvalidSource() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("12 C1 7 o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that moved card is shown in appendable
    assertTrue(result.contains("O3: K♣"));
    //checks that message to enter again also present
    assertTrue(result.contains("Enter a valid source pile."));
  }

  //valid move with invalid dest pile index present
  //actual move is C1 7 O3
  @Test
  public void validMoveInvalidDest() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 ejiwof o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that moved card is shown in appendable
    assertTrue(result.contains("O3: K♣"));
    //checks that message to enter again also present
    assertTrue(result.contains("Enter a valid destination pile."));
  }

  //move with a lot of invalid destination pile indexes
  @Test
  public void validWithInvalidInputs() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 ejiwof 8 o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that moved card is shown in appendable
    assertTrue(result.contains("O3: K♣"));
    //checks that message to enter again also present
    assertTrue(
        result.contains("Enter a valid destination pile.\nEnter a valid destination pile.\n"));
  }

  //move with lots of spaces and new lines between inputs
  @Test
  public void validMoveExcessiveSpacesAndLines() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1      \n  \n 7 \n   o3 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //checks that card still moved and is shown in appendable
    assertTrue(result.contains("O3: K♣"));
  }

  //move to foundation pile from cascade
  @Test
  public void validMovetoFoundation() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("c1 1 F1 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    //check that card moved is shown in appendable
    assertTrue(result.contains("F1: A♣"));
  }

  //move to foundation pile from open
  @Test
  public void validMovetoFoundationFromOpen() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("c1 1 O1 O1 1 F1 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    //check that card moved is shown in appendable
    assertTrue(result.contains("F1: A♣"));
  }

  //move to cascade from cascade
  @Test
  public void validMovetoCascadefromCascade() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C5 6 C2 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("C2: A♦, 3♦, 5♦, 7♦, 9♦, J♦, K♦, Q♣"));
  }

  //test quit request
  @Test
  public void invalidQuitCall() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("qqqq q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    //makes sure message asking for new source and quit message exist
    assertTrue(result.contains("Enter a valid source pile.\n\nGame quit prematurely."));
  }


  //valid moves multiple all in one string
  @Test
  public void multMoves() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("c1 1 O1 O1 1 F1 C2 1 f2 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("F1: A♣\nF2: A♦"));
  }

  //invalid move - open pile card index invalid in model
  @Test
  public void invalidMoveModelFromOpen() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("c1 7 O1 O1 0 F1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Card invalid."));
  }

  //invalid move - cascade pile card index invalid in model
  @Test
  public void invalidMoveModelFromCascade() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 9 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Card invalid."));
  }

  //invalid move - can't remove from foundation in model
  @Test
  public void invalidFoundationMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 1 F1 F1 1 O1 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Can't remove from a foundation pile."));
  }

  //invalid move in model - cascade source pile index out of bounds
  @Test
  public void invalidCascadeMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C9 6 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Cascade pile index out of bounds."));
  }

  //invalid move in model - open source pile index out of bounds
  @Test
  public void invalidOpenMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 7 O1 O5 1 F1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Open pile index out of bounds."));
  }

  //move that is valid in controller, not in model --> destination pile index out of bounds
  @Test
  public void invalidMoveToFoundation() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 F5 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Foundation pile index out of bounds."));
  }


  //move valid in controller, not in model --> card index negative
  @Test
  public void negCardIndex() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 -3 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Card invalid."));
  }

  //move valid in controller, not in model --> source pile negative
  @Test
  public void negSourceIndex() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C-1 7 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Cascade pile index out of bounds."));
  }

  //move valid in controller, not in model --> destination pile negative
  @Test
  public void negDestIndex() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 O-1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Open pile index out of bounds."));
  }


  //move valid in controller, not in model --> not adding ace first to foundation
  @Test
  public void notAceFoundation() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 7 F1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "First card needs to be an ace."));
  }

  //move valid controller, not model --> not first card foundation, not same suit and +1 value
  @Test
  public void notValidFoundationMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 1 F1 C11 1 F1 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Can't add this card to the foundation pile."));
  }

  //move valid controller, not model --> subsequent cascade card, not different color suit and -1
  @Test
  public void notValidCascadeMove() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 7 C1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. "
        + "Card cannot be added to this cascade pile."));
  }

  //move valid controller, not model --> open pile is already full
  //return message "Invalid move. Try again. Cannot add a card to a full open pile."
  @Test
  public void fullOpenPile() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 7 O1 C1 7 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Can't add a card to a full Open pile."));
    //checks that move didn't happen
    assertTrue(result.contains("O1: K♦"));
  }

  //move valid controller, not model --> tried to remove card that was not last in cascade pile
  //return message "Invalid move. Try again. Can only remove the last card in a cascade pile."
  @Test
  public void cascadePileNotLast() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C2 3 O1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result
        .contains("Invalid move. Try again. Card invalid."));
  }

  //move valid controller, not model --> tried to remove from empty open pile
  //return message "Invalid move. Try again. Open pile is empty."
  @Test
  public void emptyOpen() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("O2 1 F1 q"), app);
    controller.playGame(deck, 8, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Card invalid."));
  }

  //move valid controller, not model --> tried to remove from empty cascade pile
  //return message "Invalid move. Try again. Cascade pile is empty."
  @Test
  public void emptyCascade() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 1 O1 C1 1 O2 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("Invalid move. Try again. Card invalid."));
  }

  //game is over --> need message
  @Test
  public void gameOverController() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 1 F1 C5 1 F1 C9 1 F1 C13 1 "
            + "F1 C17 1 F1 C21 1 F1 C25 1 F1 C29 1 F1 C33 1 F1 C37 1 F1 C41 1 F1 C45 1 F1"
            + " C49 1 F1 C2 1 F2 C6 1 F2 C10 1 F2 C14 1 F2 C18 1 F2 C22 1 F2 C26 1 F2 C30 1 F2"
            + " C34 1 F2 C38 1 F2 C42 1 F2 C46 1 F2 C50 1 F2 C3 1 F3 C7 1 F3 C11 1 F3 C15 1 F3"
            + " C19 1 F3 C23 1 F3 C27 1 F3 C31 1 F3 C35 1 F3 C39 1 F3 C43 1 F3 C47 1 F3 C51 1 F3"
            + " C4 1 F4 C8 1 F4 C12 1 F4 C16 1 F4 C20 1 F4 C24 1 F4 C28 1 F4 C32 1 F4 C36 1 F4 C40"
            + " 1 F4 C44 1 F4 C48 1 F4 C52 1 F4 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("F1: A♣, 2♣, 3♣, 4♣, 5♣, 6♣, 7♣, 8♣, 9♣, 10♣, J♣, Q♣, K♣\n"
        + "F2: A♦, 2♦, 3♦, 4♦, 5♦, 6♦, 7♦, 8♦, 9♦, 10♦, J♦, Q♦, K♦\n"
        + "F3: A♥, 2♥, 3♥, 4♥, 5♥, 6♥, 7♥, 8♥, 9♥, 10♥, J♥, Q♥, K♥\n"
        + "F4: A♠, 2♠, 3♠, 4♠, 5♠, 6♠, 7♠, 8♠, 9♠, 10♠, J♠, Q♠, K♠"));
    assertTrue(result.contains("Game over."));
  }

  //game is over --> with invalid inputs in between
  @Test
  public void gameOverWithInvalid() {
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new StringReader("C1 hello 1 F1 C5 1 F1 C9 1 F1 C13 1 "
            + "F1 C17 1 F1 C21 1 F1 C25 1 F1 C29 1 F1 C33 1 F1 C37 1 F1 C41 1 F1 C45 1 F1"
            + " C49 1 F1 C2 1 F2 C6 1 F2 Q29 C10 1 F2 C14 1 F2 C18 1 F2 C22 1 F2 C26 1 F2 C30 1 F2"
            + " C34 1 F2 C38 1 F2 C42 1 F2 C46 1 F2 C50 1 F2 C3 o2 1 F3 C7 1 F3 C11 1 F3 C15 1 F3"
            + " C19 1 F3 C23 1 F3 C27 1 F3 C31 1 F3 C35 1 F3 C39 1 F3 C43 1 F3 C47 1 F3 C51 1 F3"
            + " C4 1 F4 C8 1 F4 C12 1 F4 cell C16 1 F4 C20 1 F4 C24 1 F4 C28 1 F4 C32 1 F4 C36 1 "
            + "F4 C40 1 F4 C44 1 F4 C48 1 F4 C52 1 F4 q"), app);
    controller.playGame(deck, 52, 4, false);
    String result = app.toString();
    assertTrue(result.contains("F1: A♣, 2♣, 3♣, 4♣, 5♣, 6♣, 7♣, 8♣, 9♣, 10♣, J♣, Q♣, K♣\n"
        + "F2: A♦, 2♦, 3♦, 4♦, 5♦, 6♦, 7♦, 8♦, 9♦, 10♦, J♦, Q♦, K♦\n"
        + "F3: A♥, 2♥, 3♥, 4♥, 5♥, 6♥, 7♥, 8♥, 9♥, 10♥, J♥, Q♥, K♥\n"
        + "F4: A♠, 2♠, 3♠, 4♠, 5♠, 6♠, 7♠, 8♠, 9♠, 10♠, J♠, Q♠, K♠"));
    assertTrue(result.contains("Game over."));
  }
}
