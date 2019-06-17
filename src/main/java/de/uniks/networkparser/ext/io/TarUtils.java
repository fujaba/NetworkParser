/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.uniks.networkparser.ext.io;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import de.uniks.networkparser.SimpleException;

public class TarUtils {
//CONST
	/** Default record size */
	public static final int DEFAULT_RCDSIZE = 512;

	/** Default block size */
	public static final int DEFAULT_BLKSIZE = DEFAULT_RCDSIZE * 20;

	/** GNU format as per before tar 1.12. */
	public static final int FORMAT_OLDGNU = 2;
	/** Pure Posix format. */
	public static final int FORMAT_POSIX = 3;
	/** xstar format used by Joerg Schilling's star. */
	public static final int FORMAT_XSTAR = 4;
	/** Offset inside the header for the xstar magic bytes. */
	public static final int XSTAR_MAGIC_OFFSET = 508;
	/** Length of the XSTAR magic. */
	public static final int XSTAR_MAGIC_LEN = 4;

	/** Length of the prefix field in xstar archives. */
	public static final int PREFIXLEN_XSTAR = 131;

	/** Offset of start of magic field within header record */
	public static final int VERSION_OFFSET = 263;
	/**
	 * Previously this was regarded as part of "magic" field, but it is separate.
	 */
	public static final int VERSIONLEN = 2;
	/** The length of the user id field in a header buffer. */
	public static final int UIDLEN = 8;

	/** The length of the group id field in a header buffer. */
	public static final int GIDLEN = 8;

	/** Offset of the checksum field within header record. */
	public static final int CHKSUM_OFFSET = 148;
	/**
	 * The length of the size field in a header buffer. Includes the trailing space
	 * or NUL.
	 */
	public static final int SIZELEN = 12;
	/** The length of the user name field in a header buffer. */
	public static final int UNAMELEN = 32;
	/** The length of the group name field in a header buffer. */
	public static final int GNAMELEN = 32;

	/**
	 * The maximum size of a file in a tar archive which can be expressed in octal
	 * char notation (that's 11 sevens, octal).
	 */
	public static final long MAXSIZE = 077777777777L;
	/** Offset of start of magic field within header record */
	public static final int MAGIC_OFFSET = 257;
	/** The length of the magic field in a header buffer. */
	public static final int MAGICLEN = 6;
	/**
	 * The magix string used in the last four bytes of the header to identify the
	 * xstar format.
	 */
	public static final String MAGIC_XSTAR = "tar\0";
	/** The magic tag representing an Ant tar archive. */
	public static final String MAGIC_ANT = "ustar\0";
	/** The "version" representing an Ant tar archive. */
	public static final String VERSION_ANT = "\0\0";

	/** The length of the checksum field in a header buffer. */
	public static final int CHKSUMLEN = 8;
	/**
	 * The maximum value of gid/uid in a tar archive which can be expressed in octal
	 * char notation (that's 7 sevens, octal).
	 */
	public static final long MAXID = 07777777L;
	/** The sum of the length of all sparse headers in a sparse header buffer. */
	public static final int SPARSELEN_GNU_SPARSE = 504;

	/**
	 * The magic tag representing a POSIX tar archive.
	 */
	public static final String MAGIC_POSIX = "ustar\0";
	public static final String VERSION_POSIX = "00";
	/**
	 * LF_ constants represent the "link flag" of an entry, or more commonly, the
	 * "entry type". This is the "old way" of indicating a normal file.
	 */
	public static final byte LF_OLDNORM = 0;

	/** Normal file type. */
	public static final byte LF_NORMAL = (byte) '0';
	/** Link file type. */
	public static final byte LF_LINK = (byte) '1';
	/** Symbolic link file type. */
	public static final byte LF_SYMLINK = (byte) '2';

	/** Character device file type. */
	public static final byte LF_CHR = (byte) '3';
	/** Block device file type. */
	public static final byte LF_BLK = (byte) '4';

