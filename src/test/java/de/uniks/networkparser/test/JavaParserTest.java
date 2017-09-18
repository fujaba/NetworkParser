package de.uniks.networkparser.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

//import de.uniks.networkparser.parser.JavaParser;
//import de.uniks.networkparser.parser.SymTabEntry;

public class JavaParserTest {

	@Test
	public void testJavaParser() throws IOException{
		byte[] fullContext = Files.readAllBytes(new File("src/main/java/de/uniks/networkparser/parser/JavaParser.java").toPath());
		String context = new String(fullContext);
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
