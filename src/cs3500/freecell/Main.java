package cs3500.freecell;

import cs3500.freecell.controller.FreecellController;
import cs3500.freecell.controller.SimpleFreecellController;
import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.FreecellModelCreator;
import cs3500.freecell.model.FreecellModelCreator.GameType;
import cs3500.freecell.model.hw02.ICard;
import java.io.InputStreamReader;

/**
 * Main class containing main method.
 */
public class Main {

  /**
   * Main method running whole program.
   */
  public static void main(String [] args) {
    FreecellModelCreator creator = new FreecellModelCreator();
    GameType single = GameType.SINGLEMOVE;
    GameType multi = GameType.MULTIMOVE;
    FreecellModel<ICard> model = creator.create(multi);
    FreecellController<ICard> controller =
        new SimpleFreecellController(model, new InputStreamReader(System.in), System.out);
    controller.playGame(model.getDeck(), 10, 4, true);
  }
}

