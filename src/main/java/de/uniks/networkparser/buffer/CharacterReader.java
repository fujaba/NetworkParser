package de.uniks.networkparser.buffer;

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

public class CharacterReader extends CharacterBuffer{
	/** The line. */
    protected int line;

    /** The character. */
    protected int character;

	/** Is Last String is \"String\" or Text */
	private boolean isString = true;

	public boolean isString() {
		return isString;
	}

	public CharacterReader withString(boolean isString) {
		this.isString = isString;
		return this;
	}

    public boolean back() {
    	if(super.back()) {
    		this.character -= 1;
    		return true;
    	}
    	return false;
    }

    @Override
	public char getChar() {
		char c = super.getChar();
		if (c == '\n') {
			this.line += 1;
			this.character = 0;
		} else {
			this.character += 1;
		}
		return c;
	}

    @Override
    public CharacterBuffer nextString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
    	if(quotes ==null) {
    		return sc;
    	}

    	boolean found=false;
    	for(char quote : quotes) {
			if ('"' == quote) {
				found=true;
				if (getCurrentChar() == quote) {
					isString = true;
				} else {
					isString = !isString;
				}
			}
    	}
    	if (found == false && getCurrentChar() == '"') {
			isString = true;
			for (;;) {
				int len = sc.length();
				super.nextString(sc, allowQuote, nextStep, quotes);
				if (sc.length()>len && !sc.endsWith("\"", false)) {
					sc.with(',');
				} else {
					break;
				}
			}
			return sc;
		}
		super.nextString(sc, allowQuote, nextStep, quotes);
		return sc;
	}

	public CharacterReader with(CharSequence... items) {
		super.with(items);
		return this;
	}

}
