package de.uniks.networkparser.test.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FullAssocs {
	
	public static final String PROPERTY_PERSONS = "persons";
	public static final String PROPERTY_PASSWORDS = "passwords";
	public static final String PROPERTY_ANSWER = "answer";
	public static final String PROPERTY_FULLMAP = "fullmap";
	public static final String PROPERTY_MESSAGE = "message";

	private List<String> persons= new ArrayList<String>();
	private TreeMap<String, String> passwords= new TreeMap<String, String>();
	private LinkedHashMap<FullAssocs, FullAssocs> fullMap= new LinkedHashMap<FullAssocs, FullAssocs>();
	private int answer;
	private StringMessage message;

	public List<String> getPersons() {
		return persons;
	}
	public void setPersons(List<String> persons) {
		this.persons = persons;
	}
	
	public String getPassword(String user) {
		return passwords.get(user);
	}
	
	public Map<String, String> getPasswords() {
		return passwords;
	}
	public void setPasswords(Map<String, String> passwords) {
		this.passwords = (TreeMap<String, String>) passwords;
	}
	
	public void addPassword(String user, String password){
		this.passwords.put(user, password);
	}
	
	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_PERSONS)) {
			return getPersons();
		} else if (attribute.equalsIgnoreCase(PROPERTY_PASSWORDS)) {
			return getPasswords();
		} else if (attribute.equalsIgnoreCase(PROPERTY_ANSWER)) {
			return getAnswer();
		}else if(PROPERTY_FULLMAP.equalsIgnoreCase(attribute)){
			return getFullMap();
		}else if(PROPERTY_MESSAGE.equalsIgnoreCase(attribute)){
			return getMessage();
		}
		return null;
	}
	
	public void addAssoc(FullAssocs child){
		fullMap.put(child, child);
	}
	
	public void addPerson(String name){
		persons.add(name);
	}

	@SuppressWarnings("unchecked")
	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_PERSONS)) {
			if(value instanceof ArrayList<?>){
				setPersons((List<String>) value);
			}else if(value instanceof String){
				addPerson((String) value);
			}
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_PASSWORDS)) {
			if(value instanceof Map<?,?>){
				setPasswords((Map<String, String>) value);
			}else if(value instanceof Entry<?,?>){
				Entry<Object, Object> entry=(Entry<Object, Object>) value;
				passwords.put("" +entry.getKey(), "" +entry.getValue());
			}
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_ANSWER)) {
			setAnswer((Integer)value);
			return true;
		}else if(PROPERTY_FULLMAP.equalsIgnoreCase(attribute)){
			if(value instanceof Map<?,?>){
				setFullMap((LinkedHashMap<FullAssocs, FullAssocs>) value);
			}else if(value instanceof Entry<?,?>){
				Entry<Object, Object> entry=(Entry<Object, Object>) value;
				fullMap.put((FullAssocs)entry.getKey(), (FullAssocs)entry.getValue());
			}
			return true;
		}else if(PROPERTY_MESSAGE.equalsIgnoreCase(attribute)){
			setMessage((StringMessage)value);
			return true;
		}
		return false;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	public LinkedHashMap<FullAssocs, FullAssocs> getFullMap() {
		return fullMap;
	}
	public void setFullMap(LinkedHashMap<FullAssocs, FullAssocs> value) {
		this.fullMap = value;
	}
	public StringMessage getMessage() {
		return message;
	}
	public void setMessage(StringMessage message) {
		this.message = message;
	}
}
