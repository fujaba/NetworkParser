package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.graph.Annotation;

public class AnnotationTest {
	@Test
	public void testAnnotation() {
		Annotation annotation;
		annotation = Annotation.create("@Loggable");
		assertEquals("@Loggable", annotation.toString());

		annotation = Annotation.create("@Cacheable(lifetime = 1000)");
		assertEquals("@Cacheable(lifetime=1000)", annotation.toString());

		annotation = Annotation.create("@Author (author = \"Krishan Class\")");
		assertEquals("@Author(author=\"Krishan Class\")", annotation.toString());

		annotation = Annotation.create("@Retention(RetentionPolicy.RUNTIME)");
		assertEquals("@Retention(RetentionPolicy.RUNTIME)", annotation.toString());

		annotation = Annotation.create("@RetryOnFailure(attempts = 3, delay = 1000, escalate = { UnknownHostException.class })");
		assertEquals("@RetryOnFailure(attempts=3,delay=1000,escalate=UnknownHostException.class)", annotation.toString());

		annotation = Annotation.create("@Target({ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.TYPE,ElementType.FIELD}");
		assertEquals("@Target(ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.TYPE,ElementType.FIELD)", annotation.toString());

		annotation = Annotation.create("@Loggable@Cacheable");
		assertEquals("@Loggable", annotation.toString());
		assertEquals("@Cacheable", annotation.next().toString());
	}

}
