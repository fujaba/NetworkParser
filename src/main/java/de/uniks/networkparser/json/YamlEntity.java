package de.uniks.networkparser.json;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
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
	public YamlEntity withValue(BufferItem values) {
		new YAMLTokener().parseToEntity(this, values);
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new YamlEntity();
		}
		return new YamlItem();
	}
}
