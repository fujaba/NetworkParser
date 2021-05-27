package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int indent;
	private String relativePath;
	public static final String EMPTY = "";

	public EntityStringConverter() {
	}

	public EntityStringConverter(int indentFactor, int indent) {
		this.indentFactor = indentFactor;
		this.indent = indent;
	}

	public EntityStringConverter(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	public EntityStringConverter withPath(String path) {
		this.relativePath = path;
		return this;
	}

	public String getPath() {
		return relativePath;
	}

	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof Entity) {
			return ((Entity) entity).toString(getIndentFactor());
		}
		if (entity != null) {
			return ((BaseItem) entity).toString(this);
		}
		return null;
	}

	public int getIndentFactor() {
		return indentFactor;
	}

	public int getIndent() {
		return indent;
	}

	public String getStep() {
		char[] buf = new char[indentFactor];
		for (int i = 0; i < indentFactor; i++) {
			buf[i] = SimpleMap.SPACE;
		}
		return new String(buf);
	}

	public String getPrefixFirst() {
		if (indent < 1) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i + 2] = SimpleMap.SPACE;
		}
		return new String(buf);
	}

	public String getPrefix() {
		if (indent + indentFactor == 0) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i + 2] = SimpleMap.SPACE;
		}
		return new String(buf);
	}

	public void add() {
		this.indent = this.indent + this.indentFactor;
	}

	public void minus() {
		this.indent = this.indent - this.indentFactor;
	}
}
