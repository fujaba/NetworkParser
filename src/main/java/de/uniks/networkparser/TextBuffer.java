package de.uniks.networkparser;

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
import de.uniks.networkparser.interfaces.BufferedBuffer;
/**
 * Buffer of String for alternative for StringBuffer.
 *
 */

public abstract class TextBuffer extends BufferedBuffer {
	/** The line. */
	protected int line;

	/** The character. */
	protected int character;

	@Override
	public void back() {
		super.back();
		this.character -= 1;
	}

	@Override
	public String toString() {
		return " at " + this.position + " / "+ this.length + "[character " + this.character
				+ " line " + this.line + "]";
	}
}
