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
import java.util.ArrayList;

/**
 * XsdValidationLoggingErrorHandler XSD-Validation Logging Handler
 * @author Stefan XSD Validation Error Class.
 */
public class XsdValidationLoggingErrorHandler {
	/* TODO VALIDATOR */
	/** Variable of Document valid. */
	private boolean isValid = true;
	/** Variable of all Warnings. */
	private ArrayList<String> warnings = new ArrayList<String>();
	/** Variable of all Errors. */
	private ArrayList<String> errors = new ArrayList<String>();

/*	public void warning(SAXParseException ex) throws SAXException {
		isValid = false;
		warnings.add("Warnung: " + ex.getMessage());
	}
	@Override
	public void error(SAXParseException ex) throws SAXException {
		isValid = false;
		errors.add("Fehler: " + ex.getMessage());
	}
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		isValid = false;
		errors.add("Fataler Fehler: " + ex.getMessage());
	}
*/

	/**
	 * Switch for Valid Document
	 * 
	 * @return is Document is Valid.
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Get all Errors
	 * 
	 * @return All Errors.
	 */
	public ArrayList<String> getErrors() {
		return errors;
	}

	/**
	 * Get all Errors as Text
	 * 
	 * @return the ErrorText.
	 */
	public String getErrorText() {
		StringBuilder sb = new StringBuilder();
		for (String item : errors) {
			sb.append(item + "\n");
		}
		sb.append("ERRORS: " + errors.size());
		return sb.toString();
	}

	/**
	 * Get all Warnings
	 * 
	 * @return List of Warnings.
	 */
	public ArrayList<String> getWarnings() {
		return warnings;
	}
}