	/** Directory file type. */
	public static final byte LF_DIR = (byte) '5';
	/** FIFO (pipe) file type. */
	public static final byte LF_FIFO = (byte) '6';
	/** The length of the name field in a header buffer. */
	public static final int NAMELEN = 100;
	/** The length of the mode field in a header buffer. */
	public static final int MODELEN = 8;
	/** The length of the access time field in an old GNU header buffer. */
	public static final int ATIMELEN_GNU = 12;
	/** The length of the created time field in an old GNU header buffer. */
	public static final int CTIMELEN_GNU = 12;
	/**
	 * The length of the multivolume start offset field in an old GNU header buffer.
	 */
	public static final int OFFSETLEN_GNU = 12;
	/** The length of the long names field in an old GNU header buffer. */
	public static final int LONGNAMESLEN_GNU = 4;
	/** The length of the padding field in an old GNU header buffer. */
	public static final int PAD2LEN_GNU = 1;
	/** The sum of the length of all sparse headers in an old GNU header buffer. */
	public static final int SPARSELEN_GNU = 96;
	/**
	 * The length of each of the device fields (major and minor) in a header buffer.
	 */
	public static final int DEVLEN = 8;
	/** Identifies the entry as a Pax extended header. */
	public static final byte LF_PAX_EXTENDED_HEADER_LC = (byte) 'x';
	/** Identifies the entry as a Pax extended header (SunOS tar -E). */
	public static final byte LF_PAX_EXTENDED_HEADER_UC = (byte) 'X';
	/** Identifies the entry as a Pax global extended header. */
	public static final byte LF_PAX_GLOBAL_EXTENDED_HEADER = (byte) 'g';
	/** Identifies the *next* file on the tape as having a long linkname. */
	public static final byte LF_GNUTYPE_LONGLINK = (byte) 'K';
	/** Identifies the *next* file on the tape as having a long name. */
	public static final byte LF_GNUTYPE_LONGNAME = (byte) 'L';
	public static final byte LF_GNUTYPE_SPARSE = (byte) 'S';

	public static final String MAGIC_GNU = "ustar ";
	public static final String VERSION_GNU_SPACE = " \0";
	public static final String VERSION_GNU_ZERO = "0\0";

	/** The length of the is extension field in an old GNU header buffer. */
	public static final int ISEXTENDEDLEN_GNU = 1;
	/** The length of the real size field in an old GNU header buffer. */
	public static final int REALSIZELEN_GNU = 12;
	/** The length of the modification time field in a header buffer. */
	public static final int MODTIMELEN = 12;
	/** Length of the prefix field. */
	public static final int PREFIXLEN = 155;

	private static final int BYTE_MASK = 255;

	static final NioZipEncoding DEFAULT_ENCODING = TarUtils.getZipEncoding(null);

	/**
	 * Encapsulates the algorithms used up to Commons Compress 1.3 as ZipEncoding.
	 */
	static final NioZipEncoding FALLBACK_ENCODING = new NioZipEncoding();

	/**
	 * Parse an octal string from a buffer.
	 *
	 * <p>
	 * Leading spaces are ignored. The buffer must contain a trailing space or NUL,
	 * and may contain an additional trailing space or NUL.
	 * </p>
	 *
	 * <p>
	 * The input buffer is allowed to contain all NULs, in which case the method
	 * returns 0L (this allows for missing fields).
	 * </p>
	 *
	 * <p>
	 * To work-around some tar implementations that insert a leading NUL this method
	 * returns 0 if it detects a leading NUL since Commons Compress 1.4.
	 * </p>
	 *
	 * @param buffer The buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The maximum number of bytes to parse - must be at least 2
	 *               bytes.
	 * @return The long value of the octal string.
	 * @throws SimpleException if the trailing space/NUL is missing or if a
	 *                                  invalid byte is detected.
	 */
	public static long parseOctal(byte[] buffer, int offset, int length) {
		long result = 0;
		int end = offset + length;
		int start = offset;

		if (length < 2 || buffer == null || buffer.length < start) {
			throw new SimpleException("Length " + length + " must be at least 2");
		}

		if (buffer[start] == 0) {
			return 0L;
		}

		// Skip leading spaces
		while (start < end) {
			if (buffer[start] == ' ') {
				start++;
			} else {
				break;
			}
		}

		// Trim all trailing NULs and spaces.
		// The ustar and POSIX tar specs require a trailing NUL or
		// space but some implementations use the extra digit for big
		// sizes/uids/gids ...
		byte trailer = buffer[end - 1];
		while (start < end && (trailer == 0 || trailer == ' ')) {
			end--;
			trailer = buffer[end - 1];
		}

		for (; start < end; start++) {
			final byte currentByte = buffer[start];
			// CheckStyle:MagicNumber OFF
			if (currentByte < '0' || currentByte > '7') {

//				private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
				String string = new String(buffer, offset, length);
				string = string.replaceAll("\0", "{NUL}"); // Replace NULs to allow string to be printed
				String msg =  "Invalid byte " + currentByte + " at offset " + (start - offset) + " in '" + string + "' len="
						+ length;
				throw new SimpleException(msg);
			}
			result = (result << 3) + (currentByte - '0'); // convert from ASCII
			// CheckStyle:MagicNumber ON
		}

		return result;
	}

