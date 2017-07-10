package de.uniks.networkparser.buffer;

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
import de.uniks.networkparser.interfaces.BaseItem;
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
	 * @param index		the index of the desired {@code char} value.
	 * @return			the {@code char} value at the specified index.
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
	 * @param start		the start index, inclusive.
	 * @param end		the end index, exclusive.
	 * @return			the specified subsequence.
	 *
	 * @throws IndexOutOfBoundsException
	 *		if {@code start} or {@code end} are negative,
	 *		if {@code end} is greater than {@code length()}, or if {@code start} is greater than {@code end}
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
	 * Set the currentVlaue to Buffer
	 *
	 * @param value		String of Value
	 * @return			the CharacterBuffer
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

	public char[] toCharArray() {
		char[] result = new char[this.length];
		for(int i=start; i< this.length;i++) {
			result[i] = (char) buffer[i];
		}
		return result;
	}
	
	public byte[] toByteArray() {
		byte[] result = new byte[this.length];
		for(int i=start; i< this.length;i++) {
			result[i] = (byte) buffer[i];
		}
		return result;
	}

	public boolean replace(int start, int end, String replace) {
		int pos =0;
		int diff = replace.length() - (end-start);
		char[] oldChar = null;
		int oldStart = 0;
		int oldLen=0;
		if(this.length + diff > this.buffer.length) {
			//Argh array is to Small
			int newCapacity = (this.length + diff) * 2 + 2;
			oldChar = this.buffer;
			
			char[] copy = new char[newCapacity];
			System.arraycopy(buffer, this.start, copy, 0, start);
			oldStart = end;
			oldLen = this.length - oldStart;
			this.buffer = copy;
			this.start = 0;
		}
		start += this.start;
		end += this.start;
		
	if(diff < 0) {
			while(start<(end+diff)) {
				this.buffer[start++] = replace.charAt(pos++);
			} 
			System.arraycopy(buffer, start, buffer, start+diff, buffer.length - start);
		} else {
			while(start<end) {
				this.buffer[start++] = replace.charAt(pos++);
			}
			if(diff > 0) {
				if(oldChar == null) {
					oldLen = (this.length+this.start) - start;
					oldChar = new char[oldLen];
					System.arraycopy(buffer, start, oldChar, 0, oldLen);
				}
				int no=0;
				while(no<diff) { 
					this.buffer[start++] = replace.charAt(pos++);
					no++;
				}
				if(oldLen>0) {
					System.arraycopy(oldChar, oldStart, buffer, start, oldLen);
				}
			}
		}
		this.length = this.length + diff;
		this.position = 0;
		return true;
	}
	
	public void replace(String search, String replace) {
		int deleted=0;
		CharacterBuffer inserts = null;
		int pos=position + start;
		int len = this.length + start;
		int startSet=0;
		while(pos < len) {
			int i=0;
			for(;i<search.length();i++) {
				if(buffer[pos + i] != search.charAt(i)) {
					break;
				}
			}
			if(i == search.length()) {
				int diff = replace.length()-search.length();
				if(diff<0) {
					for(i=0;i<replace.length();i++) {
						buffer[pos + deleted + i]=replace.charAt(i);
					}
					deleted += diff;
					pos += search.length();
				} else {
					if(inserts==null) {
						for(i=0;i<search.length();i++) {
							buffer[pos + i]=replace.charAt(i);
						}
						inserts = new CharacterBuffer();
						deleted +=replace.length() - i;
						pos += i;
						startSet = pos;
					}else {
						i=0;
						deleted +=replace.length() - search.length();
						pos += search.length();
					}
					for(;i<replace.length();i++) {
						inserts.with(replace.charAt(i));
					}
				}
			}else {
				if(deleted == 0){
				}else if(deleted < 0){
					buffer[pos + deleted] = buffer[pos];
				}else {
					inserts.with(buffer[pos + i]);
				}
				pos++;
			}
		}
		pos = pos + deleted-start;
		if(inserts != null) {
			if(this.length < pos) {
				char[] copy = new char[pos];
				startSet -= this.start;
				System.arraycopy(buffer, this.start, copy, 0, startSet);
				buffer = copy;
				this.start = 0;
			}
			for(int i=0;i<inserts.length();i++) {
				this.buffer[startSet + i] = inserts.charAt(i);
			}
		}
		this.length = pos;
	}

	/**
	 * Get the next character in the source string.
	 *
	 * @return The next character, or 0 if past the end of the source string.
	 */
	@Override
	public char getChar() {
		if(this.buffer == null) {
			return 0;
		}
		if (this.position+this.start >= this.buffer.length) {
			return 0;
		}
		if(this.position<this.length) {
			this.position++;
		}
		if (this.position+this.start == this.buffer.length) {
			return 0;
		}
		char c = this.buffer[this.position + this.start];
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
			System.arraycopy(oldValue, start, this.buffer, 0, this.length);
			start = 0;
			this.position = 0;
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

	public CharacterBuffer write(byte[] values, int length) {
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
		for(int i=this.length;i<newLen;i++) {
			this.buffer[i] = (char) values[i-this.length];
		}
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
				if(items[i] != null) {
					newCapubility += items[i].length();
				}
			}
			this.buffer = new char[newCapubility];
			start = 0;
			length = this.buffer.length;
			int pos = 0;
			for( int i=0;i<items.length;i++) {
				if(items[i] == null) {
					continue;
				}
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
	
	public CharacterBuffer with(int value) {
		String bytes=""+value;
		this.with(bytes);
		return this;
	}
	
	public CharacterBuffer with(long value) {
		String bytes=""+value;
		this.with(bytes);
		return this;
	}
	
	/**
	 * Append a new Character to CharacterBuffer
	 * @param item a new StartItem
	 * @return CharacterBuffer Instance
	 */
	public CharacterBuffer withStart(char item) {
		if(start>0) {
			this.buffer[--start] = item;
			return this;
		}
		char[] oldValue = this.buffer;
		this.buffer = new char[buffer.length + 1];
		this.buffer[0] = item;
		this.position = 0;
		System.arraycopy(oldValue, start, this.buffer, 1, length);
		this.length++;
		return this;
	}
	/**
	 * Append a new Character to CharacterBuffer
	 * @param item a new StartItem
	 * @return CharacterBuffer Instance
	 */
	public CharacterBuffer withStart(CharSequence item, boolean newLine) {
		if(item == null) {
			return this;
		}
		int len = item.length();
		if(newLine) {
			len += 2;
		}
		if(start>len) {
			if(newLine) {
				this.buffer[--start] = '\n';
				this.buffer[--start] = '\r';
			}
			for(int i=item.length()-1;i>=0;i--) {
				this.buffer[--start] = item.charAt(i);
			}
			return this;
		}
		char[] oldValue = this.buffer;
		if(buffer != null) {
			this.buffer = new char[buffer.length + len];
		} else {
			this.buffer = new char[len];
		}
		for(int i=0;i<item.length();i++) {
			this.buffer[i] = item.charAt(i);
		}
		if(newLine) {
			this.buffer[item.length()] = '\r';
			this.buffer[item.length()+1] = '\n';
		}
		this.position = 0;
		if(oldValue != null) {
			System.arraycopy(oldValue, start, this.buffer, len, length);
			start = 0;
		}
		length += len;
		return this;
	}

	/** Init the new CharList
	 * @param value the reference CharSequence
	 * @return the new CharList
	 */
	public CharacterBuffer set(CharSequence value) {
		this.start = 0;
		this.length = value.length();
		if(this.buffer == null || this.buffer.length < value.length()) {
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
	
	public boolean startsWith(CharSequence prefix) {
		return this.startsWith(prefix, 0, false);
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
	 * Returns the number of elements between the current position and the limit.
	 *
	 * @return The number of elements remaining in this buffer
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
		this.length -= pos;
		return this;
	}

	public CharacterBuffer trimStart(int pos) {
		this.start += pos;
		this.length -= pos;
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
					if((items[i] instanceof CharSequence) == false) {
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
				if(item instanceof CharSequence) {
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
	
	public int indexOf(String ch) {
		int pos = 0;
		for(int i=0;i<ch.length();i++) {
			pos = indexOf(ch.charAt(i), pos);
			if(pos<0) {
				break;
			}
		}
		return pos;
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
				return i;
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
	 * @param start	Startcharacter
	 * @param end	Endcharacter
	 * @return 		String of values
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

	public void setNextString(CharSequence property, int pos) {
		this.withLength(pos);
		this.start = 0;
		this.with(property);
	}

	public static CharacterBuffer create(CharSequence value) {
		if(value instanceof CharacterBuffer) {
			return (CharacterBuffer) value; 
		}
		CharacterBuffer buffer = new CharacterBuffer();
		if(value instanceof String) {
			buffer.with(value);
		}
		return buffer;
	}
	public String toCurrentString() {
		if(length<1) {
			return "";
		}
		return new String(buffer, position, length-position);
	}
}
