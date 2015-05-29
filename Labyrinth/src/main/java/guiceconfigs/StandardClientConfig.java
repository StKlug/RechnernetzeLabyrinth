package guiceconfigs;

import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import jaxb.MazeCom;
import ai.BoardEvaluator;
import ai.StandardBoardEvaluator;
import client.MazeComMarshaller;
import client.MazeComUnmarshaller;
import client.TCPMazeComMarshaller;
import client.TCPMazeComUnmarshaller;

import com.google.inject.AbstractModule;

/**
 * The standard Guice configuration for running a single Client in connection with a full fledged
 * server via TCP.
 * 
 * @author Sebastian Oberhoff
 */
public class StandardClientConfig extends AbstractModule {
  
  private final Socket server;
  
  private final JAXBContext jaxbContext;
  
  public StandardClientConfig() {
    try {
      server = new Socket((String) null, 5123); // see config.Settings.PORT
      jaxbContext = JAXBContext.newInstance(MazeCom.class);
    }
    catch (IOException | JAXBException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void configure() {
    bind(BoardEvaluator.class).to(StandardBoardEvaluator.class);
    bind(MazeComUnmarshaller.class).to(TCPMazeComUnmarshaller.class);
    bind(MazeComMarshaller.class).to(TCPMazeComMarshaller.class);
    bind(Socket.class).toInstance(server);
    bind(JAXBContext.class).toInstance(jaxbContext);
  }
}
