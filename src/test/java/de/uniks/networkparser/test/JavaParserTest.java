package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;

//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

//import de.uniks.networkparser.parser.JavaParser;
//import de.uniks.networkparser.parser.SymTabEntry;

public class JavaParserTest {

	@Test
	public void testJavaParser() throws IOException{
		CharacterBuffer fullContext = FileBuffer.readFile("src/main/java/de/uniks/networkparser/parser/JavaParser.java");
//		CompilationUnit compilationUnit = JavaParser.parse("class A { }");
//		CompilationUnit compilationUnit = JavaParser.parse(context);
//		Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
//		System.out.println(classA);
//		classA.

//		JavaParser parser = new JavaParser();
//		SymTabEntry parse = parser.parse(context);
//		Assert.assertNotNull(parse);
	}
}
