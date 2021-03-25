package de.uniks.networkparser.test.generator;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.FunctionCondition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateList;
import de.uniks.networkparser.parser.TemplateResultFragment;
import org.junit.Assert;
import org.junit.Test;

public class TestTemplate {
	@Test
	public void testSimpleTemplate() {
		String value = "Hallo {{name}}, deine Punktzahl ist {{number}}!";
		Template template = Template.create(value);
		ObjectCondition condition = template.parsing(new FunctionCondition());

		TemplateList variables=new TemplateList();
		variables.put("name", "Albert");
		variables.put("number", "42");

		template.executeSimpleEntity(condition, variables);
		Assert.assertNotNull(condition);
	}

	@Test
	public void testDebugCondition() {
		CharacterBuffer buffer=new CharacterBuffer();


		ClassModel model= new ClassModel();
		model.createClazz("uni");
		model.createClazz("room");

		buffer.withLine("{{#foreach {{clazz}}}}");
		buffer.withLine("{{#debug}}");
		buffer.withLine("{{#debug item.name==room}}");
		buffer.withLine("{{#endfor}}");

		Template template = Template.create(buffer.toString());
		ObjectCondition condition = template.parsing(new FunctionCondition());
		template.executeSimpleEntity(condition, model);
	}

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
		Assert.assertNotNull(condition);
	}

	@Test
	public void testFunctionCondition() {
		String value = "{{#func de.uniks.networkparser.EntityUtil.upFirstChar({{hello}})}} World!!";
		Template template = Template.create(value);
		ObjectCondition condition = template.parsing(new FunctionCondition());

		TemplateResultFragment entity = template.executeEntity(condition, null);
		Assert.assertNotNull(condition);
		Assert.assertNotNull(entity);
	}

}