	/**
	 * Compute the value contained in a byte buffer. If the most significant bit of
	 * the first byte in the buffer is set, this bit is ignored and the rest of the
	 * buffer is interpreted as a binary number. Otherwise, the buffer is
	 * interpreted as an octal number as per the parseOctal function above.
	 *
	 * @param buffer The buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The maximum number of bytes to parse.
	 * @return The long value of the octal or binary string.
	 * @throws SimpleException if the trailing space/NUL is missing or an
	 *                                  invalid byte is detected in an octal number,
	 *                                  or if a binary number would exceed the size
	 *                                  of a signed long 64-bit integer.
	 * @since 1.4
	 */
	public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
		if(buffer == null || buffer.length<offset) {
			return -1;
		}
		if ((buffer[offset] & 0x80) == 0) {
			return parseOctal(buffer, offset, length);
		}
		final boolean negative = buffer[offset] == (byte) 0xff;
		if (length < 9) {
			return parseBinaryLong(buffer, offset, length, negative);
		}
		return parseBinaryBigInteger(buffer, offset, length, negative);
	}

	private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
		if (length >= 9) {
			throw new SimpleException("At offset " + offset + ", " + length + " byte binary number"
					+ " exceeds maximum signed long" + " value");
		}
		long val = 0;
		for (int i = 1; i < length; i++) {
			val = (val << 8) + (buffer[offset + i] & 0xff);
		}
		if (negative) {
			// 2's complement
			val--;
			val ^= (long) Math.pow(2.0, (length - 1) * 8.0) - 1;
		}
		return negative ? -val : val;
	}

	private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
		final byte[] remainder = new byte[length - 1];
		System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
		BigInteger val = new BigInteger(remainder);
		if (negative) {
			// 2's complement
			val = val.add(BigInteger.valueOf(-1)).not();
		}
		if (val.bitLength() > 63) {
			throw new SimpleException("At offset " + offset + ", " + length + " byte binary number"
					+ " exceeds maximum signed long" + " value");
		}
		return negative ? -val.longValue() : val.longValue();
	}

	/**
	 * Parse a boolean byte from a buffer. Leading spaces and NUL are ignored. The
	 * buffer may contain trailing spaces or NULs.
	 *
	 * @param buffer The buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @return The boolean value of the bytes.
	 */
	public static boolean parseBoolean(byte[] buffer, int offset) {
		return buffer[offset] == 1;
	}


	/**
	 * Parse an entry name from a buffer. Parsing stops when a NUL is found or the
	 * buffer length is reached.
	 *
	 * @param buffer The buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The maximum number of bytes to parse.
	 * @return The entry name.
	 */
	public static String parseName(byte[] buffer, int offset, int length) {
		String result = parseName(buffer, offset, length, DEFAULT_ENCODING);
		if(result != null) {
			return result;
		}
		return parseName(buffer, offset, length, FALLBACK_ENCODING);
	}

	/**
	 * Parse an entry name from a buffer. Parsing stops when a NUL is found or the
	 * buffer length is reached.
	 *
	 * @param buffer   The buffer from which to parse.
	 * @param offset   The offset into the buffer from which to parse.
	 * @param length   The maximum number of bytes to parse.
	 * @param encoding name of the encoding to use for file names
	 * @return The entry name.
	 */
	public static String parseName(byte[] buffer, int offset, int length, NioZipEncoding encoding)  {
		if(buffer == null) {
			return null;
		}
		int len = 0;
		for (int i = offset; len < length && buffer[i] != 0; i++) {
			len++;
		}
		if (len > 0) {
			final byte[] b = new byte[len];
			System.arraycopy(buffer, offset, b, 0, len);
			return encoding.decode(b);
		}
		return "";
	}

	/**
	 * Copy a name into a buffer. Copies characters from the name into the buffer
	 * starting at the specified offset. If the buffer is longer than the name, the
	 * buffer is filled with trailing NULs. If the name is longer than the buffer,
	 * the output is truncated.
	 *
	 * @param name   The header name from which to copy the characters.
	 * @param buf    The buffer where the name is to be stored.
	 * @param offset The starting offset into the buffer
	 * @param length The maximum number of header bytes to copy.
	 * @return The updated offset, i.e. offset + length
	 */
	public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
		return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
	}

	/**
	 * Copy a name into a buffer. Copies characters from the name into the buffer
	 * starting at the specified offset. If the buffer is longer than the name, the
	 * buffer is filled with trailing NULs. If the name is longer than the buffer,
	 * the output is truncated.
	 *
	 * @param name     The header name from which to copy the characters.
	 * @param buf      The buffer where the name is to be stored.
	 * @param offset   The starting offset into the buffer
	 * @param length   The maximum number of header bytes to copy.
	 * @param encoding name of the encoding to use for file names
	 * @return The updated offset, i.e. offset + length
	 */
	public static int formatNameBytes(String name, byte[] buf, int offset, int length, NioZipEncoding encoding) {
		if(buf == null) {
			return -1;
		}
		int len = name.length();
		ByteBuffer b = encoding.encode(name);
		while (b.limit() > length && len > 0) {
			b = encoding.encode(name.substring(0, --len));
		}
		final int limit = b.limit() - b.position();
		System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);

		// Pad any remaining output bytes with NUL
		for (int i = limit; i < length; ++i) {
			buf[offset + i] = 0;
		}

		return offset + length;
	}

	/**
	 * Fill buffer with unsigned octal number, padded with leading zeroes.
	 *
	 * @param value  number to convert to octal - treated as unsigned
	 * @param buffer destination buffer
	 * @param offset starting offset in buffer
	 * @param length length of buffer to fill
	 * @return If Parameter valid 
	 */
	public static boolean formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
		int remaining = length;
		remaining--;
		if(buffer == null || buffer.length<offset) {
			return false;
		}
		if (value == 0) {
			buffer[offset + remaining--] = (byte) '0';
		} else {
			long val = value;
			for (; remaining >= 0 && val != 0; --remaining) {
				// CheckStyle:MagicNumber OFF
				buffer[offset + remaining] = (byte) ((byte) '0' + (byte) (val & 7));
				val = val >>> 3;
				// CheckStyle:MagicNumber ON
			}
			if (val != 0) {
				return false;
			}
		}

		for (; remaining >= 0; --remaining) { // leading zeros
			buffer[offset + remaining] = (byte) '0';
		}
		return true;
	}

	/**
	 * Write an octal integer into a buffer.
	 *
	 * Uses {@link #formatUnsignedOctalString} to format the value as an octal
	 * string with leading zeros. The converted number is followed by space and NUL
	 *
	 * @param value  The value to write
	 * @param buf    The buffer to receive the output
	 * @param offset The starting offset into the buffer
	 * @param length The size of the output buffer
	 * @return The updated offset, i.e offset+length
	 */
	public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {

		int idx = length - 2; // For space and trailing null
		formatUnsignedOctalString(value, buf, offset, idx);

		buf[offset + idx++] = (byte) ' '; // Trailing space
		buf[offset + idx] = 0; // Trailing null

		return offset + length;
	}

	/**
	 * Write an octal long integer into a buffer.
	 *
	 * Uses {@link #formatUnsignedOctalString} to format the value as an octal
	 * string with leading zeros. The converted number is followed by a space.
	 *
	 * @param value  The value to write as octal
	 * @param buf    The destinationbuffer.
	 * @param offset The starting offset into the buffer.
	 * @param length The length of the buffer
	 * @return The updated offset
	 */
	public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
		if(buf == null || buf.length<offset) {
			return -1;
		}
		final int idx = length - 1; // For space

		formatUnsignedOctalString(value, buf, offset, idx);
		buf[offset + idx] = (byte) ' '; // Trailing space

		return offset + length;
	}

	/**
	 * Write an long integer into a buffer as an octal string if this will fit, or
	 * as a binary number otherwise.
	 *
	 * Uses {@link #formatUnsignedOctalString} to format the value as an octal
	 * string with leading zeros. The converted number is followed by a space.
	 *
	 * @param value  The value to write into the buffer.
	 * @param buf    The destination buffer.
	 * @param offset The starting offset into the buffer.
	 * @param length The length of the buffer.
	 * @return The updated offset.
	 */
	public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {

		// Check whether we are dealing with UID/GID or SIZE field
		final long maxAsOctalChar = length == UIDLEN ? MAXID : MAXSIZE;

		final boolean negative = value < 0;
		if (!negative && value <= maxAsOctalChar) { // OK to store as octal chars
			return formatLongOctalBytes(value, buf, offset, length);
		}

		if (length < 9) {
			formatLongBinary(value, buf, offset, length, negative);
		} else {
			formatBigIntegerBinary(value, buf, offset, length, negative);
		}

		buf[offset] = (byte) (negative ? 0xff : 0x80);
		return offset + length;
	}

	private static boolean formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
		final int bits = (length - 1) * 8;
		final long max = 1L << bits;
		long val = Math.abs(value); // Long.MIN_VALUE stays Long.MIN_VALUE
		if (val < 0 || val >= max) {
			return false;
		}
		if (negative) {
			val ^= max - 1;
			val++;
			val |= 0xffL << bits;
		}
		for (int i = offset + length - 1; i >= offset; i--) {
			buf[i] = (byte) val;
			val >>= 8;
		}
		return true;
	}

	private static boolean formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
		final BigInteger val = BigInteger.valueOf(value);
		final byte[] b = val.toByteArray();
		final int len = b.length;
		if (len > length - 1) {
			return false;
		}
		final int off = offset + length - len;
		System.arraycopy(b, 0, buf, off, len);
		final byte fill = (byte) (negative ? 0xff : 0);
		for (int i = offset + 1; i < off; i++) {
			buf[i] = fill;
		}
		return true;
	}

	/**
	 * Writes an octal value into a buffer.
	 *
	 * Uses {@link #formatUnsignedOctalString} to format the value as an octal
	 * string with leading zeros. The converted number is followed by NUL and then
	 * space.
	 *
	 * @param value  The value to convert
	 * @param buf    The destination buffer
	 * @param offset The starting offset into the buffer.
	 * @param length The size of the buffer.
	 * @return The updated value of offset, i.e. offset+length
	 */
	public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {

		int idx = length - 2; // for NUL and space
		formatUnsignedOctalString(value, buf, offset, idx);

		buf[offset + idx++] = 0; // Trailing null
		buf[offset + idx] = (byte) ' '; // Trailing space

		return offset + length;
	}

	/**
	 * Compute the checksum of a tar entry header.
	 *
	 * @param buf The tar entry's header buffer.
	 * @return The computed checksum.
	 */
	public static long computeCheckSum(byte[] buf) {
		long sum = 0;

		for (final byte element : buf) {
			sum += BYTE_MASK & element;
		}

		return sum;
	}

	/**
	 * Wikipedia <a href=
	 * "https://en.wikipedia.org/wiki/Tar_(file_format)#File_header">says</a>:
	 * <blockquote> The checksum is calculated by taking the sum of the unsigned
	 * byte values of the header block with the eight checksum bytes taken to be
	 * ascii spaces (decimal value 32). It is stored as a six digit octal number
	 * with leading zeroes followed by a NUL and then a space. Various
	 * implementations do not adhere to this format. For better compatibility,
	 * ignore leading and trailing whitespace, and get the first six digits. In
	 * addition, some historic tar implementations treated bytes as signed.
	 * Implementations typically calculate the checksum both ways, and treat it as
	 * good if either the signed or unsigned sum matches the included checksum.
	 * </blockquote>
	 * <p>
	 * The return value of this method should be treated as a best-effort heuristic
	 * rather than an absolute and final truth. The checksum verification logic may
	 * well evolve over time as more special cases are encountered.
	 *
	 * @param header tar header
	 * @return whether the checksum is reasonably good
	 * @see <a href=
	 *      "https://issues.apache.org/jira/browse/COMPRESS-191">COMPRESS-191</a>
	 * @since 1.5
	 */
	public static boolean verifyCheckSum(byte[] header) {
		final long storedSum = parseOctal(header, CHKSUM_OFFSET, CHKSUMLEN);
		long unsignedSum = 0;
		long signedSum = 0;

		for (int i = 0; i < header.length; i++) {
			byte b = header[i];
			if (CHKSUM_OFFSET <= i && i < CHKSUM_OFFSET + CHKSUMLEN) {
				b = ' ';
			}
			unsignedSum += 0xff & b;
			signedSum += b;
		}
		return storedSum == unsignedSum || storedSum == signedSum;
	}

	/**
	 * Check if buffer contents matches Ascii String.
	 *
	 * @param expected expected string
	 * @param buffer   the buffer
	 * @param offset   offset to read from
	 * @param length   length of the buffer
	 * @return if buffer is the same as the expected string
	 */
	public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
		byte[] buffer1;
		try {
			buffer1 = expected.getBytes(Charset.forName("US-ASCII"));
		} catch (final Exception e) {
			return false;
		}
		return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
	}

	/**
	 * Compare byte buffers, optionally ignoring trailing nulls
	 *
	 * @param buffer1             first buffer
	 * @param offset1             first offset
	 * @param length1             first length
	 * @param buffer2             second buffer
	 * @param offset2             second offset
	 * @param length2             second length
	 * @param ignoreTrailingNulls whether to ignore trailing nulls
	 * @return if buffer1 and buffer2 have same contents, having regard to trailing
	 *         nulls
	 */
	public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2,
			boolean ignoreTrailingNulls) {
		final int minLen = length1 < length2 ? length1 : length2;
		for (int i = 0; i < minLen; i++) {
			if (buffer1[offset1 + i] != buffer2[offset2 + i]) {
				return false;
			}
		}
		if (length1 == length2) {
			return true;
		}
		if (ignoreTrailingNulls) {
			if (length1 > length2) {
				for (int i = length2; i < length1; i++) {
					if (buffer1[offset1 + i] != 0) {
						return false;
					}
				}
			} else {
				for (int i = length1; i < length2; i++) {
					if (buffer2[offset2 + i] != 0) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the first N bytes of an array are all zero
	 *
	 * @param a    The array to check
	 * @param size The number of characters to check (not the size of the array)
	 * @return true if the first N bytes are zero
	 */
	public static boolean isArrayZero(byte[] a, int size) {
		for (int i = 0; i < size; i++) {
			if (a[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public static ByteBuffer growBufferBy(ByteBuffer buffer, int increment) {
		buffer.limit(buffer.position());
		buffer.rewind();

		final ByteBuffer on = ByteBuffer.allocate(buffer.capacity() + increment);

		on.put(buffer);
		return on;
	}

	/**
	 * Instantiates a zip encoding. An NIO based character set encoder/decoder will
	 * be returned. As a special case, if the character set is UTF-8, the nio
	 * encoder will be configured replace malformed and unmappable characters with
	 * '?'. This matches existing behavior from the older fallback encoder.
	 * <p>
	 * If the requested characer set cannot be found, the platform default will be
	 * used instead.
	 * </p>
	 * 
	 * @param name The name of the zip encoding. Specify {@code null} for the
	 *             platform's default encoding.
	 * @return A zip encoding for the given encoding name.
	 */
	public static NioZipEncoding getZipEncoding(String name) {
		Charset cs = Charset.defaultCharset();
		if (name != null) {
			try {
				cs = Charset.forName(name);
			} catch (Exception e) { // NOSONAR we use the default encoding instead
			}
		}
		boolean useReplacement = isUTF8(cs.name());
		return new NioZipEncoding(cs, useReplacement);
	}

	/**
	 * Returns whether a given encoding is UTF-8. If the given name is null, then
	 * check the platform's default encoding.
	 *
	 * @param charsetName If the given name is null, then check the platform's
	 *                    default encoding.
	 * @return isUTF8
	 */
	static boolean isUTF8(String charsetName) {
		if (charsetName == null) {
			// check platform's default encoding
			charsetName = Charset.defaultCharset().name();
		}
		if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
			return true;
		}
		for (final String alias : StandardCharsets.UTF_8.aliases()) {
			if (alias.equalsIgnoreCase(charsetName)) {
				return true;
			}
		}
		return false;
	}
}
