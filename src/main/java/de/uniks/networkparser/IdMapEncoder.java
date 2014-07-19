package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.gui.table.TableList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TypList;
import de.uniks.networkparser.json.UpdateListener;
/**
 * The Class IdMap.
 */

public abstract class IdMapEncoder extends AbstractMap implements Map<String, Object> {
	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant REMOVE. */
	public static final String REMOVE = "rem";

	/** The Constant UPDATE. */
	public static final String UPDATE = "upd";

	/** The Constant NEW. */
	public static final String NEW = "new";

	/** The Constant PRIO. */
	public static final String PRIO = "prio";
	
	/** The counter. */
	private IdMapCounter counter;

	/** The update listener. */
	protected UpdateListener updateListener;

	protected ArrayList<TypList> typList;

	/** The updatelistener for Notification changes. */
	protected PropertyChangeListener updatePropertylistener;
	
	protected ArrayEntryList keyValue;
	
	protected Filter filter=new Filter();
	
	
	protected NetworkParserLog logger = new NetworkParserLog();
	
	/**
	 * @return the CurrentLogger
	 */
	public NetworkParserLog getLogger() {
		return logger;
	}

	/**
	 * Set the Current Logger for Infos
	 * @param logger the new Logger
	 * @return Itself
	 */
	public IdMapEncoder withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}


	/**
	 * Instantiates a new id map.
	 */
	public IdMapEncoder() {
		super();
		this.keyValue = new ArrayEntryList();
		this.withCreator(new TextItems());
	}

	/**
	 * set the new List of Items for the Map
	 * 
	 * @param parent
	 *            the parent-List of Items
	 * @return the Map
	 */
	public IdMapEncoder withKeyValue(ArrayEntryList parent) {
		this.keyValue = parent;
		return this;
	}

	/**
	 * Sets the counter.
	 * 
	 * @param counter
	 *            the new counter
	 * @return Itself
	 */
	public IdMapEncoder withCounter(IdMapCounter counter) {
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
	 * Sets the session id.
	 * 
	 * @param value
	 *            the new session id
	 * @return Itself
	 */
	public IdMapEncoder withSessionId(String value) {
		getCounter().withPrefixId(value);
		return this;
	}

	/**
	 * Gets the Id. Do not generate a Id
	 * 
	 * @param obj
	 *            the obj
	 * @return the key
	 */
	public String getKey(Object obj) {
		String result = null;
		try{
			result = this.keyValue.getKey(obj);
		}catch(ConcurrentModificationException e){
			if(this.logger.error(this, "getKey", NetworkParserLog.ERROR_TYP_CONCURRENTMODIFICATION, obj)){
				throw e;
			}
		}
		return result;
	}

	/**
	 * Gets the object.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	public Object getObject(String key) {
		Object result = null;
		try{
			result = this.keyValue.getValue(key);
		}catch(ConcurrentModificationException e){
			if(this.logger.error(this, "getObject", NetworkParserLog.ERROR_TYP_CONCURRENTMODIFICATION, key)){
				throw e;
			}
		}
		return result;
	}

	/**
	 * Gets or Create the id.
	 * 
	 * @param obj
	 *            the obj
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
	 * Put.
	 * 
	 * @param jsonId
	 *            the json id
	 * @param object
	 *            the object
	 * @return the newObject
	 */
	@Override
	public Object put(String jsonId, Object object) {
		this.keyValue.with(jsonId, object);
		addListener(object);
		addTypList(object);
		return object;
	}

	private void addTypList(Object object) {
		if (this.typList != null) {
			for (TypList list : this.typList) {
				list.addObject(object);
			}
		}
	}

	public void addToTypList(TypList typList) {
		if (typList == null) {
			this.typList = new ArrayList<TypList>();
		}
		this.typList.add(typList);
	}

	/**
	 * @param object
	 *            for add Listener to object
	 * @return success of adding
	 */
	public boolean addListener(Object object) {
		if (object instanceof SendableEntity) {
			return ((SendableEntity) object).addPropertyChangeListener(getUpdateListener());
		}
		return false;
	}

	public UpdateListener getUpdateListener() {
		if (this.updateListener == null) {
			this.updateListener = new UpdateListener(this);
		}
		return this.updateListener;
	}

	/**
	 * Removes the Entity from List or Destroy them
	 * 
	 * @param oldValue the old Value
	 * @param destroy destroy the missed Element
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
									IdMapEncoder.REMOVE);
						}
					} else {
						creator.setValue(oldValue, prop, reference,
								IdMapEncoder.REMOVE);
					}
				}
			}
		}
		if (key != null) {
			this.keyValue.remove(key);
			if (this.typList != null) {
				for (TypList list : this.typList) {
					list.removeObject(oldValue);
				}
			}
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
	 *            the reference
	 * @param filter
	 *            the filter
	 * @param deep
	 *            the index of deep of model-ebene
	 * @return the object
	 */
	public Object cloneObject(Object reference, CloneFilter filter, int deep) {
		SendableEntityCreator creatorClass = getCreatorClass(reference);
		Object newObject = null;
		if (creatorClass != null) {
			newObject = creatorClass.getSendableInstance(false);
			String[] properties = creatorClass.getProperties();
			filter.withObject(reference, newObject);

			for (String property : properties) {
				Object value = creatorClass.getValue(reference, property);
				if (filter.getTyp() == CloneFilter.SIMPLE) {
					creatorClass
							.setValue(newObject, property, value, IdMapEncoder.NEW);
				} else if (value instanceof Collection<?>) {
					if (filter.getTyp() == CloneFilter.FULL) {
						Collection<?> list = (Collection<?>) value;
						for (Object item : list) {
							if (filter.hasObject(item)) {
								creatorClass.setValue(newObject, property,
										filter.getObject(item), IdMapEncoder.NEW);
							} else {
								SendableEntityCreator childCreatorClass = getCreatorClass(item);
								if (childCreatorClass != null) {
									if (!filter.isConvertable(this, item,
											property, value, true, deep)) {
										creatorClass.setValue(newObject,
												property, item, IdMapEncoder.NEW);
									} else {
										cloneObject(item, filter, deep-1);
									}
								} else {
									creatorClass.setValue(newObject, property,
											item, IdMapEncoder.NEW);
								}
							}
						}
					} else {
						creatorClass.setValue(newObject, property, value,
								IdMapEncoder.NEW);
					}
				} else {
					if (filter.hasObject(value)) {
						creatorClass.setValue(newObject, property,
								filter.getObject(value), IdMapEncoder.NEW);
					} else {
						SendableEntityCreator childCreatorClass = getCreatorClass(value);
						if (childCreatorClass != null) {
							if (!filter.isConvertable(this, value, property,
									value, false, deep)) {
								creatorClass.setValue(newObject, property,
										value, IdMapEncoder.NEW);
							} else {
								cloneObject(value, filter, deep-1);
							}
						} else {
							creatorClass.setValue(newObject, property, value,
									IdMapEncoder.NEW);
						}
					}
				}
			}
		}
		return newObject;
	}

	@Override
	public AbstractMap withCreator(String className, SendableEntityCreator creator) {
		return super.withCreator(className, creator);
	}

	/**
	 * Start carbage collection.
	 * 
	 * @param root
	 *            the root
	 */
	public void startCarbageCollection(Object root) {
		if (this.updateListener == null) {
			this.updateListener = new UpdateListener(this);
		}
		this.updateListener.startGarbageColection(root);
	}

	/**
	 * Garbage collection.
	 * 
	 * @param root
	 *            the root
	 */
	public void garbageCollection(Object root) {
		if (this.updateListener == null) {
			this.updateListener = new UpdateListener(this);
		}
		this.updateListener.garbageCollection(root);
	}

	public Object startUpdateModell(String clazz) {
		SendableEntityCreator creator = super.getCreator(clazz, true);
		if (creator != null) {
			Object result = creator.getSendableInstance(false);
			String id = getId(result);
			put(id, result);
			return result;
		}
		return null;
	}

	public TableList getTypList(SendableEntityCreator creator) {
		if (creator == null) {
			return null;
		}
		TableList result = new TableList();
		String clazzName = creator.getSendableInstance(true).getClass()
				.getName();
		for(int i=0;i<this.keyValue.size();i++){
			Object item = this.keyValue.getValue(i);
			if(item != null){
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
		TableList oldValues = getTypList(creator);
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
		return this.keyValue.containsKey(""+key);
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

	public ArrayEntryList getKeyValue(){
		return keyValue;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		this.clear();

		for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
			java.util.Map.Entry<?, ?> mapEntity = (Entry<?, ?>) i.next();
			put("" + mapEntity.getKey(), mapEntity.getValue());
		}
	}

	@Override
	public void clear() {
		this.keyValue.clear();
	}
	
    /* Not Good because copy values to new List use iterator
     * @see java.util.Map#keySet()
     */
	@Override
	@Deprecated
    public Set<String> keySet() {
        return new HashSet<String>(keyValue.keySet());
    }

    /* Not Good because copy values to new List use iterator
     * @see java.util.Map#values()
     */
	@Override
	@Deprecated
	public Collection<Object> values() {
        return keyValue.values();
    }

	public IdMapEncoder withUpdateMsgListener(PropertyChangeListener listener) {
		this.updatePropertylistener = listener;
		return this;
	}
	
	public IdMapEncoder withFilter(Filter filter){
		this.filter = filter;
		return this;
	}
	
	public abstract BaseItem encode(Object value);
	public abstract BaseItem encode(Object value, Filter filter);
	public abstract BaseItem getPrototyp();

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.creators.entrySet();
	}

}
