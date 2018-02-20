package de.uniks.networkparser.test.model;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class Plant implements SendableEntityCreator{
	public static final String ID="id";
	public static final String NAME="name";

	private String name;
	private String id;


	@Override
	public String[] getProperties() {
		return new String[] {ID,NAME};
	}
	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof Plant == false) {
			return null;
		}
		Plant plant = (Plant) entity;
		if(ID.equalsIgnoreCase(attribute)) {
			return plant.getId();
		}
		if(NAME.equalsIgnoreCase(attribute)) {
			return plant.getName();
		}
		return null;
	}
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof Plant == false) {
			return false;
		}
		Plant plant = (Plant) entity;
		if(ID.equalsIgnoreCase(attribute)) {
			plant.setId(""+value);
			return true;
		}
		if(NAME.equalsIgnoreCase(attribute)) {
			plant.setName(""+value);
			return true;
		}
		return false;
	}
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Plant();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
