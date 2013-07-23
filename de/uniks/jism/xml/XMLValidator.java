package de.uniks.jism.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;

public class XMLValidator {
	public static XsdValidationLoggingErrorHandler validate(java.net.URL xsdSchema, String xmlDokument)
			throws SAXException, IOException {
		com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory schemaFactory = (XMLSchemaFactory) SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

//		Schema schema = schemaFactory.newSchema(new File(xsdSchema));
		Schema schema = schemaFactory.newSchema(xsdSchema);
		Validator validator = schema.newValidator();
		XsdValidationLoggingErrorHandler errorHandler = new XsdValidationLoggingErrorHandler();
		validator.setErrorHandler(errorHandler);
		validator.validate(new StreamSource(new File(xmlDokument)));
		return errorHandler;
	}
}

