package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.json.JsonArray;

public class JsonArrayLazy extends JsonArray {
	private Object ref = null;
	private boolean loaded;

	public JsonArrayLazy(Object element) {
		this.ref = element;
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
		int size = (int) ReflectionLoader.call("eval", ref, "this.length;");
		for (int i = 0; i < size; i++) {
			Object value = ReflectionLoader.call("eval", ref, "this[" + i + "]");
			if(ReflectionLoader.JSOBJECT.isAssignableFrom(value.getClass())) {
				boolean isArray = (boolean) ReflectionLoader.call("eval", value, "Array.isArray(this);");

				if (isArray) {
					JsonArrayLazy child = new JsonArrayLazy(value);
					this.add(child);
					child.lazyLoad();
				}
				else {
					JsonObjectLazy child = new JsonObjectLazy(value);
					this.add(child);
					child.lazyLoad();
				}
			}
			else {
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
