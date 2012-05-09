package de.uni.kassel.peermessage.json;

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uni.kassel.peermessage.Entity;
import de.uni.kassel.peermessage.EntityList;
import de.uni.kassel.peermessage.EntityUtil;
import de.uni.kassel.peermessage.Tokener;

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
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
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
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small> prefix.</li>
 * </ul>
 * 
 * @author JSON.org
 * @version 2010-12-28
 */
public class JsonArray extends EntityList{

	/**
	 * Construct an empty JSONArray.
	 */
	public JsonArray() {
	}

	/**
	 * Construct a JSONArray from a JSONTokener.
	 * 
	 * @param x
	 *            A JSONTokener
	 * @throws RuntimeException
	 *             If there is a syntax error.
	 */
	public JsonArray(Tokener x) throws RuntimeException {
		this();
		if (x.nextClean() != '[') {
			throw x.syntaxError("A JSONArray text must start with '['");
		}
		if (x.nextClean() != ']') {
			x.back();
			for (;;) {
				if (x.nextClean() == ',') {
					x.back();
					put(null);
				} else {
					x.back();
					put(x.nextValue());
				}
				switch (x.nextClean()) {
				case ';':
				case ',':
					if (x.nextClean() == ']') {
						return;
					}
					x.back();
					break;
				case ']':
					return;
				default:
					throw x.syntaxError("Expected a ',' or ']'");
				}
			}
		}
	}

	/**
	 * Construct a JSONArray from a source JSON text.
	 * 
	 * @param source
	 *            A string that begins with <code>[</code>&nbsp;<small>(left
	 *            bracket)</small> and ends with <code>]</code>
	 *            &nbsp;<small>(right bracket)</small>.
	 * @throws RuntimeException
	 *             If there is a syntax error.
	 */
	public JsonArray(String source) throws RuntimeException {
		this(new Tokener(source));
	}

	/**
	 * Construct a JSONArray from a Collection.
	 * 
	 * @param collection
	 *            A Collection.
	 */
	public JsonArray(Collection<?> collection) {
		if (collection != null) {
			getElements();
			Iterator<?> iter = collection.iterator();
			while (iter.hasNext()) {
				put(EntityUtil.wrap(iter.next(), this));
			}
		}
	}



	/**
	 * Get the JSONArray associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A JSONArray value.
	 * @throws RuntimeException
	 *             If there is no value for the index. or if the value is not a
	 *             JSONArray
	 */
	public JsonArray getJSONArray(int index) throws RuntimeException {
		Object object = get(index);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		}
		throw new RuntimeException("JSONArray[" + index
				+ "] is not a JSONArray.");
	}

	/**
	 * Get the JSONObject associated with an index.
	 * 
	 * @param index
	 *            subscript
	 * @return A JSONObject value.
	 * @throws RuntimeException
	 *             If there is no value for the index or if the value is not a
	 *             JSONObject
	 */
	public JsonObject getJSONObject(int index) throws RuntimeException {
		Object object = get(index);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		}
		throw new RuntimeException("JSONArray[" + index
				+ "] is not a JSONObject.");
	}




	/**
	 * Put a value in the JSONArray, where the value will be a JSONArray which
	 * is produced from a Collection.
	 * 
	 * @param value
	 *            A Collection value.
	 * @return this.
	 */
	public JsonArray put(Collection<?> value) {
		put(new JsonArray(value));
		return this;
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONArray which
	 * is produced from a Collection.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 * @throws RuntimeException
	 *             If the index is negative or if the value is not finite.
	 */
	public JsonArray put(int index, Collection<?> value)
			throws RuntimeException {
		put(index, new JsonArray(value));
		return this;
	}



	/**
	 * Produce a JSONObject by combining a JSONArray of names with the values of
	 * this JSONArray.
	 * 
	 * @param names
	 *            A JSONArray containing a list of key strings. These will be
	 *            paired with the values.
	 * @return A JSONObject, or null if there are no names or if this JSONArray
	 *         has no values.
	 * @throws RuntimeException
	 *             If any of the names are null.
	 */
	public JsonObject toJSONObject(JsonArray names) throws RuntimeException {
		if (names == null || names.length() == 0 || length() == 0) {
			return null;
		}
		JsonObject jo = new JsonObject();
		for (int i = 0; i < names.length(); i += 1) {
			jo.put(names.getString(i), this.get(i));
		}
		return jo;
	}

	/**
	 * Make a JSON text of this JSONArray. For compactness, no unnecessary
	 * whitespace is added. If it is not possible to produce a syntactically
	 * correct JSON text then null will be returned instead. This could occur if
	 * the array contains an invalid number.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return a printable, displayable, transmittable representation of the
	 *         array.
	 */
	public String toString() {
		try {
			return '[' + join(",") + ']';
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Make a prettyprinted JSON text of this JSONArray. Warning: This method
	 * assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>[</code>&nbsp;<small>(left
	 *         bracket)</small> and ending with <code>]</code>
	 *         &nbsp;<small>(right bracket)</small>.
	 * @throws RuntimeException
	 */
	@Override
	public String toString(int indentFactor) throws RuntimeException {
		return toString(indentFactor, 0);
	}
	public String toString(int indentFactor, int indent) {
		List<Object> elements = getElements();
		int len = elements.size();
		if (len == 0) {
			return "[]";
		}
		int i;
		StringBuffer sb = new StringBuffer("[");
		if (len == 1) {
			sb.append(EntityUtil.valueToString(elements.get(0),
					indentFactor, indent, false, this));
		} else {
			int newindent = indent + indentFactor;
            sb.append('\n');
            for (i = 0; i < len; i += 1) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; j += 1) {
                    sb.append(' ');
                }
                sb.append(EntityUtil.valueToString(elements.get(i),
						indentFactor, newindent, false, this));
            }
			sb.append('\n');
			for (i = 0; i < indent; i += 1) {
                sb.append(' ');
            }
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Write the contents of the JSONArray as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return The writer.
	 * @throws RuntimeException
	 */
	public Writer write(Writer writer) throws RuntimeException {
		try {
			List<Object> elements = getElements();
			boolean b = false;
			int len = elements.size();

			writer.write('[');

			for (int i = 0; i < len; i += 1) {
				if (b) {
					writer.write(',');
				}
				Object v = elements.get(i);
				if (v instanceof JsonObject) {
					((JsonObject) v).write(writer);
				} else if (v instanceof JsonArray) {
					((JsonArray) v).write(writer);
				} else {
					writer.write(EntityUtil.valueToString(v, this));
				}
				b = true;
			}
			writer.write(']');
			return writer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Entity getNewObject() {
		return new JsonObject();
	}

	@Override
	public EntityList getNewArray() {
		return new JsonArray();
	}
}