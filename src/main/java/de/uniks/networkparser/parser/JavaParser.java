package de.uniks.networkparser.parser;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class JavaParser {
	private boolean fileBodyHasChanged = false;
//	public static final String NAME_TOKEN = "nameToken";

	public SymTabEntry parse(CharSequence fileBody) {
		JavaFile javaFile = new JavaFile(fileBody);
		// [packagestat] importlist classlist
		if (javaFile.currentTokenEquals(SymTabEntry.TYPE_PACKAGE)) {
	         parsePackageDecl(javaFile);
	    }
		while (javaFile.currentTokenEquals(SymTabEntry.TYPE_IMPORT)) {
			parseImport(javaFile);
		}
		parseClassDecl();
		return javaFile.getRoot();
	}

	private void parseImport(JavaFile javaFile) {
		// import qualifiedName [. *];
		SymTabEntry nextEntity = javaFile.startNextSymTab(SymTabEntry.TYPE_IMPORT);
		javaFile.nextToken();

		String modifier = parseModifiers(javaFile);
		nextEntity.add(modifier);

		// if (!modifier.isEmpty())
		// System.out.println("static import");

//		CharSequence importName = 
		parseQualifiedName(javaFile, nextEntity);

		if (javaFile.currentKindEquals('*')) {
			javaFile.skip('*');
		}
		// if (currentRealToken.kind == '$'){
		// nextRealToken();
		// importName += "$"+currentRealWord();
		// nextRealToken();
		// }
		javaFile.skip(';');
//		symTab.put(IMPORT + ":" + importName, new SymTabEntry().withMemberName(importName).withModifiers(modifier)
//				.withStartPos(startPos).withEndPos(previousRealToken.endPos));
	}

	private String parseModifiers(JavaFile javaFile) {
		// names != class
		String result = "";
		String modifiers = " public protected private static abstract final native synchronized transient volatile strictfp ";
		while (modifiers.indexOf(" " + javaFile.currentWord() + " ") >= 0) {
			result += javaFile.currentWord() + " ";
			javaFile.nextToken();
		}
		return result;
	}

   private void parsePackageDecl(JavaFile javaFile)
   {
	   // skip package
	   SymTabEntry nextEntity = javaFile.startNextSymTab(SymTabEntry.TYPE_PACKAGE);
	   parseQualifiedName(javaFile, nextEntity);
	   javaFile.addCurrentCharacter(';', nextEntity);
	   javaFile.addNewLine(nextEntity);
   }
   
   private void parseClassDecl()
   {
//      int preCommentStartPos = currentRealToken.preCommentStartPos;
//      int preCommentEndPos = currentRealToken.preCommentEndPos;
//      
//      // FIXME skip all Annotations
//      int startPosAnnotations = currentRealToken.startPos;
//      while ("@".equals(currentRealWord()))
//      {
//         String annotation = parseAnnotations();
//
//         int endPosAnnotation = currentRealToken.startPos - 1;
//
//         // FIXME please
//         if (annotation != "")
//         {
//            symTab.put(ANNOTATION + ":" + annotation.substring(1),
//               new SymTabEntry().withKind(ANNOTATION).withMemberName(annotation.substring(1))
//                  .withEndPos(endPosAnnotation).withStartPos(startPosAnnotations));
//         }
//         
//         // nextRealToken();
//      }
//
//      // modifiers class name classbody
//      int startPosClazz = currentRealToken.startPos;
//      classModifier = parseModifiers();
//
//      // skip keyword
//      // skip ("class");
//
//      // class or interface or enum
//      String classTyp = parseClassType();
//      className = currentRealWord();
//      endOfClassName = currentRealToken.endPos;
//
//      symTab.put(classTyp + ":" + className,
//         new SymTabEntry().withStartPos(startPosClazz)
//            .withKind(classTyp)
//            .withMemberName(className)
//            .withEndPos(endOfClassName))
//            .withAnnotationsStartPos(startPosAnnotations)
//            .withPreCommentStartPos(preCommentStartPos)
//            .withPreCommentEndPos(preCommentEndPos)
//            ;
//
//      // skip name
//      nextRealToken();
//
//      parseGenericTypeSpec();
//
//      // extends
//      if ("extends".equals(currentRealWord()))
//      {
//         int startPos = currentRealToken.startPos;
//
//         skip("extends");
//
//         symTab.put(EXTENDS + ":" + currentRealWord(),
//            new SymTabEntry().withBodyStartPos(currentRealToken.startPos)
//               .withKind(EXTENDS)
//               .withMemberName(currentRealWord())
//               .withEndPos(currentRealToken.endPos));
//
//         // skip superclass name
//         parseTypeRef();
//
//         endOfExtendsClause = previousRealToken.endPos;
//
//         checkSearchStringFound(EXTENDS, startPos);
//      }
//
//      // implements
//      if ("implements".equals(currentRealWord()))
//      {
//         int startPos = currentRealToken.startPos;
//
//         skip("implements");
//
//         while (!currentRealKindEquals(EOF) && !currentRealKindEquals('{'))
//         {
//            symTab.put(IMPLEMENTS + ":" + currentRealWord(),
//               new SymTabEntry().withBodyStartPos(currentRealToken.startPos)
//                  .withKind(IMPLEMENTS)
//                  .withMemberName(currentRealWord())
//                  .withEndPos(currentRealToken.endPos));
//
//            // skip interface name
//            nextRealToken();
//
//            if (currentRealKindEquals(','))
//            {
//               nextRealToken();
//            }
//         }
//
//         endOfImplementsClause = previousRealToken.endPos;
//
//         checkSearchStringFound(IMPLEMENTS, startPos);
//      }
//
//      parseClassBody();
   }

   

	private CharSequence parseQualifiedName(JavaFile javaFile, SymTabEntry nextEntity) {
		// return dotted name
		javaFile.nextToken();
		javaFile.nextToken();

		while (javaFile.currentKindEquals('.') && !javaFile.lookAheadKindEquals('.')
				&& !javaFile.currentKindEquals(JavaFile.EOF)) {
			javaFile.skip(".");

			// read next name
			javaFile.nextToken();
		}
		return javaFile.finishParse(nextEntity);
	}

	public boolean isFileBodyHasChanged() {
		return fileBodyHasChanged;
	}

	public boolean setFileBodyHasChanged(boolean value) {
		if(value != this.fileBodyHasChanged) {
			this.fileBodyHasChanged = value;
			return true;
		}
		return false;
	}
}
