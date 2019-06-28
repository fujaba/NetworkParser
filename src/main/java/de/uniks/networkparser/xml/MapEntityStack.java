package de.uniks.networkparser.xml;

/*
NetworkParser
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
import de.uniks.networkparser.IdMap;
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
			int lastPos = property.lastIndexOf(IdMap.ENTITYSPLITTER);
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
		int start = entity.lastIndexOf(IdMap.ENTITYSPLITTER);
		int pos = this.tags.size() - 1;
		for (int end = start - 1; end >= 0; end--) {
			if (entity.charAt(end) == IdMap.ENTITYSPLITTER) {
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
