package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.json.JsonArray;
import netscape.javascript.JSObject;

public class JsonArrayLazy extends JsonArray {
	public static final String JS_OBJECT="[object Object]";
	public static final String JS_SET="[object Set]";
	
	private JSObject ref = null;
	private boolean loaded;

	public JsonArrayLazy(Object element) {
		if(element instanceof JSObject) {
			this.ref = (JSObject) element;
		}
	}

	public boolean lazyLoad() {
		if(this.ref == null) {
			return false;
		}
		if(this.loaded == false) {
			this.loaded = true;
		} else {
			return false;
		}
		int size  = (int) this.ref.eval("this.size;");
		for (int i = 0; i < size; i++) {
			Object value = this.ref.eval("this["+i+"]");
			if (value instanceof JSObject) {
				if(JS_SET.equals(value.toString())) {
					this.add(new JsonArrayLazy(value));
				} else {
					this.add(new JsonObjectLazy(value));
				}
			} else {
				this.add(value);
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
}
