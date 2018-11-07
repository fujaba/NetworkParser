package de.uniks.networkparser.parser;

/*
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
import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.graph.Throws;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class ParserEntity {
	public static final String VOID = "void";

	public static final String CLASS = "class";

	public static final String INTERFACE = "interface";

	public static final String ENUM = "enum";

	public static final String IMPLEMENTS = "implements";
	public static final String EXTENDS = "extends";

	public static final String NAME_TOKEN = "nameToken";

	public static final String CLASS_BODY = "classBody";

	public static final String CLASS_END = "classEnd";

	public static final String ENUMVALUE = "enumvalue";
	
	public static final String ERROR="ERROR";

	private ObjectCondition update;
	public Token lookAheadToken = new Token();
	public Token previousToken = new Token();
	public Token currentToken = new Token();

	public SymTabEntry symTabEntry;
	private SourceCode code;

	/* FIXME REMOVE */
	public char currentChar;
	public char lookAheadChar;
	public int index;
	public int lookAheadIndex = -1;
	public int parsePos;
	public long line=1;
	private NetworkParserLog logger;

	public ParserEntity withCondition(ObjectCondition update) {
		if(update != null) {
			this.update = update;
		}
		return this;
	}

	public long getLine() {
		return line;
	}
	
	public ParserEntity withFile(String fileName) {
		this.code = new SourceCode();
		this.code.withFileName(fileName);
		if(fileName.indexOf('.')>0) {
			fileName = fileName.substring(fileName.lastIndexOf('.')+1);
		}
		Clazz clazz = new Clazz(fileName);
		this.code.with(clazz);
		return this;
	}

	public ParserEntity withFile(String fileName, Clazz clazz) {
		this.code = new SourceCode();
		this.code.withFileName(fileName);
		this.code.with(clazz);
		return this;
	}

	public String getFileName() {
		if(code != null) {
			return code.getFileName();
		}
		return null;
	}
	
	public Clazz getClazz() {
		if(code != null) {
			return this.code.getClazz();
		}
		return null;
	}

	public static Clazz create(CharacterBuffer content) {
		ParserEntity parser = new ParserEntity();
		return parser.parse(content);
	}

	public Clazz parse(CharacterBuffer sequence) {
		if(this.code == null) {
			// FIX IT
			this.code = new SourceCode();
			this.code.with(new Clazz(""));
		}
		if (sequence == null || sequence.length() < 1 || this.code == null) {
			return getClazz();
		}
		this.code.withContent(sequence);

		nextChar();
		nextChar();

		nextToken();
		nextToken();
		// [comment] [packagestat] [comment] importlist classlist
		// need this to ensure parser is working when no package is present
		parseComment(true);

		skipNewLine();

		if (currentTokenEquals(SymTabEntry.TYPE_PACKAGE)) {
			parsePackageDecl();
		}
		skipNewLine();
		
		parseComment(true);

		code.withStartImports(currentToken.startPos);
		skipNewLine();

		while (currentTokenEquals(SymTabEntry.TYPE_IMPORT)) {
			parseImport();
			parseComment(true);
			skipNewLine();
		}
		code.withEndOfImports(currentToken.startPos);

		while(parseComment(true) != null) {
			skipNewLine();
		}
		

		parseClassDecl();
		return getClazz();
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
	
	private void skipNewLine() {
		while(currentKindEquals(Token.NEWLINE)) {
			nextToken();
		}
	}

	public int getCurrentEnd() {
		return currentToken.endPos;
	}

	public boolean lookAheadKindEquals(char c) {
		return lookAheadToken.kind == c;
	}
	
	public boolean previousTokenKindEquals(char c) {
		return previousToken.kind == c;
	}

	public boolean currentTokenEquals(String word) {
		return stringEquals(currentWord(), word);
	}
	public boolean currentTokenEquals(char word) {
		return (currentToken.text.length()==1 && currentToken.text.charAt(0) == word);
//		return stringEquals(currentWord(), word);
	}

	public static boolean stringEquals(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	public boolean skip(char character, boolean skipCRLF) {
		if (currentKindEquals(character)) {
			if (skipCRLF) {
				nextRealToken();
				return true;
			}
			nextToken();
			return true;
		} else {
			error("" + character);
		}
		return false;
	}

	public boolean skip(char string, boolean skipCRLF, CharacterBuffer body) {
		if (currentTokenEquals(string)) {
			if (skipCRLF) {
				if(body != null) {
					body.add(currentToken.originalText);
				}
				nextToken();
				while (currentToken.kind == Token.NEWLINE) {
					if(body != null) {
						body.add(currentToken.originalText);
					}
					nextToken();
				}
				return true;
			}
			if(body != null) {
				body.add(currentToken.originalText);
			}
			nextToken();
			return true;
		} else {
			error(""+string);
		}
		return false;
	}

	public boolean skip(String string, boolean skipCRLF) {
		if (currentTokenEquals(string)) {
			if (skipCRLF) {
				nextRealToken();
				return true;
			}
			nextToken();
			return true;
		} else {
			error(string);
		}
		return false;
	}

	public boolean error(CharSequence info) {
		CharacterBuffer buffer=new CharacterBuffer().with("Parser Error:");
		if(this.code != null) {
			buffer.with(' ');
			buffer.with(this.code.getFileName());
			buffer.with(' ');
		}
		buffer.with(" expected token ", info, " found ", currentWord(), " at pos ");
		buffer.with(""+currentToken.startPos," at line ");
		buffer.with(""+getLineIndexOf(currentToken.startPos, code.getContent()));
		if(this.update != null) {
			SimpleEvent event = new SimpleEvent(this, "error", null, buffer.toString());
			event.withType(ERROR);
			return this.update.update(event);
		}
		if(logger != null) {
			logger.error(this, "parse error", buffer.toString());
		}
		throw new RuntimeException("parse error");
	}

	public void nextRealToken() {
		nextToken();
		while (currentToken.kind == Token.NEWLINE) {
			nextToken();
		}
	}

	public void nextToken() {
		Token tmp = previousToken;
		previousToken = currentToken;
		currentToken = lookAheadToken;
		
		if(currentToken.kind == Token.NEWLINE) {
			line++;
		}

		lookAheadToken = tmp;
		lookAheadToken.kind = Token.EOF;
		lookAheadToken.clear();
//		delete(0, lookAheadToken.text.length());

		char state = Token.UNKNOWN;

		while (true) {
			switch (state) {
			case Token.UNKNOWN:
				if (Character.isLetter(currentChar) || (currentChar == '_')) {
					state = Token.VALUE;
					lookAheadToken.kind = Token.VALUE;
					lookAheadToken.addText(currentChar);
					lookAheadToken.startPos = index;
				} else if (currentChar == Token.EOF) {
					lookAheadToken.kind = Token.EOF;
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					return;
				} else if (Character.isDigit(currentChar)) {
					state = Token.NUMERIC;
					lookAheadToken.kind = Token.NUMERIC;
					lookAheadToken.addText(currentChar);
					// lookAheadToken.value = currentChar - '0';
					lookAheadToken.startPos = index;
				} else if (currentChar == '/' && (lookAheadChar == '*' || lookAheadChar == '/')) {
					// start of comment
					lookAheadToken.kind = Token.COMMENT;
					lookAheadToken.startPos = index;
					lookAheadToken.addText(currentChar);
					nextChar();
					if(currentChar == '*') {
						lookAheadToken.kind = Token.LONG_COMMENT_START;
					}
					lookAheadToken.addText(currentChar);
					
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '*' && lookAheadChar == '/') {
					// end of comment
					lookAheadToken.kind = Token.LONG_COMMENT_END;
					lookAheadToken.startPos = index;
					lookAheadToken.addText(currentChar);
					nextChar();
					lookAheadToken.addText(currentChar);
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if ("+-*/\\()\"'~=><{}!.,@[]&|?;:#".indexOf(currentChar) >= 0) {
					lookAheadToken.kind = currentChar;
					lookAheadToken.addText(currentChar);
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '\r') {
					lookAheadToken.startPos = index;
					lookAheadToken.addText(currentChar);
					nextChar();
					lookAheadToken.addText(currentChar);
					lookAheadToken.kind = Token.NEWLINE;
					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == Token.NEWLINE) {
					lookAheadToken.kind = Token.NEWLINE;
					lookAheadToken.startPos = index;
					lookAheadToken.endPos = index;
					lookAheadToken.addText(currentChar);
					nextChar();
					return;
				} else if (Character.isWhitespace(currentChar)) {
					lookAheadToken.addText(currentChar);
				}

				break;

			case Token.NUMERIC:
				if (Character.isDigit(currentChar)) {
					lookAheadToken.addText(currentChar);
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

			case Token.VALUE:
				if (Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_') {
					// keep reading
					lookAheadToken.addText(currentChar);
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

		while (lookAheadChar == 0 && lookAheadIndex < code.size() - 1) {
			lookAheadIndex++;
			lookAheadChar = code.getContent().charAt(lookAheadIndex);
		}
		if(this.update != null) {
			SimpleEvent event = new SimpleEvent(this, NetworkParserLog.DEBUG, currentToken, lookAheadToken);
			event.withValue(index);
			event.withType(NetworkParserLog.DEBUG);
			this.update.update(event);
		}
	}

	public SymTabEntry getRoot() {
		return symTabEntry;
	}

	public SymTabEntry startNextSymTab(String type) {
		SymTabEntry nextEntity = new SymTabEntry(null).withParent(code);
		nextEntity.setType(type);
		if (symTabEntry == null) {
			this.symTabEntry = nextEntity;
		} else {
			this.symTabEntry.setNext(nextEntity);
		}
		this.parsePos = getCurrentEnd() + 1;
		addCurrentToken(nextEntity);
		SimpleList<SymTabEntry> list = code.getSymbolEntries(type);
		list.add(nextEntity);
		return nextEntity;
	}

	public SymTabEntry startNextSymTab(String type, String name) {
		SymTabEntry nextEntity = startNextSymTab(type);
		nextEntity.withName(name);
		return nextEntity;
	}

	public CharSequence finishParse(SymTabEntry nextEntity) {
		int endPos = getCurrentEnd();
		CharSequence sequence = code.subString(this.parsePos, endPos);
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
		if (currentKindEquals(Token.NEWLINE)) {
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
			if (firstChar == Token.NEWLINE)
				count++;
		}
		return count;
	}
	
	public long getCurrentLine() {
		if(this.code != null && currentToken != null) {
			return getLineIndexOf(currentToken.startPos, code.getContent());
		}
		return 0;
	}

	private void parseImport() {
		// import qualifiedName [. *];
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_IMPORT);
		nextToken();

		String modifier = parseModifiers();
		nextEntity.add(modifier);

		parseQualifiedName(nextEntity);

		if (currentKindEquals('*')) {
			skip('*', false);
		}
		skip(';', true);
	}
	
	private CharacterBuffer parseComment(boolean newBlock) {
		if(isComment() == false) {
			return null;
		}
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_COMMENT);
		CharacterBuffer buffer=new CharacterBuffer();
		if(currentKindEquals(Token.COMMENT)) {
			// Simple Comment only one Line
			buffer.add(currentToken.originalText);
			nextToken();
			while(currentKindEquals(Token.NEWLINE) == false && currentKindEquals(Token.EOF) == false) {
				buffer.add(currentToken.originalText);
				nextToken();
			}
			if(currentKindEquals(Token.EOF) == false) {
				buffer.add(currentToken.originalText);
				skipNewLine();
//				nextToken();
			}
			nextEntity.withName(buffer);
			return buffer;
		}
		buffer.add(currentToken.originalText);
		nextToken();
		while(currentKindEquals(Token.LONG_COMMENT_END) == false && currentKindEquals(Token.EOF) == false) {
			buffer.add(currentToken.originalText);
			nextToken();
		}
		if(currentKindEquals(Token.EOF) == false) {
			buffer.add(currentToken.originalText);
			nextToken();
		}
		nextEntity.withName(buffer);
		return buffer;
	}
	

	private String parseModifiers() {
		// names != class
		StringBuilder result = new StringBuilder();
		while (EntityUtil.isModifier(" " + currentWord() + " ")) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(currentWord());
			nextToken();
		}
		return result.toString();
	}

	private void parsePackageDecl() {
		// skip package
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_PACKAGE);
		nextToken();
		parseQualifiedName(nextEntity);
		addCurrentCharacter(';', nextEntity);
		addNewLine(nextEntity);
	}

	private String parseAnnotations() {
		String result = "";

		while ("@".equals(currentWord())) {
			result += currentWord();
			nextToken();
			result += currentWord();
			nextToken();
			while (currentWord().equals(".")) {
				result += currentWord();
				nextToken();
				result += currentWord();
				nextToken();
			}
			if ("(".equals(currentWord())) {
				result += currentWord();
				nextToken();

				while (!")".equals(currentWord())) {
					result += currentWord();
					nextToken();
				}
				result += currentWord();
				nextToken();
			}
		}
		skipNewLine();
		return result;
	}
	
	private void parseClassDecl() {
//FIXME		int preCommentStartPos = currentToken.preCommentStartPos;
//		int preCommentEndPos = currentToken.preCommentEndPos;
		int startPosAnnotations = currentToken.startPos;
		SymTabEntry nextEntity;
		while ("@".equals(currentWord())) {
			String annotation = parseAnnotations();

			int endPosAnnotation = currentToken.startPos - 1;
			if (annotation != "") {
				nextEntity = startNextSymTab(SymTabEntry.TYPE_ANNOTATION, annotation.substring(1));
				nextEntity.withPosition(startPosAnnotations, endPosAnnotation, getLine(), getLine());
				getClazz().with(Annotation.create(annotation));
			}
		}

		// modifiers class name classbody
		int startPosClazz = currentToken.startPos;
		getClazz().with(Modifier.create(parseModifiers()));

		// class or interface or enum
		String classTyp = parseClassType();
		String className = currentWord();
		this.code.getClazz().with(className);
		GraphUtil.setClazzType(getClazz(), GraphUtil.createType(classTyp));
		code.withEndOfClassName(currentToken.endPos);

		nextEntity = startNextSymTab(classTyp, className);
		nextEntity.withPosition(startPosClazz, currentToken.endPos, getLine(), getLine());
		nextEntity.withAnnotationsStart(startPosAnnotations);
//FIXME		.withPreComment(preCommentStartPos, preCommentEndPos);

		// skip name
		nextRealToken();

		parseGenericTypeSpec();

		// extends
		if (EXTENDS.equalsIgnoreCase(currentWord())) {
			skip(EXTENDS, true);

			nextEntity = startNextSymTab(EXTENDS, currentWord());

			nextEntity.withPosition(currentToken.startPos, currentToken.endPos, getLine(), getLine());

			// skip superclass name
			parseTypeRef();

			code.withEndOfExtendsClause(previousToken.endPos);
		}

		// implements
		if (IMPLEMENTS.equals(currentWord())) {
			skip(IMPLEMENTS, true);

			while (!currentKindEquals(Token.EOF) && !currentKindEquals('{')) {
				nextEntity = startNextSymTab(IMPLEMENTS, currentWord());
				nextEntity.withPosition(currentToken.startPos, currentToken.endPos, getLine(), getLine());

				// skip interface name
				nextToken();
				if (currentKindEquals('<')) {
//					CharacterBuffer characterBuffer = new CharacterBuffer();
//					parseGenericTypeDefPart(typeString);
					parseGenericTypeSpec();
				}

				if (currentKindEquals(',')) {
					nextToken();
				}
			}
			code.withEndOfImplementsClause(previousToken.endPos);
		}

		parseClassBody();
	}

	private void parseGenericTypeSpec() {
		// genTypeSpec < T , T, ...>
		if (currentKindEquals('<')) {
			skipTo('>');
			nextToken();
		}
	}

	private String parseTypeRef() {
		CharacterBuffer typeString = new CharacterBuffer();

		// (void | qualifiedName) <T1, T2> [] ...
		String typeName = VOID;
		if (currentTokenEquals(VOID)) {
			// skip void
			nextToken();
		} else {
			typeName = parseQualifiedName().toString();
		}

		typeString.with(typeName);

		if (currentKindEquals('<')) {
			parseGenericTypeDefPart(typeString);
		}

		while(currentKindEquals('[')) {
			typeString.with("[]");
			skip("[", true);
			while ("]".equals(currentWord()) == false && currentKindEquals(Token.EOF) == false) {
				nextToken();
			}
			skip("]", true);
		}

		if (currentKindEquals('.')) {
			typeString.with("...");
			skip(".", false);
			skip(".", false);
			skip(".", true);
		}

		if ("extends".equals(lookAheadToken.text.toString())) {
			typeString.with(currentToken.text);
			nextToken();
			typeString.with(currentToken.text);
			nextToken();
			typeString.with(currentToken.text);
			nextToken();
			typeString.with(currentToken.text);

		}
		if ("@".equals(typeString.toString())) {
			typeString.with(currentToken.text);
		}
		// phew
		return typeString.toString();
	}

	private void parseGenericTypeDefPart(CharacterBuffer typeString) {
		// <T, T, ...>
		skip("<", false);
		typeString.with('<');

		while (!currentKindEquals('>') && !currentKindEquals(Token.EOF)) {
			if (currentKindEquals('<')) {
				parseGenericTypeDefPart(typeString);
			} else {
				typeString.with(currentWord());
				nextToken();
			}
		}

		// should be a < now
		typeString.with(">");
		skip(">", true);
	}

	private void parseClassBody() {
		// { classBodyDecl* }
		skip('{', true);
		boolean isDebug = this.update instanceof DebugCondition;
		code.withStartBody(currentToken.startPos, getLine());

		while (!currentKindEquals(Token.EOF) && !currentKindEquals('}')) {
			if(isDebug) {
				if(logger != null) {
					logger.debug(this, "parsing", "Parsing: "+getCurrentLine());
				}
			}
			parseMemberDecl();
		}

		if (currentKindEquals('}')) {
			code.withEndBody(currentToken.startPos, getLine());
			if (update != null) {
				update.update(CLASS_BODY);
			}
		} else if (previousToken.kind == '}') {
			code.withEndBody(previousToken.startPos, getLine());
		}

		if (!currentKindEquals(Token.EOF)) {
			skip("}", true);
		}
	}

	private void parseMemberDecl() {
		// annotations modifiers (genericsDecl) ( typeRef name [= expression ] |
		// typeRef name '(' params ')' | classdecl ) ;

		// (javadoc) comment?
//FIXME		int preCommentStartPos = currentToken.preCommentStartPos;
//		int preCommentEndPos = currentToken.preCommentEndPos;
		long startLine = getLine();
		while(parseComment(true) != null) {
			skipNewLine();
		}
		
		// annotations
		int annotationsStartPos = currentToken.startPos;

		String annotations = parseAnnotations();

		int startPos = currentToken.startPos;

		String modifiers = parseModifiers();

		if (currentTokenEquals("<")) {
			// generic type decl
			skip("<", true);
			// FIX MULTI GENERIC
			int count=1;
			while(currentTokenEquals(Token.EOF) == false && count>0 ) {
				while (currentTokenEquals(">") == false) {
					if(currentTokenEquals("<")) {
						count++;
					}
					nextToken();
				}
				skip(">", true);
				count--;
			}
		}

		if (currentTokenEquals(CLASS) || currentTokenEquals(INTERFACE)) {
			while (!currentTokenEquals("{") && currentKindEquals(Token.EOF) == false) {
				nextToken();
			}
			skipBody();

			return;
			// modifiers = parseModifiers();
		} else if (currentTokenEquals(ENUM)) {
			// skip enum name { entry, ... }
			skip(ENUM, true);
			nextToken(); // name
			skipBody();
			return;
			// modifiers = parseModifiers();
		}

		
		if (currentTokenEquals(getClazz().getName()) && lookAheadToken.kind == '(') {
			// constructor
			skip(getClazz().getName(), true);

			String params = parseFormalParamList();

			// skip throws
			if (currentTokenEquals("throws")) {
				skipTo('{');
			}
//			code.withStartBody(currentToken.startPos, line);
			CharacterBuffer block = new CharacterBuffer();
			skip('{', true, block);
			parseBlock(block, '}');

			SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_CONSTRUCTOR, getClazz().getName() + params);
			nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
//			nextEntity.withComment(c)
//FIXME			nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
			nextEntity.withValue(block.toString());
			nextEntity.withAnnotationsStart(annotationsStartPos);

			nextEntity.withBodyStartPos(code.getStartBody());
			nextEntity.withModifiers(modifiers);
		} else if(currentKindEquals('{')){
			CharacterBuffer block = new CharacterBuffer();
			skip('{', true, block);
			parseBlock(block, '}');
			SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_BLOCK, "");
			nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
			nextEntity.withModifiers(modifiers);
			nextEntity.withValue(block.toString());
		} else {
			String type = parseTypeRef();

			String memberName = currentWord();

			nextToken();
			// Switch between Enum Value and Attributes
			if (currentKindEquals('=')) {
				// field declaration with initialisation
				skip("=", true);

				CharacterBuffer expression = parseExpression(null);

				code.withEndOfAttributeInitialization(previousToken.startPos);

				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
				nextEntity.withModifiers(modifiers);
				nextEntity.withDataType(type);
				nextEntity.withValue(expression.toString());
//FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);
			} else if (currentKindEquals(';') && !",".equals(memberName)) {
				// field declaration
				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
				nextEntity.withModifiers(modifiers);
//FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);
				nextEntity.withDataType(type);
			} else if (currentKindEquals('(')) {

				String params = parseFormalParamList();
				if (type.startsWith("@")) {
					return;
				}

				// skip throws
				String throwsTags = null;
				if (currentTokenEquals("throws")) {
					int temp = currentToken.startPos;
					skipTo('{');
					throwsTags = code.subString(temp, currentToken.startPos).toString();
				}

//				code.withStartBody(previousToken.startPos, line);

				CharacterBuffer body = new CharacterBuffer();
				int startBodyPos=currentToken.startPos;
				if (currentKindEquals('{')) {
					skip('{', true, body);
					startBodyPos = currentToken.startPos;
					parseBlock(body, '}');
				} else {
					if (currentKindEquals(';')) {
						skip(';', true);
					}
				}

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_METHOD, memberName);
				nextEntity.withThrowsTags(throwsTags);
				nextEntity.withDataType(type);
				nextEntity.withParams(params);
				nextEntity.withBody(body.toString());
				nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
				nextEntity.withModifiers(modifiers);
				nextEntity.withBodyStartPos(startBodyPos);
//				.withBodyStartPos(code.getStartBody());
				nextEntity.withAnnotations(annotations);
//FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);

			} else if (ENUM.equals(getClazz().getName())) {
				if (",".equalsIgnoreCase(memberName) || ";".equalsIgnoreCase(memberName)
						|| !";".equals(type) && currentKindEquals(Token.EOF)) {
					// String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getStartBody());
//FIXME					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
				} else {
					// String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getStartBody());
//FIXME					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
					skipTo(';');
					skip(";", true);
				}
			}
		}
	}
	
	private boolean isComment() {
		return currentKindEquals(Token.LONG_COMMENT_START) ||currentKindEquals(Token.COMMENT); 
//				(currentKindEquals(Token.COMMENT) && lookAheadKindEquals(Token.COMMENT));
	}

	private CharacterBuffer parseBlock(CharacterBuffer body, char stopChar) {
		if(body == null) {
			body = new CharacterBuffer();
		}
		char prevprevTokenKind;
		// { stat ... }
		while (currentKindEquals(Token.EOF) == false && currentKindEquals(stopChar) == false) {
			while(currentKindEquals(Token.EOF) == false && isComment()) {
				body.add(parseComment(false));
			}
			if(currentKindEquals(stopChar)) {
				break;
			}
			char search=0;
			if(currentKindEquals('\'')) {
				search='\'';
			}
			if(currentKindEquals('"')) {
				search='\"';
			}
			if(search != 0) {
				while (currentKindEquals(Token.EOF) == false) {
					body.add(currentToken.originalText);
					prevprevTokenKind=previousToken.kind;
					nextToken();
					if(currentKindEquals(search)) {
						if(previousTokenKindEquals('\\') == false || prevprevTokenKind =='\\') {
							break;
						}
					}
				}
				skip(search, true, body);
				continue;
			}
			if (currentKindEquals('{')) {
				skip('{', true, body);
				parseBlock(body, '}');
			} else if (currentKindEquals('(')) {
				skip('(', true, body);
				parseBlock(body, ')');
			} else {
				body.add(currentToken.originalText);
				nextToken();
			}
		}

		skip(stopChar, true, body);
		return body;
	}

	private String parseFormalParamList() {
		StringBuilder paramList = new StringBuilder().append('(');

		// '(' (type name[,] )* ') [throws type , (type,)*]
		skip("(", true);

		while (!currentKindEquals(Token.EOF) && !currentKindEquals(')')) {
			int typeStartPos = currentToken.startPos;

			parseTypeRef();

			int typeEndPos = currentToken.startPos - 1;

			paramList.append(code.subString(typeStartPos, typeEndPos));

			// parameter ends
			if (currentKindEquals(')'))
				break;

			// skip param name
			nextToken();

			if (currentKindEquals(',')) {
				skip(",", true);
				paramList.append(',');
			}
		}
		skip(")", true);

		paramList.append(')');

		return paramList.toString();
	}

	private void skipBody() {
		int index = 1;
		// nextRealToken();
		while (index > 0 && !currentKindEquals(Token.EOF)) {
			nextToken();
			if (currentTokenEquals("{"))
				index++;
			else if (currentTokenEquals("}"))
				index--;
		}
		nextToken();
	}

	private void skipTo(char c) {
		while (!currentKindEquals(c) && !currentKindEquals(Token.EOF)) {
			nextToken();
		}
	}

	private CharSequence parseQualifiedName(SymTabEntry nextEntity) {
		// return dotted name
		nextRealToken();

		while (currentKindEquals('.') && !lookAheadKindEquals('.') && !currentKindEquals(Token.EOF)) {
			skip(".", false);

			// read next name
			nextRealToken();
		}
		return finishParse(nextEntity);
	}

	private CharSequence parseQualifiedName() {
		// return dotted name
		int startPos = currentToken.startPos;
		int endPos = currentToken.endPos;

		nextRealToken();

		while (currentKindEquals('.') && !(lookAheadToken.kind == '.') && !currentKindEquals(Token.EOF)) {
			skip(".", false);

			// read next name
			endPos = currentToken.endPos;
			nextRealToken();
		}

		return code.subString(startPos, endPos + 1);
	}

	private CharacterBuffer parseExpression(CharacterBuffer buffer) {
		// ... { ;;; } ;
		if(buffer==null) {
			buffer = new CharacterBuffer();
		}
		if(currentKindEquals('\'')) {
			while (currentKindEquals(Token.EOF) == false && currentKindEquals(';') == false) {
				while (currentKindEquals(Token.EOF) == false && currentKindEquals('\'') == false) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
				if(currentKindEquals('\'') ) {
					nextToken(); 
					buffer.add(currentToken.originalText);
				}
			}
			return buffer;
		}
		if(currentKindEquals('"') ) {
			while (currentKindEquals(Token.EOF) == false && currentKindEquals(';') == false) {
				while (currentKindEquals(Token.EOF) == false && currentKindEquals('"') == false) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
				if(currentKindEquals('"') ) {
					nextToken(); 
					buffer.add(currentToken.originalText);
				}
			}
			return buffer;
		}
		while (currentKindEquals(Token.EOF) == false && currentKindEquals(';') == false) {
			if(currentKindEquals('\'')) {
				parseExpression(buffer);
			}
			if(currentKindEquals('"')) {
				parseExpression(buffer);
			}
			if (currentKindEquals('{')) {
				skip('{', true, null);
				parseBlock(buffer, '}');
			} else if (currentKindEquals('(')) {
				skip('(', true, buffer);
				parseBlock(buffer, ')');
			} else {
				buffer.add(currentToken.originalText);
				nextToken();
			}
		}
		return buffer;
	}

	private String parseClassType() {
		String classType = "";
		if (CLASS.equals(currentWord())) {
			classType = "class";
		} else if (INTERFACE.equals(currentWord())) {
			classType = INTERFACE;
		} else if (ENUM.equals(currentWord())) {
			classType = ENUM;
		}
		if (classType.isEmpty() == false) {
			skip(classType, true);
		}
		return classType;
	}

	public void addMemberToModel(boolean addStaticAttribute) {
		if (code == null) {
			return;
		}
		SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab = code.getSymbolTab();
		Set<String> keySet = symbolTab.keySet();
		for (String key : keySet) {
			SimpleList<SymTabEntry> entities = symbolTab.get(key);
			if (key.startsWith(SymTabEntry.TYPE_METHOD)) {
				for (SymTabEntry entry : entities) {
					addMemberAsMethod(entry, symbolTab);
				}

			} else if (key.startsWith(SymTabEntry.TYPE_ATTRIBUTE)) {
				// add new attributes
				for (SymTabEntry entry : entities) {
					addMemberAsMethod(entry, symbolTab);
					addMemberAsAttribut(entry, symbolTab, addStaticAttribute);
				}
			} else if (key.startsWith(SymTabEntry.TYPE_EXTENDS)) {
				// add super classes
				if (GraphUtil.isInterface(this.getClazz())) {
					for (SymTabEntry entry : entities) {
						addMemberAsInterface(entry, symbolTab);
					}
				} else {
					// addMemberAsSuperClass(clazz, memberName, parser);
				}
			} else if (key.startsWith(SymTabEntry.TYPE_IMPLEMENTS)) {
				for (SymTabEntry entry : entities) {
					addMemberAsInterface(entry, symbolTab);
				}
			}
		}
	}

	private void addMemberAsInterface(SymTabEntry memberName,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
		Clazz memberClass = findMemberClass(this.getClazz(), memberName, symbolTab);

		if (memberClass == null) {
			return;
		}
		if (memberClass != null) {
			// memberClass.withInterface(true);
			this.getClazz().withSuperClazz(memberClass);
		}
	}

	private Clazz findClassInModel(String name) {
		GraphModel model = this.getClazz().getClassModel();
		if (model == null) {
			return null;
		}
		SimpleSet<Clazz> classes = model.getClazzes();

		for (Clazz eClazz : classes) {
			if (eClazz.getName(false).equals(name)) {
				return eClazz;
			}
		}
		return null;
	}

	private Clazz findMemberClass(Clazz clazz, SymTabEntry memberName,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
//FIXME		String[] split = memberName.split(":");
//		String signature = split[1];
		String signature = memberName.getName();

		for (String key : symbolTab.keySet()) {
			String importName = symbolTab.get(key).first().getName();
			if (key.startsWith(SymTabEntry.TYPE_IMPORT + ":") && importName.endsWith(signature)) {
				Clazz modelClass = findClassInModel(importName);

				if (modelClass != null) {
					return modelClass;
				} else {
					GraphModel model = this.getClazz().getClassModel();
					if (model == null) {
						return null;
					}
					Clazz externClass = model.createClazz(importName).withExternal(true);
					return externClass;
				}
			} else if (key.startsWith(SymTabEntry.TYPE_IMPORT + ":") && importName.endsWith("*")) {
				// might work
				importName = importName.substring(0, importName.length() - 1) + signature;

				Clazz modelClass = findClassInModel(importName);

				if (modelClass != null) {
					return modelClass;
				}
			}
		}

		String name = clazz.getName(false);
		int lastIndex = name.lastIndexOf('.');
		name = name.substring(0, lastIndex + 1) + signature;

		return findClassInModel(name);
	}

	private void addMemberAsAttribut(SymTabEntry symTabEntry,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab, boolean addStaticAttribute) {
		// filter public static final constances
		String modifiers = symTabEntry.getModifiers();
		if (addStaticAttribute == false && 
				((modifiers.indexOf("public") >= 0 || modifiers.indexOf("private") >= 0) 
				&& modifiers.indexOf("static") >= 0 && modifiers.indexOf("final") >= 0)) {
			// ignore
			return;
		}
		String type = symTabEntry.getDataType();
		// include arrays
		type = type.replace("[]", "");

		String attrName = symTabEntry.getName();
		if (EntityUtil.isPrimitiveType(type)) {
			if (!classContainsAttribut(attrName, symTabEntry.getType())) {
				this.getClazz().withAttribute(attrName, DataType.create(symTabEntry.getDataType()));
			}
		} else {
			// handle complex attributes
			if(handleComplexAttr(attrName, symTabEntry, symbolTab) == false) {
				// Not Found so simple Attribute
				this.getClazz().withAttribute(attrName, DataType.create(symTabEntry.getDataType()));
			}
		}
	}

	private boolean classContainsAttribut(String attrName, String type) {
		for (Attribute attr : this.getClazz().getAttributes()) {
			if (attrName.equals(attr.getName()) && type.equals(attr.getType()))
				return true;
		}
		return false;
	}

	private boolean handleComplexAttr(String attrName, SymTabEntry symTabEntry,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
		GraphModel model = this.getClazz().getClassModel();
		if (model == null) {
			return false;
		}
		String memberName = symTabEntry.getName();
		String partnerTypeName = symTabEntry.getDataType();
		// String partnerTypeName = symTabEntry.getType();

		String partnerClassName = findPartnerClassName(partnerTypeName);
		Clazz partnerClass = null;
		for (Clazz clazz : model.getClazzes()) {
			if (partnerClassName.equals(clazz.getName())) {
				partnerClass = clazz;
				break;
			}
		}
		if (partnerClass == null)
			return false;

		int card = findRoleCard(partnerTypeName, model);

		String setterPrefix = "set";
		if (Association.MANY == card) {
			setterPrefix = "with";
		}

		String name = EntityUtil.upFirstChar(memberName);

		/*
		 * SymTabEntry addToSymTabEntry = symbolTab .get(SymTabEntry.TYPE_METHOD + ":" +
		 * setterPrefix + name + "(" + partnerClassName + ")").first();
		 */

		SymTabEntry addToSymTabEntry = null;

		for (SymTabEntry entry : symbolTab.get(SymTabEntry.TYPE_METHOD)) {
			String methodName = entry.getName() + entry.getParams();
			if (methodName.equals(setterPrefix + name + "(" + partnerClassName + ")")
					|| methodName.equals(setterPrefix + name + "(" + partnerClassName + "...)")) {
				addToSymTabEntry = entry;
				break;
			}
		}

		if (addToSymTabEntry == null && "with".equals(setterPrefix)) {
			SimpleList<SymTabEntry> simpleList = symbolTab.get(SymTabEntry.TYPE_METHOD + ":" + "with" + name + "(" + partnerClassName + "...)");
			if(simpleList == null) {
				return false;
			}
			addToSymTabEntry = simpleList.first();
		}

		// type is unknown
		if (addToSymTabEntry == null) {
			this.getClazz().withAttribute(memberName, DataType.create(partnerTypeName));
			return false;
		}

		SimpleList<SymTabEntry> methodBodyQualifiedNames = symbolTab.get(SymTabEntry.TYPE_METHOD);
		// for (String key : parser.getMethodBodyQualifiedNames()) {
		// methodBodyQualifiedNames.add(key);
		// }

		boolean done = false;
		for (SymTabEntry qualifiedEntry : methodBodyQualifiedNames) {
			String qualifiedName = qualifiedEntry.getName();
			if (qualifiedName.startsWith("value.set")) {

				// handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.set".length()));
				done = true;
			} else if (qualifiedName.startsWith("value.with") || qualifiedName.startsWith("item.with")) {
				// handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.with".length()));
				done = true;
			} else if (qualifiedName.startsWith("value.with")) {
				// FIXME handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.addTo".length()));
				done = true;
			}
		}
		if (!done) {
			// did not find reverse role, add as attribute
			boolean found = false;

			String srcRoleName = "";
			int srcCardinality = Association.ONE;

			String potentialCode = "";

			for (SymTabEntry qualifiedEntry : methodBodyQualifiedNames) {
				String methodBody = this.code.getContent().toString().substring(qualifiedEntry.getStartPos(),
						qualifiedEntry.getEndPos());
				if (card == Association.ONE && qualifiedEntry.getName().startsWith("set")) {
					if (methodBody.contains("oldValue.without")) {
						potentialCode = methodBody.substring(methodBody.indexOf("oldValue.without") + 16);
						srcCardinality = Association.MANY;

						found = true;
						break;
					} else if (methodBody.contains("oldValue.set")) {
						potentialCode = methodBody.substring(methodBody.indexOf("oldValue.set") + 12);
						srcCardinality = Association.ONE;

						found = true;
						break;
					}
				} else if (card == Association.MANY && qualifiedEntry.getName().startsWith("with")) {
					if (methodBody.contains("item.with")) {
						potentialCode = methodBody.substring(methodBody.indexOf("item.with") + 9);
						srcCardinality = Association.MANY;

						found = true;
						break;
					} else if (methodBody.contains("item.set")) {
						potentialCode = methodBody.substring(methodBody.indexOf("item.set") + 8);
						srcCardinality = Association.ONE;

						found = true;
						break;
					}
				}
			}

			if (found) {
				boolean foundAssoc = false;

				for (Association association : this.getClazz().getAssociations()) {
					if (association.getName().equals(memberName)) {
						continue;
					}
					if (association.getOther().getName().equalsIgnoreCase(srcRoleName)) {
						continue;
					}

					foundAssoc = true;
					break;
				}

				if (foundAssoc == false) {
					srcRoleName = potentialCode.substring(0, potentialCode.indexOf("(")).toLowerCase();

					SourceCode partnerCode = (SourceCode) partnerClass.getChildByName("SourceCode", SourceCode.class);

					String partnerFile = partnerCode.getContent().toString();

					if (partnerFile.contains("PROPERTY_" + srcRoleName.toUpperCase() + " = ")) {
						String potentialName = partnerFile
								.substring(partnerFile.indexOf("PROPERTY_" + srcRoleName.toUpperCase()));
						potentialName = potentialName.substring(0, potentialName.indexOf(";"));
						srcRoleName = potentialName.substring(potentialName.indexOf("\"") + 1,
								potentialName.lastIndexOf("\""));
					}

					this.getClazz().withBidirectional(partnerClass, memberName, card, srcRoleName, srcCardinality);
				}
			} else {
				this.getClazz().withUniDirectional(partnerClass, memberName, card);
			}
		}
		return true;
	}

	public String findPartnerClassName(String partnerTypeName) {
		String partnerClassName;
		int openAngleBracket = partnerTypeName.indexOf("<");
		int closeAngleBracket = partnerTypeName.indexOf(">");

		if (openAngleBracket > -1 && closeAngleBracket > openAngleBracket) {
			partnerClassName = partnerTypeName.substring(openAngleBracket + 1, closeAngleBracket);
		} else if (partnerTypeName.endsWith("Set")) {
			// TODO: should check for superclass ModelSet
			partnerClassName = partnerTypeName.substring(0, partnerTypeName.length() - 3);
		} else {
			partnerClassName = partnerTypeName;
		}
		return partnerClassName;
	}

	private int findRoleCard(String partnerTypeName, GraphModel model) {
		int partnerCard = Association.ONE;
		int _openAngleBracket = partnerTypeName.indexOf("<");
		int _closeAngleBracket = partnerTypeName.indexOf(">");
		if (_openAngleBracket > 1 && _closeAngleBracket > _openAngleBracket) {
			// partner to many
			partnerCard = Association.MANY;
		} else if (partnerTypeName.endsWith("Set") && partnerTypeName.length() > 3) {
			// it might be a ModelSet. Look if it starts with a clazz name
			String prefix = partnerTypeName.substring(0, partnerTypeName.length() - 3);
			for (Clazz clazz : model.getClazzes()) {
				if (prefix.equals(EntityUtil.shortClassName(clazz.getName()))) {
					partnerCard = Association.MANY;
					break;
				}
			}
		}
		return partnerCard;
	}

	private static final String SKIPMETGODS = "get(String) firePropertyChange(String,Object,Object) set(String,Object) getPropertyChangeSupport() removeYou() addPropertyChangeListener(PropertyChangeListener) removePropertyChangeListener(PropertyChangeListener) addPropertyChangeListener(String,PropertyChangeListener) removePropertyChangeListener(String,PropertyChangeListener) toString()";

	private void addMemberAsMethod(SymTabEntry symTabEntry,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symTab) {
		String fullSignature = symTabEntry.getType();
		String signature = symTabEntry.getName();

		// filter internal generated methods
		if (SymTabEntry.TYPE_METHOD.equals(fullSignature) == false) {
			return;
		}
		String sign = signature + symTabEntry.getParams();
		if (SKIPMETGODS.indexOf(sign) < 0) {
			if (symTabEntry.isNoGen()) {
				Attribute attribtue = getAttribtue(signature);
				GraphUtil.setGenerate(attribtue, false);
			}
			if (isGetterSetter(signature, symTab)) {
				return;
			}
			if (isAssoc(signature, symTab)) {
				return;
			}
			Method method = getMethod(signature);
			if (method != null) {
				// Replace Body
				method.withBody(
						this.code.subString(symTabEntry.getBodyStartPos(), symTabEntry.getEndPos() + 1).toString());
				return;
			}

			String paramsStr = symTabEntry.getParams();
			String[] params = paramsStr.substring(1, paramsStr.length() - 1).split(",");

			method = new Method(signature).with(DataType.create(symTabEntry.getDataType()));
			for (String param : params) {
				if (param != null && param.length() > 0) {
					method.with(new Parameter(DataType.create(param)));
				}
			}

			method = getMethod(method);

			method.withParent(this.getClazz());

			if (!symTabEntry.getAnnotations().isEmpty()) {
				method.with(new Annotation(symTabEntry.getAnnotations()));
			}
			method.with(new Throws(symTabEntry.getThrowsTags()));
			method.withBody(this.code.subString(symTabEntry.getBodyStartPos(), symTabEntry.getEndPos() + 1).toString());
		}
	}

	private Method getMethod(Method search) {
		MethodSet methods = this.getClazz().getMethods();
		for (Method method : methods) {
			if (method.toString().equals(search.toString())) {
				return method;
			} else if (search.getName().equals(method.getName())) {
				if (search.getReturnType().equals(method.getReturnType())) {
					// Check all Parameter
					ParameterSet searchParam = search.getParameters();
					ParameterSet param = method.getParameters();
					if (searchParam.size() == param.size()) {
						boolean found = true;
						for (int i = 0; i < param.size(); i++) {
							if (param.get(i).getType().equals(searchParam.get(i).getType()) == false) {
								found = false;
								break;
							}
						}
						if (found) {
							return method;
						}
					}
				}
			}
		}
		return search;
	}

	private boolean isGetterSetter(String methodName, SimpleKeyValueList<String, SimpleList<SymTabEntry>> symTab) {
		// method starts with: with set get ...
		if (methodName.startsWith("with") || methodName.startsWith("set") || methodName.startsWith("get")
				|| methodName.startsWith("add") || methodName.startsWith("remove") || methodName.startsWith("create")) {

			SimpleList<SymTabEntry> attributes = new SimpleList<SymTabEntry>();
			for (String key : symTab.keySet()) {
				if (key.startsWith("attribute")) {
					SimpleList<SymTabEntry> simpleList = symTab.get(key);
					attributes.addAll(simpleList);
				}
			}

			// is class attribute
			for (SymTabEntry entry : attributes) {
				String attrName = entry.getName();
				if (methodName.toLowerCase().endsWith(attrName.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAssoc(String methodName, SimpleKeyValueList<String, SimpleList<SymTabEntry>> symTab) {
		// method starts with: with set get ...
		if (methodName.startsWith("with") || methodName.startsWith("set") || methodName.startsWith("get")
				|| methodName.startsWith("add") || methodName.startsWith("remove") || methodName.startsWith("create")) {

			SimpleList<SymTabEntry> assoc = new SimpleList<SymTabEntry>();
			for (String key : symTab.keySet()) {
				if (key.startsWith("attribute")) {
					SimpleList<SymTabEntry> simpleList = symTab.get(key);
					assoc.addAll(simpleList);
				}
			}

			// is class attribute
			for (SymTabEntry entry : assoc) {
				String attrName = entry.getName();
				if (methodName.toLowerCase().endsWith(attrName.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private Method getMethod(String memberName) {
		for (Method method : getClazz().getMethods()) {
			if (method.getName(false, false).equals(memberName))
				return method;
		}
		return null;
	}

	private Attribute getAttribtue(String memberName) {
		for (Attribute attribute : getClazz().getAttributes()) {
			if (attribute.getName().equals(memberName)) {
				return attribute;
			}
		}
		return null;
	}

	public SourceCode getCode() {
		return code;
	}

	public SymTabEntry getSymbolEntry(String type, String name) {
		if (this.code != null) {
			return this.code.getSymbolEntry(type, name);
		}
		return null;
	}
	public SimpleList<SymTabEntry> getSymbolEntries(String type) {
		if (this.code != null) {
			return this.code.getSymbolEntries(type);
		}
		return null;
	}

	public ParserEntity withLogger(NetworkParserLog value) {
		this.logger = value;
		return this;
	}
}
