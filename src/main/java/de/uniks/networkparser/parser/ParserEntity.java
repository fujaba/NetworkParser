package de.uniks.networkparser.parser;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.list.SimpleList;

public class ParserEntity {

	private JavaFile file;
	public Token lookAheadToken = new Token();
	public Token previousToken = new Token();
	public Token currentToken = new Token();
	public char currentChar;
	public char lookAheadChar;
	public int index;
	public int lookAheadIndex = -1;
	public int parsePos;
	public SymTabEntry symTabEntry;

	public ParserEntity(JavaFile file) {
		this.file = file;

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
			error("" + character);
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
		System.err.println("Parser Error: expected token " + info + " found " + currentWord() + " at pos "
				+ currentToken.startPos + " at line " + getLineIndexOf(currentToken.startPos, file.getContent()));
		throw new RuntimeException("parse error");
	}

	public void nextToken() {
		Token tmp = previousToken;
		previousToken = currentToken;
		currentToken = lookAheadToken;

		lookAheadToken = tmp;
		lookAheadToken.kind = JavaFile.EOF;
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
				} else if (currentChar == JavaFile.EOF) {
					lookAheadToken.kind = JavaFile.EOF;
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
					lookAheadToken.kind = JavaFile.COMMENT_START;
					lookAheadToken.startPos = index;
					lookAheadToken.text.append(currentChar);
					nextChar();
					lookAheadToken.text.append(currentChar);
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '*' && lookAheadChar == '/') {
					// start of comment
					lookAheadToken.kind = JavaFile.LONG_COMMENT_END;
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
					lookAheadToken.kind = JavaFile.NEW_LINE;
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == JavaFile.NEW_LINE) {
					lookAheadToken.kind = JavaFile.NEW_LINE;
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
					// lookAheadToken.value = lookAheadToken.value * 10 + (currentChar - '0');
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

		while (lookAheadChar == 0 && lookAheadIndex < file.getSize() - 1) {
			lookAheadIndex++;
			lookAheadChar = file.getContent().charAt(lookAheadIndex);
		}
	}

	public SymTabEntry getRoot() {
		return symTabEntry;
	}

	public SymTabEntry startNextSymTab(String type) {
		SymTabEntry nextEntity = new SymTabEntry();
		nextEntity.setType(type);
		if (symTabEntry == null) {
			this.symTabEntry = nextEntity;
		} else {
			this.symTabEntry.setNext(nextEntity);
		}
		this.parsePos = getCurrentEnd() + 1;
		addCurrentToken(nextEntity);
		SimpleList<SymTabEntry> list = file.getSymbolEntries(type);
		list.add(nextEntity);
		return nextEntity;
	}

	public CharSequence finishParse(SymTabEntry nextEntity) {
		int endPos = getCurrentEnd();
		CharSequence sequence = file.subString(this.parsePos, endPos);
		nextEntity.add(sequence);
		return sequence;
	}

	public void addCurrentCharacter(char checkCharacter, SymTabEntry nextEntity) {
		if (currentKindEquals(checkCharacter)) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}

	public void addNewLine(SymTabEntry nextEntity) {
		if (currentKindEquals(JavaFile.NEW_LINE)) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}

	public void addCurrentToken(SymTabEntry nextEntity) {
		nextEntity.add(this.currentToken.text.toString());
	}

	private long getLineIndexOf(int startPos, CharSequence fileBody) {
		long count = 1;
		CharSequence substring = fileBody.subSequence(0, startPos);
		for (int index = 0; index < substring.length() - 1; ++index) {
			final char firstChar = substring.charAt(index);
			if (firstChar == JavaFile.NEW_LINE)
				count++;
		}
		return count;
	}
	
	
	public ParserEntity parse() {
		// [packagestat] importlist classlist
		if (currentTokenEquals(SymTabEntry.TYPE_PACKAGE)) {
	         parsePackageDecl();
	    }
		while (currentTokenEquals(SymTabEntry.TYPE_IMPORT)) {
			parseImport();
		}
		parseClassDecl();
		return this;
	}
	
	private void parseImport() {
		// import qualifiedName [. *];
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_IMPORT);
		nextToken();

