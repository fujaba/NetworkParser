package de.uniks.networkparser;

import java.util.Iterator;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.MapEntityStack;
import de.uniks.networkparser.xml.XMLEntity;
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
/**
 * @author Stefan
 * MapEntity for IdMap
 */
public class MapEntity extends SimpleSet<Object>{
	protected Filter filter;
	protected int deep;
	protected Object target;
	protected MapEntityStack stack;
	/** The show line. */
	protected byte tokenerFlag;
	protected IdMap map;
	public byte mapFlag;

	public MapEntity(String tag, Object item, SendableEntityCreator creator) {
		this.withStack(new MapEntityStack().withStack(tag, item, creator));
	}

	public MapEntity(Filter filter, byte flag, IdMap map) {
		if(filter != null) {
			this.filter = filter;
		}
		this.map = map;
		this.mapFlag = flag;
	}

	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Entity encode(Object entity, Tokener tokener) {
		return tokener.getMap().encode(entity, this, tokener);
	}

	public int getDeep() {
		return deep;
	}

	public boolean isTypSave() {
		return (mapFlag & IdMap.FLAG_TYPESAVE) != 0;
	}
	public boolean isSearchForSuperClass() {
		return (mapFlag & IdMap.FLAG_SEARCHFORSUPERCLASS) != 0;
	}
	public boolean isSimpleFormat() {
		return (mapFlag & IdMap.FLAG_SIMPLEFORMAT) != 0;
	}

