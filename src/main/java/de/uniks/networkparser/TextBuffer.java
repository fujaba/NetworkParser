package de.uniks.networkparser;

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
