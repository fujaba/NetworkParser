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
 * The Interface IdMapCounter.
 */

public interface IdMapCounter {
	/**
	 * Sets the prefix for The Id of Elements
	 *
	 * @param sessionId		the new prefix id
	 * @return Itself
	 */
	public IdMapCounter withPrefixId(String sessionId);

	/**
	 * @return the current sessionid
	 */
	public String getPrefixId();

	/**
	 * Sets the splitter for The session id
	 *
	 * @param splitter	the new splitter character for the session id
	 * @return Itself
	 */
	public IdMapCounter withSplitter(char splitter);

	/**
	 * @return the current splitterString
	 */
	public char getSplitter();

	/**
	 * Gets the id.
	 *
	 * @param obj	the obj
	 * @return 		the id
	 */
	public String getId(Object obj);

	/**
	 * Read id.
	 *
	 * @param id	the last id from Message
	 */
	public void readId(String id);

	/**
	 * @return the Prio Object for checking errors
	 */
	public Object getPrio();
}
