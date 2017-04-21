package test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.TemplateResultFragment;
import de.uniks.template.TemplateResultModel;
import de.uniks.template.generator.ModelGenerator;
import de.uniks.template.generator.Template;

public class TestSimpleGenerator {

	@Test
	public void testGenerator() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		ModelGenerator javaModelFactory = new ModelGenerator();
		javaModelFactory.generate("src", classModel);
//		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
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
	
	@Test
	public void testGeneratorTemplateFragment() {
		Template template = new Template().withType(TemplateParser.DECLARATION).withTemplate(
				"{{#template PACKAGE}}Hello {{#endtemplate}}","",
				"{{#template IMPORT}}World{{#endtemplate}}");

		Clazz person = new Clazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TemplateResultModel model = new TemplateResultModel();
		model.withTemplate(new TemplateFragmentCondition());
		
		
		TemplateResultFragment generate = template.generate(model, templateFile, name);
		
		Assert.assertEquals("Hello World", templateFile.toString());
	}
	
}
