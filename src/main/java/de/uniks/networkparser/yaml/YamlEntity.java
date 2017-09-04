package de.uniks.networkparser.yaml;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SortedList;

public class YamlEntity extends SortedList<Object> implements EntityList {
	/**
	 * Default Constructor
	 */
	public YamlEntity() {
		super(false);
	}
	
	@Override
	public int sizeChildren() {
		return super.size();
	}


	@Override
	public YamlEntity withValue(Buffer values) {
		new YAMLTokener().withBuffer(values).parseToEntity(this);
		return this;
	}
	
	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new YamlEntity();
		}
		return new YamlItem();
	}
}