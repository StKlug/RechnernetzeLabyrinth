package client;

import jaxb.MazeCom;

/**
 * Responsible for receiving MazeCom objects from the server and handing them to the client.
 * 
 * @author Sebastian Oberhoff
 */
@FunctionalInterface
public interface MazeComUnmarshaller
{
    /**
     * Unmarshalls a single MazeCom. If the server hasn't responded yet, this method will block until
     * a response is forthcoming.
     */
    MazeCom unmarshall();

}