		String modifier = parseModifiers();
		nextEntity.add(modifier);

		parseQualifiedName(nextEntity);

		if (currentKindEquals('*')) {
			skip('*');
		}
		skip(';');
	}

	private String parseModifiers() {
		// names != class
		StringBuilder result = new StringBuilder();
		while (EntityUtil.isModifier(" " + currentWord() + " ")) {
			result.append(currentWord());
			result.append(" ");
			nextToken();
		}
		return result.toString();
	}

   private void parsePackageDecl()
   {
	   // skip package
	   SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_PACKAGE);
	   parseQualifiedName(nextEntity);
	   addCurrentCharacter(';', nextEntity);
	   addNewLine(nextEntity);
   }
   
	private void parseClassDecl() {
//		int preCommentStartPos = currentRealToken.preCommentStartPos;
//		int preCommentEndPos = currentRealToken.preCommentEndPos;
//
//		// FIXME skip all Annotations
//		int startPosAnnotations = currentRealToken.startPos;
//		while ("@".equals(currentRealWord())) {
//			String annotation = parseAnnotations();
//
//			int endPosAnnotation = currentRealToken.startPos - 1;
//
//			// FIXME please
//			if (annotation != "") {
//				symTab.put(ANNOTATION + ":" + annotation.substring(1),
//						new SymTabEntry().withKind(ANNOTATION).withMemberName(annotation.substring(1))
//								.withEndPos(endPosAnnotation).withStartPos(startPosAnnotations));
//			}
//
//			// nextRealToken();
//		}
//
//		// modifiers class name classbody
//		int startPosClazz = currentRealToken.startPos;
//		classModifier = parseModifiers();
//
//		// skip keyword
//		// skip ("class");
//
//		// class or interface or enum
//		String classTyp = parseClassType();
//		className = currentRealWord();
//		endOfClassName = currentRealToken.endPos;
//
//		symTab.put(classTyp + ":" + className,
//				new SymTabEntry().withStartPos(startPosClazz).withKind(classTyp).withMemberName(className)
//						.withEndPos(endOfClassName))
//				.withAnnotationsStartPos(startPosAnnotations).withPreCommentStartPos(preCommentStartPos)
//				.withPreCommentEndPos(preCommentEndPos);
//
//		// skip name
//		nextRealToken();
//
//		parseGenericTypeSpec();
//
//		// extends
//		if ("extends".equals(currentRealWord())) {
//			int startPos = currentRealToken.startPos;
//
//			skip("extends");
//
//			symTab.put(EXTENDS + ":" + currentRealWord(), new SymTabEntry().withBodyStartPos(currentRealToken.startPos)
//					.withKind(EXTENDS).withMemberName(currentRealWord()).withEndPos(currentRealToken.endPos));
//
//			// skip superclass name
//			parseTypeRef();
//
//			endOfExtendsClause = previousRealToken.endPos;
//
//			checkSearchStringFound(EXTENDS, startPos);
//		}
//
//		// implements
//		if ("implements".equals(currentRealWord())) {
//			int startPos = currentRealToken.startPos;
//
//			skip("implements");
//
//			while (!currentRealKindEquals(EOF) && !currentRealKindEquals('{')) {
//				symTab.put(IMPLEMENTS + ":" + currentRealWord(),
//						new SymTabEntry().withBodyStartPos(currentRealToken.startPos).withKind(IMPLEMENTS)
//								.withMemberName(currentRealWord()).withEndPos(currentRealToken.endPos));
//
//				// skip interface name
//				nextRealToken();
//
//				if (currentRealKindEquals(',')) {
//					nextRealToken();
//				}
//			}
//
//			endOfImplementsClause = previousRealToken.endPos;
//
//			checkSearchStringFound(IMPLEMENTS, startPos);
//		}
//
//		parseClassBody();
	}
   
	private CharSequence parseQualifiedName(SymTabEntry nextEntity) {
		// return dotted name
		nextToken();
		nextToken();

		while (currentKindEquals('.') && !lookAheadKindEquals('.')
				&& !currentKindEquals(JavaFile.EOF)) {
			skip(".");

			// read next name
			nextToken();
		}
		return finishParse(nextEntity);
	}
}
