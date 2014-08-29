package de.uniks.networkparser.json;

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
import java.util.Map;
import de.uniks.networkparser.AbstractEntity;
import de.uniks.networkparser.AbstractKeyValueList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.interfaces.StringItem;
/* Copyright (c) 2002 JSON.org */


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
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=&gt;</code> as well as by
 * <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2011-11-24
 */
public class JsonObject extends AbstractKeyValueList<String, Object> implements StringItem, FactoryEntity, Entity{
	private boolean visible=true;

	/**
	 * Get the JsonArray value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return A JsonArray which is the value.
	 *             if the key is not found or if the value is not a JsonArray.
	 */
	public JsonArray getJsonArray(String key) {
		Object object = this.get(key);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		}
		throw new RuntimeException("JsonObject[" + EntityUtil.quote(key)
				+ "] is not a JsonArray.");
	}

	/**
	 * Get the JsonObject value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return A JsonObject which is the value.
	 * @throws RuntimeException
	 *             if the key is not found or if the value is not a JsonObject.
	 */
	public JsonObject getJsonObject(String key) {
		Object object = this.get(key);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		}
		throw new RuntimeException("JsonObject[" + EntityUtil.quote(key)
				+ "] is not a JsonObject.");
	}

   /**
    * Get the JsonObject value associated with a key.
    *
    * @param key
    *            A key string.
    * @return A JsonObject which is the value.
    * @throws RuntimeException
    *             if the key is not found or if the value is not a JsonObject.
    */
   public long getLong(String key) {
      Object object = this.get(key);
      if (object instanceof Long) {
         return (long) object;
      }
      else if (object instanceof Integer)
      {
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
	 *         of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	@Override
	public String toString() {
		int length = this.size();
		if (length == 0) {
			return "{}";
		}
		if (!isVisible()) {
			return "{Item with " + values.size() + " values}";
		}

		StringBuilder sb = new StringBuilder("{");
		sb.append(EntityUtil.quote(get(0).toString()));
		sb.append(":");
		sb.append(EntityUtil.valueToString(getValue(0), false, this));
		for (int i=1;i<size();i++) {
			sb.append(",");
			sb.append(EntityUtil.quote(get(i).toString()));
			sb.append(":");
			sb.append(EntityUtil.valueToString(getValue(i), false, this));
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Make a prettyprinted JSON text of this JsonObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation
	 *         of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	@Override
	public String toString(int indentFactor, int indent) {
		int length = this.size();
		if (length == 0) {
			return "{}";
		}

		if (!isVisible()) {
			return "{" + values.size() + " values}";
		}

		int newindent = indent + indentFactor;
		String prefix = "";
		StringBuilder sb;
		String step = EntityUtil.repeat(' ', indentFactor);
		if (indent > 0) {
			sb = new StringBuilder();
			for (int i = 0; i < indent; i += indentFactor) {
				sb.append(step);
			}
			prefix = CRLF + sb.toString();
		} else if (indentFactor > 0) {
			prefix = CRLF;
		}

		if ( length == 1) {
			sb = new StringBuilder("{");
		} else {
			sb = new StringBuilder("{" + prefix + step);
		}

		sb.append(EntityUtil.quote(get(0).toString()));
		sb.append(":");
		sb.append(EntityUtil.valueToString(getValue(0), indentFactor, newindent,
				false, this));
		for (int i=1; i<length;i++) {
			sb.append("," + prefix + step);
			sb.append(EntityUtil.quote(get(i).toString()));
			sb.append(":");
			sb.append(EntityUtil.valueToString(getValue(i), indentFactor,
					newindent, false, this));
		}
		if ( length == 1) {
			sb.append("}");
		} else {
			sb.append(prefix + "}");
		}
		return sb.toString();
	}

	/**
	 * Set the value to Tokener or pairs of values
	 *
	 * @param values
	 *            a simple String of Value or pairs of key-values
	 * @return Itself
	 */
	public JsonObject withValue(String... values) {
		this.values.clear();
		if (values.length % 2 == 0) {
			for (int z = 0; z < values.length; z += 2) {
				put(values[z], values[z + 1]);
			}
			return this;
		}
		if (values.length>0) {
			new JsonTokener().withText(values[0]).parseToEntity(this);
		}
		return this;
	}

	/**
	 * Tokener to init the JsonObject
	 *
	 * @param x
	 *            tokener to add values with the tokener
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
	 *            entity to add values with the tokener
	 * @return Itself
	 */
	public JsonObject withEntity(AbstractKeyValueList<?, ?> entity) {
		new JsonTokener().parseToEntity(this, entity);
		return this;
	}

	/**
	 * Get a new Instance of JsonArray
	 */
	@Override
	public JsonObject getNewObject() {
		return new JsonObject();
	}

	/**
	 * Get a new Instance of JsonObject
	 */
	@Override
	public JsonArray getNewArray() {
		return new JsonArray();
	}

	@Override
	public BaseItem withVisible(boolean value) {
		this.visible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public boolean has(String key) {
		return containsKey(key);
	}

	@Override
	public JsonObject withValue(Object key, Object value) {
		super.withValue(key, value);
		return this;
	}

	@Override
	public JsonObject getNewInstance() {
		return new JsonObject();
	}

	@Override
	public JsonObject with(
			Object... values) {
		if (values != null) {
			for (Object value : values) {
				if (value instanceof AbstractEntity<?,?>) {
					AbstractEntity<?,?> item = (AbstractEntity<?, ?>) value;
					this.put(item.getKeyString(), item.getValue());
				}else if (value instanceof Map<?,?>) {
					this.withMap( (Map<?,?>) value);
				}
			}
		}
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
	 *            A key string.
	 * @param value
	 *            An object to be accumulated under the key.
	 * @return this.
	 */
	public JsonObject addToList(String key, Object value) {
		Object object = this.get(key);
		if (object == null) {
			this.put(key, value instanceof AbstractList ? getNewArray().with(value) : value);
		} else if (object instanceof AbstractList) {
			((AbstractList<?>) object).with(value);
		} else {
			this.put(key, getNewArray().with(object).with(value));
		}
		return this;
	}

	@Override
	public Object remove(Object key) {
		return removeItemByObject((String) key);
	}

	@Override
	public Object put(String key, Object value) {
		int pos;
		if (!isAllowDuplicate()) {
			key = key.toLowerCase();
		}
		pos = getPositionKey(key);
		if (pos>=0) {
	    	if (this.hashTableValues != null) {
	    		this.hashTableValues[pos] = value;
	    		pos = transformIndex(pos, key);
	    	}
			return this.values.set(pos, value);
		}
		addEntity(key, value);
	
		return value;
	}

	@Override
	public Object get(Object key) {
		if (!isAllowDuplicate() && key instanceof String) {
			key = ("" +key).toLowerCase();
		}
		return super.get(key);
	}
}
