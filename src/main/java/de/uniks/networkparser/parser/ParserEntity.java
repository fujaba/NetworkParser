package de.uniks.networkparser.parser;

import java.io.File;
import java.util.LinkedHashMap;

import org.sdmlib.codegen.Parser;
import org.sdmlib.codegen.SymTabEntry;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.ext.generator.SDMLibParser;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzType;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.list.SimpleList;

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

	public static final String METHOD = "method";

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
	
	public static Clazz create(CharSequence content) {
		ParserEntity parser = new ParserEntity();
		return parser.parse(content);
	}

	public Clazz parse(CharSequence sequence) {
		this.file = new Clazz("");
		this.code = new SourceCode().withContent(sequence);
		this.code.with(this.file);

		nextChar();
		nextChar();

		nextToken();
		nextToken();
		// [packagestat] importlist classlist
		if (currentTokenEquals(SymTabEntry.TYPE_PACKAGE)) {
			parsePackageDecl();
		}
		while (currentTokenEquals(SymTabEntry.TYPE_IMPORT)) {
			parseImport();
		}
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
				+ currentToken.startPos + " at line " + getLineIndexOf(currentToken.startPos, code.getContent()));
		throw new RuntimeException("parse error");
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
		SymTabEntry nextEntity = new SymTabEntry();
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

	private void parsePackageDecl() {
		// skip package
		SymTabEntry nextEntity = startNextSymTab(SymTabEntry.TYPE_PACKAGE);
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
			// FIXME please
			if (annotation != "") {
				nextEntity = startNextSymTab(SymTabEntry.TYPE_ANNOTATION, annotation.substring(1));
				nextEntity.withPosition(startPosAnnotations, endPosAnnotation);
			}
		}

		// modifiers class name classbody
		int startPosClazz = currentToken.startPos;
		file.with(Modifier.create(parseModifiers()));

		// class or interface or enum
		String classTyp = parseClassType();
		String className = currentWord();
		file.with(className);
		file.with(ClazzType.valueOf(classTyp));
		code.withEndOfClassName(currentToken.endPos);

		nextEntity = startNextSymTab(classTyp, className);
		nextEntity.withPosition(startPosClazz, currentToken.endPos);
		nextEntity.withAnnotationsStart(startPosAnnotations).withPreComment(preCommentStartPos, preCommentEndPos);

		// skip name
		nextToken();

		parseGenericTypeSpec();

		// extends
		if (EXTENDS.equalsIgnoreCase(currentWord())) {
			int startPos = currentToken.startPos;

			skip(EXTENDS);

			nextEntity = startNextSymTab(EXTENDS, currentWord());

			nextEntity.withPosition(currentToken.startPos, currentToken.endPos);

			// skip superclass name
			parseTypeRef();

			code.withEndOfExtendsClause(previousToken.endPos);

			checkSearchStringFound(EXTENDS, startPos);
		}

		// implements
		if ("implements".equals(currentWord())) {
			int startPos = currentToken.startPos;

			skip("implements");

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
			skip("[");
			while (!"]".equals(currentWord()) && !currentKindEquals(EOF)) {
				nextToken();
			}
			skip("]");
		}

		if (currentKindEquals('.')) {
			typeString.append("...");
			skip(".");
			skip(".");
			skip(".");
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
		skip("<");
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
		skip(">");
	}

	private void parseClassBody() {
		// { classBodyDecl* }
		skip("{");
		checkSearchStringFound(CLASS_BODY, currentToken.startPos);
		while (!currentKindEquals(EOF) && !currentKindEquals('}')) {
			parseMemberDecl();
		}

		if (currentKindEquals('}')) {
			checkSearchStringFound(CLASS_END, currentToken.startPos);
		}

		if (!currentKindEquals(EOF)) {
			skip("}");
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
			skip("<");
			while (!currentTokenEquals(">")) {
				nextToken();
			}
			skip(">");
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
			skip(ENUM);
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
			skip(file.getName());

			String params = parseFormalParamList();

			// skip throws
			if (currentTokenEquals("throws")) {
				skipTo('{');
			}
			code.withStartBody(currentToken.startPos);
			parseBlock();

			
			String constructorSignature = SDMLibParser.CONSTRUCTOR + ":" + file.getName() + params;
			SymTabEntry nextEntity = startNextSymTab(SDMLibParser.CONSTRUCTOR, file.getName() + params);
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
				skip("=");

				parseExpression();

				
				code.withEndOfAttributeInitialization(previousToken.startPos);

				skip(";");
				
				SymTabEntry nextEntity = startNextSymTab(SDMLibParser.ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos);
				nextEntity.withModifiers(modifiers);
				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);

				checkSearchStringFound(SDMLibParser.ATTRIBUTE + ":" + memberName, startPos);
			} else if (currentKindEquals(';') && !",".equals(memberName)) {
				// field declaration
				checkSearchStringFound(NAME_TOKEN + ":" + searchString, startPos);
				skip(";");
				
				SymTabEntry nextEntity = startNextSymTab(SDMLibParser.ATTRIBUTE, memberName);
				nextEntity.withPosition(startPos, previousToken.startPos);
				nextEntity.withModifiers(modifiers);
				nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
				nextEntity.withAnnotationsStart(annotationsStartPos);

				checkSearchStringFound(SDMLibParser.ATTRIBUTE + ":" + memberName, startPos);
			} else if (currentKindEquals('(')) {

				String params = parseFormalParamList();

				// FIXME : skip annotations
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
					skip(';');

				String methodSignature = SDMLibParser.METHOD + ":" + memberName + params;
				
				SymTabEntry nextEntity = startNextSymTab(SDMLibParser.METHOD, memberName);
				nextEntity.withThrowsTags(throwsTags);
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
//					String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SDMLibParser.ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos);
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getBodyStart());
					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
				} else {
//					String enumSignature = SDMLibParser.ENUMVALUE + ":" + type;
					SymTabEntry nextEntity = startNextSymTab(SDMLibParser.ENUMVALUE, type);
					nextEntity.withPosition(startPos, previousToken.startPos);
					nextEntity.withModifiers(modifiers).withBodyStartPos(code.getBodyStart());
					nextEntity.withPreComment(preCommentStartPos, preCommentEndPos);
					nextEntity.withAnnotationsStart(annotationsStartPos);
					skipTo(';');
					skip(";");
				}
			}
		}
	}
	
	private void parseBlock() {
		// { stat ... }
		skip("{");

		while (!currentKindEquals(EOF) && !currentKindEquals('}')) {
			if (currentKindEquals('{')) {
				parseBlock();
			} else {
				nextToken();
			}
		}

		skip("}");
	}

	private String parseFormalParamList() {
		StringBuilder paramList = new StringBuilder().append('(');

		// '(' (type name[,] )* ') [throws type , (type,)*]
		skip("(");

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
				skip(",");
				paramList.append(',');
			}
		}
		skip(")");

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
		nextToken();

		while (currentKindEquals('.') && !lookAheadKindEquals('.') && !currentKindEquals(EOF)) {
			skip(".");

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
			skip(".");

			// read next name
			endPos = currentToken.endPos;
			checkSearchStringFound(NAME_TOKEN + ":" + currentWord(), currentToken.startPos);
			nextToken();
		}

		return code.subString(startPos, endPos + 1);
	}

	
	//TODO DEBUG METHODS
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
			skip(classType);
		}
		return classType;
	}
	
	public void addMemberToModel(Clazz clazz, Parser parser, String memberName, String rootDir)
	   {
	      //add annotations
	      if(memberName.startsWith("annotation")) {
	         addMemberAsAnnotation(clazz, memberName, parser);
	      }
	      
	      // add new methods
	      if (memberName.startsWith(Parser.METHOD))
	      {
	         addMemberAsMethod(clazz, memberName, parser);
	      }
	      // add new attributes
	      else if (memberName.startsWith(Parser.ATTRIBUTE))
	      {
	         String[] split = memberName.split(":");
	         String attrName = split[1];
	         SymTabEntry symTabEntry = parser.getSymTab().get(memberName);
	         if (symTabEntry != null)
	            addMemberAsAttribut(clazz, attrName, symTabEntry, rootDir);
	      }

	      // add super classes
	      if (memberName.startsWith(Parser.EXTENDS))
	      {
	         if (GraphUtil.isInterface(clazz))
	         {
	            addMemberAsInterface(clazz, memberName, parser);
	         }
	         else
	         {
	            addMemberAsSuperClass(clazz, memberName, parser);
	         }
	      }
	      else if (memberName.startsWith(Parser.IMPLEMENTS))
	      {
	         addMemberAsInterface(clazz, memberName, parser);
	      }

	   }

}
