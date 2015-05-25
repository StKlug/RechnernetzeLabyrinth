package util;

import jaxb.LoginReplyMessageType;

import com.google.inject.Singleton;

/**
 * Holds the value of the current player ID that was assigned to this client.
 * 
 * @author Sebastian Oberhoff
 */
@Singleton
public final class CurrentID {
  
  private int currentID;
  
  public void update(LoginReplyMessageType loginReplyMessageType) {
    this.currentID = loginReplyMessageType.getNewID();
  }
  
  public int getCurrentID() {
    return currentID;
  }
  
}
