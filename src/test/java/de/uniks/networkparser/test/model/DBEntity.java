package de.uniks.networkparser.test.model;

import java.util.Date;

public class DBEntity {
	public static final String PROPERTY_TEXT="text";
	public static final String PROPERTY_CHECK="check";
	public static final String PROPERTY_COMBO="combo";
	public static final String PROPERTY_PASSWORD="password";
	public static final String PROPERTY_DATE="date";
	public static final String PROPERTY_NUMBER="NUMBER";
	public static final String PROPERTY_SPINNER="SPINNER";
	public static final String PROPERTY_PERSON="PERSON";
	private String text;
	private boolean check;
	private String combo;
	private String password;
	private Date date;
	private double number;
	private int spinner;
	private Person person;

	public String getText() {
		return text;
	}

	public DBEntity withText(String text) {
		this.text = text;
		return this;
	}

	public boolean isCheck() {
		return check;
	}

	public DBEntity withCheck(boolean check) {
		this.check = check;
		return this;
	}

	public String getCombo() {
		return combo;
	}

	public DBEntity withCombo(String combo) {
		this.combo = combo;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public DBEntity withPassword(String password) {
		this.password = password;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public DBEntity withDate(Date date) {
		this.date = date;
		return this;
	}

	public double getNumber() {
		return number;
	}

	public DBEntity withNumber(double number) {
		this.number = number;
		return this;
	}

	public int getSpinner() {
		return spinner;
	}

	public DBEntity withSpinner(int spinner) {
		this.spinner = spinner;
		return this;
	}

	public Person getPerson() {
		return person;
	}

	public DBEntity withPerson(Person person) {
		this.person = person;
		return this;
	}
	public Object get(String attribute) {
		if(PROPERTY_TEXT.equalsIgnoreCase(attribute)){
			return getText();
		}
		if(PROPERTY_CHECK.equalsIgnoreCase(attribute)){
			return isCheck();
		}
		if(PROPERTY_COMBO.equalsIgnoreCase(attribute)){
			return getCombo();
		}
		if(PROPERTY_PASSWORD.equalsIgnoreCase(attribute)){
			return getPassword();
		}
		if(PROPERTY_DATE.equalsIgnoreCase(attribute)){
			return getDate();
		}
		if(PROPERTY_NUMBER.equalsIgnoreCase(attribute)){
			return getNumber();
		}
		if(PROPERTY_SPINNER.equalsIgnoreCase(attribute)){
			return getSpinner();
		}
		if(PROPERTY_PERSON.equalsIgnoreCase(attribute)){
			return getPerson();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if(PROPERTY_TEXT.equalsIgnoreCase(attribute)){
			withText("" +value);
			return true;
		}
		if(PROPERTY_CHECK.equalsIgnoreCase(attribute)){
			withCheck((Boolean)value);
			return true;
		}
		if(PROPERTY_COMBO.equalsIgnoreCase(attribute)){
			withCombo("" +value);
			return true;
		}
		if(PROPERTY_PASSWORD.equalsIgnoreCase(attribute)){
			withPassword("" +value);
			return true;
		}
		if(PROPERTY_DATE.equalsIgnoreCase(attribute)){
			withDate((Date)value);
			return true;
		}
		if(PROPERTY_NUMBER.equalsIgnoreCase(attribute)){
			withNumber((Double)value);
			return true;
		}
		if(PROPERTY_SPINNER.equalsIgnoreCase(attribute)){
			withSpinner((Integer)value);
			return true;
		}
		if(PROPERTY_PERSON.equalsIgnoreCase(attribute)){
			withPerson((Person)value);
			return true;
		}
		return false;
	}
}
