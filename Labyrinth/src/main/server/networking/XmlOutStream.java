package networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jaxb.MazeCom;
import tools.Debug;
import tools.DebugLevel;

public class XmlOutStream extends UTFOutputStream{

	private Marshaller marshaller;

	public XmlOutStream(OutputStream out) {
		super(out);
		// Anlegen der JAXB-Komponenten
		try {
			JAXBContext jc = JAXBContext.newInstance(MazeCom.class);
			this.marshaller = jc.createMarshaller();
			this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
		} catch (JAXBException e) {
			Debug.print(Messages.getString("XmlOutStream.ErrorInitialisingJAXBComponent"),DebugLevel.DEFAULT); //$NON-NLS-1$
		}
	}

	/**
	 * Versenden einer XML Nachricht
	 * 
	 * @param mc
	 */
	public void write(MazeCom mc){
		// generierung des fertigen XML
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this.marshaller.marshal(mc, baos);
			Debug.print(Messages.getString("XmlOutStream.Written"),DebugLevel.DEBUG); //$NON-NLS-1$
			Debug.print(new String(baos.toByteArray()),DebugLevel.DEBUG);
			// Versenden des XML
			this.writeUTF8(new String(baos.toByteArray()));
			this.flush();
		} catch (IOException e) {
			Debug.print(Messages.getString("XmlOutStream.errorSendingMessage"),DebugLevel.DEFAULT); //$NON-NLS-1$
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
	}
	

}
