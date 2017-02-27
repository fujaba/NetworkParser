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
 * The Class BaseEntity.
 * @author Stefan Lindel
 */

public interface BaseItem {
	public static final String CRLF = "\r\n";
	
	/** The Constant CLASS. */
	public static final String CLASS = "class";

	/**
	 * Convert Element to String
	 * @return the Item as String
	 */
	@Override
	public String toString();

	/**
	 * Convert Element to String
	 * @param converter	Converter for Format
	 * @return the Item as String with converter
	 */
	public String toString(Converter converter);

	/** Add Elements to List or KeyValue
	 * if param Modulo 2 the Params can be Key,Value
	 * or add all Values  to List
	 * @param values Items to Add to List
	 * @return this Component
	 */
	BaseItem with(Object... values);
	
	public BaseItem getNewList(boolean keyValue);

	/** Get the Size of Elements
	 * @return the size
	 */
	public int size();
}
