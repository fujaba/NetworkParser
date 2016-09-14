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
 * @author Stefan The XSD Entity
 */

public class XSDEntity extends XMLEntity {
	/** Constant of Choice. */
	public static final String PROPERTY_CHOICE = "choice";
	/** Constant of Sequence. */
	public static final String PROPERTY_SEQUENCE = "sequence";
	/** Constant of Attributes. */
	public static final String PROPERTY_ATTRIBUTE = "attribute";
	/** Constant of Minimum Elements. */
	public static final String PROPERTY_MINOCCURS = "minOccurs";
	/** Constant of Maximum Elements. */
	public static final String PROPERTY_MAXOCCURS = "minOccurs";

	/** Elements of Choice. */
	private ArrayList<XSDEntity> choice;
	/** Elements of Sequence. */
	private ArrayList<XSDEntity> sequence;
	/** All Attributes. */
	private ArrayList<String> attribute;
	/** The Minimum of Elements. */
	private String minOccurs;
	/** The Maximum of Elements. */
	private String maxOccurs;

	/** @return The Choice of Elements. */
	public ArrayList<XSDEntity> getChoice() {
		return choice;
	}

	/**
	 * Set a ChoiceList
	 *
	 * @param value Elements of Choice.
	 */
	public void setChoice(ArrayList<XSDEntity> value) {
		this.choice = value;
	}

	/** @return The Sequence of Elements. */
	public ArrayList<XSDEntity> getSequence() {
		return sequence;
	}

	/**
	 * Set a Sequence Validator
	 *
	 * @param values	Set the Sequence.
	 */
	public void setSequence(ArrayList<XSDEntity> values) {
		this.sequence = values;
	}

	/** @return All Attributes. */
	public ArrayList<String> getAttribute() {
		return attribute;
	}

	/**
	 * Set a List of Attributes
	 *
	 * @param values	Set All Attributes.
	 */
	public void setAttribute(ArrayList<String> values) {
		this.attribute = values;
	}

	/** @return The Minimum of Elements. */
	public String getMinOccurs() {
		return minOccurs;
	}

	/**
	 * Set the Mimimum for XSD Entity
	 *
	 * @param value		The Minimum of Elements.
	 */
	public void setMinOccurs(String value) {
		this.minOccurs = value;
	}

	/** @return The Maximum of Elements. */
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * Set the Maximum of Occurs
	 *
	 * @param value		the Maximum of Elements.
	 */
	public void setMaxOccurs(String value) {
		this.maxOccurs = value;
	}
}
