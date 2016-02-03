package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.list.SimpleList;

public interface XMLitem extends Entity {
	public String getTag();
	

	public String getValueItem();
	
	public XMLitem withValueItem(String value);

	/** Get all children
	 * @return all Children
	 */
	public SimpleList<XMLitem> getChildren();

	public boolean addChild(XMLitem child);

	public XMLitem getChild(String value, boolean recursiv);
}
