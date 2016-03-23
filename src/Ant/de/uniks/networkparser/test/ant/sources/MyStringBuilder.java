package de.uniks.networkparser.test.ant.sources;

public class MyStringBuilder implements FilePart{
	private StringBuilder sb = new StringBuilder();

	@Override
	public void append(String value) {
		sb.append(value);
	}

	@Override
	public int length() {
		return sb.length();
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	public void finish() {
	}
}
