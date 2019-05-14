package de.uniks.networkparser.converter;

/*
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
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class ByteConverter64 extends ByteConverter {
	// private static final int BYTEPERATOM = 3;
	private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '+', '/', '=' };
	private static final byte PADDING = 127;

	// ENCODE
	@Override
	public String toString(BufferedBuffer values) {
		int i, j, k;
		CharacterBuffer buffer = new CharacterBuffer();
		values.back();
		while (values.isEnd() == false) {
			i = values.getByte();
			j = values.getByte();
			k = values.getByte();
			buffer.with(pem_array[(i >>> 2 & 0x3F)]);
			buffer.with(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			buffer.with(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
			buffer.with(pem_array[(k & 0x3F)]);
		}
		return buffer.toString();
	}

	private int getStaticSize(int size) {
		return ((size + 2) / 3) * 4;
	}

	/**
	 * Base64 encode a byte array. No line breaks are inserted. This method is
	 * suitable for short strings, such as those in the IMAP AUTHENTICATE protocol,
	 * but not to encode the entire content of a MIME part.
	 *
	 * @param values the byte array
	 * @return the encoded byte array
	 **/
	public CharacterBuffer toStaticString(CharSequence values) {
		return toStaticString(values, true);
	}

	
	/**
	 * Base64 encode a byte array. No line breaks are inserted. This method is
	 * suitable for short strings, such as those in the IMAP AUTHENTICATE protocol,
	 * but not to encode the entire content of a MIME part.
	 *
	 * @param values the byte array
	 * @param finishToken finish TOken with =
	 * @return the encoded byte array
	 **/
	public CharacterBuffer toStaticString(CharSequence values, boolean finishToken) {
		if (values.length() == 0) {
			return new CharacterBuffer();
		}
		if (values instanceof CharacterBuffer) {
			return encode((CharacterBuffer) values, 0, values.length(), finishToken);
		}
		CharacterBuffer buffer = new CharacterBuffer();
		buffer.withObjects(values);
		return encode(buffer, 0, buffer.length(), finishToken);
	}

	/**
	 * Base64 encode a byte array. No line breaks are inserted. This method is
	 * suitable for short strings, such as those in the IMAP AUTHENTICATE protocol,
	 * but not to encode the entire content of a MIME part.
	 *
	 * @param values the byte array
 	 * @param finishToken finish TOken with =
	 * @return the encoded byte array
	 */
	public CharacterBuffer toStaticString(byte[] values, boolean finishToken) {
		if (values.length == 0) {
			return new CharacterBuffer();
		}
		ByteBuffer buffer = new ByteBuffer().with(values);
		return encode(buffer, 0, buffer.length(), finishToken);
	}

	/**
	 * Convert a simpleString to Base64
	 * 
	 * @param values Input String
	 * @return a Base64 String
	 */
	public static CharacterBuffer toBase64String(CharSequence values) {
		ByteConverter64 converter = new ByteConverter64();
		return converter.toStaticString(values);
	}

	/**
	 * Convert a simpleString to Base64
	 * 
	 * @param values Input String
	 * @param finishToken boolean for Finish Token
	 * @return a Base64 String
	 */
	public static CharacterBuffer toBase64String(CharSequence values, boolean finishToken) {
		ByteConverter64 converter = new ByteConverter64();
		return converter.toStaticString(values, finishToken);
	}

	/**
	 * Convert a simpleString from Base64
	 * 
	 * @param values Input String(CharSequence) or byte[]
	 * @return a decoded String
	 */
	public static CharacterBuffer fromBase64String(Object values) {
		ByteConverter64 converter = new ByteConverter64();
		byte[] ref;
		if (values instanceof String) {
			ref = converter.decode((String) values);
		} else {
			CharacterBuffer buffer = new CharacterBuffer();
			if (values instanceof byte[]) {
				buffer.with((byte[]) values);
			} else if (values instanceof CharSequence) {
				buffer.with((CharSequence) values);
			}
			ref = converter.decode(buffer.toString());
		}
		return new CharacterBuffer().with(ref);
	}

	/**
	 * Internal use only version of encode. Allow specifying which part of the input
	 * buffer to encode. If outbuf is non-null, it's used as is. Otherwise, a new
	 * output buffer is allocated.
	 * 
	 * @param buffer Buffer for encoding
	 * @param off    offset of String
	 * @param size   size of String
	 * @param finishToken finish String with =
	 * @return encoded String
	 */
	private CharacterBuffer encode(BufferedBuffer buffer, int off, int size, boolean finishToken) {
		int len = getStaticSize(size);
		if(finishToken == false) {
			if(size%3==1) {
				len -= 2;	
			}else if(size%3 == 2) {
				len-=1;
			}
		}
		
		byte[] outbuf = new byte[len];

		int inpos, outpos;
		int val;
		for (inpos = off, outpos = 0; size >= 3; size -= 3, outpos += 4) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			outbuf[outpos + 3] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 2] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 1] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_array[val & 0x3f];
		}
		// done with groups of three, finish up any odd bytes left
		if (size == 1) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 4;
			if(finishToken) {
				outbuf[outpos + 3] = (byte) '='; // pad character;
				outbuf[outpos + 2] = (byte) '='; // pad character;
			}
			outbuf[outpos + 1] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_array[val & 0x3f];
		} else if (size == 2) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			val <<= 2;
			if(finishToken) {
				outbuf[outpos + 3] = (byte) '='; // pad character;
			}
			outbuf[outpos + 2] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 1] = (byte) pem_array[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_array[val & 0x3f];
		}
		return new CharacterBuffer().with(outbuf);
	}

	private static byte[] pem_convert_array = null;

	private void initPEMArray() {
		pem_convert_array = new byte[256];
		for (int i = 0; i < 255; i++) {
			pem_convert_array[i] = -1;
		}
		for (int i = 0; i < pem_array.length; i++) {
			pem_convert_array[pem_array[i]] = ((byte) i);
		}
		pem_convert_array['='] = PADDING;
	}

	@Override
	public byte[] decode(CharSequence value) {
		if (value == null || value instanceof String == false) {
			return new byte[0];
		}
		if (pem_convert_array == null) {
			initPEMArray();
		}
		byte[] bytes = ((String) value).getBytes();
		int i;
		byte[] result = null;
		for (i = bytes.length - 1; i >= bytes.length - 3; i--) {
			byte c = pem_convert_array[value.charAt(i)];
			if (c == PADDING) {
				continue;
			}
			if (c == -1) {
				result = new byte[bytes.length * 3 / 4];
			}
			break;
		}
		i++;
		if (result == null && (value.length() - i) > 2) {
			result = new byte[bytes.length * 3 / 4];
		} else {
			result = new byte[bytes.length * 3 / 4 - bytes.length + i];
		}
		int pos = 0;
		for (i = 0; i < bytes.length - 7; i += 4) {
			int n = pem_convert_array[(bytes[i + 3] & 0xFF)];
			int m = pem_convert_array[(bytes[i + 2] & 0xFF)];
			int k = pem_convert_array[(bytes[i + 1] & 0xFF)];
			int j = pem_convert_array[(bytes[i + 0] & 0xFF)];
			result[pos++] = (byte) (j << 2 & 0xFC | k >>> 4 & 0x3);
			result[pos++] = (byte) (k << 4 & 0xF0 | m >>> 2 & 0xF);
			result[pos++] = (byte) (m << 6 & 0xC0 | n & 0x3F);
		}
		int n = pem_convert_array[(bytes[i + 3] & 0xFF)];
		int m = pem_convert_array[(bytes[i + 2] & 0xFF)];
		int k = pem_convert_array[(bytes[i + 1] & 0xFF)];
		int j = pem_convert_array[(bytes[i + 0] & 0xFF)];
		result[pos++] = (byte) (j << 2 & 0xFC | k >>> 4 & 0x3);
		if (pos < result.length) {
			result[pos++] = (byte) (k << 4 & 0xF0 | m >>> 2 & 0xF);
			if (pos < result.length) {
				result[pos++] = (byte) (m << 6 & 0xC0 | n & 0x3F);
			}
		}
		return result;
	}
}
