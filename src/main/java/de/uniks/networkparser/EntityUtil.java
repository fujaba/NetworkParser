package de.uniks.networkparser;

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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class EntityUtil {
	private static final String HEXVAL = "0123456789abcdef";
	public static final String NON_FILE_CHARSSIMPLE = "[\\\\/\\:\\;\\*\\?\"<>\\|!&', \u001F\u0084\u0093\u0094\u0096\u2013\u201E\u201C\u03B1 ]";

	/**
	 * Produce a string from a double. The string "null" will be returned if the
	 * number is not finite.
	 *
	 * @param d
	 *			A double.
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
	 *			A Number
	 * @return A String.
	 * @throws IllegalArgumentException
	 *			 If n is a non-finite number.
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

	public static String unQuote(CharSequence value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(value.length());
		char c;
		int i = 0;
		int len = value.length();
		if(value.charAt(0)=='\"'){
			i++;
			len--;
		}
		for (; i < len; i++) {
			c = value.charAt(i);
			if (c == '\\') {
				if (i + 1 == len) {
					sb.append('\\');
					break;
				}
				c = value.charAt(++i);
				if (c == 'u') {
					char no = fromHex(value.charAt(++i), value.charAt(++i),
							value.charAt(++i), value.charAt(++i));
					sb.append((char) no);
					continue;
				} else if (c == '"' || c == '\\') {
					// remove the backslash
				} else {
					sb.append('\\');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

   public static String basicUnQuote(String value) {
	  if (value == null || value.length() == 0) {
		 return "";
	  }
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
			   //			} else if (c == '"') {
			   //			   // remove the backslash
			   //			} else {
			   //			   sb.append('\\');
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
	 *			A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
//		char b = 0, c;
		char c;
		String hhhh;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			if (c == '\\') {
				sb.append("\\\\");
				continue;
			}
			if (c == '"' ) {
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
//			b = c;
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
	 *			The value to be serialized.
	 * @param indentFactor
	 *			The number of spaces to add to each level of indentation.
	 * @param intent
	 *			The indentation of the top level.
	 * @param simpleText
	 *			Boolean for switch between text and Escaped-Text
	 * @param reference
	 *			A Reference Object to generate new Objects like Factory
	 *			Pattern
	 * @return a printable, displayable, transmittable representation of the
	 *		 object, beginning with <code>{</code>&nbsp;<small>(left
	 *		 brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *		 brace)</small>.
	 */
	public static String valueToString(Object value, int indentFactor,
			int intent, boolean simpleText, BaseItem reference) {
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
			BaseItem item = reference.getNewList(true).withAll((Map<?, ?>) value);
			if (item instanceof StringItem) {
				return ((StringItem) item).toString(indentFactor, intent);
			}
			return ((StringItem) item).toString();
		}
		if (value instanceof Collection) {
			BaseItem item = reference.getNewList(true);
			if(item instanceof SimpleKeyValueList<?,?>) {
				return ((SimpleKeyValueList<?,?>) item).withList((Map<?, ?>) value).toString();
			}
			if (item instanceof StringItem) {
				return ((StringItem) item).toString(indentFactor, intent);
			}
			return ((StringItem) item).toString();
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			BaseItem item = reference.getNewList(false);
			for (Object entity : items) {
				item.withAll(entity);
			}
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
			BaseItem reference) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof AbstractArray<?>) {
			return ((AbstractArray<?>) value).toString();
		}
		if (value instanceof Collection) {
			return reference.getNewList(false).withAll(
			(Collection<?>) value).toString();
		}
		if (value.getClass().isArray()) {
			return reference.getNewList(false).withAll(
			(Map<?, ?>) value).toString();
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
	 *			The object to wrap
	 * @param reference
	 *			The reference
	 * @return The wrapped value
	 */
	public static Object wrap(Object object, BaseItem reference) {
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
				return ((AbstractList<?>) reference.getNewList(false))
						.withList((Collection<?>) object);
			}
			if (object.getClass().isArray()) {
				return ((AbstractList<?>) reference.getNewList(false))
						.withList((Collection<?>) object);
			}
			if (object instanceof Map) {
				return ((SimpleKeyValueList<?, ?>) reference.getNewList(false))
						.withList((Map<?, ?>) object);
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
	 *			Char
	 * @param repeat
	 *			Number of Repeat
	 * @return a String
	 */
	public static String repeat(char ch, int repeat) {
		if(repeat<0) {
			return null;
		}
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}
	
	
	/**
	 * format a String with 0
	 *
	 * @param value
	 *			the numericvalue
	 * @param length
	 *			the length of Value
	 * @return a String of Value
	 */
	public static String strZero(int value, int length) {
		return strZero(String.valueOf(value), length, -1);
	}
	
	/**
	 * format a String with 0
	 *
	 * @param value
	 *			the numericvalue
	 * @param length
	 *			the length of Value
	 * @return a String of Value
	 */
	public static String strZero(long value, int length) {
		return strZero(String.valueOf(value), length, -1);
	}
	
	/**
	 * format a String with 0
	 *
	 * @param value
	 *			the numericvalue
	 * @param length
	 *			the length of Value
	 * @param max
	 *			the maxValue
	 * @return a String of Value
	 */
	public static String strZero(long value, int length, int max) {
		return strZero(String.valueOf(value), length, max);
	}
	
	/**
	 * Format a date with 0
	 *
	 * @param value
	 *			the numericvalue
	 * @param length
	 *			the length of Value
	 * @param max
	 *			the maxValue
	 * @return a String of Value with max value
	 */
	public static String strZero(int value, int length, int max) {
		return strZero(String.valueOf(value), length, max);
	}
	
	public static String strZero(String value, int length, int max) {
		if(max>0 && max<length) {
			length = max;
		}
		StringBuilder sb=new StringBuilder();
		if(length>value.length()) {
			sb.ensureCapacity(length);
			max = length - value.length();
			while(max>0) {
				sb.append("0");
				max--;
			}
		}
		sb.append(value);
		return sb.toString();
	}
	
	public static String getValidChars(String source, int maxLen) {
		int i = source.length()-1;
		StringBuilder sb=new StringBuilder();
		if(i>0){
			while (' '==source.charAt(i) || '.'==source.charAt(i)){
				i--;
			}
		}
		i++;
		if(maxLen>0 && i > maxLen) {
			String search = source.substring(0, maxLen);
			int lastSpace = search.lastIndexOf(" ");
			int lastComma = search.lastIndexOf(",");
			if(lastSpace > lastComma ) {
				i = lastSpace;
			}else if(lastComma > lastSpace) {
				i = lastComma;
			}
		}
		
		for(int k=0;k<i;k++) {
			char charAt = source.charAt(k);
			if(NON_FILE_CHARSSIMPLE.indexOf(charAt)<0 && charAt<55000) {
				sb.append(charAt);
			}
		}
		return sb.toString();
	}

	public static boolean compareEntity(Entity entityA, Entity entityB) {
		return compareEntity(entityA, entityB, null);
	}
	public static boolean compareEntity(Entity entityA, Entity entityB, Entity sameObject) {
		if(entityB == null) {
			return entityA == null;
		}
		for(int i=entityA.size()- 1 ;i>=0;i--) {
			String key = entityA.getKeyByIndex(i);
			Object valueA = entityA.get(key);
			Object valueB = entityB.get(key);
			if(valueA == null) {
				if(valueB == null) {
					Object oldValue = entityA.get(key);
					if(sameObject != null) {
						sameObject.withAll(key, oldValue);
					}
					entityA.without(key);
					entityB.without(key);
				}
				continue;
			}
			Object oldValue = compareValue(valueA, valueB);
			if(oldValue != null) {
				if(sameObject != null) {
					sameObject.withAll(key, oldValue);
				}
				entityA.without(key);
				entityB.without(key);
			}
		}
		boolean isSamesize = entityA.size()<1 && entityB.size()<1;
		if(entityA instanceof XMLEntity && entityB instanceof XMLEntity) {
			XMLEntity xmlA = (XMLEntity) entityA;
			XMLEntity xmlB = (XMLEntity) entityB;
			compareEntity(xmlA.getChildren(), xmlB.getChildren());
			return isSamesize && xmlA.getTag().equals(xmlB.getTag());
		}
		return isSamesize;
	}

	public static boolean compareEntity(List<?> jsonA, List<?> jsonB) {
		return compareEntity(jsonA, jsonB, null);
	}

	public static boolean compareEntity(List<?> jsonA, List<?> jsonB, BaseItem sameList) {
		if(jsonB == null) {
			return jsonA == null;
		}
		for(int i=jsonA.size() - 1;i>=0;i--) {
			Object valueA = jsonA.get(i);
			if(jsonB.size()<i) {
				continue;
			}
			Object valueB = jsonB.get(i);
			Object oldValue = compareValue(valueA, valueB); 
			if(oldValue != null) {
				jsonA.remove(i);
				if(sameList != null) {
					sameList.withAll(oldValue);
				}
				jsonB.remove(i);
			}
		}
		return jsonA.size()<1 && jsonB.size()<1;
	}
	
	static Object compareValue(Object valueA, Object valueB) {
		if(valueA instanceof Entity && valueB instanceof Entity) {
			Entity entityA = (Entity)valueA;
			Entity newKeyValue = (Entity) entityA.getNewList(true);
			if(compareEntity(entityA, (Entity)valueB, newKeyValue)) {
				return newKeyValue;
			}
			return null;
		} else if(valueA instanceof BaseItem && valueB instanceof List<?>) {
			BaseItem sameList = ((BaseItem)valueA).getNewList(false);
			if(compareEntity((List<?>)valueA, (List<?>)valueB, sameList)) {
				return sameList;
			}
			return null;
		}
		if(valueA.equals(valueB)) {
			return valueA;
		}
		return null;
	}

	public static final String emfTypes = " EOBJECT EBIG_DECIMAL EBOOLEAN EBYTE EBYTE_ARRAY ECHAR EDATE EDOUBLE EFLOAT EINT EINTEGER ELONG EMAP ERESOURCE ESHORT ESTRING ";

	public static boolean isEMFType(String tag) {
		return emfTypes.indexOf(" " + tag.toUpperCase() + " ") >= 0;
	}

	public static boolean isPrimitiveType(String type) {
		String primitiveTypes = " void String long Long int Integer char Char boolean Boolean byte Byte float Float double Double Object java.util.Date ";

		if (type == null)
			return false;

		return primitiveTypes.indexOf(" " + type + " ") >= 0;
	}
	
	public static String convertPrimitiveToObjectType(String type) {
		int pos = transferMap.indexOf(type);
		if(pos<0) {
			return type;
		}
		return transferMap.getValueByIndex(pos);
	}

	public static final String javaKeyWords = " abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for if goto implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while ";
	private static SimpleKeyValueList<String, String> transferMap =  new SimpleKeyValueList<String, String>()
																		.withKeyValue("long", "Long")
																		.withKeyValue("int", "Integer")
																		.withKeyValue("char", "Character")
																		.withKeyValue("boolean", "Boolean")
																		.withKeyValue("byte", "Byte")
																		.withKeyValue("float", "Float")
																		.withKeyValue("double", "Double");

	public static String toValidJavaId(String tag) {
		if (javaKeyWords.indexOf(" " + tag + " ") >= 0) {
			tag = "_" + tag;
		}

		return tag;
	}

	public static String getId(String name) {
		if (name.indexOf("/") >= 0) {
			return name.substring(name.lastIndexOf("/") + 1);
		}
		if (name.indexOf("#") >= 0) {
			return name.substring(name.indexOf("#") + 2);
		}
		return name;
	}

	public static String shortClassName(String name) {
		int pos = name.lastIndexOf('.');
		name = name.substring(pos + 1);
		pos = name.lastIndexOf('$');
		if (pos >= 0) {
			name = name.substring(pos + 1);
		}
		return name;
	}
	
	public static String upFirstChar(String name) {
		if(name == null || name.length()<1) {
			return name;
		}
		return name.substring(0, 1).toUpperCase()+name.substring(1);
	}
}
