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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteConverterHex;

/**
 * A ZipEncoding, which uses a java.nio {@link java.nio.charset.Charset Charset}
 * to encode names.
 * <p>
 * The methods of this class are reentrant.
 * </p>
 * @author Stefan
 * @Immutable
 */
class NioZipEncoding {

	private final Charset charset;
	private final boolean useReplacement;
    private static final byte[] REPLACEMENT_BYTES = { (byte) '?' };
	/**
	 * Construct an NioZipEncoding using the given charset.
	 * 
	 * @param charset        The character set to use.
	 * @param useReplacement should invalid characters be replaced, or reported.
	 */
	NioZipEncoding(final Charset charset, boolean useReplacement) {
		this.charset = charset;
		this.useReplacement = useReplacement;
	}

	public NioZipEncoding() {
		this.charset = null;
		this.useReplacement = false;
	}

	public Charset getCharset() {
		return charset;
	}

	/**
	 * Encode Stream
	 * 
	 * @param name of Encoding
	 * @return ByteBuffer
	 */
	public ByteBuffer encode(String name) {
		if (name == null) {
			return null;
		}
		if (this.charset == null) {
			final int length = name.length();
			final byte[] buf = new byte[length];

			/* copy until end of input or output is reached. */
			for (int i = 0; i < length; ++i) {
				buf[i] = (byte) name.charAt(i);
			}
			return ByteBuffer.wrap(buf);
		}
		final CharsetEncoder enc = newEncoder();
		final CharBuffer cb = CharBuffer.wrap(name);
		CharacterBuffer tmp = new CharacterBuffer();
		ByteBuffer out = ByteBuffer.allocate(estimateInitialBufferSize(enc, cb.remaining()));

		while (cb.remaining() > 0) {
			final CoderResult res = enc.encode(cb, out, false);

			if (res.isUnmappable() || res.isMalformed()) {
 				/*
				 * write the unmappable characters in utf-16 pseudo-URL encoding style to
				 * ByteBuffer.
				 */

				int spaceForSurrogate = estimateIncrementalEncodingSize(enc, 6 * res.length());
				if (spaceForSurrogate > out.remaining()) {
					/*
					 * if the destination buffer isn't over sized, assume that the presence of one
					 * unmappable character makes it likely that there will be more. Find all the
					 * un-encoded characters and allocate space based on those estimates.
					 */
					int charCount = 0;
					for (int i = cb.position(); i < cb.limit(); i++) {
						charCount += !enc.canEncode(cb.get(i)) ? 6 : 1;
					}
					int totalExtraSpace = estimateIncrementalEncodingSize(enc, charCount);
					out = TarUtils.growBufferBy(out, totalExtraSpace - out.remaining());
				}
				
				for (int i = 0; i < res.length(); ++i) {
					out = encodeFully(enc, encodeSurrogate(tmp, cb.get()), out);
				}

			} else if (res.isOverflow()) {
				int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
				out = TarUtils.growBufferBy(out, increment);
			}
		}
		/* tell the encoder we are done */
		enc.encode(cb, out, true);
		/* may have caused underflow, but that's been ignored traditionally */

		out.limit(out.position());
		out.rewind();
		return out;
	}

	/**
	 * Decode Stream
	 * 
	 * @param data for Decoding
	 * @return decoded String
	 */
	public String decode(byte[] data) {
		if (data == null) {
			return null;
		}
		if (this.charset == null) {
			final int length = data.length;
			final StringBuilder result = new StringBuilder(length);

			for (final byte b : data) {
				if (b == 0) { /* Trailing null */
					break;
				}
				result.append((char) (b & 0xFF)); /* Allow for sign-extension */
			}
			return result.toString();
		}
		try {
			return newDecoder().decode(ByteBuffer.wrap(data)).toString();
		} catch (Exception e) {
		}
		return null;
	}

	private ByteBuffer encodeFully(CharsetEncoder enc, CharacterBuffer cb, ByteBuffer out) {
		ByteBuffer o = out;
		if (cb == null) {
			return o;
		}
		CharBuffer buffer=CharBuffer.wrap(cb.toCharArray());
		while (buffer.hasRemaining()) {
			CoderResult result = enc.encode(buffer, o, false);
			if (result.isOverflow()) {
				int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
				o = TarUtils.growBufferBy(o, increment);
			}
		}
		return o;
	}

	private CharacterBuffer encodeSurrogate(CharacterBuffer buffer, char c) {
	    if(buffer == null) {
	        return null;
	    }
	    buffer.clear(); 
		buffer.with((byte)'%', (byte)'U');
		ByteConverterHex.convert(buffer, (byte)((c >> 12) & 0x0f), (byte)((c >> 8) & 0x0f), (byte)((c >> 4) & 0x0f), (byte) (c & 0x0f));
		return buffer;
	}

	private CharsetEncoder newEncoder() {
		if (charset == null) {
			return null;
		}
		if (useReplacement) {
			return charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_BYTES);
		} else {
			return charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT);
		}
	}

	private CharsetDecoder newDecoder() {
		if (charset == null) {
			return null;
		}
		if (useReplacement == false) {
			return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT);
		} else {
			return charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?");
		}
	}

	/**
	 * Estimate the initial encoded size (in bytes) for a character buffer.
	 * <p>
	 * The estimate assumes that one character consumes uses the maximum length
	 * encoding, whilst the rest use an average size encoding. This accounts for any
	 * BOM for UTF-16, at the expense of a couple of extra bytes for UTF-8 encoded
	 * ASCII.
	 * </p>
	 *
	 * @param enc        encoder to use for estimates
	 * @param charChount number of characters in string
	 * @return estimated size in bytes.
	 */
	private static int estimateInitialBufferSize(CharsetEncoder enc, int charChount) {
		if (enc == null) {
			return -1;
		}
		float first = enc.maxBytesPerChar();
		float rest = (charChount - 1) * enc.averageBytesPerChar();
		return (int) Math.ceil(first + rest);
	}

	/**
	 * Estimate the size needed for remaining characters
	 *
	 * @param enc       encoder to use for estimates
	 * @param charCount number of characters remaining
	 * @return estimated size in bytes.
	 */
	private static int estimateIncrementalEncodingSize(CharsetEncoder enc, int charCount) {
		if (enc == null) {
			return -1;
		}
		return (int) Math.ceil(charCount * enc.averageBytesPerChar());
	}
}
