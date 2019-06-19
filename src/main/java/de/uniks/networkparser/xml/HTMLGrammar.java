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
import java.util.Map.Entry;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class HTMLGrammar extends SimpleGrammar {
	public static final String CLASSNAME = "%CLASSNAME";
	public static final String PROPERTY = "%PROPERTY";
	public static final String DEEP = "%DEEP";

	private SimpleKeyValueList<String, String> transformValue = new SimpleKeyValueList<String, String>();
	private SimpleIteratorSet<String, String> iterator = new SimpleIteratorSet<String, String>(transformValue);

	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		if (entity == null || map == null) {
			return null;
		}
		HTMLEntity rootItem = new HTMLEntity();
		rootItem.withEncoding(HTMLEntity.ENCODING);
		Entity child = map.encode(entity);
		rootItem.add(child);
		return rootItem;
	}

	private boolean transformValue(CharacterBuffer value, int deep, boolean isClassName) {
		iterator.reset();
		while (iterator.hasNext()) {
			Entry<String, String> item = iterator.next();
			if (isClassName && CLASSNAME.equals(item.getKey())) {
				if (value.indexOf('.') > 0) {
					value.set(item.getValue());
					return true;
				}
			}
			if (value != null && value.equals(item.getKey())) {
				String property = value.toString();
				value.set(item.getValue());
				value.replace(DEEP, "" + deep);
				value.replace(PROPERTY, property);

				return true;
			}
		}
		return false;
	}

	@Override
	public Entity writeBasicValue(Entity entity, String className, String id, String type, IdMap map) {
		CharacterBuffer value = new CharacterBuffer().with(className);
		if (transformValue(value, 0, true) && entity != null) {
			String prop = value.toString();
			Entity item = (Entity) entity.getNewList(false);
			String session = map.getSession();
			if (session != null) {
				entity.put(IdMap.SESSION, session);
			}

			if (id != null) {
				entity.put(IdMap.ID, id);
				entity.put(IdMap.TIMESTAMP, id.substring(1));
			}
			entity.add(item);
			entity = item;
			super.writeBasicValue(item, prop, id, type, map);
		} else {
			super.writeBasicValue(entity, className, id, type, map);
		}
		return entity;
	}

	@Override
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener) {
		if (parent instanceof EntityList && tokener.isChild(value)) {
			((EntityList) parent).add(value);
		} else if (parent instanceof Entity) {
			CharacterBuffer prop = new CharacterBuffer().with(property);
			transformValue(prop, map.getDeep(), false);
			parent = map.convertProperty(prop, parent);
			property = prop.toString();
			if (property.length() == 1 && property.charAt(0) == IdMap.ENTITYSPLITTER) {
				/* Its ChildValue */
				Object element = tokener.transformValue(value, parent);
				CharacterBuffer buffer = new CharacterBuffer().with("" + element);
				((Entity) parent).withValue(buffer);
			} else {
				((Entity) parent).put(property, tokener.transformValue(value, parent));
			}
		}
		return true;
	}

	/**
	 * Variables: %CLASSNAME ClassName %PROPERTY Property %DEEP Property
	 * 
	 * @param key   the Key for transform
	 * @param value the Value for transform
	 */
	public void with(String key, String value) {
		this.transformValue.add(key, value);
	}
}
