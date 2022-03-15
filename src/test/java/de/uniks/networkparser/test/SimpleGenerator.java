package de.uniks.networkparser.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.IfCondition;
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
		assertEquals("Hallo\r\n", fragment.getResult().toString());


		template ="{{#if {{type}}!=INTERFACE}}"
				+ "Hallo"
				+"{{#endif}}";
		person = new Clazz("Person");
		fragment = generator.parseTemplate(template, person);
		assertEquals("Hallo\r\n", fragment.getResult().toString());
	}


	@Test
	public void testIfAndStatement() {
		String template ="{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{type}}!=INTERFACE{{#ENDAND}}}}"
				+ "Hallo"
				+"{{#endif}}";
		Clazz person = new Clazz("Person");
		ModelGenerator generator = new ModelGenerator();
		TemplateResultFragment fragment = generator.parseTemplate(template, person);
		assertEquals("Hallo\r\n", fragment.getResult().toString());
	}

//	@Test
	//FIXME STUDENT NOT CORRECT
	public void testGenerator() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz student = classModel.createClazz("Student");
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		student.withSuperClazz(person);
		student.withBidirectional(room, "room", Association.ONE, "persons", Association.MANY);
		person.withAttribute("name", DataType.STRING);
		person.withMethod("eat", DataType.BOOLEAN);
		ModelGenerator javaModelFactory = new ModelGenerator();
		javaModelFactory.generating("build/gen/java", classModel, null, ModelGenerator.TYPE_JAVA, true, true);
//		javaModelFactory.generateTypescript("build", classModel);
	}

	@Test
	public void testGeneratorTemplate() {
		Template template = new Template().withTemplate(
				"{{#ifnot {{parent.type}}==interface}}"+
								"Hello World" +
							"{{#endif}}");

		Clazz person = new Clazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		ModelGenerator generator = new ModelGenerator();
		TemplateResultFragment generate = generator.parseTemplate(template, name);

		assertEquals("Hello World\r\n", generate.getResult().toString());
	}

	@Test
	public void testGeneratorTemplateFragmentCondition() {
		Template template = new Template().withType(TemplateParser.DECLARATION).withTemplate(
				"{{#template PACKAGE {{parent.packagename}}}}Hello {{#endtemplate}}");

		Clazz person = new Clazz("de.uniks.Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());

		template.generate(model, templateFile, name);

		assertEquals("Hello ", templateFile.toString());
	}

	@Test
	public void testGeneratorTemplateFragment() {
		Template template = new Template().withType(TemplateParser.DECLARATION).withTemplate(
				"{{#template PACKAGE {{parent.packagename}}}}Hello {{#endtemplate}}","",
				"{{#template IMPORT}}World{{#endtemplate}}");
		Clazz person = new Clazz("de.uniks.Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());

		template.generate(model, templateFile, name);

		assertEquals("Hello World", templateFile.toString());
	}


	@Test
	public void testGeneratorTemplateFragmentIfCondition() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Template template = new Template().withType(TemplateParser.DECLARATION)
				.withTemplate("{{#template FIELD {{file.clazz.type}}!=interface}}Hello{{#endtemplate}}");
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());
		model.withTemplate(new IfCondition().withKey(IfCondition.IFNOT));


		template.generate(model, templateFile, person);

		assertEquals("Hello", templateFile.toString());
	}

	@Test
	public void testImport() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz student = classModel.createClazz("Student");
		Clazz uni = classModel.createClazz("Uni");
		Association assoc = Association.create(student, uni);
		assoc.with("uni");
		assoc.getOther().with("studs");
		Template template = new Template().withType(TemplateParser.DECLARATION)
				.withTemplate("{{#import {{other.clazz.packageName}}.{{member.other.clazz.name}}Set}}");
		TemplateResultFile templateFile = new TemplateResultFile(student, true);
		TemplateResultModel model = new TemplateResultModel();
		ModelGenerator modelGenerator = new ModelGenerator();
		model.withTemplate(modelGenerator.getCondition());


		TemplateResultFragment fragment = template.generate(model, templateFile, assoc);

		assertEquals("de.uniks.test.model.UniSet", fragment.getHeaders().first());
	}
	@Test
	public void testAttribute() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		Template template = new Template().withType(TemplateParser.DECLARATION)
				.withTemplate("{{#if {{member.type#sub(0,10)}}==SimpleSet<}}{{#import " + SimpleSet.class.getName() + "}}{{#endif}}");


		ModelGenerator generator = new ModelGenerator();
		TemplateResultFragment fragment;
		fragment = generator.parseTemplate(template, name);
		assertEquals("", fragment.getResult().trim().toString());
		assertNull(fragment.getHeaders());

		name.with(DataTypeSet.create(String.class));
		fragment = generator.parseTemplate(template, name);
		assertEquals("(de.uniks.networkparser.list.SimpleSet)", fragment.getHeaders().toString());
	}

}
