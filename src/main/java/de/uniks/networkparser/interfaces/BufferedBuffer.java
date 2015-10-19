package de.uniks.networkparser.interfaces;

public abstract class BufferedBuffer extends Buffer {
	/** The length. */
	protected int length;

	public void back() {
		if (this.position > 0) {
			this.position--;
		}
	}

	public BufferedBuffer withPosition(int value) {
		this.position = value;
		return this;
	}

	@Override
	public int length() {
		return length;
	}
	
	public BufferedBuffer withLength(int value) {
		this.length = value;
		return this;
	}

	public abstract byte byteAt(int index);

	public abstract char charAt(int index);
	
	/**
	 * @return The currentChar
	 */
	public char getCurrentChar() {
		return charAt(position());
	}


	/**
	 * @param start
	 *            startindex for parsing
	 * @param length
	 *            the length of Substring
	 * @return the Substring
	 */
	public abstract String substring(int start, int length);
	
	@Override
	public BufferedBuffer withLookAHead(String lookahead) {
		if(lookahead == null) {
			return this;
		}
		this.withPosition(this.position() - lookahead.length() + 1);
		return this;
	}
}
