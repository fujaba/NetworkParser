package de.uniks.networkparser.test.model;

import java.util.LinkedHashSet;

public class ListItem {
	public static final String PROPERTY_ENTITY="item";
	private LinkedHashSet<Entity> child= new LinkedHashSet<Entity>();
	public LinkedHashSet<Entity> getChild() {
		return child;
	}
	public void setChild(LinkedHashSet<Entity> child) {
		this.child = child;
	}
	public void addChild(Entity item){
		child.add(item);
	}
	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_ENTITY)) {
			addChild((Entity) value);
			return true;
		}
		return false;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ENTITY)) {
			return getChild();
		}
		return null;
	}
	public Entity getChildByName(String name){
		for (Entity item : child){
			if(name.equals(item.getValue())){
				return item;
			}
		}
		return null;
	}
}
