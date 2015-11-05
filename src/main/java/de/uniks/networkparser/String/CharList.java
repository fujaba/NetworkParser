package de.uniks.networkparser.String;

public class CharList implements CharSequence{
    /**
     * The value is used for character storage.
     */
    char[] value;

    /**
     * The count is the number of characters used.
     */
    int count;

    /**
     * The start is the number of characters started.
     */
    int start;

    
    /** Init the new CharList
	 * @param values the reference CharArray
	 * @param start the Startposition for the new CharList
	 * @param end the Endposition for the new CharList
	 * @param copy Boolean if copy the Array to new one
	 * @return the new CharList
	 */
	public CharList with(char[] values, int start, int end, boolean copy) {
		if (copy) {
			this.value = new char[end];
			start = 0;
			count = end;
			System.arraycopy(values, start, this.value, 0, end);
		} else {
			this.value = values;
			this.start = start;
			this.count = end;
		}
		return this;
	}

	/** Init the new CharList
	 * @param values the reference CharSequence
	 * @param start the Startposition for the new CharList
	 * @param end the Endposition for the new CharList
	 * @return the new CharList
	 */
	public CharList with(CharSequence values, int start, int end) {
		if(this.value == null) {
			this.value = new char[end];
			start = 0;
			count = end;
			System.arraycopy(values, start, this.value, 0, end);
		} else {
			if(this.count +values.length() > value.length) {
				int newCapacity = value.length * 2 + 2;
				char[] copy = new char[newCapacity];
		        System.arraycopy(value, this.start, copy, 0, count);
		        value = copy;
		        this.start = 0;
			}
			int len = values.length();
			for(int c=0;c<len; c++) {
				this.value[count+start+c] = values.charAt(c);
			}
		}
		return this;
	}
	
	/** Init the new CharList
	 * @param items the reference CharSequence
	 * @return the new CharList
	 */
	public CharList with(CharSequence... items) {
		if(items == null) {
			return this;
		}
		if(this.value == null) {
			int newCapubility=0;
			for( int i=0;i<items.length;i++) {
				newCapubility += items[i].length();
			}
			this.value = new char[newCapubility];
			start = 0;
			count = this.value.length;
			int pos = 0;
			for( int i=0;i<items.length;i++) {
				int len = items[i].length();
				for(int c=0;c<len; c++) {
					this.value[pos++] = items[i].charAt(c);
				}
			}
		} else {
			for(CharSequence item : items) {
				with(item, 0, item.length());
			}
		}
		return this;
	}
	
	/** Init the new CharList
	 * @param src the reference CharSequence
	 * @return the new CharList
	 */
	public CharList with(char src) {
		if(this.value == null) {
			this.value = new char[5];
			start = 0;
			count = 1;
			this.value[0] = src;
		} else {
			if(this.count + 1 > value.length) {
				int newCapacity = value.length * 2 + 2;
				char[] copy = new char[newCapacity];
		        System.arraycopy(value, this.start, copy, 0, count);
		        value = copy;
		        this.start = 0;
			}
			this.value[count++] = src;
		}
		return this;
	}

    /**
     * Returns the length (character count).
     *
     * @return  the length of the sequence of characters currently
     *          represented by this object
     */
    @Override
    public int length() {
        return count;
    }

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
     * @param      index   the index of the desired {@code char} value.
     * @return     the {@code char} value at the specified index.
     * @throws     IndexOutOfBoundsException  if {@code index} is
     *             negative or greater than or equal to {@code length()}.
     */
	@Override
	public char charAt(int index) {
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        return value[index];
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
     * @param      start   the start index, inclusive.
     * @param      end     the end index, exclusive.
     * @return     the specified subsequence.
     *
     * @throws  IndexOutOfBoundsException
     *          if {@code start} or {@code end} are negative,
     *          if {@code end} is greater than {@code length()},
     *          or if {@code start} is greater than {@code end}
     */
	@Override
	 public final CharSequence subSequence(int start, int end) {
		return new CharList().with(this.value, this.start + start, end - start, false);
	}

	public boolean startsWith(String prefix, int toffset) {
		char ta[] = value;
        int to = toffset;
        int pc = prefix.length();
        if ((toffset < 0) || (toffset > value.length - pc)) {
            return false;
        }        
        int po = 0;
        // Note: toffset might be near -1>>>1.
        while (--pc >= 0) {
            if (ta[to++] != prefix.charAt(po++)) {
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
        return value.length - count - start;
    }
    
    public final void clear() {
    	this.count = 0;
    	this.start = 0;
    }

	public char remove(int position) {
		char oldChar = this.value[position];
		if(position == start ) {
			start++;
			return this.value[position];
		} else if(position == count ) {
		} else {
			char[] copy = new char[this.value.length]; 
			System.arraycopy(this.value, start, copy, 0, position - 1);
			System.arraycopy(this.value, position + 1, copy, position, count -position);
			start = 0;
		}
		count--;
		return oldChar;
	}

	public CharList addStart(int pos) {
		this.start += pos;
		return this;
	}
	
	public CharSequence trim() {
		int len = count+start;
		while ((start < len) && (value[len - 1] <= ' ')) {
            len--;
        }
		this.count = len - start;
		while ((start < len) && (value[start] <= ' ')) {
        	start++;
        }
		return this;
	}
	
	public String toString() {
		return new String(value, start, count);
	}
	
	/** Init the new CharList
	 * @param items the reference CharSequence
	 * @return the new CharList
	 */
	public CharList set(CharSequence value) {
		this.start = 0;
		this.count = value.length();
		if(this.value.length < value.length()) {
			this.value = new char[this.count];
		}
		for(int i=0; i < this.count;i++) {
			this.value[i] = value.charAt(i);
		}
		return this;
	}
	/** Init the new CharList
	 * @param items the reference CharSequence
	 * @return the new CharList
	 */
	public CharList set(char value) {
		this.start = 0;
		this.count = 1;
		if(this.value.length < 1) {
			this.value = new char[1];
		}
		this.value[0] = value;
		return this;
	}
}
