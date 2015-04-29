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
import de.uniks.networkparser.json.UpdateListenerJson;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class IdMap.
 */

public abstract class IdMap extends AbstractMap implements
		Map<String, Object> {
	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant REMOVE. */
	public static final String REMOVE = "rem";

	/** The Constant UPDATE. */
	public static final String UPDATE = "upd";

	/** The Constant NEW. */
	public static final String NEW = "new";

	/** The Constant COLLISION. */
	public static final String COLLISION = "collision";
	
	/** The Constant PRIO. */
	public static final String PRIO = "prio";

	/** The Constant PRIO. */
	public static final String SENDUPDATE = "sendupdate";

	
	/** The counter. */
	private IdMapCounter counter;

	/** The update listener. */
	protected UpdateListenerJson updateListener;

	/** The updatelistener for Notification changes. */
	protected PropertyChangeListener updatePropertylistener;

	protected SimpleKeyValueList<String, Object> keyValue;

	protected Filter filter = new Filter();

	protected NetworkParserLog logger = new NetworkParserLog();

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
	 *            the new Logger
	 * @return Itself
	 */
	public IdMap withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * Instantiates a new id map.
	 */
	public IdMap() {
		super();
		this.keyValue = new SimpleKeyValueList<String, Object>();
		this.keyValue.withFlag(SimpleKeyValueList.BIDI);
		this.withCreator(new TextItems());
	}

	/**
	 * set the new List of Items for the Map
	 *
	 * @param parent
	 *            the parent-List of Items
	 * @return the Map
	 */
	public IdMap withKeyValue(SimpleKeyValueList<String, Object> parent) {
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
	public IdMap withCounter(IdMapCounter counter) {
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
	public IdMap withSessionId(String value) {
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
	 *            the key
	 * @return the object
	 */
	public Object getObject(String key) {
		Object result = null;
		try {
			result = this.keyValue.getValueItem(key);
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
		if(jsonId.startsWith("192.168.2.181:8000;T") || jsonId.startsWith("192.168.2.183:8000;T")) {
			System.out.println("ERROR");
		}
		this.keyValue.with(jsonId, object);
		addListener(object);
		return object;
	}

	/**
	 * @param object
	 *            for add Listener to object
	 * @return success of adding
	 */
	public boolean addListener(Object object) {
		if (object instanceof SendableEntity) {
			return ((SendableEntity) object)
					.addPropertyChangeListener(getUpdateListener());
		}
		return false;
	}

	public UpdateListenerJson getUpdateListener() {
		if (this.updateListener == null) {
			this.updateListener = new UpdateListenerJson(this);
		}
		return this.updateListener;
	}

	/**
	 * Removes the Entity from List or Destroy them
	 *
	 * @param oldValue
	 *            the old Value
	 * @param destroy
	 *            destroy the missed Element
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
			this.keyValue.without(key, oldValue);
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
									if (!filter.isConvertable(this, reference,
											property, item, true, deep)) {
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
							if (!filter.isConvertable(this, reference,
									property, value, false, deep)) {
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

	@Override
	public AbstractMap withCreator(String className,
			SendableEntityCreator creator) {
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
			this.updateListener = new UpdateListenerJson(this);
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
			this.updateListener = new UpdateListenerJson(this);
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

	public IdMap withUpdateListener(PropertyChangeListener listener) {
		this.updatePropertylistener = listener;
		return this;
	}

	public IdMap withFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	public abstract BaseItem encode(Object value);

	public abstract BaseItem encode(Object value, Filter filter);

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		LinkedHashSet<java.util.Map.Entry<String, Object>> list = new LinkedHashSet<java.util.Map.Entry<String, Object>>();
		for (String key : keyValue.keySet()) {
			list.add(new MapEntry().with(key, keyValue.getValueItem(key)));
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
	 *            the new Value of CaseSensitive
	 * @return XMLGrammar Instance
	 */
	public IdMap withCaseSensitive(boolean value) {
		keyValue.withCaseSensitive(value);
		return this;
	}
}
