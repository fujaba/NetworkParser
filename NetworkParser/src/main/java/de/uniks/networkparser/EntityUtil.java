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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class EntityUtil {
	private static final String HEXVAL = "0123456789abcdef";

	/**
	 * Produce a string from a double. The string "null" will be returned if the
	 * number is not finite.
	 *
	 * @param d
	 *            A double.
	 * @return A String.
	 */
	public static String doubleToString(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return "null";
		}
		// Shave off trailing zeros and decimal point, if possible.
		String string = Double.toString(d);
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0
				&& string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
		}
		return string;
	}

	/**
	 * Produce a string from a Number.
	 *
	 * @param number
	 *            A Number
	 * @return A String.
	 * @throws IllegalArgumentException
	 *             If n is a non-finite number.
	 */
	public static String valueToString(Number number)
			throws IllegalArgumentException {
		if (number == null) {
			throw new IllegalArgumentException("Null pointer");
		}
		// Shave off trailing zeros and decimal point, if possible.

		String string = number.toString();
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0
				&& string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
		}
		return string;
	}

	public static String unQuote(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		// FIXME STEFAN if (!value.startsWith(""")) {
		// return value;
		// }
		StringBuilder sb = new StringBuilder(value.length());
		char c;
		for (int i = 0; i < value.length(); i++) {
			c = value.charAt(i);
			if (c == '\\') {
				if (i + 1 == value.length()) {
					sb.append('\\');
					break;
				}
				c = value.charAt(++i);
				if (c == 'u') {
					char no = fromHex(value.charAt(++i), value.charAt(++i),
							value.charAt(++i), value.charAt(++i));
					sb.append((char) no);
					continue;
				} else if (c == '"') {
					// remove the backslash
				} else {
					sb.append('\\');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private static char fromHex(char... values) {
		return (char) ((HEXVAL.indexOf(values[0]) << 24)
				+ (HEXVAL.indexOf(values[1]) << 16)
				+ (HEXVAL.indexOf(values[2]) << 8) + HEXVAL.indexOf(values[3]));
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within &lt;/, producing
	 * &lt;\/, allowing JSON text to be delivered in HTML. In JSON text, a
	 * string cannot contain a control character or an unescaped quote or
	 * backslash.
	 *
	 * @param string
	 *            A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		char b = 0, c;
		String hhhh;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			if (c == '"' && b != '\\') {
				sb.append("\\\"");
				continue;
			}
			if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
					|| (c >= '\u2000' && c < '\u2100')) {
				hhhh = "000" + Integer.toHexString(c);
				sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
			} else {
				sb.append(c);
			}
			b = c;
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * Make a prettyprinted JSON text of an object value.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param value
	 *            The value to be serialized.
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @param intent
	 *            The indentation of the top level.
	 * @param simpleText
	 *            Boolean for switch between text and Escaped-Text
	 * @param reference
	 *            A Reference Object to generate new Objects like Factory
	 *            Pattern
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public static String valueToString(Object value, int indentFactor,
			int intent, boolean simpleText, FactoryEntity reference) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof StringItem) {
			return ((StringItem) value).toString(indentFactor, intent);
		}
		if (value instanceof Map) {
			BaseItem item = ((SimpleKeyValueList<?, ?>) reference
					.getNewArray()).with((Map<?, ?>) value);
			if (item instanceof StringItem) {
				return ((StringItem) item).toString(indentFactor, intent);
			}
			return ((StringItem) item).toString();
		}
		if (value instanceof Collection) {
			SimpleList<?> item = reference.getNewArray().with(
					(Collection<?>) value);
			if (item instanceof StringItem) {
				return ((StringItem) item).toString(indentFactor, intent);
			}
			return ((StringItem) item).toString();
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object item : items) {
				arrayList.add(item);
			}
			AbstractList<?> item = reference.getNewArray().with(arrayList);
			if (item instanceof StringItem) {
				return ((StringItem) item).toString(indentFactor, intent);
			}
			return ((StringItem) item).toString();
		}
		if (simpleText) {
			return value.toString();
		}
		return quote(value.toString());
	}

	public static String valueToString(Object value, boolean simpleText,
			FactoryEntity reference) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof AbstractList) {
			return ((AbstractList<?>) value).toString();
		}
		if (value instanceof Map) {
			return ((SimpleKeyValueList<?, ?>) reference.getNewArray()).with(
					(Map<?, ?>) value).toString();
		}
		if (value instanceof Collection) {
			return reference.getNewArray().with((Collection<?>) value)
					.toString();
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object item : items) {
				arrayList.add(item);
			}
			return ((AbstractList<?>) reference.getNewObject()).with(arrayList)
					.toString();
		}
		if (simpleText) {
			return value.toString();
		}
		return quote(value.toString());
	}

	/**
	 * Wrap an object, if necessary. If the object is null, return the NULL
	 * object. If it is an array or collection, wrap it in a JsonArray. If it is
	 * a map, wrap it in a JsonObject. If it is a standard property (Double,
	 * String, et al) then it is already wrapped. Otherwise, if it comes from
	 * one of the java packages, turn it into a string. And if it doesn't, try
	 * to wrap it in a JsonObject. If the wrapping fails, then null is returned.
	 *
	 * @param object
	 *            The object to wrap
	 * @param reference
	 *            The reference
	 * @return The wrapped value
	 */
	public static Object wrap(Object object, FactoryEntity reference) {
		try {
			if (object == null) {
				return null;
			}

			if (object instanceof AbstractArray || object instanceof Byte
					|| object instanceof Character || object instanceof Short
					|| object instanceof Integer || object instanceof Long
					|| object instanceof Boolean || object instanceof Float
					|| object instanceof Double || object instanceof String) {
				return object;
			}

			if (object instanceof Collection) {
				return ((AbstractList<?>) reference.getNewObject())
						.with((Collection<?>) object);
			}
			if (object.getClass().isArray()) {
				return ((AbstractList<?>) reference.getNewObject())
						.with((Collection<?>) object);
			}
			if (object instanceof Map) {
				return ((SimpleKeyValueList<?, ?>) reference.getNewObject())
						.with((Map<?, ?>) object);
			}
			if (object.getClass().getName().startsWith("java.")
					|| object.getClass().getName().startsWith("javax.")) {
				return object.toString();
			}
		} catch (Exception exception) {
			// DO Nothing
		}
		return null;
	}

	/**
	 * Repeat a Char and return a simple String
	 *
	 * @param ch
	 *            Char
	 * @param repeat
	 *            Number of Repeat
	 * @return a String
	 */
	public static String repeat(char ch, int repeat) {
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}
}
