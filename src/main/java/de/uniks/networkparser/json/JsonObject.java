package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * A JsonObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having <code>get</code> and <code>opt</code> methods for accessing the
 * values by name, and <code>put</code> methods for adding or replacing values
 * by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JsonArray</code>, <code>JsonObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JsonObject.NULL</code> object. A JsonObject
 * constructor can be used to convert an external form JSON text into an
 * internal form whose values can be retrieved with the <code>get</code> and
 * <code>opt</code> methods, or to convert values into a JSON text using the
 * <code>put</code> and <code>toString</code> methods. A <code>get</code> method
 * returns a value if one can be found, and throws an exception if one cannot be
 * found. An <code>opt</code> method returns a default value instead of throwing
 * an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they do
 * not throw. Instead, they return a specified value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For example,
 *
 * <pre>
 * myString = new JsonObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 *
 *

 * produces the string <code>{"JSON": "Hello, World"}</code>.
	* <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON syntax rules. The constructors are more forgiving in the texts they
 * will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 * <code>{} [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=&gt;</code> as well as
 * by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2011-11-24
 */
public class JsonObject extends SimpleKeyValueList<String, Object> implements Entity {
	public final static String START="{";
	public final static String END="}";
	public JsonObject() {
		this.withAllowDuplicate(false);
	}

	/**
	 * Get the JsonArray value associated with a key.
	 *
	 * @param key
	 *			A key string.
	 * @return A JsonArray which is the value. if the key is not found or if the
	 *		 value is not a JsonArray.
	 */
	public JsonArray getJsonArray(String key) {
		Object object = this.get(key);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		} else if(object instanceof String) {
			return new JsonArray().withValue(""+object);
		}
		throw new RuntimeException("JsonObject[" + EntityUtil.quote(key)
				+ "] is not a JsonArray.");
	}

	/**
	 * Get the JsonObject value associated with a key.
	 *
	 * @param key
	 *			A key string.
	 * @return A JsonObject which is the value.
	 * @throws RuntimeException
	 *			 if the key is not found or if the value is not a JsonObject.
	 */
	public JsonObject getJsonObject(String key) {
		Object object = this.get(key);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		} else if(object instanceof String) {
			return new JsonObject().withValue(""+object);
		}
		throw new RuntimeException("JsonObject[" + EntityUtil.quote(key)
				+ "] is not a JsonObject.");
	}

	/**
	 * Get the JsonObject value associated with a key.
	 *
	 * @param key
	 *			A key string.
	 * @return A JsonObject which is the value.
	 * @throws RuntimeException
	 *			 if the key is not found or if the value is not a JsonObject.
	 */
	public long getLong(String key) {
		Object object = this.get(key);
		if (object instanceof Long) {
			return (Long) object;
		} else if (object instanceof Integer) {
			return 0l + (Integer) object;
		}
		throw new RuntimeException("JsonObject[" + EntityUtil.quote(key)
				+ "] is not a JsonObject.");
	}

	/**
	 * Make a JSON text of this JsonObject. For compactness, no whitespace is
	 * added. If this would not result in a syntactically correct JSON text,
	 * then null will be returned instead.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return a printable, displayable, portable, transmittable representation
	 *		 of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *		 brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *		 brace)</small>.
	 */
	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	/**
	 * Make a prettyprinted JSON text of this JsonObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor
	 *			The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation
	 *		 of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *		 brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *		 brace)</small>.
	 */
	@Override
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}
	protected String parseItem(EntityStringConverter converter) {
		int length = this.size();
		if (length == 0) {
			return "{}";
		}

		if (!isVisible()) {
			return "{" + size() + " values}";
		}
		converter.add();
		StringBuilder sb = new StringBuilder(START);
		if(length>1) {
			sb.append(converter.getPrefix());
		}
		sb.append(EntityUtil.quote(get(0).toString()));
		sb.append(":");
		sb.append(EntityUtil.valueToString(getValueByIndex(0), false, this, converter));
		for (int i = 1; i < length; i++) {
			sb.append(",");
			sb.append(converter.getPrefix());
			sb.append(EntityUtil.quote(get(i).toString()));
			sb.append(":");
			sb.append(EntityUtil.valueToString(getValueByIndex(i), false, this, converter));
		}
		converter.minus();
		if(length>1) {
			sb.append(converter.getPrefix());
		}
		sb.append(END);
		return sb.toString();
	}

	/**
	 * Set the value to Tokener or pairs of values
	 *
	 * @param values
	 *			a simple String of Value or pairs of key-values
	 * @return Itself
	 */
	public JsonObject withValue(String... values) {
		if (values.length % 2 == 0) {
			for (int z = 0; z < values.length; z += 2) {
				if(values[z + 1]!= null) {
					// Only add value != null
					put(values[z], values[z + 1]);
				}
			}
			return this;
		}
		if (values.length > 0) {
			new JsonTokener().withBuffer(values[0]).parseToEntity(this);
		}
		return this;
	}
	
	/**
	 * Set the value to Tokener or pairs of values
	 *
	 * @param values
	 *			a simple String of Value or pairs of key-values
	 * @return Itself
	 */
	public JsonObject withValue(Buffer values) {
		new JsonTokener().withBuffer(values).parseToEntity(this);
		return this;
	}

	/**
	 * Tokener to init the JsonObject
	 *
	 * @param x
	 *			tokener to add values with the tokener
	 * @return Itself
	 */
	public JsonObject withTokener(Tokener x) {
		x.parseToEntity(this);
		return this;
	}

	/**
	 * Tokener to init the JsonObject
	 *
	 * @param entity
	 *			entity to add values with the tokener
	 * @return Itself
	 */
	public JsonObject withEntity(SimpleKeyValueList<?, ?> entity) {
		new JsonTokener().parseToEntity(this, entity);
		return this;
	}

	/**
	 * Get a new Instance of JsonArray, JsonObject
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new JsonObject();
		}
		return new JsonArray();
	}

	public boolean has(String key) {
		return containsKey(key);
	}

	@Override
	public JsonObject withKeyValue(Object key, Object value) {
		super.withKeyValue(key, value);
		return this;
	}

	/**
	 * Accumulate values under a key. It is similar to the put method except
	 * that if there is already an object stored under the key then a EntityList
	 * is stored under the key to hold all of the accumulated values. If there
	 * is already a EntityList, then the new value is appended to it. In
	 * contrast, the put method replaces the previous value.
	 *
	 * If only one value is accumulated that is not a EntityList, then the
	 * result will be the same as using put. But if multiple values are
	 * accumulated, then the result will be like append.
	 *
	 * @param key
	 *			A key string.
	 * @param value
	 *			An object to be accumulated under the key.
	 * @return this.
	 */
	public JsonObject addToList(String key, Object value) {
		Object object = this.get(key);
		if (object == null) {
			this.put(key,
					value instanceof AbstractList ? getNewList(true).with(value)
							: value);
		} else if (object instanceof AbstractList) {
			((AbstractList<?>) object).with(value);
		} else {
			this.put(key, getNewList(false).with(object, value));
		}
		return this;
	}

	public JsonObject withKeyValue(String key, Object value) {
		if(value != null) {
			// Only add value != null
			int index = indexOf(key);
			if (index >= 0) {
				setValueItem(key, value);
				return this;
			}
			super.withKeyValue(key, value);
		}
		return this;
	}

	public Entity without(String key) {
		remove(key);
		return this;
	}

	public static JsonObject create(String value) {
		return new JsonObject().withValue(value);
	}

	@Override
	public boolean setValueItem(Object value) {
		this.add(IdMap.VALUE, value);
		return true;
	}

	@Override
	public BaseItem getChild(String label, boolean recursiv) {
		if(label == null || this.size() < 1) {
			return null;
		}
		Object item = this.get(label);
		JsonObject child; 
		if(item instanceof JsonObject) {
			child = (JsonObject) item;
		}else {
			child = new JsonObject();
			this.put(label, child);
		}
		return child;

		
	}

	@Override
	public void setType(String type) {
		this.add(IdMap.CLASS, type);
	}
}
