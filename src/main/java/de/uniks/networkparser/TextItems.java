package de.uniks.networkparser;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.util.TreeMap;

import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TextItems implements SendableEntityCreator, LocalisationInterface {
	public static final String PROPERTY_VALUE = "value";
	private TreeMap<String, String> values = new TreeMap<String, String>();
	private LocalisationInterface customLanguage = null;

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((TextItems) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((TextItems) entity).set(attribute, value);
	}

	public Object get(String attribute) {
		if (values.containsKey(attribute)) {
			return values.get(attribute);
		}
		return attribute;
	}

	@Override
	public String getText(String label, Object model, Object gui) {
		String text = null;
		if (customLanguage != null) {
			text = customLanguage.getText(label, model, gui);
			if (text != null) {
				return text;
			}
		}
		text = getLabelValue(label + "."
				+ System.getProperty("java.class.version", ""));
		if (text != null) {
			return text;

		}
		text = getLabelValue(label);
		if (text != null) {
			return text;
		}
		return label;
	}

	private String getLabelValue(String label) {
		if (values.containsKey(label)) {
			return values.get(label);
		}
		return null;
	}

	public void addTextLabel(String key, String value) {
		values.put(key, value);
	}

	public boolean set(String attribute, Object value) {
		return false;
	}

	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_VALUE };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TextItems();
	}

	public void setCustomLanguage(LocalisationInterface value) {
		this.customLanguage = value;
	}
}
