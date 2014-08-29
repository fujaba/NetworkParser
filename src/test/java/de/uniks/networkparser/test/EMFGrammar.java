package de.uniks.networkparser.test;

import java.util.Iterator;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.Grammar;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;

public class EMFGrammar extends Grammar{
	public static final String SRC="@src";
	public static final String PROP="@prop";
	public static final String NV ="@nv";
	@Override
	public SendableEntityCreator getReadCreator(JsonObject jsonObject,
			IdMapEncoder map) {
		SendableEntityCreator result = getCreator(jsonObject.getString(SRC), map);
		if(result!=null){
			return result;
		}
		
		return super.getReadCreator(jsonObject, map);
	}
	
	@Override
	public String getReadValue(JsonObject json, String property) {
		if(property.equals(JsonIdMap.ID)){
			property = SRC;
		}
		return super.getReadValue(json, property);
	}
	
	@Override
	public boolean hasReadValue(JsonObject json, String property) {
		if(property.equals(JsonIdMap.ID)){
			property = SRC;
		}
		return super.hasReadValue(json, property);
	}
	
	@Override
	public JsonObject getReadProperties(JsonObject jsonObject, IdMapEncoder map, Filter filter, boolean isId) {
		JsonObject props= new JsonObject();
		if(jsonObject.has(PROP)){
			String key = jsonObject.getString(PROP);
			String value = jsonObject.getString(NV);
			SendableEntityCreator result = getCreator(value, map);
			if(result!=null){
				props.put(key, new JsonObject().withValue(SRC, value));
			}else{
				props.put(key, value);
			}
		}
		return props;
	}
	
	public SendableEntityCreator getCreator(String className, IdMapEncoder map){
		int pos=className.indexOf("@");
		if(pos>0){
			String clazz=className.substring(0, pos);
			for (Iterator<SendableEntityCreator> i = map.iterator();i.hasNext();){
				SendableEntityCreator creator = i.next();
				Object sendableInstance = creator.getSendableInstance(true);
				String refClazzName = sendableInstance.getClass().getName();
				if(refClazzName.endsWith("." +clazz)){
					return creator;
				}
			}
		}
		return null;
	}
}
