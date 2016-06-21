package de.uniks.networkparser.parser;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class JavaFile {
	public static final char EOF = Character.MIN_VALUE;
	public static final char COMMENT_START = 'c';
	public static final char LONG_COMMENT_END = 'd';
	public static char NEW_LINE = '\n';
	private CharSequence content;
	private Token lookAheadToken = new Token();
	private Token previousToken = new Token();
	private Token currentToken = new Token();
	private char currentChar;
	private char lookAheadChar;
	private int index;
	private int lookAheadIndex=-1;
	private int endPos;
	private int parsePos;
	private SymTabEntry symTabEntry;
	private SimpleKeyValueList<String, SimpleList<SymTabEntry>> keys=new SimpleKeyValueList<String, SimpleList<SymTabEntry>>();

	public JavaFile(CharSequence content) {
		this.content = content;
		this.endPos = content.length();

		nextChar();
	    nextChar();

		nextToken();
		nextToken();
	}

	public String currentWord() {
		return currentToken.text.toString();
	}

	public boolean currentKindEquals(char c) {
		return currentToken.kind == c;
	}

	public int getCurrentStart() {
		return currentToken.startPos;
	}

	public int getCurrentEnd() {
		return currentToken.endPos;
	}

	public boolean lookAheadKindEquals(char c) {
		return lookAheadToken.kind == c;
	}
	public boolean currentTokenEquals(String word) {
		return stringEquals(currentWord(), word);
	}

	public static boolean stringEquals(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	public boolean skip(char character) {
		if (currentKindEquals(character)) {
			nextToken();
			return true;
		} else {
			error(""+character);
		}
		return false;
	}

	public boolean skip(String string) {
		if (currentTokenEquals(string)) {
			nextToken();
			return true;
		} else {
			error(string);
		}
		return false;
	}

	public void error(CharSequence info) {
		System.err.println("Parser Error: expected token " + info + " found " + currentWord()
        + " at pos " + currentToken.startPos + " at line "
        + getLineIndexOf(currentToken.startPos, content));
        throw new RuntimeException("parse error");
	}

	private long getLineIndexOf(int startPos, CharSequence fileBody)
	   {
	      long count = 1;
	      CharSequence substring = fileBody.subSequence(0, startPos);
	      for (int index = 0; index < substring.length() - 1; ++index)
	      {
	         final char firstChar = substring.charAt(index);
	         if (firstChar == NEW_LINE)
	            count++;
	      }
	      return count;
	   }

	public void nextToken() {
		Token tmp = previousToken;
		previousToken = currentToken;
		currentToken = lookAheadToken;

		lookAheadToken = tmp;
		lookAheadToken.kind = EOF;
		lookAheadToken.text.delete(0, lookAheadToken.text.length());

		char state = 'i';

		while (true) {
			switch (state) {
			case 'i':
				if (Character.isLetter(currentChar) || (currentChar == '_')) {
					state = 'v';
					lookAheadToken.kind = 'v';
					lookAheadToken.text.append(currentChar);
					lookAheadToken.startPos = index;
				} else if (currentChar == EOF) {
					lookAheadToken.kind = EOF;
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					return;
				} else if (Character.isDigit(currentChar)) {
					state = '9';
					lookAheadToken.kind = '9';
					lookAheadToken.text.append(currentChar);
					// lookAheadToken.value = currentChar - '0';
					lookAheadToken.startPos = index;
				} else if (currentChar == '/' && (lookAheadChar == '*' || lookAheadChar == '/')) {
					// start of comment
					lookAheadToken.kind = COMMENT_START;
					lookAheadToken.startPos = index;
					lookAheadToken.text.append(currentChar);
					nextChar();
					lookAheadToken.text.append(currentChar);
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '*' && lookAheadChar == '/') {
					// start of comment
					lookAheadToken.kind = LONG_COMMENT_END;
					lookAheadToken.startPos = index;
					lookAheadToken.text.append(currentChar);
					nextChar();
					lookAheadToken.text.append(currentChar);
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if ("+-*/\\\"'~=()><{}!.,@[]&|?;:#".indexOf(currentChar) >= 0) {
					lookAheadToken.kind = currentChar;
					lookAheadToken.text.append(currentChar);
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '\r') {
					lookAheadToken.startPos = index;
					lookAheadToken.text.append(currentChar);
					nextChar();
					lookAheadToken.text.append(currentChar);
					lookAheadToken.kind = NEW_LINE;
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == NEW_LINE) {
					lookAheadToken.kind = NEW_LINE;
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					lookAheadToken.text.append(currentChar);
					nextChar();
					return;
				} else if (Character.isWhitespace(currentChar)) {
				}

				break;

			case '9':
				if (Character.isDigit(currentChar)) {
					lookAheadToken.text.append(currentChar);
//					lookAheadToken.value = lookAheadToken.value * 10 + (currentChar - '0');
				} else if (currentChar == '.') {
					state = '8';
				} else {
					lookAheadToken.endPos = index - 1;
					return;
				}
				break;

			case '8':
				if (!Character.isDigit(currentChar)) {
					lookAheadToken.endPos = index - 1;
					return;
				}
				break;

			case 'v':
				if (Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_') {
					// keep reading
					lookAheadToken.text.append(currentChar);
				} else {
					lookAheadToken.endPos = index - 1;
					return; // <==== sudden death
				}
				break;

			default:
				break;
			}

			nextChar();
		}
	}

	private void nextChar() {
		currentChar = lookAheadChar;
		index = lookAheadIndex;
		lookAheadChar = 0;

		while (lookAheadChar == 0 && lookAheadIndex < endPos - 1) {
			lookAheadIndex++;

			lookAheadChar = content.charAt(lookAheadIndex);
		}
	}

	public SymTabEntry getRoot() {
		return symTabEntry;
	}

	public SymTabEntry startNextSymTab(String type) {
		SymTabEntry nextEntity = new SymTabEntry();
		nextEntity.setType(type);
		if(symTabEntry == null) {
			this.symTabEntry = nextEntity;
		} else {
			this.symTabEntry.setNext(nextEntity);
		}
		this.parsePos = getCurrentEnd() + 1;
		addCurrentToken(nextEntity);
		SimpleList<SymTabEntry> list = keys.get(type);
		if(list == null) {
			list = new SimpleList<SymTabEntry>();
			keys.add(type, list);
		}
		list.add(nextEntity);
		return nextEntity;
	}

	public CharSequence finishParse(SymTabEntry nextEntity) {
		int endPos = getCurrentEnd();
		CharSequence sequence = subString(this.parsePos, endPos);
		nextEntity.add( sequence );
		return sequence;
	}

	public void addCurrentCharacter(char checkCharacter, SymTabEntry nextEntity) {
		if(currentKindEquals(checkCharacter)) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}
	public void addNewLine(SymTabEntry nextEntity) {
		if (currentKindEquals(NEW_LINE)) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}

	public void addCurrentToken(SymTabEntry nextEntity) {
		nextEntity.add(this.currentToken.text.toString());
	}

	public CharSequence subString(int start, int end) {
		return content.subSequence(start, end);
	}
}
