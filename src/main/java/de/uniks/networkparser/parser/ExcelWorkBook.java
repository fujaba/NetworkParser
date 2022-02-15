package de.uniks.networkparser.parser;

import de.uniks.networkparser.list.SimpleList;

/**
 * The Class ExcelWorkBook.
 *
 * @author Stefan
 */
public class ExcelWorkBook extends SimpleList<ExcelSheet> {
	
	/** The Constant PROPERTY_AUTHOR. */
	public static final String PROPERTY_AUTHOR = "author";
	private String author;

	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setAuthor(String value) {
		if ((this.author == null && value != null) || (this.author != null && this.author.equals(value) == false)) {
			this.author = value;
			return true;
		}
		return false;
	}

	/**
	 * With author.
	 *
	 * @param value the value
	 * @return the excel work book
	 */
	public ExcelWorkBook withAuthor(String value) {
		setAuthor(value);
		return this;
	}
}
