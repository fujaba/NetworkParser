package de.uniks.networkparser;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.List;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class TextDiff.
 *
 * @author Stefan
 */
public class TextDiff {
	
	/** The Constant NEW. */
	public static final char NEW = '+';
	
	/** The Constant NONE. */
	public static final char NONE = ' ';
	
	/** The Constant CHANGE. */
	public static final char CHANGE = '#';
	
	/** The Constant REMOVE. */
	public static final char REMOVE = '-';
	
	/** The Constant TO. */
	public static final String TO = "->";

	private String key;
	private char type;
	private Object left;
	private Object right;
	private SimpleList<TextDiff> children;

	/**
	 * With.
	 *
	 * @param key the key
	 * @param left the left
	 * @param right the right
	 * @return the text diff
	 */
	public TextDiff with(String key, Object left, Object right) {
		this.left = left;
		this.right = right;
		this.key = key;
		if (left == null) {
			if (right == null) {
				this.type = NONE;
			} else {
				this.type = NEW;
			}
		} else {
			if (right == null) {
				this.type = REMOVE;
			} else if (left.equals(right)) {
				this.type = NONE;
			} else {
				this.type = CHANGE;
			}
		}
		return this;
	}

	/**
	 * Replace child.
	 *
	 * @param last the last
	 * @param key the key
	 * @param left the left
	 * @param right the right
	 * @return the text diff
	 */
	public TextDiff replaceChild(TextDiff last, String key, Object left, Object right) {
		TextDiff lastChild = null;
		if (this.children != null) {
			TextDiff child = new TextDiff();
			child.with(key, left, right);
			int size = this.children.size();
			int pos = 0;
			if (last != null) {
				pos = this.children.indexOf(last);
			}
			for (int i = pos; i < size; i++) {
				lastChild = this.children.get(pos);
				this.children.remove(pos);
				child.withChild(lastChild);
			}
			this.children.add(child);
		}
		return lastChild;
	}

	/**
	 * Gets the last.
	 *
	 * @return the last
	 */
	public TextDiff getLast() {
		if (this.children != null) {
			return this.children.get(this.children.size() - 1);
		}
		return null;
	}

	/**
	 * With child.
	 *
	 * @param child the child
	 * @return the text diff
	 */
	public TextDiff withChild(TextDiff child) {
		if (this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.children.add(child);
		return this;
	}

	/**
	 * With child.
	 *
	 * @param key the key
	 * @param type the type
	 * @param child the child
	 * @return the text diff
	 */
	public TextDiff withChild(String key, char type, TextDiff child) {
		if (this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.key = key;
		this.type = type;
		this.children.add(child);
		return this;
	}

	/**
	 * Creates the child.
	 *
	 * @param key the key
	 * @param left the left
	 * @param right the right
	 * @return the text diff
	 */
	public TextDiff createChild(String key, Object left, Object right) {
		TextDiff child = new TextDiff();
		if (this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.children.add(child);
		child.with(key, left, right);
		return child;
	}

	/**
	 * Gets the left.
	 *
	 * @return the left
	 */
	public Object getLeft() {
		return left;
	}

	/**
	 * Gets the right.
	 *
	 * @return the right
	 */
	public Object getRight() {
		return right;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public char getType() {
		return type;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString(0, true));
		return sb.toString();
	}

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @param splitAddAndRemove the split add and remove
	 * @return the string
	 */
	public String toString(int indentFactor, boolean splitAddAndRemove) {
		CharacterBuffer sb = new CharacterBuffer();
		if (this.children != null) {
			int newIndent = indentFactor;
			if (type != 0 && key != null) {
				sb.with(type);
				sb.withRepeat(" ", indentFactor);
				sb.with(this.key);
				sb.with(Entity.CRLF);
				newIndent += 2;
			}
			for (TextDiff diff : this.children) {
				sb.with(diff.toString(newIndent, splitAddAndRemove));
			}
		} else {
			if (key != null) {
				sb.with(type);
				sb.withRepeat(" ", indentFactor);
				sb.with(this.key + ":");
			}
			if (CHANGE == type) {
				sb.with("" + left);
				sb.with("->");
				sb.with("" + right);
			} else if (NEW == type) {
				if (splitAddAndRemove) {
					sb.with(splitValue(right, type).toString(indentFactor, false));
				} else {
					sb.with("" + right);
				}
			} else {
				if (splitAddAndRemove) {
					sb.with(splitValue(left, type).toString(indentFactor, false));
				} else {
					/* REMOVE == type || NONE == type */
					sb.with("" + left);
				}
			}
			sb.with(Entity.CRLF);
		}
		return sb.toString();
	}

	private TextDiff splitValue(Entity item, char type) {
		TextDiff diff = new TextDiff();
		if (item == null) {
			return diff;
		}
		for (int i = 0; i < item.size(); i++) {
			String key = item.getKeyByIndex(i);
			Object value = item.getValue(key);
			if (value instanceof Entity) {
				diff.withChild(key, type, splitValue((Entity) value, type));
			} else if (value instanceof List<?>) {
				diff.withChild(key, type, splitValue((List<?>) value, type));
			} else {
				if (NEW == type) {
					diff.createChild(key, null, value);
				} else {
					diff.createChild(key, value, null);
				}
			}
		}
		return diff;
	}

	private TextDiff splitValue(Object item, char type) {
		if (item instanceof Entity) {
			return splitValue((Entity) item, type);
		} else if (item instanceof List<?>) {
			return splitValue((List<?>) item, type);
		}
		TextDiff diff = new TextDiff();
		if (NEW == type) {
			diff.createChild(null, null, item);
		} else {
			diff.createChild(null, item, null);
		}
		return diff;
	}

	private TextDiff splitValue(List<?> item, char type) {
		TextDiff diff = new TextDiff();
		if (item == null) {
			return diff;
		}
		for (int i = 0; i < item.size(); i++) {
			Object value = item.get(i);
			if (value instanceof Entity) {
				diff.withChild(null, type, splitValue((Entity) value, type));
			} else if (value instanceof List<?>) {
				diff.withChild(null, type, splitValue((List<?>) value, type));
			} else {
				if (NEW == type) {
					diff.createChild(null, null, value);
				} else {
					diff.createChild(null, value, null);
				}
			}
		}
		return diff;
	}
}
