package networking;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jaxb.MazeCom;

import org.xml.sax.SAXException;

import tools.Debug;
import tools.DebugLevel;

public class XmlOutStream extends UTFOutputStream {

	private Marshaller marshaller;

	@SuppressWarnings("nls")
	public XmlOutStream(OutputStream out) {
		super(out);
		// Anlegen der JAXB-Komponenten
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(MazeCom.class);
			this.marshaller = jaxbContext.createMarshaller();
			this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				File xsdFile = new File(getClass().getResource(
						"/XSD/mazeCom.xsd").getPath());
				Schema schema = schemaFactory.newSchema(xsdFile);
				//this.marshaller.setSchema(schema);
			} catch (SAXException e) {
				e.printStackTrace();
				Debug.print(
						"[Warning] InStream: XML Schema failed => XML Validation disabled",
						DebugLevel.DEFAULT);
			}
		} catch (JAXBException e) {
			Debug.print(
					Messages.getString("XmlOutStream.ErrorInitialisingJAXBComponent"), DebugLevel.DEFAULT); //$NON-NLS-1$
		}
	}

	/**
	 * Versenden einer XML Nachricht
	 * 
	 * @param mc
	 */
	public void write(MazeCom mc) {
		// generierung des fertigen XML
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this.marshaller.marshal(mc, baos);
			Debug.print(
					Messages.getString("XmlOutStream.Written"), DebugLevel.DEBUG); //$NON-NLS-1$
			Debug.print(new String(baos.toByteArray()), DebugLevel.DEBUG);
			// Versenden des XML
			this.writeUTF8(new String(baos.toByteArray()));
			this.flush();
		} catch (IOException e) {
			Debug.print(
					Messages.getString("XmlOutStream.errorSendingMessage"), DebugLevel.DEFAULT); //$NON-NLS-1$
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
	}

}
