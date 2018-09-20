package de.uniks.networkparser;

/*
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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;

public class SimpleGrammar implements Grammar {
	private SimpleList<String> basicProperties =new SimpleList<String>().with(IdMap.ID, IdMap.CLASS, IdMap.SESSION, IdMap.TIMESTAMP);

	@Override
	public BaseItem getProperties(Entity item, MapEntity map, boolean isId) {
		if (isId) {
			if (item.has(JsonTokener.PROPS)) {
				return ((JsonObject)item).getJsonObject(JsonTokener.PROPS);
			}
			return item;
		}
		JsonObject props = new JsonObject();
		for (int i = 0; i < item.size(); i++) {
			if (!IdMap.CLASS.equalsIgnoreCase(item.getKeyByIndex(i))) {
				props.put(item.getKeyByIndex(i), item.getValueByIndex(i));
			}

		}
		return props;
	}

	@Override
	public String getId(Object obj, IdMap map) {
		return null;
	}

	@Override
	public Entity writeBasicValue(Entity entity, String className, String id, String type, IdMap map) {
		if(entity == null || map == null) {
			return null;
		}
		if(type != null && SendableEntityCreator.UPDATE.equalsIgnoreCase(type) == false) {
			entity.put(IdMap.TYPE, type);
		}
		if(basicProperties.contains(IdMap.SESSION)) {
			String session = map.getSession();
			if(session != null) {
				entity.put(IdMap.SESSION, session);
			}
		}
		if(basicProperties.contains(IdMap.CLASS)) {
			entity.withType(className);
		}

		if(id != null) {
			if(basicProperties.contains(IdMap.ID)) {
				entity.put(IdMap.ID, id);
			}
			if(basicProperties.contains(IdMap.TIMESTAMP)) {
				if(map.getTimeStamp() == 0) {
					String ts = null;
					if(id.length()>0) {
						ts = id.substring(1);
					}
					if(EntityUtil.isNumeric(ts)) {
							entity.put(IdMap.TIMESTAMP, ts);
					}
				}
			}
		}
		return entity;
	}

	@Override
	public SendableEntityCreator getCreator(String type, Object item, MapEntity entity, String className) {
//			IdMap map, boolean searchForSuperCreator, String className) {
		if(item == null) {
			return null;
		}
		IdMap map =entity.getMap();
		if(map == null) {
			return null;
		}
		if(Grammar.WRITE.equals(type)) {
			if(className == null) {
				className = item.getClass().getName();
			}
			SendableEntityCreator creator = map.getCreator(className, true, null);

			if(creator != null) {
				return creator;
			}
			if (item instanceof SendableEntityCreator) {
				return (SendableEntityCreator) item;
			}
			return getSuperCreator(map, entity.isSearchForSuperClass(), item);
		}
		if(className == null && item instanceof Entity) {
			Object name = ((Entity)item).getValue(IdMap.CLASS);
			if(name == null) {
				return null;
			}
			className = (String) name;
		}
		SendableEntityCreator creator = map.getCreator(className, false, null);
		if(creator != null) {
			return creator;
		}
		Class<?> clazzName = getClassForName(className);
		return getSuperCreator(map, entity.isSearchForSuperClass(), clazzName);
	}

	public SendableEntityCreator getSuperCreator(IdMap map, boolean searchForSuperCreator, Object modelItem) {
		return null;
	}

	protected Class<?> getClassForName(String name) {
		return null;
	}

	@Override
	public boolean hasValue(Entity item, String property) {
		if(item == null) {
			return false;
		}
		return item.has(property);
	}

	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		return creator.getSendableInstance(prototype);
	}

	@Override
	public String getValue(Entity item, String property) {
		return item.getString(property);
	}

	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		return map.getTokener().encode(entity, map);
	}

	public SimpleGrammar withBasicFeature(String... values) {
		if(values == null) {
			return this;
		}
		for(String item : values) {
			this.basicProperties.add(item);
		}
		return this;
	}

	public SimpleGrammar withoutBasicFeature(String... values) {
		if(values == null) {
			return this;
		}
		for(String item : values) {
			this.basicProperties.without(item);
		}
		return this;
	}

	@Override
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener) {
		return false;
	}
}
