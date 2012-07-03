package de.uni.kassel.peermessage;

/*
Copyright (c) 2012, Stefan Lindel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
   This product includes software developed by Stefan Lindel.
4. Neither the name of contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import de.uni.kassel.peermessage.interfaces.IdMapCounter;


/**
 * The Class SimpleIdCounter.
 */
public class SimpleIdCounter implements IdMapCounter{
	
	/** The prefix id. */
	protected String prefixId="J1";
	
	/** The number. */
	protected long number = 1;
	
	private char splitter='.';
	
	/** 
	 * Set the Session Prefix for a Peer
	 */
	public void setPrefixId(String sessionId) {
		this.prefixId = sessionId;
	}

	/** 
	 * Get a new Id
	 */
	public String getId(Object obj) {
		String key;

		// new object generate key and add to tables
		// <session id>.<first char><running number>
		if (obj == null) {
			try {
				throw new Exception("NullPointer: " + obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String className = obj.getClass().getName();
		char firstChar = className.charAt(className.lastIndexOf(splitter) + 1);
		if (prefixId != null) {
			key = prefixId + splitter + firstChar + number;
		} else {
			key = "" + firstChar + number;
		}
		number++;
		return key;
	}

	/**
	 * Read a Id from jsonString
	 */
	public void readId(String jsonId) {
		// adjust number to be higher than read numbers
		String[] split = jsonId.split("\\"+splitter);

		if (split.length != 2) {
			throw new RuntimeException("jsonid " + jsonId
					+ " should have one "+splitter+" in its middle");
		}
		if (prefixId.equals(split[0])) {
			String oldNumber = split[1].substring(1);
			long oldInt = Long.parseLong(oldNumber);
			if (oldInt >= number) {
				number = oldInt + 1;
			}
		}
	}

	public char getSplitter() {
		return splitter;
	}

	public void setSplitter(char splitter) {
		this.splitter = splitter;
	}
}
