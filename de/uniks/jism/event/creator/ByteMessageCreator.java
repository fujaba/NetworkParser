package de.uniks.jism.event.creator;

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

 THIS SOFTWARE 'Json Id Serialisierung Map' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
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

import de.uniks.jism.event.ByteMessage;
import de.uniks.jism.interfaces.ByteEntityCreator;

/**
 * The Class ByteMessageCreator.
 */
public class ByteMessageCreator implements ByteEntityCreator {

	/** The properties. */
	private final String[] properties = new String[] {ByteMessage.PROPERTY_VALUE};

	/*
	 * return the Properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/*
	 * Create new Instance of ByteMessage
	 */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new ByteMessage();
	}

	/*
	 * Get the EventTyp of BasicMessage (0x01)
	 */
	@Override
	public byte getEventTyp() {
		return 0x01;
	}

	/*
	 * Getter for ByteMessage
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		return ((ByteMessage) entity).get(attribute);
	}

	/*
	 * Setter for ByteMessage
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String typ) {
		return ((ByteMessage) entity).set(attribute, value);
	}
}
