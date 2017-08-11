package de.uniks.networkparser.interfaces;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.buffer.Buffer;

public interface Entity extends BaseItem{
	public String getString(String key);

	public Object getValue(Object key);
	
	public String getKeyByIndex(int pos);
	
	public Object getValueByIndex(int index);

	public Entity without(String key);

	/**
	 * check if Entity has the Key
	 *
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
	 * @param indentFactor	The number of spaces to add to each level of indentation.
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

	public BaseItem getElementBy(String key, String value);

	public BaseItem withValue(Buffer values);

	public BaseItem setType(String type);
}
