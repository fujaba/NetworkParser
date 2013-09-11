package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

public class CharacterBuffer implements Buffer{
	/** The buffer. */
	protected char[] buffer;

	/** The length. */
	private int length;
	
	/** The index. */
	protected int index;
	
	/** The line. */
	protected int line;

	/** The character. */
	protected int character;

	
	public CharacterBuffer(String value){
		this.buffer = value.toCharArray();
		this.length = buffer.length; 
	}

	public int length() {
		return length;
	}
	public char charAt(int index){
		return buffer[index];
	}
	
	@Override
	public byte byteAt(int index) {
		return (byte)buffer[index];
	}

	
	public String substring(int startTag, int length){
		if(startTag+length>buffer.length){
			length = buffer.length - startTag;
		}
		return new String(buffer, startTag, length);
	}

	@Override
	public Buffer withLength(int value) {
		this.length = value;
		return this;
	}

	@Override
	public int position() {
		return index;
	}

	@Override
	public void back() {
		this.index -= 1;
		this.character -= 1;
	}

	@Override
	public boolean isEnd() {
		return length <= this.index;
	}

	@Override
	public int remaining() {
		return length - index;
	}
	
	@Override
	public String toString() {
		return " at " + this.index + " [character " + this.character + " line "
				+ this.line + "]";
	}

	@Override
	public Buffer withPosition(int index) {
		this.index = index;
		return this;
	}

	@Override
	public String toText() {
		return new String(buffer);
	}
	@Override
	public byte[] toArray() {
		byte[] result = new byte[buffer.length];
		for(int i=0;i<buffer.length;i++){
			result[i]=(byte) buffer[i];
		}
		return result;
	}


	@Override
	public char getChar() {
		this.index++;
		if(this.index==this.buffer.length){
			return 0;
		}
		char c = this.buffer[this.index];
		if (c == '\r') {
			this.line += 1;
			if (this.buffer[this.index] == '\n') {
				this.character = 1;
				this.index++;
				c = '\n';
			} else {
				this.character = 0;
			}
		} else if (c == '\n') {
			this.line += 1;
			this.character = 0;
		} else {
			this.character += 1;
		}
		return c;
	}
}
