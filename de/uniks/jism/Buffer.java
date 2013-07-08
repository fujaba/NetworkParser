package de.uniks.jism;

public interface Buffer {
	public int length();
	public char charAt(int index);
	public char nextChar();
	public String substring2(int startTag, int length);
	public Buffer withLength(int length);
	public Byte get(int pos);
	public int position();
	public int remaining();
	public void back();
	public boolean isEnd();
	public Buffer setPosition(int index);
}
