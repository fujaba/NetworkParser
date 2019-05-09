package de.uniks.networkparser;

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

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class Tokener {
	public static final String PROPS = "prop";
	public static final char ENTER = '=';
	public static final char COLON = ':';

	protected IdMap map;

	// Methods for Map
	public SendableEntityCreator getCreatorClass(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getCreatorClass(reference);
	}

	public SendableEntityCreator getCreator(String className, boolean fullName,
			SimpleList<SendableEntityCreator> creators) {
		if (map == null) {
			return null;
		}
		return map.getCreator(className, fullName, null);
	}

	public String getKey(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getKey(reference);
	}

	public String getId(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getId(reference, true);
	}

	public Object getObject(String key) {
		if (map == null) {
			return null;
		}
		return map.getObject(key);
	}

	public boolean notify(PropertyChangeEvent evt) {
		if (map == null) {
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
		if (map == null) {
			return true;
		}
		// , String className
		return map.isError(owner, method, type, entity, null);
	}

	public BaseItem parseToEntity(BaseItem entity, Object buffer) {
		return entity;
	}

	public BaseItem encode(Object entity, MapEntity map) {
		IdMap idMap = this.map;
		if (this.map == null) {
			if (map == null) {
				return null;
			}
			idMap = map.getMap();
			if (idMap == null) {
				return null;
			}
		}
		return idMap.encode(entity, map);
	}

	public Object nextValue(Buffer buffer, BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c) {
		if (buffer != null) {
			return buffer.nextValue(creator, allowQuote, allowDuppleMark, c);
		}
		return null;
	}

	public CharacterBuffer nextString(Buffer buffer, char... quotes) {
		if (buffer != null) {
			return buffer.nextString(quotes);
		}
		return null;
	}

	public CharacterBuffer nextString(Buffer buffer) {
		if (buffer != null) {
			return buffer.nextString();
		}
		return null;
	}

	public CharacterBuffer nextString(Buffer buffer, CharacterBuffer sc, boolean allowCRLF, boolean nextStep,
			char... quotes) {
		if (buffer != null) {
			return buffer.nextString(sc, allowCRLF, nextStep, quotes);
		}
		return null;
	}

	public CharacterBuffer nextToken(Buffer buffer, boolean current, char... stopWords) {
		if (buffer != null) {
			return buffer.nextToken(current, stopWords);
		}
		return null;
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
