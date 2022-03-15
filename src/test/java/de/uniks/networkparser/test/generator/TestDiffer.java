package de.uniks.networkparser.test.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.parser.GraphMatcher;
import de.uniks.networkparser.parser.TemplateResultFragment;

public class TestDiffer {
	@Test
	public void testBasicAttributeAdd() {
		ClassModel oldModel = new ClassModel();
		Clazz oldUniversity = oldModel.createClazz("University");
		oldUniversity.createAttribute("name", DataType.INT);
		
//		oldModel.generate("src/test/java");

		ClassModel model = new ClassModel();
		Clazz university = model.createClazz("University");
		university.createAttribute("name", DataType.STRING);

		GraphMatcher matcher = new GraphMatcher(oldModel, model);
//		matcher.generate();
		GraphSimpleSet diffs = matcher.getDiffs();
		assertEquals(diffs.size(), 1);

		 // SOLUTION 1
			GraphConverter converter = new GraphConverter();
			TemplateResultFragment fragment = converter.convertToAdvanced(TemplateResultFragment.create(diffs, true, true));
	
			CharacterBuffer value = fragment.getValue();
			assertNotNull(value);
		
		// SOLUTION 2
			ClassModel gen = new ClassModel();
			gen.add(diffs);
//			gen.generate("src/test/java");
		
		
//		Differ differ = new Differ();
//		List<Match> diffs = differ.diff(oldModel, model);
//		
//		assertEquals(1, diffs.size());
//		Match diff = diffs.get(0);
//		assertEquals(SendableEntityCreator.NEW, diff.getType());
//		assertEquals(oldUniversity, diff.getMatch());
//		assertEquals(null, diff.getOldValue());
//		assertEquals(attribute, diff.getNewValue());
//	
//		GraphModel newModel = differ.generateAdvance(diffs, oldModel);
//		assertEquals(1, oldUniversity.getAttributes().size());
//		Attribute generatedAttribute = oldUniversity.getAttributes().get(0);
//		assertEquals(attribute.getName(), generatedAttribute.getName());
//		assertEquals(attribute.getType(), generatedAttribute.getType());
//		assertEquals(attribute.getModifier().toString(), generatedAttribute.getModifier().toString());
//		
//		DiffGenerator diffGenerator = new DiffGenerator(null);
//		String code = diffGenerator.generateNewVersion(newModel, "testBasicAttributeAdd", "src/main/java", "src/test/java");
//		assertEquals("" +
//				"	private void testBasicAttributeAddNewVersion()\n" +
//				"	{\n" +
//				"		de.uniks.networkparser.ext.ClassModel model = new de.uniks.networkparser.ext.ClassModel();\n" +
//				"		\n" +
//				"		de.uniks.networkparser.graph.Clazz university = model.createClazz(\"University\");\n" +
//				"		de.uniks.networkparser.graph.Attribute university_name = university.createAttribute(\"name\", de.uniks.networkparser.graph.DataType.STRING);\n" +
//				"		university_name.with(de.uniks.networkparser.graph.Modifier.create(\"static\"));\n" +
//				"		model.generate(\"src/main/java\");\n" +
//				"	}\n\n", code);
	}
}
