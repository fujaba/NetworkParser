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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.bytes.ByteTokener;
import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.event.ObjectMapEntry;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.json.UpdateListenerJson;
import de.uniks.networkparser.json.util.JsonArrayCreator;
import de.uniks.networkparser.json.util.JsonObjectCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.xml.EMFTokener;
import de.uniks.networkparser.xml.MapEntityStack;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLEntityCreator;
/**
 * The Class IdMap.
 */
public class IdMap implements Iterable<SendableEntityCreator> {
	/** The Constant CLASS. */
	public static final String CLASS = "class";

	/** The Constant VALUE. */
	public static final String VALUE = "value";
	
	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant REMOVE. */
	public static final String REMOVE = "rem";

	/** The Constant UPDATE. */
	public static final String UPDATE = "upd";

	/** The Constant NEW. */
	public static final String NEW = "new";
	
	/** The Constant SPACE. */
	public static final char SPACE = ' ';
	/** The Constant EQUALS. */
	public static final char EQUALS = '=';

	/** The Constant MAINITEM. */
	public static final String MAINITEM = "main";

	public static final char DOUBLEQUOTIONMARK = '"';
	
	private Grammar grammar = new SimpleGrammar();
	
	/** The update listener. */
	protected UpdateListenerJson updateListenerJson;

	/** The counter. */
	private IdMapCounter counter;

	protected SimpleKeyValueList<String, Object> keyValue = new SimpleKeyValueList<String, Object>().withFlag(SimpleKeyValueList.BIDI);

	protected Filter filter = new Filter();
	
	private JsonTokener jsonTokener = new JsonTokener();

	private XMLTokener xmlTokener = new XMLTokener();
	
