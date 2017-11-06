package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.list.AbstractList;

/**
 * Property List
 * 
 * @author Stefan
 *
 */
public class PropertyList extends AbstractList<String>{
	private String[] cache;
	
	public boolean add(String value) {
		boolean result = super.add(value);
		this.cache = null;
		return result;
	}
	
	public boolean addAll(String... values) {
		if(values == null) {
			return true;
		}
		for(String value : values) {
			if(super.add(value) == false) {
				this.cache = null;
				return false;
			}
		}
		this.cache = null;
		return true;
	}
	
	@Override
	public PropertyList getNewList(boolean keyValue) {
		return new PropertyList();
	}
	
	public String[] getList() {
		if(this.cache == null) {
			this.cache = this.toArray(new String[this.size()]);
		}
		return this.cache;
	}
	
	public static PropertyList create(String... properties) {
		PropertyList list = new PropertyList();
		if(properties != null) {
			for(String item : properties) {
				list.add(item);
			}
		}
		return list;
	}
}
