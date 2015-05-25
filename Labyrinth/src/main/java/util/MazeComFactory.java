package util;

import jaxb.LoginMessageType;
import jaxb.MazeCom;
import jaxb.MazeComType;
import jaxb.MoveMessageType;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Assists the construction of new MazeComs by simplifying the construction as well as performing
 * runtime error checks. Also inserts the current ID into the response MazeComs.
 * 
 * @author Sebastian Oberhoff
 */
@Singleton
public final class MazeComFactory {
  
  private final CurrentID currentID;
  
  @Inject
  public MazeComFactory(CurrentID currentID) {
    this.currentID = currentID;
  }
  
  /**
   * @param name the name that should represent the client. (Should be something like
   * "Amazonen Ameisen" in our case)
   */
  public MazeCom createLoginMessage(String name) {
    MazeCom mazeCom = new MazeCom();
    mazeCom.setMcType(MazeComType.LOGIN);
    LoginMessageType loginMessageType = new LoginMessageType();
    loginMessageType.setName(name);
    mazeCom.setLoginMessage(loginMessageType);
    return mazeCom;
  }
  
  public MazeCom createMoveMessage(MoveMessageType moveMessageType) {
    MazeCom mazeCom = new MazeCom();
    mazeCom.setId(currentID.getCurrentID());
    mazeCom.setMcType(MazeComType.MOVE);
    mazeCom.setMoveMessage(moveMessageType);
    return mazeCom;
  }
  
}
