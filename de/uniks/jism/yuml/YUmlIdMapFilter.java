package de.uniks.jism.yuml;

import java.util.HashMap;
import java.util.Set;

import de.uniks.jism.IdMapFilter;

public class YUmlIdMapFilter extends IdMapFilter{
	/** The link cardinality. */
	private HashMap<String, String> linkCardinality = new HashMap<String, String>();

	/** The link property. */
	private HashMap<String, String> linkProperty = new HashMap<String, String>();

	/** The value yuml. */
	private HashMap<String, String> valueYUML = new HashMap<String, String>();

	/** The show line. */
	private boolean isShowLine;
	
	private boolean isShowCardinality;
	
	public YUmlIdMapFilter(boolean showCardinality){
		this.isShowCardinality = showCardinality;
	}
	
	public void reset(){
		this.valueYUML.clear();
		this.linkProperty.clear();
		this.linkCardinality.clear();
	}

	public String getLinkCardinality(String key) {
		return linkCardinality.get(key);
	}
	
	public boolean addLinkCardinality(String key, String value) {
		linkCardinality.put(key, value);
		return true;
	}
	
	public Set<String> getLinkPropertys() {
		return linkProperty.keySet();
	}
	
	
	public String getLinkProperty(String key) {
		return linkProperty.get(key);
	}
	
	public boolean addLinkProperty(String key, String value) {
		linkProperty.put(key, value);
		return true;
	}
	
	public String getValueYUML(String key) {
		return valueYUML.get(key);
	}
	
	public boolean addValueYUML(String key, String value) {
		valueYUML.put(key, value);
		return true;
	}
	
	public String removeValueYUML(String key) {
		return valueYUML.remove(key);
	}
	
	public boolean containsKeyValueYUML(String key){
		return valueYUML.containsKey(key);
	}

	/**
	 * Checks if is show line.
	 *
	 * @return true, if is show line for objects
	 */
	public boolean isShowLine() {
		return this.isShowLine;
	}

	/**
	 * Sets the show line.
	 *
	 * @param value
	 *			the new show line
	 */
	public void setShowLine(boolean value) {
		this.isShowLine = value;
	}

	public boolean isShowCardinality() {
		return isShowCardinality;
	}

	public void setShowCardinality(boolean showCardinality) {
		this.isShowCardinality = showCardinality;
	}
}
