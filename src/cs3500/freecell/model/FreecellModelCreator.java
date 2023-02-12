package cs3500.freecell.model;

import cs3500.freecell.model.hw02.ICard;
import cs3500.freecell.model.hw02.SimpleFreecellModel;

/**
 * Factory class holding a factory method that creates an object of specified model type.
 */
public class FreecellModelCreator {

  /**
   * Represents two freecell game types. Single move: Can only move one card at a time Multimove:
   * Multiple cards can be moved at a time if conditions met
   */
  public enum GameType {
    SINGLEMOVE, MULTIMOVE
  }

  /**
   * Creates different FreecellModel depending on specified GameType parameter.
   *
   * @param type GameType that represents the specific FreecellModel object desired: SINGLEMOVE for
   *             SimpleFreecellModel and MULTIMOVE for MultiFreecellModel
   * @return returns the created model
   * @throws IllegalArgumentException when given GameType is invalid
   */
  public static FreecellModel<ICard> create(GameType type) throws IllegalArgumentException {
    if (type == null) {
      throw new IllegalArgumentException("Given type can't be null.");
    }
    else {
      switch (type) {
        case SINGLEMOVE:
          return new SimpleFreecellModel();
        case MULTIMOVE:
          return new MultiFreecellModel();
        default:
          throw new IllegalArgumentException("Given type is invalid.");
      }
    }
  }

}
