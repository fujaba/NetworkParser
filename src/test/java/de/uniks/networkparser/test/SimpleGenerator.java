package de.uniks.networkparser.test;


import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.parser.TemplateResultModel;

public class SimpleGenerator {
	@Test
	public void testIfStatement() {
		String template ="{{#if {{type}}==INTERFACE}}"
				+ "Hallo"
				+"{{#endif}}";
		Clazz person = new Clazz("Person").enableInterface();
		ModelGenerator generator = new ModelGenerator();

		TemplateResultFragment fragment = generator.parseTemplate(template, person);
		Assert.assertEquals("Hallo\r\n", fragment.getResult().toString());
		
		
		template ="{{#if {{type}}!=INTERFACE}}"
				+ "Hallo"
				+"{{#endif}}";
		person = new Clazz("Person");
		fragment = generator.parseTemplate(template, person);
		Assert.assertEquals("Hallo\r\n", fragment.getResult().toString());
	}
	
	
	@Test
	public void testIfAndStatement() {
		String template ="{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}}{{type}}!=INTERFACE{{#ENDAND}}}}"
				+ "Hallo"
				+" {{#endif}}";
		Clazz person = new Clazz("Person");
		ModelGenerator generator = new ModelGenerator();
		TemplateResultFragment fragment = generator.parseTemplate(template, person);
//		System.out.println(fragment);
		Assert.assertEquals("Hallo", fragment.getResult().toString());
	}
	
	@Test
	public void testGenerator() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz student = classModel.createClazz("Student");
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		student.withSuperClazz(person);
		student.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		person.withAttribute("name", DataType.STRING);
		person.withMethod("eat", DataType.BOOLEAN);
		ModelGenerator javaModelFactory = new ModelGenerator();
		javaModelFactory.generate("build", classModel);
//		javaModelFactory.generateTypescript("build", classModel);
//		System.out.println(javaModelFactory.create(classModel));
	}
	
//	@Test
	public void testGeneratorTemplate() {
		Template template = new Template().withTemplate(
				"{{#ifnot {{member.parent.type}}==interface}}"+
								"Hello World" +
							"{{#endif}}");
		
		Clazz person = new Clazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TextItems textItems = new TextItems();
		
		TemplateResultFragment generate = template.generate(templateFile, textItems, name);
		
		Assert.assertEquals("Hello World\r\n", generate.getResult().toString());
	}
	
//	@Test
	public void testGeneratorTemplateFragmentCondition() {
		Template template = new Template().withType(TemplateParser.DECLARATION).withTemplate(
				"{{#template PACKAGE {{PACKAGE}}}}Hello {{#endtemplate}}");

		Clazz person = new Clazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());
		
		
		TemplateResultFragment generate = template.generate(model, templateFile, name);
		
		Assert.assertEquals("Hello ", templateFile.toString());
	}
	
//	@Test
	public void testGeneratorTemplateFragment() {
		Template template = new Template().withType(TemplateParser.DECLARATION).withTemplate(
				"{{#template PACKAGE {{packagename}}}}Hello {{#endtemplate}}","",
				"{{#template IMPORT}}World{{#endtemplate}}");
		Clazz person = new Clazz("de.uniks.Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());
		
		
		TemplateResultFragment generate = template.generate(model, templateFile, name);
		
		Assert.assertEquals("Hello World", templateFile.toString());
	}
	
	
//	@Test
	public void testGeneratorTemplateFragmentIfCondition() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Template template = new Template().withType(TemplateParser.DECLARATION)
				.withTemplate("{{#template FIELD {{#ifnot {{file.clazz.type}}==interface}}}}Hello{{#endtemplate}}");
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());
		
		
		TemplateResultFragment generate = template.generate(model, templateFile, person);
		
		Assert.assertEquals("Hello", templateFile.toString());
	}
	
//	@Test
	public void testImport() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Template template = new Template().withType(TemplateParser.DECLARATION)
				.withTemplate("{{#import {{member.other.clazz.packageName}}{{member.other.clazz.name}}Set}}");
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		ModelGenerator modelGenerator = new ModelGenerator();
		model.withTemplate(modelGenerator.getTemplates());
		
		
		TemplateResultFragment generate = template.generate(model, templateFile, person);
		System.out.println(generate);
		
		Assert.assertEquals("Hello", templateFile.toString());
	}
	
}
