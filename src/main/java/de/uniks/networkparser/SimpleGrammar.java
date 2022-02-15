package de.uniks.networkparser;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class SimpleGrammar.
 *
 * @author Stefan
 */
public class SimpleGrammar implements Grammar {
	private boolean flatFormat = false;
	private ObjectCondition condition;
	private SimpleList<String> basicProperties = new SimpleList<String>().with(SimpleMap.ID, SimpleMap.CLASS, SimpleMap.SESSION, SimpleMap.TIMESTAMP);

	/**
	 * With flat format.
	 *
	 * @param value the value
	 * @return the simple grammar
	 */
	public SimpleGrammar withFlatFormat(boolean value) {
		this.flatFormat = value;
		return this;
	}

	/**
	 * Checks if is flat format.
	 *
	 * @return true, if is flat format
	 */
	public boolean isFlatFormat() {
		return flatFormat;
	}

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
		if (item == null) {
			return null;
		}
		if (isId) {
			if (item.has(JsonTokener.PROPS)) {
				return ((JsonObject) item).getJsonObject(JsonTokener.PROPS);
			}
			return item;
		}
		JsonObject props = new JsonObject();
		for (int i = 0; i < item.size(); i++) {
			if (!SimpleMap.CLASS.equalsIgnoreCase(item.getKeyByIndex(i))) {
				props.put(item.getKeyByIndex(i), item.getValueByIndex(i));
			}

		}
		return props;
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
		if (condition != null) {
			/* Please set Type for new Id */
			SimpleEvent evt = new SimpleEvent(map, "id", null, obj);
			if (condition.update(evt)) {
				return evt.getType();
			}
		}
		return null;
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
		if (entity == null || map == null) {
			return null;
		}
		if (this.flatFormat) {
			if (type != null && !SendableEntityCreator.UPDATE.equalsIgnoreCase(type)) {
				entity.put("." + SimpleMap.TYPE, type);
			}
			if (basicProperties.contains(SimpleMap.SESSION)) {
				String session = map.getSession();
				if (session != null) {
					entity.put("." + SimpleMap.SESSION, session);
				}
			}
			if (basicProperties.contains(SimpleMap.CLASS)) {
				entity.put("." + SimpleMap.CLASS, className);
			}
			if (id != null) {
				if (basicProperties.contains(SimpleMap.ID)) {
					entity.put("." + SimpleMap.ID, id);
				}
				if (basicProperties.contains(SimpleMap.TIMESTAMP)) {
					if (map.getTimeStamp() == 0) {
						String ts = null;
						if (id.length() > 0) {
							ts = id.substring(1);
						}
						if (StringUtil.isNumeric(ts)) {
							entity.put("." + SimpleMap.TIMESTAMP, ts);
						}
					}
				}
			}
			return entity;
		}
		if (type != null && !SendableEntityCreator.UPDATE.equalsIgnoreCase(type)) {
			entity.put(SimpleMap.TYPE, type);
		}
		if (basicProperties.contains(SimpleMap.SESSION)) {
			String session = map.getSession();
			if (session != null) {
				entity.put(SimpleMap.SESSION, session);
			}
		}
		if (basicProperties.contains(SimpleMap.CLASS)) {
			entity.withType(className);
		}

		if (id != null) {
			if (basicProperties.contains(SimpleMap.ID)) {
				entity.put(SimpleMap.ID, id);
			}
			if (basicProperties.contains(SimpleMap.TIMESTAMP)) {
				if (map.getTimeStamp() == 0) {
					String ts = null;
					if (id.length() > 0) {
						ts = id.substring(1);
					}
					if (StringUtil.isNumeric(ts)) {
						entity.put(SimpleMap.TIMESTAMP, ts);
					}
				}
			}
		}
		return entity;
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
		if (item == null) {
			return null;
		}
		SimpleMap map = entity.getMap();
		if (map == null) {
			return null;
		}
		if (Grammar.WRITE.equals(type)) {
			if (className == null) {
				className = item.getClass().getName();
			}
			SendableEntityCreator creator = map.getCreator(className, true, true, null);

			if (creator != null) {
				return creator;
			}
			if (item instanceof SendableEntityCreator) {
				return (SendableEntityCreator) item;
			}
			return getSuperCreator(map, entity.isSearchForSuperClass(), item);
		}
		if (className == null && item instanceof Entity) {
			Object name = ((Entity) item).getValue(SimpleMap.CLASS);
			if (name == null) {
				return null;
			}
			className = (String) name;
		}
		SendableEntityCreator creator = map.getCreator(className, false, true, null);
		if (creator != null) {
			return creator;
		}
		Class<?> clazzName = getClassForName(className);
		return getSuperCreator(map, entity.isSearchForSuperClass(), clazzName);
	}

	/**
	 * Gets the super creator.
	 *
	 * @param map the map
	 * @param searchForSuperCreator the search for super creator
	 * @param modelItem the model item
	 * @return the super creator
	 */
	public SendableEntityCreator getSuperCreator(SimpleMap map, boolean searchForSuperCreator, Object modelItem) {
		return null;
	}

	protected Class<?> getClassForName(String name) {
		return null;
	}

	/**
	 * Checks for value.
	 *
	 * @param item the item
	 * @param property the property
	 * @return true, if successful
	 */
	@Override
	public boolean hasValue(Entity item, String property) {
		if (item == null) {
			return false;
		}
		return item.has(property);
	}

	/**
	 * Gets the new entity.
	 *
	 * @param creator the creator
	 * @param className the class name
	 * @param prototype the prototype
	 * @return the new entity
	 */
	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		if (creator == null) {
			return null;
		}
		return creator.getSendableInstance(prototype);
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
		return item.getString(property);
	}

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @param map the map
	 * @return the base item
	 */
	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		if (map != null) {
			Tokener tokener = map.getTokener();
			if (tokener != null) {
				return map.getTokener().encode(entity, map);
			}
		}
		return null;
	}

	/**
	 * With basic feature.
	 *
	 * @param values the values
	 * @return the simple grammar
	 */
	public SimpleGrammar withBasicFeature(String... values) {
		if (values == null) {
			return this;
		}
		for (String item : values) {
			this.basicProperties.add(item);
		}
		return this;
	}

	/**
	 * Without basic feature.
	 *
	 * @param values the values
	 * @return the simple grammar
	 */
	public SimpleGrammar withoutBasicFeature(String... values) {
		if (values == null) {
			return this;
		}
		for (String item : values) {
			this.basicProperties.without(item);
		}
		return this;
	}

	/**
	 * Write value.
	 *
	 * @param parent the parent
	 * @param property the property
	 * @param value the value
	 * @param map the map
	 * @param tokener the tokener
	 * @return true, if successful
	 */
	@Override
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener) {
		return false;
	}
}
