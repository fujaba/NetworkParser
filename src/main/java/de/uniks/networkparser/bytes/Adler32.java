package de.uniks.networkparser.bytes;

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
/**
 * A class that can be used to compute the Adler32 of a data stream. This
 * implementation uses the class java.util.zip.Adler32 from the Java Standard
 * API.
 */

public class Adler32 extends Checksum {
	private static final int BASE = 65521;

	@Override
	public boolean update(int b) {
		super.update(b);
		int s1 = (int) value & 0xffff;
		int s2 = (int) value >>> 16;
		s1 = (s1 + (b & 0xFF)) % BASE;
		s2 = (s1 + s2) % BASE;

		value = (s2 << 16) + s1;
		return true;
	}

	@Override
	public int getOrder() {
		return 32;
	}
}
