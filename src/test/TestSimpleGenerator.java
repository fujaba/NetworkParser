package test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.template.Template;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.generator.ModelGenerator;

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
				"{{#ifnot {{member.type}}==INTERFACE}}"+
								"Hello World" +
							"{{#endif}}");
		
		Clazz person = new Clazz("Person");
		Attribute name = person.createAttribute("name", DataType.STRING);
		TemplateResultFile templateFile = new TemplateResultFile(person, true);
		TextItems textItems = new TextItems();
		
		template.generate(templateFile, textItems, name);
		
		Assert.assertEquals("Hello World", templateFile.toString());
	}
}
