package de.uniks.networkparser.json;

/*
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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

/**
 * The Class YamlItem.
 *
 * @author Stefan
 */
public class YamlItem implements BaseItem {
	private Object key;
	private Object value;
	private String comment;

	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * With comment.
	 *
	 * @param comment the comment
	 * @return the yaml item
	 */
	public YamlItem withComment(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the yaml item
	 */
	public YamlItem withValue(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * With key.
	 *
	 * @param key the key
	 * @return the yaml item
	 */
	public YamlItem withKey(Object key) {
		this.key = key;
		return this;
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		return null;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		return false;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new YamlEntity();
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return 1;
	}
}
