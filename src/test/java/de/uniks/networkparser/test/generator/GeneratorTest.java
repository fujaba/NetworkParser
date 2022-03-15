package de.uniks.networkparser.test.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultModel;

public class GeneratorTest {

	@Test
	public void testOr() {
		String value ="{{#if {{#OR}}{{item.type}}==class {{item.type}}==interface{{#ENDOR}}}}{{#endif}}";
		Template template = new Template("Declaration").withType(Template.DECLARATION);
		template.withTemplate(value);


		ModelGenerator generator = new ModelGenerator();
		TemplateResultModel resultModel = generator.getResultModel();


		template.generate(resultModel, null, new Attribute("name", DataType.STRING));
		assertEquals("0: Declaration", template.toString());

	}
}
