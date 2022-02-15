package de.uniks.networkparser.json;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SortedList;

/**
 * The Class YamlEntity.
 *
 * @author Stefan
 */
public class YamlEntity extends SortedList<Object> implements EntityList {
	
	/**
	 * Default Constructor.
	 */
	public YamlEntity() {
		super(false);
	}

	/**
	 * Size children.
	 *
	 * @return the int
	 */
	@Override
	public int sizeChildren() {
		return super.size();
	}

	/**
	 * With value.
	 *
	 * @param values the values
	 * @return the yaml entity
	 */
	@Override
	public YamlEntity withValue(BufferItem values) {
		new YAMLTokener().parseToEntity(this, values);
		return this;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new YamlEntity();
		}
		return new YamlItem();
	}
}
