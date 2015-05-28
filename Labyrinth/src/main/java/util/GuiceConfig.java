package util;

import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import jaxb.MazeCom;
import ai.BoardEvaluator;

import com.google.inject.AbstractModule;

/**
 * Dependency injection configuration for Guice
 * 
 * @author Sebastian Oberhoff
 */
public final class GuiceConfig extends AbstractModule {
  
  private final Socket server;
  
  private final JAXBContext jaxbContext;
  
  private final BoardEvaluator boardEvaluator;
  
  public GuiceConfig(BoardEvaluator boardEvaluator) {
    this.boardEvaluator = boardEvaluator;
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
    bind(BoardEvaluator.class).toInstance(boardEvaluator);
    bind(Socket.class).toInstance(server);
    bind(JAXBContext.class).toInstance(jaxbContext);
  }
}
