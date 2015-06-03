package util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Central location for all Loggers.
 * 
 * @author Sebastian Oberhoff
 */
public final class Loggers {
  
  private Loggers() {
    // noninstantiable
  }
  
  public static final Logger AI = Logger.getLogger("ai");
  
  public static final Logger TCP = Logger.getLogger("tcp");
  
  public static final Logger COMPETITION = Logger.getLogger("competition");
  
  public static final Logger EVOLUTION = Logger.getLogger("evolution");
  
  public static final Logger FEATURE = Logger.getLogger("boardnorm");
  
  static {
    BasicConfigurator.configure();
    AI.setLevel(Level.INFO);
    TCP.setLevel(Level.INFO);
    COMPETITION.setLevel(Level.INFO);
    EVOLUTION.setLevel(Level.INFO);
    FEATURE.setLevel(Level.INFO);
  }
  
}
