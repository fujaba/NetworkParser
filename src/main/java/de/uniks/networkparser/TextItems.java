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

public class TextItems extends SimpleKeyValueList<String, Object>
		implements SendableEntityCreator, LocalisationInterface {
	public static final String PROPERTY_VALUE = "value";
	private LocalisationInterface customLanguage = null;
	private ObjectCondition listener = null;
	private boolean defaultLabel = true;
	private boolean autoCreate = true;
	private boolean templateReplace = true;
	private boolean replaceEmptyString = true;

	public static final TextItems DEFAULT = new TextItems()
			// Month
			.with("JANUARY", "January").with("FEBRUARY", "February").with("MARCH", "March").with("APRIL", "April")
			.with("MAY", "May").with("JUNE", "June").with("JULY", "July").with("AUGUST", "August")
			.with("SEPTEMBER", "September").with("OCTOBER", "October").with("NOVEMBER", "November")
			.with("DECEMBER", "December")
			// Weekday
			.with("SUNDAY", "Sunday").with("MONDAY", "Monday").with("TUESDAY", "Tuesday").with("WEDNESDAY", "Wednesday")
			.with("THURSDAY", "Thursday").with("FRIDAY", "Friday").with("SATURDAY", "Saturday")
			// ANY
			.with("LOAD", "Load").with("SAVE", "Save").with("SAVEAS", "Save As").with("RELOAD", "Reload")
			.with("SEARCH", "Search").with("COLUMNS", "Columns").with("CUT", "Cut").with("RELOAD", "Reload")
			.with("COPY", "Copy").with("PASTE", "Paste").with("SELECTALL", "Select all")
			.with("TEXTSTATUSLINE", "Pos: %POS% / %LEN%  Line: %LINE% / %LINECOUNT%  ");

	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof TextItems) {
			return ((TextItems) entity).get(attribute);
		}
		return null;
	}

	@Override
	public String get(Object key) {
		if (key == null || key instanceof String == false) {
			return null;
		}
		String k = (String) key;
		if (isCaseSensitive() == false) {
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
		// Not Found Check if Notification Listener
		if (listener != null) {
			SimpleEvent simpleEvent = new SimpleEvent(this, "" + key, null, "Key not found: " + key).withType("ERROR");
			listener.update(simpleEvent);
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof TextItems == false) {
			return false;
		}
		TextItems items = (TextItems) entity;
		return items.add(attribute, value);
	}

	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		if (label == null) {
			return null;
		}
		String text = null;
		if (containsKey(label) == false) {
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
		if (this.defaultLabel == false) {
			return null;
		}
		return label.toString();
	}

	public String getLabelString(CharSequence label) {
		if (containsKey(label)) {
			Object value = get(label);
			if (value instanceof String) {
				return (String) value;
			}
		}
		return null;
	}

	public Object getLabelValue(CharSequence label) {
		if (containsKey(label)) {
			return super.get(label);
		}
		return null;
	}

	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_VALUE };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TextItems();
	}

	public void setCustomLanguage(LocalisationInterface value) {
		this.customLanguage = value;
	}

	@Override
	public String put(String label, Object object) {
		if (this.customLanguage != null) {
			return this.customLanguage.put(label, object);
		}
		if (object == null) {
			return null;
		}
		// Add String
		if (object instanceof List<?>) {
			// Additional List of same Key

		}
		if (this.add(label, object)) {
			return object.toString();
		}
		return null;
	}

	public boolean isDefaultLabel() {
		return defaultLabel;
	}

	public TextItems withDefaultLabel(boolean value) {
		this.defaultLabel = value;
		return this;
	}

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

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public TextItems withAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
		return this;
	}

	public boolean isTemplateReplace() {
		return templateReplace;
	}

	public TextItems withTemplateReplace(boolean templateReplace) {
		this.templateReplace = templateReplace;
		return this;
	}

	public boolean isReplaceEmptyString() {
		return replaceEmptyString;
	}

	public TextItems withReplaceEmptyString(boolean replaceEmptyString) {
		this.replaceEmptyString = replaceEmptyString;
		return this;
	}

	public TextItems withListener(ObjectCondition value) {
		this.listener = value;
		return this;
	}
}
