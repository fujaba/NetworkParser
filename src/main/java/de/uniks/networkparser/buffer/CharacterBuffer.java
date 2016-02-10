package de.uniks.networkparser.buffer;

import de.uniks.networkparser.interfaces.BaseItem;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
/**
 * Buffer of String for alternative for StringBuffer.
 *
 */

public class CharacterBuffer extends BufferedBuffer implements CharSequence{
	/** The value is used for character storage. */
	char[] buffer;

	/**
	 * Returns the {@code char} value in this sequence at the specified index.
	 * The first {@code char} value is at index {@code 0}, the next at index
	 * {@code 1}, and so on, as in array indexing.
	 * <p>
	 * The index argument must be greater than or equal to
	 * {@code 0}, and less than the length of this sequence.
	 *
	 * <p>If the {@code char} value specified by the index is a
	 * <a href="Character.html#unicode">surrogate</a>, the surrogate
	 * value is returned.
	 *
	 * @param	  index   the index of the desired {@code char} value.
	 * @return	 the {@code char} value at the specified index.
	 */
	@Override
	public char charAt(int index) {
		if ((index < 0) || (index >= length)) {
			return 0;
		}
		index += start;
		return buffer[index];
	}

	@Override
	public byte byteAt(int index) {
		if ((index < 0) || (index >= length)) {
			return 0;
		}
		index+=start;
		return (byte) buffer[index+start];
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 *
	 * <p> An invocation of this method of the form
	 *
	 * <pre>{@code
	 * sb.subSequence(begin,&nbsp;end)}</pre>
	 *
	 * behaves in exactly the same way as the invocation
	 *
	 * <pre>{@code
	 * sb.substring(begin,&nbsp;end)}</pre>
	 *
	 * This method is provided so that this class can
	 * implement the {@link CharSequence} interface.
	 *
	 * @param	  start   the start index, inclusive.
	 * @param	  end	 the end index, exclusive.
	 * @return	 the specified subsequence.
	 *
	 * @throws  IndexOutOfBoundsException
	 *		  if {@code start} or {@code end} are negative,
	 *		  if {@code end} is greater than {@code length()},
	 *		  or if {@code start} is greater than {@code end}
	 */
	@Override
	public CharacterBuffer subSequence(int start, int end) {
		start += this.start;
		end += this.start;
		if(start<0) {
			start = position();
		}
		if (end > buffer.length) {
			end = buffer.length;
		}
		return new CharacterBuffer().with(this.buffer, start, end, false);
	}

	/**
	 * @param value
	 *			String of Value
	 * @return the CharacterBuffer
	 */
	public CharacterBuffer withValue(String value) {
		if(value != null) {
			this.buffer = value.toCharArray();
			this.length = buffer.length;
			this.start = 0;
			this.position = 0;
		}
		return this;
	}

	@Override
	public byte[] toArray() {
		byte[] result = new byte[this.length];
		for(int i=start; i< this.length;i++) {
			result[i] = (byte) buffer[i];
		}
		return result;
	}

	public char[] toCharArray() {
		char[] result = new char[this.length];
		for(int i=start; i< this.length;i++) {
			result[i] = (char) buffer[i];
		}
		return result;
	}

	/**
	 * Get the next character in the source string.
	 *
	 * @return The next character, or 0 if past the end of the source string.
	 */
	@Override
	public char getChar() {
		if (this.position+this.start >= this.buffer.length) {
			return 0;
		}
		this.position++;
		if (this.position+this.start == this.buffer.length) {
			return 0;
		}
		char c = this.buffer[this.position + this.start];
		if (c == '\r' && this.buffer[this.position+this.start + 1] == '\n') {
			this.position++;
			c = '\n';
		}
		return c;
	}

	/** Init the new CharacterBuffer
	 * @param values the reference CharArray
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer with(byte[] values) {
		this.buffer = new char[values.length];
		start = 0;
		length = values.length;
		position = 0;
		for(int i=0;i<values.length;i++) {
			this.buffer[i] = (char) values[i];
		}
		return this;
	}
	public CharacterBuffer withLine(CharSequence value) {
		with(value);
		with(BaseItem.CRLF);
		return this;
	}
	
	@Override
	public CharacterBuffer withLength(int len) {
		withBufferLength(len);
		super.withLength(len);
		return this;
	}
	
	public CharacterBuffer withBufferLength(int len) {
		if(this.buffer == null ) {
			this.buffer = new char[len];
		} else if (len+start > buffer.length) {
			char[] oldValue = this.buffer;
			this.buffer = new char[len];
			int oldLen = this.length - start;

			System.arraycopy(oldValue, start, this.buffer, 0, this.length);
			start = 0;
			this.position = 0;
			len = oldLen;
		}
		return this;
	}

	/**
	 * Set the Current Startposition
	 * @param pos The new Startposition
	 * @return This Component
	 */
	public CharacterBuffer withStartPosition(int pos) {
		int diff = pos - start;
		this.start = pos;
		if(length > diff) {
			this.length -= diff;
		}else {
			this.length = 0;
		}
		return this;
	}

	/** Init the new CharList
	 * @param values the reference CharArray
	 * @param start the Startposition for the new CharacterBuffer
	 * @param end the Endposition for the new CharacterBuffer
	 * @param copy Boolean if copy the Array to new one
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer with(char[] values, int start, int end, boolean copy) {
		if (copy) {
			this.buffer = new char[end];
			this.start = 0;
			this.position = 0;
			length = end;
			System.arraycopy(values, start, this.buffer, 0, end);
		} else {
			this.buffer = values;
			this.start = start;
			this.length = end - start;
		}
		return this;
	}

	/** Init the new CharList
	 * @param values the reference CharArray
	 * @param start the Startposition for the new CharacterBuffer
	 * @param length the Endposition for the new CharacterBuffer
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer with(char[] values, int start, int length) {
		int newLen = length+this.length;
		if ( buffer == null || newLen+this.start>buffer.length) {
			char[] oldValue = this.buffer;
			this.buffer = new char[(newLen*2+2)];
			if(oldValue != null) {
				System.arraycopy(oldValue, start, this.buffer, 0, this.length);
			}
			this.start = 0;
			this.position = 0;
		}
		System.arraycopy(values, start, this.buffer, this.length, length);
		this.length = newLen;
		return this;
	}

	/** Init the new CharacterBuffer
	 * @param values the reference CharSequence
	 * @param start the Startposition for the new CharacterBuffer
	 * @param end the Endposition for the new CharacterBuffer
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer with(CharSequence values, int start, int end) {
		if(this.buffer == null) {
			this.buffer = new char[end];
			start = 0;
			length = end;
			this.position = 0;
			System.arraycopy(values, start, this.buffer, 0, end);
		} else {
			if(this.length +values.length() > buffer.length) {
				int newCapacity = (this.length + values.length()) * 2 + 2;
				char[] copy = new char[newCapacity];
				System.arraycopy(buffer, this.start, copy, 0, length);
				buffer = copy;
				this.start = 0;
			}
			int len = values.length();
			for(int c=0;c<len; c++) {
				this.buffer[length+start+c] = values.charAt(c);
			}
			length += len;
		}
		return this;
	}

	/** Init the new CharacterBuffer
	 * @param items the reference CharSequence
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer with(CharSequence... items) {
		if(items == null) {
			return this;
		}
		if(this.buffer == null) {
			int newCapubility=0;
			for( int i=0;i<items.length;i++) {
				newCapubility += items[i].length();
			}
			this.buffer = new char[newCapubility];
			start = 0;
			length = this.buffer.length;
			int pos = 0;
			for( int i=0;i<items.length;i++) {
				int len = items[i].length();
				for(int c=0;c<len; c++) {
					this.buffer[pos++] = items[i].charAt(c);
				}
			}
		} else {
			for(CharSequence item : items) {
				if(item == null) {
					continue;
				}
				with(item, 0, item.length());
			}
		}
		return this;
	}
	/**
	 *  Append a new Character to CharacterBuffer
	 * @param item a new StartItem
	 * @return CharacterBuffer Instance
	 */
	public CharacterBuffer withStart(char item) {
		if(start>0) {
			this.buffer[--start] = item;
		} else {
			char[] oldValue = this.buffer;
			this.buffer = new char[buffer.length + 1];
			this.buffer[0] = item;
			this.position = 0;
			System.arraycopy(oldValue, start, this.buffer, 1, length);
			this.length++;
		}
		return this;
	}

	/** Init the new CharList
	 * @param value the reference CharSequence
	 * @return the new CharList
	 */
	public CharacterBuffer set(CharSequence value) {
		this.start = 0;
		this.length = value.length();
		if(this.buffer.length < value.length()) {
			this.buffer = new char[this.length];
		}
		for(int i=0; i < this.length;i++) {
			this.buffer[i] = value.charAt(i);
		}
		return this;
	}

	/** Init the new CharacterBuffer
	 * @param value the reference CharSequence
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer set(char value) {
		this.start = 0;
		this.length = 1;
		if(this.buffer.length < 1) {
			this.buffer = new char[1];
		}
		this.buffer[0] = value;
		return this;
	}

	public boolean startsWith(CharSequence prefix, int toffset, boolean ignoreCase) {
		if (buffer == null) {
			return false;
		}
		char ta[] = buffer;
		int to = toffset+start;
		int pc = prefix.length();
		if ((toffset < 0) || (toffset > buffer.length - pc)) {
			return false;
		}
		int po = 0;
		// Note: toffset might be near -1>>>1.
		while (--pc >= 0) {
			char c1 =ta[to++];
			char c2 = prefix.charAt(po++);
			if (c1 != c2) {
	            if (ignoreCase) {
	                if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
	                    continue;
	                }
	            }
				return false;
			}
		}
		return true;
	 }

	/**
	 * Returns the number of elements between the current position and the
	 * limit.
	 *
	 * @return  The number of elements remaining in this buffer
	 */
	public final int remaining() {
		return buffer.length - start;
	}

	public final void clear() {
		this.length = 0;
		this.start = 0;
		this.position = 0;
	}

	public char remove(int position) {
		char oldChar = this.buffer[position];
		if(position == start ) {
			start++;
			return this.buffer[position];
		} else if(position == length ) {
		} else {
			char[] copy = new char[this.buffer.length];
			System.arraycopy(this.buffer, start, copy, 0, position - 1);
			System.arraycopy(this.buffer, position + 1, copy, position, length -position);
			start = 0;
		}
		length--;
		return oldChar;
	}

	public CharacterBuffer addStart(int pos) {
		this.start += pos;
		return this;
	}

	public CharacterBuffer trim() {
		while (length>0 && (buffer[length +start - 1] <= SPACE)) {
			length--;
		}
		while ((start < length) && (buffer[start] <= SPACE)) {
			start++;
			length--;
		}
		return this;
	}
	
	public boolean isEmptyCharacter() {
		if(super.isEmpty()) {
			return true;
		}
		int len = length;
		int pos=start;
		while (len>0 && (buffer[len + pos - 1] <= SPACE)) {
			len--;
		}
		while ((pos < len) && (buffer[pos] <= SPACE)) {
			pos++;
			len--;
		}
		return len == 0;
	}

	public void withRepeat(String string, int rest) {
		int newCapacity = this.length + rest*string.length();
		if(this.buffer == null) {
			this.buffer = new char[newCapacity];
			start = 0;
			length = 0;
		} else {
			if(newCapacity > buffer.length) {
				char[] copy = new char[newCapacity];
				System.arraycopy(buffer, this.start, copy, 0, length);
				buffer = copy;
				this.start = 0;
			}
		}
		for( int i=0; i < rest;i++) {
			for(int c=0;c<string.length(); c++) {
				this.buffer[length++] = string.charAt(c);
			}
		}
	}
	/** Init the new CharacterBuffer
	 * @param items the reference CharSequence
	 * @return the new CharacterBuffer
	 */
	public CharacterBuffer withObjects(Object... items) {
		if(items == null) {
			return this;
		}
		if(this.buffer == null) {
			int newCapubility=0;
			for( int i=0;i<items.length;i++) {
				if(items[i] != null) {
					if((items[i] instanceof CharSequence) == false)  {
						items[i] = items[i].toString();
					}
					newCapubility += ((CharSequence)items[i]).length();
				}
			}
			this.buffer = new char[newCapubility];
			start = 0;
			length = this.buffer.length;
			int pos = 0;
			for( int i=0;i<items.length;i++) {
				if(items[i] != null) {
					CharSequence value = (CharSequence) items[i];
					int len = value.length();
					for(int c=0;c<len; c++) {
						this.buffer[pos++] = value.charAt(c);
					}
				}
			}
		} else {
			for(Object item : items) {
				CharSequence value = null;
				if(item instanceof CharSequence)  {
					value = (CharSequence) item;
				} else if(item != null) {
					value = item.toString();
				}
				if(value != null) {
					with(value, 0, value.length());
				}
			}
		}
		return this;
	}

	/** Init the new CharList
	 * @param src the reference CharSequence
	 * @return the new CharList
	 */
	public CharacterBuffer with(char src) {
		if(this.buffer == null) {
			this.buffer = new char[5];
			start = 0;
			length = 1;
			this.buffer[0] = src;
		} else {
			if(this.length + 1 > buffer.length) {
				int newCapacity = buffer.length * 2 + 2;
				char[] copy = new char[newCapacity];
				System.arraycopy(buffer, this.start, copy, 0, length);
				buffer = copy;
				this.start = 0;
			}
			this.buffer[length++] = src;
		}
		return this;
	}

	public void reset() {
		this.length = 0;
		this.position = 0;
		this.start = 0;
	}

	@Override
	public String toString() {
		if(length<1) {
			return "";
		}
		return new String(buffer, start, length);
	}

	public boolean equals(CharSequence other) {
		if(other==null || other.length() != length) {
			return false;
		}
		return startsWith(other, 0, false);
	 }
	public boolean equalsIgnoreCase(CharSequence other) {
		if(other==null || other.length() != length) {
			return false;
		}
		return startsWith(other, 0, true);
	}

	public int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	public int indexOf(int ch, int fromIndex) {
		final int max = length();
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= length) {
			// Note: fromIndex might be near -1>>>1.
			return -1;
		}
		for (int i = fromIndex; i < max; i++) {
			if (buffer[i+start] == ch) {
				return i - start;
			}
		}
		return -1;
	}

	public int lastIndexOf(char ch) {
		for (int i = length - 1; i >= start; i--) {
			if (buffer[i] == ch) {
				return i - start;
			}
		}
		return -1;
	}
	
	/**
	 * get the () values
	 *
	 * @param start
	 *            Startcharacter
	 * @param end
	 *            Endcharacter
	 * @return string of values
	 */
	public String getStringPart(Character start, Character end) {
		int count = 1;
		Character current = null;
		int pos;
		if (getCurrentChar() == start) {
			pos = position();
		} else {
			pos = position() - 1;
		}
		while (!isEnd()) {
			current = getChar();
			if (current.compareTo(end) == 0) {
				count--;
				if (count == 0) {
					skip();
					return subSequence(pos, position()).toString();
				}
				continue;
			}
			if (current.compareTo(start) == 0) {
				count++;
			}
		}
		return null;
	}

	public boolean endsWith(CharSequence string, boolean ignoreCase) {
		int pos = this.length() - string.length();
		if(pos<0) {
			return false;
		}
		return startsWith(string, pos, ignoreCase);
	}
}
