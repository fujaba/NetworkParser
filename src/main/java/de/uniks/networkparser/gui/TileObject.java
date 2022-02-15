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

/**
 * The Class TileObject.
 *
 * @author Stefan
 */
public class TileObject implements SendableEntityCreatorTag {
	
	/** The Constant TAG. */
	public static final String TAG = "object";
	
	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "id";
	
	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";
	
	/** The Constant PROPERTY_GID. */
	public static final String PROPERTY_GID = "gid";
	
	/** The Constant PROPERTY_WIDTH. */
	public static final String PROPERTY_WIDTH = "width";
	
	/** The Constant PROPERTY_HEIGHT. */
	public static final String PROPERTY_HEIGHT = "height";
	
	/** The Constant PROPERTY_SOURCE. */
	public static final String PROPERTY_SOURCE = "source";

	/** The Constant PROPERTY_X. */
	public static final String PROPERTY_X = "x";
	
	/** The Constant PROPERTY_Y. */
	public static final String PROPERTY_Y = "y";
	
	/** The id. */
	public String id;
	
	/** The name. */
	public String name;
	
	/** The source. */
	public String source;
	
	/** The gid. */
	public int gid;
	
	/** The x. */
	public int x;
	
	/** The y. */
	public int y;
	
	/** The width. */
	public int width;
	
	/** The height. */
	public int height;
	
	/** The count. */
	public int count;

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TileObject();
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return TAG;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_ID, PROPERTY_NAME, PROPERTY_GID, PROPERTY_WIDTH, PROPERTY_HEIGHT, PROPERTY_X,
				PROPERTY_Y };
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
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

	/**
	 * Creates the.
	 *
	 * @param entity the entity
	 * @return the tile object
	 */
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
