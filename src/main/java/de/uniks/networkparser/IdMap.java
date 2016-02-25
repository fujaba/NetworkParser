package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
/**
 * The Class IdMap.
 */
public abstract class IdMap implements Map<String, Object>, Iterable<SendableEntityCreator> {
	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant REMOVE. */
	public static final String REMOVE = "rem";

	/** The Constant UPDATE. */
	public static final String UPDATE = "upd";

	/** The Constant NEW. */
	public static final String NEW = "new";

	/** The Constant MERGE. */
	public static final String MERGE = "merge";

	/** The Constant COLLISION. */
	public static final String COLLISION = "collision";

	/** The Constant PRIO. */
	public static final String PRIO = "prio";
	
	public static final String CHILDREN= "<CHILDREN>";
	
	/** The Constant SPACE. */
	public static final char SPACE = ' ';
	/** The Constant EQUALS. */
	public static final char EQUALS = '=';

	/** The counter. */
	private IdMapCounter counter;

	protected SimpleKeyValueList<String, Object> keyValue = new SimpleKeyValueList<String, Object>().withFlag(SimpleKeyValueList.BIDI);

	protected Filter filter = new Filter().withMap(this);

	protected NetworkParserLog logger = new NetworkParserLog();

	/** The updatelistener for Notification changes. */
	protected Object listener;

	/** The Constant ENTITYSPLITTER. */
	public static final char ENTITYSPLITTER = '.';

	/** The creators. */
	protected SimpleKeyValueList<String, SendableEntityCreator> creators = new SimpleKeyValueList<String, SendableEntityCreator>()
			.withAllowDuplicate(false);
	/**
	 * boolean for switch of search for Interface or Abstract superclass for entity
	 */
	protected boolean searchForSuperCreator;
	/**
	 * Gets the creator class.
	 *
	 * @param reference
	 *			the reference
	 * @return the creator class
	 */
	public SendableEntityCreator getCreatorClass(Object reference) {
		if (reference == null) {
			return null;
		}
		return getCreator(reference.getClass().getName(), true);
	}

	/**
	 * Gets the creator classes.
	 *
	 * @param clazz
	 *			Clazzname for search
	 * @param fullName
	 *			if the clazzName is the Fullname for search
	 * @return return a Creator class for a clazz name
	 */
	public SendableEntityCreator getCreator(String clazz, boolean fullName) {
		Object creator = this.creators.getValue(clazz);
		if (creator != null || fullName ) {
			return (SendableEntityCreator) creator;
		}
		String endTag;
		if(clazz.lastIndexOf(ENTITYSPLITTER)>=0) {
			endTag = ENTITYSPLITTER + clazz.substring(clazz.lastIndexOf(ENTITYSPLITTER)+1);
		} else {
			endTag = ENTITYSPLITTER + clazz;
		}
		for(int i=0;i<this.creators.size();i++) {
			String key = this.creators.getKeyByIndex(i);
			SendableEntityCreator value = this.creators.getValueByIndex(i);
			if (key.endsWith(endTag)) {
				return value;
			}
		}
		
		// Search for Child Node
		return null;
	}

	/**
	 * Adds the creator.
	 *
	 * @param creatorSet
	 *			the creater class
	 * @return return a Creator class for a clazz name
	 */
	public IdMap with(Collection<SendableEntityCreator> creatorSet) {
		if(creatorSet == null) {
			return this;
		}
		for (SendableEntityCreator sendableEntityCreator : creatorSet) {
			with(sendableEntityCreator);
		}
		return this;
	}

	/**
	 * Adds the creator.
	 *
	 * @param iterator
	 *			the creater classes
	 * @return return a Creator class for a clazz name
	 */
	public IdMap with(Iterable<SendableEntityCreator> iterator) {
		if(iterator == null) {
			return null;
		}
		for (Iterator<SendableEntityCreator> i = iterator.iterator(); i
				.hasNext();) {
			with(i.next());
		}
		return this;
	}

	/**
	 * add a Creator to list of all creators.
	 *
	 * @param className
	 *			the class name
	 * @param creator
	 *			the creator
	 * @return AbstractIdMap to interlink arguments
	 */
	public IdMap with(String className,
			SendableEntityCreator creator) {
		addCreator(className, creator);
		return this;
	}

