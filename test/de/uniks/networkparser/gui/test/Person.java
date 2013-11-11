package de.uniks.networkparser.gui.test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
	public static final String PROPERTY_FIRSTNAME="firstname";
	public static final String PROPERTY_LASTNAME="lastname";
	public static final String PROPERTY_EMAIL="email";
    private StringProperty firstName;

    private StringProperty lastName;

    private StringProperty email;


    public Person(){
    	
    }
    public Person(String fName, String lName, String email) {

        this.firstName = new SimpleStringProperty(fName);

        this.lastName = new SimpleStringProperty(lName);

        this.email = new SimpleStringProperty(email);
    }

     

    public StringProperty firstnameProperty() { return firstName; }

    public StringProperty lastnameProperty() { return lastName; }

    public StringProperty emailProperty() { return email; }

}
