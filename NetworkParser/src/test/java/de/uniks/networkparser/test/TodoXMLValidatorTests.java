package de.uniks.networkparser.test;
//
//import static org.junit.Assert.assertNotNull;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.xml.XMLConstants;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//import javax.xml.validation.Validator;
//
//import org.junit.Test;
//import org.xml.sax.ErrorHandler;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;
//
//import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
//
//import de.uniks.networkparser.xml.XMLSimpleIdMap;
//import de.uniks.networkparser.xml.XMLTokener;
//import de.uniks.networkparser.xml.creator.XSDEntityCreator;
//
public class TodoXMLValidatorTests extends IOClasses{
//
//	@Test
//	public void testValitdator() throws IOException {
//		StringBuffer stringBuffer = readFile("test/de/uniks/networkparser/test/resource/codebook.xsd");
//		XMLSimpleIdMap map = new XMLSimpleIdMap();
////		map.addCreator(new XSDEntityCreator("qb"));
//		String value = stringBuffer.toString();
//		XSDEntityCreator factory = new XSDEntityCreator().withNameSpace("cb");
//		Object decode = map.decode(new XMLTokener().withText(value), factory);
//		assertNotNull(decode);
//	}
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 * @throws SAXException
//	 */
//	public static void main(String[] args) throws SAXException, IOException {
//		args = new String[] {
//				"E:/Uni/workspace/uni/ExampleDocuments/codebook.xsd",
//				"E:/Uni/workspace/uni/ExampleDocuments/codebook_demo.xml" };
//
//		if (args.length != 2) {
//			System.out.println("Bitte XSD-Schema und XML-Dokument angeben.");
//			return;
//		}
//		System.out.println(args[0] + " + " + args[1]);
////		XMLValidator.validate(args[0], args[1]);
//	}
//
//	public static boolean validate(java.net.URL xsdSchema, String xmlDokument)
//			throws SAXException, IOException {
////		com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory schemaFactory = (XMLSchemaFactory) SchemaFactory
////				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
////		Schema schema = schemaFactory.newSchema(new File(xsdSchema));
//		Schema schema = schemaFactory.newSchema(xsdSchema);
//		Validator validator = schema.newValidator();
//		XsdValidationLoggingErrorHandler errorHandler = new XsdValidationLoggingErrorHandler();
//		validator.setErrorHandler(errorHandler);
//		validator.validate(new StreamSource(new File(xmlDokument)));
//		return errorHandler.isValid();
//	}
//}
//
//class XsdValidationLoggingErrorHandler implements ErrorHandler {
//	private boolean isValid=true;
//	public void warning(SAXParseException ex) throws SAXException {
//		isValid = false;
//		System.out.println("Warnung: " + ex.getMessage());
//	}
//
//	public void error(SAXParseException ex) throws SAXException {
//		isValid = false;
//		System.out.println("Fehler: " + ex.getMessage());
//	}
//
//	public void fatalError(SAXParseException ex) throws SAXException {
//		isValid = false;
//		System.out.println("Fataler Fehler: " + ex.getMessage());
//	}
//
//	public boolean isValid() {
//		return isValid;
//	}
}
