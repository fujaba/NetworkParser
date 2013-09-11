package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.Collection;
import java.util.Iterator;
import de.uniks.networkparser.EntityList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.JISMEntity;
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

public class JsonArray extends EntityList {
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
		if (names == null || names.size() == 0 || size() == 0) {
			return null;
		}
		JsonObject jo = new JsonObject();
		for (int i = 0; i < names.size(); i += 1) {
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
	@Override
	public String toString() {
		try {
			if (!isVisible()) {
				return "[" + size() + " Items]";
			}
			return '[' + join(",") + ']';
		} catch (Exception e) {
			return "";
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
	 *             the runtime exception
	 */
	@Override
	public String toString(int indentFactor) throws RuntimeException {
		return toString(indentFactor, 0);
	}

	/**
	 * Make a prettyprinted JSON text of this JSONArray.
	 */
	@Override
	public String toString(int indentFactor, int indent) {
		Iterator<Object> iterator = iterator();
		if (!iterator.hasNext()) {
			return "[]";
		}

		if (!isVisible()) {
			return "[" + size() + " Items]";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indentFactor; i++) {
			sb.append(' ');
		}
		String step = sb.toString();
		String prefix = "";
		int newindent = indent + indentFactor;
		if (newindent > 0) {
			sb = new StringBuilder();
			for (int i = 0; i < indent; i += indentFactor) {
				sb.append(step);
			}
			prefix = CRLF + sb.toString();
		}
		// First Element

		sb = new StringBuilder("[" + prefix + step);
		Object element = iterator.next();
		sb.append(EntityUtil.valueToString(element, indentFactor, newindent,
				false, this));

		while (iterator.hasNext()) {
			element = iterator.next();
			sb.append("," + prefix + step);
			sb.append(EntityUtil.valueToString(element, indentFactor,
					newindent, false, this));
		}
		sb.append(prefix + ']');
		return sb.toString();
	}

	/**
	 * JSONArray from a source JSON text.
	 * 
	 * @param source
	 *            A string that begins with <code>[</code>&nbsp;<small>(left
	 *            bracket)</small> and ends with <code>]</code>
	 *            &nbsp;<small>(right bracket)</small>.
	 * @throws RuntimeException
	 *             If there is a syntax error.
	 */
	public JsonArray withValue(String value) {
		clear();
		new JsonTokener().withText(value).parseToEntity(this);
		return this;
	}
	
	/**
	 * JSONArray from a JSONTokener.
	 * 
	 * @param x
	 *            A JSONTokener
	 * @throws RuntimeException
	 *             If there is a syntax error.
	 */
	public JsonArray withValue(Tokener x) throws RuntimeException {
		x.parseToEntity(this);
		return this;
	}

	/**
	 * JSONArray from a Collection.
	 * 
	 * @param collection
	 *            A Collection.
	 */
	public JsonArray withValue(Collection<?> collection) {
		if (collection != null) {
			Iterator<?> iter = collection.iterator();
			while (iter.hasNext()) {
				put(EntityUtil.wrap(iter.next(), this));
			}
		}
		return this;
	}

	/**
	 * JSONArray from a BaseEntityArray.
	 * 
	 * @param Array
	 *            of Elements.
	 */
	public JsonArray withValue(JISMEntity... values) {
		for (int i = 0; i < values.length; i++) {
			put(EntityUtil.wrap(values[i], this));
		}
		return this;
	}
	
	/**
	 * Get a new Instance of a JsonObject
	 */
	@Override
	public JsonObject getNewObject() {
		return new JsonObject();
	}

	/**
	 * Get a new Instance of a JsonArray
	 */
	@Override
	public JsonArray getNewArray() {
		return new JsonArray();
	}
}
