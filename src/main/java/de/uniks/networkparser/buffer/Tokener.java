package de.uniks.networkparser.buffer;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class Tokener implements BufferItem {
	public static final String PROPS = "prop";
	protected IdMap map;

	/** BUFFER */
	protected Buffer buffer;
	
	// Methods for Map
	public SendableEntityCreator getCreatorClass(Object reference) {
		if(map == null) {
			return null;
		}
		return map.getCreatorClass(reference);
	}
	
	public SendableEntityCreator getCreator(String className, boolean fullName) {
		if(map == null) {
			return null;
		}
		return map.getCreator(className, fullName);
	}

	public String getKey(Object reference) {
		if(map == null) {
			return null;
		}
		return map.getKey(reference);
	}

	public String getId(Object reference) {
		if(map == null) {
			return null;
		}
		return map.getId(reference, true);
	}

	public Object getObject(String key) {
		if(map == null) {
			return null;
		}
		return map.getObject(key);
	}

	public boolean notify(PropertyChangeEvent evt) {
		if(map == null) {
			return false;
		}
		return this.map.notify(evt);
	}
	public Tokener withMap(IdMap map) {
		this.map = map;
		return this;
	}

	public IdMap getMap() {
		return map;
	}

	public boolean isError(Object owner, String method, String type, Object entity) {
		if(map == null) {
			return true;
		}
		//, String className
		return map.isError(owner, method, type, entity, null);
	}

	public void parseToEntity(Entity entity) {}

	public void parseToEntity(EntityList entity) {}

	public BaseItem encode(Object entity, MapEntity map) {return null;}

	/**
	 * Reset the Tokener
	 *
	 * @param value		The Text for parsing
	 * @return 			Itself
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
	public CharacterBuffer nextString(char... quotes) {
		if(buffer != null) {
			return buffer.nextString(quotes);
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
	public byte getByte() {
		if(buffer != null) {
			return buffer.getByte();
		}
		return 0;
	}

	@Override
	public byte[] array(int len, boolean current) {
		if(buffer != null) {
			return buffer.array(len, current);
		}
		return null;
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
	public CharacterBuffer nextToken(boolean current, char... stopWords) {
		if(buffer != null) {
			return buffer.nextToken(current, stopWords);
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

	public Entity newInstance() {
		return null;
	}

	public EntityList newInstanceList() {
		return null;
	}

	public Object transformValue(Object value, BaseItem reference) {
		return value;
	}

	public Entity createLink(Entity parent, String property, String className, String id) {
		return null;
	}

	public boolean isChild(Object writeValue) {
		return true;
	}
}
