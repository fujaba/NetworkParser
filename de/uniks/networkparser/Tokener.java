package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Buffer;
/**parseToEntity
 * The Class Tokener.
 */

public abstract class Tokener {
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	
	/** BUFFER */
	protected Buffer buffer;
	
	protected NetworkParserLog logger=new NetworkParserLog();
	
	/**
	 * Reset the Tokener
	 * 
	 * @param value The Text for parsing
	 * @return Itself
	 */
	public Tokener withText(String value) {
		this.buffer = new CharacterBuffer().withValue(value);
		return this;
	}

	/**
	 * Back up one character. This provides a sort of lookahead capability, so
	 * that you can test for a digit or letter before attempting to parse the
	 * next number or identifier.
	 */
	public void back() {
		if (this.buffer.length() <= 0) {
			logger.error(this, "back", "Stepping back two steps is not supported");
			return;
		}
		this.buffer.back();
	}

	/**
	 * Check if End of String
	 * 
	 * @return true, if successful
	 */
	public boolean isEnd() {
		return buffer.isEnd();
	}

	/**
	 * Get the next character in the source string.
	 * 
	 * @return The next character, or 0 if past the end of the source string.
	 */
	public char next() {
		if (this.isEnd()) {
			return 0;
		}
		return buffer.getChar();
	}

	/**
	 * Get the next n characters.
	 * 
	 * @param n
	 *            The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not
	 *         n characters remaining in the source string.
	 */
	public String getNextString(int n) {
		int pos = 0;
		if(n<-1){
			n=n*-1;
			char[] chars = new char[n];
			while (pos < n) {
				chars[pos] = this.buffer.charAt(this.buffer.position() - (n-pos++));
			}
			return new String(chars);
		}else if (n == -1) {
			n = buffer.length() - this.buffer.position();
		} else if (n == 0) {
			return "";
		} else if (this.buffer.position() + n > this.buffer.length()) {
			n = buffer.length() - this.buffer.position();
		}
		char[] chars = new char[n];

		while (pos < n) {
			chars[pos] = this.buffer.charAt(this.buffer.position() + pos++);
		}
		return new String(chars);
	}

	/**
	 * Get the next n characters.
	 * 
	 * @param n
	 *            The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not
	 *         n characters remaining in the source string.
	 */
	public String skipPos(int n) {
		if (n == -1) {
			n = buffer.remaining();
		} else if (n == 0) {
			return "";
		}

		char[] chars = new char[n];
		int pos = 0;

		while (pos < n) {
			chars[pos] = next();
			if (isEnd()) {
				logger.error(this, "skipPos", "Substring bounds error", n);
				return null;
			}
			pos += 1;
		}
		return new String(chars);
	}

	/**
	 * Get the next char in the string, skipping whitespace.
	 * 
	 * @return A character, or 0 if there are no more characters.
	 */
	public char nextClean() {
		char c=getCurrentChar();
		do{
			c = next();
		}while(c!=0 && c <= ' ');
		return c;
	}
	
	public char nextStartClean() {
		char c=getCurrentChar();
		if(c!=0 && c <= ' '){
			c=nextClean();
		}
		return c;
	}
	
	public String nextString(char quote, boolean allowCRLF){
		return nextString(quote, allowCRLF, false, false, true);
	}
	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in
	 * single quotes, but an implementation is allowed to accept them.
	 * 
	 * @param quote
	 *            The quoting character, either <code>"</code>
	 *            &nbsp;<small>(double quote)</small> or <code>'</code>
	 *            &nbsp;<small>(single quote)</small>.
	 * @param allowCRLF
	 *            is allow CRLF in Stream
	 * @param allowQuote
	 *            is allow Quote in Stream
	 * @param mustQuote
	 *            must find Quote in Stream

	 * @param nextStep
	 *            must i step next after find Text
	 * @return A String.
	 */
	public String nextString(char quote, boolean allowCRLF, boolean allowQuote, boolean mustQuote, boolean nextStep) {
		if(getCurrentChar()==0){
			return "";
		}
		if(getCurrentChar()==quote){
			if(nextStep){
				next();
			}
			return "";
		}
		if(buffer.isCache()){
			return getString(quote, allowCRLF, allowQuote, mustQuote, nextStep);
		}
		return getStringBuffer(quote, allowCRLF, allowQuote, mustQuote, nextStep);
	}
	
