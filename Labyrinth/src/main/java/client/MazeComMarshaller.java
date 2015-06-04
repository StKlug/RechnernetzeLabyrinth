package client;

import jaxb.MazeCom;

/**
 * Responsible for sending MazeCom objects from the client to the server.
 * 
 * @author Sebastian Oberhoff
 */
@FunctionalInterface
public interface MazeComMarshaller
{
    /**
     * Marshalls a single MazeCom. This method doesn't block until the server accepts the message, but
     * rather returns immediately after placing the MazeCom into some kind of shared data structure
     * (TCP stream or BlockingQueue).
     */
    void marshall(MazeCom mazeCom);
}
