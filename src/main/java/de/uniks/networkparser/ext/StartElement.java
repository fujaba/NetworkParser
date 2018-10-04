package de.uniks.networkparser.ext;

/*
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

public class StartElement {
	private String key;
	private String label;
	private String description;
	private Object defaultValue;
	private Object value;

	/** @return the label */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 * @return ThisComponent
	 */
	public StartElement withLabel(String label) {
		this.label = label;
		return this;
	}

	/** @return the description */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 * @return ThisComponent
	 */
	public StartElement withDescription(String description) {
		this.description = description;
		return this;
	}

	/** @return the values */
	public Object getDefaultValues() {
		return defaultValue;
	}

	/**
	 * @param values the values to set
	 * @return ThisComponent
	 */
	public StartElement withDefaultValues(Object values) {
		this.defaultValue = values;
		return this;
	}

	/** @return the key */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 * @return ThisComponent
	 */
	public StartElement withKey(String key) {
		this.key = key;
		return this;
	}

	/** @return the value */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 * @return ThisComponent
	 */
	public StartElement withValue(Object value) {
		this.value = value;
		return this;
	}
}
