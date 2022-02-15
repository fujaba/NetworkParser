package de.uniks.networkparser.ext;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.Set;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class StartData.
 *
 * @author Stefan
 */
public class StartData implements SendableEntityCreatorNoIndex {
	
	/** The Constant PROPERTY_EDITABLE. */
	public static final String PROPERTY_EDITABLE = "editable";
	private static StartData instance;
	private String[] attribute = null;
	private SimpleList<StartElement> properties = new SimpleList<StartElement>();
	private boolean editable = true;
	private String fileName = "config.json";

	/**
	 * Sets the file name.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean setFileName(String value) {
		if (value != null && !value.equals(instance().fileName)) {
			instance().fileName = value;
			return true;
		}
		return false;
	}

	/**
	 * Instance.
	 *
	 * @return the start data
	 */
	public static StartData instance() {
		if(instance == null) {
			instance = new StartData();
		}
		return instance;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param label the label
	 * @param description the description
	 * @param values the values
	 * @return true, if successful
	 */
	public static boolean addParameter(String key, String label, String description, Object values) {
		if (PROPERTY_EDITABLE.equals(key)) {
			return false;
		}
		StartElement startElement = new StartElement();
		startElement.withDescription(description);
		startElement.withLabel(label);
		startElement.withKey(key);
		startElement.withDefaultValues(values);
		boolean success = instance().properties.add(startElement);
		if (success) {
			instance().attribute = null;
		}
		return success;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean addParameter(String key, Object value) {
		if (PROPERTY_EDITABLE.equals(key)) {
			if (value instanceof Boolean) {
				instance().editable = (Boolean) value;
				return true;
			}
			return false;
		}
		StartElement startElement = new StartElement();
		startElement.withKey(key);
		startElement.withValue(value);
		boolean success = instance().properties.add(startElement);
		if (success) {
			instance().attribute = null;
		}
		return success;
	}

	/**
	 * Checks for.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public static boolean has(String key) {
		if (key == null) {
			return false;
		}
		for (StartElement item : instance().properties) {
			if (key.equalsIgnoreCase(item.getKey())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	public static boolean isEditable() {
		return instance().editable;
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String getString(String key) {
		if (key == null) {
			return "";
		}
		for (StartElement item : instance().properties) {
			if (key.equalsIgnoreCase(item.getKey())) {
				Object result = item.getValue();
				if (result instanceof String) {
					return (String) result;
				}
				return "" + result;
			}
		}
		return "";
	}

	/**
	 * Gets the integer.
	 *
	 * @param key the key
	 * @return the integer
	 */
	public static Integer getInteger(String key) {
		if (key == null) {
			return null;
		}
		for (StartElement item : instance().properties) {
			if (key.equalsIgnoreCase(item.getKey())) {
				Object result = item.getValue();
				if (result instanceof Integer) {
					return (Integer) result;
				}
				return Integer.valueOf("" + result);
			}
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean setValue(String key, Object value) {
		if (PROPERTY_EDITABLE.equals(key)) {
			if (value instanceof Boolean) {
				instance().editable = (Boolean) value;
				return true;
			}
			return false;
		}
		for (StartElement item : instance().properties) {
			if (key.equalsIgnoreCase(item.getKey())) {
				item.withValue(value);
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		if (instance.attribute == null) {
			instance.attribute = new String[instance().properties.size() + 1]; 
			attribute[0] = PROPERTY_EDITABLE;
			int i = 1;
			for (StartElement item : properties) {
				instance.attribute[i++] = item.getKey();
			}
		}
		return attribute;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param reference the reference
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new StartData();
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public static SimpleList<StartElement> getElements() {
		return instance().properties;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attrName the attr name
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attrName) {
		if (attrName == null || !(entity instanceof StartData)) {
			return null;
		}
		if (PROPERTY_EDITABLE.equalsIgnoreCase(attrName)) {
			return isEditable();
		}
		SimpleList<StartElement> props = StartData.getElements();
		for (StartElement item : props) {
			if (attrName.equalsIgnoreCase(item.getKey())) {
				return item.getValue();
			}
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attrName the attr name
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attrName, Object value, String type) {
		if (attrName == null || !(entity instanceof StartData)) {
			return false;
		}
		if (PROPERTY_EDITABLE.equalsIgnoreCase(attrName)) {
			instance().editable = (Boolean) value;
			return true;
		}
		for (StartElement item : properties) {
			if (attrName.equalsIgnoreCase(item.getKey())) {
				item.withValue(value);
				return true;
			}
		}
		/* Not Found Create it */
		return StartData.addParameter(attrName, value);
	}

	/**
	 * Save.
	 *
	 * @return true, if successful
	 */
	public static boolean save() {
		StartData startData = StartData.instance();
		if (startData.size() < 1) {
			/* its only the editableFlag not a value */
			return false;
		}
		IdMap map = new IdMap();
		map.with(startData);
		JsonObject config = map.toJsonObject(startData, Filter.createFull());
		return FileBuffer.writeFile(instance().fileName, config.toString(2)) >= 0;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return instance().properties.size();
	}

	/**
	 * Load.
	 *
	 * @return true, if successful
	 */
	public static boolean load() {
		IdMap map = new IdMap();
		map.with(StartData.instance());
		CharacterBuffer readFile = FileBuffer.readFile(instance().fileName);
		if (readFile.length() < 1) {
			return true;
		}
		JsonObject json = new JsonObject().withValue(readFile);
		/* Merge Properties from File and Properties from StartData */
		Set<String> keySet = json.keySet();
		for (String key : keySet) {
			if (BaseItem.CLASS.equals(key)) {
				continue;
			}
			if (!has(key)) {
				addParameter(key, null);
			}
		}
		return map.decode(json, StartData.instance(), null) != null;
	}

	/**
	 * Checks if is auto start.
	 *
	 * @return true, if is auto start
	 */
	public static boolean isAutoStart() {
		return !StartData.isEditable();
	}
}
