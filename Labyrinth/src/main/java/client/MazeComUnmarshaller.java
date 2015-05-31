package client;

import jaxb.MazeCom;

@FunctionalInterface
public interface MazeComUnmarshaller {
  
  /**
   * Unmarshalls a single MazeCom. If the server hasn't responded yet, this method will block until
   * a response is forthcoming.
   */
  MazeCom unmarshall();
  
}
