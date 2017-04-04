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
		
		template.generate(null);
	}
	
	@Test
	public void testTemplatePlusIfExtra() {
		Template template=new Template();
		template.withTemplate("{{T}}{{#if T}} {{#endif}}Welt");
		
		String generate = template.generate(new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class));
		System.out.println("#"+generate+"#");
		
	}


}
