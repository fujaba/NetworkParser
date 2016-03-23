package de.uniks.networkparser.test.ant.ikvm;

public class Contains implements Filter {
	private String fragment;

	public void addText (String fragment) {
		this.fragment = fragment;
	}

	public boolean suppress (String output) {
		return output.contains(fragment);
	}

}
