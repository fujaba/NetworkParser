package de.uniks.networkparser.parser;

/*
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
import java.util.Set;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.StringUtil;
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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class ParserEntity.
 *
 * @author Stefan
 */
public class ParserEntity implements SendableEntityCreator {
	
	/** The Constant NAME_TOKEN. */
	public static final String NAME_TOKEN = "nameToken";
	
	/** The Constant CLASS_BODY. */
	public static final String CLASS_BODY = "classBody";
	
	/** The Constant CLASS_END. */
	public static final String CLASS_END = "classEnd";
	
	/** The Constant ERROR. */
	public static final String ERROR = "ERROR";
	
	/** The Constant PROPERTY_FILENAME. */
	public static final String PROPERTY_FILENAME = "FILENAME";

	private ObjectCondition update;
	private Token lookAheadToken = new Token();
	private Token previousToken = new Token();
	private Token currentToken = new Token();

	private SymTabEntry symTabEntry;
	private SourceCode code;

	private char currentChar;
	private char lookAheadChar;
	private int index;
	private int lookAheadIndex = -1;
	private int parsePos;
	private long line = 1;

	/**
	 * With condition.
	 *
	 * @param update the update
	 * @return the parser entity
	 */
	public ParserEntity withCondition(ObjectCondition update) {
		if (update != null) {
			this.update = update;
		}
		return this;
	}

	/**
	 * Gets the line.
	 *
	 * @return the line
	 */
	public long getLine() {
		return line;
	}
	
	/**
	 * With file.
	 *
	 * @param fileName the file name
	 * @return the parser entity
	 */
	public ParserEntity withFile(String fileName) {
		this.code = new SourceCode();
		this.code.withFileName(fileName);
		if (fileName == null) {
			return this;
		}
		if (fileName.indexOf('.') > 0) {
			fileName = fileName.substring(fileName.lastIndexOf('.') + 1);
		}
		Clazz clazz = new Clazz(fileName);
		this.code.with(clazz);
		return this;
	}

	/**
	 * With file.
	 *
	 * @param fileName the file name
	 * @param clazz the clazz
	 * @return the parser entity
	 */
	public ParserEntity withFile(String fileName, Clazz clazz) {
		this.code = new SourceCode();
		this.code.withFileName(fileName);
		this.code.with(clazz);
		return this;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		if (code != null) {
			return code.getFileName();
		}
		return null;
	}

	/**
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	public Clazz getClazz() {
		if (code != null) {
			return this.code.getClazz();
		}
		return null;
	}

	/**
	 * Creates the.
	 *
	 * @param content the content
	 * @return the clazz
	 */
	public static Clazz create(CharacterBuffer content) {
		ParserEntity parser = new ParserEntity();
		return parser.parse(content);
	}

	/**
	 * Parses the.
	 *
	 * @param sequence the sequence
	 * @return the clazz
	 */
	public Clazz parse(CharacterBuffer sequence) {
		if (this.code == null) {
			/* FIX IT */
			this.code = new SourceCode();
			this.code.with(new Clazz(""));
		}
		if (sequence == null || sequence.length() < 1) {
			return getClazz();
		}
		this.code.withContent(sequence);

		nextChar();
		nextChar();

		nextToken();
		nextToken();
		/* [comment] [packagestat] [comment] importlist classlist */
		/* need this to ensure parser is working when no package is present */
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

		while (parseComment(true) != null) {
			skipNewLine();
		}

		parseClassDecl();
		return getClazz();
	}

	/**
	 * Current word.
	 *
	 * @return the string
	 */
	public String currentWord() {
		return currentToken.text.toString();
	}

	/**
	 * Current kind equals.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean currentKindEquals(char c) {
		return currentToken.kind == c;
	}

	/**
	 * Gets the current start.
	 *
	 * @return the current start
	 */
	public int getCurrentStart() {
		return currentToken.startPos;
	}

	private void skipNewLine() {
		while (currentKindEquals(Token.NEWLINE)) {
			nextToken();
		}
	}

	/**
	 * Gets the current end.
	 *
	 * @return the current end
	 */
	public int getCurrentEnd() {
		return currentToken.endPos;
	}

	/**
	 * Look ahead kind equals.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean lookAheadKindEquals(char c) {
		return lookAheadToken.kind == c;
	}

	/**
	 * Previous token kind equals.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean previousTokenKindEquals(char c) {
		return previousToken.kind == c;
	}

	/**
	 * Current token equals.
	 *
	 * @param word the word
	 * @return true, if successful
	 */
	public boolean currentTokenEquals(String word) {
		return stringEquals(currentWord(), word);
	}

