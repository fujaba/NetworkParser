package de.uniks.networkparser.json;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class JsonObjectCreator implements SendableEntityCreator, SendableEntityCreatorNoIndex {
	private final static String VALUE = "VALUE";
	private final String[] properties = new String[] { VALUE };

	@Override
	public String[] getProperties() {
		return this.properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new JsonObject();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (VALUE.equalsIgnoreCase(attribute)) {
			return entity.toString();
		}
		if (entity == null) {
			return null;
		}
		return ((JsonObject) entity).getValue(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (SendableEntityCreator.REMOVE_YOU.equalsIgnoreCase(type)) {
			return false;
		}
		JsonObject json = (JsonObject) entity;
		if (VALUE.equals(attribute)) {
			json.withValue((String) value);
		} else {
			json.withKeyValue(attribute, value);
		}
		return true;
	}
}
