package de.uniks.networkparser.interfaces;

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
/**
 * The Interface IdMapCounter.
 */

public interface IdMapCounter {
	/**
	 * Sets the prefix for The Id of Elements
	 *
	 * @param sessionId
	 *			the new prefix id
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
	 * @param splitter
	 *			the new splitter character for the session id
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
	 * @param obj
	 *			the obj
	 * @return the id
	 */
	public String getId(Object obj);

	/**
	 * Read id.
	 *
	 * @param id
	 *			the last id from Message
	 */
	public void readId(String id);

	/**
	 * @return the Prio Object for checking errors
	 */
	public Object getPrio();
}
