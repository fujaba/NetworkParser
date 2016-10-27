package de.uniks.networkparser.gui;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

public class TileObject implements SendableEntityCreatorTag {
	public static final String TAG="object";
	public static final String PROPERTY_ID="id";
	public static final String PROPERTY_NAME="name";
	public static final String PROPERTY_GID="gid";
	public static final String PROPERTY_WIDTH="width";
	public static final String PROPERTY_HEIGHT="height";
	public static final String PROPERTY_X="x";
	public static final String PROPERTY_Y="y";
	public String id;
	public String name;
	public int gid;
	public int x;
	public int y;
	public int width;
	public int height;

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TileObject();
	}
	@Override
	public String getTag() {
		return TAG;
	}
	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_ID, PROPERTY_NAME, PROPERTY_GID, PROPERTY_WIDTH, PROPERTY_HEIGHT, PROPERTY_X, PROPERTY_Y};
	}
	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TileObject == false) {
			return null;
		}
		TileObject tileObj = (TileObject) entity;
		if(PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return tileObj.id;
		}
		if(PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return tileObj.name;
		}
		if(PROPERTY_GID.equalsIgnoreCase(attribute)) {
			return tileObj.gid;
		}
		if(PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			return tileObj.width;
		}
		if(PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			return tileObj.height;
		}
		if(PROPERTY_X.equalsIgnoreCase(attribute)) {
			return tileObj.x;
		}
		if(PROPERTY_Y.equalsIgnoreCase(attribute)) {
			return tileObj.y;
		}
		return null;
	}
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof TileObject == false) {
			return false;
		}
		TileObject tileObj = (TileObject) entity;
		if(PROPERTY_ID.equalsIgnoreCase(attribute)) {
			tileObj.id = ""+value;
			return true;
		}
		if(PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			tileObj.name = ""+value;
			return true;
		}
		if(PROPERTY_GID.equalsIgnoreCase(attribute)) {
			tileObj.gid = Integer.valueOf(""+value);
			return true;
		}
		if(PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			tileObj.width = Integer.valueOf(""+value);
			return true;
		}
		if(PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			tileObj.height = Integer.valueOf(""+value);
			return true;
		}
		if(PROPERTY_X.equalsIgnoreCase(attribute)) {
			tileObj.x = Integer.valueOf(""+value);
			return true;
		}
		if(PROPERTY_Y.equalsIgnoreCase(attribute)) {
			tileObj.y = Integer.valueOf(""+value);
			return true;
		}
		return false;
	}
	public static TileObject create(Entity entity) {
		TileObject element = new TileObject();
		for(String item : element.getProperties()) {
			String value = entity.getString(item);
			if(value != null && value.length()>0) {
				element.setValue(element, item, value, IdMap.NEW);
			}
		}
		return element;
	}
}
