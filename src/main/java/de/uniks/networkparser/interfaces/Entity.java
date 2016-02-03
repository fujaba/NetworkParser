package de.uniks.networkparser.interfaces;

import java.util.Set;

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
	public boolean containsKey(Object key);

	public String getString(String key);

	public boolean getBoolean(String key);

	public double getDouble(String key);

	public int getInt(String key);

	public Object getValue(String key);

	public int size();

	public Entity without(String key);

	public String getKeyByIndex(int pos);
	
	public Object getValueByIndex(int pos);
	
	/**
	 * @param key The Key for search
	 * @return success if the item has the Property
	 */
	public boolean has(String key);
	
	public Object remove(Object key);
	
	/** Add Key-Value item to Entity
	 * @param key The key
	 * @param value The new Value
	 * @return The value
	 */
	public Object put(String key, Object value);

	public Set<String> keySet();
	
	public String toString(int indentFactor, int intent);
}
