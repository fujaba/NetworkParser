package de.uniks.networkparser;

import java.util.Iterator;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.XMLEntity;

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
/**
 * A Entity for Map
 * 
 * @author Stefan MapEntity for IdMap
 */
public class MapEntity extends AbstractList<Object> {
	protected Filter filter;
	protected int deep;
	protected Object target;
	/** The show line. */
	protected byte tokenerFlag;
	private SimpleMap map;
	public byte mapFlag;
	private Grammar grammar;
	private Tokener tokener;
    
    /** The Stack. */
	private SimpleKeyValueList<Object, SendableEntityCreator> stackItems = new SimpleKeyValueList<Object, SendableEntityCreator>();
	private SimpleList<String> tags = new SimpleList<String>();
	private SimpleKeyValueList<String, SimpleSet<String>> childProperties = new SimpleKeyValueList<String, SimpleSet<String>>();

	public MapEntity(Filter filter, byte flag, SimpleMap map, Tokener tokener) {
		if (filter != null) {
			this.filter = filter;
		}
		this.mapFlag = flag;
		this.map = map;
		if (map != null) {
			this.grammar = map.getGrammar();
		}
		this.tokener = tokener;
	}

	public MapEntity(SimpleMap map) {
		if (map != null) {
			this.filter = map.getFilter();
			this.mapFlag = map.getFlag();
			this.grammar = map.getGrammar();
		}
		this.map = map;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public Filter getFilter() {
		return filter;
	}

	public Entity encode(Object entity) {
		if (tokener == null || tokener.getMap() == null) {
			return null;
		}
		return tokener.getMap().encode(entity, this);
	}

	public int getDeep() {
		return deep;
	}

	public boolean isId(Object target) {
		String className = target.getClass().getName();
		return filter.isId(target, className, map);
	}

	public boolean isSearchForSuperClass() {
		return (mapFlag & SimpleMap.FLAG_SEARCHFORSUPERCLASS) != 0;
	}

	public boolean isSimpleFormat() {
		boolean result = (mapFlag & SimpleMap.FLAG_SIMPLEFORMAT) != 0;
		if (result) {
			return result;
		}
		if (filter == null) {
			return false;
		}
		return filter.isSimpleFormat();
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

	public MapEntity withStrategy(String value) {
		if (value != null) {
			this.filter.withStrategy(value);
		}
		return this;
	}

	public MapEntity withDeep(int value) {
		this.deep = value;
		return this;
	}

	public int addDeep() {
		deep++;
		return deep;
	}

	public Object getCloneByEntity(Object value) {
		for (int i = 0; i < size(); i += 2) {
			if (get(i) == value) {
				return get(i + 1);
			}
		}
		return null;
	}

	public Object getEntityByClone(Object value) {
		for (int i = 1; i < size(); i += 2) {
			if (get(i) == value) {
				return get(i - 1);
			}
		}
		return null;
	}


	public void pushStack(String className, Object entity, SendableEntityCreator creator) {
		this.withStack(className, entity, creator);
		this.deep = this.deep + 1;
	}

	public CharacterBuffer getPrefixProperties(SendableEntityCreator creator, Object entity, String className) {
		CharacterBuffer result = new CharacterBuffer();
		if (this.isSimpleFormat() || grammar.isFlatFormat()) {
			return result;
		}
		boolean isComplex = filter.isSimpleFormat(entity, creator, className, map);
		if (isComplex) {
			return result;
		}
		result.with(SimpleMap.ENTITYSPLITTER).with(Tokener.PROPS).with(SimpleMap.ENTITYSPLITTER);
		return result;
	}

	public MapEntity withFilter(Filter filter) {
		this.filter = filter;
		return this;
	}
	/* Method for Filter */
	public String getId(Object entity, String className) {
		if (filter == null) {
			return null;
		}
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

	public Entity writeBasicValue(SendableEntityCreator creator, Entity entity, BaseItem parent, String className,
			String id) {
		if ((mapFlag & SimpleMap.FLAG_ID) == 0) {
			if (creator instanceof SendableEntityCreatorTag) {
				className = ((SendableEntityCreatorTag) creator).getTag();
			}
			id = null;
		} else if (filter.isShortClass()) {
			if (className != null && className.startsWith("de.uniks.networkparser.ext.petaf")) {
				className = className.substring(className.lastIndexOf('.') + 1);
			}
		}
		return grammar.writeBasicValue(entity, className, id, this.filter.getStrategy(), this.getMap());
	}

	/**
	 * @param value Is Association To Parent
	 * @return the addOwnerLink
	 */
	public boolean isAddOwnerLink(Object value) {
		if ((mapFlag & SimpleMap.FLAG_ID) != 0) {
			return true;
		}
		return getPrevItem() != value;
	}

	public int getIndexOfClazz(String clazzName) {
		if (clazzName == null) {
			return -1;
		}
		int pos = 0;
		for (Iterator<Object> i = this.iterator(); i.hasNext();) {
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
		for (Iterator<Object> i = this.iterator(); i.hasNext();) {
			Object item = i.next();
			if (item == element) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public Object getVisitedObjects(int index) {
		if (index >= 0 && index < size()) {
			return get(index);
		}
		return null;
	}

	public String getClazz(int pos) {
		if (pos < 0 || pos > size()) {
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
		if (item != null) {
			return item.getClass().getName();
		}
		return null;
	}

	public Entity convertProperty(CharacterBuffer property, BaseItem parent) {
		BaseItem child = parent;
		if (property == null) {
			return null;
		}
		while (property.charAt(0) == SimpleMap.ENTITYSPLITTER) {
			if (property.length() == 1) {
				break;
			}
			/* Its ChildValue */
			int pos = property.indexOf(SimpleMap.ENTITYSPLITTER, 1);
			if (pos < 0) {
				property.trimStart(1);
				break;
			}
			String label = property.substring(1, pos);
			property.trimStart(label.length() + 1);
			if (child instanceof Entity) {
				Entity entity = (Entity) child;
				BaseItem newItem = entity.getElementBy(XMLEntity.PROPERTY_TAG, label);
				if (newItem == null) {
					newItem = child.getNewList(true);
					if (newItem instanceof XMLEntity) {
						((XMLEntity) newItem).withType(label);
						entity.add(newItem);
					} else {
						((Entity) child).put(label, newItem);
					}
				}
				child = newItem;
			}
		}
		return (Entity) child;
	}

	/**
	 * @param flag is the Flag is Set
	 * @return the type
	 */
	public boolean isFlag(byte flag) {
		return (this.mapFlag & flag) != 0;
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

	public SimpleMap getMap() {
		return map;
	}

	@Override
	public MapEntity getNewList(boolean keyValue) {
		return new MapEntity(null);
	}

	public Tokener getTokener() {
		if (tokener == null && map != null) {
			tokener = map.getJsonTokener();
		}
		return this.tokener;
	}

	public boolean isStrategyNew() {
		if (this.filter == null) {
			return true;
		}
		return SendableEntityCreator.NEW.equalsIgnoreCase(this.filter.getStrategy());
	}

    /**
     * Remove The Last Element
     */
    public void popStack() {
        this.stackItems.removePos(this.stackItems.size() - 1);
        this.deep = this.deep - 1;
        this.tags.remove(this.tags.size() - 1);
    }

    /** @return The StackSize */
    public int getStackSize() {
        return this.stackItems.size();
    }

    public SimpleList<String> getTags() {
        return tags;
    }

    /**
     * Get the current Element
     *
     * @return The Stack Element - offset
     */
    public Object getCurrentItem() {
        return this.stackItems.last();
    }

    /**
     * Get the previous Element
     * 
     * @return The Stack Element - offset
     */
    public Object getPrevItem() {
        int pos = this.stackItems.size() - 2;
        if (pos < 0) {
            return null;
        }
        return this.stackItems.get(pos);
    }

    /**
     * Add a new Reference Object to Stack.
     * 
     * @param tag     The new Tag
     * @param item    new Reference Object
     * @param creator The Creator for the Item
     * @return XMLTokener Instance
     */
    public MapEntity withStack(String tag, Object item, SendableEntityCreator creator) {
        if (creator == null) {
            return this;
        }
        stackItems.add(item, creator);
        tags.add(tag);
        String[] properties = creator.getProperties();
        for (String property : properties) {
            int lastPos = property.lastIndexOf(SimpleMap.ENTITYSPLITTER);
            if (lastPos >= 0) {
                String prop;
                if (lastPos == property.length() - 1) {
                    /* Value of XML Entity like uni. */
                    prop = ".";
                } else {
                    prop = property.substring(lastPos + 1);
                }
                int pos = childProperties.indexOf(prop);
                if (pos >= 0) {
                    childProperties.getValueByIndex(pos).add(property);
                } else {
                    SimpleSet<String> child = new SimpleSet<String>();
                    child.add(property);
                    childProperties.put(prop, child);
                }
            }
        }
        return this;
    }

    /**
     * Get the Current Creator for the MapEntity
     *
     * @return The Stack Element - offset
     */
    public SendableEntityCreator getCurrentCreator() {
        return this.stackItems.getValueByIndex(this.stackItems.size() - 1);
    }

    public void setValue(String key, String value) {
        SimpleSet<String> set = childProperties.get(key);
        if (set != null) {
            for (String ChildKey : set) {
                int pos = getEntityPos(ChildKey);
                if (pos >= 0) {
                    Object entity = stackItems.getKeyByIndex(pos);
                    SendableEntityCreator creator = stackItems.getValueByIndex(pos);
                    creator.setValue(entity, ChildKey, value, SendableEntityCreator.NEW);
                }
            }
        }
    }

    private int getEntityPos(String entity) {
        if (entity == null) {
            return -1;
        }
        int start = entity.lastIndexOf(SimpleMap.ENTITYSPLITTER);
        int pos = this.tags.size() - 1;
        for (int end = start - 1; end >= 0; end--) {
            if (entity.charAt(end) == SimpleMap.ENTITYSPLITTER) {
                String item = entity.substring(end + 1, start);
                String tag = tags.get(pos);
                if (tag == null || tag.equals(item) == false) {
                    return -1;
                }
                start = end;
                pos--;
            }
        }
        return pos;
    }

    public String getCurrentTag() {
        if (this.tags.size() > 0) {
            return this.tags.get(this.tags.size() - 1);
        }
        return null;
    }

	
}
