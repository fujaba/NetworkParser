package de.uniks.networkparser;

public class SimpleException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Object source;

	public SimpleException(String msg) {
		super(msg);
	}

	public SimpleException(String msg, Object source) {
		super(msg);
		this.source = source;
	}

	public SimpleException(Throwable e) {
		super(e);
	}

	public Object getSource() {
		return source;
	}
}
