package de.uniks.jism;

public class CharacterBuffer implements Buffer{
	/** The buffer. */
	protected char[] buffer;

	/** The length. */
	private int length;
	
	/** The index. */
	protected int index;
	
	/** The line. */
	protected int line;

	/** The character. */
	protected int character;

	
	public CharacterBuffer(String value){
		this.buffer = value.toCharArray();
		this.length = buffer.length; 
	}

	public int length() {
		return length;
	}
	public char charAt(int index){
		return buffer[index];
	}
	public String substring2(int startTag, int length){
		return new String(buffer, startTag, length);
	}

	@Override
	public Buffer withLength(int value) {
		this.length = value;
		return this;
	}

	@Override
	public Byte get(int pos) {
		return (byte) buffer[pos];
	}

	@Override
	public int position() {
		return index;
	}

	@Override
	public void back() {
		this.index -= 1;
		this.character -= 1;
	}

	@Override
	public boolean isEnd() {
		return length <= this.index;
	}

	@Override
	public char nextChar() {
		char c = this.buffer[this.index];
		this.index++;
		if (c == '\r') {
			this.line += 1;
			if (this.buffer[this.index] == '\n') {
				this.character = 1;
				this.index++;
				c = '\n';
			} else {
				this.character = 0;
			}
		} else if (c == '\n') {
			this.line += 1;
			this.character = 0;
		} else {
			this.character += 1;
		}
		return c;
	}

	@Override
	public int remaining() {
		return length - index;
	}
	
	@Override
	public String toString() {
		return " at " + this.index + " [character " + this.character + " line "
				+ this.line + "]";
	}

	@Override
	public Buffer setPosition(int index) {
		this.index = index;
		return this;
	}
}
