package de.uniks.networkparser.buffer;

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
import java.nio.BufferOverflowException;
import java.nio.ReadOnlyBufferException;

public class StringContainer implements CharSequence {
	/**
	 * The value is used for character storage.
	 */
	CharSequence value;

	private final static String EMPTY="";

	/**
	 * Returns {@code true} if, and only if, {@link #length()} is {@code 0}.
	 *
	 * @return {@code true} if {@link #length()} is {@code 0}, otherwise
	 *		 {@code false}
	 *
	 * @since 1.6
	 */
	public boolean isEmpty() {
		if(value==null) {
			return true;
		}
		return value.length() == 0;
	}

	/**
	 * Returns the length (character count).
	 *
	 * @return the length of the sequence of characters currently represented by
	 *		 this object
	 */
	@Override
	public int length() {
		if(value==null) {
			return 0;
		}
		return value.length();
	}

	/**
	 * Returns the current capacity. The capacity is the amount of storage
	 * available for newly inserted characters, beyond which an allocation will
	 * occur.
	 *
	 * @return the current capacity
	 */
	public int capacity() {
		return value.length();
	}

	/**
	 * Returns a string representing the data in this sequence. A new
	 * {@code String} object is allocated and initialized to contain the
	 * character sequence currently represented by this object. This
	 * {@code String} is then returned. Subsequent changes to this sequence do
	 * not affect the contents of the {@code String}.
	 *
	 * @return a string representation of this sequence of characters.
	 */
	public String toString() {
		if(value == null) {
			return EMPTY;
		}
		return this.value.toString();
	}

	/**
	 * @return The interal value
	 * Needed by {@code String} for the contentEquals method.
	 */
	public CharSequence getValue() {
		return value;
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 *
	 * <p>
	 * An invocation of this method of the form
	 *
	 * <pre>
	 * {@code
	 * sb.subSequence(begin,&nbsp;end)}
	 * </pre>
	 *
	 * behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 * {@code
	 * sb.substring(begin,&nbsp;end)}
	 * </pre>
	 *
	 * This method is provided so that this class can implement the
	 * {@link CharSequence} interface.
	 *
	 * @param start
	 *			the start index, inclusive.
	 * @param end
	 *			the end index, exclusive.
	 * @return the specified subsequence.
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}

	/**
	 * Returns a new {@code String} that contains a subsequence of characters
	 * currently contained in this character sequence. The substring begins at
	 * the specified index and extends to the end of this sequence.
	 *
	 * @param start
	 *			The beginning index, inclusive.
	 * @return The new CharSequence.
	 */
	public CharSequence substring(int start) {
		return substring(start, value.length());
	}

	/**
	 * Returns a new {@code String} that contains a subsequence of characters
	 * currently contained in this sequence. The substring begins at the
	 * specified {@code start} and extends to the character at index
	 * {@code end - 1}.
	 *
	 * @param start
	 *			The beginning index, inclusive.
	 * @param end
	 *			The ending index, exclusive.
	 * @return The new CharSequence.
	 */
	public CharSequence substring(int start, int end) {
		if (start < 0)
			start = 0;
		if (end > value.length())
			end = value.length();
		if (start > end)
			return EMPTY;
		return new CharacterBuffer().with(value, start, end);
	}

	/**
	 * Set a new Startposition
	 * @param start The new Start position relativ
	 * @return this instance
	 */
	public CharSequence split(int start) {
		if(value instanceof CharacterBuffer) {
			((CharacterBuffer)value).addStart(start);
		}else {
			value = new CharacterBuffer().with(value, start, value.length());
		}
		return this;
	}

