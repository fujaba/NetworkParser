package de.uniks.networkparser.xml;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

public class XMLValidator {
	// FIXME public static XsdValidationLoggingErrorHandler
	// validate(java.net.URL xsdSchema, String xmlDokument)
	// throws SAXException, IOException {
	// com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory
	// schemaFactory = (XMLSchemaFactory) SchemaFactory
	// .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	//
	// // Schema schema = schemaFactory.newSchema(new File(xsdSchema));
	// Schema schema = schemaFactory.newSchema(xsdSchema);
	// Validator validator = schema.newValidator();
	// XsdValidationLoggingErrorHandler errorHandler = new
	// XsdValidationLoggingErrorHandler();
	// validator.setErrorHandler(errorHandler);
	// validator.validate(new StreamSource(new File(xmlDokument)));
	// return errorHandler;
	// }
}
