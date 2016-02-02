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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class JsonGrammar extends SimpleGrammar {
	@Override
	public BaseItem getProperties(Entity item, IdMap map, Filter filter, boolean isId, String type) {
		if (isId) {
			if (item.has(JsonIdMap.JSON_PROPS)) {
				return ((JsonObject)item).getJsonObject(JsonIdMap.JSON_PROPS);
			}
		} else {
			JsonObject props = new JsonObject();
			for (int i = 0; i < item.size(); i++) {
				if (!JsonIdMap.CLASS.equalsIgnoreCase(item.getKeyByIndex(i))) {
					props.put(item.getKeyByIndex(i), item.getValueByIndex(i));
				}

			}
			return props;
		}
		return null;
	}
	
	@Override
	public BaseItem setProperties(IdMap map, SendableEntityCreator prototyp, String className, String id,
			Entity properties, Filter filter) {
		JsonObject json = new JsonObject();
		json.put(JsonIdMap.CLASS, className);
		if (prototyp instanceof SendableEntityCreatorNoIndex
				|| !filter.isId(properties, className)) {
			for (int i = 0; i < properties.size(); i++) {
				json.put(properties.getKeyByIndex(i), properties.getValueByIndex(i));
			}
			return json;
		}
		json.put(IdMap.ID, id);
		if (properties.size() > 0) {
			json.put(JsonIdMap.JSON_PROPS, properties);
		}
		return json;
	}
	
	@Override
	public String getId(Object obj, IdMapCounter counter) {
		return null;
	}
}
