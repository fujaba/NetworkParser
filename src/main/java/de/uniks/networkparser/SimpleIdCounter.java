package de.uniks.networkparser;

import java.sql.Time;
import java.sql.Timestamp;

import de.uniks.networkparser.interfaces.IdMapCounter;

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
 * The Class SimpleIdCounter.
 */

public class SimpleIdCounter implements IdMapCounter {
	/** The prefix id. */
	protected String session = null;

	/** The prio Object mostly a Timestamp or int value. */
	protected long timeStamp;


	/**
	 * Set the Session Prefix for a Peer
	 */
	@Override
	public IdMapCounter withSession(String value) {
		this.session = value;
		return this;
	}


	/**
	 * Set the Session Prefix for a Peer
	 */
	@Override
	public String getSession() {
		return this.session;
	}


	/**
	 * Get a new Id
	 */
	@Override
	public String getId(Object obj) {
		String key;

		// new object generate key and add to tables
		// <ShortClassName>#<Timestamp>
		if (obj == null) {
			return "";
		}
		String shortClassName = obj.getClass().getSimpleName();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		key = shortClassName + IdMap.ENTITYSPLITTER + timestamp.toString();
		return key;
	}

	//	/**
	//	 * Gets the prio.
	//	 *
	//	 * @return the prio
	//	 */
	//	@Override
	//	public Object getPrio() {
	//		return this.prio;
	//	}
	//
	//	/**
	//	 * Sets the prio.
	//	 *
	//	 * @param prio		the new prio
	//	 * @return 			Itself
	//	 */
	//	public SimpleIdCounter withPrio(Object prio) {
	//		this.prio = prio;
	//		return this;
	//	}
}
