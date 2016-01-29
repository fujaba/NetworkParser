package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleList;

public interface BufferItem {
	public final char SPACE=' ';
	/** @return the length of the buffer */
	public abstract int length();

	/** @return The next Char */
	public abstract char getChar();

	/** @return The currentChar */
	public abstract char getCurrentChar();
	
	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int position();
	
	/**
	 * @return the remaining Count of bytes of the Buffer
	 */
	public int remaining();
	
	/**
	 * @return Is Buffer is Empty
	 */
	public boolean isEmpty();

	/**
	 * Is the Buffer is on End
	 * @return boolean for is Position of Buffer on End
	 */
	public boolean isEnd();

	/**
	 * @return The Buffer as byte Array
	 */
	public abstract byte[] toArray();
	
	/**
	 * Add lookAHead to Buffer
	 * @param lookahead The String for look A Head String. For Simple Buffer change position back to the length of String or Save the String.
	 * @return Self Instance
	 */
	public BufferItem withLookAHead(CharSequence lookahead);

	/**
	 * Add lookAHead to Buffer
	 * @param lookahead The next Character
	 * @return Self Instance
	 */
	public BufferItem withLookAHead(char lookahead);
	
	/**
	 * Get the next String
	 * @param len is the Length of the new String
	 * @return the next StringPart
	 */
	public CharacterBuffer getString(int len);

		/**
	 * Get the next char in the string, skipping whitespace.
	  * @param currentValid
	 *			is the current char also a valid character
	 *
	 * @return A character, or 0 if there are no more characters.
	 */
	public char nextClean(boolean currentValid);
	
	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in
	 * single quotes, but an implementation is allowed to accept them.
	 *
	 * @return the StringContainer with the new Value
	 */
	public CharacterBuffer nextString();
	
	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in
	 * single quotes, but an implementation is allowed to accept them.
	 * 
	 * @param sc StringContainer for manage Chars
	 * @param allowCRLF
	 *			is allow CRLF in Stream
	 * @param allowQuote
	 *			is allow Quote in Stream
	 * @param mustQuote
	 *			must find Quote in Stream
	 * @param nextStep
	 *			must i step next after find Text
	 * @param quotes
	 *			The quoting character, either <code>"</code>
	 *			&nbsp;<small>(double quote)</small> or <code>'</code>
	 *			&nbsp;<small>(single quote)</small>.
	 * @return the StringContainer with the new Value  
	 */
	public CharacterBuffer nextString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes);	

	/**
	 * Get the NextVlaue
	 * @param creator Creator for creating Child Item
	 * @param allowQuote Is it allow Quote in NextValue
	 * @param c CurrentChar
	 * @param allowDuppleMark Is allow DuppleMarks
	 * @return The NextValue
	 */
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c);
	
	/**
	 * Get the next Token 
	 * @param stopWords may be at Simple Space
	 * @return The next Token
	 */
	public CharacterBuffer nextToken(String stopWords);
	
	/**
	 * Skip.
	 *
	 * @param search
	 *            the The String of searchelements
	 * @param order
	 *            the if the order of search element importent
	 * @param notEscape
	 *            Boolean if escaping the text
	 * @return true, if successful
	 */
	public boolean skipTo(String search, boolean order, boolean notEscape);

	/**
	 * Skip.
	 *
	 * @param search
	 *            the The String of searchelements
	 * @param notEscape
	 *            Boolean if escaping the text
	 * @return true, if successful
	 */
	public boolean skipTo(char search, boolean notEscape);

	
	/**
	 * Skip number of chars
	 *
	 * @param pos
	 *            the pos
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
	 * @param items
	 *			the items
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
	 * Split Strings
	 * @param value The String value
	 * @param split boolean for Spliting
	 * @return a List of String Parts
	 */
	public SimpleList<String> splitStrings(String value, boolean split);
	
	/**
	 * Skip The quotes if the CurrentChar is it
	 * @param quotes Quotes to Skip
	 * @return the Current Char
	 */
	public char skipChar(char... quotes);
}