	/**
	 * Returns the {@code char} value in this sequence at the specified index.
	 * The first {@code char} value is at index {@code 0}, the next at index
	 * {@code 1}, and so on, as in array indexing.
	 * <p>
	 * The index argument must be greater than or equal to {@code 0}, and less
	 * than the length of this sequence.
	 *
	 * <p>
	 * If the {@code char} value specified by the index is a
	 * <a href="Character.html#unicode">surrogate</a>, the surrogate value is
	 * returned.
	 *
	 * @param index
	 *			the index of the desired {@code char} value.
	 * @return the {@code char} value at the specified index.
	 * @throws IndexOutOfBoundsException
	 *			 if {@code index} is negative or greater than or equal to
	 *			 {@code length()}.
	 */
	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	/**
	 * Tests if this string ends with the specified suffix.
	 *
	 * @param suffix
	 *			the suffix.
	 * @return {@code true} if the character sequence represented by the
	 *		 argument is a suffix of the character sequence represented by
	 *		 this object; {@code false} otherwise. Note that the result will
	 *		 be {@code true} if the argument is the empty string or is equal
	 *		 to this {@code String} object as determined by the
	 *		 {@link #equals(Object)} method.
	 */
	public boolean endsWith(String suffix) {
		return startsWith(suffix, value.length() - suffix.length());
	}

	/**
	 * Tests if this string starts with the specified prefix.
	 *
	 * @param   prefix   the prefix.
	 * @return  {@code true} if the character sequence represented by the
	 *		  argument is a prefix of the character sequence represented by
	 *		  this string; {@code false} otherwise.
	 *		  Note also that {@code true} will be returned if the
	 *		  argument is an empty string or is equal to this
	 *		  {@code String} object as determined by the
	 *		  {@link #equals(Object)} method.
	 * @since   1. 0
	 */
	public boolean startsWith(String prefix) {
		return startsWith(prefix, 0);
	}

	  /**
	 * Tests if the substring of this string beginning at the
	 * specified index starts with the specified prefix.
	 *
	 * @param   prefix	the prefix.
	 * @param   toffset   where to begin looking in this string.
	 * @return  {@code true} if the character sequence represented by the
	 *		  argument is a prefix of the substring of this object starting
	 *		  at index {@code toffset}; {@code false} otherwise.
	 *		  The result is {@code false} if {@code toffset} is
	 *		  negative or greater than the length of this
	 *		  {@code String} object; otherwise the result is the same
	 *		  as the result of the expression
	 *		  <pre>
	 *		  this.substring(toffset).startsWith(prefix)
	 *		  </pre>
	 */
	public boolean startsWith(String prefix, int toffset) {
		if(value instanceof String) {
			return ((String)value).startsWith(prefix, toffset);
		}else if(value instanceof CharacterBuffer) {
			return ((CharacterBuffer)value).startsWith(prefix, toffset, false);
		}
		return value.toString().startsWith(prefix, toffset);
	}

	/**
	 * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
	 *
	 * <p> This method transfers the entire content of the given source string
	 * into this buffer.  An invocation of this method of the form
	 * <tt>dst.put(s)</tt> behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 *	 dst.put(s, 0, s.length()) </pre>
	 *
	 * @param   src
	 *		  The source string
	 *
	 * @return  This buffer
	 *
	 * @throws  BufferOverflowException
	 *		  If there is insufficient space in this buffer
	 *
	 * @throws  ReadOnlyBufferException
	 *		  If this buffer is read-only
	 */
	public final StringContainer with(CharSequence src) {
		if(value == null) {
			this.value = src;
		} else if(value instanceof CharacterBuffer) {
			((CharacterBuffer)this.value).with(src);
		} else {
			this.value = new CharacterBuffer().with(this.value).with(src);
		}
		return this;
	}

	public final StringContainer with(char src) {
		if(value == null) {
			this.value = new CharacterBuffer().with(src);

		} else if(value instanceof CharacterBuffer) {
			((CharacterBuffer)this.value).with(src);
		} else {
			this.value = new CharacterBuffer().with(this.value).with(src);
		}
		return this;
	}

	public char remove(int position) {
		if(value == null) {
			return 0;
		}
		if(value instanceof CharacterBuffer) {
			return ((CharacterBuffer)this.value).remove(position);
		}
		String newString = this.value.toString();
		char oldChar = newString.charAt(position);
		if(position == 0) {
			this.value = newString.substring(1);
		} else if(position == this.value.length()) {
			this.value = newString.substring(0, position - 1);
		} else {
			this.value = newString.substring(0, position - 1) + newString.substring(position + 1);
		}
		return oldChar;
	}

	public CharSequence trim() {
		if(value == null) {
			return value;
		}
		if(value instanceof CharacterBuffer) {
			return ((CharacterBuffer)this.value).trim();
		}
		value = value.toString().trim();
		return value;
	}
}
