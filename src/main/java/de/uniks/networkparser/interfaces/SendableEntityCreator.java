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
/**
 * The Interface SendableEntityCreator. This is the Basic Interface for the
 * Creator for Serialization
 */

public interface SendableEntityCreator {
	/** The Constant REMOVE_YOU. */
	public static final String REMOVE_YOU = "REMOVE_YOU";

	/** The Constant REMOVE. */
	public static final String REMOVE = "rem";

	/** The Constant UPDATE. */
	public static final String UPDATE = "upd";

	/** The Constant NEW. */
	public static final String NEW = "new";

	/** The Constant SimpleFormat. */
	public static final String SIMPLE = "simple";

	/** The Constant Dynamic for save additional values. */
	public static final String DYNAMIC = "dynamic";

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public String[] getProperties();

	/**
	 * Gets the value.
	 *
	 * @param entity    the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	public Object getValue(Object entity, String attribute);

	/**
	 * Sets the value.
	 *
	 * @param entity    the entity
	 * @param attribute the attribute
	 * @param value     the value
	 * @param type      edit, update or remove operation
	 * @return true, if successful
	 */
	public boolean setValue(Object entity, String attribute, Object value, String type);

	/**
	 * Gets the sendable instance.
	 * 
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	Object getSendableInstance(boolean prototyp);
}
