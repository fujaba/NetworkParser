package de.uniks.networkparser;

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
import de.uniks.networkparser.buffer.CharacterBuffer;

public class Pos {
	public int x = -1;
	public int y = -1;

	public static Pos valueOf(String tag) {
		Pos pos = new Pos();
		if (tag == null || tag.length() < 1) {
			return pos;
		}
		int rowPos = 1;
		if (tag.charAt(0) >= 65 && tag.charAt(0) <= 90) {
			pos.x = tag.charAt(0) - 65;
		}
		if (tag.charAt(1) >= 65 && tag.charAt(1) <= 90) {
			pos.x = pos.x * 26 + tag.charAt(0) - 65;
			rowPos = 2;
		}
		if (rowPos < tag.length()) {
			pos.y = Integer.valueOf(tag.substring(rowPos));
		}
		return pos;
	}

	@Override
	public String toString() {
		return toTag().toString();
	}

	public CharacterBuffer toTag() {
		CharacterBuffer buffer = new CharacterBuffer();
		int pos = x;
		while (pos > 26) {
			int no = pos / 26;
			buffer.with((char) (65 + no));
			pos -= no * 26;
		}
		buffer.with((char) (65 + pos));
		if (y > 0) {
			buffer.with("" + y);
		}
		return buffer;
	}

	public static Pos create(int x, int y) {
		Pos pos = new Pos();
		pos.x = x;
		pos.y = y;
		return pos;
	}
}