    public boolean addCreator(String className, SendableEntityCreator creator) {
    	boolean result = this.creators.add(className, creator);
		if (creator instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag creatorTag = (SendableEntityCreatorTag) creator;
			this.creators.add(creatorTag.getTag(), creator);
		}
		return result;
    }

	/**
	 * Adds the creator.
	 *
	 * @param createrClass
	 *			the creater class
	 * @return AbstractIdMap to interlink arguments
	 */
	public IdMap with(SendableEntityCreator... createrClass) {
		addCreator(createrClass);
		return this;
	}

	public boolean addCreator(SendableEntityCreator... createrClass) {
		if(createrClass == null) {
			return false;
		}
		for (SendableEntityCreator creator : createrClass) {
			if(creator == null)
				continue;
			try{
				Object reference = creator.getSendableInstance(true);
				if (reference != null) {
					if (reference instanceof Class<?>) {
						this.searchForSuperCreator = true;
						addCreator(((Class<?>)reference).getName(), creator);
					} else {
						addCreator(reference.getClass().getName(), creator);
					}
				}
			}catch(Exception e){}
		}
		return true;
    }

	/**
	 * remove the creator.
	 *
	 * @param className
	 *			the creater class
	 * @return true, if successful
	 */
	public boolean removeCreator(String className) {
		return this.creators.remove(className) != null;
	}

	/**
	 * @return the CurrentLogger
	 */
	public NetworkParserLog getLogger() {
		return logger;
	}

