package de.uniks.networkparser;

import java.util.List;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.list.SimpleList;

public class TextDiff {
	public static final char NEW='+';
	public static final char NONE=' ';
	public static final char CHANGE='#';
	public static final char REMOVE='-';
	public static final String TO ="->";
	
	private String key;
	private char type;
	private Object left;
	private Object right;
	private SimpleList<TextDiff> children;

	public TextDiff with(String key, Object left, Object right) {
		this.left = left;
		this.right = right;
		this.key = key;
		if(left == null) {
			if(right == null) {
				this.type = NONE;
			} else {
				this.type = NEW;
			}
		} else {
			if(right == null) {
				this.type = REMOVE;
			} else if(left.equals(right)) {
				this.type = NONE;
			} else {
				this.type = CHANGE;
			}
		}
		return this;
	}
	
	public TextDiff replaceChild(TextDiff last, String key, Object left, Object right) {
		TextDiff lastChild = null;
		if(this.children != null) {
			TextDiff child = new TextDiff();
			child.with(key, left, right);
			int size = this.children.size();
			int pos =0;
			if(last != null) {
				pos = this.children.indexOf(last);
			}
			for(int i=pos; i<size;i++) {
				lastChild = this.children.get(pos);
				this.children.remove(pos);
				child.withChild(lastChild);
			}
			this.children.add(child);
		}
		return lastChild;
	}
	

	public TextDiff getLast() {
		if(this.children!= null) {
			return this.children.get(this.children.size() - 1);	
		}
		return null;
	}
	
	public TextDiff withChild(TextDiff child) {
		if(this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.children.add(child);
		return this;
	}
	
	public TextDiff withChild(String key, char type, TextDiff child) {
		if(this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.key = key;
		this.type = type;
		this.children.add(child);
		return this;
	}
	
	public TextDiff createChild(String key, Object left, Object right) {
		TextDiff child = new TextDiff();
		if(this.children == null) {
			this.children = new SimpleList<TextDiff>();
		}
		this.children.add(child);
		child.with(key, left, right);
		return child;
	}
	
	public Object getLeft() {
		return left;
	}
	
	public Object getRight() {
		return right;
	}
	
	public char getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(toString(0, true));
		return sb.toString();
	}
	public String toString(int indentFactor, boolean splitAddAndRemove) {
		CharacterBuffer sb=new CharacterBuffer();
		if(this.children != null) {
			int newIndent = indentFactor;
			if(type != 0 && key != null) {
				sb.with(type);
				sb.withRepeat(" ", indentFactor);
				sb.with(this.key);
				sb.with(Entity.CRLF);
				newIndent += 2;
			}
			for(TextDiff diff : this.children) {
				sb.with(diff.toString(newIndent,splitAddAndRemove));
			}
		} else {
			if(key != null) {
				sb.with(type);
				sb.withRepeat(" ", indentFactor);
				sb.with(this.key+":");
			}
			if(CHANGE == type) {
				sb.with(""+left);
				sb.with("->");
				sb.with(""+right);
			} else if(NEW  == type) {
				if(splitAddAndRemove) {
					sb.with(splitValue(right, type).toString(indentFactor, false));
				} else {
					sb.with(""+right);
				}
			} else { 
				if(splitAddAndRemove) {
					sb.with(splitValue(left, type).toString(indentFactor, false));
				} else {
	//			if(REMOVE  == type || NONE == type) {
					sb.with(""+left);
				}
			}
			sb.with(Entity.CRLF);
		}
		return sb.toString();
	}

	private TextDiff splitValue(Entity item, char type) {
		TextDiff diff=new TextDiff();
		for(int i=0;i<item.size();i++) {
			String key = item.getKeyByIndex(i);
			Object value = item.getValue(key);
			if (value instanceof Entity) {
				diff.withChild(key, type, splitValue((Entity)value, type));
			} else if(value instanceof List<?>) {
				diff.withChild(key, type, splitValue((List<?>)value, type));
			} else {
				if(NEW  == type) {
					diff.createChild(key, null, value);
				} else {
					diff.createChild(key, value, null);
				}
			}
		}
		return diff;
	}
	private TextDiff splitValue(Object item, char type) {
		if(item instanceof Entity) {
			return splitValue((Entity)item, type);
		} else if(item instanceof List<?>) {
			return splitValue((List<?>)item, type);
		}
		TextDiff diff = new TextDiff();
		if(NEW  == type) {
			diff.createChild(null, null, item);
		} else {
			diff.createChild(null, item, null);
		}
		return diff;
	}
	private TextDiff splitValue(List<?> item, char type) {
		TextDiff diff=new TextDiff();
		for(int i=0;i<item.size();i++) {
			Object value = item.get(i);
			if (value instanceof Entity) {
				diff.withChild(null, type, splitValue((Entity)value, type));
			} else if(value instanceof List<?>) {
				diff.withChild(null, type, splitValue((List<?>)value, type));
			} else {
				if(NEW  == type) {
					diff.createChild(null, null, value);
				} else {
					diff.createChild(null, value, null);
				}
			}
		}
		return diff;
	}
}
