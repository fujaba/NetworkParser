package de.uniks.networkparser;

import java.util.List;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class TextItems.
 *
 * @author Stefan
 */
public class TextItems extends SimpleKeyValueList<String, Object>
		implements SendableEntityCreator, LocalisationInterface {
	
	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";
	private LocalisationInterface customLanguage = null;
	private ObjectCondition listener = null;
	private boolean defaultLabel = true;
	private boolean autoCreate = true;
	private boolean templateReplace = true;
	private boolean replaceEmptyString = true;

	/** The Constant DEFAULT. */
	public static final TextItems DEFAULT = new TextItems()
			/* Month */
			.with("JANUARY", "January").with("FEBRUARY", "February").with("MARCH", "March").with("APRIL", "April")
			.with("MAY", "May").with("JUNE", "June").with("JULY", "July").with("AUGUST", "August")
			.with("SEPTEMBER", "September").with("OCTOBER", "October").with("NOVEMBER", "November")
			.with("DECEMBER", "December")
			/* Weekday */
			.with("SUNDAY", "Sunday").with("MONDAY", "Monday").with("TUESDAY", "Tuesday").with("WEDNESDAY", "Wednesday")
			.with("THURSDAY", "Thursday").with("FRIDAY", "Friday").with("SATURDAY", "Saturday")
			/* ANY */
			.with("LOAD", "Load").with("SAVE", "Save").with("SAVEAS", "Save As").with("RELOAD", "Reload")
			.with("SEARCH", "Search").with("COLUMNS", "Columns").with("CUT", "Cut").with("RELOAD", "Reload")
			.with("COPY", "Copy").with("PASTE", "Paste").with("SELECTALL", "Select all")
			.with("TEXTSTATUSLINE", "Pos: %POS% / %LEN%  Line: %LINE% / %LINECOUNT%  ");

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof TextItems) {
			return ((TextItems) entity).get(attribute);
		}
		return null;
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	@Override
	public String get(Object key) {
		if (key == null || !(key instanceof String)) {
			return null;
		}
		String k = (String) key;
		if (!isCaseSensitive()) {
			k = k.toLowerCase();
		}
		Object object = super.get(k);
		if (object != null && object instanceof String) {
			return (String) object;
		}
		String newKey = k.replace(".", ":");
		object = super.get(newKey);
		if (object != null && object instanceof String) {
			return (String) object;
		}
		/* Not Found Check if Notification Listener */
		if (listener != null) {
			SimpleEvent simpleEvent = new SimpleEvent(this, "" + key, null, "Key not found: " + key).withType("ERROR");
			listener.update(simpleEvent);
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof TextItems)) {
			return false;
		}
		TextItems items = (TextItems) entity;
		return items.add(attribute, value);
	}

	/**
	 * Gets the text.
	 *
	 * @param label the label
	 * @param model the model
	 * @param gui the gui
	 * @return the text
	 */
	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		if (label == null) {
			return null;
		}
		String text = null;
		if (!containsKey(label)) {
			CharacterBuffer buffer = new CharacterBuffer();
			buffer.with(label);
			for (int i = 0; i < buffer.length(); i++) {
				if (buffer.charAt(i) == '.') {
					buffer.withStartPosition(i + 1);
					String testField = buffer.toString();
					if (containsKey(testField)) {
						return getText(testField, model, gui);
					}
				}
			}
		}
		if (customLanguage != null) {
			text = customLanguage.getText(label, model, gui);
			if (text != null) {
				return text;
			}
		}
		text = getLabelString(label + "." + System.getProperty("java.class.version", ""));
		if (text != null) {
			return text;

		}
		text = getLabelString(label);
		if (text != null) {
			return text;
		}
		if (!this.defaultLabel) {
			return null;
		}
		return label.toString();
	}

	/**
	 * Gets the label string.
	 *
	 * @param label the label
	 * @return the label string
	 */
	public String getLabelString(CharSequence label) {
		if (containsKey(label)) {
			return get(label);
		}
		return null;
	}

	/**
	 * Gets the label value.
	 *
	 * @param label the label
	 * @return the label value
	 */
	public Object getLabelValue(CharSequence label) {
		if (containsKey(label)) {
			return super.get(label);
		}
		return null;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_VALUE };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TextItems();
	}

	/**
	 * Sets the custom language.
	 *
	 * @param value the new custom language
	 */
	public void setCustomLanguage(LocalisationInterface value) {
		this.customLanguage = value;
	}

	/**
	 * Put.
	 *
	 * @param label the label
	 * @param object the object
	 * @return the string
	 */
	@Override
	public String put(String label, Object object) {
		if (this.customLanguage != null) {
			return this.customLanguage.put(label, object);
		}
		if (object == null) {
			return null;
		}
		/* Add String */
		if (object instanceof List<?>) {
			/* Additional List of same Key */
		}
		if (this.add(label, object)) {
			return object.toString();
		}
		return null;
	}

	/**
	 * Checks if is default label.
	 *
	 * @return true, if is default label
	 */
	public boolean isDefaultLabel() {
		return defaultLabel;
	}

	/**
	 * With default label.
	 *
	 * @param value the value
	 * @return the text items
	 */
	public TextItems withDefaultLabel(boolean value) {
		this.defaultLabel = value;
		return this;
	}

	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @param ignoreCase the ignore case
	 * @return the text items
	 */
	public static TextItems create(Entity value, boolean ignoreCase) {
		TextItems textItems = new TextItems();
		if (value != null) {
			textItems.parseJsonObject(value, null, ignoreCase);
		}
		return textItems;
	}

	private boolean parseJsonObject(Entity item, String parent, boolean ignoreCase) {
		if (item == null) {
			return false;
		}
		for (int i = 0; i < item.size(); i++) {
			String key = item.getKeyByIndex(i);
			String fullKey;
			if (parent != null) {
				fullKey = parent + ":" + key;
			} else {
				fullKey = key;
			}
			Object value = item.getValueByIndex(i);
			if (value instanceof Entity) {
				parseJsonObject((Entity) value, fullKey, ignoreCase);
			} else if (value instanceof List<?>) {
				List<?> list = (List<?>) value;
				SimpleList<Object> newValue = new SimpleList<Object>();
				for (Object child : list) {
					if (child instanceof String) {
						newValue.add(child);
					} else if (child instanceof JsonObject) {
						newValue.add(child);
					}
				}
				if (newValue.size() > 0) {
					this.put(fullKey.toLowerCase(), newValue);
				}
			} else if (value instanceof String) {
				if (ignoreCase) {
					this.put(fullKey.toLowerCase(), value);
				} else {
					this.put(fullKey, value);
				}
			} else if (value instanceof Boolean) {
				if (ignoreCase) {
					this.put(fullKey.toLowerCase(), value);
				} else {
					this.put(fullKey, value);
				}
			}
		}
		return true;
	}

	/**
	 * Checks if is auto create.
	 *
	 * @return true, if is auto create
	 */
	public boolean isAutoCreate() {
		return autoCreate;
	}

	/**
	 * With auto create.
	 *
	 * @param autoCreate the auto create
	 * @return the text items
	 */
	public TextItems withAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
		return this;
	}

	/**
	 * Checks if is template replace.
	 *
	 * @return true, if is template replace
	 */
	public boolean isTemplateReplace() {
		return templateReplace;
	}

	/**
	 * With template replace.
	 *
	 * @param templateReplace the template replace
	 * @return the text items
	 */
	public TextItems withTemplateReplace(boolean templateReplace) {
		this.templateReplace = templateReplace;
		return this;
	}

	/**
	 * Checks if is replace empty string.
	 *
	 * @return true, if is replace empty string
	 */
	public boolean isReplaceEmptyString() {
		return replaceEmptyString;
	}

	/**
	 * With replace empty string.
	 *
	 * @param replaceEmptyString the replace empty string
	 * @return the text items
	 */
	public TextItems withReplaceEmptyString(boolean replaceEmptyString) {
		this.replaceEmptyString = replaceEmptyString;
		return this;
	}

	/**
	 * With listener.
	 *
	 * @param value the value
	 * @return the text items
	 */
	public TextItems withListener(ObjectCondition value) {
		this.listener = value;
		return this;
	}
}
