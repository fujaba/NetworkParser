package de.uniks.networkparser.xml;

import java.util.Map.Entry;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class HTMLGrammar extends SimpleGrammar{
	public static final String CLASSNAME="%CLASSNAME";
	public static final String PROPERTY="%PROPERTY";
	public static final String DEEP="%DEEP";

	private SimpleKeyValueList<String, String> transformValue = new SimpleKeyValueList<String, String>();
	private SimpleIteratorSet<String, String> iterator = new SimpleIteratorSet<String, String>(transformValue);
	@Override
	public BaseItem encode(Object entity, MapEntity map, Tokener tokener) {
		HTMLEntity rootItem=new HTMLEntity();
		rootItem.withEncoding("utf-8");
		Entity child = map.encode(entity, tokener);
		rootItem.with(child);
		return rootItem;
	}
	
	private boolean transformValue(CharacterBuffer value, int deep, boolean isClassName) {
		iterator.reset();
		while(iterator.hasNext()) {
			Entry<String, String> item = iterator.next();
			if(isClassName && CLASSNAME.equals(item.getKey())) {
				if(value.indexOf('.')>0) {
					value.set(item.getValue());
					return true;
				}
			}
			if(value.equals(item.getKey())) {
				String property = value.toString();
				value.set(item.getValue());
				value.replace(DEEP, ""+deep);
				value.replace(PROPERTY, property);
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Entity writeBasicValue(Entity entity, BaseItem parent, String className, String id, MapEntity map) {
		CharacterBuffer value = new CharacterBuffer().with(className);
		if(transformValue(value, 0, true)) {
			if(value.charAt(0) == IdMap.ENTITYSPLITTER) {
				entity = map.convertProperty(value, parent);
			} else {
				entity = map.convertProperty(value, entity);
			}
			String prop = value.toString();
			Entity item = (Entity) entity.getNewList(false);
			item.setType(prop);
			entity.with(item);
			entity = item;
			super.writeBasicValue(item, entity, prop, id, map);
			entity.with(IdMap.CLASS, className);
		}else {
			super.writeBasicValue(entity, parent, className, id, map);
		}
		return entity;
	}
	
	@Override
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener) {
		if (parent instanceof EntityList && tokener.isChild(value)){
			((EntityList)parent).with(value);
		} else if (parent instanceof Entity){
			CharacterBuffer prop = new CharacterBuffer().with(property);
			transformValue(prop, map.getDeep(), false);
			parent = map.convertProperty(prop, parent);
			property = prop.toString();
			if (property.length() == 1 && property.charAt(0) == IdMap.ENTITYSPLITTER) {
//				// Its ChildValue
				((Entity)parent).setValueItem(tokener.transformValue(value, parent));
			} else {
				((Entity)parent).put(property, tokener.transformValue(value, parent));
			}
		}
		return true;
	}

	/**
	 *  Variables:
	 * %CLASSNAME ClassName
	 * %PROPERTY Property
	 * %DEEP Property
	 * @param key the Key for transform
	 * @param value the Value for transform
	 */
	public void with(String key, String value) {
		this.transformValue.add(key, value);
	}
}
