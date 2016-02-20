package de.uniks.networkparser.json;
/*
NetworkParser
Copyright (c) 2011 - 2015, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.
*/
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class JsonGrammar extends SimpleGrammar {
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
		entity.put(IdMap.CLASS, className);
		if(id != null) {
			entity.put(IdMap.ID, id);
		}
	}
}
