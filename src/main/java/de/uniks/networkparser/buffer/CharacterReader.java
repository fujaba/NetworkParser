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

/**
 * Reader for Characters.
 * @author Stefan Lindel
 */
public class CharacterReader extends CharacterBuffer {
  /** The line. */
  protected int line;

  /** The character. */
  protected int character;

  /** Is Last String is \"String\" or Text */
  private boolean isString = true;

  /**
   * Checks if is string.
   *
   * @return true, if is string
   */
  public boolean isString() {
    return isString;
  }

  /**
   * With string.
   *
   * @param isString the is string
   * @return the character reader
   */
  public CharacterReader withString(boolean isString) {
    this.isString = isString;
    return this;
  }

  /**
   * Back.
   *
   * @return true, if successful
   */
  public boolean back() {
    if (super.back()) {
      this.character -= 1;
      return true;
    }
    return false;
  }

  /**
   * Gets the char.
   *
   * @return the char
   */
  @Override
  public char getChar() {
    char c = super.getChar();
    if (c == '\n') {
      this.line += 1;
      this.character = 0;
    } else {
      this.character += 1;
    }
    return c;
  }

  /**
   * Next string.
   *
   * @param sc the sc
   * @param allowQuote the allow quote
   * @param quotes the quotes
   * @return the character buffer
   */
  @Override
  public CharacterBuffer nextString(boolean allowQuote, char... quotes) {
    if (quotes == null) {
      return new CharacterBuffer();
    }

    boolean found = false;
    for (char quote : quotes) {
      if ('"' == quote) {
        found = true;
        if (getCurrentChar() == quote) {
          isString = true;
        } else {
          isString = !isString;
        }
      }
    }
    if (!found && getCurrentChar() == '"') {
      isString = true;
      CharacterBuffer sc = new CharacterBuffer();
      while (!isEnd()) {
        int len = sc.length();
        sc  = super.nextString(allowQuote, quotes);
        if (sc.length() > len && !sc.endsWith("\"", false)) {
          sc.with(',');
        } else {
          break;
        }
      }
      return sc;
    }
    return super.nextString(allowQuote, quotes);
  }

  /**
   * With.
   *
   * @param items the items
   * @return the character reader
   */
  public CharacterReader with(CharSequence... items) {
    super.with(items);
    return this;
  }
}
