package de.uniks.networkparser.interfaces;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleList;

public interface BufferItem {
	public static final char SPACE = ' ';
	public static final char QUOTES = '"';

	/** @return the length of the buffer */
	public int length();

	/** @return The next Char */
	public abstract char getChar();

	public abstract byte getByte();

	/** @return The currentChar */
	public abstract char getCurrentChar();

	/**
	 * Return a new Array of Elements
	 *
	 * @param len     len of next values -1 remaining length -2 all size (Only for
	 *                BufferedBuffer)
	 * @param current Add Current Byte to Array
	 *
	 * @return The Buffer as byte Array
	 */
	public byte[] array(int len, boolean current);

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int position();

	/**
	 * count of Remaining Size of Buffer
	 *
	 * @return the remaining Count of bytes of the Buffer
	 */
	public int remaining();

	/**
	 * @return Is Buffer is Empty
	 */
	public boolean isEmpty();

	/**
	 * Is the Buffer is on End
	 * 
	 * @return boolean for is Position of Buffer on End
	 */
	public boolean isEnd();

	/**
	 * Add lookAHead to Buffer
	 * 
	 * @param lookahead The String for look A Head String. For Simple Buffer change
	 *                  position back to the length of String or Save the String.
	 * @return Self Instance
	 */
	public BufferItem withLookAHead(CharSequence lookahead);

	/**
	 * Add lookAHead to Buffer
	 * 
	 * @param lookahead The next Character
	 * @return Self Instance
	 */
	public BufferItem withLookAHead(char lookahead);

	/**
	 * Get the next String
	 * 
	 * @param len is the Length of the new String
	 * @return the next StringPart
	 */
	public CharacterBuffer getString(int len);

	/**
	 * Get the next char in the string, skipping whitespace.
	 * 
	 * @param currentValid is the current char also a valid character
	 *
	 * @return A character, or 0 if there are no more characters.
	 */
	public char nextClean(boolean currentValid);

	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in single
	 * quotes, but an implementation is allowed to accept them.
	 * 
	 * @param quotes for End
	 * @return the StringContainer with the new Value
	 */
	public CharacterBuffer nextString(char... quotes);

	/**
	 * Return the characters up to the next close quote character. Remove QUOTES
	 * 
	 * @return the StringContainer with the new Value
	 */
	public CharacterBuffer nextString();

	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in single
	 * quotes, but an implementation is allowed to accept them.
	 *
	 * @param sc         StringContainer for manage Chars
	 * @param allowQuote is allow Quote in Stream
	 * @param nextStep   must i step next after find Text
	 * @param quotes     The quoting character, either <code>"</code>
	 *                   &nbsp;<small>(double quote)</small> or <code>'</code>
	 *                   &nbsp;<small>(single quote)</small>.
	 * @return the StringContainer with the new Value
	 */
	public CharacterBuffer nextString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes);

	/**
	 * Get the NextVlaue
	 * 
	 * @param creator         Creator for creating Child Item
	 * @param allowQuote      Is it allow Quote in NextValue
	 * @param c               CurrentChar
	 * @param allowDuppleMark Is allow DuppleMarks
	 * @return The NextValue
	 */
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c);

	/**
	 * Get the next Token
	 * 
	 * @param current   switch for add the current Character
	 * @param stopWords may be at Simple Space
	 * @return The next Token
	 */
	public CharacterBuffer nextToken(boolean current, char... stopWords);

	/**
	 * Skip.
	 *
	 * @param search    the The String of searchelements
	 * @param order     the if the order of search element importent
	 * @param notEscape Boolean if escaping the text
	 * @return true, if successful
	 */
	public boolean skipTo(String search, boolean order, boolean notEscape);

	/**
	 * Skip.
	 *
	 * @param search    the The String of searchelements
	 * @param notEscape Boolean if escaping the text
	 * @return true, if successful
	 */
	public boolean skipTo(char search, boolean notEscape);

	/**
	 * Skip number of chars
	 *
	 * @param pos the pos
	 * @return true, if successful
	 */
	public boolean skip(int pos);

	/**
	 * Skip
	 *
	 * @return true, if successful
	 */
	public boolean skip();

	/**
	 * Check values of the Current Char
	 *
	 * @param items the items
	 * @return true, if successful
	 */
	public boolean checkValues(char... items);

	/**
	 * Method for parsing String Elements
	 *
	 * @return A List of String
	 */
	public SimpleList<String> getStringList();

	/**
	 * Skip The quotes if the CurrentChar is it
	 * 
	 * @param quotes Quotes to Skip
	 * @return the Current Char
	 */
	public char skipChar(char... quotes);
}
