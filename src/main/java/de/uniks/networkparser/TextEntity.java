package de.uniks.networkparser;

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
import java.util.Comparator;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleList;

public class TextEntity implements EntityList {
	private SimpleList<BaseItem> children;
	private String tag;
	private CharSequence tagEnd;

	@Override
	public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		if (converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
		}
		return converter.encode(this);
	}

	private String parseItem(EntityStringConverter converter) {
		if (converter == null) {
			return null;
		}
		CharacterBuffer sb = new CharacterBuffer().with(converter.getPrefixFirst());
		sb.with(this.tag);
		if (this.children != null) {
			for (int i = 0; i < this.children.size(); i++) {
				BaseItem child = this.children.get(i);
				if (i > 0) {
					sb.with(BaseItem.CRLF);
				}
				sb.with(child.toString(converter));
			}
		}
		sb.with(this.tagEnd);
		return sb.toString();
	}

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	@Override
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	@Override
	public boolean add(Object... values) {
		if (values == null || values.length < 1) {
			return false;
		}
		if (values[0] instanceof String) {
			if (values.length == 1) {
				if (this.tag == null) {
					this.withTag((String) values[0]);
				} else {
					this.withChild(new TextEntity().withTag((String) values[0]));
				}
			}
		} else if (values.length % 2 == 1) {
			for (Object item : values) {
				if (item instanceof BaseItem) {
					this.withChild((BaseItem) item);
				}
			}
			return true;
		}
		return false;
	}

	private void withChild(BaseItem item) {
		if (this.children == null) {
			this.children = new SimpleList<BaseItem>();
		}
		this.children.add(item);
	}

	public String getTag() {
		return tag;
	}

	public TextEntity withTag(String value) {
		this.tag = value;
		return this;
	}

	public TextEntity withTag(char value) {
		this.tag = "" + value;
		return this;
	}

	public TextEntity withTagEnd(CharSequence value) {
		this.tagEnd = value;
		return this;
	}

	public TextEntity withTagEnd(char value) {
		this.tagEnd = "" + value;
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new SimpleList<Entity>();
		}
		return new TextEntity();
	}

	@Override
	public int size() {
		if (this.children == null) {
			return 0;
		}
		return this.children.size();
	}

	@Override
	public int sizeChildren() {
		return size();
	}

	@Override
	public BaseItem getChild(int index) {
		if (this.children == null) {
			return null;
		}
		return this.children.get(index);
	}

	@Override
	public boolean isComparator() {
		return false;
	}

	@Override
	public Comparator<Object> comparator() {
		return null;
	}

	@Override
	public BaseItem withValue(BufferItem values) {
		return null;
	}

	@Override
	public BaseItem firstChild() {
		return getChild(0);
	}
}