	/**
	 * Current token equals.
	 *
	 * @param word the word
	 * @return true, if successful
	 */
	public boolean currentTokenEquals(char word) {
		return (currentToken.text.length() == 1 && currentToken.text.charAt(0) == word);
	}

	/**
	 * String equals.
	 *
	 * @param s1 the s 1
	 * @param s2 the s 2
	 * @return true, if successful
	 */
	public static boolean stringEquals(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	/**
	 * Skip.
	 *
	 * @param character the character
	 * @param skipCRLF the skip CRLF
	 * @return true, if successful
	 */
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

	/**
	 * Skip.
	 *
	 * @param string the string
	 * @param skipCRLF the skip CRLF
	 * @param body the body
	 * @return true, if successful
	 */
	public boolean skip(char string, boolean skipCRLF, CharacterBuffer body) {
		if (currentTokenEquals(string)) {
			if (skipCRLF) {
				if (body != null) {
					body.add(currentToken.originalText);
				}
				nextToken();
				while (currentToken.kind == Token.NEWLINE) {
					if (body != null) {
						body.add(currentToken.originalText);
					}
					nextToken();
				}
				return true;
			}
			if (body != null) {
				body.add(currentToken.originalText);
			}
			nextToken();
			return true;
		} else {
			error("" + string);
		}
		return false;
	}

	/**
	 * Skip.
	 *
	 * @param string the string
	 * @param skipCRLF the skip CRLF
	 * @return true, if successful
	 */
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

	/**
	 * Error.
	 *
	 * @param info the info
	 * @return true, if successful
	 */
	public boolean error(CharSequence info) {
		CharacterBuffer buffer = new CharacterBuffer().with("Parser Error:");
		if (this.code != null) {
			buffer.with(' ');
			buffer.with(this.code.getFileName());
			buffer.with(' ');
		}
		buffer.with(" expected token ", info, " found ", currentWord(), " at pos ");
		buffer.with("" + currentToken.startPos, " at line ");
		buffer.with("" + getLineIndexOf(currentToken.startPos, code.getContent()));
		if (this.update != null) {
			SimpleEvent event = new SimpleEvent(this, "error", null, buffer.toString());
			event.withType(ERROR);
			return this.update.update(event);
		}
		throw new SimpleException("parse error", this, buffer); 
	}

	/**
	 * Next real token.
	 */
	public void nextRealToken() {
		nextToken();
		while (currentToken.kind == Token.NEWLINE) {
			nextToken();
		}
	}

	/**
	 * Next token.
	 */
	public void nextToken() {
		Token tmp = previousToken;
		previousToken = currentToken;
		currentToken = lookAheadToken;

		if (currentToken.kind == Token.NEWLINE) {
			line++;
		}

		lookAheadToken = tmp;
		lookAheadToken.kind = Token.EOF;
		lookAheadToken.clear();

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
					/* lookAheadToken.value = currentChar - '0'; */
					lookAheadToken.startPos = index;
				} else if (currentChar == '/' && (lookAheadChar == '*' || lookAheadChar == '/')) {
					/* start of comment */
					lookAheadToken.kind = Token.COMMENT;
					lookAheadToken.startPos = index;
					lookAheadToken.addText(currentChar);
					nextChar();
					if (currentChar == '*') {
						lookAheadToken.kind = Token.LONG_COMMENT_START;
					}
					lookAheadToken.addText(currentChar);

					lookAheadToken.endPos = index;
					nextChar();
					return;
				} else if (currentChar == '*' && lookAheadChar == '/') {
					/* end of comment */
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
					/* keep reading */
					lookAheadToken.addText(currentChar);
				} else {
					lookAheadToken.endPos = index - 1;
					return; /* <==== sudden death */
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
		if (this.update != null) {
			SimpleEvent event = new SimpleEvent(this, NetworkParserLog.DEBUG, currentToken, lookAheadToken);
			event.withValue(index);
			event.withType(NetworkParserLog.DEBUG);
			this.update.update(event);
		}
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public SymTabEntry getRoot() {
		return symTabEntry;
	}

	/**
	 * Start next sym tab.
	 *
	 * @param type the type
	 * @return the sym tab entry
	 */
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

	/**
	 * Start next sym tab.
	 *
	 * @param type the type
	 * @param name the name
	 * @return the sym tab entry
	 */
	public SymTabEntry startNextSymTab(String type, String name) {
		SymTabEntry nextEntity = startNextSymTab(type);
		nextEntity.withName(name);
		return nextEntity;
	}

	/**
	 * Finish parse.
	 *
	 * @param nextEntity the next entity
	 * @return the char sequence
	 */
	public CharSequence finishParse(SymTabEntry nextEntity) {
		int endPos = getCurrentEnd();
		CharSequence sequence = code.subString(this.parsePos, endPos);
		if (nextEntity != null) {
			nextEntity.add(sequence);
		}
		return sequence;
	}

	/**
	 * Adds the current character.
	 *
	 * @param checkCharacter the check character
	 * @param nextEntity the next entity
	 */
	public void addCurrentCharacter(char checkCharacter, SymTabEntry nextEntity) {
		if (currentKindEquals(checkCharacter) && nextEntity != null) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}

	/**
	 * Adds the new line.
	 *
	 * @param nextEntity the next entity
	 */
	public void addNewLine(SymTabEntry nextEntity) {
		if (currentKindEquals(Token.NEWLINE) && nextEntity != null) {
			nextEntity.add(this.currentToken.text.toString());
			nextToken();
		}
	}

	/**
	 * Adds the current token.
	 *
	 * @param nextEntity the next entity
	 */
	public void addCurrentToken(SymTabEntry nextEntity) {
		if (nextEntity != null) {
			nextEntity.add(this.currentToken.text.toString());
		}
	}

	private long getLineIndexOf(int startPos, CharSequence fileBody) {
		long count = 1;
		if (fileBody == null) {
			return count;
		}
		CharSequence substring = fileBody.subSequence(0, startPos);
		for (int index = 0; index < substring.length() - 1; ++index) {
			final char firstChar = substring.charAt(index);
			if (firstChar == Token.NEWLINE)
				count++;
		}
		return count;
	}

	/**
	 * Gets the current line.
	 *
	 * @return the current line
	 */
	public long getCurrentLine() {
		if (this.code != null && currentToken != null) {
			return getLineIndexOf(currentToken.startPos, code.getContent());
		}
		return 0;
	}

	private void parseImport() {
		/* import qualifiedName [. *]; */
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
		if (!isComment()) {
			return null;
		}
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_COMMENT);
		CharacterBuffer buffer = new CharacterBuffer();
		if (currentKindEquals(Token.COMMENT)) {
			/* Simple Comment only one Line */
			buffer.add(currentToken.originalText);
			nextToken();
			while (!currentKindEquals(Token.NEWLINE) && !currentKindEquals(Token.EOF)) {
				buffer.add(currentToken.originalText);
				nextToken();
			}
			if (!currentKindEquals(Token.EOF)) {
				buffer.add(currentToken.originalText);
				skipNewLine();
			}
			nextEntity.withName(buffer);
			return buffer;
		}
		buffer.add(currentToken.originalText);
		nextToken();
		while (!currentKindEquals(Token.LONG_COMMENT_END) && !currentKindEquals(Token.EOF)) {
			buffer.add(currentToken.originalText);
			nextToken();
		}
		if (!currentKindEquals(Token.EOF)) {
			buffer.add(currentToken.originalText);
			nextToken();
		}
		nextEntity.withName(buffer);
		return buffer;
	}

	private String parseModifiers() {
		/* names != class */
		StringBuilder result = new StringBuilder();
		while (StringUtil.isModifier(" " + currentWord() + " ")) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(currentWord());
			nextToken();
		}
		return result.toString();
	}

