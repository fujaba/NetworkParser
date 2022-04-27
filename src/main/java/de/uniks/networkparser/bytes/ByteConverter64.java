package de.uniks.networkparser.bytes;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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

/**
 * Converter for converting String in BYTE64.
 *
 * @author Stefan Lindel
 */
public class ByteConverter64 extends ByteConverter {
	/* private static final int BYTEPERATOM = 3; */
	private static final byte PADDING = 127;

    private char[] pem_encode = null;
    private byte[] pem_decode = null;

	/**
	 * To string.
	 *
	 * @param values the values
	 * @return the string
	 */
	/* ENCODE */
	@Override
	public String toString(BufferedBuffer values) {
		if (values == null) {
			return null;
		}
		int i, j, k;
		CharacterBuffer buffer = new CharacterBuffer();
		values.back();
		initEncodePEM();
		while (!values.isEnd()) {
			i = values.getByte();
			j = values.getByte();
			k = values.getByte();
			buffer.with(pem_encode[(i >>> 2 & 0x3F)]);
			buffer.with(pem_encode[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			buffer.with(pem_encode[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
			buffer.with(pem_encode[(k & 0x3F)]);
		}
		return buffer.toString();
	}
	
	private void initEncodePEM() {
	    pem_encode = new char[26+26+10+3];
	    int count = 0;
	    for(byte i='A';i<='Z';i++) {pem_encode[count++] = (char)i;}
	    for(byte i='a';i<='z';i++) {pem_encode[count++] = (char)i;}
	    for(byte i='0';i<='9';i++) {pem_encode[count++] = (char)i;}
	    pem_encode[count++]='+';
	    pem_encode[count++]='/';
	    pem_encode[count++]='=';
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
	 * @param values      the byte array
	 * @param finishToken finish TOken with =
	 * @return the encoded byte array
	 **/
	public CharacterBuffer toStaticString(CharSequence values, boolean finishToken) {
		if (values == null || values.length() == 0) {
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
	 * @param values      the byte array
	 * @param finishToken finish TOken with =
	 * @return the encoded byte array
	 */
	public CharacterBuffer toStaticString(byte[] values, boolean finishToken) {
		if (values == null || values.length == 0) {
			return new CharacterBuffer();
		}
		ByteBuffer buffer = new ByteBuffer().with(values);
		return encode(buffer, 0, buffer.length(), finishToken);
	}

	/**
	 * Convert a simpleString to Base64.
	 *
	 * @param values Input String
	 * @return a Base64 String
	 */
	public static CharacterBuffer toBase64String(CharSequence values) {
		ByteConverter64 converter = new ByteConverter64();
		return converter.toStaticString(values);
	}
	
	/**
     * Convert a simpleString to Base64.
     *
     * @param values Input String
     * @return a Base64 String
     */
    public static CharacterBuffer toBase64String(byte... values) {
        ByteConverter64 converter = new ByteConverter64();
        return converter.toStaticString(values, true);
    }

	/**
	 * Convert a simpleString to Base64.
	 *
	 * @param values      Input String
	 * @param finishToken boolean for Finish Token
	 * @return a Base64 String
	 */
	public static CharacterBuffer toBase64String(CharSequence values, boolean finishToken) {
		ByteConverter64 converter = new ByteConverter64();
		return converter.toStaticString(values, finishToken);
	}

	/**
	 * Convert a simpleString from Base64.
	 *
	 * @param values Input String(CharSequence) or byte[]
	 * @return a decoded String
	 */
	public static CharacterBuffer fromBase64String(Object values) {
		ByteConverter64 converter = new ByteConverter64();
        if (values instanceof String) {
            byte[] ref =converter.decode((String) values);
            return new CharacterBuffer().with(ref, 0, ref.length);
		} 

		CharacterBuffer buffer = new CharacterBuffer();
		if (values instanceof byte[]) {
			buffer.with((byte[]) values, 0, ((byte[]) values).length);
		} else if (values instanceof CharSequence) {
			buffer.with((CharSequence) values);
		}
		byte[] ref =converter.decode(buffer.toString());
		return new CharacterBuffer().with(ref, 0, ref.length);
	}

	/**
	 * Internal use only version of encode. Allow specifying which part of the input
	 * buffer to encode. If outbuf is non-null, it's used as is. Otherwise, a new
	 * output buffer is allocated.
	 * 
	 * @param buffer      Buffer for encoding
	 * @param off         offset of String
	 * @param size        size of String
	 * @param finishToken finish String with =
	 * @return encoded String
	 */
	private CharacterBuffer encode(BufferedBuffer buffer, int off, int size, boolean finishToken) {
		if (buffer == null || size > buffer.size()) {
			return null;
		}
		int len = getStaticSize(size);
		if (!finishToken) {
			if (size % 3 == 1) {
				len -= 2;
			} else if (size % 3 == 2) {
				len -= 1;
			}
		}

		byte[] outbuf = new byte[len];
		initEncodePEM();
		int inpos, outpos;
		int val;
		for (inpos = off, outpos = 0; size >= 3; size -= 3, outpos += 4) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			outbuf[outpos + 3] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 2] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 1] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_encode[val & 0x3f];
		}
		/* done with groups of three, finish up any odd bytes left */
		if (size == 1) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 4;
			if (finishToken) {
				outbuf[outpos + 3] = (byte) '='; /* pad character; */
				outbuf[outpos + 2] = (byte) '='; /* pad character; */
			}
			outbuf[outpos + 1] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_encode[val & 0x3f];
		} else if (size == 2) {
			val = buffer.charAt(inpos++) & 0xff;
			val <<= 8;
			val |= buffer.charAt(inpos++) & 0xff;
			val <<= 2;
			if (finishToken) {
				outbuf[outpos + 3] = (byte) '='; /* pad character; */
			}
			outbuf[outpos + 2] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 1] = (byte) pem_encode[val & 0x3f];
			val >>= 6;
			outbuf[outpos + 0] = (byte) pem_encode[val & 0x3f];
		}
		return new CharacterBuffer().with(outbuf, 0, outbuf.length);
	}

	private void initPEMArray() {
	    if(this.pem_encode == null) {
	        initEncodePEM();
	    }
		pem_decode = new byte[256];
		for (int i = 0; i < 255; i++) {
		    pem_decode[i] = -1;
		}
		for (int i = 0; i < pem_encode.length; i++) {
		    pem_decode[pem_encode[i]] = (byte)i;
		}
		pem_decode['='] = PADDING;
	}

	/**
	 * Decode.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	@Override
	public byte[] decode(CharSequence value) {
		if (value == null || !(value instanceof String)) {
			return new byte[0];
		}
		byte[] bytes = ((String) value).getBytes();
		if (bytes.length < 1) {
			return bytes;
		}
        if (pem_decode == null) {
            initPEMArray();
        }
		int i;
		byte[] result = null;
		for (i = bytes.length - 1; i >= bytes.length - 3; i--) {
			byte c = pem_decode[value.charAt(i)];
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
		int n, m, k, j;
		for (i = 0; i < bytes.length - 7; i += 4) {
			n = pem_decode[(bytes[i + 3] & 0xFF)];
			m = pem_decode[(bytes[i + 2] & 0xFF)];
			k = pem_decode[(bytes[i + 1] & 0xFF)];
			j = pem_decode[(bytes[i + 0] & 0xFF)];
			result[pos++] = (byte) (j << 2 & 0xFC | k >>> 4 & 0x3);
			result[pos++] = (byte) (k << 4 & 0xF0 | m >>> 2 & 0xF);
			result[pos++] = (byte) (m << 6 & 0xC0 | n & 0x3F);
		}
		n = pem_decode[(bytes[i + 3] & 0xFF)];
		m = pem_decode[(bytes[i + 2] & 0xFF)];
		k = pem_decode[(bytes[i + 1] & 0xFF)];
		j = pem_decode[(bytes[i + 0] & 0xFF)];
		result[pos++] = (byte) (j << 2 & 0xFC | k >>> 4 & 0x3);
		if (pos < result.length) {
			result[pos++] = (byte) (k << 4 & 0xF0 | m >>> 2 & 0xF);
			if (pos < result.length) {
				result[pos++] = (byte) (m << 6 & 0xC0 | n & 0x3F);
				if (pos < result.length) {
					j = pem_decode[(bytes[i + 4] & 0xFF)];
					k = pem_decode[(bytes[i + 5] & 0xFF)];
					result[pos++] = (byte) (j << 2 & 0xFC | k >>> 4 & 0x3);
				}
			}
		}
		return result;
	}
}
