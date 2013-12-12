package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or � as soon they
 will be approved by the European Commission - subsequent
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
import de.uniks.networkparser.interfaces.BaseEntity;

public class EntityUtil {
	/**
	 * Produce a string from a Number.
	 * 
	 * @param number
	 *            A Number
	 * @return A String.
	 * @throws IllegalArgumentException
	 *             If n is a non-finite number.
	 */
	public static String valueToString(Number number) throws IllegalArgumentException{
		if (number == null) {
			throw new IllegalArgumentException("Null pointer");
		}
		testValidity(number);

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

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, producing <\/,
	 * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
	 * contain a control character or an unescaped quote or backslash.
	 * 
	 * @param string
	 *            A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
						|| (c >= '\u2000' && c < '\u2100')) {
					hhhh = "000" + Integer.toHexString(c);
					sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * Try to convert a string into a number, boolean, or null. If the string
	 * can't be converted, return the string.
	 * 
	 * @param string
	 *            A String.
	 * @return A simple JSON value.
	 */
	public static Object stringToValue(String string) {
		if (string.equals("")) {
			return string;
		}
		if (string.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		if (string.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		if (string.equalsIgnoreCase("null")) {
			return null;
		}

		/*
		 * If it might be a number, try converting it. If a number cannot be
		 * produced, then the value will just be a string. Note that the plus
		 * and implied string conventions are non-standard. A JSON parser may
		 * accept non-JSON forms as long as it accepts all correct JSON forms.
		 */
		Double d;
		char b = string.charAt(0);
		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
			try {
				if (string.indexOf('.') > -1 || string.indexOf('e') > -1
						|| string.indexOf('E') > -1) {
					d = Double.valueOf(string);
					if (!d.isInfinite() && !d.isNaN()) {
						return d;
					}
				} else {
					Long myLong = Long.valueOf(string);
					if (myLong.longValue() == myLong.intValue()) {
						return Integer.valueOf(myLong.intValue());
					}
					return myLong;
				}
			} catch (Exception ignore) {
				// DO nothing
			}
		}
		return string;
	}

	/**
	 * Throw an exception if the object is a NaN or infinite number.
	 * 
	 * @param o
	 *            The object to test.
	 * @throws RuntimeException
	 *             If o is a non-finite number.
	 */
	public static void testValidity(Object o) {
		if (o != null) {
			if (o instanceof Double) {
				if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
					throw new RuntimeException(
							"JSON does not allow non-finite numbers.");
				}
			} else if (o instanceof Float) {
				if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
					throw new RuntimeException(
							"JSON does not allow non-finite numbers.");
				}
			}
		}
	}

	/**
	 * Make a JSON text of an Object value. If the object has an
	 * value.toJSONString() method, then that method will be used to produce the
	 * JSON text. The method is required to produce a strictly conforming text.
	 * If the object does not contain a toJSONString method (which is the most
	 * common case), then a text will be produced by other means. If the value
	 * is an array or Collection, then a JsonArray will be made from it and its
	 * toJSONString method will be called. If the value is a MAP, then a
	 * JsonObject will be made from it and its toJSONString method will be
	 * called. Otherwise, the value's toString method will be called, and the
	 * result will be quoted.
	 * 
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param value
	 *            The value to be serialized.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public static String valueToString(Object value, BaseEntity reference) {
		return valueToString(value, false, reference);
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
	 *            A Reference Object to generate new Objects like Factory Pattern
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public static String valueToString(Object value, int indentFactor,
			int intent, boolean simpleText, BaseEntity reference) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof Entity) {
			return ((Entity) value).toString(indentFactor, intent);
		}
		if (value instanceof EntityList) {
			return ((EntityList) value).toString(indentFactor, intent);
		}
		if (value instanceof Map) {
			Entity entity = (Entity) reference.getNewObject();
			entity.initWithMap(value);
			return entity.toString(indentFactor, intent);
		}
		if (value instanceof Collection) {
			return reference.getNewArray().initWithMap((Collection<?>) value)
					.toString(indentFactor, intent);
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object item : items) {
				arrayList.add(item);
			}
			return reference.getNewArray().initWithMap(arrayList)
					.toString(indentFactor, intent);
		}
		if (simpleText) {
			return value.toString();
		}
		return quote(value.toString());
	}

	public static String valueToString(Object value, boolean simpleText,
			BaseEntity reference) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof Entity) {
			return ((Entity) value).toString();
		}
		if (value instanceof EntityList) {
			return ((EntityList) value).toString();
		}
		if (value instanceof Map) {
			Entity entity = (Entity) reference.getNewObject();
			entity.initWithMap(value);
			return entity.toString();
		}
		if (value instanceof Collection) {
			return reference.getNewArray().initWithMap((Collection<?>) value)
					.toString();
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object item : items) {
				arrayList.add(item);
			}

			return reference.getNewArray().initWithMap(arrayList).toString();
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
	 * @return The wrapped value
	 */
	public static Object wrap(Object object, BaseEntity reference) {
		try {
			if (object == null) {
				return null;
			}

			if (object instanceof Entity || object instanceof EntityList
					|| object instanceof Byte || object instanceof Character
					|| object instanceof Short || object instanceof Integer
					|| object instanceof Long || object instanceof Boolean
					|| object instanceof Float || object instanceof Double
					|| object instanceof String) {
				return object;
			}

			if (object instanceof Collection) {
				return reference.getNewArray().initWithMap(
						(Collection<?>) object);
			}
			if (object.getClass().isArray()) {
				return reference.getNewArray().initWithMap(
						(Collection<?>) object);
			}
			if (object instanceof Map) {
				Entity entity = (Entity) reference.getNewObject();
				entity.initWithMap(object);
				return entity;
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
}
