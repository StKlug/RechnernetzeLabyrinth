package ai.featureevaluator;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;
import util.Loggers;

public final class IsStandingOnTreasure implements Feature {
  
  private final DistanceToTreasure distanceToTreasure = new DistanceToTreasure();
  
  @Override
  public int measure(AwaitMoveMessageType awaitMoveMessageType,
      BoardType boardType, CurrentID currentID) {
    boolean isStandingOnTreasure = distanceToTreasure.measure(awaitMoveMessageType, boardType,
        currentID) == 0;
    Loggers.FEATURE.debug("IsStandingOnTreasure: " + isStandingOnTreasure);
    return isStandingOnTreasure ? 1 : 0;
  }
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
