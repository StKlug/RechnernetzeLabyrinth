package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import networking.UTFInputStream;
import jaxb.MazeCom;
import util.Loggers;

import com.google.inject.Inject;

/**
 * Implementation of the {@link MazeComUnmarshaller} interface using TCP.
 * 
 * @author Sebastian Oberhoff
 */
public final class TCPMazeComUnmarshaller implements MazeComUnmarshaller
{
    private final Unmarshaller unmarshaller;

    private final UTFInputStream utfInputStream;

    @Inject
    public TCPMazeComUnmarshaller(Socket server, JAXBContext jaxbContext)
    {
        try
        {
            this.utfInputStream = new UTFInputStream(server.getInputStream());
            this.unmarshaller = jaxbContext.createUnmarshaller();
        }
        catch (JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MazeCom unmarshall()
    {
        try
        {
            byte[] inputBytes = utfInputStream.readUTF8().getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
            MazeCom mazeCom = (MazeCom) unmarshaller.unmarshal(inputStream);
            Loggers.TCP.debug("Unmarshalled: " + mazeCom.getMcType());
            return mazeCom;
        }
        catch (JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
