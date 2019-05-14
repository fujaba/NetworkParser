package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.FunctionCondition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;

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
	
	@Test
	public void testFunctionCondition() {
		String value = "{{#func de.uniks.networkparser.EntityUtil.upFirstChar({{hello}})}} World!!";
		Template template = Template.create(value);
		ObjectCondition condition = template.parsing(new FunctionCondition());
		
		TemplateResultFragment entity = template.executeEntity(condition, null);
		System.out.println(entity.getValue());
		Assert.assertNotNull(condition);
		Assert.assertNotNull(entity);
	}

}
