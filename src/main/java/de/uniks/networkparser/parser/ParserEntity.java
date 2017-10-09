package de.uniks.networkparser.parser;

import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzType;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.graph.Throws;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.graph.util.ParameterSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class ParserEntity {
	public static final char EOF = Character.MIN_VALUE;
	public static final char COMMENT_START = 'c';
	public static final char LONG_COMMENT_END = 'd';
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

	public static char NEW_LINE = '\n';

	private Clazz file;
	public Token lookAheadToken = new Token();
	public Token previousToken = new Token();
	public Token currentToken = new Token();
	public char currentChar;
	public char lookAheadChar;
	public int index;
	public int lookAheadIndex = -1;
	public int parsePos;
	public SymTabEntry symTabEntry;
	private SourceCode code;
	private String searchString;
	public int indexOfResult;
	private boolean verbose = false;

	public static Clazz create(CharacterBuffer content) {
		ParserEntity parser = new ParserEntity();
		return parser.parse(content);
	}

	public Clazz parse(CharacterBuffer sequence) {
		return parse(sequence, new Clazz(""), "");
	}
	public Clazz parse(CharacterBuffer sequence, Clazz file, String fileName) {
		if(sequence == null || sequence.length()<1) {
			return file;
		}
		this.file = file;
		this.code = new SourceCode().withContent(sequence);
		this.code.withFileName(fileName);
		this.code.with(this.file);

		nextChar();
		nextChar();

		nextToken();
		nextToken();
		// [packagestat] importlist classlist
		if (currentTokenEquals(SymTabEntry.TYPE_PACKAGE)) {
			parsePackageDecl();
		}
		code.withStartImports(currentToken.startPos);
		while (currentTokenEquals(SymTabEntry.TYPE_IMPORT)) {
			parseImport();
		}
		code.withEndOfImports(currentToken.startPos);
		parseClassDecl();
		return this.file;
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

	public boolean skip(char character, boolean skipCRLF) {
		if (currentKindEquals(character)) {
			if(skipCRLF) {
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

	public boolean skip(String string, boolean skipCRLF) {
		if (currentTokenEquals(string)) {
			if(skipCRLF) {
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

	public void error(CharSequence info) {
		System.err.println("Parser Error: expected token " + info + " found " + currentWord() + " at pos "
				+ currentToken.startPos + " at line " + getLineIndexOf(currentToken.startPos, code.getContent()));
		throw new RuntimeException("parse error");
	}

	public void nextRealToken() {
		nextToken();
		while (currentToken.kind == NEW_LINE) {
			nextToken();
		}
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

		while (lookAheadChar == 0 && lookAheadIndex < code.size() - 1) {
			lookAheadIndex++;
			lookAheadChar = code.getContent().charAt(lookAheadIndex);
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
		nextEntity.withValue(name);
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
		if (currentKindEquals(NEW_LINE)) {
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
			if (firstChar == NEW_LINE)
				count++;
		}
		return count;
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
		return result;
	}

	private void parseClassDecl() {
		int preCommentStartPos = currentToken.preCommentStartPos;
		int preCommentEndPos = currentToken.preCommentEndPos;
		int startPosAnnotations = currentToken.startPos;
		SymTabEntry nextEntity;
		while ("@".equals(currentWord())) {
			String annotation = parseAnnotations();

			int endPosAnnotation = currentToken.startPos - 1;
			if (annotation != "") {
				nextEntity = startNextSymTab(SymTabEntry.TYPE_ANNOTATION, annotation.substring(1));
				nextEntity.withPosition(startPosAnnotations, endPosAnnotation);
				this.file.with(Annotation.create(annotation));
			}
		}

		// modifiers class name classbody
		int startPosClazz = currentToken.startPos;
		file.with(Modifier.create(parseModifiers()));

		// class or interface or enum
		String classTyp = parseClassType();
		String className = currentWord();
		file.with(className);
		GraphUtil.setClazzType(file, ClazzType.create(classTyp));
		code.withEndOfClassName(currentToken.endPos);

		nextEntity = startNextSymTab(classTyp, className);
		nextEntity.withPosition(startPosClazz, currentToken.endPos);
		nextEntity.withAnnotationsStart(startPosAnnotations).withPreComment(preCommentStartPos, preCommentEndPos);

		// skip name
		nextRealToken();

		parseGenericTypeSpec();

		// extends
		if (EXTENDS.equalsIgnoreCase(currentWord())) {
			int startPos = currentToken.startPos;

			skip(EXTENDS, true);

			nextEntity = startNextSymTab(EXTENDS, currentWord());

			nextEntity.withPosition(currentToken.startPos, currentToken.endPos);

			// skip superclass name
			parseTypeRef();

			code.withEndOfExtendsClause(previousToken.endPos);

			checkSearchStringFound(EXTENDS, startPos);
		}

		// implements
		if (IMPLEMENTS.equals(currentWord())) {
			int startPos = currentToken.startPos;

			skip(IMPLEMENTS, true);

			while (!currentKindEquals(EOF) && !currentKindEquals('{')) {
				nextEntity = startNextSymTab(IMPLEMENTS, currentWord());
				nextEntity.withPosition(currentToken.startPos, currentToken.endPos);

				// skip interface name
				nextToken();

				if (currentKindEquals(',')) {
					nextToken();
				}
			}
			code.withEndOfImplementsClause(previousToken.endPos);

			checkSearchStringFound(IMPLEMENTS, startPos);
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
		StringBuilder typeString = new StringBuilder();

		// (void | qualifiedName) <T1, T2> [] ...
		String typeName = VOID;
		if (currentTokenEquals(VOID)) {
			// skip void
			nextToken();
		} else {
			typeName = parseQualifiedName().toString();
		}

		typeString.append(typeName);

		if (currentKindEquals('<')) {
			parseGenericTypeDefPart(typeString);
		}

		if (currentKindEquals('[')) {
			typeString.append("[]");
			skip("[", true);
			while (!"]".equals(currentWord()) && !currentKindEquals(EOF)) {
				nextToken();
			}
			skip("]", true);
		}

		if (currentKindEquals('.')) {
			typeString.append("...");
			skip(".", false);
			skip(".", false);
			skip(".", true);
		}

		if ("extends".equals(lookAheadToken.text.toString())) {
			typeString.append(currentToken.text);
			nextToken();
			typeString.append(currentToken.text);
			nextToken();
			typeString.append(currentToken.text);
			nextToken();
			typeString.append(currentToken.text);

		}
		if ("@".equals(typeString.toString())) {
			typeString.append(currentToken.text);
		}
		// phew
		return typeString.toString();
	}

	private void parseGenericTypeDefPart(StringBuilder typeString) {
		// <T, T, ...>
		skip("<", false);
		typeString.append('<');

		while (!currentKindEquals('>') && !currentKindEquals(EOF)) {
			if (currentKindEquals('<')) {
				parseGenericTypeDefPart(typeString);
			} else {
				typeString.append(currentWord());
				nextToken();
			}
		}

		// should be a < now
		typeString.append(">");
		skip(">", true);
	}

	private void parseClassBody() {
		// { classBodyDecl* }
		skip('{', true);
		checkSearchStringFound(CLASS_BODY, currentToken.startPos);
		while (!currentKindEquals(EOF) && !currentKindEquals('}')) {
			parseMemberDecl();
		}

		if (currentKindEquals('}')) {
			code.withEndBody(currentToken.startPos);
			checkSearchStringFound(CLASS_END, currentToken.startPos);
		}

		if (!currentKindEquals(EOF)) {
			skip("}", true);
		} else {
			checkSearchStringFound(CLASS_END, currentToken.startPos);
		}
	}

	private void parseMemberDecl() {
		// annotations modifiers (genericsDecl) ( typeRef name [= expression ] |
		// typeRef name '(' params ')' | classdecl ) ;

		// (javadoc) comment?
		int preCommentStartPos = currentToken.preCommentStartPos;
		int preCommentEndPos = currentToken.preCommentEndPos;

		// annotations
		int annotationsStartPos = currentToken.startPos;

		String annotations = parseAnnotations();

		int startPos = currentToken.startPos;

		String modifiers = parseModifiers();

		if (currentTokenEquals("<")) {
			// generic type decl
			skip("<", true);
			while (!currentTokenEquals(">")) {
				nextToken();
			}
			skip(">", true);
		}

		if (currentTokenEquals(CLASS) || currentTokenEquals(INTERFACE)) {
			// parse nested class
			// throw new RuntimeException("class " + className +
			// " has nested class. " + " Can't parse it.");
			// System.err.println("class " + fileName +
			// " has nested class in line " +
			// getLineIndexOf(currentRealToken.startPos) +
			// " Can't parse it. Skip it.");
			while (!currentTokenEquals("{")) {
				nextToken();
			}
			skipBody();

			return;
			// if (currentRealTokenEquals("}")) {
			// return;
			// }
			// modifiers = parseModifiers();
		} else if (currentTokenEquals(ENUM)) {
			// skip enum name { entry, ... }
			skip(ENUM, true);
			nextToken(); // name
			skipBody();
			return;

			// if (currentRealTokenEquals("}")){
			// return;
			// }
			// modifiers = parseModifiers();
		}

		if (currentTokenEquals(file.getName()) && lookAheadToken.kind == '(') {
			// constructor
			skip(file.getName(), true);

			String params = parseFormalParamList();

			// skip throws
			if (currentTokenEquals("throws")) {
				skipTo('{');
			}
			code.withStartBody(currentToken.startPos);
			parseBlock();

			String constructorSignature = SymTabEntry.TYPE_CONSTRUCTOR + ":" + file.getName() + params;
			SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_CONSTRUCTOR, file.getName() + params);
			nextEntity.withPosition(startPos, previousToken.startPos);
			nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
			nextEntity.withAnnotationsStart(annotationsStartPos);

			nextEntity.withBodyStartPos(code.getBodyStart());
			nextEntity.withModifiers(modifiers);

			checkSearchStringFound(constructorSignature, startPos);
		} else {
			String type = parseTypeRef();

			String memberName = currentWord();
			verbose("parsing member: " + memberName);

			nextToken();
			// Switch between Enum Value and Attributes
			if (currentKindEquals('=')) {
				// field declaration with initialisation
				skip("=", true);

				parseExpression();

				code.withEndOfAttributeInitialization(previousToken.startPos);

				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos);
				nextEntity.withModifiers(modifiers);
				nextEntity.withDataType(type);
				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);

				checkSearchStringFound(SymTabEntry.TYPE_ATTRIBUTE + ":" + memberName, startPos);
			} else if (currentKindEquals(';') && !",".equals(memberName)) {
				// field declaration
				checkSearchStringFound(NAME_TOKEN + ":" + searchString, startPos);
				skip(";", true);

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos);
				nextEntity.withModifiers(modifiers);
				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);
				nextEntity.withDataType(type);

				checkSearchStringFound(SymTabEntry.TYPE_ATTRIBUTE + ":" + memberName, startPos);
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

				code.withStartBody(currentToken.startPos);

				if (currentKindEquals('{')) {
					parseBlock();
				}

				else if (currentKindEquals(';'))
					skip(';', true);

				String methodSignature = SymTabEntry.TYPE_METHOD + ":" + memberName + params;

				SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_METHOD, memberName);
				nextEntity.withThrowsTags(throwsTags);
				nextEntity.withDataType(type);
				nextEntity.withParams(params);
				nextEntity.withPosition(startPos, previousToken.startPos);
				nextEntity.withModifiers(modifiers).withBodyStartPos(code.getBodyStart());
				nextEntity.withAnnotations(annotations);
				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);

				checkSearchStringFound(methodSignature, startPos);
				// System.out.println(className + " : " + methodSignature);
			} else if (ENUM.equals(file.getName())) {
				if (",".equalsIgnoreCase(memberName) || ";".equalsIgnoreCase(memberName)
						|| !";".equals(type) && currentKindEquals(EOF)) {
					// String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos);
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getBodyStart());
					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
				} else {
					// String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos);
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getBodyStart());
					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
					skipTo(';');
					skip(";", true);
				}
			}
		}
	}

	private void parseBlock() {
		// { stat ... }
		skip("{", true);

		while (!currentKindEquals(EOF) && !currentKindEquals('}')) {
			if (currentKindEquals('{')) {
				parseBlock();
			} else {
				nextToken();
			}
		}

		skip("}", true);
	}

	private String parseFormalParamList() {
		StringBuilder paramList = new StringBuilder().append('(');

		// '(' (type name[,] )* ') [throws type , (type,)*]
		skip("(", true);

		while (!currentKindEquals(EOF) && !currentKindEquals(')')) {
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
		while (index > 0 && !currentKindEquals(EOF)) {
			nextToken();
			if (currentTokenEquals("{"))
				index++;
			else if (currentTokenEquals("}"))
				index--;
		}
		nextToken();
	}

	private void skipTo(char c) {
		while (!currentKindEquals(c) && !currentKindEquals(EOF)) {
			nextToken();
		}
	}

	private CharSequence parseQualifiedName(SymTabEntry nextEntity) {
		// return dotted name
		nextToken();

		while (currentKindEquals('.') && !lookAheadKindEquals('.') && !currentKindEquals(EOF)) {
			skip(".", false);

			// read next name
			nextToken();
		}
		return finishParse(nextEntity);
	}

	private CharSequence parseQualifiedName() {
		// return dotted name
		int startPos = currentToken.startPos;
		int endPos = currentToken.endPos;

		checkSearchStringFound(NAME_TOKEN + ":" + currentWord(), currentToken.startPos);
		nextToken();

		while (currentKindEquals('.') && !(lookAheadToken.kind == '.') && !currentKindEquals(EOF)) {
			skip(".", false);

			// read next name
			endPos = currentToken.endPos;
			checkSearchStringFound(NAME_TOKEN + ":" + currentWord(), currentToken.startPos);
			nextToken();
		}

		return code.subString(startPos, endPos + 1);
	}

	// DEBUG METHODS
	private void checkSearchStringFound(String foundElem, int startPos) {
		if (EntityUtil.stringEquals(searchString, foundElem)) {
			indexOfResult = startPos;
			throw new RuntimeException("FOUND");
		}
	}

	private void verbose(String string) {
		if (verbose) {
			System.out.println(string);
		}
	}

	private void parseExpression() {
		// ... { ;;; } ;
		while (!currentKindEquals(EOF) && !currentKindEquals(';')) {
			if (currentKindEquals('{')) {
				parseBlock();
			} else {
				nextToken();
			}
		}
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

	public void addMemberToModel() {
		if(code == null) {
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
					addMemberAsAttribut(entry, symbolTab);
				}
			} else if (key.startsWith(SymTabEntry.TYPE_EXTENDS)) {
				// add super classes
				if (GraphUtil.isInterface(this.file)) {
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

	private static final String SDMLIBFILES = "org.sdmlib.serialization.EntityFactory "
			+ "org.sdmlib.models.pattern.PatternObject " + "org.sdmlib.models.pattern.util.PatternObjectCreator "
			+ "org.sdmlib.models.modelsets.SDMSet " + "org.sdmlib.serialization.PropertyChangeInterface";

	private void addMemberAsInterface(SymTabEntry memberName,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
		Clazz memberClass = findMemberClass(this.file, memberName, symbolTab);

		if(memberClass == null) {
			return;
		}
		// ignore helperclasses
		boolean found = SDMLIBFILES.indexOf(memberClass.getName(false)) > 0;
		if (found) {
			GraphUtil.removeYou(memberClass);
			return;
		}
		if (memberClass != null) {
			// memberClass.withInterface(true);
			this.file.withSuperClazz(memberClass);
		}
	}

	private Clazz findClassInModel(String name) {
		GraphModel model = this.file.getClassModel();
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
//		String[] split = memberName.split(":");
//		String signature = split[1];
		String signature = memberName.getValue();

		for (String key : symbolTab.keySet()) {
			String importName = symbolTab.get(key).first().getValue();
			if (key.startsWith(SymTabEntry.TYPE_IMPORT + ":") && importName.endsWith(signature)) {
				Clazz modelClass = findClassInModel(importName);

				if (modelClass != null) {
					return modelClass;
				} else {
					GraphModel model = this.file.getClassModel();
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
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
		// filter public static final constances
		String modifiers = symTabEntry.getModifiers();
		if ((modifiers.indexOf("public") >= 0 || modifiers.indexOf("private") >= 0) && modifiers.indexOf("static") >= 0
				&& modifiers.indexOf("final") >= 0) {
			// ignore
			return;
		}
		String type = symTabEntry.getDataType();
		// include arrays
		type = type.replace("[]", "");

		String attrName = symTabEntry.getValue();
		if (EntityUtil.isPrimitiveType(type)) {
			if (!classContainsAttribut(attrName, symTabEntry.getType())) {
				this.file.withAttribute(attrName, DataType.create(symTabEntry.getDataType()));
			}
		} else {
			// handle complex attributes
			handleComplexAttr(attrName, symTabEntry, symbolTab);
		}
	}

	private boolean classContainsAttribut(String attrName, String type) {
		for (Attribute attr : this.file.getAttributes()) {
			if (attrName.equals(attr.getName()) && type.equals(attr.getType()))
				return true;
		}
		return false;
	}

	private void handleComplexAttr(String attrName, SymTabEntry symTabEntry,
			SimpleKeyValueList<String, SimpleList<SymTabEntry>> symbolTab) {
		GraphModel model = this.file.getClassModel();
		if (model == null) {
			return;
		}
		String memberName = symTabEntry.getValue();
		String partnerTypeName = symTabEntry.getType();

		String partnerClassName = findPartnerClassName(partnerTypeName);
		Clazz partnerClass = null;
		for (Clazz clazz : model.getClazzes()) {
			if (partnerTypeName.equals(clazz.getName())) {
				partnerClass = clazz;
				break;
			}
		}
		if (partnerClass == null)
			return;

		Cardinality card = findRoleCard(partnerTypeName, model);

		String setterPrefix = "set";
		if (Cardinality.MANY.equals(card)) {
			setterPrefix = "addTo";
		}

		String name = EntityUtil.upFirstChar(memberName);

		SymTabEntry addToSymTabEntry = symbolTab
				.get(SymTabEntry.TYPE_METHOD + ":" + setterPrefix + name + "(" + partnerClassName + ")").first();

		if (addToSymTabEntry == null && "addTo".equals(setterPrefix)) {
			addToSymTabEntry = symbolTab
					.get(SymTabEntry.TYPE_METHOD + ":" + "with" + name + "(" + partnerClassName + "...)").first();
		}

		// type is unknown
		if (addToSymTabEntry == null) {
			this.file.withAttribute(memberName, DataType.create(partnerTypeName));
			return;
		}

		SimpleList<SymTabEntry> methodBodyQualifiedNames = symbolTab.get(SymTabEntry.TYPE_METHOD);
		// for (String key : parser.getMethodBodyQualifiedNames()) {
		// methodBodyQualifiedNames.add(key);
		// }

		boolean done = false;
		for (SymTabEntry qualifiedEntry : methodBodyQualifiedNames) {
			String qualifiedName = qualifiedEntry.getValue();
			if (qualifiedName.startsWith("value.set")) {

				// handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.set".length()));
				done = true;
			} else if (qualifiedName.startsWith("value.with") || qualifiedName.startsWith("item.with")) {
				// handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.with".length()));
				done = true;
			} else if (qualifiedName.startsWith("value.addTo")) {
				// FIXME handleAssoc(memberName, card, partnerClassName, partnerClass,
				// qualifiedName.substring("value.addTo".length()));
				done = true;
			}
		}
		if (!done) {
			// did not find reverse role, add as attribute
			this.file.withAttribute(memberName, DataType.create(partnerTypeName));
		}
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

	private Cardinality findRoleCard(String partnerTypeName, GraphModel model) {
		Cardinality partnerCard = Cardinality.ONE;
		int _openAngleBracket = partnerTypeName.indexOf("<");
		int _closeAngleBracket = partnerTypeName.indexOf(">");
		if (_openAngleBracket > 1 && _closeAngleBracket > _openAngleBracket) {
			// partner to many
			partnerCard = Cardinality.MANY;
		} else if (partnerTypeName.endsWith("Set") && partnerTypeName.length() > 3) {
			// it might be a ModelSet. Look if it starts with a clazz name
			String prefix = partnerTypeName.substring(0, partnerTypeName.length() - 3);
			for (Clazz clazz : model.getClazzes()) {
				if (prefix.equals(EntityUtil.shortClassName(clazz.getName()))) {
					partnerCard = Cardinality.MANY;
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
		String signature = symTabEntry.getValue();

		// filter internal generated methods
		if(SymTabEntry.TYPE_METHOD.equals(fullSignature) == false) {
			return;
		}
		String sign = signature + symTabEntry.getParams();
		if (SKIPMETGODS.indexOf(sign) < 0 && isGetterSetter(signature, symTab) == false
				&& isNewMethod(signature)) {
			String paramsStr = symTabEntry.getParams();
			String[] params = paramsStr.substring(1, paramsStr.length() - 1).split(",");

			Method method = new Method(signature)
					.with(DataType.create(symTabEntry.getDataType()));
			for (String param : params) {
				if (param != null && param.length() > 0) {
					method.with(new Parameter(DataType.create(param)));
				}
			}
			
			method = getMethod(method);
			
			method.withParent(this.file);
			
			if (!symTabEntry.getAnnotations().isEmpty()) {
				method.with(new Annotation(symTabEntry.getAnnotations()));
			}
			method.with(new Throws(symTabEntry.getThrowsTags()));
			method.withBody(this.code.subString(symTabEntry.getBodyStartPos(), symTabEntry.getEndPos() + 1).toString());
		}
	}
	
	private Method getMethod(Method search) {
		MethodSet methods = this.file.getMethods();
		for(Method method : methods) {
			if(method.toString().equals(search.toString())) {
				return method;
			} else  if(search.getName().equals(method.getName())) {
				if(search.getReturnType().equals(method.getReturnType())) {
					// Check all Parameter
					ParameterSet searchParam = search.getParameter();
					ParameterSet param = method.getParameter();
					if(searchParam.size() == param.size()) {
						boolean found=true;
						for(int i=0;i<param.size();i++) {
							if(param.get(i).getType().equals(searchParam.get(i).getType()) == false) {
								found=false;
								break;
							}
						}
						if(found) {
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
				String attrName = entry.getValue();
//				String signName = entry.getValue();
				if (methodName.toLowerCase().endsWith(attrName.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNewMethod(String memberName) {
		for (Method method : this.file.getMethods()) {
			if (method.getName(false).equals(memberName))
				return false;
		}
		return true;
	}
	
	public SourceCode getCode() {
		return code;
	}

	public SymTabEntry getSymbolEntry(String type, String name) {
		if(this.code != null) {
			return this.code.getSymbolEntry(type, name);
		}
		return null;
	}
}
