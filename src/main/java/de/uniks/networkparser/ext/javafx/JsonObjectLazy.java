package de.uniks.networkparser.ext.javafx;

/*
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
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.json.JsonObject;

public class JsonObjectLazy extends JsonObject {
	// public static final String JS_OBJECT="[object Object]";
//	public static final String JS_SET="[object Set]";
	public static final String FILTERPROP = "$";

	private Object ref = null;
	private boolean loaded;

	public JsonObjectLazy(Object element) {
		this.ref = element;
	}

	public boolean lazyLoad() {
		if (this.ref == null) {
			return false;
		}
		if (this.loaded == false) {
			this.loaded = true;
		} else {
			return false;
		}
		Object eval = ReflectionLoader.call(ref, "eval", "Object.keys(this).map(function (key) {return key;});");
		if (eval != null) {
			String[] keys = eval.toString().split(",");
			for (int i = 0; i < keys.length; i++) {
				// Get from Javascript the full Object and Filter the $ for not necessary links
				// or bidirectional links
				if (keys[i].startsWith(FILTERPROP)) {
					continue;
				}
				Object value = getMember(this.ref, keys[i]);
				if (value == null) {
					return false;
				}
				if (ReflectionLoader.JSOBJECT.isAssignableFrom(value.getClass())) {
					// JSObject jsValue = (JSObject) value;
					boolean isArray = (Boolean) ReflectionLoader.call(value, "eval", "Array.isArray(this);");
					// boolean isArray = Boolean.parseBoolean("" + jsValue.eval());
					if (isArray) {
						JsonArrayLazy child = new JsonArrayLazy(value);
						this.add(keys[i], child);
						child.lazyLoad();
					} else {
						JsonObjectLazy child = new JsonObjectLazy(value);
						this.add(keys[i], child);
						child.lazyLoad();
					}
				} else {
					this.add(keys[i], value);
				}
			}
		}
		return true;
	}

	@Override
	protected Object getByIndex(int offset, int index, int size) {
		Object result = super.getByIndex(offset, index, size);
		if (result != null) {
			if (result instanceof JsonObjectLazy) {
				((JsonObjectLazy) result).lazyLoad();
			} else if (result instanceof JsonArrayLazy) {
				((JsonArrayLazy) result).lazyLoad();
			}
		}
		return result;
	}

	public Object getReference() {
		return this.ref;
	}

	/**
	 * Tries to load the Value directly from the JSObject, if it is not already
	 * loaded.
	 * 
	 * @param key load the Key from Json
	 * @return the value, that the REF contains, otherwise null
	 */
	public Object loadValue(Object key) {
		// if already loaded, take the loaded value..
		if (this.keySet().contains(key)) {
			return this.get(key);
		}
		if (this.ref == null) {
			return null;
		}
		// if not, try to get the Member from the JSObject directly
		Object member = getMember(this.ref, "" + key);
		if (member != null) {
			if (ReflectionLoader.JSOBJECT.isAssignableFrom(member.getClass())) {
				return new JsonObjectLazy(member);
			} else {
				// if primitive, just return the value...
				return member;
			}
		}
		return null;
	}

	private static Object getMember(Object obj, String value) {
		if (obj == null || obj.getClass().getName().startsWith("javafx") == false) {
			return null;
		}
		return ReflectionLoader.call(obj, "getMember", String.class, value);
	}

}
