package de.uniks.networkparser.test.javafx;

import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PersonGUI {
	public static final String PROPERTY_FIRSTNAME="firstname";
	public static final String PROPERTY_LASTNAME="lastname";
	public static final String PROPERTY_EMAIL="email";
	public static final String PROPERTY_DISTANCE="distance";
	public static final String PROPERTY_CREATE="created";
	private StringProperty firstName;

	private StringProperty lastName;

	private StringProperty email;

	private SimpleIntegerProperty distanceValue;

	private Date date;

	private String caption;

	public PersonGUI(){
		this.firstName = new SimpleStringProperty("");

		this.lastName = new SimpleStringProperty("");

		this.email = new SimpleStringProperty("");

		this.distanceValue = new SimpleIntegerProperty();

	}
	public PersonGUI(String fName, String lName, String email, int distance) {

		this.firstName = new SimpleStringProperty(fName);

		this.lastName = new SimpleStringProperty(lName);

		this.email = new SimpleStringProperty(email);
		this.distanceValue = new SimpleIntegerProperty(distance);
	}

	public PersonGUI(String fName, String lName, String email) {
		this.firstName = new SimpleStringProperty(fName);

		this.lastName = new SimpleStringProperty(lName);

		this.email = new SimpleStringProperty(email);
	}
	public StringProperty firstnameProperty() { return firstName; }

	public StringProperty lastnameProperty() { return lastName; }

	public StringProperty emailProperty() { return email; }

	public SimpleIntegerProperty distanceProperty() { return distanceValue; }

	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public PersonGUI withName(String value) {
		this.lastName.setValue(value);
		return this;
	}
	public Integer getDistance() {
		return distanceValue.getValue();
	}
	public Date getDate() {
		return date;
	}
	public PersonGUI withDate(Date date) {
		this.date = date;
		return this;
	}
}
