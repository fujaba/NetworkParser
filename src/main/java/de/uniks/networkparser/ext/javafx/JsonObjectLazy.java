package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.json.JsonObject;
import netscape.javascript.JSObject;

public class JsonObjectLazy extends JsonObject {
//	public static final String JS_OBJECT="[object Object]";
//	public static final String JS_SET="[object Set]";
	public static final String FILTERPROP="$";
	
	private JSObject ref = null;
	private boolean loaded;
	
	public JsonObjectLazy(Object element) {
		if(element instanceof JSObject) {
			this.ref = (JSObject) element;
		}
	}

	public boolean lazyLoad() {
		if (this.ref == null) {
			return false;
		}
		if (this.loaded == false) {
			this.loaded = true;
		}
		else {
			return false;
		}
		JSObject eval = (JSObject) this.ref.eval("Object.keys(this).map(function (key) {return key;});");
		String[] keys = eval.toString().split(",");
		for (int i = 0; i < keys.length; i++) {
			// Get from Javascript the full Object and Filter the $ for not necessary links or bidirectional links
			if (keys[i].startsWith(FILTERPROP)) {
				continue;
			}
			Object value = this.ref.getMember(keys[i]);
			if (value instanceof JSObject) {
				JSObject jsValue = (JSObject) value;
				boolean isArray = Boolean.parseBoolean("" + jsValue.eval("Array.isArray(this);"));

				if (isArray) {
					JsonArrayLazy child = new JsonArrayLazy(value);
					this.add(keys[i], child);
					child.lazyLoad();
				}
				else {
					JsonObjectLazy child = new JsonObjectLazy(value);
					this.add(keys[i], child);
					child.lazyLoad();
				}
			}
			else {
				this.add(keys[i], value);
			}
		}
		return true;
	}
	
	@Override
	protected Object getByIndex(int offset, int index, int size) {
		Object result = super.getByIndex(offset, index, size);
		if(result != null ) {
			if(result instanceof JsonObjectLazy) {
				((JsonObjectLazy) result).lazyLoad();
			} else if(result instanceof JsonArrayLazy) {
				((JsonArrayLazy) result).lazyLoad();
			}
		}
		return result;
	}
	
	public JSObject getReference() {
		return this.ref;
	}
	
	/**
	 * Tries to load the Value directly from the JSObject, if it is not already loaded. 
	 * @param key load the Key from Json
	 * @return the value, that the REF contains, otherwise null
	 */
	public Object loadValue(Object key){
		// if already loaded, take the loaded value..
		if(this.keySet().contains(key)){
			return this.get(key);
		}
		if(this.ref == null) {
			return null;
		}
		// if not, try to get the Member from the JSObject directly
		Object member = this.ref.getMember("" + key);
		if(member != null){
			if(ReflectionLoader.JSOBJECT.isAssignableFrom(member.getClass())){
				return new JsonObjectLazy(member);
			}else{
				// if primitive, just return the value...
				return member;
			}
		}
		return null;
	}
}
