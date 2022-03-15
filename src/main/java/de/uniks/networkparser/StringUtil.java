package de.uniks.networkparser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteConverterHex;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class StringUtil.
 *
 * @author Stefan
 */
public class StringUtil {
	
	/** The Constant CLASS. */
	public static final String CLASS = "class";
	
	/** The Constant NON_FILE_CHARSSIMPLE. */
	public static final String NON_FILE_CHARSSIMPLE = "[\\\\/\\:\\;\\*\\?\"<>\\|!&', \u001F\u0084\u0093\u0094\u0096\u2013\u201E\u201C\u03B1 ]";
	private static final String primitiveTypes = " java.util.Date void String char Char boolean Boolean byte Byte Object ";
	private static final String numericTypes = " long Long short Short int Integer byte Byte float Float double Double ";
	private static final String types = "         long    Long    short   Short   int     Integer byte    Byte    float   Float   double  Double  boolean Boolean char    Char ";
	private static final String javaLang = "java.lang.";
	private static final String modifier = " public protected private static abstract final native synchronized transient volatile strictfp default ";
	
	/** The Constant javaKeyWords. */
	public static final String javaKeyWords = " assert break case catch class const continue do else enum extends finally for goto if implements import instanceof interface native new package return super switch this throw throws try while true false null ";

	/**
	 * Pseudo-random number generator object for use with randomString(). The Random
	 * class is not considered to be cryptographically secure, so only use these
	 * random Strings for low to medium security applications.
	 */
	private static Random randGen;

