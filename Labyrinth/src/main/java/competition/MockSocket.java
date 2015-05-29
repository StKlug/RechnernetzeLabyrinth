package competition;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MockSocket extends Socket {
  
  @Override
  public InputStream getInputStream() {
    return new InputStream() {
      
      @Override
      public int read() {
        return 0;
      }
    };
  }
  
  @Override
  public OutputStream getOutputStream() {
    return new OutputStream() {
      
      @Override
      public void write(int b) {
      }
    };
  }
}
