package de.uniks.networkparser.buffer;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class Tokener implements BufferItem{
	protected NetworkParserLog logger = new NetworkParserLog();

	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {}
	public void parseToEntity(AbstractList<?> entityList) {}

	/** BUFFER */
	protected Buffer buffer;

	/**
	 * Reset the Tokener
	 *
	 * @param value
	 *			The Text for parsing
	 * @return Itself
	 */
	public Tokener withBuffer(CharSequence value) {
		this.buffer = new CharacterBuffer().with(value);
		return this;
	}

	public Tokener withBuffer(Buffer value) {
		this.buffer = value;
		return this;
	}

	@Override
	public int length() {
		if(buffer != null) {
			return buffer.length();
		}
		return -1;
	}
	@Override
	public int remaining() {
		if(buffer != null) {
			return buffer.remaining();
		}
		return -1;
	}
	@Override
	public boolean isEmpty() {
		if(buffer != null) {
			return buffer.isEmpty();
		}
		return true;
	}
	@Override
	public byte[] toArray() {
		if(buffer != null) {
			return buffer.toArray();
		}
		return null;
	}
	@Override
	public BufferItem withLookAHead(CharSequence lookahead) {
		if(buffer != null) {
			buffer.withLookAHead(lookahead);
		}
		return this;
	}
	@Override
	public BufferItem withLookAHead(char lookahead) {
		if(buffer != null) {
			buffer.withLookAHead(lookahead);
		}
		return this;
	}
	@Override
	public CharacterBuffer nextString() {
		if(buffer != null) {
			return buffer.nextString();
		}
		return null;
	}
	@Override
	public boolean skipTo(char search, boolean notEscape) {
		if(buffer != null) {
			return buffer.skipTo(search, notEscape);
		}
		return false;
	}

	@Override
	public boolean skipTo(String search, boolean order, boolean notEscape) {
		if(buffer != null) {
			return buffer.skipTo(search, order, notEscape);
		}
		return false;
	}
	@Override
	public boolean skip(int pos) {
		if(buffer != null) {
			return buffer.skip(pos);
		}
		return false;
	}
	@Override
	public boolean skip() {
		if(buffer != null) {
			return buffer.skip();
		}
		return false;
	}
	@Override
	public char getChar() {
		if(buffer != null) {
			return buffer.getChar();
		}
		return 0;
	}
	@Override
	public char getCurrentChar() {
		if(buffer != null) {
			return buffer.getCurrentChar();
		}
		return 0;
	}
	@Override
	public int position() {
		if(buffer != null) {
			return buffer.position();
		}
		return -1;
	}
	@Override
	public boolean isEnd() {
		if(buffer != null) {
			return buffer.isEnd();
		}
		return true;
	}
	@Override
	public CharacterBuffer getString(int len) {
		if(buffer != null) {
			return buffer.getString(len);
		}
		return null;
	}
	@Override
	public char nextClean(boolean currentValid) {
		if(buffer != null) {
			return buffer.nextClean(currentValid);
		}
		return 0;
	}
	@Override
	public CharacterBuffer nextString(CharacterBuffer sc, boolean allowCRLF, boolean nextStep, char... quotes) {
		if(buffer != null) {
			return buffer.nextString(sc, allowCRLF, nextStep, quotes);
		}
		return null;
	}

	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c) {
		if(buffer != null) {
			return buffer.nextValue(creator, allowQuote, allowDuppleMark, c);
		}
		return null;
	}
	@Override
	public CharacterBuffer nextToken(String stopWords) {
		if(buffer != null) {
			return buffer.nextToken(stopWords);
		}
		return null;
	}
	@Override
	public boolean checkValues(char... items) {
		if(buffer != null) {
			return buffer.checkValues(items);
		}
		return false;
	}
	@Override
	public SimpleList<String> getStringList() {
		if(buffer != null) {
			return buffer.getStringList();
		}
		return null;
	}
	@Override
	public SimpleList<String> splitStrings(String value, boolean split) {
		if(buffer != null) {
			return buffer.splitStrings(value, split);
		}
		return null;
	}
	@Override
	public char skipChar(char... quotes) {
		if(buffer != null) {
			return buffer.skipChar(quotes);
		}
		return 0;
	}
}
