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

public class BitValue {
	private byte start;
	private byte len;
	private int orientation = 1;

	public BitValue(int start, int len) {
		this.start = (byte)start;
		this.len = (byte)len;
	}
	public BitValue(byte start, byte len) {
		this.start = start;
		this.len = len;
	}
	public byte getStart() {
		return start;
	}
	public BitValue withStart(byte start) {
		this.start = start;
		return this;
	}
	public byte getLen() {
		return len;
	}
	public BitValue withLen(byte len) {
		this.len = len;
		return this;
	}
	public BitValue withOrientation(int value) {
		this.orientation = value;
		return this;
	}

	public int getOrientation() {
		return orientation;
	}
}
