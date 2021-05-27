package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class MapEntityStack {
	/** The Stack. */
	private SimpleKeyValueList<Object, SendableEntityCreator> stack = new SimpleKeyValueList<Object, SendableEntityCreator>();

	private SimpleList<String> tags = new SimpleList<String>();

	private SimpleKeyValueList<String, SimpleSet<String>> childProperties = new SimpleKeyValueList<String, SimpleSet<String>>();

	/**
	 * Remove The Last Element
	 */
	public void popStack() {
		this.stack.removePos(this.stack.size() - 1);
		this.tags.remove(this.tags.size() - 1);
	}

	/** @return The StackSize */
	public int getStackSize() {
		return this.stack.size();
	}

	public SimpleList<String> getTags() {
		return tags;
	}

	/**
	 * Get the current Element
	 *
	 * @return The Stack Element - offset
	 */
	public Object getCurrentItem() {
		return this.stack.last();
	}

	/**
	 * Get the previous Element
	 * 
	 * @return The Stack Element - offset
	 */
	public Object getPrevItem() {
		int pos = this.stack.size() - 2;
		if (pos < 0) {
			return null;
		}
		return this.stack.get(pos);
	}

	/**
	 * Add a new Reference Object to Stack.
	 * 
	 * @param tag     The new Tag
	 * @param item    new Reference Object
	 * @param creator The Creator for the Item
	 * @return XMLTokener Instance
	 */
	public MapEntityStack withStack(String tag, Object item, SendableEntityCreator creator) {
		if (creator == null) {
			return this;
		}
		stack.add(item, creator);
		tags.add(tag);
		String[] properties = creator.getProperties();
		for (String property : properties) {
			int lastPos = property.lastIndexOf(SimpleMap.ENTITYSPLITTER);
			if (lastPos >= 0) {
				String prop;
				if (lastPos == property.length() - 1) {
					/* Value of XML Entity like uni. */
					prop = ".";
				} else {
					prop = property.substring(lastPos + 1);
				}
				int pos = childProperties.indexOf(prop);
				if (pos >= 0) {
					childProperties.getValueByIndex(pos).add(property);
				} else {
					SimpleSet<String> child = new SimpleSet<String>();
					child.add(property);
					childProperties.put(prop, child);
				}
			}
		}
		return this;
	}

	/**
	 * Get the Current Creator for the MapEntity
	 *
	 * @return The Stack Element - offset
	 */
	public SendableEntityCreator getCurrentCreator() {
		return this.stack.getValueByIndex(this.stack.size() - 1);
	}

	public void setValue(String key, String value) {
		SimpleSet<String> set = childProperties.get(key);
		if (set != null) {
			for (String ChildKey : set) {
				int pos = getEntityPos(ChildKey);
				if (pos >= 0) {
					Object entity = stack.getKeyByIndex(pos);
					SendableEntityCreator creator = stack.getValueByIndex(pos);
					creator.setValue(entity, ChildKey, value, SendableEntityCreator.NEW);
				}
			}
		}
	}

	private int getEntityPos(String entity) {
		if (entity == null) {
			return -1;
		}
		int start = entity.lastIndexOf(SimpleMap.ENTITYSPLITTER);
		int pos = this.tags.size() - 1;
		for (int end = start - 1; end >= 0; end--) {
			if (entity.charAt(end) == SimpleMap.ENTITYSPLITTER) {
				String item = entity.substring(end + 1, start);
				String tag = tags.get(pos);
				if (tag == null || tag.equals(item) == false) {
					return -1;
				}
				start = end;
				pos--;
			}
		}
		return pos;
	}

	public String getCurrentTag() {
		if (this.tags.size() > 0) {
			return this.tags.get(this.tags.size() - 1);
		}
		return null;
	}
}
