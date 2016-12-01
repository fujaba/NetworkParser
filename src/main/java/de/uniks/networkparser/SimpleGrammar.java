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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;

public class SimpleGrammar implements Grammar{
	@Override
	public BaseItem getProperties(Entity item, IdMap map, Filter filter, boolean isId, String type) {
		if (isId) {
			if (item.has(JsonTokener.PROPS)) {
				return ((JsonObject)item).getJsonObject(JsonTokener.PROPS);
			}
		} else {
			JsonObject props = new JsonObject();
			for (int i = 0; i < item.size(); i++) {
				if (!IdMap.CLASS.equalsIgnoreCase(item.getKeyByIndex(i))) {
					props.put(item.getKeyByIndex(i), item.getValue(i));
				}

			}
			return props;
		}
		return null;
	}

	@Override
	public String getId(Object obj, IdMapCounter counter) {
		return null;
	}

	@Override
	public CharacterBuffer getPrefixProperties(SendableEntityCreator creator, Tokener format, boolean isId) {
		if (creator instanceof SendableEntityCreatorNoIndex || isId == false || format instanceof JsonTokener == false) {
			return new CharacterBuffer();
		}
		return new CharacterBuffer().with(IdMap.ENTITYSPLITTER).with(JsonTokener.PROPS).with(IdMap.ENTITYSPLITTER);
	}

	@Override
	public Entity writeBasicValue(Entity entity, BaseItem parent, String className, String id, MapEntity map) {
		entity.setType(className);
		if(id != null) {
			entity.put(IdMap.ID, id);
		}
		return entity;
	}

	@Override
	public SendableEntityCreator getCreator(String type, Object item, IdMap map, boolean searchForSuperCreator, String className) {
		if(Grammar.WRITE.equals(type)) {
			SendableEntityCreator creator = map.getCreator(className, true);

			if(creator != null) {
				return creator;
			}
			if (item instanceof SendableEntityCreator) {
				return (SendableEntityCreator) item;
			}
			return getSuperCreator(map, searchForSuperCreator, item); 
		}
		if(className == null && item instanceof Entity) {
			Object name = ((Entity)item).getValue(IdMap.CLASS);
			if(name == null) {
				return null;
			}
			className = (String) name;
		}
		SendableEntityCreator creator = map.getCreator((String) className, true);
		if(creator != null) {
			return creator;
		}
		Class<?> clazzName = getClassForName((String) className);
		return getSuperCreator(map, searchForSuperCreator, clazzName);
	}

	public SendableEntityCreator getSuperCreator(IdMap map, boolean searchForSuperCreator, Object modelItem) {
		return null;
	}

	protected Class<?> getClassForName(String name) {
		return null;
	}

	@Override
	public boolean hasValue(Entity item, String property) {
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
	public BaseItem encode(Object entity, MapEntity map, Tokener tokener) {
		return tokener.encode(entity, map);
	}

	@Override
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener) {
		return false;
	}
}
