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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A JsonObject is an unordered collection of name/value pairs. Its
 * external form is a string wrapped in curly braces with colons between the
 * names and values, and commas between the values and names. The internal form
 * is an object having <code>get</code> and <code>opt</code> methods for
 * accessing the values by name, and <code>put</code> methods for adding or
 * replacing values by name. The values can be any of these types:
 * <code>Boolean</code>, <code>JsonArray</code>, <code>JsonObject</code>,
 * <code>Number</code>, <code>String</code>, or the <code>JsonObject.NULL</code>
 * object. A JsonObject constructor can be used to convert an external form
 * JSON text into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>opt</code> methods, or to convert values into a
 * JSON text using the <code>put</code> and <code>toString</code> methods.
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they
 * do not throw. Instead, they return a specified value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For example,
 * <pre>myString = new JsonObject().put("JSON", "Hello, World!").toString();</pre>
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON syntax rules.
 * The constructors are more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 *     before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 *     quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 *     or single quote, and if they do not contain leading or trailing spaces,
 *     and if they do not contain any of these characters:
 *     <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers
 *     and if they are not the reserved words <code>true</code>,
 *     <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as
 *     by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 *     well as by <code>,</code> <small>(comma)</small>.</li>
 * </ul>
 * @author JSON.org
 * @version 2011-11-24
 */
public class JsonObject{
    /**
     * The map where the JsonObject's properties are kept.
     */
    private Map<String, Object> map;
    private Map<String, Object> getMap(){
    	if(map==null){
    		map=new LinkedHashMap<String, Object>();
    	}
    	return map;
    }

    /**
     * Construct an empty JsonObject.
     */
    public JsonObject() {
    }

