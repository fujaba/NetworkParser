package de.uniks.jism.exceptions;

import de.uniks.jism.Tokener;

public class TextParsingException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String message;
	private String tokenerMsg;
	private int index;
	
	public TextParsingException(String message, Tokener tokener) {
		this.message = message;
		this.tokenerMsg=tokener.toString();
		this.index=tokener.getIndex();
	}

	public String getMessage() {
		return message;
	}

	public String getTokenerMsg() {
		return tokenerMsg;
	}

	public int getIndex() {
		return index;
	}
}
