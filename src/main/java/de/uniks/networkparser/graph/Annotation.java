package de.uniks.networkparser.graph;

import java.util.List;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.list.SimpleList;

/**
 * Annotation of Methods or Attributes or Classes.
 *
 * @author Stefan Lindel
 */
public class Annotation extends GraphMember {
	
	/** The Constant DEPRECATED. */
	public static final Annotation DEPRECATED = new Annotation("Deprecated");

	/** The Constant OVERRIDE. */
	public static final Annotation OVERRIDE = new Annotation("Override");

	/** The Constant SAFE_VARGARGS. */
	public static final Annotation SAFE_VARGARGS = new Annotation("SafeVarargs");

	/** The Constant SUPPRESS_WARNINGS. */
	public static final Annotation SUPPRESS_WARNINGS = new Annotation("SuppressWarnings");

	private boolean keyValue;
	private Annotation nextAnnotaton;
	private String scope;

	Annotation() {
	}

	/**
	 * Instantiates a new annotation.
	 *
	 * @param name the name
	 */
	public Annotation(String name) {
		super.with(name);
	}

	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @param values the values
	 * @return the annotation
	 */
	public static Annotation create(String value, String... values) {
		Annotation annotation = new Annotation();
		if (values == null || values.length < 1) {
			annotation.decode(value);
		} else {
			annotation.setName(value);
			if (values.length % 2 == 0) {
				for (int i = 0; i < values.length; i += 2) {
					annotation.addValue(new Annotation().withKeyValue(values[i], values[i + 1]));
				}
			}
		}
		return annotation;
	}

	/**
	 * With key value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the annotation
	 */
	public Annotation withKeyValue(String key, String value) {
		this.name = key;
		this.keyValue = true;
		SimpleList<Annotation> list = new SimpleList<Annotation>();
		if (value != null && value.length() > 0) {
			if (value.indexOf('"') < 1 && value.charAt(0) != '{') {
				list.add(new Annotation("\"" + value + "\""));
			} else {
				list.add(new Annotation(value));
			}
		}
		this.children = list;
		return this;
	}

	/**
	 * New instance.
	 *
	 * @return the annotation
	 */
	public Annotation newInstance() {
		Annotation annotation = new Annotation();
		annotation.decode(this.name);
		return annotation;
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the annotation
	 */
	/* Redirect */
	@Override
	public Annotation with(String name) {
		super.with(name);
		return this;
	}

	/**
	 * Decode.
	 *
	 * @param value the value
	 * @return the annotation
	 */
	public Annotation decode(String value) {
		CharacterBuffer tokener = new CharacterBuffer();
		tokener.with(value);
		decode(tokener, (char) 0, null);
		return this;
	}

	/**
	 * With next.
	 *
	 * @param annotation the annotation
	 * @return the annotation
	 */
	public Annotation withNext(Annotation annotation) {
		this.nextAnnotaton = annotation;
		return this;
	}

	protected Annotation addValue(Annotation... values) {
		if (values == null) {
			return this;
		}
		SimpleList<?> list;
		if (this.children == null) {
			list = new SimpleList<Object>();
			this.children = list;
		} else {
			list = (SimpleList<?>) children;
		}
		for (Annotation item : values) {
			if (item != null) {
				list.add(item);
			}
		}
		return this;
	}

	/**
	 * decode BufferItem.
	 * @param tokener Tokener
	 * @param endTag Character of EndTag
	 * @param parent Parent parent
	 * @return ThisComponent
	 */
	public Annotation decode(BufferItem tokener, char endTag, Annotation parent) {
		if (tokener == null) {
			return null;
		}
		char item = tokener.getCurrentChar();
		CharacterBuffer token = new CharacterBuffer();
		boolean charCount = false;
		while (item != 0 && item != endTag) {
			if (item == '"') {
				charCount = !charCount;
			}
			if (charCount) {
				token.with(item);
				item = tokener.getChar();
				continue;
			}
			if (item == ' ') {
				item = tokener.getChar();
				continue;
			}
			/* Subannotation */
			if (item == '(') {
				this.name = token.toString();
				tokener.skip();
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, ')', this);
				return this;
			} else if (item == '{') {
				this.name = token.toString();
				tokener.skip();
				decode(tokener, '}', parent);
				return this;
			} else if (item == '=') {
				this.name = token.toString();
				this.keyValue = true;
				tokener.skip();
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, endTag, parent);
				item = tokener.getCurrentChar();
				if (item != ',') {
					break;
				}
			}
			if (item == ',') {
				this.name = token.toString();
				tokener.skip();
				if (parent != null) {
					Annotation child = new Annotation();
					parent.addValue(child);
					child.decode(tokener, endTag, parent);
				}
				break;
			}
			token.with(item);
			item = tokener.getChar();

			if (item == '@') {
				this.name = token.toString();
				this.nextAnnotaton = new Annotation().decode(tokener, (char) 0, null);
				return this;
			}
		}
		if (item == 0 || item == endTag) {
			this.name = token.toString();
		}
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public SimpleList<Annotation> getValue() {
		SimpleList<Annotation> list = new SimpleList<Annotation>();
		if (children != null) {
			if (children instanceof Annotation) {
				list.add(children);
			} else if (children instanceof List<?>) {
				List<?> collection = (List<?>) children;
				for (Object item : collection) {
					if (item instanceof Annotation) {
						list.add(item);
					}
				}
			}
		}
		return list;
	}

	/**
	 * With import.
	 *
	 * @param item the item
	 * @return the annotation
	 */
	public Annotation withImport(String item) {
		Import importItem = Import.create(item);
		if (this.children == null) {
			this.children = importItem;
		} else if (children instanceof SimpleList<?>) {
			SimpleList<?> list = (SimpleList<?>) children;
			list.add(importItem);
		} else {
			SimpleList<Object> list = new SimpleList<Object>();
			list.add(children);
			list.add(importItem);
			this.children = list;
		}
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		if (children == null) {
			return sb.toString();
		}
		if (children instanceof SimpleList<?>) {
			SimpleList<?> list = (SimpleList<?>) children;
			if (keyValue && list.size() == 1) {
				sb.append("=");
				sb.append(list.first().toString());
				return sb.toString();
			}
			sb.append("(");
			if (list.size() > 0) {
				sb.append(list.first());
			}
			for (int i = 1; i < list.size(); i++) {
				Object child = list.get(i);
				if (!(child instanceof Import)) {
					sb.append(",");
					sb.append(list.get(i));
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return nextAnnotaton != null;
	}

	/**
	 * Next.
	 *
	 * @return the annotation
	 */
	public Annotation next() {
		return nextAnnotaton;
	}

	/**
	 * Gets the annotation.
	 *
	 * @param key the key
	 * @return the annotation
	 */
	public Annotation getAnnotation(String key) {
		if (key == null) {
			return null;
		}
		if (key.equalsIgnoreCase(getName())) {
			return this;
		}
		if (nextAnnotaton == null) {
			return null;
		}
		return nextAnnotaton.getAnnotation(key);
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public GraphMember getParent() {
		return (GraphMember) parentNode;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * With scope.
	 *
	 * @param scope the scope
	 * @return the annotation
	 */
	public Annotation withScope(String scope) {
		this.scope = scope;
		return this;
	}
}
