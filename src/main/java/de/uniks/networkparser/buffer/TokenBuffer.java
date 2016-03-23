package de.uniks.networkparser.buffer;

public class TokenBuffer extends CharacterBuffer{
//	private boolean isString = true;
	private int startToken = -1;

	public CharSequence getToken(CharSequence defaultText) {
		if(this.startToken < 0) {
			nextClean(false);
			return defaultText;
		}
		CharacterBuffer token = subSequence(startToken, this.position());
		this.startToken = -1;
		nextClean(false);
		return token;
	}
}
