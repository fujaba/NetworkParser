package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.Annotation;

public class AnnotationTest {
	@Test
	public void testAnnotation() {
		Annotation annotation;
		annotation = Annotation.create("@Loggable");
		Assert.assertEquals("@Loggable", annotation.toString());

		annotation = Annotation.create("@Cacheable(lifetime = 1000)");
		Assert.assertEquals("@Cacheable(lifetime=1000)", annotation.toString());

		annotation = Annotation.create("@Author (author = \"Krishan Class\")");
		Assert.assertEquals("@Author(author=\"Krishan Class\")", annotation.toString());

		annotation = Annotation.create("@Retention(RetentionPolicy.RUNTIME)");
		Assert.assertEquals("@Retention(RetentionPolicy.RUNTIME)", annotation.toString());

		annotation = Annotation.create("@RetryOnFailure(attempts = 3, delay = 1000, escalate = { UnknownHostException.class })");
		Assert.assertEquals("@RetryOnFailure(attempts=3,delay=1000,escalate=UnknownHostException.class)", annotation.toString());

		annotation = Annotation.create("@Target({ElementType.METHOD,\n" + 
			 "ElementType.CONSTRUCTOR,\n"+
			 "ElementType.TYPE,\n"+ 
			 "ElementType.FIELD}");
		Assert.assertEquals("@Target(ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.TYPE,ElementType.FIELD)", annotation.toString());
		
		annotation = Annotation.create("@Loggable@Cacheable");
		Assert.assertEquals("@Loggable", annotation.toString());
		Assert.assertEquals("@Cacheable", annotation.next().toString());
	}

}
