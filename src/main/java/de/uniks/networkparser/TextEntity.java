package de.uniks.networkparser;

import java.util.Comparator;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleList;

public class TextEntity implements EntityList {
	private SimpleList<BaseItem> children;
	private String tag;
	private String tagEnd;

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
		CharacterBuffer sb = new CharacterBuffer().with(converter.getPrefixFirst());
		sb.with(this.tag);
		 if(this.children != null) {
			 for(int i=0;i<this.children.size();i++) {
				 BaseItem child = this.children.get(i);
				 if(i > 0) {
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
		if(values==null || values.length < 1){
			return false;
		}
		if(values[0] instanceof String) {
			if(values.length == 1) {
				if(this.tag == null) {
					this.withTag((String)values[0]);
				} else {
					this.withChild(new TextEntity().withTag((String)values[0]));
				}
			}
		} else if (values.length % 2 == 1) {
			for(Object item : values) {
				if(item instanceof BaseItem) {
					this.withChild((BaseItem) item);
				} 
			}
			return true;
		}
		return false;
	}

	private void withChild(BaseItem item) {
		if(this.children == null) {
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
	
	public TextEntity withTagEnd(String value) {
		this.tagEnd = value;
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new SimpleList<Entity>();
		}
		return new TextEntity();
	}

	@Override
	public int size() {
		if(this.children == null) {
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
		if(this.children == null) {
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
	public BaseItem withValue(Buffer values) {
		return null;
	}
}
