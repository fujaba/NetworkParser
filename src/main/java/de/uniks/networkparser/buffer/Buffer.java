package de.uniks.networkparser.buffer;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.list.SimpleList;

/**
 * Interface for Buffer For Tokener to parse some Values.
 *
 * @author Stefan
 */
public abstract class Buffer implements BufferItem {
  
  /** The Constant STOPCHARSJSON. */
  public static final String STOPCHARSJSON = ",:]}/\\\"[{;=# ";
  
  /** The Constant STOPCHARSXML. */
  public static final String STOPCHARSXML = ",]}/\\\"[{;=# ";
  
  /** The Constant STOPCHARSXMLEND. */
  public static final char[] STOPCHARSXMLEND = new char[] {'"', ',', ']', '}', '/', '\\', '[', '{', ';', '=', '#',
      '>', '\r', '\n', ' '};
  
  /** The Constant ENDLINE. */
  public static final char ENDLINE = '\n';

  /** The index. */
  protected int position;

  /**
   * Gets the short.
   *
   * @return the short
   */
  public short getShort() {
    byte[] bytes = array(Short.SIZE / Byte.SIZE, false);
    short result = (short) ((bytes[0] << 8) + bytes[1]);
    return result;
  }

  /**
   * Gets the int.
   *
   * @return the int
   */
  public int getInt() {
    byte[] bytes = array(Integer.SIZE / Byte.SIZE, false);
    return (int) ((bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3]);
  }

  /**
   * Gets the unsigned int.
   *
   * @return the unsigned int
   */
  public int getUnsignedInt() {
    byte[] bytes = array(Integer.SIZE / Byte.SIZE, false);
    return (int) (((bytes[0] & 0xff) << 24) + ((bytes[1] & 0xff) << 16) + ((bytes[2] & 0xff) << 8)
        + (bytes[3] & 0xff));
  }

  /**
   * Gets the char.
   *
   * @return the char
   */
  public abstract char getChar();

  /**
   * Read resource.
   *
   * @param file the file
   * @return the character buffer
   */
  public CharacterBuffer readResource(String file) {
    return new CharacterBuffer();
  }

  /**
   * Gets the long.
   *
   * @return the long
   */
  public long getLong() {
    byte[] bytes = array(Long.SIZE / Byte.SIZE, false);
    long result = bytes[0];
    result = result << 8 + bytes[1];
    result = result << 8 + bytes[2];
    result = result << 8 + bytes[3];
    result = result << 8 + bytes[4];
    result = result << 8 + bytes[5];
    result = result << 8 + bytes[6];
    result = result << 8 + bytes[7];
    return result;
  }

  /**
   * Gets the float.
   *
   * @return the float
   */
  public float getFloat() {
    int asInt = getInt();
    return Float.intBitsToFloat(asInt);
  }

  /**
   * Substring.
   *
   * @param values the values
   * @return the string
   */
  public String substring(int... values) {
    return "";
  }

  /**
   * Next.
   *
   * @param positions the positions
   * @return the string
   */
  public String next(int... positions) {
    return "";
  }

  /**
   * Gets the double.
   *
   * @return the double
   */
  public double getDouble() {
    long asLong = getLong();
    return Double.longBitsToDouble(asLong);
  }

  /**
   * Array.
   *
   * @param len the len
   * @param current the current
   * @return the byte[]
   */
  public byte[] array(int len, boolean current) {
    if (len == -1) {
      len = remaining();
    } else if (len == -2) {
      len = length();
    }
    if (len < 0) {
      return null;
    }
    byte[] result = new byte[len];
    int start = 0;
    if (current) {
      if (len > 0) {
        if (position < 0) {
          position = 0;
        }
        result[0] = (byte) getCurrentChar();
      }
      start = 1;
    }
    for (int i = start; i < len; i++) {
      result[i] = getByte();
    }
    return result;
  }

