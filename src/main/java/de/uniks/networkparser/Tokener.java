package de.uniks.networkparser;

/*
NetworkParser
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
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class Tokener.
 *
 * @author Stefan
 */
public class Tokener {
	
	/** The Constant PROPS. */
	public static final String PROPS = "prop";
	
	/** The Constant ENTER. */
	public static final char ENTER = '=';
	
	/** The Constant COLON. */
	public static final char COLON = ':';
	
	/** The SPLITTER. */
	public static final char SPLITTER = ' ';

	protected SimpleMap map;

	/**
	 * Gets the creator class.
	 *
	 * @param reference the reference
	 * @return the creator class
	 */
	public SendableEntityCreator getCreatorClass(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getCreatorClass(reference);
	}

	/**
	 * Gets the creator.
	 *
	 * @param className the class name
	 * @param fullName the full name
	 * @param creators the creators
	 * @return the creator
	 */
	public SendableEntityCreator getCreator(String className, boolean fullName,
			SimpleList<SendableEntityCreator> creators) {
		if (map == null) {
			return null;
		}
		return map.getCreator(className, fullName, true, null);
	}

	/**
	 * Gets the key.
	 *
	 * @param reference the reference
	 * @return the key
	 */
	public String getKey(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getKey(reference);
	}

	/**
	 * Gets the id.
	 *
	 * @param reference the reference
	 * @return the id
	 */
	public String getId(Object reference) {
		if (map == null) {
			return null;
		}
		return map.getId(reference, true);
	}

	/**
	 * Gets the object.
	 *
	 * @param key the key
	 * @return the object
	 */
	public Object getObject(String key) {
		if (map == null) {
			return null;
		}
		return map.getObject(key);
	}

	/**
	 * Notify.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	public boolean notify(PropertyChangeEvent evt) {
		if (map == null) {
			return false;
		}
		return this.map.notify(evt);
	}

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the tokener
	 */
	public Tokener withMap(SimpleMap map) {
		this.map = map;
		return this;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public SimpleMap getMap() {
		return map;
	}

	/**
	 * Checks if is error.
	 *
	 * @param owner the owner
	 * @param method the method
	 * @param type the type
	 * @param entity the entity
	 * @return true, if is error
	 */
	public boolean isError(Object owner, String method, String type, Object entity) {
		if (map == null) {
			return true;
		}
		return map.isError(owner, method, type, entity, null);
	}

	/**
	 * Parses the to entity.
	 *
	 * @param entity the entity
	 * @param buffer the buffer
	 * @return the base item
	 */
	public BaseItem parseToEntity(BaseItem entity, Object buffer) {
		return entity;
	}
	

    protected BaseItem parsingEntity(Entity parent, SimpleKeyValueList<?, ?> newValue) {
        if(newValue instanceof XMLEntity) {
            parent.put(SimpleMap.CLASS, ((XMLEntity)newValue).getTag());
            String value = ((XMLEntity)newValue).getValue();
            if (value != null && value.length() > 0) {
                parent.put(SimpleMap.VALUE, value);
            }
        }
    
        JsonObject props = new JsonObject();
        for (int i = 0; i < newValue.size(); i++) {
            parseEntityProp(props, newValue.getValueByIndex(i), (String)newValue.getKeyByIndex(i));
        }
        if(newValue instanceof XMLEntity) {
            XMLEntity entity = (XMLEntity) newValue;
            for (int i = 0; i < entity.sizeChildren(); i++) {
                BaseItem child = entity.getChild(i);
                if (!(child instanceof XMLEntity)) {
                    continue;
                }
                parseEntityProp(props, child, ((XMLEntity) child).getTag());
            }
        }
        parent.put(PROPS, props);
        return parent;
    }
    
  private void parseEntityProp(JsonObject props, Object propValue, String prop) {
      if (propValue != null) {
          if (propValue instanceof XMLEntity) {
              if (props.containsKey(prop)) {
                  Object child = props.get(prop);
                  JsonArray propList = null;
                  if (child instanceof JsonObject) {
                      propList = new JsonArray();
                      propList.add(child);
                  } else if (child instanceof JsonArray) {
                      propList = (JsonArray) child;
                  }
                  if (propList != null) {
                      propList.add(parsingEntity(newInstance(), (SimpleKeyValueList<?, ?>)propValue));
                      props.put(prop, propList);
                  }
              } else {
                  props.put(prop, parsingEntity(newInstance(), (SimpleKeyValueList<?, ?>) propValue));
              }
          } else {
              props.put(prop, propValue);
          }
      }
  }

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @param map the map
	 * @return the base item
	 */
	public BaseItem encode(Object entity, MapEntity map) {
		SimpleMap idMap = this.map;
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

	/**
	 * Next string.
	 *
	 * @param buffer the buffer
	 * @param quotes the quotes
	 * @return the character buffer
	 */
	public CharacterBuffer nextString(Buffer buffer, char... quotes) {
		if (buffer != null) {
			return buffer.nextString(quotes);
		}
		return null;
	}

	/**
	 * Next string.
	 *
	 * @param buffer the buffer
	 * @return the character buffer
	 */
	public CharacterBuffer nextString(Buffer buffer) {
		if (buffer != null) {
			return buffer.nextString();
		}
		return null;
	}

	/**
	 * Next string.
	 *
	 * @param buffer the buffer
	 * @param sc the sc
	 * @param allowCRLF the allow CRLF
	 * @param quotes the quotes
	 * @return the character buffer
	 */
	public CharacterBuffer nextString(Buffer buffer, CharacterBuffer sc, boolean allowCRLF, char... quotes) {
		if (buffer != null) {
			return buffer.nextString(sc, allowCRLF, quotes);
		}
		return null;
	}

	/**
	 * Next token.
	 *
	 * @param buffer the buffer
	 * @param current the current
	 * @param stopWords the stop words
	 * @return the character buffer
	 */
	public CharacterBuffer nextToken(Buffer buffer, char... stopWords) {
		if (buffer != null) {
			return buffer.nextToken(stopWords);
		}
		return null;
	}


    /**
     * Next value.
     *
     * @param buffer          the buffer
     * @param stopChars       the Chars for stopping
     * @return the object
     */
    public Object nextValue(Buffer buffer, char... stopChars) {
        if (buffer != null) {
            return buffer.validateReturn(buffer.nextValue(stopChars));
        }
        return null;
    }
	
	/**
	 * New instance.
	 *
	 * @return the entity
	 */
	public Entity newInstance() {
		return null;
	}

	/**
	 * New instance list.
	 *
	 * @return the entity list
	 */
	public EntityList newInstanceList() {
		return null;
	}

	/**
	 * Transform value.
	 *
	 * @param value the value
	 * @param reference the reference
	 * @return the object
	 */
	public Object transformValue(Object value, BaseItem reference) {
		return value;
	}

	/**
	 * Creates the link.
	 *
	 * @param parent the parent
	 * @param property the property
	 * @param className the class name
	 * @param id the id
	 * @return the entity
	 */
	public Entity createLink(Entity parent, String property, String className, String id) {
		return null;
	}

	/**
	 * Checks if is child.
	 *
	 * @param writeValue the write value
	 * @return true, if is child
	 */
	public boolean isChild(Object writeValue) {
		return true;
	}
}
