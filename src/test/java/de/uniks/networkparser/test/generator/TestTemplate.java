package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.parser.Template;

public class TestTemplate {

	@Test
	public void testIFTemplate() {
		String value = "{{#if true ? \"Hello\" : \"World\"}}!";
		Assert.assertNotNull(value);
//		Template template = Template.create(value);
//		ObjectCondition condition = template.parsing();
//		Assert.assertNotNull(condition);
	}
	@Test
	public void testIFTemplateSpace() {
//		String value = "{{#if true ? String... filter : \"World\"}}!";
		String value = "{{#if {{typecat}}!=SET? {{type}}... filter}}";
		Template template = Template.create(value);
		ObjectCondition condition = template.parsing();
		System.out.println(condition);
		Assert.assertNotNull(condition);
	}
}