  /**
   * Gets the byte.
   *
   * @return the byte
   */
  @Override
  public byte getByte() {
    return (byte) getChar();
  }

  /**
   * Gets the boolean.
   *
   * @return the boolean
   */
  public boolean getBoolean() {
    int ch = getChar();
    return ch != 1 && ch != 0;
  }

  /**
   * Position.
   *
   * @return the int
   */
  @Override
  public int position() {
    return position;
  }

  /**
   * Remaining.
   *
   * @return the int
   */
  @Override
  public int remaining() {
    int remaining = length() - position();
    if (remaining > 0) {
      return remaining - 1;
    }
    return 0;
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  @Override
  public boolean isEmpty() {
    return length() == 0;
  }

  /**
   * Checks if is end.
   *
   * @return true, if is end
   */
  @Override
  public boolean isEnd() {
    return position() >= length();
  }

  /**
   * Gets the string.
   *
   * @param len the len
   * @return the string
   */
  public CharacterBuffer getString(int len) {
    CharacterBuffer result = new CharacterBuffer();
    if (len < 1) {
      return result;
    }
    result.withBufferLength(len);
    result.with(getCurrentChar());
    for (int i = 1; i < len; i++) {
      result.with(getChar());
      if (isEnd()) {
        break;
      }
    }
    return result;
  }

  /**
   * Read line.
   *
   * @return the character buffer
   */
  public CharacterBuffer readLine() {
    CharacterBuffer line = new CharacterBuffer();
    char character = getCurrentChar();
    while (character != '\r' && character != '\n' && character != 0) {
      line.with(character);
      character = getChar();
    }
    if (character == '\r') {
      character = getChar();
    }
    if (character == '\n') {
      skip();
    }
    return line;
  }

  /**
   * Next clean.
   *
   * @param currentValid the current valid
   * @return the char
   */
  @Override
  public char nextClean(boolean currentValid) {
    if (position < 0) {
      position = 0;
    }
    char c = getCurrentChar();
    if (currentValid && c > ' ') {
      return c;
    }
    do {
      c = getChar();
    } while (c != 0 && c <= ' ');
    return c;
  }

  /**
   * Next string.
   *
   * @param quotes the quotes
   * @return the character buffer
   */
  @Override
  public CharacterBuffer nextString(char... quotes) {
    if (quotes == null) {
      quotes = new char[] {'"'};
    }
    return nextString(new CharacterBuffer(), false, false, quotes);
  }

  /**
   * Next string.
   *
   * @return the character buffer
   */
  public CharacterBuffer nextString() {
    nextClean(true);
    boolean isQuote = getCurrentChar() == QUOTES;
    if (isQuote) {
      this.skipChar(QUOTES);
      CharacterBuffer result = nextString(QUOTES);
      this.skipChar(QUOTES);
      return result;
    }
    nextClean(true);
    CharacterBuffer result = nextString(SPACE, ENDLINE);
    return result;
  }

  /**
   * Next string.
   *
   * @param sc the sc
   * @param allowQuote the allow quote
   * @param nextStep the next step
   * @param quotes the quotes
   * @return the character buffer
   */
  @Override
  public CharacterBuffer nextString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
    if (getCurrentChar() == 0 || quotes == null) {
      return sc;
    }
    char c = getCurrentChar();
    int i = 0;
    for (i = 0; i < quotes.length; i++) {
      if (c == quotes[i]) {
        break;
      }
    }
    if (i < quotes.length) {
      if (nextStep) {
        skip();
      }
      return sc;
    }
    parseString(sc, allowQuote, nextStep, quotes);
    return sc;
  }

  protected CharacterBuffer parseString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
    sc.with(getCurrentChar());
    if (quotes == null) {
      return sc;
    }
    char c, b = 0;
    int i;
    boolean isQuote = false;

