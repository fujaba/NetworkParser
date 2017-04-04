package test;

import org.junit.Test;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.template.Template;


public class TestTemplate {

	@Test
	public void testTemplateSimple() {
		Template template=new Template();
		template.withTemplate("{{T}}");
		
		template.generate(null);
		
	}
	
	@Test
	public void testTemplate() {
		Template template=new Template();
		template.withTemplate("{{{T}}}");
		
		template.generate(null);
		
	}

	@Test
	public void testTemplatePlusIf() {
		Template template=new Template();
		template.withTemplate("{{T} }");
		
//		String generate = template.generate(new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class));
//		System.out.println("#"+generate+"#");
	}
	
	@Test
	public void testTemplatePlusIfExtra() {
		Template template=new Template();
		template.withTemplate("{{T}}{{#if G}} {{#endif}}Welt");
		String generate;
		
		SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class);
		generate = template.generate(variables);
		System.out.println("#"+generate+"#");
		
		template.withTemplate("{{T}}{{#if T}} {{#endif}}Welt");
		generate = template.generate(variables);
		System.out.println("#"+generate+"#");

		
		template.withTemplate("{{T}}{{#if G}}false{{#else}} {{#endif}}Welt");
		generate = template.generate(variables);
		System.out.println("#"+generate+"#");

		template.withTemplate("{{T}}{{#ifnot G}} {{#endif}}Welt");
		generate = template.generate(variables);
		System.out.println("#"+generate+"#");
		
	}


}