	// Methods for Grammar
	public SendableEntityCreator getCreator(String type, Object item, String className) {
		return map.getGrammar().getCreator(type, item, map, isSearchForSuperClass(), className);
	}
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		return map.getGrammar().getNewEntity(creator, className, prototype);
	}
	public boolean hasValue(Entity item, String property) {
		return map.getGrammar().hasValue(item, property);
	}
	public String getValue(Entity item, String property) {
		return map.getGrammar().getValue(item, property);
	}
	public BaseItem getProperties(Entity entity, boolean isId, String type) {
		return map.getGrammar().getProperties(entity, map, filter, isId, type);
	}

	// Method for Filter
	public String getId(Object entity, String className) {
		if (filter.isId(entity, className, map) == false) {
			this.with(entity);
		} else {
			boolean newMessage = SendableEntityCreator.UPDATE.equals(this.getFilter().getStrategy()) == false;
			String id = map.getId(entity, newMessage);
			this.with(id);
			return id;
		}
		return null;
	}

	public boolean isFullSeriation() {
		return filter.isFullSerialization();
	}
	
	public String[] getProperties(Tokener tokener, SendableEntityCreator creator) {
		return filter.getProperties(creator);
	}

	public int convert(Object entity, String property, Object value) {
		return filter.convert(entity, property, value, map, deep);
	}

	public boolean isId(Object entity, String className) {
		return filter.isId(entity, className, map);
	}
	public String getStrategy() {
		return filter.getStrategy();
	}
	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 * @return The Target Object
	 */
	public MapEntity withTarget(Object target) {
		this.target = target;
		return this;
	}

	public MapEntity withDeep(int value) {
		this.deep = value;
		return this;
	}

	public Object getRefByEntity(Object value) {
		for (int i = 0; i < size(); i += 2) {
			if (get(i) == value) {
				return get(i + 1);
			}
		}
		return null;
	}

	/**
	 * @return the stack
	 */
	public MapEntityStack getStack() {
		return stack;
	}

	public void pushStack(String className, Object entity, SendableEntityCreator creator) {
		if(this.stack != null) {
			this.stack.withStack(className, entity, creator);
		}
		this.deep = this.deep + 1;
	}

	public void popStack() {
		if(this.stack != null) {
			this.stack.popStack();
		}
		this.deep = this.deep - 1;
	}
	/**
	 * @param stack the stack to set
	 * @return ThisComponent
	 */
	public MapEntity withStack(MapEntityStack stack) {
		this.stack = stack;
		return this;
	}
	
	public CharacterBuffer getPrefixProperties(SendableEntityCreator creator, Object entity, String className) {
		CharacterBuffer result = new CharacterBuffer();
		if(this.isSimpleFormat()) {
			return result;
		}
		boolean isComplex = filter.isSimpleFormat(entity, creator, className, map);
		if(isComplex) {
			return result;
		}
		result.with(IdMap.ENTITYSPLITTER).with(Tokener.PROPS).with(IdMap.ENTITYSPLITTER);
		return result;
	}

	public Entity writeBasicValue(SendableEntityCreator creator, Entity entity, BaseItem parent, String className, String id) {
		if((mapFlag & IdMap.FLAG_ID) == 0) {
			if(creator instanceof SendableEntityCreatorTag) {
				className = ((SendableEntityCreatorTag)creator).getTag();
			}
			id = null;
		}
		return map.getGrammar().writeBasicValue(entity, parent, className, id, this);
	}

	/**
	 * @param value Is Association To Parent
	 * @return the addOwnerLink
	 */
	public boolean isAddOwnerLink(Object value) {
		if((mapFlag & IdMap.FLAG_ID) != 0) {
			return true;
		}
		if(stack != null) {
			return stack.getPrevItem() != value;
		}
		return false;
	}

	public int getIndexOfClazz(String clazzName) {
		if(clazzName == null) {
			return -1;
		}
		int pos = 0;
		for(Iterator<Object> i = this.iterator();i.hasNext();) {
			Object item = i.next();
			if (clazzName.equalsIgnoreCase(item.getClass().getName())) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public int getIndexVisitedObjects(Object element) {
		int pos = 0;
		for(Iterator<Object> i = this.iterator();i.hasNext();) {
			Object item = i.next();
			if (item == element) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public Object getVisitedObjects(int index) {
		if (index>=0 && index < size()) {
			return get(index);
		}
		return null;
	}

	public String getClazz(int pos) {
		if(pos<0 || pos > size()) {
			return null;
		}
		Object item = get(pos);
		if (item instanceof String) {
			return "" + item;
		}
		return null;
	}

	public String getLastClazz() {
		Object item = last();
		if(item != null) {
			return item.getClass().getName();
		}
		return null;
	}

	public Entity convertProperty(CharacterBuffer property, BaseItem parent) {
		BaseItem child=parent;
		while(property.charAt(0) == IdMap.ENTITYSPLITTER) {
			if(property.length() == 1) {
				break;
			}
			// Its ChildValue
			int pos = property.indexOf(IdMap.ENTITYSPLITTER, 1);
			if (pos < 0) {
				property.trimStart(1);
				break;
			}
			String label = property.substring(1, pos);
			property.trimStart(label.length()+1);
			if (child instanceof Entity) {
				BaseItem newItem = ((Entity)child).getElementBy(XMLEntity.PROPERTY_TAG, label);
				if(newItem == null) {
					newItem = child.getNewList(true);
					if(newItem instanceof XMLEntity) {
						((XMLEntity) newItem).setType(label);
						child.add(newItem);
					} else {
						((Entity) child).put(label, newItem);
					}
				}
				child = newItem;
			}
		}
		return (Entity)child;
	}

	/**
	 * @param flag is the Flag is Set
	 * @return the type
	 */
	public boolean isFlag(byte flag) {
		return (this.mapFlag & flag) != 0;
	}

	public boolean writeValue(BaseItem parent, String property, Object value, Tokener tokener) {
		return map.getGrammar().writeValue(parent, property, value, this, tokener);
	}

	public MapEntity withTokenerFlag(byte flag) {
		this.tokenerFlag = (byte) (this.tokenerFlag | flag);
		return this;
	}
	public MapEntity withoutTokenerFlag(byte flag) {
		this.tokenerFlag = (byte) (this.tokenerFlag | flag);
		this.tokenerFlag -= flag;
		return this;
	}
	/**
	 * @param flag is the Flag is Set
	 * @return the type
	 */
	public boolean isTokenerFlag(byte flag) {
		return (this.tokenerFlag & flag) != 0;
	}

	public IdMap getMap() {
		return map;
	}
}
