package competition;

import guiceconfigs.ReconfigurableClientConfig;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import ai.Evaluator;
import client.Client;

import com.google.inject.Guice;
import com.google.inject.Module;

public final class ReconfigurableClient {
  
  private Evaluator delegate;
  
  private final Client client;
  
  public ReconfigurableClient(BlockingQueue<MazeCom> inputQueue, BlockingQueue<MazeCom> outputQueue) {
    Evaluator delegatingEvaluator = (awaitMoveMessageType, possibleBoardTypes, currentID) ->
        delegate.findBest(awaitMoveMessageType, possibleBoardTypes, currentID);
    Module guiceConfig = new ReconfigurableClientConfig(delegatingEvaluator, inputQueue,
        outputQueue);
    client = Guice.createInjector(guiceConfig).getInstance(Client.class);
  }
  
  public void setDelegate(Evaluator delegate) {
    this.delegate = delegate;
  }
  
  public boolean play() {
    return client.play();
  }
}
