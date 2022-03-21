package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleList;

// TODO: Auto-generated Javadoc
/**
 * The Interface BufferItem.
 *
 * @author Stefan
 */
public interface BufferItem {
	
	/** The Constant SPACE. */
	public static final char SPACE = ' ';
	
	/** The Constant QUOTES. */
	public static final char QUOTES = '"';

	/**
	 * Length.
	 *
	 * @return the length of the buffer
	 */
	int length();

	/**
	 * Gets the char.
	 *
	 * @return The next Char
	 */
	char getChar();

	/**
	 * Gets the byte.
	 *
	 * @return the byte
	 */
	byte getByte();

	/**
	 * Gets the current char.
	 *
	 * @return The currentChar
	 */
	char getCurrentChar();

	/**
	 * Return a new Array of Elements.
	 *
	 * @param len     len of next values -1 remaining length -2 all size (Only for
	 *                BufferedBuffer)
	 * @param current Add Current Byte to Array
	 * @return The Buffer as byte Array
	 */
	byte[] array(int len, boolean current);

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	int position();

	/**
	 * count of Remaining Size of Buffer.
	 *
	 * @return the remaining Count of bytes of the Buffer
	 */
	int remaining();

	/**
	 * Checks if is empty.
	 *
	 * @return Is Buffer is Empty
	 */
	boolean isEmpty();

	/**
	 * Is the Buffer is on End.
	 *
	 * @return boolean for is Position of Buffer on End
	 */
	boolean isEnd();

	/**
	 * Add lookAHead to Buffer.
	 *
	 * @param lookahead The String for look A Head String. For Simple Buffer change
	 *                  position back to the length of String or Save the String.
	 * @return Self Instance
	 */
	BufferItem withLookAHead(CharSequence lookahead);

	/**
	 * Add lookAHead to Buffer.
	 *
	 * @param lookahead The next Character
	 * @return Self Instance
	 */
	BufferItem withLookAHead(char lookahead);

	/**
	 * Get the next String.
	 *
	 * @param len is the Length of the new String
	 * @return the next StringPart
	 */
	CharacterBuffer getString(int len);

	/**
	 * Get the next char in the string, skipping whitespace.
	 * 
	 * @return A character, or 0 if there are no more characters.
	 */
	char nextClean();
	
	   /**
     * Get the next char in the string, skipping whitespace.
     * 
     * @return A character, or 0 if there are no more characters.
     */
    char nextCleanSkip();

	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in single
	 * quotes, but an implementation is allowed to accept them.
	 * 
	 * @param quotes for End
	 * @return the StringContainer with the new Value
	 */
	CharacterBuffer nextString(char... quotes);

	/**
	 * Return the characters up to the next close quote character. Remove QUOTES
	 * 
	 * @return the StringContainer with the new Value
	 */
	CharacterBuffer nextString();

	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in single
	 * quotes, but an implementation is allowed to accept them.
	 *
	 * @param sc         StringContainer for manage Chars
	 * @param allowQuote is allow Quote in Stream
	 * @param quotes     The quoting character, either <code>"</code>
	 *                   &nbsp;<small>(double quote)</small> or <code>'</code>
	 *                   &nbsp;<small>(single quote)</small>.
	 * @return the StringContainer with the new Value
	 */

	/**
	 * Get the NextVlaue.
	 *
	 * @param stopChars      stopChars
	 * @return The NextValue
	 */
	CharacterBuffer nextValue(char[] stopChars);

	  /**
	 * Get the next Token.
	 *
	 * @param stopWords may be at Simple Space
	 * @return The next Token
	 */
	CharacterBuffer nextToken(char... stopWords);

	/**
	 * Skip.
	 *
	 * @param search    the The String of searchelements
	 * @param order     the if the order of search element importent
	 * @param notEscape Boolean if escaping the text
	 * @return true, if successful
	 */
	boolean skipTo(String search, boolean order, boolean notEscape);

	/**
	 * Skip.
	 *
	 * @param search    the The String of searchelements
	 * @param notEscape Boolean if escaping the text
	 * @return true, if successful
	 */
	boolean skipTo(char search, boolean notEscape);

	/**
	 * Skip number of chars.
	 *
	 * @param pos the pos
	 * @return true, if successful
	 */
	boolean skip(int pos);

	/**
	 * Skip.
	 *
	 * @return true, if successful
	 */
	boolean skip();

	/**
	 * Check values of the Current Char.
	 *
	 * @param items the items
	 * @return true, if successful
	 */
	boolean checkValues(char... items);

	/**
	 * Method for parsing String Elements.
	 *
	 * @return A List of String
	 */
	SimpleList<String> getStringList();

	/**
	 * Skip The quotes if the CurrentChar is it.
	 *
	 * @param quotes Quotes to Skip
	 * @return the Current Char
	 */
	char skipChar(char... quotes);
}
