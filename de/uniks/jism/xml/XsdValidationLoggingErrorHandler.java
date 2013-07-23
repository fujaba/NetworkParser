package de.uniks.jism.xml;

import java.util.ArrayList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XsdValidationLoggingErrorHandler implements ErrorHandler {
	private boolean isValid=true;
	private ArrayList<String> warnings=new ArrayList<String>();
	private ArrayList<String> errors=new ArrayList<String>();
	public void warning(SAXParseException ex) throws SAXException {
		isValid = false;
		warnings.add("Warnung: " + ex.getMessage());
	}

	public void error(SAXParseException ex) throws SAXException {
		isValid = false;
		errors.add("Fehler: " + ex.getMessage());
	}

	public void fatalError(SAXParseException ex) throws SAXException {
		isValid = false;
		errors.add("Fataler Fehler: " + ex.getMessage());
	}

	public boolean isValid() {
		return isValid;
	}
	
	public ArrayList<String> getErrors(){
		return errors;
	}
	public String getErrorText(){
		StringBuilder sb=new StringBuilder();
		for(String item : errors){
			sb.append(item+"\n");
		}
		sb.append("ERRORS: "+errors.size());
		return sb.toString();
	}
	public ArrayList<String> getWarnings(){
		return warnings;
	}
}
