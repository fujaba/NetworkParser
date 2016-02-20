package de.uniks.networkparser.interfaces;

import java.util.Comparator;

import de.uniks.networkparser.list.SimpleList;

public interface EntityList extends BaseItem{

	public SimpleList<EntityList> getChildren();
	
	/**
	 * Make a prettyprinted Text of this Entity.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor
	 *			The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation
	 *		 of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *		 brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *		 brace)</small>.
	 */
	public String toString(int indentFactor);
	
	public boolean isComparator();
	
	public Comparator<Object> comparator();
}
