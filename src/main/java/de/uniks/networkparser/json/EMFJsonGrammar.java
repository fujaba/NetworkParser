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
import java.util.Iterator;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EMFJsonGrammar extends SimpleGrammar {
	public static final String SRC="@src";
	public static final String PROP="@prop";
	public static final String NV ="@nv";


	@Override
	public BaseItem getProperties(Entity item, IdMap map, Filter filter, boolean isId, String type) {
		JsonObject props= new JsonObject();
		if(item.has(PROP)){
			String key = item.getString(PROP);
			String value = item.getString(NV);
			SendableEntityCreator result = getCreator(Grammar.READ, null, map, false, value);
			if(result!=null){
				props.put(key, new JsonObject().withValue(SRC, value));
			} else {
				props.put(key, value);
			}
			return props;
		}
		return item;
	}

	@Override
	public SendableEntityCreator getCreator(String type, Object item, IdMap map, boolean searchForSuperCreator,
			String className) {
		if(Grammar.READ.equals(type) && item instanceof Entity) {
			SendableEntityCreator result = getCreator(type, null, map, false, ((Entity)item).getString(SRC));
			if(result!=null){
				return result;
			}
			return super.getCreator(type, item, map, searchForSuperCreator, className);
		}
		if(className == null) {
			return null;
		}
		int pos=className.indexOf("@");
		String clazz=null;
		if(pos>0){
			clazz=className.substring(0, pos);
		}else {
			pos = className.lastIndexOf(".");
			if(pos>0) {
				clazz=className.substring(0, pos);
			}
		}
		if(clazz != null) {
			for (Iterator<SendableEntityCreator> i = map.iterator();i.hasNext();){
				SendableEntityCreator creator = i.next();
				Object sendableInstance = creator.getSendableInstance(true);
				String refClazzName = sendableInstance.getClass().getName();
				if(refClazzName.endsWith("." +clazz)){
					return creator;
				}
			}
		}
		return super.getCreator(type, item, map, searchForSuperCreator, className);
	}

	@Override
	public String getId(Object obj, IdMapCounter counter) {
		String name = obj.getClass().getName();
		int pos = name.lastIndexOf(".");
		counter.withPrefixId(null);
		if (pos > 0) {
			return name.substring(pos + 1) + counter.getSplitter()
					+ counter.getId(obj);
		} else {
			return name + counter.getSplitter() + counter.getId(obj);
		}
	}

	@Override
	public String getValue(Entity item, String property) {
		if (JsonIdMap.ID.equals(property)) {
			return item.getString(SRC);
		}
		return item.getString(property);
	}

	@Override
	public boolean hasValue(Entity json, String property) {
		if(property.equals(JsonIdMap.ID)){
			property = SRC;
		}
		return super.hasValue(json, property);
	}

	@Override
	public BaseItem setProperties(IdMap map, SendableEntityCreator prototyp, String className, String id,
			Entity properties, Filter filter) {
		JsonObject json = new JsonObject();

		json.put(SRC, id);

		if (properties.size() > 0) {
			for (int i = 0; i < properties.size(); i++) {
				json.put(properties.getKeyByIndex(i), properties.getValue(i));
			}
		}
		return json;
	}
}
