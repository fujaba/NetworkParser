package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.MapEntityStack;

/**
 * @author Stefan
 * MapEntity for IdMap
 */
public class MapEntity extends SimpleList<Object>{
	private IdMap map;
	private Filter filter;
	private Grammar grammar;
	private int deep;
	private Object target;
	private MapEntityStack stack;
	private boolean isId = true;
	/** The show line. */
	private byte graphFlag = GraphTokener.FLAG_CLASS;
	
	/** boolean for switch of search for Interface or Abstract superclass for entity */
	protected boolean searchForSuperCreator;
	/** If this is true the IdMap save the Typ of primary datatypes. */
	private boolean typSave;

	public MapEntity(IdMap map, Filter filter, Grammar grammar, boolean searchForSuperCreator) {
		this.map = map;
		if(filter != null) {
			this.filter = filter;
		} else {
			this.filter = this.map.getDefaultFilter();
		}
		this.grammar = grammar;
		this.searchForSuperCreator = searchForSuperCreator;
	}
	
	public IdMap getMap() {
		return map;
	}
	public void setMap(IdMap map) {
		this.map = map;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * @return the grammar
	 */
	public Grammar getGrammar() {
		return grammar;
	}
	public void add() {
		this.deep = this.deep + 1;
	}
	public void minus() {
		this.deep = this.deep - 1;
	}
	public boolean isTypSave() {
		return typSave;
	}
	
	// Methods for Map
	public boolean error(String method, String type, Object entity, String className) {
		return false;
	}
	public SendableEntityCreator getCreatorClass(Object reference) {
		return map.getCreatorClass(reference);
	}
	public SendableEntityCreator getCreator(String className, boolean fullName) {
		return map.getCreator(className, fullName);
	}
	
	public String getKey(Object reference) {
		return map.getKey(reference);
	}
	public String getId(Object reference) {
		return map.getId(reference);
	}
	public Object getObject(String key) {
		return map.getObject(key);
	}
	public boolean notify(PropertyChangeEvent evt) {
		return this.map.notify(evt);
	}
	
	// Methods for Grammar
	public SendableEntityCreator getCreator(String type, Object item, String className) {
		return grammar.getCreator(type, item, map, searchForSuperCreator, className);
	}
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		return grammar.getNewEntity(creator, className, prototype);
	}
	public boolean hasValue(Entity item, String property) {
		return grammar.hasValue(item, property);
	}
	public String getValue(Entity item, String property) {
		return grammar.getValue(item, property);
	}
	public BaseItem getProperties(Entity entity, boolean isId, String type) {
		return grammar.getProperties(entity, map, filter, isId, type);
	}

	// Method for Filter
	public String getId(Object entity, String className) {
		if (filter.isId(entity, className, map) == false) {
			this.with(entity);
		} else {
			String id = map.getId(entity);
			this.with(id);
			return id;
		}
		return null;
	}

	public boolean isFullSeriation() {
		return filter.isFullSeriation();
	}
	public String[] getProperties(SendableEntityCreator creator) {
		return filter.getProperties(creator);
	}
	public boolean isPropertyRegard(Object entity, String property, Object value) {
		return filter.isPropertyRegard(entity, property, value, map, deep);
	}
	public boolean isConvertable(Object entity, String property, Object value) {
		return filter.isConvertable(entity, property, value, map, deep);
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
	}
	
	public void popStack() {
		if(this.stack != null) {
			this.stack.popStack();
		}
	}
	/**
	 * @param stack the stack to set
	 * @return ThisComponent
	 */
	public MapEntity withStack(MapEntityStack stack) {
		this.stack = stack;
		return this;
	}

	public CharacterBuffer getPrefixProperties(SendableEntityCreator creator, Tokener tokener, Object entity, String className) {
		boolean isId = filter.isId(entity, className, map);
		return grammar.getPrefixProperties(creator, tokener, isId);
	}
	
	public void writeBasicValue(SendableEntityCreator creator, Entity entity, String className, String id) {
		if(this.isId == false) {
			if(creator instanceof SendableEntityCreatorTag) {
				className = ((SendableEntityCreatorTag)creator).getTag();
			}
			id = null;
		}
		grammar.writeBasicValue(entity, className, id);
	}
	
	public MapEntity withId(boolean value) {
		this.isId = value;
		return this;
	}
	
	/**
	 * @param value Is Association To Parent
	 * @return the addOwnerLink
	 */
	public boolean isAddOwnerLink(Object value) {
		if(isId) {
			return isId;
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

	public MapEntity withGraphFlag(byte flag) {
		if(flag == GraphTokener.FLAG_CLASS ) {
			graphFlag = (byte) (graphFlag & (graphFlag & GraphTokener.FLAG_OBJECT) | GraphTokener.FLAG_CLASS);
		} else if(flag == GraphTokener.FLAG_OBJECT ) {
			graphFlag = (byte) (graphFlag & (graphFlag & GraphTokener.FLAG_CLASS) | GraphTokener.FLAG_OBJECT);
		} else {
			this.graphFlag = (byte) (this.graphFlag | flag);
		}
		return this;
	}
	
	/**
	 * @param flag is the Flag is Set
	 * @return the type
	 */
	public boolean isFlag(byte flag) {
		return (graphFlag & flag) != 0;
	}
}
