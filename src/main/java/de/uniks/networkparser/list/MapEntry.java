package de.uniks.networkparser.list;

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

/**
 * The Class MapEntry.
 *
 * @author Stefan
 */
public class MapEntry extends SimpleEntity<String, Object> {
	
	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new MapEntry();
	}

	/**
	 * With key item.
	 *
	 * @param key the key
	 * @return the map entry
	 */
	@Override
	public MapEntry withKeyItem(Object key) {
		if (key instanceof String) {
			withKey((String) key);
		}
		return this;
	}

	/**
	 * With value item.
	 *
	 * @param value the value
	 * @return the map entry
	 */
	@Override
	public MapEntry withValueItem(Object value) {
		this.withValue(value);
		return this;
	}
	
    /**
     * Creates the.
     *
     * @param key the key
     * @param value the value
     * @return the map entry
     */
    public static MapEntry create(String key, String value) {
        MapEntry entry = new MapEntry();
        entry.withKey(key).withValue(value);
        return entry;
    }
    
    /**
     * Gets the value string.
     *
     * @return the value string
     */
    public String getValueString() {
        return "" + this.getValue();
    }
}
