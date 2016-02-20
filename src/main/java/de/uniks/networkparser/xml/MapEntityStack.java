package de.uniks.networkparser.xml;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class MapEntityStack {
	/** The Stack. */
	private SimpleKeyValueList<Object, SendableEntityCreator> stack = new SimpleKeyValueList<Object, SendableEntityCreator>();
	
	private SimpleList<String> tags = new SimpleList<String>();

	private SimpleKeyValueList<String, SimpleSet<String>> childProperties= new SimpleKeyValueList<String, SimpleSet<String>>();
	
	private SendableEntityCreator defaultFactory;

	/** Variable of AllowQuote. */
//	private boolean isAllowQuote;
	
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
	
	/**
	 * @return The Stack Element - offset
	 */
	public Object getCurrentItem() {
		return this.stack.last();
	}
	

	/**
	 * Add a new Reference Object to Stack.
	 * @param tag	The new Tag
	 * @param item 	new Reference Object
	 * @param creator The Creator for the Item
	 * @return XMLTokener Instance
	 */
	public MapEntityStack withStack(String tag, Object item, SendableEntityCreator creator) {
		stack.add(item, creator);
		tags.add(tag);
		String[] properties = creator.getProperties();
		for(String property : properties) {
			int lastPos = property.lastIndexOf(IdMap.ENTITYSPLITTER);
			if(lastPos >= 0) {
				String prop;
				if(lastPos == property.length() - 1) {
					// Value of XML Entity like uni.
					prop = ".";
				} else {
					prop = property.substring(lastPos + 1);
				}
				int pos = childProperties.indexOf(prop);
				if(pos>=0) {
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
	 * @return The Stack Element - offset
	 */
	public SendableEntityCreator getCurrentCreator() {
		return this.stack.getValueByIndex(this.stack.size() - 1);
	}
	
	public void setValue(String key, String value) {
		SimpleSet<String> set = childProperties.get(key);
		if(set != null) {
			for(String ChildKey : set) {
				int pos = getEntityPos(ChildKey);
				if(pos >= 0 ) {
					Object entity = stack.getKeyByIndex(pos);
					SendableEntityCreator creator = stack.getValueByIndex(pos);
					creator.setValue(entity, ChildKey, value, XMLIdMap.NEW);
				}
			}
		}
	}
	
	private int getEntityPos(String entity) {
		int start=entity.lastIndexOf(XMLIdMap.ENTITYSPLITTER);
		int pos = this.tags.size() - 1;
		for(int end=start-1;end>=0;end --) {
			if(entity.charAt(end) ==XMLIdMap.ENTITYSPLITTER) {
				String item = entity.substring(end+1, start);
				String tag = tags.get(pos);
				if(tag == null || tag.equals(item) == false) {
					return -1;
				}
				start = end;
				pos--;
			}
		}
		return pos;
	}

	public String getCurrentTag() {
		if(this.tags.size() >0 ){
			return this.tags.get(this.tags.size() - 1);
		}
		return null;
	}

	/**
	 * @return the defaultFactory
	 */
	public SendableEntityCreator getDefaultFactory() {
		return defaultFactory;
	}

	/**
	 * @param defaultFactory the defaultFactory to set
	 * @return ThisComponent
	 */
	public MapEntityStack withDefaultFactory(SendableEntityCreator defaultFactory) {
		this.defaultFactory = defaultFactory;
		return this;
	}

//FIXME REMOVE	/**
//	 * @param value
//	 *			of AllowQuote
//	 * @return XMLTokener Instance
//	 */
//	public MapEntityStack withAllowQuote(boolean value) {
//		this.isAllowQuote = value;
//		return this;
//	}
//	
//	public boolean AllowQuote() {
//		return isAllowQuote;
//	}
}
