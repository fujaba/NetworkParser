package de.uniks.networkparser.xml;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
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
	 * @param value
	 *            Elements of Choice.
	 */
	public void setChoice(ArrayList<XSDEntity> value) {
		this.choice = value;
	}

	/** @return The Sequence of Elements. */
	public ArrayList<XSDEntity> getSequence() {
		return sequence;
	}

	/**
	 * @param values
	 *            Set the Sequence.
	 */
	public void setSequence(ArrayList<XSDEntity> values) {
		this.sequence = values;
	}

	/** @return All Attributes. */
	public ArrayList<String> getAttribute() {
		return attribute;
	}

	/**
	 * @param values
	 *            Set All Attributes.
	 */
	public void setAttribute(ArrayList<String> values) {
		this.attribute = values;
	}

	/** @return The Minimum of Elements. */
	public String getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @param value
	 *            The Minimum of Elements.
	 */
	public void setMinOccurs(String value) {
		this.minOccurs = value;
	}

	/** @return The Maximum of Elements. */
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @param value
	 *            the Maximum of Elements.
	 */
	public void setMaxOccurs(String value) {
		this.maxOccurs = value;
	}
}
