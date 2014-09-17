package de.uniks.networkparser.test.ant.sources;

public interface FilePart {
	public void append(String value);
	public int length();
	@Override
	public String toString();
	public void finish();
}
