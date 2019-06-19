package de.uniks.networkparser.ext.javafx;

/*
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
		} else {
			return false;
		}
		int size = (Integer) ReflectionLoader.call(ref, "eval", "this.length;");
		for (int i = 0; i < size; i++) {
			Object value = ReflectionLoader.call(ref, "eval", "this[" + i + "]");
			if (ReflectionLoader.JSOBJECT.isAssignableFrom(value.getClass())) {
				boolean isArray = (Boolean) ReflectionLoader.call(value, "eval", "Array.isArray(this);");

				if (isArray) {
					JsonArrayLazy child = new JsonArrayLazy(value);
					this.add(child);
					child.lazyLoad();
				} else {
					JsonObjectLazy child = new JsonObjectLazy(value);
					this.add(child);
					child.lazyLoad();
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
		if (result != null) {
			if (result instanceof JsonObjectLazy) {
				((JsonObjectLazy) result).lazyLoad();
			} else if (result instanceof JsonArrayLazy) {
				((JsonArrayLazy) result).lazyLoad();
			}
		}
		return result;
	}
}
