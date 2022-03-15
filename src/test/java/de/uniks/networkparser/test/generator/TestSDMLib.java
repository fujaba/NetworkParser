package de.uniks.networkparser.test.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.ModifyEntry;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.SymTabEntry;
import de.uniks.networkparser.parser.TemplateResultFile;

public class TestSDMLib {
	@Test
	public void testSDMLibModification() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.sdmLib");
		ModelGenerator generator = model.getGenerator();

		Clazz person = model.createClazz("Person");
		Attribute nameAttribute = person.createAttribute("name", DataType.STRING);
		Method eatMethod = person.createMethod("eat");

		// Generate and override SourceCode
		generator.removeAndGenerate("java");

		assertEquals(1, person.getAttributes().size());
		assertEquals(1, person.getMethods().size());


		person.createAttribute("age", DataType.INT);
		person.createMethod("go");

		assertEquals(2, person.getAttributes().size());
		assertEquals(2, person.getMethods().size());


		// Change SourceCode
		TemplateResultFile templateResult = TemplateResultFile.createJava(person);
		ParserEntity parser = generator.parse("build/gen/java", templateResult);
		SymTabEntry entry = parser.getSymbolEntry(SymTabEntry.TYPE_METHOD, "eat");
		if(entry != null) {
			entry.writeBody("System.out.println(\"I am eating\");");
			generator.write("build/gen/java", templateResult);
		}

		person.remove(nameAttribute);
		person.remove(eatMethod);

		assertEquals(1, person.getAttributes().size());
		assertEquals(1, person.getMethods().size());


		//model.generate("src/test/java");
		generator.generating("build/gen/java", model, null, ModelGenerator.TYPE_JAVA, true, true);

		// Create a Person with name and age Attribute
		// and eat and go Method
		assertEquals(3, person.getAttributes().size());
//		MethodSet methods = 
				person.getMethods();
//FIXME		assertEquals(2, methods.size());


		//Add Remove Modifier
		GraphUtil.setModifierEntry(person, ModifyEntry.createDelete(eatMethod));


		// Remove Element from SourceCode
		generator.generating("build/gen/java", model, null, ModelGenerator.TYPE_JAVA, true, true);
	}
}
