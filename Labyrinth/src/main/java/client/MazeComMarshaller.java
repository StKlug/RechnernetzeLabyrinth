package client;

import jaxb.MazeCom;

@FunctionalInterface
public interface MazeComMarshaller {
  
  void marshall(MazeCom mazeCom);
}
