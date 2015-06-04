package competition;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Required to instantiate the server without actually opening a real TCP connection.
 * 
 * @author Sebastian Oberhoff
 */
public final class MockSocket extends Socket
{
    @Override
    public InputStream getInputStream()
    {
        return new InputStream()
        {

            @Override
            public int read()
            {
                return 0;
            }
        };
    }

    @Override
    public OutputStream getOutputStream()
    {
        return new OutputStream()
        {
            @Override
            public void write(int b)
            {
            }
        };
    }
}
