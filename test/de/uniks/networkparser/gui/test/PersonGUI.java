package de.uniks.networkparser.gui.test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PersonGUI {
	public static final String PROPERTY_FIRSTNAME="firstname";
	public static final String PROPERTY_LASTNAME="lastname";
	public static final String PROPERTY_EMAIL="email";
	public static final String PROPERTY_DISTANCE="distance";
    private StringProperty firstName;

    private StringProperty lastName;

    private StringProperty email;
    
    private StringProperty distanceValue;
	private int distance;
	private String caption;


    public PersonGUI(){
    	this.firstName = new SimpleStringProperty("");

        this.lastName = new SimpleStringProperty("");

        this.email = new SimpleStringProperty("");
        
        this.distanceValue = new SimpleStringProperty("");
    	
    }
    public PersonGUI(String fName, String lName, String email, int distance) {

        this.firstName = new SimpleStringProperty(fName);

        this.lastName = new SimpleStringProperty(lName);

        this.email = new SimpleStringProperty(email);
        this.distance = distance;
        this.distanceValue = new SimpleStringProperty(distance+" min");
    }

     

    public StringProperty firstnameProperty() { return firstName; }

    public StringProperty lastnameProperty() { return lastName; }

    public StringProperty emailProperty() { return email; }
    
    public StringProperty distanceProperty() { return distanceValue; }

    public Integer getDistance() {
		return distance;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
}
