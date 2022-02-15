package de.uniks.networkparser.gui.controls;

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
import de.uniks.networkparser.IdMap;

/**
 * The Class AutoCompletionList.
 *
 * @author Stefan
 */
public class AutoCompletionList extends Control {
	private boolean caseSensitive = false;
	private boolean sort = false;

	/* ANHAND EINER IDMAP */
	private IdMap map;
	private String property;

	/* COLLECTION with PROPERTY example: PersonSet or SimpleValue like Strings */

	/**
	 * With map.
	 *
	 * @param map the map
	 * @param property the property
	 * @return the auto completion list
	 */
	public AutoCompletionList withMap(IdMap map, String property) {
		this.map = map;
		this.property = property;
		return this;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public IdMap getMap() {
		return map;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * With case sensitive.
	 *
	 * @param value the value
	 * @return the auto completion list
	 */
	public AutoCompletionList withCaseSensitive(boolean value) {
		this.caseSensitive = value;
		return this;
	}

	/**
	 * Checks if is case sensitive.
	 *
	 * @return true, if is case sensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * With sorted.
	 *
	 * @param value the value
	 * @return the auto completion list
	 */
	public AutoCompletionList withSorted(boolean value) {
		this.sort = value;
		return this;
	}

	/**
	 * Checks if is sorted.
	 *
	 * @return true, if is sorted
	 */
	public boolean isSorted() {
		return sort;
	}

	/**
	 * New instance.
	 *
	 * @return the auto completion list
	 */
	@Override
	public AutoCompletionList newInstance() {
		return new AutoCompletionList();
	}
}
