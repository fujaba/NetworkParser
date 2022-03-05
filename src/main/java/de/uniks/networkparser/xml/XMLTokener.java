package de.uniks.networkparser.xml;

/*
 * NetworkParser The MIT License Copyright (c) 2010-2016 Stefan Lindel
 * https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.util.ArrayList;

import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class XMLTokener.
 *
 * @author Stefan
 */
public class XMLTokener extends Tokener {
  /** The Constant ENDTAG. */
  public static final char ENDTAG = '/';

  private static final char[] TOKEN = new char[] {' ', XMLEntity.START, ENDTAG, XMLEntity.END};

  /** The Constant CHILDREN. */
  public static final String CHILDREN = "<CHILDREN>";

  private SendableEntityCreator defaultFactory;
  
  /** The Constant STOPCHARSXMLEND. */
  public static final char[] STOPCHARSXMLEND = new char[] {'"', ',', ']', '}', '/', '\\', '[', '{', ';', '=', '#', '>', '\r', '\n', ' '};
      

  /** The stopwords. */
  private ArrayList<String> stopwords = new ArrayList<String>();

  /** The Constant SIMPLECONVERTER. */
  public static final EntityStringConverter SIMPLECONVERTER = new EntityStringConverter();

  private boolean isAllowQuote;

  /** Instantiates a new XML id map. */
  public XMLTokener() {
    this.stopwords.add("?xml");
    this.stopwords.add("!--");
    this.stopwords.add("!DOCTYPE");
  }

  /**
   * Get the next value. The value can be a Boolean, Double, Integer, BaseEntity, Long, or String.
   *
   * @param buffer the buffer
   * @param creator The new Creator
   * @param allowQuote is in Text allow Quote
   * @param allowDuppleMarks the allow dupple marks
   * @param c The Terminate Char
   * @return An object.
   */
  public Object nextValue(Buffer buffer, char c) {
    switch (c) {
      case BufferItem.QUOTES:
      case '\'':
        buffer.skip();
        CharacterBuffer v;
        if(isAllowQuote) {
            v = nextString(buffer, new CharacterBuffer(), isAllowQuote, true, '"');
        }else {
            v = nextString(buffer, c);
        }
        String g = StringUtil.unQuote(v);
        return g;
      case XMLEntity.START:
        BaseItem element = new XMLEntity();
        if (element instanceof Entity) {
          parseToEntity(element, buffer);
        }
        return element;
      default:
        break;
    }
    return super.nextValue(buffer, STOPCHARSXMLEND);
  }

