package de.uniks.networkparser.json;

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
import java.util.Iterator;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SortedList;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is <code>,</code>
 * &nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 *
 * 
 * <code>{} [ ] / \ : , = ; #</code> and if they do not look like numbers and if
 * they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small> prefix.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2010-12-28
 */

public class JsonArray extends SortedList<Object> implements EntityList {
	public static final char START = '[';
	public static final char END = ']';

	/**
	 * Default Constructor
	 */
	public JsonArray() {
		super(false);
	}

	/**
	 * Get the JSONArray associated with an index.
	 *
	 * @param index The index must be between 0 and length() - 1.
	 * @return A JSONArray value.
	 */
	public JsonArray getJSONArray(int index) {
		Object object = get(index);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		}
		JsonArray returnValue = new JsonArray();
		if (object != null) {
			returnValue.add(object);
		}
		return returnValue;
	}

	@Override
	public int sizeChildren() {
		return super.size();
	}

	/**
	 * Get the JSONObject associated with an index.
	 *
	 * @param index subscript
	 * @return A JSONObject value.
	 * @throws RuntimeException If there is no value for the index or if the value
	 *                          is not a JSONObject
	 */
	public JsonObject getJSONObject(int index) {
		Object object = get(index);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		} else if (object instanceof String) {
			return new JsonObject().withValue("" + object);
		}
		throw new RuntimeException("JSONArray[" + index + "] is not a JSONObject.");
	}

	/**
	 * Get the JSONObject associated with an index.
	 *
	 * @param index subscript
	 * @return A JSONObject value.
	 * @throws RuntimeException If there is no value for the index or if the value
	 *                          is not a JSONObject
	 */
	public String getString(int index) {
		if(index <0 || index > size()) {
			return "";
		}
		Object object = get(index);
		if (object instanceof String) {
			return (String) object;
		}
		throw new RuntimeException("JSONArray[" + index + "] is not a String.");
	}

	/**
	 * Produce a JSONObject by combining a JSONArray of names with the values of
	 * this JSONArray.
	 *
	 * @param names A JSONArray containing a list of key strings. These will be
	 *              paired with the values.
	 * @return A JSONObject, or null if there are no names or if this JSONArray has
	 *         no values.
	 */
	public JsonObject toJSONObject(JsonArray names) {
		if (names == null || names.size() == 0 || size() == 0) {
			return null;
		}
		JsonObject jo = new JsonObject();
		for (int i = 0; i < names.size(); i += 1) {
			jo.put("" + names.getKeyByIndex(i), this.get(i));
		}
		return jo;
	}

	/**
	 * Make a JSON text of this JSONArray. For compactness, no unnecessary
	 * whitespace is added. If it is not possible to produce a syntactically correct
	 * JSON text then null will be returned instead. This could occur if the array
	 * contains an invalid number.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return a printable, displayable, transmittable representation of the array.
	 */
	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	/**
	 * Make a prettyprinted JSON text of this JSONArray.
	 * 
	 * @param converter Factor for spacing between Level
	 * @return return Item As String
	 */
	@Override
	protected String parseItem(EntityStringConverter converter) {
		Iterator<Object> iterator = iterator();
		if (!iterator.hasNext()) {
			return "[]";
		}

		if (!isVisible()) {
			return "[" + size() + " Items]";
		}
		// First Element
		converter.add();
		StringBuilder sb = new StringBuilder().append(START).append(converter.getPrefix());
		Object element = iterator.next();
		sb.append(EntityUtil.valueToString(element, false, this, converter));
		while (iterator.hasNext()) {
			element = iterator.next();
			sb.append(",");
			sb.append(converter.getPrefix());
			sb.append(EntityUtil.valueToString(element, false, this, converter));
		}
		converter.minus();
		sb.append(converter.getPrefix());
		sb.append(END);
		return sb.toString();
	}

	/**
	 * JSONArray from a source JSON text.
	 *
	 * @param value A string that begins with <code>[</code>&nbsp;<small>(left
	 *              bracket)</small> and ends with <code>]</code>&nbsp;<small>(right
	 *              bracket)</small>.
	 * @return Itself
	 */
	public JsonArray withValue(String value) {
		clear();
		JsonTokener tokener = new JsonTokener();
		CharacterBuffer buffer = new CharacterBuffer().with(value);
		tokener.parseToEntity(this, buffer);
		return this;
	}

	/**
	 * Set the value to Tokener or pairs of values
	 *
	 * @param values a simple String of Value or pairs of key-values
	 * @return Itself
	 */
	public JsonArray withValue(Buffer values) {
		new JsonTokener().parseToEntity(this, values);
		return this;
	}

	/**
	 * JSONArray from a BaseEntityArray.
	 *
	 * @param values of Elements.
	 * @return Itself
	 */
	public JsonArray withValue(BaseItem... values) {
		for (int i = 0; i < values.length; i++) {
			add(EntityUtil.wrap(values[i], this));
		}
		return this;
	}

	public JsonObject get(String id) {
		for (Object item : this) {
			if (item instanceof JsonObject) {
				JsonObject json = (JsonObject) item;
				if (json.has(IdMap.ID) && json.getString(IdMap.ID).equals(id)) {
					return json;
				}
			}
		}
		return null;
	}

	/**
	 * Get a new Instance of a JsonObject
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new JsonObject();
		}
		return new JsonArray();
	}

	@Override
	public boolean remove(Object value) {
		return removeByObject(value) >= 0;
	}

	@Override
	public JsonArray subList(int fromIndex, int toIndex) {
		return (JsonArray) super.subList(fromIndex, toIndex);
	}

	public static JsonArray create(String value) {
		return new JsonArray().withValue(value);
	}
}
