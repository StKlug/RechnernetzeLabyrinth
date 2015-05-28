package client;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBException;

import jaxb.MazeCom;
import jaxb.MazeComType;
import jaxb.MoveMessageType;
import util.CurrentID;
import util.GuiceConfig;
import util.MazeComFactory;
import ai.ArtificialIntelligence;
import ai.StandardBoardEvaluator;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * The central class of the program, containing both the main-method as well as the while-loop that
 * keeps the program running.
 * 
 * @author Sebastian Oberhoff
 */
public final class Client {
  
  private static enum Status {
    RUNNING, LOST, WON;
  }
  
  private Status status = Status.RUNNING;
  
  private final MazeComUnmarshaller mazeComUnmarshaller;
  
  private final MazeComFactory mazeComFactory;
  
  private final ArtificialIntelligence artificialIntelligence;
  
  private final MazeComMarshaller mazeComMarshaller;
  
  private final CurrentID currentID;
  
  @Inject
  public Client(MazeComUnmarshaller mazeComUnmarshaller,
      MazeComFactory mazeComFactory,
      ArtificialIntelligence artificialIntelligence,
      MazeComMarshaller mazeComMarshaller,
      CurrentID currentID) {
    this.mazeComUnmarshaller = mazeComUnmarshaller;
    this.mazeComFactory = mazeComFactory;
    this.artificialIntelligence = artificialIntelligence;
    this.mazeComMarshaller = mazeComMarshaller;
    this.currentID = currentID;
  }
  
  /**
   * Logs into the server, then keeps unmarshalling new incoming messages.
   * 
   * @return true if the client won the game
   */
  public boolean play() {
    mazeComMarshaller.marshall(mazeComFactory.createLoginMessage("Ameisen"));
    while (status == Status.RUNNING) {
      MazeCom mazeCom = mazeComUnmarshaller.unmarshall();
      dispatch(mazeCom);
    }
    return status == Status.WON;
  }
  
  /**
   * Dispatches an incoming MazeCom to the appropriate Module and marshalls any responses.
   * 
   * @param mazeCom the MazeCom sent from the server
   */
  private void dispatch(MazeCom mazeCom) {
    if (mazeCom.getMcType() == MazeComType.WIN) {
      if (mazeCom.getWinMessage().getWinner().getId() == currentID.getCurrentID()) {
        status = Status.WON;
      }
      else {
        status = Status.LOST;
      }
    }
    else if (mazeCom.getMcType() == MazeComType.DISCONNECT) {
      status = Status.LOST;
    }
    else if (mazeCom.getMcType() == MazeComType.LOGINREPLY) {
      currentID.update(mazeCom.getLoginReplyMessage());
    }
    else if (mazeCom.getMcType() == MazeComType.AWAITMOVE) {
      MoveMessageType moveMessageType = artificialIntelligence.computeMove(mazeCom
          .getAwaitMoveMessage());
      mazeComMarshaller.marshall(mazeComFactory.createMoveMessage(moveMessageType));
    }
  }
  
  /**
   * Central entry point to the application. Performs Guice dependency injection bootstrapping, then
   * sends the program into the run-loop.
   */
  public static void main(String[] args) throws JAXBException, UnknownHostException, IOException {
    Injector injector = Guice.createInjector(new GuiceConfig(new StandardBoardEvaluator()));
    injector.getInstance(Client.class).play();
  }
}
