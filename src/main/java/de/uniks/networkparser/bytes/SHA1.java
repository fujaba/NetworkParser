package de.uniks.networkparser.bytes;

/*
NetworkParser
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
import de.uniks.networkparser.buffer.CharacterBuffer;

public class SHA1 extends Checksum {
	private int H0, H1, H2, H3, H4;

	private int[] w = new int[80];
	private int currentPos;
	private long currentLen;

	public SHA1() {
		reset();
	}

	@Override
	public int getOrder() {
		return 20;
	}

	@Override
	public void reset() {
		H0 = 0x67452301;
		H1 = 0xEFCDAB89;
		H2 = 0x98BADCFE;
		H3 = 0x10325476;
		H4 = 0xC3D2E1F0;

		currentPos = 0;
		currentLen = 0;

		/*
		 * In case of complete paranoia, we should also wipe out the information
		 * contained in the w[] array
		 */
	}

	@Override
	public boolean update(byte[] bytes, int offset, int length) {
		if (bytes == null || bytes.length < offset) {
			return false;
		}
		if (length >= 4) {
			int idx = currentPos >> 2;

			switch (currentPos & 3) {
			case 0:
				w[idx] = (((bytes[offset++] & 0xff) << 24) | ((bytes[offset++] & 0xff) << 16)
						| ((bytes[offset++] & 0xff) << 8) | (bytes[offset++] & 0xff));
				length -= 4;
				currentPos += 4;
				currentLen += 32;
				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}
				break;
			case 1:
				w[idx] = (w[idx] << 24) | (((bytes[offset++] & 0xff) << 16) | ((bytes[offset++] & 0xff) << 8)
						| (bytes[offset++] & 0xff));
				length -= 3;
				currentPos += 3;
				currentLen += 24;
				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}
				break;
			case 2:
				w[idx] = (w[idx] << 16) | (((bytes[offset++] & 0xff) << 8) | (bytes[offset++] & 0xff));
				length -= 2;
				currentPos += 2;
				currentLen += 16;
				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}
				break;
			case 3:
				w[idx] = (w[idx] << 8) | (bytes[offset++] & 0xff);
				length--;
				currentPos++;
				currentLen += 8;
				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}
				break;
			}

			/* Now currentPos is a multiple of 4 - this is the place to be... */

			while (length >= 8) {
				w[currentPos >> 2] = ((bytes[offset++] & 0xff) << 24) | ((bytes[offset++] & 0xff) << 16)
						| ((bytes[offset++] & 0xff) << 8) | (bytes[offset++] & 0xff);
				currentPos += 4;

				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}

				w[currentPos >> 2] = ((bytes[offset++] & 0xff) << 24) | ((bytes[offset++] & 0xff) << 16)
						| ((bytes[offset++] & 0xff) << 8) | (bytes[offset++] & 0xff);

				currentPos += 4;

				if (currentPos == 64) {
					perform();
					currentPos = 0;
				}

				currentLen += 64;
				length -= 8;
			}
		}
		/* Remaining bytes (1-3) */
		while (length > 0) {
			/* Here is room for further improvements */
			int idx = currentPos >> 2;
			w[idx] = (w[idx] << 8) | (bytes[offset++] & 0xff);

			currentLen += 8;
			currentPos++;

			if (currentPos == 64) {
				perform();
				currentPos = 0;
			}
			length--;
		}
		return true;
	}

	@Override
	public boolean update(byte b) {
		int idx = currentPos >> 2;
		w[idx] = (w[idx] << 8) | (b & 0xff);

		currentLen += 8;
		currentPos++;

		if (currentPos == 64) {
			perform();
			currentPos = 0;
		}
		return true;
	}

	private void putInt(byte[] b, int pos, int val) {
		if (b == null || pos > b.length - 3) {
			return;
		}
		b[pos] = (byte) (val >> 24);
		b[pos + 1] = (byte) (val >> 16);
		b[pos + 2] = (byte) (val >> 8);
		b[pos + 3] = (byte) val;
	}

	@Override
	public byte[] getByteArray() {
		byte[] out = new byte[getOrder()];
		int off = 0;
		/* Pad with a '1' and 7-31 zero bits... */

		int idx = currentPos >> 2;
		w[idx] = ((w[idx] << 8) | (0x80)) << ((3 - (currentPos & 3)) << 3);

		currentPos = (currentPos & ~3) + 4;

		if (currentPos == 64) {
			currentPos = 0;
			perform();
		} else if (currentPos == 60) {
			currentPos = 0;
			w[15] = 0;
			perform();
		}
		/*
		 * Now currentPos is a multiple of 4 and we can do the remaining padding much
		 * more efficiently, furthermore we are sure that currentPos <= 56.
		 */
		for (int i = currentPos >> 2; i < 14; i++) {
			w[i] = 0;
		}
		w[14] = (int) (currentLen >> 32);
		w[15] = (int) currentLen;

		perform();

		putInt(out, off, H0);
		putInt(out, off + 4, H1);
		putInt(out, off + 8, H2);
		putInt(out, off + 12, H3);
		putInt(out, off + 16, H4);

		reset();
		return out;
	}

	/* Constants for each round */
	private final static int round1_kt = 0x5a827999;
	private final static int round2_kt = 0x6ed9eba1;
	private final static int round3_kt = 0x8f1bbcdc;
	private final static int round4_kt = 0xca62c1d6;

	private void perform() {
		for (int t = 16; t < 80; t++) {
			int x = w[t - 3] ^ w[t - 8] ^ w[t - 14] ^ w[t - 16];
			w[t] = ((x << 1) | (x >>> 31));
		}
		/* The first 16 ints have the byte stream, compute the rest of the buffer */
		for (int t = 16; t <= 79; t++) {
			int temp = w[t - 3] ^ w[t - 8] ^ w[t - 14] ^ w[t - 16];
			w[t] = (temp << 1) | (temp >>> 31);
		}

		int a = H0;
		int b = H1;
		int c = H2;
		int d = H3;
		int e = H4;

		/* Round 1 */
		for (int i = 0; i < 20; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + ((b & c) | ((~b) & d)) + e + w[i] + round1_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		/* Round 2 */
		for (int i = 20; i < 40; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + (b ^ c ^ d) + e + w[i] + round2_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		/* Round 3 */
		for (int i = 40; i < 60; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + ((b & c) | (b & d) | (c & d)) + e + w[i] + round3_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		/* Round 4 */
		for (int i = 60; i < 80; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + (b ^ c ^ d) + e + w[i] + round4_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}
		H0 += a;
		H1 += b;
		H2 += c;
		H3 += d;
		H4 += e;
	}

	public static CharacterBuffer value(CharSequence value) {
		SHA1 sha1 = new SHA1();
		sha1.update(toByteArray(value));
		return convertToHex(sha1.getByteArray());
	}

	public static CharacterBuffer convertToHex(byte[] data) {
		if (data == null) {
			return null;
		}
		CharacterBuffer buf = new CharacterBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.with((char) ('0' + halfbyte));
				else
					buf.with((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf;
	}

	public static byte[] toByteArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charSequence.charAt(i);
		}
		return barr;
	}
}
