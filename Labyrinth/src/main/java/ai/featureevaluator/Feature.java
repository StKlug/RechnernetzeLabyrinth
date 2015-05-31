package ai.featureevaluator;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

@FunctionalInterface
public interface Feature {
  
  int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID);
}