  /**
   * Parses the to entity.
   *
   * @param entity the entity
   * @param source the source
   * @return the base item
   */
  @Override
  public BaseItem parseToEntity(BaseItem entity, Object source) {
    if (!(source instanceof Buffer)) {
      return null;
    }
    Buffer buffer = (Buffer) source;
    skipHeader(buffer);
    char c = buffer.getCurrentChar();
    if (c != XMLEntity.START) {
      c = buffer.nextClean(false);
    }
    if (!(entity instanceof XMLEntity)) {
      if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
        throw new SimpleException("Parse only XMLEntity");
      }
      return null;
    }
     XMLEntity xmlEntity = (XMLEntity) entity;
    if (c != XMLEntity.START) {
      if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
        throw new SimpleException("A XML text must begin with '<'");
      }
      return null;
    }
    xmlEntity.withType(buffer.nextToken(false, STOPCHARSXMLEND).toString());
    XMLEntity child;
    while (true) {
      c = buffer.nextClean(true);
      if (c == 0) {
        break;
      } else if (c == XMLEntity.END) {
        c = buffer.nextClean(false);
        if (c == 0) {
          return entity;
        }
        if (c != XMLEntity.START) {
          CharacterBuffer item = new CharacterBuffer();
          buffer.nextString(item, false, false, '<');
          char currentChar = buffer.getCurrentChar();
          char nextChar = buffer.nextClean(false);
          if (nextChar != '/') {
            /* May be another child so it is possible text node text */
            XMLEntity newChild = new XMLEntity();
            newChild.withValue(item.toString());
            xmlEntity.withChild(newChild);
          } else {
            xmlEntity.withValue(item.toString());
          }
          buffer.withLookAHead("" + currentChar + nextChar);
          c = currentChar;
        }
      }

      if (c == XMLEntity.START) {
        char nextChar = buffer.getChar();
        if (nextChar == '/') {
          buffer.skipTo(XMLEntity.END, false);
          break;
        } else if (nextChar == '!') {
          nextChar = buffer.getChar();
          if ('[' == nextChar) {
            /* MIGHT BE <![CDATA[ */
            int start = buffer.position();
            buffer.skipTo("]]>", true, true);
            int end = buffer.position();
            if (end != start) {
              start += 7; /* CDATA[ */
              end -= 2;
              xmlEntity.withValueItem(buffer.substring(start, end).toString());
            }
          } else {
            buffer.skipTo("-->", true, true);
          }
          buffer.skip();
        } else {
          buffer.withLookAHead(c);
          if (buffer.getCurrentChar() == '<') {
            child = (XMLEntity) xmlEntity.getNewList(true);
            if (parseToEntity(child, buffer) != null) {
              xmlEntity.with(child);
              buffer.skip();
            }
          } else {
            xmlEntity.withValue(nextString(buffer, new CharacterBuffer(), false, false, '<').toString());
          }
        }
      } else if (c == '/') {
        buffer.skip();
        break;
      } else {
        if (xmlEntity.sizeChildren() < 1) {
          /* Normal key Value */
          CharSequence value = (CharSequence) nextValue(buffer, STOPCHARSXMLEND);
          if (value == null) {
            return null;
          }
          if (value.length() > 0) {
            xmlEntity.put(value.toString(), nextValue(buffer, buffer.nextClean(false)));
          }
        } else {
          /* Just a Child */
          CharacterBuffer item = new CharacterBuffer();
          nextString(buffer, item, false, false, '<');
          /* May be another child so it is possible text node text */
          XMLEntity newChild = new XMLEntity();
          newChild.withValue(item.toString());
          xmlEntity.withChild(newChild);
        }
      }
    }
    return entity;
  }

  /**
   * Skip the Current Entity to &gt;.
   * 
   * @param buffer Buffer for Values
   */
  protected void skipEntity(Buffer buffer) {
    if (buffer == null) {
      return;
    }
    buffer.skipTo('>', false);
    /* Skip > */
    buffer.nextClean(false);
  }

  /**
   * Skip header.
   *
   * @param buffer the buffer
   * @return the string
   */
  public String skipHeader(Buffer buffer) {
    boolean skip = false;
    CharacterBuffer tag;
    if (buffer == null) {
      return null;
    }
    do {
      tag = buffer.getString(2);
      if (tag.equals("<?")) {
        skipEntity(buffer);
        skip = true;
      } else if (tag.equals("<!")) {
        buffer.skipTo(">", true, true);
        buffer.nextClean(false);
        skip = true;
      } else {
        skip = false;
      }
    } while (skip);
    String item = tag.toString();
    buffer.withLookAHead(item);
    return item;
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "XMLTokener";
  }

  /**
   * New instance.
   *
   * @return the entity
   */
  @Override
  public Entity newInstance() {
    return new XMLEntity();
  }

  /**
   * New instance list.
   *
   * @return the entity list
   */
  @Override
  public EntityList newInstanceList() {
    return new XMLEntity();
  }

  /**
   * Find tag.
   *
   * @param tokener the tokener
   * @param buffer Buffer for Values
   * @param map decoding runtime values
   * @return the object
   */
  public Object parse(XMLTokener tokener, Buffer buffer, MapEntity map) {
    parseAttribute(tokener, buffer, map);
    return parseChildren(tokener, buffer, map);
  }

  protected void parseAttribute(XMLTokener tokener, Buffer buffer, MapEntity stack) {
    if (stack == null) {
      return;
    }
    Object entity = stack.getCurrentItem();
    SendableEntityCreator creator = stack.getCurrentCreator();
    if (entity != null) {
      /* Parsing attributes */
      CharacterBuffer token = new CharacterBuffer();
      char myChar;
      do {
        if (buffer.getCurrentChar() == SimpleMap.SPACE) {
          buffer.getChar();
        }
        tokener.nextString(buffer, token, true, false, SimpleMap.SPACE, SimpleMap.EQUALS, XMLEntity.END, ENDTAG);
        myChar = buffer.getCurrentChar();
        if (myChar == ENTER) {
          String key = token.toString();
          token.clear();
          buffer.skip(2);
          tokener.nextString(buffer, token, true, false, SimpleMap.DOUBLEQUOTIONMARK);
          String value = token.toString();
          token.clear();
          buffer.skip();
          creator.setValue(entity, key, value, SendableEntityCreator.NEW);
          stack.setValue(key, value);
          myChar = buffer.getCurrentChar();
        }
      } while (myChar != XMLEntity.END && myChar != 0 && myChar != ENDTAG);
    }
  }

  protected Object parseChildren(XMLTokener tokener, Buffer buffer, MapEntity stack) {
    if (stack == null) {
      return null;
    }
    Object entity = stack.getCurrentItem();
    SendableEntityCreator creator = stack.getCurrentCreator();
    if (creator == null) {
      return null;
    }
    /* Parsing next Element */
    if (buffer.skipTo("/>", false, false)) {
      if (buffer.getCurrentChar() == '/') {
        stack.popStack();
        buffer.getChar();
        tokener.nextToken(buffer, false, TOKEN);
        return entity;
      }

      char quote = XMLEntity.START;
      /* Skip > */
      buffer.skip();
      CharacterBuffer valueItem = new CharacterBuffer();
      tokener.nextString(buffer, valueItem, false, false, quote);
      if (!valueItem.isEmptyCharacter()) {
        CharacterBuffer test = new CharacterBuffer();
        while (!buffer.isEnd()) {
          if (buffer.getCurrentChar() == XMLEntity.START) {
            test.with(buffer.getCurrentChar());
            test.with(buffer.getChar());
          }
          if (buffer.getCurrentChar() == ENDTAG) {
            CharacterBuffer endTag = tokener.nextToken(buffer, false, XMLTokener.TOKEN);
            String currentTag = stack.getCurrentTag();
            if (currentTag == null || currentTag.equals(endTag.toString())) {
              break;
            } else {
              valueItem.with(test);
              valueItem.with(endTag);
              valueItem.with(buffer.getCurrentChar());
            }
          } else if (test.length() > 0) {
            valueItem.with(test);
          } else {
            char currentChar = buffer.getChar();
            if (currentChar != XMLEntity.START) {
              valueItem.with(currentChar);
            }
          }
          test.clear();
        }
        if (entity != null) {
          creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), SendableEntityCreator.NEW);
        }
        stack.setValue("" + SimpleMap.ENTITYSPLITTER, valueItem.toString());
        stack.popStack();
        tokener.skipEntity(buffer);
        return entity;
      }
      if (buffer.getCurrentChar() == XMLEntity.START) {
        /* show next Tag */
        Object child;
        do {
          valueItem = parseEntity(tokener, buffer, stack);
          if (valueItem == null) {
            if (buffer.getCurrentChar() == ENDTAG) {
              /* Show if Item is End */
              valueItem = tokener.nextToken(buffer, false, XMLEntity.END);
              if (valueItem.equals(stack.getCurrentTag())) {
                stack.popStack();
                /* Skip > EndTag */
                buffer.skip();
              }
            }
            return entity;
          }
          if (!valueItem.isEmpty()) {
            creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(),
                SendableEntityCreator.NEW);
            stack.setValue("" + SimpleMap.ENTITYSPLITTER, valueItem.toString());
            stack.popStack();
            tokener.skipEntity(buffer);
            return entity;
          }

          String childTag = stack.getCurrentTag();
          child = parse(tokener, buffer, stack);
          if (childTag != null && child != null) {
            creator.setValue(entity, childTag, child, CHILDREN);
          }
        } while (child != null);
      }
    }
    return entity;
  }

  /**
   * Gets the entity.
   *
   * @param tokener the tokener
   * @param buffer Buffer for Values
   * @param stack the decoding runtime values
   * @return the entity
   */
  public CharacterBuffer parseEntity(XMLTokener tokener, Buffer buffer, MapEntity stack) {
    CharacterBuffer valueItem = new CharacterBuffer();
    if (tokener == null || buffer == null) {
      return valueItem;
    }
    CharacterBuffer tag;
    boolean isEmpty = true;
    do {
      if (buffer.getCurrentChar() != XMLEntity.START) {
        tokener.nextString(buffer, valueItem, false, false, XMLEntity.START);
        if (!valueItem.isEmpty()) {
          valueItem.trim();
          isEmpty = valueItem.isEmpty();
        }
      }
      tag = tokener.nextToken(buffer, false, TOKEN);
      if (tag != null) {
        for (String stopword : this.stopwords) {
          if (tag.startsWith(stopword, 0, false)) {
            buffer.skipTo(XMLEntity.END, false);
            buffer.skipTo(XMLEntity.START, false);
            tag = null;
            break;
          }
        }
      }
      if (buffer.isEnd()) {
        break;
      }
    } while (tag == null);
    if (tag == null || tag.length() < 1) {
      return null;
    }
    if (tag.isEmpty() && isEmpty) {
      valueItem.clear();
    }
    SimpleMap idMap = getMap();
    SendableEntityCreator item = null;
    if (idMap != null) {
      item = idMap.getCreator(tag.toString(), false, true, null);
    }
    if (item != null && item instanceof SendableEntityCreatorTag) {
      addToStack((SendableEntityCreatorTag) item, tokener, tag, valueItem, stack);
      return valueItem;
    }
    String startTag;
    if (tag.lastIndexOf(SimpleMap.ENTITYSPLITTER) >= 0) {
      startTag = tag.substring(0, tag.lastIndexOf(SimpleMap.ENTITYSPLITTER));
    } else {
      startTag = tag.toString();
    }
    SimpleKeyValueList<String, SendableEntityCreatorTag> filter = new SimpleKeyValueList<String, SendableEntityCreatorTag>();
    if (idMap != null) {
      for (int i = 0; i < idMap.getCreators().size(); i++) {
        String key = idMap.getCreators().getKeyByIndex(i);
        SendableEntityCreator value = idMap.getCreators().getValueByIndex(i);
        if (key.startsWith(startTag) && value instanceof SendableEntityCreatorTag) {
          filter.put(key, (SendableEntityCreatorTag) value);
        }
      }
    }
    SendableEntityCreator defaultCreator = getDefaultFactory();
    SendableEntityCreatorTag creator;
    if (defaultCreator instanceof SendableEntityCreatorTag) {
      creator = (SendableEntityCreatorTag) defaultCreator;
    } else {
      creator = EntityCreator.createXML();
    }
    if (filter.size() < 1) {
      addToStack(creator, tokener, tag, valueItem, stack);
      return valueItem;
    }
    StringBuilder sTag = new StringBuilder(startTag);
    while (filter.size() > 0) {
      addToStack(creator, tokener, tag, valueItem, stack);
      parseAttribute(tokener, buffer, stack);
      if (buffer.getCurrentChar() == '/') {
        stack.popStack();
      } else {
        buffer.skip();
        if (buffer.getCurrentChar() != XMLEntity.START) {
          tokener.nextString(buffer, valueItem, false, false, XMLEntity.START);
          if (!valueItem.isEmpty()) {
            valueItem.trim();
            Object entity = stack.getCurrentItem();
            creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(),
                SendableEntityCreator.NEW);
          }
        }
        tag = tokener.nextToken(buffer, false, TOKEN);
        item = idMap.getCreator(tag.toString(), false, true, null);
        if (item instanceof SendableEntityCreatorTag) {
          creator = (SendableEntityCreatorTag) item;
        } else {
          creator = (SendableEntityCreatorTag) defaultCreator;
        }
        sTag.append(SimpleMap.ENTITYSPLITTER).append(tag.toString());
        for (int i = filter.size() - 1; i >= 0; i--) {
          String key = filter.getKeyByIndex(i);
          if (key.equals(sTag.toString())) {
            /* FOUND THE Item */
            creator = filter.getValueByIndex(i);
            addToStack(creator, tokener, tag, valueItem, stack);
            return valueItem;
          }
          if (!key.startsWith(sTag.toString())) {
            filter.removePos(i);
          }
        }
        addToStack(creator, tokener, tag, valueItem, stack);
      }
    }
    return valueItem;
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
    if (parent != null) {
      parent.put(property, id);
    }
    return null;
  }

  protected Object addToStack(SendableEntityCreatorTag creator, XMLTokener tokener, CharacterBuffer tag,
      CharacterBuffer value, MapEntity map) {
    if (creator == null) {
      return null;
    }
    Object entity = creator.getSendableInstance(false);
    if (entity instanceof EntityList) {
      creator.setValue(entity, XMLEntity.PROPERTY_VALUE, value.toString(), SendableEntityCreator.NEW);
      creator.setValue(entity, XMLEntity.PROPERTY_TAG, tag.toString(), SendableEntityCreator.NEW);
    }
    map.withStack(tag.toString(), entity, creator);
    return entity;
  }

  /**
   * Transform value.
   *
   * @param value the value
   * @param reference the reference
   * @return the object
   */
  @Override
  public Object transformValue(Object value, BaseItem reference) {
    return StringUtil.valueToString(value, true, reference, SIMPLECONVERTER);
  }

  /**
   * Get the DefaultFactory for Creating Element for Serialization.
   *
   * @return the get The DefaultFactory
   */
  public SendableEntityCreator getDefaultFactory() {
    return defaultFactory;
  }

  /**
   * Add a DefaultFactoriy for creating Elements for Serialization.
   *
   * @param defaultFactory the defaultFactory to set
   * @return ThisComponent
   */
  public XMLTokener withDefaultFactory(SendableEntityCreator defaultFactory) {
    this.defaultFactory = defaultFactory;
    return this;
  }

  /**
   * Checks if is child.
   *
   * @param writeValue the write value
   * @return true, if is child
   */
  public boolean isChild(Object writeValue) {
    return writeValue instanceof BaseItem;
  }

  /**
   * With allow quote.
   *
   * @param value the value
   * @return the XML tokener
   */
  public XMLTokener withAllowQuote(boolean value) {
    this.isAllowQuote = value;
    return this;
  }

  /**
   * With map.
   *
   * @param map the map
   * @return the XML tokener
   */
  @Override
  public XMLTokener withMap(SimpleMap map) {
    super.withMap(map);
    return this;
  }

  /* FIXME public static XsdValidationLoggingErrorHandler */
  /*
   * validate(java.net.URL xsdSchema, String xmlDokument) throws SAXException, IOException {
   * com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory schemaFactory =
   * (XMLSchemaFactory) SchemaFactory .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
   * 
   * Schema schema = schemaFactory.newSchema(new File(xsdSchema)); Schema schema =
   * schemaFactory.newSchema(xsdSchema); Validator validator = schema.newValidator();
   * XsdValidationLoggingErrorHandler errorHandler = new XsdValidationLoggingErrorHandler();
   * validator.setErrorHandler(errorHandler); validator.validate(new StreamSource(new
   * File(xmlDokument))); return errorHandler; }
   */
}
