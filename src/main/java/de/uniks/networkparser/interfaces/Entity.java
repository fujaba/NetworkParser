package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.Buffer;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public interface Entity extends BaseItem{
	public String getString(String key);

	public Object getValue(Object key);

	public String getKeyByIndex(int pos);

	public Entity without(String key);
	
	/**
	 * @param key The Key for search
	 * @return success if the item has the Property
	 */
	public boolean has(String key);
	
	/** Add Key-Value item to Entity
	 * @param key The key
	 * @param value The new Value
	 * @return The value
	 */
	public Object put(String key, Object value);

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
	
	/**
	 * Activate Allow Empty Value
	 * @param allow is Empty Value (NULL) Allow
	 */
	public void setAllowEmptyValue(boolean allow);
	
	public boolean setValueItem(Object value);
	
	public BaseItem getChild(String label, boolean recursiv);
	
	public int size();
	
	public BaseItem withValue(Buffer values);
	
	public void setType(String type);
}