	private void parsePackageDecl() {
		/* skip package */
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
/* FIXME		int preCommentStartPos = currentToken.preCommentStartPos; */
/*		int preCommentEndPos = currentToken.preCommentEndPos; */
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

		/* modifiers class name classbody */
		int startPosClazz = currentToken.startPos;
		if (this.getClazz() == null) {
			return;
		}
		getClazz().with(Modifier.create(parseModifiers()));

		/* class or interface or enum */
		String classTyp = parseClassType();
		String className = currentWord();
		this.code.getClazz().with(className);
		GraphUtil.setClazzType(getClazz(), GraphUtil.createType(classTyp));
		code.withEndOfClassName(currentToken.endPos);

		nextEntity = startNextSymTab(classTyp, className);
		nextEntity.withPosition(startPosClazz, currentToken.endPos, getLine(), getLine());
		nextEntity.withAnnotationsStart(startPosAnnotations);
/* FIXME		.withPreComment(preCommentStartPos, preCommentEndPos); */

		/* skip name */
		nextRealToken();
		parseGenericTypeSpec();

		/* extends */
		if (SymTabEntry.TYPE_EXTENDS.equalsIgnoreCase(currentWord())) {
			skip(SymTabEntry.TYPE_EXTENDS, true);

			nextEntity = startNextSymTab(SymTabEntry.TYPE_EXTENDS, currentWord());

			nextEntity.withPosition(currentToken.startPos, currentToken.endPos, getLine(), getLine());

			/* skip superclass name */
			parseTypeRef();
			code.withEndOfExtendsClause(previousToken.endPos);
		}
		/* implements */
		if (SymTabEntry.TYPE_IMPLEMENTS.equals(currentWord())) {
			skip(SymTabEntry.TYPE_IMPLEMENTS, true);

			while (!currentKindEquals(Token.EOF) && !currentKindEquals('{')) {
				nextEntity = startNextSymTab(SymTabEntry.TYPE_IMPLEMENTS, currentWord());
				nextEntity.withPosition(currentToken.startPos, currentToken.endPos, getLine(), getLine());

				/* skip interface name */
				nextToken();
				if (currentKindEquals('<')) {
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
		/* genTypeSpec < T , T, ...> */
		if (currentKindEquals('<')) {
			skipTo('>');
			nextToken();
		}
	}

	private String parseTypeRef() {
		CharacterBuffer typeString = new CharacterBuffer();
		/* (void | qualifiedName) <T1, T2> [] ... */
		String typeName = SymTabEntry.TYPE_VOID;
		if (currentTokenEquals(SymTabEntry.TYPE_VOID)) {
			/* skip void */
			nextToken();
		} else {
			typeName = parseQualifiedName().toString();
		}

		typeString.with(typeName);

		if (currentKindEquals('<')) {
			parseGenericTypeDefPart(typeString);
		}

		while (currentKindEquals('[')) {
			typeString.with("[]");
			skip("[", true);
			while (!"]".equals(currentWord()) && !currentKindEquals(Token.EOF)) {
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
		/* phew */
		return typeString.toString();
	}

	private void parseGenericTypeDefPart(CharacterBuffer typeString) {
		/* <T, T, ...> */
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

		/* should be a < now */
		typeString.with(">");
		skip(">", true);
	}

	private void parseClassBody() {
		/* { classBodyDecl* } */
		skip('{', true);
		boolean isDebug = this.update instanceof DebugCondition;
		code.withStartBody(currentToken.startPos, getLine());

		while (!currentKindEquals(Token.EOF) && !currentKindEquals('}')) {
			if (isDebug) {
			    SimpleEvent event = new SimpleEvent(this, "parsing", "Parsing: " + getCurrentLine()).withType(NetworkParserLog.DEBUG);
			    ((DebugCondition) this.update).update(event);
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

	/**  annotations 
	* annotations modifiers (genericsDecl) ( typeRef name [= expression ] | typeRef name '(' params ')' | classdecl ) ;
	* (javadoc) comment?
	*/
	private void parseMemberDecl() {
		long startLine = getLine();
		while (parseComment(true) != null) {
			skipNewLine();
		}

		/* annotations */
		int annotationsStartPos = currentToken.startPos;

		String annotations = parseAnnotations();

		int startPos = currentToken.startPos;

		String modifiers = parseModifiers();

		if (currentTokenEquals("<")) {
			/* generic type decl */
			skip("<", true);
			/* FIX MULTI GENERIC */
			int count = 1;
			while (!currentTokenEquals(Token.EOF) && count > 0) {
				while (!currentTokenEquals(">")) {
					if (currentTokenEquals("<")) {
						count++;
					}
					nextToken();
				}
				skip(">", true);
				count--;
			}
		}

		if (currentTokenEquals(SymTabEntry.TYPE_CLASS) || currentTokenEquals(SymTabEntry.TYPE_INTERFACE)) {
			while (!currentTokenEquals("{") && !currentKindEquals(Token.EOF)) {
				nextToken();
			}
			skipBody();

			return;
			/* modifiers = parseModifiers(); */
		} else if (currentTokenEquals(SymTabEntry.TYPE_ENUM)) {
			/* skip enum name { entry, ... } */
			skip(SymTabEntry.TYPE_ENUM, true);
			nextToken(); /* name */
			skipBody();
			return;
			/* modifiers = parseModifiers(); */
		}

		if (currentTokenEquals(getClazz().getName()) && lookAheadToken.kind == '(') {
			/* constructor */
			skip(getClazz().getName(), true);

			String params = parseFormalParamList();

			/* skip throws */
			if (currentTokenEquals("throws")) {
				skipTo('{');
			}
/*			code.withStartBody(currentToken.startPos, line); */
			CharacterBuffer block = new CharacterBuffer();
			skip('{', true, block);
			parseBlock(block, '}');

			SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_CONSTRUCTOR, getClazz().getName() + params);
			nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
/*			nextEntity.withComment(c) */
/* FIXME			nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
			nextEntity.withValue(block.toString());
			nextEntity.withAnnotationsStart(annotationsStartPos);

			nextEntity.withBodyStartPos(code.getStartBody());
			nextEntity.withModifiers(modifiers);
		} else if (currentKindEquals('{')) {
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
			/* Switch between Enum Value and Attributes */
			if (currentKindEquals('=')) {
				/* field declaration with initialisation */
				skip("=", true);

				CharacterBuffer expression = parseExpression(null);

				code.withEndOfAttributeInitialization(previousToken.startPos);

				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
				nextEntity.withModifiers(modifiers);
				nextEntity.withDataType(type);
				nextEntity.withValue(expression.toString());
/* FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
				nextEntity.withAnnotationsStart(annotationsStartPos);
			} else if (currentKindEquals(';') && !",".equals(memberName)) {
				/* field declaration */
				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
				nextEntity.withModifiers(modifiers);
/*FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
				nextEntity.withAnnotationsStart(annotationsStartPos);
				nextEntity.withDataType(type);
			} else if (currentKindEquals('(')) {

				String params = parseFormalParamList();
				if (type.startsWith("@")) {
					return;
				}

				/* skip throws */
				String throwsTags = null;
				if (currentTokenEquals("throws")) {
					int temp = currentToken.startPos;
					skipTo('{');
					throwsTags = code.subString(temp, currentToken.startPos).toString();
				}

				CharacterBuffer body = new CharacterBuffer();
				int startBodyPos = currentToken.startPos;
				if (currentKindEquals('{')) {
					startBodyPos = currentToken.startPos + 1;
					skip('{', true, body);
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
/*				.withBodyStartPos(code.getStartBody()); */
				nextEntity.withAnnotations(annotations);
/* FIXME				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
				nextEntity.withAnnotationsStart(annotationsStartPos);

			} else if (SymTabEntry.TYPE_ENUM.equals(getClazz().getType())) {
				if (",".equalsIgnoreCase(memberName) || ";".equalsIgnoreCase(memberName)
						|| !";".equals(type) && currentKindEquals(Token.EOF)) {
					/* String enumSignature = SDMLibParser.ENUMVALUE + ":" + type; */
					if (!"}".equals(type)) {
						SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
						nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
						nextEntity.withModifiers(modifiers).withBodyStartPos(code.getStartBody());
						/* FIXME nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
						nextEntity.withAnnotationsStart(annotationsStartPos);
					}
				} else {
					/* String enumSignature = SDMLibParser.ENUMVALUE + ":" + type; */
					SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos, startLine, getLine());
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getStartBody());
/* FIXME					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos); */
					nextEntity.withAnnotationsStart(annotationsStartPos);
					skipTo(';');
					skip(";", true);
				}
			}
		}
	}

	private boolean isComment() {
		return currentKindEquals(Token.LONG_COMMENT_START) || currentKindEquals(Token.COMMENT);
	}

	private CharacterBuffer parseBlock(CharacterBuffer body, char stopChar) {
		if (body == null) {
			body = new CharacterBuffer();
		}
		char prevprevTokenKind;
		/* { stat ... } */
		while (!currentKindEquals(Token.EOF) && !currentKindEquals(stopChar)) {
			while (!currentKindEquals(Token.EOF) && isComment()) {
				body.add(parseComment(false));
			}
			if (currentKindEquals(stopChar)) {
				break;
			}
			char search = 0;
			if (currentKindEquals('\'')) {
				search = '\'';
			}
			if (currentKindEquals('"')) {
				search = '\"';
			}
			if (search != 0) {
				while (!currentKindEquals(Token.EOF)) {
					body.add(currentToken.originalText);
					prevprevTokenKind = previousToken.kind;
					nextToken();
					if (currentKindEquals(search) && (!previousTokenKindEquals('\\') || prevprevTokenKind == '\\')) {
						break;
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

		/* '(' (type name[,] )* ') [throws type , (type,)*] */
		skip("(", true);

		while (!currentKindEquals(Token.EOF) && !currentKindEquals(')')) {
			int typeStartPos = currentToken.startPos;
			parseTypeRef();
			int typeEndPos = currentToken.startPos - 1;
			paramList.append(code.subString(typeStartPos, typeEndPos));

			/* parameter ends */
			if (currentKindEquals(')'))
				break;

			/* skip param name */
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
		/* nextRealToken(); */
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
		/* return dotted name */
		nextRealToken();

		while (currentKindEquals('.') && !lookAheadKindEquals('.') && !currentKindEquals(Token.EOF)) {
			skip(".", false);
			/* read next name */
			nextRealToken();
		}
		return finishParse(nextEntity);
	}

	private CharSequence parseQualifiedName() {
		/* return dotted name */
		int startPos = currentToken.startPos;
		int endPos = currentToken.endPos;

		nextRealToken();

		while (currentKindEquals('.') && !(lookAheadToken.kind == '.') && !currentKindEquals(Token.EOF)) {
			skip(".", false);

			/* read next name */
			endPos = currentToken.endPos;
			nextRealToken();
		}

		return code.subString(startPos, endPos + 1);
	}

	private CharacterBuffer parseExpression(CharacterBuffer buffer) {
		/* ... { ;;; } ; */
		if (buffer == null) {
			buffer = new CharacterBuffer();
		}
		if (currentKindEquals('\'')) {
			while (!currentKindEquals(Token.EOF) && !currentKindEquals(';')) {
				while (!currentKindEquals(Token.EOF) && !currentKindEquals('\'')) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
				if (currentKindEquals('\'')) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
			}
			return buffer;
		}
		if (currentKindEquals('"')) {
			while (!currentKindEquals(Token.EOF) && !currentKindEquals(';')) {
				while (!currentKindEquals(Token.EOF) && !currentKindEquals('"')) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
				if (currentKindEquals('"')) {
					nextToken();
					buffer.add(currentToken.originalText);
				}
			}
			return buffer;
		}
		while (!currentKindEquals(Token.EOF) && !currentKindEquals(';')) {
			if (currentKindEquals('\'')) {
				parseExpression(buffer);
			}
			if (currentKindEquals('"')) {
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
		if (SymTabEntry.TYPE_CLASS.equals(currentWord())) {
			classType = "class";
		} else if (SymTabEntry.TYPE_INTERFACE.equals(currentWord())) {
			classType = SymTabEntry.TYPE_INTERFACE;
		} else if (SymTabEntry.TYPE_ENUM.equals(currentWord())) {
			classType = SymTabEntry.TYPE_ENUM;
		}
		if (!classType.isEmpty()) {
			skip(classType, true);
		}
		return classType;
	}

	/**
	 * Adds the member to model.
	 *
	 * @param addStaticAttribute the add static attribute
	 */
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
				/* add new attributes */
				for (SymTabEntry entry : entities) {
					addMemberAsMethod(entry, symbolTab);
					addMemberAsAttribut(entry, symbolTab, addStaticAttribute);
				}
			} else if (key.startsWith(SymTabEntry.TYPE_EXTENDS)) {
				/* add super classes */
				if (GraphUtil.isInterface(this.getClazz())) {
					for (SymTabEntry entry : entities) {
						addMemberAsInterface(entry, symbolTab);
					}
				} else {
					/* addMemberAsSuperClass(clazz, memberName, parser); */
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
			/* memberClass.withInterface(true); */
			this.getClazz().withSuperClazz(memberClass);
		}
	}

	private Clazz findClassInModel(String name) {
		if (this.getClazz() == null) {
			return null;
		}
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
/* FIXME		String[] split = memberName.split(":"); */
		if (memberName == null) {
			return null;
		}
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
				/* might work */
				importName = importName.substring(0, importName.length() - 1) + signature;
				Clazz modelClass = findClassInModel(importName);
				if (modelClass != null) {
					return modelClass;
				}
			}
		}

		String name = clazz.getName(false);
		if (name != null) {
			int lastIndex = name.lastIndexOf('.');
			name = name.substring(0, lastIndex + 1) + signature;
		}
		return findClassInModel(name);
	}

	private void addMemberAsAttribut(SymTabEntry symTabEntry,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab, boolean addStaticAttribute) {
		/* filter public static final constances */
		if (symTabEntry == null) {
			return;
		}
		String modifiers = symTabEntry.getModifiers();
		if (!addStaticAttribute && ((modifiers.indexOf(Modifier.PUBLIC.getName()) >= 0 || modifiers.indexOf(Modifier.PRIVATE.getName()) >= 0)
				&& modifiers.indexOf(Modifier.STATIC.getName()) >= 0 && modifiers.indexOf(Modifier.FINAL.getName()) >= 0)) {
			/* ignore */
			return;
		}
		String type = symTabEntry.getDataType();
		if (type != null) {
			/* include arrays */
			type = type.replace("[]", "");
		}

		String attrName = symTabEntry.getName();
		if (StringUtil.isPrimitiveType(type)) {
			if (!classContainsAttribut(attrName, symTabEntry.getType())) {
				this.getClazz().withAttribute(attrName, DataType.create(symTabEntry.getDataType()));
			}
		} else {
			/* handle complex attributes */
			if (!handleComplexAttr(attrName, symTabEntry, symbolTab)) {
				/* Not Found so simple Attribute */
				this.getClazz().withAttribute(attrName, DataType.create(symTabEntry.getDataType()));
			}
		}
	}

	private boolean classContainsAttribut(String attrName, String type) {
		if (this.getClazz() == null) {
			return false;
		}
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

		String name = StringUtil.upFirstChar(memberName);

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
			SimpleList<SymTabEntry> simpleList = symbolTab
					.get(SymTabEntry.TYPE_METHOD + ":" + "with" + name + "(" + partnerClassName + "...)");
			if (simpleList == null) {
				return false;
			}
			addToSymTabEntry = simpleList.first();
		}

		/* type is unknown */
		if (addToSymTabEntry == null) {
			this.getClazz().withAttribute(memberName, DataType.create(partnerTypeName));
			return false;
		}

		SimpleList<SymTabEntry> methodBodyQualifiedNames = symbolTab.get(SymTabEntry.TYPE_METHOD);
		boolean done = false;
		for (SymTabEntry qualifiedEntry : methodBodyQualifiedNames) {
			String qualifiedName = qualifiedEntry.getName();
			if (qualifiedName.startsWith("value.set")) {
				done = true;
			} else if (qualifiedName.startsWith("value.with") || qualifiedName.startsWith("item.with")) {
				/* handleAssoc(memberName, card, partnerClassName, partnerClass, qualifiedName.substring("value.with".length())); */
				done = true;
			} else if (qualifiedName.startsWith("value.with")) {
				/* FIXME handleAssoc(memberName, card, partnerClassName, partnerClass, qualifiedName.substring("value.addTo".length())); */
				done = true;
			}
		}
		if (!done) {
			/* did not find reverse role, add as attribute */
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

				if (!foundAssoc) {
					srcRoleName = potentialCode.substring(0, potentialCode.indexOf("(")).toLowerCase();

					SourceCode partnerCode = (SourceCode) partnerClass.getChildByName("SourceCode", SourceCode.class);
					if (partnerCode == null) {
						return false;
					}
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

	/**
	 * Find partner class name.
	 *
	 * @param partnerTypeName the partner type name
	 * @return the string
	 */
	public String findPartnerClassName(String partnerTypeName) {
		String partnerClassName;
		if (partnerTypeName == null) {
			return null;
		}
		int openAngleBracket = partnerTypeName.indexOf("<");
		int closeAngleBracket = partnerTypeName.indexOf(">");

		if (openAngleBracket > -1 && closeAngleBracket > openAngleBracket) {
			partnerClassName = partnerTypeName.substring(openAngleBracket + 1, closeAngleBracket);
		} else if (partnerTypeName.endsWith("Set")) {
			/* TODO: should check for superclass ModelSet */
			partnerClassName = partnerTypeName.substring(0, partnerTypeName.length() - 3);
		} else {
			partnerClassName = partnerTypeName;
		}
		return partnerClassName;
	}

	private int findRoleCard(String partnerTypeName, GraphModel model) {
		int partnerCard = Association.ONE;
		if (partnerTypeName == null) {
			return partnerCard;
		}
		int _openAngleBracket = partnerTypeName.indexOf("<");
		int _closeAngleBracket = partnerTypeName.indexOf(">");
		if (_openAngleBracket > 1 && _closeAngleBracket > _openAngleBracket) {
			/* partner to many */
			partnerCard = Association.MANY;
		} else if (partnerTypeName.endsWith("Set") && partnerTypeName.length() > 3) {
			/* it might be a ModelSet. Look if it starts with a clazz name */
			String prefix = partnerTypeName.substring(0, partnerTypeName.length() - 3);
			for (Clazz clazz : model.getClazzes()) {
				if (prefix.equals(StringUtil.shortClassName(clazz.getName()))) {
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
		if (symTabEntry == null) {
			return;
		}
		String fullSignature = symTabEntry.getType();
		String signature = symTabEntry.getName();

		/* filter internal generated methods */
		if (!SymTabEntry.TYPE_METHOD.equals(fullSignature)) {
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
				/* Replace Body */
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
		Clazz clazz = this.getClazz();
		if (clazz == null) {
			return search;
		}
		MethodSet methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.toString().equals(search.toString())) {
				return method;
			} else if (search.getName().equals(method.getName())) {
				if (search.getReturnType().equals(method.getReturnType())) {
					/* Check all Parameter */
					ParameterSet searchParam = search.getParameters();
					ParameterSet param = method.getParameters();
					if (searchParam.size() == param.size()) {
						boolean found = true;
						for (int i = 0; i < param.size(); i++) {
							if (!param.get(i).getType().equals(searchParam.get(i).getType())) {
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
		/* method starts with: with set get ... */
		if (methodName == null) {
			return false;
		}
		if (methodName.startsWith("with") || methodName.startsWith("set") || methodName.startsWith("get")
				|| methodName.startsWith("add") || methodName.startsWith("remove") || methodName.startsWith("create")) {

			SimpleList<SymTabEntry> attributes = new SimpleList<SymTabEntry>();
			for (String key : symTab.keySet()) {
				if (key.startsWith("attribute")) {
					SimpleList<SymTabEntry> simpleList = symTab.get(key);
					attributes.addAll(simpleList);
				}
			}

			/* is class attribute */
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
		/* method starts with: with set get ... */
		if (methodName == null) {
			return false;
		}
		if (methodName.startsWith("with") || methodName.startsWith("set") || methodName.startsWith("get")
				|| methodName.startsWith("add") || methodName.startsWith("remove") || methodName.startsWith("create")) {

			SimpleList<SymTabEntry> assoc = new SimpleList<SymTabEntry>();
			for (String key : symTab.keySet()) {
				if (key.startsWith("attribute")) {
					SimpleList<SymTabEntry> simpleList = symTab.get(key);
					assoc.addAll(simpleList);
				}
			}

			/* is class attribute */
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
		Clazz clazz = getClazz();
		if (clazz == null) {
			return null;
		}
		for (Attribute attribute : clazz.getAttributes()) {
			if (attribute.getName().equals(memberName)) {
				return attribute;
			}
		}
		return null;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public SourceCode getCode() {
		return code;
	}

	/**
	 * Gets the symbol entry.
	 *
	 * @param type the type
	 * @param name the name
	 * @return the symbol entry
	 */
	public SymTabEntry getSymbolEntry(String type, String name) {
		if (this.code != null) {
			return this.code.getSymbolEntry(type, name);
		}
		return null;
	}

	/**
	 * Gets the symbol entries.
	 *
	 * @param type the type
	 * @return the symbol entries
	 */
	public SimpleList<SymTabEntry> getSymbolEntries(String type) {
		if (this.code != null) {
			return this.code.getSymbolEntries(type);
		}
		return null;
	}
	
	/**
	 * Checks if is content.
	 *
	 * @return true, if is content
	 */
	public boolean isContent() {
	    if(code != null) {
	        return !code.getContent().isEmpty();
	    }
	    return false;
	}

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    @Override
    public String[] getProperties() {
        return new String[] {PROPERTY_FILENAME};
    }

    /**
     * Gets the value.
     *
     * @param entity the entity
     * @param attribute the attribute
     * @return the value
     */
    @Override
    public Object getValue(Object entity, String attribute) {
        if(entity instanceof ParserEntity && PROPERTY_FILENAME.equalsIgnoreCase(attribute)) {
            return ((ParserEntity)entity).getFileName();
        }
        return null;
    }

    /**
     * Sets the value.
     *
     * @param entity the entity
     * @param attribute the attribute
     * @param value the value
     * @param type the type
     * @return true, if successful
     */
    @Override
    public boolean setValue(Object entity, String attribute, Object value, String type) {
        if(entity instanceof ParserEntity && PROPERTY_FILENAME.equalsIgnoreCase(attribute)) {
             ((ParserEntity)entity).withFile("" + value);
             return true;
        }
        return false;
    }

    /**
     * Gets the sendable instance.
     *
     * @param prototyp the prototyp
     * @return the sendable instance
     */
    @Override
    public Object getSendableInstance(boolean prototyp) {
        return new ParserEntity();
    }
}
