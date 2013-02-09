package de.uniks.jism.event;

import java.util.ArrayList;

import de.uniks.jism.xml.XMLEntity;

public class XSDEntity extends XMLEntity{
	public static final String PROPERTY_CHOICE="choice";
	public static final String PROPERTY_SEQUENCE="sequence";
	public static final String PROPERTY_ATTRIBUTE="attribute";
	public static final String PROPERTY_MINOCCURS="minOccurs";
	public static final String PROPERTY_MAXOCCURS="minOccurs";
	
	private ArrayList<XSDEntity> choice;
	private ArrayList<XSDEntity> sequence;
	private ArrayList<String> attribute;
	private String minOccurs;
	private String maxOccurs;

	public ArrayList<XSDEntity> getChoice() {
		return choice;
	}
	public void setChoice(ArrayList<XSDEntity> choice) {
		this.choice = choice;
	}
	public ArrayList<XSDEntity> getSequence() {
		return sequence;
	}
	public void setSequence(ArrayList<XSDEntity> sequence) {
		this.sequence = sequence;
	}
	public ArrayList<String> getAttribute() {
		return attribute;
	}
	public void setAttribute(ArrayList<String> attribute) {
		this.attribute = attribute;
	}
	public String getMinOccurs() {
		return minOccurs;
	}
	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}
	public String getMaxOccurs() {
		return maxOccurs;
	}
	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
}