	private ByteTokener byteTokener = new ByteTokener();
	
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
	 * Instantiates a new id map.
	 */
	public IdMap() {
		this.with(new TextItems());
		this.with(new DateCreator());
		this.with(new JsonObjectCreator());
		this.with(new JsonArrayCreator());
		this.with(new ObjectMapEntry());
		this.with(new XMLEntityCreator());
	}

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
		for (Iterator<SendableEntityCreator> i = iterator.iterator(); i.hasNext();) {
			with(i.next());
		}
		return this;
	}

	/**
	 * Adds the creator.
	 *
	 * @param createrClass
	 *			the creater class
	 * @return AbstractIdMap to interlink arguments
	 */
	public IdMap with(SendableEntityCreator... createrClass) {
		if(createrClass == null) {
			return this;
		}
		for (SendableEntityCreator creator : createrClass) {
			if(creator == null)
				continue;
			try{
				Object reference = creator.getSendableInstance(true);
				if (reference != null) {
					if (reference instanceof Class<?>) {
						this.searchForSuperCreator = true;
						with(((Class<?>)reference).getName(), creator);
					} else {
						with(reference.getClass().getName(), creator);
					}
				}
			}catch(Exception e){}
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
    	this.creators.add(className, creator);
		if (creator instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag creatorTag = (SendableEntityCreatorTag) creator;
			this.creators.add(creatorTag.getTag(), creator);
		}
		return this;
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
		if (key != null) {
			return key;
		}
		key = grammar.getId(obj, getCounter());
		if (key != null) {
			put(key, obj);
			return key;
		}
		key = getCounter().getId(obj);
		put(key, obj);
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

	public UpdateListenerJson getUpdateExecuter() {
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		return this.updateListenerJson;
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
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withDeep(deep);
		return cloning(reference, map);
	}
	private Object cloning(Object reference, MapEntity map) {
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
						map.minus();
						for (Object item : list) {
							Object refValue = map.getRefByEntity(item);
							if (refValue != null) {
								creatorClass.setValue(newObject, property,
										refValue, IdMap.NEW);
							} else {
								SendableEntityCreator childCreatorClass = getCreatorClass(item);
								if (childCreatorClass != null) {
									if (!map.isConvertable(reference, property, item)) {
										creatorClass.setValue(newObject,
												property, item,
												IdMap.NEW);
									} else {
										
										cloning(item, map);
									}
								} else {
									creatorClass.setValue(newObject, property,
											item, IdMap.NEW);
								}
							}
						}
						map.add();
					}
				} else {
					Object refValue = map.getRefByEntity(value);
					if (refValue != null) {
						creatorClass.setValue(newObject, property, refValue,
								IdMap.NEW);
					} else {
						SendableEntityCreator childCreatorClass = getCreatorClass(value);
						if (childCreatorClass != null) {
							if (!map.isConvertable(reference, property, value)) {
								creatorClass.setValue(newObject, property,
										value, IdMap.NEW);
							} else {
								map.minus();
								cloning(value, map);
								map.add();
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
						this.keyValue.remove(oldValue);
						put(oldKey, newObject);
					}
				}
			}
		}
		return result;
	}

	public void clear() {
		this.keyValue.clear();
	}

	public IdMap with(Filter filter) {
		this.filter = filter;
		return this;
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

	@Override
	public Iterator<SendableEntityCreator> iterator() {
		return this.creators.values().iterator();
	}
	
	public boolean notify(PropertyChangeEvent event) {
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



	public IdMap with(UpdateListenerJson updateListener) {
		this.updateListenerJson = updateListener;
		return this;
	}

	/**
	 * Garbage collection.
	 *
	 * @param root
	 *			the root
	 */
	public void garbageCollection(Object root) {
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		this.updateListenerJson.garbageCollection(root);
	}
	
	/**
	 * @param value
	 *			Gammar value
	 * @return Itself
	 */
	public IdMap with(Grammar value) {
		this.grammar = value;
		return this;
	}

	public IdMap with(PropertyChangeListener listener) {
		this.listener = listener;
		return this;
	}

	/**
	 * Set the new Listener
	 *
	 * @param listener the new Listener
	 * @return This Component
	 *
	 * @see IdMap#with(PropertyChangeListener)
	 * @see de.uniks.networkparser.ChainUpdateListener
	 */
	public IdMap with(UpdateListener listener) {
		this.listener = listener;
		return this;
	}

	public IdMap withSessionId(String value) {
		getCounter().withPrefixId(value);
		return this;
	}

	public boolean hasKey(Object element) {
		return this.keyValue.contains(element);
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @param value value to decode
	 * @return the object
	 */
	public Object decode(Buffer value) {
		char firstChar = value.nextClean(true);
		if (firstChar == JsonTokener.STARTARRAY) {
			return decode(jsonTokener.newInstanceList().withValue(value));
		}
		if (firstChar == JsonTokener.STARTENTITY) {
			return decode(jsonTokener.newInstance().withValue(value));
		}
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		if(firstChar == XMLTokener.ITEMSTART) {
			XMLTokener tokener = new XMLTokener();
			tokener.withBuffer(value);
			tokener.skipHeader();
			return decodingXMLEntity(tokener, map);
		}
		// MUST BE BYTE
		return byteTokener.decodeValue((byte)firstChar, value, map);
	}
	
	public Object decode(Tokener tokener) {
		char firstChar = tokener.nextClean(true);
		if (firstChar == '[') {
			return decode(new JsonArray().withValue(tokener));
		}
		if(firstChar == XMLTokener.ITEMSTART) {
			XMLTokener xmlTokener = (XMLTokener) tokener;
			xmlTokener.skipHeader();
			MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
			return decodingXMLEntity(xmlTokener, map);
		}
		Entity item = tokener.newInstance();
		tokener.parseToEntity(item);
		return decode(item);
	}
	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @param value for Decoding
	 * @return the object
	 */
	public Object decode(String value) {
		return decode(new CharacterBuffer().with(value.intern()));
	}
	
	/**
	 * Special Case for EMF
	 * @param value EMF-Value as String
	 * @return the object
	 */
	public GraphList decodeEMF(String value) {
		EMFTokener tokener = new EMFTokener();
		return tokener.decoding(value);
	}
	/**
	 * Decode.
	 *
	 * @param value
	 *			the value
	 * @param converter
	 *			the Converter for bytes to String
	 * @return the object
	 */
	public Object decode(String value, ByteConverter converter) {
		if(converter == null) {
			return null;
		}
		byte[] decodeBytes = converter.decode(value);
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		ByteBuffer buffer = new ByteBuffer().with(decodeBytes);
		return byteTokener.decodeValue((byte)buffer.getCurrentChar(), buffer, map);
	}


	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @param value Value for decoding as SubClasss from BaseItem
	 * @return the object
	 */
	public Object decode(BaseItem value) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return decoding(value, map);
	}
	
	/**
	 * Read json.
	 *
	 * @param value
	 *			the value for decoding
	 * @param target
	 *			the target
	 * @param filter
	 *			the filter for decoding
	 * @return the object
	 */
	public Object decode(BaseItem value, Object target, Filter filter) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withTarget(target);
		return decoding(value, map);
	}
	
	/**
	 * @param value
	 * @param map
	 * @return the decoded Values
	 */
	private Object decoding(BaseItem value, MapEntity map) {
		if (value instanceof JsonArray) {
			return decodingJsonArray((JsonArray) value, map);
		}
		if(value instanceof XMLEntity) {
			return decodingXMLEntity(new XMLTokener().withBuffer(value.toString()), map);
		}
		if(value instanceof ByteEntity) {
			ByteEntity entity = (ByteEntity) value;
			return byteTokener.decodeValue(entity, map);
		}

		return decodingJsonObject((JsonObject) value, map);
	}
	
	/**
	 * Decoding the XMLTokener with XMLGrammar.
	 *
	 * @param tokener
	 *			The XMLTokener
	 * @return the Model-Instance
	 */
	
	private Object decodingXMLEntity(XMLTokener tokener, MapEntity map) {
		if (tokener.skipTo(XMLTokener.ITEMSTART, false)) {
			map.withStack(new MapEntityStack());
			//FIRST TAG
			tokener.parseEntity(tokener, map);
			return tokener.parse(tokener, map);
		}
		return null;
	}

	// Methods for decoding SubElements 
	/**
	 * Read json.
	 *
	 * @param jsonArray
	 *            the json array
	 * @return the object
	 */
	private Object decodingJsonArray(JsonArray jsonArray, MapEntity map) {
		Object result = null;
		int len = jsonArray.size() - 1;
		// Add all Objects
		for (int i = 0; i <= len; i++) {
			JsonObject kidObject = jsonArray.getJSONObject(i);
			Object tmp = decodingJsonObject(kidObject, map);
			if (kidObject.has(MAINITEM)) {
				result = tmp;
			} else if (i == 0) {
				result = tmp;
			}
		}
		return result;
	}

	private Object decodingJsonObject(JsonObject jsonObject, MapEntity map) {
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		Object result = this.updateListenerJson.execute(jsonObject, filter);
		if (result != null) {
			return result;
		}
		return new JsonTokener().decoding(jsonObject, map);
	}
	
	/**
	 * To json array by ids.
	 *
	 * @param ids
	 *            the suspend id list
	 * @return success all Items to baseItem
	 */
	public JsonArray getJsonByIds(List<String> ids) {
		if (ids == null) {
			return null;
		}
		JsonArray items = new JsonArray();
		for (String childId : ids) {
			JsonObject jsonObject = toJsonObject(getObject(childId),
					new Filter().withConvertable(new Deep().withDeep(0)));
			if (jsonObject != null) {
				items.add(jsonObject);
			}
		}
		return items;
	}
	
	Filter getDefaultFilter() {
		return filter;
	}
	
	/**
	 * To XMLEntity
	 * @param entity the object
	 * @return the XMLEntity
	 */
	public XMLEntity toXMLEntity(Object entity) {
		if (entity == null) {
			return null;
		}
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return (XMLEntity) this.encode(entity, map, xmlTokener);
	}

	/**
	 * To XMLEntity
	 * @param entity the object
	 * @return the XMLEntity
	 */
	public XMLEntity toSimpleXML(Object entity) {
		if (entity == null) {
			return null;
		}
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withId(false);
		map.withStack(new MapEntityStack());
		return (XMLEntity) this.encode(entity, map, xmlTokener);
	}

	/**
	 * To json object.
	 *
	 * @param entity
	 *			the object
	 * @return the json object
	 */
	public JsonObject toJsonObject(Object entity) {
		if (entity == null) {
			return null;
		}
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return (JsonObject) this.encode(entity, map, jsonTokener);
	}

	/**
	 * To Jsonobject.
	 *
	 * @param entity
	 *			the entity
	 * @param filter
	 *			the filter
	 * @return the Jsonobject
	 */
	public JsonObject toJsonObject(Object entity, Filter filter) {
		if (entity == null) {
			return null;
		}
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return (JsonObject) this.encode(entity, map, new JsonTokener());
	}
	
	/**
	 * To json array.
	 *
	 * @param object
	 *			the object
	 * @return the json array
	 */
	public JsonArray toJsonArray(Object object) {
		return toJsonArray(object, null);
	}	
	public JsonArray toJsonArray(Object object, Filter filter) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withTarget(jsonTokener.newInstanceList());
		return (JsonArray) encodeList(object, map, jsonTokener);
	}
	public JsonArray toJsonArray(Object object, JsonArray target, Filter filter) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		if (target.isComparator()
				&& target.comparator() instanceof EntityComparator) {
			((EntityComparator<?>) target.comparator()).withMap(this);
		}
		map.withTarget(target);
		return (JsonArray) encodeList(object, map, jsonTokener);
	}
	
	public ByteItem toByteItem(Object object) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return byteTokener.encode(object, map);
	}
	public ByteItem toByteItem(Object object, Filter filter) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		return byteTokener.encode(object, map);
	}
	
	public GraphList toObjectDiagram(Object object) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withGraphFlag(GraphTokener.FLAG_OBJECT);
		return new GraphTokener().encode(object, map);
	}
	
	public GraphList toClassDiagram(Object object) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		map.withGraphFlag(GraphTokener.FLAG_CLASS);
		return new GraphTokener().encode(object, map);
	}
	
	public GraphList getDiffList(Object source, Object target) {
		GraphList sourceModel = toObjectDiagram(source);
		GraphList targetModel = toObjectDiagram(target);
		return new GraphTokener().diffModel(sourceModel, targetModel);
	}
	
	/**
	 * Convert to JsonArray in the resource
	 *
	 * @param object
	 *            the object
	 * @param filter
	 *            the filter
	 * @return the json array
	 */
	private EntityList encodeList(Object object, MapEntity map, Tokener tokener) {
		EntityList target = (EntityList) map.getTarget();

		if (object instanceof Collection<?>) {
			Collection<?> list = (Collection<?>) object;
			for (Iterator<?> i = list.iterator(); i.hasNext();) {
				Object item = i.next();
				if(map.getKey(item)==null) {
					//DEEP 0
					encode(item, map, tokener);
				}
			}
			return target;
		}
		if (object.getClass().isArray()) {
			for (Object item : ((Object[]) object)) {
				if(map.getKey(item)==null) {
					//DEEP 0
					encode(item, map, tokener);
				}
			}
			return target;
		}
		encode(object, map, tokener);
		return target;
	}
	
	/**
	 * Convert a Model to Tokener
	 * @param model The Model to encode
	 * @param tokener The Tokener For Syntax
	 * @return The Encoded Model
	 */
	public BaseItem encodeModel(Object model, Tokener tokener) {
		MapEntity map = new MapEntity(this, filter, grammar, searchForSuperCreator);
		BaseItem item = tokener.encode(model, map);
		if(item != null) {
			return item;
		}
		return encode(model, map, tokener);
	}
	
	/**
	 * Encode Model
	 *
	 * @param entity
	 *            the entity to convert
	 * @param map
	 *            encoding runtimevalue
	 * @param tokener
	 *            tokener for Encoding like JsonTokener, XMLTokener
	 * @return the Jsonobject
	 */
	private Entity encode(Object entity, MapEntity map, Tokener tokener) {
		if(entity == null) {
			return null;
		}
		String className = entity.getClass().getName();
 		return encode(entity, className,  map, tokener);
	}
	private Entity encode(Object entity, String className, MapEntity map, Tokener tokener) {
		String id = null;
		SendableEntityCreator creator = map.getCreator(Grammar.WRITE, entity, className);
		if (creator == null) {
			return null;
		}
		Entity newInstance = tokener.newInstance();
		if(creator instanceof SendableEntityCreatorNoIndex == false) {
			id = map.getId(entity, className);
		}
		map.writeBasicValue(creator, newInstance, className, id);
		EntityList targetList = (EntityList) map.getTarget();
		if(targetList != null && targetList.isComparator() == false) {
			targetList.with(newInstance);
		}
		String[] properties = map.getProperties(creator);
		if (properties != null) {
			map.pushStack(className, entity, creator);
			newInstance.setAllowEmptyValue(map.isFullSeriation());
			Object referenceObject = map.getNewEntity(creator, className, true);
			map.add();
			CharacterBuffer prop = map.getPrefixProperties(creator, tokener, entity, className);
			int pos=prop.length();
			
			for (String property : properties) {
				if (newInstance.has(property)) {
					if (map.error("toJsonObject", NetworkParserLog.ERROR_TYP_DUPPLICATE, entity, className)) {
						throw new RuntimeException("Property duplicate:" + property + "(" + className + ")");
					}
				}
				Object value = creator.getValue(entity, property);
				if(map.isPropertyRegard(entity, property, value) == false) {
					continue;
				}
				if (value != null) {
					boolean encoding = map.isFullSeriation();
					if (referenceObject instanceof Class<?>) {
						encoding = true;
					}
					if (!encoding) {
						Object refValue = creator.getValue(referenceObject, property);
						encoding = !value.equals(refValue);
					}
					if (encoding) {
						prop.setNextString(property, pos);
						Entity parent = (Entity) convertProperty(prop, newInstance);
						className = value.getClass().getName();
						SendableEntityCreator valueCreater = map.getCreator(Grammar.WRITE, value, className);
						String fullProp = prop.toString();
						
						
						String childClassName = value.getClass().getName();
						Object key = value;
						if(map.isId(value, className)) {
							key = map.getKey(value);
						}
						boolean contains = map.contains(key);
						if(valueCreater != null && targetList != null) {
							if(map.isConvertable(entity, property, value)  && contains == false ) {
								encode(value, childClassName, map, tokener);
							}
							Entity child = tokener.createLink(newInstance, fullProp, childClassName, map.getId(value));
							if(child != null) {
								SendableEntityCreator childCreater = map.getCreator(Grammar.WRITE, child, child.getClass().getName());
								parseValue(fullProp, child, null, childCreater, map, tokener, parent);
							}
						} else if ((map.isConvertable(entity, property, value) == false || contains ) && valueCreater != null){
							Entity child = null;
							if(map.isAddOwnerLink(value)) {
								child = tokener.createLink(newInstance, fullProp, childClassName, map.getId(value));
							}
							if(child != null) {
								SendableEntityCreator childCreater = map.getCreator(Grammar.WRITE, child, child.getClass().getName());
								parseValue(fullProp, child, null, childCreater, map, tokener, parent);
							}
						} else {
							parseValue(fullProp, value, className, valueCreater, map, tokener, parent);
						}
					}
				}else if(map.isFullSeriation()) {
					prop.setNextString(property, pos);
					Entity parent  = (Entity) convertProperty(prop, newInstance);
					parent.put(prop.toString(), value);
				}
			}
			map.minus();
			map.popStack();
		}
		if(targetList != null && targetList.isComparator()) {
			targetList.with(newInstance);
		}
		return newInstance;
	}
	private BaseItem convertProperty(CharacterBuffer property, BaseItem parent) {
		BaseItem child=parent;
		while(property.charAt(0) == ENTITYSPLITTER) {
			if(property.length() == 1) {
				break;
			}
			// Its ChildValue
			int pos = property.indexOf(ENTITYSPLITTER, 1);
			if (pos < 0) {
				property.trimStart(1);
				break;
			}
			String label = property.substring(1, pos);
			property.trimStart(label.length()+1);
			if (child instanceof Entity) {
				child = ((Entity)child).getChild(label, false);
			}
		}
		return child;
	}

	private void parseValue(String property, Object value, String className, SendableEntityCreator valueCreater, MapEntity map, Tokener tokener, BaseItem parent) {
		Object writeValue = null;
		if (value instanceof Collection<?> && valueCreater == null) {
			// Simple List or Assocs
			EntityList subValues;
			boolean isArray=map.getTarget() != null;
			subValues = tokener.newInstanceList();
//			map.add();
			for (Object child : ((Collection<?>) value)) {
				if(child != null) {
					String childClassName = child.getClass().getName();
					SendableEntityCreator childCreater = map.getCreator(Grammar.WRITE, child, childClassName);
					Object key = child;
					if(map.isId(child, className)) {
						key = map.getKey(child);
					}
					if(map.contains(key)) {
						child = tokener.createLink((Entity)parent, property, childClassName, map.getKey(child));
						childClassName = null;
					} else if(isArray) {
						if(map.isConvertable(value, property, child)) {
							encode(child, childClassName, map, tokener);
						}
						child = tokener.createLink((Entity)parent, property, childClassName, map.getId(child));
						childClassName = null;
					}
					parseValue( property, child, childClassName, childCreater, map, tokener, subValues);
				}
			}
//			map.minus();
			writeValue = subValues;
		} else if (value instanceof Map<?, ?> && valueCreater == null) {
			// Maps
			Map<?, ?> list = (Map<?, ?>) value;
			EntityList subValues = tokener.newInstanceList();
			String packageName = ObjectMapEntry.class.getName();
			for (Iterator<?> i = list.entrySet().iterator(); i.hasNext();) {
				Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
				SendableEntityCreator childCreater = map.getCreator(Grammar.WRITE, mapEntry, packageName);
				parseValue(property, mapEntry, packageName, childCreater, map, tokener, subValues);
			}
			writeValue = subValues;
		} else if(valueCreater != null && className != null){
			writeValue = encode(value, className, map, tokener);
				
		} else {
			writeValue = value;
		}
		if (parent instanceof EntityList && tokener.isChild(writeValue)){
			((EntityList)parent).with(tokener.transformValue(writeValue, parent));
		} else if (parent instanceof Entity){
			if (property.length() == 1 && property.charAt(0) == ENTITYSPLITTER) {
					// Its ChildValue
				((Entity)parent).setValueItem(tokener.transformValue(value, parent));
			} else  if (map.isTypSave() ) {
				Entity child = tokener.newInstance();
				child.put(CLASS, className);
				child.put(VALUE, tokener.transformValue(writeValue, parent));
				((Entity)parent).put(property, child);
			} else {
				//FILTER
				((Entity)parent).put(property, tokener.transformValue(writeValue, parent));
			}
		}
	}

	public SimpleKeyValueList<String, Object> getKeyValue() {
		return keyValue;
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

	public SimpleKeyValueList<String, SendableEntityCreator> getCreators() {
		return this.creators;
	}
}