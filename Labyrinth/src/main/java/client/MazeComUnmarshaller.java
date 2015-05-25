package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jaxb.MazeCom;
import util.UTFInputStream;

import com.google.inject.Inject;

/**
 * Listens to the connection to the server and unmarshalls any incoming MazeComs.
 * 
 * @author Sebastian Oberhoff
 */
public final class MazeComUnmarshaller {
  
  private final Unmarshaller unmarshaller;
  
  private final UTFInputStream utfInputStream;
  
  @Inject
  public MazeComUnmarshaller(Socket server, JAXBContext jaxbContext) {
    try {
      this.utfInputStream = new UTFInputStream(server.getInputStream());
      this.unmarshaller = jaxbContext.createUnmarshaller();
    }
    catch (JAXBException | IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Unmarshalls a single MazeCom. If the server hasn't responded yet, this method will block until
   * a response is forthcoming.
   */
  public MazeCom unmarshall() {
    try {
      byte[] inputBytes = utfInputStream.readUTF8().getBytes();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
      MazeCom mazeCom = (MazeCom) unmarshaller.unmarshal(inputStream);
      log(mazeCom);
      return mazeCom;
    }
    catch (JAXBException | IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void log(MazeCom mazeCom) {
    System.out.println("Unmarshalled: " + mazeCom.getMcType());
  }
}
