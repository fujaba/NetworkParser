package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class StoryObjectFilter extends Filter{
	private SimpleList<Object> elements=new SimpleList<Object>();
	private SimpleKeyValueList<Object, String> ids=new SimpleKeyValueList<Object, String>();
	private SimpleKeyValueList<String, String> images=new SimpleKeyValueList<String, String>();
	
	public StoryObjectFilter with(Object...elements) {
		this.elements.add(elements);
		return this;
	}
	
	@Override
	public int convert(Object entity, String property, Object value, IdMap map, int deep) {
		if(elements.contains(value)) {
			return 1;
		}
		return -1;
//		return super.convert(entity, property, value, map, deep);
		
	}

	public SimpleList<Object> getElements() {
		return elements;
	}
	
	public SimpleKeyValueList<Object, String> getIds() {
		return ids;
	}
	
	public SimpleKeyValueList<String, String> getImages() {
		return images;
	}
}
