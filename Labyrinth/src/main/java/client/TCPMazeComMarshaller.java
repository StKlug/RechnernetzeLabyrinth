package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jaxb.MazeCom;
import util.Loggers;
import util.UTFOutputStream;

import com.google.inject.Inject;

/**
 * Implementation of the {@link MazeComMarshaller} interface using TCP.
 * 
 * @author Sebastian Oberhoff
 */
public final class TCPMazeComMarshaller implements MazeComMarshaller
{
    private final UTFOutputStream utfOutputStream;

    private final Marshaller marshaller;

    @Inject
    public TCPMazeComMarshaller(Socket server, JAXBContext jaxbContext)
    {
        try
        {
            this.utfOutputStream = new UTFOutputStream(server.getOutputStream());
            this.marshaller = jaxbContext.createMarshaller();
        }
        catch (JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void marshall(MazeCom mazeCom)
    {
        try
        {
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            marshaller.marshal(mazeCom, outputBytes);
            utfOutputStream.writeUTF8(new String(outputBytes.toByteArray(), StandardCharsets.UTF_8));
            Loggers.TCP.debug("Marshalled: " + mazeCom.getMcType());
        }
        catch (JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
