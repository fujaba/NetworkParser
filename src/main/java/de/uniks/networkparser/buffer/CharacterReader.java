package de.uniks.networkparser.buffer;

public class CharacterReader extends CharacterBuffer{
    /** The line. */
    protected int line;

    /** The character. */
    protected int character;
    
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
}
