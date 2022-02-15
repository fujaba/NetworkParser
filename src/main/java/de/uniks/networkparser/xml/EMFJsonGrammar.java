package de.uniks.networkparser.xml;

/*
NetworkParser
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
import java.util.Iterator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

/**
 * The Class EMFJsonGrammar.
 *
 * @author Stefan
 */
public class EMFJsonGrammar extends SimpleGrammar {
	
	/** The Constant SRC. */
	public static final String SRC = "@src";
	
	/** The Constant PROP. */
	public static final String PROP = "@prop";
	
	/** The Constant NV. */
	public static final String NV = "@nv";

	/**
	 * Gets the properties.
	 *
	 * @param item the item
	 * @param map the map
	 * @param isId the is id
	 * @return the properties
	 */
	@Override
	public BaseItem getProperties(Entity item, MapEntity map, boolean isId) {
		JsonObject props = new JsonObject();
		if (item != null && item.has(PROP)) {
			String key = item.getString(PROP);
			String value = item.getString(NV);
			SendableEntityCreator creator = getCreator(Grammar.READ, null, map, value);

			if (creator != null) {
				props.put(key, new JsonObject().withValue(SRC, value));
			} else {
				props.put(key, value);
			}
			return props;
		}
		return item;
	}

	/**
	 * Gets the creator.
	 *
	 * @param type the type
	 * @param item the item
	 * @param entity the entity
	 * @param className the class name
	 * @return the creator
	 */
	@Override
	public SendableEntityCreator getCreator(String type, Object item, MapEntity entity, String className) {
		if (Grammar.READ.equals(type) && item instanceof Entity) {
			SendableEntityCreator result = getCreator(type, null, entity, ((Entity) item).getString(SRC));
			if (result != null) {
				return result;
			}
			return super.getCreator(type, item, entity, className);
		}
		if (className == null) {
			return null;
		}
		int pos = className.indexOf("@");
		String clazz = null;
		if (pos > 0) {
			clazz = className.substring(0, pos);
		} else {
			pos = className.lastIndexOf(".");
			if (pos > 0) {
				clazz = className.substring(0, pos);
			}
		}
		if (clazz != null && entity != null) {
			SimpleMap map = entity.getMap();
			for (Iterator<SendableEntityCreator> i = map.iterator(); i.hasNext();) {
				SendableEntityCreator creator = i.next();
				Object sendableInstance = creator.getSendableInstance(true);
				String refClazzName = sendableInstance.getClass().getName();
				if (refClazzName.endsWith("." + clazz)) {
					return creator;
				}
			}
		}
		return super.getCreator(type, item, entity, className);
	}

	/**
	 * Gets the id.
	 *
	 * @param obj the obj
	 * @param map the map
	 * @return the id
	 */
	@Override
	public String getId(Object obj, SimpleMap map) {
		if (obj == null) {
			return null;
		}
		String name = obj.getClass().getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			return name.substring(pos + 1) + IdMap.ENTITYSPLITTER + map.createId(obj, true);
		}
		return name + IdMap.ENTITYSPLITTER + map.createId(obj, true);
	}

	/**
	 * Gets the value.
	 *
	 * @param item the item
	 * @param property the property
	 * @return the value
	 */
	@Override
	public String getValue(Entity item, String property) {
		if (item == null) {
			return null;
		}
		if (IdMap.ID.equals(property)) {
			return item.getString(SRC);
		}
		return item.getString(property);
	}

	/**
	 * Checks for value.
	 *
	 * @param json the json
	 * @param property the property
	 * @return true, if successful
	 */
	@Override
	public boolean hasValue(Entity json, String property) {
		if (property == null) {
			return false;
		}
		if (property.equals(IdMap.ID)) {
			property = SRC;
		}
		return super.hasValue(json, property);
	}

	/**
	 * Write basic value.
	 *
	 * @param entity the entity
	 * @param className the class name
	 * @param id the id
	 * @param type the type
	 * @param map the map
	 * @return the entity
	 */
	@Override
	public Entity writeBasicValue(Entity entity, String className, String id, String type, SimpleMap map) {
		if (id != null && entity != null) {
			entity.put(SRC, id);
		}
		return entity;
	}
}
