package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

/**
 * The Class EntityStringConverter.
 *
 * @author Stefan
 */
public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int indent;
	private String relativePath;
	
	/** The Constant EMPTY. */
	public static final String EMPTY = "";

	/**
	 * Instantiates a new entity string converter.
	 */
	public EntityStringConverter() {
	}

	/**
	 * Instantiates a new entity string converter.
	 *
	 * @param indentFactor the indent factor
	 * @param indent the indent
	 */
	public EntityStringConverter(int indentFactor, int indent) {
		this.indentFactor = indentFactor;
		this.indent = indent;
	}

	/**
	 * Instantiates a new entity string converter.
	 *
	 * @param indentFactor the indent factor
	 */
	public EntityStringConverter(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	/**
	 * With path.
	 *
	 * @param path the path
	 * @return the entity string converter
	 */
	public EntityStringConverter withPath(String path) {
		this.relativePath = path;
		return this;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return relativePath;
	}

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @return the string
	 */
	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof Entity) {
			return ((Entity) entity).toString(getIndentFactor());
		}
		if (entity != null) {
			return entity.toString(this);
		}
		return null;
	}

	/**
	 * Gets the indent factor.
	 *
	 * @return the indent factor
	 */
	public int getIndentFactor() {
		return indentFactor;
	}

	/**
	 * Gets the indent.
	 *
	 * @return the indent
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * Gets the step.
	 *
	 * @return the step
	 */
	public String getStep() {
		char[] buf = new char[indentFactor];
		for (int i = 0; i < indentFactor; i++) {
			buf[i] = SimpleMap.SPACE;
		}
		return new String(buf);
	}

	/**
	 * Gets the prefix first.
	 *
	 * @return the prefix first
	 */
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

	/**
	 * Gets the prefix.
	 *
	 * @return the prefix
	 */
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

	/**
	 * Adds the.
	 */
	public void add() {
		this.indent = this.indent + this.indentFactor;
	}

	/**
	 * Minus.
	 */
	public void minus() {
		this.indent = this.indent - this.indentFactor;
	}
}