	/**
	 * Set the Current Logger for Infos
	 *
	 * @param logger
	 *			the new Logger
	 * @return Itself
	 */
	public IdMap with(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * Instantiates a new id map.
	 */
	public IdMap() {
		super();
		this.with(new TextItems());
	}

	/**
	 * set the new List of Items for the Map
	 *
	 * @param parent
	 *			the parent-List of Items
	 * @return the Map
	 */
	public IdMap withKeyValue(SimpleKeyValueList<String, Object> parent) {
		if(parent != null)
			this.keyValue = parent;
		return this;
	}

	/**
	 * Sets the counter.
	 *
	 * @param counter
	 *			the new counter
	 * @return Itself
	 */
	public IdMap with(IdMapCounter counter) {
		this.counter = counter;
		return this;
	}

	/**
	 * Gets the counter.
	 *
	 * @return the counter
	 */
	public IdMapCounter getCounter() {
		if (this.counter == null) {
			this.counter = new SimpleIdCounter();
		}
		return this.counter;
	}

	/**
	 * Gets the Id. Do not generate a Id
	 *
	 * @param obj
	 *			the obj
	 * @return the key
	 */
	public String getKey(Object obj) {
		String result = null;
		try {
			int pos = this.keyValue.indexOfValue(obj);
			if(pos>=0) {
				return this.keyValue.getKeyByIndex(pos);
			}
		} catch (ConcurrentModificationException e) {
			if (this.logger.error(this, "getKey",
					NetworkParserLog.ERROR_TYP_CONCURRENTMODIFICATION, obj)) {
				throw e;
			}
		}
		return result;
	}

	/**
	 * Gets the object.
	 *
	 * @param key
	 *			the key
	 * @return the object
	 */
	public Object getObject(String key) {
		Object result = null;
		try {
			result = this.keyValue.getValue(key);
		} catch (ConcurrentModificationException e) {
			if (this.logger.error(this, "getObject",
					NetworkParserLog.ERROR_TYP_CONCURRENTMODIFICATION, key)) {
				throw e;
			}
		}
		return result;
	}

	/**
	 * Gets or Create the id.
	 *
	 * @param obj
	 *			the obj
	 * @return the id
	 */
	public String getId(Object obj) {
		String key = getKey(obj);
		if (key == null) {
			key = getCounter().getId(obj);
			put(key, obj);
		}
		return key;
	}

	/**
	 * Put a Object to List
	 *
	 * @param jsonId
	 *			the json id
	 * @param object
	 *			the object
	 * @return the newObject
	 */
	@Override
	public Object put(String jsonId, Object object) {
		this.keyValue.with(jsonId, object);
		addListener(object);
		return object;
	}

	protected boolean addListener(Object object) {
		if (object instanceof SendableEntity) {
			((SendableEntity) object).addPropertyChangeListener(getUpdateExecuter());
		}
		return false;
	}

	public PropertyChangeListener getUpdateExecuter() {
		return null;
	}

	/**
	 * Removes the Entity from List or Destroy them
	 *
	 * @param oldValue
	 *			the old Value
	 * @param destroy
	 *			destroy the missed Element
	 * @return boolean if success
	 */
	public boolean removeObj(Object oldValue, boolean destroy) {
		String key = getKey(oldValue);
		if (destroy) {
			SendableEntityCreator creator = getCreatorClass(oldValue);
			if (creator != null) {
				String[] props = creator.getProperties();
				for (String prop : props) {
					Object reference = creator.getValue(oldValue, prop);
					if (reference instanceof Collection<?>) {
						Collection<?> continee = (Collection<?>) reference;
						Iterator<?> i = continee.iterator();
						while (i.hasNext()) {
							creator.setValue(oldValue, prop, i.next(),
									IdMap.REMOVE);
						}
					} else {
						creator.setValue(oldValue, prop, reference,
								IdMap.REMOVE);
					}
				}
			}
		}
		if (key != null) {
			this.keyValue.withoutAll(key, oldValue);
			return true;
		}
		return false;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return this.keyValue.size();
	}

	/**
	 * Clone object.
	 *
	 * @param reference
	 *			the reference
	 * @param filter
	 *			the filter
	 * @param deep
	 *			the index of deep of model-ebene
	 * @return the object
	 */
	public Object cloneObject(Object reference, Filter filter, int deep) {
		SendableEntityCreator creatorClass = getCreatorClass(reference);
		Object newObject = null;
		if (creatorClass != null) {
			newObject = creatorClass.getSendableInstance(false);
			String[] properties = creatorClass.getProperties();

			for (String property : properties) {
				Object value = creatorClass.getValue(reference, property);
				if (value instanceof Collection<?>) {
					if (filter.isFullSeriation()) {
						Collection<?> list = (Collection<?>) value;
						for (Object item : list) {
							Object refValue = filter.getRefByEntity(item);
							if (refValue != null) {
								creatorClass.setValue(newObject, property,
										refValue, IdMap.NEW);
							} else {
								SendableEntityCreator childCreatorClass = getCreatorClass(item);
								if (childCreatorClass != null) {
									if (!filter.isConvertable(reference, property, item, deep)) {
										creatorClass.setValue(newObject,
												property, item,
												IdMap.NEW);
									} else {
										cloneObject(item, filter, deep - 1);
									}
								} else {
									creatorClass.setValue(newObject, property,
											item, IdMap.NEW);
								}
							}
						}
					}
				} else {
					Object refValue = filter.getRefByEntity(value);
					if (refValue != null) {
						creatorClass.setValue(newObject, property, refValue,
								IdMap.NEW);
					} else {
						SendableEntityCreator childCreatorClass = getCreatorClass(value);
						if (childCreatorClass != null) {
							if (!filter.isConvertable(reference, property, value, deep)) {
								creatorClass.setValue(newObject, property,
										value, IdMap.NEW);
							} else {
								cloneObject(value, filter, deep - 1);
							}
						} else {
							creatorClass.setValue(newObject, property, value,
									IdMap.NEW);
						}
					}

				}
			}
		}
		return newObject;
	}

	public Object startUpdateModell(String clazz) {
		SendableEntityCreator creator = getCreator(clazz, true);
		if (creator != null) {
			Object result = creator.getSendableInstance(false);
			String id = getId(result);
			put(id, result);
			return result;
		}
		return null;
	}

	public SimpleList<Object> getTypList(SendableEntityCreator creator) {
		if (creator == null) {
			return null;
		}
		SimpleList<Object> result = new SimpleList<Object>();
		String clazzName = creator.getSendableInstance(true).getClass()
				.getName();
		for (Object item : this.keyValue.values()) {
			if (item != null) {
				if (item.getClass().getName().equals(clazzName)) {
					result.add(item);
				}
			}
		}
		return result;
	}

	public boolean replaceObject(Object newObject) {
		String key = getKey(newObject);
		if (key != null) {
			return false;
		}
		if (!(newObject instanceof Comparable<?>)) {
			return false;
		}
		SendableEntityCreator creator = getCreatorClass(newObject);
		if (creator == null) {
			return false;
		}
		boolean result = false;
		SimpleList<Object> oldValues = getTypList(creator);
		for (Object obj : oldValues) {
			if (obj instanceof Comparable<?>) {
				@SuppressWarnings("unchecked")
				Comparable<Object> oldValue = (Comparable<Object>) obj;
				if (oldValue.compareTo(newObject) == 0) {
					String oldKey = getKey(oldValue);
					if (oldKey != null) {
						remove(oldValue);
						put(oldKey, newObject);
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return this.keyValue.size() < 1;
	}

	@Override
	public boolean containsKey(Object key) {
		return this.keyValue.containsKey("" + key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.keyValue.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return getKey(key);
	}

	@Override
	public Object remove(Object oldValue) {
		if (removeObj(oldValue, false)) {
			return oldValue;
		}
		return null;
	}

	public SimpleKeyValueList<String, Object> getKeyValue() {
		return keyValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		if(map == null) {
			return;
		}
		for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
			java.util.Map.Entry<?, ?> mapEntity = (Entry<?, ?>) i.next();
			put("" + mapEntity.getKey(), mapEntity.getValue());
		}
	}

	@Override
	public void clear() {
		this.keyValue.clear();
	}

	/*
	 * Not Good because copy values to new List use iterator
	 *
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet() {
		return keyValue.keySet();
	}

	/*
	 * Not Good because copy values to new List use iterator
	 *
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Object> values() {
		return keyValue.values();
	}

	public IdMap with(Filter filter) {
		this.filter = filter;
		return this;
	}

	public abstract BaseItem encode(Object value);

	public abstract BaseItem encode(Object value, Filter filter);

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		LinkedHashSet<java.util.Map.Entry<String, Object>> list = new LinkedHashSet<java.util.Map.Entry<String, Object>>();
		for (String key : keyValue.keySet()) {
			list.add(new MapEntry().with(key, keyValue.getValue(key)));
		}
		return list;
	}

	/**
	 * @return the CaseSensitive Option
	 */
	public boolean isCaseSensitive() {
		return keyValue.isCaseSensitive();
	}

	/**
	 * For setting the Option of checking the CaseSensitive of the Properties
	 *
	 * @param value
	 *			the new Value of CaseSensitive
	 * @return XMLGrammar Instance
	 */
	public IdMap withCaseSensitive(boolean value) {
		keyValue.withCaseSensitive(value);
		return this;
	}

	// Methods for Filtering
	protected boolean hasObjects(Filter filter, Object element) {
		return filter.hasObjects(element);
	}
	protected void with(Filter filter, Object... visitedObject) {
		filter.withObjects(visitedObject);
	}
	protected int getIndexVisitedObjects(Filter filter, Object element) {
		return filter.getIndexVisitedObjects(element);
	}
	protected Object getVisitedObjects(Filter filter, int index) {
		return filter.getVisitedObjects(index);
	}
	protected boolean isConvertable(Filter filter, Object entity, String property, Object value, int deep) {
		return filter.isConvertable(entity, property, value, deep);
	}
	protected boolean isPropertyRegard(Filter filter,  Object entity, String property, Object value, int deep) {
		return filter.isPropertyRegard(entity, property, value, deep);
	}
	
	@Override
	public Iterator<SendableEntityCreator> iterator() {
		return this.creators.values().iterator();
	}
	
	protected boolean notify(PropertyChangeEvent event) {
    	if (this.listener != null ) {
    		if(this.listener instanceof PropertyChangeListener) {
    			((PropertyChangeListener)this.listener).propertyChange(event);
    		}
    		if (this.listener != null && this.listener instanceof UpdateListener) {
    			return ((UpdateListener)this.listener).update(event);
    		}
    	}
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + " (" + this.size() + ")";
	}
}