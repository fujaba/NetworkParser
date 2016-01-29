package de.uniks.networkparser.gui;

import de.uniks.networkparser.buffer.CharacterBuffer;

public class Pos {
	public int x = -1;
	public int y = -1;

	public static Pos valueOf(String tag) {
		Pos pos = new Pos();
		if(tag== null || tag.length() < 1) {
			return pos;
		}
		int rowPos=1;
		if(tag.charAt(0)>=65 && tag.charAt(0) <= 90) {
			pos.x = tag.charAt(0)-65;
		}
		if(tag.charAt(1)>=65 && tag.charAt(1) <= 90) {
			pos.x = pos.x*26+tag.charAt(0)-65;
			rowPos=2;
		}
		if(rowPos<tag.length()) {
			pos.y = Integer.valueOf(tag.substring(rowPos));
		}
		return pos;
	}

	@Override
	public String toString() {
		return toTag().toString();
	}

	public CharacterBuffer toTag() {
		CharacterBuffer buffer=new CharacterBuffer();
		int pos=x;
		while(pos>26) {
			int no = pos/26;
			buffer.with((char)(65+no));
			pos -= no*26;
		}
		buffer.with((char)(65+pos));
		buffer.with(""+y);
		return buffer;
	}

	public static Pos create(int x, int y) {
		Pos pos = new Pos();
		pos.x = x;
		pos.y = y;
		return pos;
	}
}