	private String getString(char quote, boolean allowCRLF, boolean allowQuote, boolean mustQuote, boolean nextStep){
		int startpos = this.buffer.position();
	      char c;
	      boolean isQuote=false;
	      char b = 0;
	      do
	      {
	         c = next();
	         switch (c)
	         {
	         case 0:
	         case '\n':
	         case '\r':
	            if (!allowCRLF)
	            {
	            	logger.error(this, "getString", "Unterminated string", quote, allowCRLF, allowQuote, mustQuote, nextStep);
	            	return null;
	            }
	         default:
	            if (b == '\\')
	            {
					if (c == '\\')
					{
						c = 1;
						isQuote = false;
					}
	            	if(allowQuote){
		               b = c;
		               c = 1;
		               continue;
	            	}
	            	isQuote = true;
	            }
	         }
	         b = c;
	      }
	      while (c != 0 && c != quote);

	      int endPos = this.buffer.position();
	      if(nextStep){
	    	  next();
	      }
	      if( (isQuote && allowQuote) || mustQuote){
	    	  return this.buffer.substring(startpos, endPos - startpos - 1);
	      }

	      return this.buffer.substring(startpos, endPos - startpos);
	}

	private String getStringBuffer(char quote, boolean allowCRLF, boolean allowQuote, boolean mustQuote, boolean nextStep){
		StringBuilder sb = new StringBuilder();
		sb.append(getCurrentChar());

		char c, b = 0;
		boolean isQuote=false;
		do{
			c = next();
			switch (c) {
			case 0:
			case '\n':
			case '\r':
				if (!allowCRLF) {
					logger.error(this, "getStringBuffer", "Unterminated string", quote, allowCRLF, allowQuote, mustQuote, nextStep);
					return null;
				}
			default:
				if (b == '\\')
	            {
	            	if(allowQuote){
	            		sb.append(c);
	            		if (c == '\\'){
	            			c = 1;
	            		}
	            		b = c;
	            		c = 1;
	            		continue;
	            	}else if (c == '\\')
					{
						c = 1;
					}
	            	isQuote = true;
	            }
				if (c != quote){
					sb.append(c);
				}
				b = c;
			}
		}while (c != 0 && c != quote);
		if(nextStep){
			next();
	    }
		if(isQuote || mustQuote){
	    	  return sb.substring(0, sb.length()-1);
		}
		return sb.toString();
	}

	/**
	 * Handle unquoted text. This could be the values true, false, or null, or
	 * it can be a number. An implementation (such as this one) is allowed to
	 * also accept non-standard forms.
	 * 
	 * Accumulate characters until we reach the end of the text or a formatting
	 * character.
	 * 
	 * @param creator The creatorobject
	 * @param allowQuote is allow Quote in Strem
	 * @return the new Element
	 */
	public Object nextValue(BaseItem creator, boolean allowQuote) {
		return nextValue(creator, allowQuote, nextStartClean());
	}
	
	public Object nextValue(BaseItem creator, boolean allowQuote, char c) {
		String value;
		if(buffer.isCache()){
			int start=buffer.position();
			while (c >= ' ' && getStopChars().indexOf(c) < 0) {
				c = next();
			}
			value = buffer.substring(start, buffer.position() - start).trim();
		}else{
			StringBuilder sb = new StringBuilder();
			while (c >= ' ' && getStopChars().indexOf(c) < 0) {
				sb.append(c);
				c = next();
			}
			value = sb.toString().trim();
		}
		
		if (value.length()<1) {
			logger.error(this, "nextValue", "Missing value", creator, allowQuote, c);
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
		 * If it might be a number, try converting it. If a number cannot be
		 * produced, then the value will just be a string. Note that the plus
		 * and implied string conventions are non-standard. A JSON parser may
		 * accept non-JSON forms as long as it accepts all correct JSON forms.
		 */
		Double d;
		char b = value.charAt(0);
		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
			try {
				if (value.indexOf('.') > -1 || value.indexOf('e') > -1
					|| value.indexOf('E') > -1) {
					d = Double.valueOf(value);
					if (!d.isInfinite() && !d.isNaN()) {
						return d;
					}
				} else {
					Long myLong = Long.valueOf(value);
					if (myLong.longValue() == myLong.intValue()) {
						return Integer.valueOf(myLong.intValue());
						}
						return myLong;
					}
			} catch (Exception ignore) {
			// DO nothing
			}
		}
		return value;
	}

