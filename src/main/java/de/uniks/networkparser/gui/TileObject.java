package de.uniks.networkparser.gui;

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
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

public class TileObject implements SendableEntityCreatorTag {
	public static final String TAG = "object";
	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_GID = "gid";
	public static final String PROPERTY_WIDTH = "width";
	public static final String PROPERTY_HEIGHT = "height";
	public static final String PROPERTY_SOURCE = "source";

	public static final String PROPERTY_X = "x";
	public static final String PROPERTY_Y = "y";
	public String id;
	public String name;
	public String source;
	public int gid;
	public int x;
	public int y;
	public int width;
	public int height;
	public int count;

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
		return new String[] { PROPERTY_ID, PROPERTY_NAME, PROPERTY_GID, PROPERTY_WIDTH, PROPERTY_HEIGHT, PROPERTY_X,
				PROPERTY_Y };
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof TileObject)) {
			return null;
		}
		TileObject tileObj = (TileObject) entity;
		if (PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return tileObj.id;
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return tileObj.name;
		}
		if (PROPERTY_GID.equalsIgnoreCase(attribute)) {
			return tileObj.gid;
		}
		if (PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			return tileObj.width;
		}
		if (PROPERTY_SOURCE.equalsIgnoreCase(attribute)) {
			return tileObj.source;
		}
		if (PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			return tileObj.height;
		}
		if (PROPERTY_X.equalsIgnoreCase(attribute)) {
			return tileObj.x;
		}
		if (PROPERTY_Y.equalsIgnoreCase(attribute)) {
			return tileObj.y;
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof TileObject)) {
			return false;
		}
		TileObject tileObj = (TileObject) entity;
		if (PROPERTY_ID.equalsIgnoreCase(attribute)) {
			tileObj.id = "" + value;
			return true;
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			tileObj.name = "" + value;
			return true;
		}
		if (PROPERTY_SOURCE.equalsIgnoreCase(attribute)) {
			tileObj.source = "" + value;
			return true;
		}
		if (PROPERTY_GID.equalsIgnoreCase(attribute)) {
			tileObj.gid = Integer.parseInt("" + value);
			return true;
		}
		if (PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			tileObj.width = Integer.parseInt("" + value);
			return true;
		}
		if (PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			tileObj.height = Integer.parseInt("" + value);
			return true;
		}
		if (PROPERTY_X.equalsIgnoreCase(attribute)) {
			tileObj.x = Integer.parseInt("" + value);
			return true;
		}
		if (PROPERTY_Y.equalsIgnoreCase(attribute)) {
			tileObj.y = Integer.parseInt("" + value);
			return true;
		}
		return false;
	}

	public static TileObject create(Entity entity) {
		TileObject element = new TileObject();
		for (String item : element.getProperties()) {
			String value = entity.getString(item);
			if (value != null && value.length() > 0) {
				element.setValue(element, item, value, SendableEntityCreator.NEW);
			}
		}
		return element;
	}
}