	/**
	 * Produce a string from a double. The string "null" will be returned if the
	 * number is not finite.
	 *
	 * @param d a double.
	 * @return a String.
	 */
	public static final String doubleToString(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return "null";
		}
		/* Shave off trailing zeros and decimal point, if possible. */
		String string = Double.toString(d);
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
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
	 * Checks if is numeric.
	 *
	 * @param strNum the str num
	 * @return true, if is numeric
	 */
	public static final boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}


	/**
	 * Gets the integer.
	 *
	 * @param strNum the str num
	 * @return the integer
	 */
	public static final int getInteger(String strNum) {
		if (strNum == null) {
			return -1;
		}
		try {
			return Integer.parseInt(strNum);
		} catch (NumberFormatException e) {
		}
		return -1;
	}	
	
	/**
	 * Produce a string from a Number.
	 *
	 * @param number A Number
	 * @return A String.
	 */
	public static final String valueToString(Number number) {
		if (number == null) {
			return "";
		}
		/* Shave off trailing zeros and decimal point, if possible. */

		String string = number.toString();
		if (string.indexOf('.') >= 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
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
	 * Un quote.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static final String unQuote(CharSequence value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(value.length());
		char c;
		int i = 0;
		int len = value.length();
		if (value.charAt(0) == '\"') {
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
					sb.append((char) ByteConverterHex.fromHex(value, ++i, 4));
					i += 3;
					continue;
				} else if (c == '"' || c == '\\') {
					/* remove the backslash */
				} else {
					sb.append('\\');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Basic un quote.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static final String basicUnQuote(String value) {
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
					sb.append((char) ByteConverterHex.fromHex(value, ++i, 4));
					i += 3;
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the right
	 * places. A backslash will be inserted within &lt;/, producing &lt;\/, allowing
	 * JSON text to be delivered in HTML. In JSON text, a string cannot contain a
	 * control character or an unescaped quote or backslash.
	 *
	 * @param string A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static final String quote(CharSequence string) {
       if (string == null || string.length() == 0) {
           return "\"\"";
        }
	    CharacterBuffer sb = new CharacterBuffer().withBufferLength(string.length() + 4);
	    sb.with('"');
	    quoteInteral(sb, string);
	    sb.with('"');
	    return sb.toString();
	    
	}
	/**
	 * Quote String
	 * @param buffer Target Buffer
     * @param string A String
	 * @return A String correctly formatted for insertion quote-String
	 */
	public static final CharacterBuffer quoteInteral(CharacterBuffer buffer, CharSequence string) {
		if (string == null || string.length() == 0) {
		    return null;
		}
		int i;
		int len = string.length();
		if(buffer == null) {
		    buffer = new CharacterBuffer().withBufferLength(len+4);
		}
		char c;
		String hhhh;
		
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			if (c == '\\') {
			    buffer.with('\\').with('\\');
				continue;
			}
			if (c == '"') {
			    buffer.with('\\', '\"');
				continue;
			}
			if (c ==13) {
			    buffer.with('\\', 'r');
			}else if (c ==10) {
			    buffer.with('\\', 'n');
			}else if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
				hhhh = "000" + Integer.toHexString(c);
				buffer.with("\\u" + hhhh.substring(hhhh.length() - 4));
			} else {
			    buffer.with(c);
			}
		}
		return buffer;
	}

	/**
	 * Make a prettyprinted JSON text of an object value.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param value      The value to be serialized.
	 * @param simpleText Boolean for switch between text and Escaped-Text
	 * @param reference  A Reference Object to generate new Objects like Factory
	 *                   Pattern
	 * @param converter  The Converter to transform Item
	 * @return a printable, displayable, transmittable representation of the object,
	 *         beginning with <code>{</code>&nbsp;<small>(left brace)</small> and
	 *         ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 */
	public static final String valueToString(Object value, boolean simpleText, BaseItem reference,
			Converter converter) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return valueToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}

		if (value instanceof BaseItem) {
			return ((BaseItem) value).toString(converter);
		}
		if (value instanceof Map) {
			BaseItem item = reference.getNewList(true);
			item.add((Map<?, ?>) value);
			return item.toString(converter);
		}
		if (value instanceof Collection) {
			BaseItem item = reference.getNewList(false);
			if (item instanceof AbstractList<?>) {
				((AbstractList<?>) item).withList((Collection<?>) value).toString(converter);
			}
			return item.toString(converter);
		}
		if (value.getClass().getName().equals("[B")) {
			/* Its a ByteArray */
			return quote(new String((byte[]) value));
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			BaseItem item = reference.getNewList(false);
			if(item instanceof AbstractArray<?>) {
				((AbstractArray<?>)item).withFlag(SimpleList.ALLOWEMPTYVALUE);
			}
			for (Object entity : items) {
				item.add(entity);
			}
			return item.toString(converter);
		}
		if (simpleText) {
			return value.toString();
		}
		return quote(value.toString());
	}

	/**
	 * Wrap an object, if necessary. If the object is null, return the NULL object.
	 * If it is an array or collection, wrap it in a JsonArray. If it is a map, wrap
	 * it in a JsonObject. If it is a standard property (Double, String, et al) then
	 * it is already wrapped. Otherwise, if it comes from one of the java packages,
	 * turn it into a string. And if it doesn't, try to wrap it in a JsonObject. If
	 * the wrapping fails, then null is returned.
	 *
	 * @param object    The object to wrap
	 * @param reference The reference
	 * @return The wrapped value
	 */
	public static final Object wrap(Object object, BaseItem reference) {
		try {
			if (object == null) {
				return null;
			}

			if (object instanceof AbstractArray || object instanceof Byte || object instanceof Character
					|| object instanceof Short || object instanceof Integer || object instanceof Long
					|| object instanceof Boolean || object instanceof Float || object instanceof Double
					|| object instanceof String) {
				return object;
			}

			if (object instanceof Collection) {
				return ((AbstractList<?>) reference.getNewList(false)).withList((Collection<?>) object);
			}
			if (object.getClass().isArray()) {
				return ((AbstractList<?>) reference.getNewList(false)).withList((Collection<?>) object);
			}
			if (object instanceof Map) {
				return ((SimpleKeyValueList<?, ?>) reference.getNewList(false)).withMap((Map<?, ?>) object);
			}
			if (object.getClass().getName().startsWith("java.") || object.getClass().getName().startsWith("javax.")) {
				return object.toString();
			}
		} catch (Exception exception) {
			/* Do Nothing */
		}
		return null;
	}

	/**
	 * Repeat a Char and return a simple String.
	 *
	 * @param ch     Char
	 * @param repeat Number of Repeat
	 * @return a String
	 */
	public static final String repeat(char ch, int repeat) {
		if (repeat < 0) {
			return "";
		}
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}

	/**
	 * Safe String comparison.
	 *
	 * @param s1 first string
	 * @param s2 second string
	 * @return true if both parameters are null or equal
	 */
	public static final boolean stringEquals(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	/**
	 * format a String with 0.
	 *
	 * @param value  the numericvalue
	 * @param length the length of Value
	 * @return a String of Value
	 */
	public static final String strZero(int value, int length) {
		return strZero(String.valueOf(value), length, -1);
	}

	/**
	 * format a String with 0.
	 *
	 * @param value  the numericvalue
	 * @param length the length of Value
	 * @return a String of Value
	 */
	public static final String strZero(long value, int length) {
		return strZero(String.valueOf(value), length, -1);
	}

	/**
	 * format a String with 0.
	 *
	 * @param value  the numericvalue
	 * @param length the length of Value
	 * @param max    the maxValue
	 * @return a String of Value
	 */
	public static final String strZero(long value, int length, int max) {
		return strZero(String.valueOf(value), length, max);
	}

	/**
	 * Format a date with 0.
	 *
	 * @param value  the numericvalue
	 * @param length the length of Value
	 * @param max    the maxValue
	 * @return a String of Value with max value
	 */
	public static final String strZero(int value, int length, int max) {
		return strZero(String.valueOf(value), length, max);
	}

	/**
	 * Str zero.
	 *
	 * @param value the value
	 * @param length the length
	 * @param max the max
	 * @return the string
	 */
	public static final String strZero(String value, int length, int max) {
		if (max > 0 && max < length) {
			length = max;
		}
		if (value == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (length > value.length()) {
			sb.ensureCapacity(length);
			max = length - value.length();
			while (max > 0) {
				sb.append("0");
				max--;
			}
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * Gets the valid chars.
	 *
	 * @param source the source
	 * @param maxLen the max len
	 * @return the valid chars
	 */
	public static final String getValidChars(String source, int maxLen) {
		if (source == null) {
			return null;
		}
		int i = source.length() - 1;
		StringBuilder sb = new StringBuilder();
		if (i > 0) {
			while (' ' == source.charAt(i) || '.' == source.charAt(i)) {
				i--;
			}
		}
		i++;
		if (maxLen > 0 && i > maxLen) {
			String search = source.substring(0, maxLen);
			int lastSpace = search.lastIndexOf(" ");
			int lastComma = search.lastIndexOf(",");
			if (lastSpace > lastComma) {
				i = lastSpace;
			} else if (lastComma > lastSpace) {
				i = lastComma;
			}
		}

		for (int k = 0; k < i; k++) {
			char charAt = source.charAt(k);
			if (NON_FILE_CHARSSIMPLE.indexOf(charAt) < 0 && charAt < 55000) {
				sb.append(charAt);
			}
		}
		return sb.toString();
	}

	/**
	 * Gets the default value.
	 *
	 * @param datatype the datatype
	 * @return the default value
	 */
	public static final String getDefaultValue(String datatype) {
		if (StringUtil.isNumericType(datatype)) {
			if ("Long".equals(datatype)) {
				return "0L";
			} else if ("Float".equals(datatype)) {
				return "0f";
			} else if ("Double".equals(datatype)) {
				return "0d";
			}
			return "0";
		}
		if ("void".equals(datatype)) {
			return "void";
		} else if ("boolean".equalsIgnoreCase(datatype)) {
			return "false";
		} else if (datatype != null) {
			if (datatype.endsWith("[]")) {
				return datatype.substring(0, datatype.length() - 2);
			} else if (datatype.endsWith("...")) {
				return datatype.substring(0, datatype.length() - 3);
			}
		}
		return "null";
	}

	/**
	 * Checks if is valid java id.
	 *
	 * @param myRoleName the my role name
	 * @return true, if is valid java id
	 */
	public static boolean isValidJavaId(String myRoleName) {
		if (myRoleName == null || isModifier(myRoleName)) {
			return false;
		}
		if (myRoleName.endsWith(".") || myRoleName.startsWith(".")) {
			return false;
		}
		if (myRoleName.indexOf(' ') >= 0) {
			return false;
		}
		if (myRoleName.indexOf('.') >= 0) {
			for (String s : myRoleName.split("\\.")) {
				if (!isValidJavaId(s)) {
					return false;
				}
			}
			return true;
		}
		if (isPrimitiveType(myRoleName)) {
			return false;
		}
		if (javaKeyWords.indexOf(" " + myRoleName + " ") >= 0) {
			return false;
		}
		return true;
	}

	/**
	 * To valid java id.
	 *
	 * @param tag the tag
	 * @return the string
	 */
	public static final String toValidJavaId(String tag) {
		if (!isValidJavaId(tag)) {
			tag = "_" + tag;
		}
		return tag;
	}

	/**
	 * Checks if is modifier.
	 *
	 * @param type the type
	 * @return true, if is modifier
	 */
	public static final boolean isModifier(String type) {
		if (type == null) {
			return false;
		}
		return modifier.indexOf(type) >= 0;
	}

	/**
	 * Checks if is primitive type.
	 *
	 * @param type the type
	 * @return true, if is primitive type
	 */
	public static final boolean isPrimitiveType(String type) {
		if (type == null) {
			return false;
		}
		if (type.endsWith("...")) {
			type = type.substring(0, type.length() - 3);
		}
		if (type.startsWith(javaLang)) {
			type = " " + type.substring(javaLang.length()) + " ";
		} else {
			type = " " + type + " ";
		}

		return numericTypes.indexOf(type) >= 0 || primitiveTypes.indexOf(type) >= 0;
	}

	/**
	 * Gets the object type.
	 *
	 * @param type the type
	 * @return the object type
	 */
	public static final String getObjectType(String type) {
		int pos = types.indexOf(" " + type + " ") / 8;
		if (pos % 2 == 1 && pos > 0) {
			int posNew = (pos + 1) * 8;

			String newType = types.substring(posNew + 1, posNew + 8).trim();
			return newType;
		}
		return type;
	}

	/**
	 * Checks if is date.
	 *
	 * @param type the type
	 * @return true, if is date
	 */
	public static final boolean isDate(String type) {
		return primitiveTypes.indexOf(" " + type + " ") == 0;
	}

	/**
	 * Checks if is numeric type.
	 *
	 * @param type the type
	 * @return true, if is numeric type
	 */
	public static final boolean isNumericType(String type) {
		if (type == null)
			return false;
		if (type.startsWith(javaLang)) {
			type = type.substring(javaLang.length() + 1);
		}
		return numericTypes.indexOf(" " + type + " ") >= 0;
	}

	/**
	 * Checks if is numeric type container.
	 *
	 * @param typeA the type A
	 * @param typeB the type B
	 * @return true, if is numeric type container
	 */
	public static final boolean isNumericTypeContainer(String typeA, String typeB) {
		if (typeA == null || typeB == null) {
			return typeA == typeB;
		}
		if (typeA.startsWith(javaLang)) {
			typeA = typeA.substring(javaLang.length() + 1);
		}
		if (typeB.startsWith(javaLang)) {
			typeB = typeB.substring(javaLang.length() + 1);
		}
		int posA = types.indexOf(" " + typeA + " ");
		if (posA < 0) {
			return false;
		}
		posA = posA / 8;
		int posB = types.indexOf(" " + typeB + " ") / 8;
		if (posA % 2 == 1) {
			return posB - 1 == posA;
		}
		return posB + 1 == posA;
	}

	/**
	 * Convert primitive to object type.
	 *
	 * @param type the type
	 * @return the string
	 */
	public static final String convertPrimitiveToObjectType(String type) {
		int pos = transferMap.indexOf(type);
		if (pos < 0) {
			return type;
		}
		return transferMap.getValueByIndex(pos);
	}

	private static final SimpleKeyValueList<String, String> transferMap = new SimpleKeyValueList<String, String>()
			.withKeyValueString(
					"long:Long,int:Integer,char:Character,boolean:Boolean,byte:Byte,float:Float,double:Double",
					String.class);

	/**
	 * Gets the id.
	 *
	 * @param name the name
	 * @return the id
	 */
	public static final String getId(String name) {
		if (name == null) {
			return "";
		}
		if (name.lastIndexOf("/") >= 0) {
			return name.substring(name.lastIndexOf("/") + 1);
		}
		if (name.indexOf("#") >= 0) {
			return name.substring(name.indexOf("#") + 2);
		}
		return name;
	}

	/**
	 * Short class name.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static final String shortClassName(String name) {
		if (name == null) {
			return "";
		}
		int pos = name.lastIndexOf('.');
		name = name.substring(pos + 1);
		pos = name.lastIndexOf('$');
		if (pos >= 0) {
			name = name.substring(pos + 1);
		}
		return name;
	}

	/**
	 * Up first char.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static final String upFirstChar(String name) {
		if (name == null || name.length() < 1) {
			return name;
		}
		/* SpeedUp Change only if nessessary */
		int no = (int) name.charAt(0);
		if (no >= 'A' && no <= 'Z') {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * Down first char.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static final String downFirstChar(String name) {
		if (name == null || name.length() < 1) {
			return name;
		}
		/* SpeedUp Change only if nessessary */
		int no = (int) name.charAt(0);
		if (no >= 'a' && no <= 'z') {
			return name;
		}
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	/** Map to convert extended characters in html entities. */
	public static final String HTMLTOKEN = "Aacute:193,aacute:225,Acirc:194,acirc:226,acute:180,AElig:198,aelig:230,Agrave:192,agrave:224,alefsym:8501,Alpha:913,alpha:945,amp:38,and:8743,ang:8736,Aring:197,aring:229,asymp:8776,Atilde:195,atilde:227,Auml:196,auml:228,bdquo:8222,Beta:914,beta:946,brvbar:166,bull:8226,cap:8745,Ccedil:199,ccedil:231,cedil:184,cent:162,Chi:935,chi:967,circ:710,clubs:9827,cong:8773,copy:169,crarr:8629,cup:8746,curren:164,dagger:8224,Dagger:8225,darr:8595,dArr:8659,deg:176,Delta:916,delta:948,diams:9830,divide:247,Eacute:201,eacute:233,Ecirc:202,ecirc:234,Egrave:200,egrave:232,empty:8709,emsp:8195,ensp:8194,Epsilon:917,epsilon:949,equiv:8801,Eta:919,eta:951,ETH:208,eth:240,Euml:203,euml:235,euro:8364,exist:8707,fnof:402,forall:8704,frac12:189,frac14:188,frac34:190,frasl:8260,Gamma:915,gamma:947,ge:8805,harr:8596,hArr:8660,hearts:9829,hellip:8230,Iacute:205,iacute:237,Icirc:206,icirc:238,iexcl:161,Igrave:204,igrave:236,image:8465,infin:8734,int:8747,Iota:921,iota:953,iquest:191,isin:8712,Iuml:207,iuml:239,Kappa:922,kappa:954,Lambda:923,lambda:955,lang:9001,laquo:171,larr:8592,lArr:8656,lceil:8968,ldquo:8220,le:8804,lfloor:8970,lowast:8727,loz:9674,lrm:8206,lsaquo:8249,lsquo:8216,macr:175,mdash:8212,micro:181,middot:183,minus:8722,Mu:924,mu:956,nabla:8711,nbsp:160,ndash:8211,ne:8800,ni:8715,not:172,notin:8713,nsub:8836,Ntilde:209,ntilde:241,Nu:925,nu:957,Oacute:211,oacute:243,Ocirc:212,ocirc:244,OElig:338,oelig:339,Ograve:210,ograve:242,oline:8254,Omega:937,omega:969,Omicron:927,omicron:959,oplus:8853,or:8744,ordf:170,ordm:186,Oslash:216,oslash:248,Otilde:213,otilde:245,otimes:8855,Ouml:214,ouml:246,para:182,part:8706,permil:8240,perp:8869,Phi:934,phi:966,Pi:928,pi:960,piv:982,plusmn:177,pound:163,prime:8242,Prime:8243,prod:8719,prop:8733,Psi:936,psi:968,radic:8730,rang:9002,raquo:187,rarr:8594,rArr:8658,rceil:8969,rdquo:8221,real:8476,reg:174,rfloor:8971,Rho:929,rho:961,rlm:8207,rsaquo:8250,rsquo:8217,sbquo:8218,Scaron:352,scaron:353,sdot:8901,sect:167,shy:173,Sigma:931,sigma:963,sigmaf:962,sim:8764,spades:9824,sub:8834,sube:8838,sum:8721,sup1:185,sup2:178,sup3:179,sup:8835,supe:8839,szlig:223,Tau:932,tau:964,there4:8756,Theta:920,theta:952,thetasym:977,thinsp:8201,THORN:222,thorn:254,tilde:732,times:215,trade:8482,Uacute:218,uacute:250,uarr:8593,uArr:8657,Ucirc:219,ucirc:251,Ugrave:217,ugrave:249,uml:168,upsih:978,Upsilon:933,upsilon:965,Uuml:220,uuml:252,weierp:8472,Xi:926,xi:958,Yacute:221,yacute:253,yen:165,yuml:255,Yuml:376,Zeta:918,zeta:950,zwj:8205,zwnj:8204";
	private static SimpleKeyValueList<String, Integer> entities = new SimpleKeyValueList<String, Integer>()
			.withKeyValueString(HTMLTOKEN, Integer.class).with("lt", 60).with("gt", 62);

	/**
	 * Convert special and extended characters into HTML entitities.
	 *
	 * @param str input string
	 * @return formatted string
	 */
	public static String encode(String str) {
		if (str == null) {
			return "";
		}
		StringBuilder buf = new StringBuilder(); /* the output string buffer */
		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);
			String entity = entities.getKey(Integer.valueOf(ch)); /* get equivalent html entity */
			if (entity == null) { /* if entity has not been found */
				if (ch > 128) { /* check if is an extended character */
					buf.append("&#" + ((int) ch) + ";"); /* convert extended character */
				} else {
					buf.append(ch); /* append the character as is */
				}
			} else {
				buf.append("&" + entity + ";"); /* append the html entity */
			}
		}
		return buf.toString();
	}

	/**
	 * Convert HTML entities to special and extended unicode characters equivalents.
	 *
	 * @param str input string
	 * @return formatted string
	 */
	public static String decode(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);
			if (ch == '&') {
				int semi = str.indexOf(';', i + 1);
				if ((semi == -1) || ((semi - i) > 7)) {
					buf.append(ch);
					continue;
				}
				String entity = str.substring(i + 1, semi);
				Integer iso;
				if (entity.charAt(1) == ' ') {
					buf.append(ch);
					continue;
				}
				if (entity.charAt(0) == '#') {
					if (entity.charAt(1) == 'x') {
						iso = Integer.valueOf(entity.substring(2, entity.length()), 16);
					} else {
						iso = Integer.valueOf(entity.substring(1, entity.length()));
					}
				} else {
					iso = entities.get(entity);
				}
				if (iso == null) {
					buf.append(entity);
				} else {
					buf.append((char) (iso.intValue()));
				}
				i = semi;
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * Clone.
	 *
	 * @param entity the entity
	 * @return the byte[]
	 */
	public static final byte[] clone(byte[] entity) {
		if (entity == null) {
			return null;
		}
		byte[] result = new byte[entity.length];
		for (int i = 0; i < entity.length; i++) {
			result[i] = entity[i];
		}
		return result;
	}


	/**
	 * Convert String to ByteArray.
	 *
	 * @param string The String
	 * @return the ByteArray
	 */
	public static final byte[] getBytes(CharSequence string) {
		if (string == null) {
			return null;
		}
		int size = string.length();
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) string.charAt(i);
		}
		return bytes;
	}

	/**
	 * Counts how many times the substring appears in the larger string.
	 *
	 * @param str the CharSequence to check, may be null
	 * @param sub the substring to count, may be null
	 * @return the number of occurrences, 0 if either CharSequence is {@code null}
	 */
	public static final int countMatches(CharSequence str, CharSequence sub) {
		if (isEmpty(str) || isEmpty(sub)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		String string = str.toString();
		String subStr = sub.toString();
		while ((idx = string.indexOf(subStr, idx)) != -1) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	/**
	 * Checks if a CharSequence is empty ("") or null.
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return if the CharSequence is empty or null
	 */
	public static final boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * Checks if the CharSequence equals any character in the given set of
	 * characters.
	 *
	 * @param cs   the CharSequence to check
	 * @param strs the set of characters to check against
	 * @return true if equals any
	 */
	public static final boolean equalsAny(CharSequence cs, CharSequence[] strs) {
		if (strs == null) {
			return cs == null;
		}
		if (cs == null) {
			return strs == null;
		}

		for (int i = 0; i < strs.length; i++) {
			if (!cs.equals(strs[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the CharSequence contains any character in the given set of
	 * characters.
	 *
	 * @param cs          the CharSequence to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the {@code true} if any of the chars are found, {@code false} if no
	 *         match or null input
	 */
	public static final boolean containsAny(CharSequence cs, CharSequence searchChars) {
		if (searchChars == null) {
			return false;
		}
		return containsAny(cs, toCharArray(searchChars));
	}

	public static final boolean containsAny(char cs, char[] searchChars) {
	    for (int i = 0; i < searchChars.length; i++) {
	        if(searchChars[i]==cs) {
	            return true;
	        }
	    }
	    return false;
	}
	/**
	 * Checks if the CharSequence contains any character in the given set of
	 * characters.
	 *
	 * @param cs          the CharSequence to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the {@code true} if any of the chars are found, {@code false} if no
	 *         match or null input
	 */
	public static final boolean containsAny(CharSequence cs, char[] searchChars) {
		if (isEmpty(cs) || searchChars == null || searchChars.length == 0) {
			return false;
		}

		int csLength = cs.length();
		int searchLength = searchChars.length;
		int csLast = csLength - 1;
		int searchLast = searchLength - 1;
		for (int i = 0; i < csLength; i++) {
			char ch = cs.charAt(i);
			for (int j = 0; j < searchLength; j++) {
				if (searchChars[j] == ch) {
					if (Character.isHighSurrogate(ch)) {
						if (j == searchLast) {
							/* missing low surrogate, fine, like String.indexOf(String) */
							return true;
						}
						if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
							return true;
						}
					} else {
						/* ch is in the Basic Multilingual Plane */
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Green implementation of toCharArray.
	 *
	 * @param cs the {@code CharSequence} to be processed
	 * @return the resulting char array
	 */
	private static final char[] toCharArray(CharSequence cs) {
		if (cs instanceof String) {
			return ((String) cs).toCharArray();
		} else if (cs != null) {
			int sz = cs.length();
			char[] array = new char[cs.length()];
			for (int i = 0; i < sz; i++) {
				array[i] = cs.charAt(i);
			}
			return array;
		}
		return new char[0];
	}

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list twice
	 * so that there is a more equal chance that a number will be picked. We can use
	 * the array to get a random number or letter by picking a random array index.
	 */
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
			.toCharArray();

	/**
	 * Returns a random String of numbers and letters (lower and upper case) of the
	 * specified length. The method uses the Random class that is built-in to Java
	 * which is suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically generated
	 * so is not truly random.
	 * <p>
	 *
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 *
	 * @param length the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static String randomString(int length) {
		if (length < 1) {
			return null;
		}
		/* Create a char buffer to put random letters and numbers in. */
		char[] randBuffer = new char[length];
		if (StringUtil.randGen == null) {
			StringUtil.randGen = new Random();
		}
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The difference
	 * between min and max can be at most <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
		/*
		 * NOTE: Usually this should be a field rather than a method variable so that it
		 * is not re-seeded every call.
		 */
		if (StringUtil.randGen == null) {
			StringUtil.randGen = new Random();
		}

		/*
		 * nextInt is normally exclusive of the top value, so add 1 to make it inclusive
		 */
		int randomNum = StringUtil.randGen.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/**
	 * Gets the path.
	 *
	 * @param path the path
	 * @param separator the separator
	 * @return the path
	 */
	public static String getPath(String path, String separator) {
		if (path == null) {
			return null;
		}
		String[] base = path.split(separator);
		if (base.length < 1) {
			return path;
		}
		CharacterBuffer buffer = new CharacterBuffer();
		for (int i = base.length - 1; i >= 0; i--) {
			if (base[i].equals("..")) {
				i--;
			} else {
				if (i > 0) {
					buffer.insert(0, separator + base[i]);
				} else {
					buffer.insert(0, base[i]);
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * Get the relative path from one file to another, specifying the directory
	 * separator. If one of the provided resources does not exist, it is assumed to
	 * be a file unless it ends with '/' or '\'.
	 * 
	 * @param targetPath targetPath is calculated to this file
	 * @param basePath   basePath is calculated from this file
	 * @param separator  directory separator. The platform default is not assumed so
	 *                   that we can test Unix behaviour when running on Windows
	 *                   (for example)
	 * @return The Realative Path
	 */
	public static String getRelativePath(String targetPath, String basePath, String separator) {
		if (targetPath == null || basePath == null) {
			return null;
		}
		String[] base = basePath.split(separator);
		String[] target = targetPath.split(separator);

		/*
		 * First get all the common elements. Store them as a string, and also count how
		 * many of them there are.
		 */
		CharacterBuffer common = new CharacterBuffer();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length
				&& target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + separator);
			commonIndex++;
		}
		if (commonIndex == 0) {
			/*
			 * No single common path element. This most likely indicates differing drive
			 * letters, like C: and D:. These paths cannot be relativized.
			 */
			return null;
		}

		boolean baseIsFile = true;

		File baseResource = new File(basePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();

		} else if (basePath.endsWith(separator)) {
			baseIsFile = false;
		}
		CharacterBuffer relative = new CharacterBuffer();

		if (base.length != commonIndex) {
			int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;
			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + separator);
			}
		}
		relative.append(targetPath.substring(common.length()));
		return relative.toString();
	}

	/**
	 * Replace all.
	 *
	 * @param text the text
	 * @param args the args
	 * @return the character buffer
	 */
	public static final CharacterBuffer replaceAll(String text, Object... args) {
		CharacterBuffer value = new CharacterBuffer().with(text);
		if (args == null || args[0] == null) {
			return value;
		}
		int pos = -1 - args[0].toString().length();
		String placeholder;
		/*
		 * args are pairs of placeholder, replacement in the first run, replace
		 * placeholders by <$<placeholders>$> to mark them uniquely
		 */
		for (int i = 0; i < args.length; i += 2) {
			placeholder = args[i].toString();
			pos = -1 - placeholder.length();
			pos = text.indexOf(placeholder, pos + placeholder.length());
			while (pos >= 0) {
				value.replace(pos, pos + placeholder.length(), "<$<" + placeholder + ">$>");
				pos = text.indexOf(placeholder, pos + placeholder.length() + 6);
			}
		}

		/* in the second run, replace <$<placeholders>$> by replacement */
		for (int i = 0; i < args.length; i += 2) {
			placeholder = "<$<" + args[i] + ">$>";
			pos = -1 - placeholder.length();
			pos = text.indexOf(placeholder, pos + placeholder.length());
			while (pos >= 0) {
				String newString = "" + args[i + 1];
				value.replace(pos, pos + placeholder.length(), newString);
				pos = text.indexOf(placeholder, pos + newString.length());
			}
		}
		return value;
	}
	
	/**
	 * Encode umlaute.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String encodeUmlaute(String value) {
		CharacterBuffer sb = new CharacterBuffer().withBufferLength(value.length());
		for (int i=0;i<value.length();i++) {
			char character = value.charAt(i);
			if(character == 223) {
				sb.append("ss");
			} else if(character == 196) {
				sb.append("Ae");
			} else if(character == 220) {
				sb.append("Ue");
			} else if(character == 214) {
				sb.append("Oe");
			} else if(character == 228) {
				sb.append("ae");
			} else if(character == 252) {
				sb.append("ue");
			} else if(character == 246) {
				sb.append("oe");
			}else {
				sb.add(character);
			}
		}
		return sb.toString(); 
	}
	
    public static String convert(String value, String fromEncoding, String toEncoding) {
        try {
            return new String(value.getBytes(fromEncoding), toEncoding);
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }

    public static String charset(String value, String... charsets) {
        String probe = StandardCharsets.UTF_8.name();
        for (String c : charsets) {
            Charset charset = Charset.forName(c);
            if (charset != null) {
                if (value.equals(convert(convert(value, charset.name(), probe), probe, charset.name()))) {
                    return c;
                }
            }
        }
        return StandardCharsets.UTF_8.name();
    }
	
	/**
	 * Encode parameter.
	 *
	 * @param value the value
	 * @param charset the charset
	 * @return the string
	 */
	public static String encodeParameter(String value, Charset... charset) {
		if(value == null) {
			return null;
		}
		Charset currentSet = Charset.defaultCharset();
		if(charset!= null && charset.length>0) {
			currentSet = charset[0];
		}
		CharacterBuffer sb = new CharacterBuffer().withBufferLength(value.length());
		for (int i=0;i<value.length();i++) {
			char character = value.charAt(i);
			if (
					(character>= 'a' && character<= 'z') ||
					(character>= 'A' && character<= 'Z') ||
					(character>= '0' && character<= '9') ||
					"-_.*".indexOf(character)>=0)
			{
				sb.add(character);
			} else if (character == ' ') {
				sb.add('+');
			} else {
				byte[] str = ("" + character).getBytes(currentSet);
				for(byte b : str) {
					sb.add('%');
					int bit = (b >> 4) & 0xF;
					sb.add((char)(bit+(bit>9 ? 55 : 48)));
					bit = b & 0xF;
					sb.add((char)(bit+(bit>9 ? 55 : 48)));
				}
			}
		}
		return sb.toString();
	}

    /**
     * Checks if is text.
     *
     * @param name the name
     * @return true, if is text
     */
    public static boolean isText(String name) {
        if(name == null || name.isEmpty()) {
            return true;
        }
        for(int i=0;i<name.length();i++) {
            if (!((name.charAt(i) >= 'a' && name.charAt(i)<= 'z') || (name.charAt(i) >= 'A' && name.charAt(i)<= 'Z'))) {
                return false;
            }
        }
        return true;
    }
}