	protected String getStopChars() {
		return STOPCHARS;
	}

	/**
	 * Skip.
	 * 
	 * @param pos
	 *            the pos
	 * @return true, if successful
	 */
	public boolean skip(int pos) {
		while (pos > 0) {
			if (next() == 0) {
				return false;
			}
			pos--;
		}
		return true;
	}

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
	public boolean stepPos(String search, boolean order, boolean notEscape) {
		char[] character = search.toCharArray();
		int z = 0;
		int strLen = character.length;
		int len = buffer.length();
		char lastChar = 0;
		if (this.buffer.position() > 0 && this.buffer.position() < len) {
			lastChar = this.buffer.charAt(this.buffer.position() - 1);
		}
		while (this.buffer.position() < len) {
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
					if (currentChar == zeichen
							&& (!notEscape || lastChar != '\\')) {
						return true;
					}
				}
			}
			lastChar = currentChar;
			next();
		}
		return false;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int position() {
		return this.buffer.position();
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int length() {
		return buffer.length();
	}

	/**
	 * Make a printable string of this JSONTokener.
	 * 
	 * @return " at {index} [character {character} line {line}]"
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}

	/**
	 * Char at.
	 * 
	 * @param pos the Position of the bufferarray
	 * @return the char
	 */
	public char charAt(int pos) {
		return this.buffer.charAt(pos);
	}

	/**
	 * Gets the current char.
	 * 
	 * @return the current char
	 */
	public char getCurrentChar() {
		if (buffer.remaining()>0) {
			return this.buffer.charAt(this.buffer.position());
		}
		return 0;
	}

	/**
	 * @param positions
	 *            first is start Position, second is Endposition
	 * 
	 *            Absolut fix Start and End start&gt;0 StartPosition end&gt;Start
	 *            EndPosition
	 * 
	 *            Absolut from fix Position Start&gt;0 Position end NULL To End end
	 *            -1 To this.index
	 * 
	 *            Relativ from indexPosition Start Position from this.index +
	 *            (-Start) End = 0 current Position
	 * 
	 * @return substring from buffer
	 */
	public String substring(int... positions) {
		int start = positions[0], end = -1;
		if (positions.length < 2) {
			// END IS END OF BUFFER (Exclude)
			end = buffer.length();
		} else {
			end = positions[1];
		}
		if (end == -1) {
			end = this.buffer.position();
		} else if (end == 0) {
			if (start < 0) {
				end = this.buffer.position();
				start = this.buffer.position() + start;
			} else {
				end = this.buffer.position() + start;
				if (this.buffer.position() + end > buffer.length()) {
					end = buffer.length();
				}
				start = this.buffer.position();
			}
		}
		if (start < 0 || end <= 0 || start > end) {
			return "";
		}
		return this.buffer.substring(start, end-start);
	}

	/**
	 * Check values.
	 * 
	 * @param items
	 *            the items
	 * @return true, if successful
	 */
	public boolean checkValues(char... items) {
		char current = this.buffer.charAt(this.buffer.position());
		for (char item : items) {
			if (current == item) {
				return true;
			}
		}
		return false;
	}

	public String getNextTag() {
		nextClean();
		int startTag = this.buffer.position();
		if (stepPos(" >//<", false, true)) {
			return this.buffer.substring(startTag, this.buffer.position()-startTag);
		}
		return "";
	}

	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index) {
		this.buffer.withPosition(index);
	}
	
	public byte[] toArray(){
		return buffer.toArray();
	}
	
	public String toText(){
		return buffer.toText();
	}
	
	public Tokener withBuffer(Buffer buffer){
		this.buffer=buffer;
		return this;
	}

	public abstract void parseToEntity(AbstractKeyValueList<?, ?> entity);

	public abstract void parseToEntity(AbstractEntityList<?> entityList);
}
