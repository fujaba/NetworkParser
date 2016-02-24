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
import de.uniks.networkparser.gui.Pos;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
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

	public static SimpleList<Pos> getExcelRange(String tag) {
		SimpleList<Pos> range = new SimpleList<Pos>();
		if(tag == null) {
			return range;
		}
		int pos = tag.toUpperCase().indexOf(":");
		if(pos>0) {
			Pos start = Pos.valueOf(tag.substring(0, pos));
			Pos end = Pos.valueOf(tag.substring(pos+1));
			Pos step = Pos.create(start.x, start.y);

			while(step.y<=end.y) {
				while(step.x<=end.x) {
					range.add(step);
					step = Pos.create(step.x + 1, step.y);
				}
				step = Pos.create(start.x, step.y + 1);
			}
		}else {
			range.add(Pos.valueOf(tag));
		}
		return range;
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
	 * @param simpleText
	 *			Boolean for switch between text and Escaped-Text
	 * @param reference
	 *			A Reference Object to generate new Objects like Factory
	 *			Pattern
	 * @param converter
	 *			The Converter to transform Item
	 * @return a printable, displayable, transmittable representation of the
	 *		 object, beginning with <code>{</code>&nbsp;<small>(left
	 *		 brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *		 brace)</small>.
	 */
	public static String valueToString(Object value, boolean simpleText, BaseItem reference, Converter converter) {
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
			BaseItem item = reference.getNewList(true).with((Map<?, ?>) value);
			if (item instanceof BaseItem) {
				return ((BaseItem) item).toString(converter);
			}
			return ((BaseItem) item).toString();
		}
		if (value instanceof Collection) {
			BaseItem item = reference.getNewList(true);
			if(item instanceof SimpleKeyValueList<?,?>) {
				return ((SimpleKeyValueList<?,?>) item).withMap((Map<?, ?>) value).toString(converter);
			}
			if (item instanceof BaseItem) {
				return ((BaseItem) item).toString(converter);
			}
			return ((BaseItem) item).toString();
		}
		if (value.getClass().isArray()) {
			Object[] items = (Object[]) value;
			BaseItem item = reference.getNewList(false);
			for (Object entity : items) {
				item.with(entity);
			}
			if (item instanceof BaseItem) {
				return ((BaseItem) item).toString(converter);
			}
			return ((BaseItem) item).toString();
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
						.withMap((Map<?, ?>) object);
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
			Object valueA = entityA.getValue(key);
			Object valueB = entityB.getValue(key);
			if(valueA == null) {
				if(valueB == null) {
					Object oldValue = entityA.getValue(key);
					if(sameObject != null) {
						sameObject.with(key, oldValue);
					}
					entityA.without(key);
					entityB.without(key);
				}
				continue;
			}
			Object oldValue = compareValue(valueA, valueB);
			if(oldValue != null) {
				if(sameObject != null) {
					sameObject.with(key, oldValue);
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
					sameList.with(oldValue);
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

	private static final String primitiveTypes = " void String long Long int Integer char Char boolean Boolean byte Byte float Float double Double Object java.util.Date ";
	private static final String numericTypes = " long Long int Integer byte Byte float Float double Double ";
	private static final String javaLang="java.lang.";
	public static boolean isPrimitiveType(String type) {

		if (type == null)
			return false;
		if(type.startsWith(javaLang)) {
			type = type.substring(javaLang.length() );
		}
		return primitiveTypes.indexOf(" " + type + " ") >= 0;
	}
	
	public static boolean isNumericType(String type) {
		if (type == null)
			return false;
		if(type.startsWith(javaLang)) {
			type = type.substring(javaLang.length() +1 );
		}
		return numericTypes.indexOf(" " + type + " ") >= 0;
	}

	public static String convertPrimitiveToObjectType(String type) {
		int pos = transferMap.indexOf(type);
		if(pos<0) {
			return type;
		}
		return transferMap.getValueByIndex(pos);
	}

	public static final String javaKeyWords = " abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for if goto implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while ";
	private static final SimpleKeyValueList<String, String> transferMap =  new SimpleKeyValueList<String, String>().withKeyValueString("long:Long,int:Integer,char:Character,boolean:Boolean,byte:Byte,float:Float,double:Double", String.class);

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

	/**
	 * Map to convert extended characters in html entities.
	 */
	public static final String HTMLTOKEN="Aacute:193,aacute:225,Acirc:194,acirc:226,acute:180,AElig:198,aelig:230,Agrave:192,agrave:224,alefsym:8501,Alpha:913,alpha:945,amp:38,and:8743,ang:8736,Aring:197,aring:229,asymp:8776,Atilde:195,atilde:227,Auml:196,auml:228,bdquo:8222,Beta:914,beta:946,brvbar:166,bull:8226,cap:8745,Ccedil:199,ccedil:231,cedil:184,cent:162,Chi:935,chi:967,circ:710,clubs:9827,cong:8773,copy:169,crarr:8629,cup:8746,curren:164,dagger:8224,Dagger:8225,darr:8595,dArr:8659,deg:176,Delta:916,delta:948,diams:9830,divide:247,Eacute:201,eacute:233,Ecirc:202,ecirc:234,Egrave:200,egrave:232,empty:8709,emsp:8195,ensp:8194,Epsilon:917,epsilon:949,equiv:8801,Eta:919,eta:951,ETH:208,eth:240,Euml:203,euml:235,euro:8364,exist:8707,fnof:402,forall:8704,frac12:189,frac14:188,frac34:190,frasl:8260,Gamma:915,gamma:947,ge:8805,harr:8596,hArr:8660,hearts:9829,hellip:8230,Iacute:205,iacute:237,Icirc:206,icirc:238,iexcl:161,Igrave:204,igrave:236,image:8465,infin:8734,int:8747,Iota:921,iota:953,iquest:191,isin:8712,Iuml:207,iuml:239,Kappa:922,kappa:954,Lambda:923,lambda:955,lang:9001,laquo:171,larr:8592,lArr:8656,lceil:8968,ldquo:8220,le:8804,lfloor:8970,lowast:8727,loz:9674,lrm:8206,lsaquo:8249,lsquo:8216,macr:175,mdash:8212,micro:181,middot:183,minus:8722,Mu:924,mu:956,nabla:8711,nbsp:160,ndash:8211,ne:8800,ni:8715,not:172,notin:8713,nsub:8836,Ntilde:209,ntilde:241,Nu:925,nu:957,Oacute:211,oacute:243,Ocirc:212,ocirc:244,OElig:338,oelig:339,Ograve:210,ograve:242,oline:8254,Omega:937,omega:969,Omicron:927,omicron:959,oplus:8853,or:8744,ordf:170,ordm:186,Oslash:216,oslash:248,Otilde:213,otilde:245,otimes:8855,Ouml:214,ouml:246,para:182,part:8706,permil:8240,perp:8869,Phi:934,phi:966,Pi:928,pi:960,piv:982,plusmn:177,pound:163,prime:8242,Prime:8243,prod:8719,prop:8733,Psi:936,psi:968,radic:8730,rang:9002,raquo:187,rarr:8594,rArr:8658,rceil:8969,rdquo:8221,real:8476,reg:174,rfloor:8971,Rho:929,rho:961,rlm:8207,rsaquo:8250,rsquo:8217,sbquo:8218,Scaron:352,scaron:353,sdot:8901,sect:167,shy:173,Sigma:931,sigma:963,sigmaf:962,sim:8764,spades:9824,sub:8834,sube:8838,sum:8721,sup1:185,sup2:178,sup3:179,sup:8835,supe:8839,szlig:223,Tau:932,tau:964,there4:8756,Theta:920,theta:952,thetasym:977,thinsp:8201,THORN:222,thorn:254,tilde:732,times:215,trade:8482,Uacute:218,uacute:250,uarr:8593,uArr:8657,Ucirc:219,ucirc:251,Ugrave:217,ugrave:249,uml:168,upsih:978,Upsilon:933,upsilon:965,Uuml:220,uuml:252,weierp:8472,Xi:926,xi:958,Yacute:221,yacute:253,yen:165,yuml:255,Yuml:376,Zeta:918,zeta:950,zwj:8205,zwnj:8204";
	private SimpleKeyValueList<String, Integer> entities = new SimpleKeyValueList<String, Integer>()
			.withKeyValueString(HTMLTOKEN, Integer.class)
			.with("lt", 60)
			.with("gt", 62);

	/**
	 * Convert special and extended characters into HTML entitities.
	 *
	 * @param str
	 *			input string
	 * @return formatted string
	 */
	public String encode(String str) {
		if (str == null) {
			return "";
		}
		StringBuilder buf = new StringBuilder(); // the output string buffer
		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);
			String entity = this.entities.getKey(Integer.valueOf(ch)); // get equivalent html  entity
			if (entity == null) { // if entity has not been found
				if (ch > 128) { // check if is an extended character
					buf.append("&#" + ((int) ch) + ";"); // convert extended
															// character
				} else {
					buf.append(ch); // append the character as is
				}
			} else {
				buf.append("&" + entity+ ";"); // append the html entity
			}
		}
		return buf.toString();
	}

	/**
	 * Convert HTML entities to special and extended unicode characters
	 * equivalents.
	 *
	 * @param str
	 *			input string
	 * @return formatted string
	 */
	public String decode(String str) {
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);
			if (ch == '&') {
				int semi = str.indexOf(';', i + 1);
				if ((semi == -1) || ((semi - i) > 7)) {
					buf.append(ch);
					continue;
				}
				String entity = str.substring(i+1, semi);
				Integer iso;
				if (entity.charAt(1) == ' ') {
					buf.append(ch);
					continue;
				}
				if (entity.charAt(0) == '#') {
					if (entity.charAt(1) == 'x') {
						iso = Integer.valueOf(
								entity.substring(2, entity.length() ), 16);
					} else {
						iso = Integer.valueOf(entity.substring(1,
								entity.length() ));
					}
				} else {
					iso = this.entities.get(entity);
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
}
