package de.uniks.networkparser;

public class SimpleException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final transient Object source;

  public SimpleException(String msg) {
    super(msg);
    this.source = null;
  }

  public SimpleException(String msg, Object source) {
    super(msg);
    this.source = source;
  }

  public SimpleException(Throwable e) {
    super(e);
    this.source = null;
  }

  public Object getSource() {
    return source;
  }
}
