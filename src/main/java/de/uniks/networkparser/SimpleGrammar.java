package de.uniks.networkparser;

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
	public void writeBasicValue(Entity entity, String className, String id) {
		entity.setType(className);
		if(id != null) {
			entity.put(IdMap.ID, id);
		}
	}
	
	@Override
	public SendableEntityCreator getCreator(String type, Object item, IdMap map, boolean searchForSuperCreator, String className) {
		if(Grammar.WRITE.equals(type)) {
			SendableEntityCreator creator = map.getCreator(className, true);
			if(creator != null) {
				return creator;
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
}
