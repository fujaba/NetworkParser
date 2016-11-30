package de.uniks.networkparser.ext.generic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class SimpleObject implements SendableEntityCreator {
    private String className;
    private String id;

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    private SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();
    private String[] properties;
    private boolean dirty = false;

    public static SimpleObject create(JsonObject json) {
	SimpleObject result = new SimpleObject();
	
	result.setId(json.getString(IdMap.ID));
	Object className = json.get(IdMap.CLASS);
	if(className != null) {
	    result.setClassName(""+className);
	}
	Object props = json.get(JsonTokener.PROPS);
	if (props != null && props instanceof JsonObject) {
	    JsonObject jsonProps = (JsonObject) props;
	    for (int i = 0; i < jsonProps.size(); i++) {
		String key = jsonProps.getKeyByIndex(i);
		Object value = jsonProps.getValueByIndex(i);
		result.addValue(key, value);
	    }
	}
	return result;
    }

    void addValue(String key, Object value) {
	this.values.add(key, value);
	this.dirty = true;
    }

    public Object getValue(String key) {
	return values.get(key);
    }

    public Object getValue() {
	if (values.size() == 1) {
	    return values.get(values.getValueByIndex(0));
	}
	return null;
    }

    @Override
    public String[] getProperties() {
	if (this.dirty) {
	    this.properties = this.values.keySet().toArray(new String[this.values.size()]);
	    this.dirty = false;
	}
	return this.properties;
    }

    @Override
    public Object getValue(Object entity, String attribute) {
	if (entity instanceof SimpleObject) {
	    return ((SimpleObject) entity).getValue(attribute);
	}
	return null;
    }

    public boolean setValue(String attribute, Object value) {
	int pos = this.values.indexOf(attribute);
	if (pos < 0) {
	    this.addValue(attribute, value);
	} else {
	    this.values.setValue(pos, value);
	}
	return true;
    }

    @Override
    public boolean setValue(Object entity, String attribute, Object value, String type) {
	if (entity instanceof SimpleObject) {
	    ((SimpleObject) entity).setValue(attribute, value);
	    return true;
	}
	return false;
    }

    @Override
    public Object getSendableInstance(boolean prototyp) {
	return new SimpleObject();
    }
    @Override
    public String toString() {
	StringBuilder sb=new StringBuilder();
	sb.append('[');
	if(id != null) {
	    sb.append(id);
	    if(className != null) {
		sb.append(':');
		sb.append(className);
	    }
	} else if(className != null) {
	    sb.append(className);
	}
	if(values.size()>0) {
	    sb.append('|');
	    for (int i = 0; i < values.size(); i++) {
		String key = values.getKeyByIndex(i);
		Object value = values.getValueByIndex(i);
		if(i>0) {
		    sb.append(',');
		}
		sb.append(key);
		sb.append('=');
		sb.append(value);
		
	    }
	}
	sb.append(']');
	return sb.toString();
    }
}
