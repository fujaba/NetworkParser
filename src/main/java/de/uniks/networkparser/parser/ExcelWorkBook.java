package de.uniks.networkparser.parser;

import de.uniks.networkparser.list.SimpleList;

public class ExcelWorkBook extends SimpleList<ExcelSheet> {
	public static final String PROPERTY_AUTHOR = "author";
	private String author;

	public String getAuthor() {
		return author;
	}

	public boolean setAuthor(String value) {
		if ((this.author == null && value != null) || (this.author != null && this.author.equals(value) == false)) {
			this.author = value;
			return true;
		}
		return false;
	}

	public ExcelWorkBook withAuthor(String value) {
		setAuthor(value);
		return this;
	}
}
