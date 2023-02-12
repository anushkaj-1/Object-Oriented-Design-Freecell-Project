import static org.junit.Assert.assertTrue;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.FreecellModelCreator;
import cs3500.freecell.model.FreecellModelCreator.GameType;
import cs3500.freecell.model.MultiFreecellModel;
import cs3500.freecell.model.hw02.ICard;
import cs3500.freecell.model.hw02.SimpleFreecellModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for creator factory class. Contains tests that make sure desired objects are created
 * correctly.
 */
public class FreecellCreatorTests {

  FreecellModelCreator creator;
  GameType single;
  GameType multi;

  @Before
  public void setUp() {
    creator = new FreecellModelCreator();
    single = GameType.SINGLEMOVE;
    multi = GameType.MULTIMOVE;
  }

  //invalid with null argument
  @Test (expected = IllegalArgumentException.class)
  public void invalidNull() {
    creator.create(null);
  }

  //valid simple
  //checks to see if object created is instance of simplefreecellmodel
  @Test
  public void simpleTest() {
    FreecellModel<ICard> model = creator.create(single);
    assertTrue(model instanceof SimpleFreecellModel);
  }

  //valid multi
  //checks to see if object created is instance of multifreecellmodel
  @Test
  public void multiTest() {
    FreecellModel<ICard> model = creator.create(multi);
    assertTrue(model instanceof MultiFreecellModel);
  }
}