    int quoteLen = quotes.length;
    do {
      c = getChar();
      i = quoteLen;
      switch (c) {
        case 0:
          return sc;
        case '\n':
        case '\r':
        default:
          if (b == '\\') {
            if (allowQuote) {
              sc.with(c);
              if (c == '\\') {
                c = 1;
              }
              b = c;
              c = 1;
              continue;
            } else if (c == '\\') {
              c = 1;
            }
            isQuote = true;
          }
          for (i = 0; i < quoteLen; i++) {
            if (c == quotes[i]) {
              break;
            }
          }
          if (i == quoteLen) {
            sc.with(c);
          }
          b = c;
      }
      if (i < quoteLen) {
        c = 0;
      }
    } while (c != 0);
    if (nextStep) {
      skip();
    }
    if (isQuote) {
      sc.remove(sc.length() - 1);
    }
    return sc;
  }

  protected CharacterBuffer nextValue(char c, boolean allowDuppleMark) {
    CharacterBuffer sb = new CharacterBuffer();
    if (allowDuppleMark) {
      while (c >= ' ' && STOPCHARSXML.indexOf(c) < 0) {
        sb.with(c);
        c = getChar();
        if (c == 0) {
          break;
        }
      }
    } else {
      while (c >= ' ' && STOPCHARSJSON.indexOf(c) < 0) {
        sb.with(c);
        c = getChar();
        if (c == 0) {
          break;
        }
      }
    }
    return sb.trim();
  }

  /**
   * Next value.
   *
   * @param creator the creator
   * @param allowQuote the allow quote
   * @param allowDuppleMark the allow dupple mark
   * @param c the c
   * @return the object
   */
  @Override
  public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c) {
    CharacterBuffer value = nextValue(c, allowDuppleMark);
    if (value.length() < 1) {
      return null;
    }
    if (value.equals("")) {
      return value;
    }
    if (value.equalsIgnoreCase("true")) {
      return Boolean.TRUE;
    }
    if (value.equalsIgnoreCase("false")) {
      return Boolean.FALSE;
    }
    if (value.equalsIgnoreCase("null")) {
      return null;
    }
    /*
     * If it might be a number, try converting it. If a number cannot be produced, then the value will
     * just be a string. Note that the plus and implied string conventions are non-standard. A JSON
     * parser may accept non-JSON forms as long as it accepts all correct JSON forms.
     */
    Double d;
    char b = value.charAt(0);
    if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
      try {
        if (value.indexOf('.') > -1 || value.indexOf('e') > -1 || value.indexOf('E') > -1) {
          d = Double.valueOf(value.toString());
          if (!d.isInfinite() && !d.isNaN()) {
            return d;
          }
        } else {
          Long myLong = Long.valueOf(value.toString());
          if (myLong.longValue() == myLong.intValue()) {
            return Integer.valueOf(myLong.intValue());
          }
          return myLong;
        }
      } catch (Exception ignore) {
        /* Do nothing */
      }
    }
    return value;
  }

  /**
   * Skip to.
   *
   * @param search the search
   * @param notEscape the not escape
   * @return true, if successful
   */
  @Override
  public boolean skipTo(char search, boolean notEscape) {
    int len = length();
    char lastChar = 0;
    if (this.position() > 0 && this.position() < len) {
      lastChar = this.getCurrentChar();
    }
    while (this.position() < len) {
      char currentChar = getCurrentChar();
      if (currentChar == search && (!notEscape || lastChar != '\\')) {
        return true;
      }
      lastChar = currentChar;
      if (!skip()) {
        break;
      }
    }
    return false;
  }

  /**
   * Skip to.
   *
   * @param search the search
   * @param order the order
   * @param notEscape the not escape
   * @return true, if successful
   */
  @Override
  public boolean skipTo(String search, boolean order, boolean notEscape) {
    if (position() < 0) {
      return false;
    }
    char[] character = search.toCharArray();
    int z = 0;
    int strLen = character.length;
    int len = length();
    char lastChar = 0;
    if (this.position() > 0 && this.position() < len) {
      lastChar = this.getCurrentChar();
    }
    while (this.position() < len) {
      char currentChar = getCurrentChar();
      if (order) {
        if (currentChar == character[z]) {
          z++;
          if (z >= strLen) {
            return true;
          }
        } else {
          z = 0;
        }
      } else {
        for (char zeichen : character) {
          if (currentChar == zeichen && (!notEscape || lastChar != '\\')) {
            return true;
          }
        }
      }
      lastChar = currentChar;
      skip();
    }
    return false;
  }

  /**
   * Skip.
   *
   * @param pos the pos
   * @return true, if successful
   */
  @Override
  public boolean skip(int pos) {
    while (pos > 0) {
      if (getChar() == 0) {
        return false;
      }
      pos--;
    }
    return true;
  }

  /**
   * Skip.
   *
   * @return true, if successful
   */
  @Override
  public boolean skip() {
    return getChar() != 0;
  }

  /**
   * Next token.
   *
   * @param current the current
   * @param stopWords the stop words
   * @return the character buffer
   */
  @Override
  public CharacterBuffer nextToken(boolean current, char... stopWords) {
    nextClean(current);
    CharacterBuffer characterBuffer = new CharacterBuffer();
    char c = getCurrentChar();
    for (int i = 0; i < stopWords.length; i++) {
      if (stopWords[i] == c) {
        return characterBuffer;
      }
    }
    parseString(characterBuffer, true, false, stopWords);
    return characterBuffer;
  }

  /**
   * Check values.
   *
   * @param items the items
   * @return true, if successful
   */
  @Override
  public boolean checkValues(char... items) {
    char current = getCurrentChar();
    for (char item : items) {
      if (current == item) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the string list.
   *
   * @return the string list
   */
  @Override
  public SimpleList<String> getStringList() {
    SimpleList<String> list = new SimpleList<String>();
    CharacterBuffer sc = new CharacterBuffer();
    do {
      sc.clear();
      nextString(sc, true, true, '"');
      if (sc.length() > 0) {
        if (sc.indexOf('\"') >= 0) {
          list.add("\"" + sc + "\"");
        } else {
          list.add(sc.toString());
        }
      }
    } while (sc.length() > 0);
    return list;
  }

  /**
   * Skip char.
   *
   * @param quotes the quotes
   * @return the char
   */
  @Override
  public char skipChar(char... quotes) {
    char c = getCurrentChar();
    if (quotes == null) {
      return c;
    }
    boolean found;
    do {
      found = false;
      for (int i = 0; i < quotes.length; i++) {
        if (quotes[i] == c) {
          found = true;
          break;
        }
      }
      c = getChar();
      if (found) {
        break;
      }
    } while (c != 0);
    return c;
  }

  /**
   * Skip if.
   *
   * @param allowSpace the allow space
   * @param quotes the quotes
   * @return true, if successful
   */
  public boolean skipIf(boolean allowSpace, char... quotes) {
    char c = getCurrentChar();
    if (quotes == null) {
      return true;
    }
    for (int i = 0; i < quotes.length; i++) {
      if (quotes[i] != c) {
        if (allowSpace && c == ' ') {
          c = getChar();
          i--;
          continue;
        }
        return false;
      }
      c = getChar();
    }
    return true;
  }
  
  /**
   * Skip for.
   *
   * @param quotes the quotes
   * @return true, if successful
   */
  public boolean skipFor(char... quotes) {
      char c = getChar();
      if (quotes == null) {
        return true;
      }
      boolean found;
      do {
          found=false;
          for (int i = 0; i < quotes.length; i++) {
              if(quotes[i] == c) {
                  found = true;
                  break;
              }
          }
          if(found) {
              c = getChar();
          }
      } while(found);
      return true;
    }

  /**
   * Prints the error.
   *
   * @param msg the msg
   */
  public void printError(String msg) {
    if (msg != null && msg.length() > 0) {
      System.err.println(msg);
    }
  }
}
