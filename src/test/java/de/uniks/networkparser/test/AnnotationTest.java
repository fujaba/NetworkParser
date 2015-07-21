package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.emf.Annotation;

public class AnnotationTest {
	@Test
	public void testAnnotation() {
		Annotation annotation;
//		annotation = Annotation.create("@Loggable");
//		System.out.println(annotation);
//		annotation = Annotation.create("@Cacheable(lifetime = 1000)");
//		System.out.println(annotation);
//		annotation = Annotation.create("@Author (author = \"Krishan Class\")");
//		System.out.println(annotation);
//		annotation = Annotation.create("@Retention(RetentionPolicy.RUNTIME)");
//		System.out.println(annotation);
		annotation = Annotation.create("@RetryOnFailure(attempts = 3, delay = 1000, escalate = { UnknownHostException.class })");
		System.out.println(annotation);
		annotation = Annotation.create("@Target({ElementType.METHOD,\n" + 
			 "ElementType.CONSTRUCTOR,\n"+
			 "ElementType.TYPE,\n"+ 
			 "ElementType.FIELD}");
		System.out.println(annotation);
		annotation = Annotation.create("@Loggable@Cacheable");
		System.out.println(annotation);
	}

}