    /**
     * Construct a JsonObject from a JSONTokener.
     * @param x A JSONTokener object containing the source string.
     *  or a duplicated key.
     */
    public JsonObject(JsonTokener x) {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JsonObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A JsonObject text must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }

// The key is followed by ':'. We will also tolerate '=' or '=>'.

            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            this.put(key, x.nextValue());

// Pairs are separated by ','. We will also tolerate ';'.

            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }


    /**
     * Construct a JsonObject from a Map.
     *
     * @param map A map object that can be used to initialize the contents of
     *  the JsonObject.
     */
    public JsonObject(Map<String, Object> map) {
    	getMap();
        if (map != null) {
        	Iterator<Entry<String, Object>> i = map.entrySet().iterator();
            while (i.hasNext()) {
            	Entry<String, Object> e = i.next();
                Object value = e.getValue();
                if (value != null) {
                    this.put(e.getKey(), wrap(value));
                }
            }
        }
    }


    /**
     * Construct a JsonObject from a source JSON text string.
     * This is the most commonly used JsonObject constructor.
     * @param source    A string beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public JsonObject(String source) {
        this(new JsonTokener(source));
    }

    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a
     * JsonArray is stored under the key to hold all of the accumulated values.
     * If there is already a JsonArray, then the new value is appended to it.
     * In contrast, the put method replaces the previous value.
     *
     * If only one value is accumulated that is not a JsonArray, then the
     * result will be the same as using put. But if multiple values are
     * accumulated, then the result will be like append.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws JSONException If the value is an invalid number
     *  or if the key is null.
     */
    public JsonObject accumulate(String key, Object value){
        testValidity(value);
        Object object = this.get(key);
        if (object == null) {
            this.put(key, value instanceof JsonArray
                    ? new JsonArray().put(value)
                    : value);
        } else if (object instanceof JsonArray) {
            ((JsonArray)object).put(value);
        } else {
            this.put(key, new JsonArray().put(object).put(value));
        }
        return this;
    }


    /**
     * Append values to the array under a key. If the key does not exist in the
     * JsonObject, then the key is put in the JsonObject with its value being a
     * JsonArray containing the value parameter. If the key was already
     * associated with a JsonArray, then the value parameter is appended to it.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws RuntimeException If the key is null or if the current value
     *  associated with the key is not a JsonArray.
     */
    public JsonObject append(String key, Object value) {
        testValidity(value);
        Object object = this.get(key);
        if (object == null) {
            this.put(key, new JsonArray().put(value));
        } else if (object instanceof JsonArray) {
            this.put(key, ((JsonArray)object).put(value));
        } else {
            throw new RuntimeException("JsonObject[" + key +
                    "] is not a JsonArray.");
        }
        return this;
    }


    /**
     * Produce a string from a double. The string "null" will be returned if
     * the number is not finite.
     * @param  d A double.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 &&
                string.indexOf('E') < 0) {
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
     * Get the value object associated with a key.
     *
     * @param key   A key string.
     * @return      The object associated with the key.
     * @throws      RuntimeException if the key is not found.
     */
    public Object get(String key) {
        if (key == null) {
            throw new RuntimeException("Null key.");
        }
        return getMap().get(key);
    }


    /**
     * Get the boolean value associated with a key.
     *
     * @param key   A key string.
     * @return      The truth.
     * @throws      RuntimeException
     *  if the value is not a Boolean or the String "true" or "false".
     */
    public boolean getBoolean(String key)  {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not a Boolean.");
    }


    /**
     * Get the double value associated with a key.
     * @param key   A key string.
     * @return      The numeric value.
     * @throws RuntimeException if the key is not found or
     *  if the value is not a Number object and cannot be converted to a number.
     */
    public double getDouble(String key) {
        Object object = this.get(key);
        try {
            return object instanceof Number
                ? ((Number)object).doubleValue()
                : Double.parseDouble((String)object);
        } catch (Exception e) {
            throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not a number.");
        }
    }


    /**
     * Get the int value associated with a key.
     *
     * @param key   A key string.
     * @return      The integer value.
     * @throws   RuntimeException if the key is not found or if the value cannot
     *  be converted to an integer.
     */
    public int getInt(String key)  {
        Object object = this.get(key);
        try {
            return object instanceof Number
                ? ((Number)object).intValue()
                : Integer.parseInt((String)object);
        } catch (Exception e) {
            throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not an int.");
        }
    }


    /**
     * Get the JsonArray value associated with a key.
     *
     * @param key   A key string.
     * @return      A JsonArray which is the value.
     * @throws      RuntimeExpetion if the key is not found or
     *  if the value is not a JsonArray.
     */
    public JsonArray getJsonArray(String key) {
        Object object = this.get(key);
        if (object instanceof JsonArray) {
            return (JsonArray)object;
        }
        throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not a JsonArray.");
    }


    /**
     * Get the JsonObject value associated with a key.
     *
     * @param key   A key string.
     * @return      A JsonObject which is the value.
     * @throws      RuntimeException if the key is not found or
     *  if the value is not a JsonObject.
     */
    public JsonObject getJsonObject(String key) {
        Object object = this.get(key);
        if (object instanceof JsonObject) {
            return (JsonObject)object;
        }
        throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not a JsonObject.");
    }


    /**
     * Get the long value associated with a key.
     *
     * @param key   A key string.
     * @return      The long value.
     * @throws   RuntimeException if the key is not found or if the value cannot
     *  be converted to a long.
     */
    public long getLong(String key)  {
        Object object = this.get(key);
        try {
            return object instanceof Number
                ? ((Number)object).longValue()
                : Long.parseLong((String)object);
        } catch (Exception e) {
            throw new RuntimeException("JsonObject[" + quote(key) +
                "] is not a long.");
        }
    }


    /**
     * Get an array of field names from a JsonObject.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(JsonObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator<String> keys = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (keys.hasNext()) {
            names[i] = (String)keys.next();
            i += 1;
        }
        return names;
    }



    /**
     * Get the string associated with a key.
     *
     * @param key   A key string.
     * @return      A string which is the value.
     * @throws   RuntimeException if there is no string value for the key.
     */
    public String getString(String key) {
        Object object = this.get(key);
        if (object instanceof String) {
            return (String)object;
        }
        throw new RuntimeException("JsonObject[" + quote(key) +
            "] not a string.");
    }


    /**
     * Determine if the JsonObject contains a specific key.
     * @param key   A key string.
     * @return      true if the key exists in the JsonObject.
     */
    public boolean has(String key) {
        return getMap().containsKey(key);
    }


    /**
     * Increment a property of a JsonObject. If there is no such property,
     * create one with a value of 1. If there is such a property, and if
     * it is an Integer, Long, Double, or Float, then add one to it.
     * @param key  A key string.
     * @return this.
     * @throws RuntimeException If there is already a property with this name
     * that is not an Integer, Long, Double, or Float.
     */
    public JsonObject increment(String key) {
        Object value = this.get(key);
        if (value == null) {
            this.put(key, 1);
        } else if (value instanceof Integer) {
            this.put(key, ((Integer)value).intValue() + 1);
        } else if (value instanceof Long) {
            this.put(key, ((Long)value).longValue() + 1);
        } else if (value instanceof Double) {
            this.put(key, ((Double)value).doubleValue() + 1);
        } else if (value instanceof Float) {
            this.put(key, ((Float)value).floatValue() + 1);
        } else {
            throw new RuntimeException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }

    /**
     * Get an enumeration of the keys of the JsonObject.
     *
     * @return An iterator of the keys.
     */
    public Iterator<String> keys() {
        return getMap().keySet().iterator();
    }

    /**
     * Get the number of keys stored in the JsonObject.
     *
     * @return The number of keys in the JsonObject.
     */
    public int length() {
    	if(map==null){
    		return 0;
    	}
        return this.map.size();
    }


    /**
     * Produce a JsonArray containing the names of the elements of this
     * JsonObject.
     * @return A JsonArray containing the key strings, or null if the JsonObject
     * is empty.
     */
    public JsonArray names() {
        JsonArray ja = new JsonArray();
        Iterator<String> keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }

    /**
     * Produce a string from a Number.
     * @param  number A Number
     * @return A String.
     * @throws RuntimeException If n is a non-finite number.
     */
    public static String numberToString(Number number) {
        if (number == null) {
            throw new RuntimeException("Null pointer");
        }
        testValidity(number);

// Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 &&
                string.indexOf('E') < 0) {
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
     * Put a key/boolean pair in the JsonObject.
     *
     * @param key   A key string.
     * @param value A boolean which is the value.
     * @return this.
     */
    public JsonObject put(String key, boolean value) {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Put a key/value pair in the JsonObject, where the value will be a
     * JsonArray which is produced from a Collection.
     * @param key   A key string.
     * @param value A Collection value.
     * @return      this.
     */
    public JsonObject put(String key, Collection<?> value) {
        this.put(key, new JsonArray(value));
        return this;
    }


    /**
     * Put a key/double pair in the JsonObject.
     *
     * @param key   A key string.
     * @param value A double which is the value.
     * @return this.
     */
    public JsonObject put(String key, double value) {
        this.put(key, new Double(value));
        return this;
    }


    /**
     * Put a key/int pair in the JsonObject.
     *
     * @param key   A key string.
     * @param value An int which is the value.
     * @return this.
     */
    public JsonObject put(String key, int value)  {
        this.put(key, new Integer(value));
        return this;
    }


    /**
     * Put a key/long pair in the JsonObject.
     *
     * @param key   A key string.
     * @param value A long which is the value.
     * @return this.
     */
    public JsonObject put(String key, long value)  {
        this.put(key, new Long(value));
        return this;
    }


    /**
     * Put a key/value pair in the JsonObject, where the value will be a
     * JsonObject which is produced from a Map.
     * @param key   A key string.
     * @param value A Map value.
     * @return      this.
     * @throws JSONException
     */
    public JsonObject put(String key, Map<String, Object> value)  {
        this.put(key, new JsonObject(value));
        return this;
    }


    /**
     * Put a key/value pair in the JsonObject. If the value is null,
     * then the key will be removed from the JsonObject if it is present.
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, JsonArray, JsonObject, Long, String,
     *  or the JsonObject.NULL object.
     * @return this.
     * @throws RuntimeException If the value is non-finite number
     *  or if the key is null.
     */
    public JsonObject put(String key, Object value) {
        if (key == null) {
            throw new RuntimeException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            getMap().put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, producing <\/,
     * allowing JSON text to be delivered in HTML. In JSON text, a string
     * cannot contain a control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
     */
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        String       hhhh;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);

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
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
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
     * Remove a name and its value, if present.
     * @param key The name to be removed.
     * @return The value that was associated with the name,
     * or null if there was no value.
     */
    public Object remove(String key) {
        return getMap().remove(key);
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     * @param string A String.
     * @return A simple JSON value.
     */
    public static Object stringToValue(String string) {
        Double d;
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
         * If it might be a number, try converting it.
         * If a number cannot be produced, then the value will just
         * be a string. Note that the plus and implied string
         * conventions are non-standard. A JSON parser may accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            try {
                if (string.indexOf('.') > -1 ||
                        string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                    d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long myLong = new Long(string);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Integer(myLong.intValue());
                    } else {
                        return myLong;
                    }
                }
            }  catch (Exception ignore) {
            }
        }
        return string;
    }


    /**
     * Throw an exception if the object is a NaN or infinite number.
     * @param o The object to test.
     * @throws RuntimeException If o is a non-finite number.
     */
    public static void testValidity(Object o) {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new RuntimeException(
                        "JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float)o).isInfinite() || ((Float)o).isNaN()) {
                    throw new RuntimeException(
                        "JSON does not allow non-finite numbers.");
                }
            }
        }
    }


    /**
     * Make a JSON text of this JsonObject. For compactness, no whitespace
     * is added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public String toString() {
        try {
        	Iterator<String> keys = this.keys();
            StringBuffer sb = new StringBuffer("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(getMap().get(o)));
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Make a prettyprinted JSON text of this JsonObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public String toString(int indentFactor){
        return this.toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted JSON text of this JsonObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws RuntimeException If the object contains an invalid number.
     */
    String toString(int indentFactor, int indent) {
        int i;
        int length = this.length();
        if (length == 0) {
            return "{}";
        }
        Map<String, Object> map = getMap();
        Iterator<String> keys = map.keySet().iterator();
        int          newindent = indent + indentFactor;
        Object       object;
        StringBuffer sb = new StringBuffer("{");
        if (length == 1) {
            object = keys.next();
            sb.append(quote(object.toString()));
            sb.append(": ");
            sb.append(valueToString(map.get(object), indentFactor,
                    indent));
        } else {
            while (keys.hasNext()) {
                object = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                } else {
                    sb.append('\n');
                }
                for (i = 0; i < newindent; i += 1) {
                    sb.append(' ');
                }
                sb.append(quote(object.toString()));
                sb.append(": ");
                sb.append(valueToString(map.get(object), indentFactor,
                        newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (i = 0; i < indent; i += 1) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }


    /**
     * Make a JSON text of an Object value. If the object has an
     * value.toJSONString() method, then that method will be used to produce
     * the JSON text. The method is required to produce a strictly
     * conforming text. If the object does not contain a toJSONString
     * method (which is the most common case), then a text will be
     * produced by other means. If the value is an array or Collection,
     * then a JsonArray will be made from it and its toJSONString method
     * will be called. If the value is a MAP, then a JsonObject will be made
     * from it and its toJSONString method will be called. Otherwise, the
     * value's toString method will be called, and the result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    @SuppressWarnings("unchecked")
	public static String valueToString(Object value) {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof JsonObject ||
                value instanceof JsonArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new JsonObject((Map<String, Object>)value).toString();
        }
        if (value instanceof Collection) {
            return new JsonArray((Collection<?>)value).toString();
        }
        if (value.getClass().isArray()) {
            return new JsonArray((Collection<?>)value).toString();
        }
        return quote(value.toString());
    }


    /**
     * Make a prettyprinted JSON text of an object value.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
     @SuppressWarnings("unchecked")
	static String valueToString(
         Object value,
         int    indentFactor,
         int    indent
     )  {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JsonObject) {
            return ((JsonObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JsonArray) {
            return ((JsonArray)value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            return new JsonObject((Map<String,Object>)value).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            return new JsonArray((Collection<?>)value).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JsonArray((Collection<?>)value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }


     /**
      * Wrap an object, if necessary. If the object is null, return the NULL
      * object. If it is an array or collection, wrap it in a JsonArray. If
      * it is a map, wrap it in a JsonObject. If it is a standard property
      * (Double, String, et al) then it is already wrapped. Otherwise, if it
      * comes from one of the java packages, turn it into a string. And if
      * it doesn't, try to wrap it in a JsonObject. If the wrapping fails,
      * then null is returned.
      *
      * @param object The object to wrap
      * @return The wrapped value
      */
     @SuppressWarnings("unchecked")
	public static Object wrap(Object object) {
         try {
        	 if (object == null) {
 				return null;
 			}
        	 
        	 if (object instanceof JsonObject
 					|| object instanceof JsonArray
 					|| object == null
 					|| object instanceof Byte || object instanceof Character
 					|| object instanceof Short || object instanceof Integer
 					|| object instanceof Long || object instanceof Boolean
 					|| object instanceof Float || object instanceof Double
 					|| object instanceof String) {
                 return object;
             }

             if (object instanceof Collection) {
                 return new JsonArray((Collection<?>)object);
             }
             if (object.getClass().isArray()) {
            	 return new JsonArray((Collection<?>) object);
             }
             if (object instanceof Map) {
                 return new JsonObject((Map<String,Object>)object);
             }
             Package objectPackage = object.getClass().getPackage();
             String objectPackageName = objectPackage != null
                 ? objectPackage.getName()
                 : "";
             if (
                 objectPackageName.startsWith("java.") ||
                 objectPackageName.startsWith("javax.") ||
                 object.getClass().getClassLoader() == null
             ) {
                 return object.toString();
             }
             return null;
         } catch(Exception exception) {
             return null;
         }
     }


     /**
      * Write the contents of the JsonObject as JSON text to a writer.
      * For compactness, no whitespace is added.
      * <p>
      * Warning: This method assumes that the data structure is acyclical.
      *
      * @return The writer.
      */
     public Writer write(Writer writer)  {
        try {
            boolean  commanate = false;
            Map<String, Object> map = getMap();
            Iterator<String> keys = map.keySet().iterator();
            writer.write('{');

            while (keys.hasNext()) {
                if (commanate) {
                    writer.write(',');
                }
                Object key = keys.next();
                writer.write(quote(key.toString()));
                writer.write(':');
                Object value = map.get(key);
                if (value instanceof JsonObject) {
                    ((JsonObject)value).write(writer);
                } else if (value instanceof JsonArray) {
                    ((JsonArray)value).write(writer);
                } else {
                    writer.write(valueToString(value));
                }
                commanate = true;
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
     }
}