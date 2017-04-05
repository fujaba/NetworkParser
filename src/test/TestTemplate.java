package test;

import org.junit.Test;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.template.Template;


public class TestTemplate {
	@Test
	public void testTemplateFeatureCondition() {
		Template template=new Template();
		String templateString ="{{#feature SETCLASS=SimpleSet}}"+BaseItem.CRLF;
	   ObjectCondition parsing = template.parsing(StringCondition.create(templateString), false);
	   

	   System.out.println(parsing);
	   template.withCondition(templateString);
	   template.generate(null, null);
	}
	@Test
	public void testTemplateERROR() {
		Template template=new Template();
		String templateString ="   {{methodVisibility}} {{getModifiers}}{{#if getModifiers}} {{#endif}}{{value}} {{getName}}{{Name}}(){{methodEnd}}"+BaseItem.CRLF
			 	  +"{{#ifnot methodEnd}}"+BaseItem.CRLF
			 	  +"   {"+BaseItem.CRLF
			 	  +"      return this.{{name}};"+BaseItem.CRLF
			 	  +"   }"+BaseItem.CRLF
			 	  +"{{#endif}}"+BaseItem.CRLF;
	   ObjectCondition parsing = template.parsing(StringCondition.create(templateString), false);
	   System.out.println(parsing);
	}

	
	@Test
	public void testTemplateCondition() {
		Template template=new Template();
		ObjectCondition parsing = template.parsing(StringCondition.create("{{T}}"), false);
		System.out.println(parsing);
		
		SimpleKeyValueList<String, String> parameters = new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class);
		System.out.println(parsing.update(template));
		System.out.println(parsing.update(parameters));
		
		parsing = template.parsing(StringCondition.create("{{G}}"), false);
		System.out.println(parsing.update(parameters));
		
		parsing = template.parsing(StringCondition.create("{{#if T}}"), false);
		System.out.println(parsing.update(parameters));
		
		parsing = template.parsing(StringCondition.create("{{#if G}}"), false);
		System.out.println(parsing.update(parameters));
	}
	@Test
	public void testTemplateSimple() {
		Template template=new Template();
		template.withTemplate("{{T}}");
		
		String generate =  template.generate(null, null);
		System.out.println("#"+generate+"#");
		
	}
	
	@Test
	public void testTemplate() {
		Template template=new Template();
		template.withTemplate("{{{T}}}");
		
		template.generate(null, null);
		
	}

	@Test
	public void testTemplatePlusIf() {
		Template template=new Template();
		template.withTemplate("{{T} }");
		
		String generate = template.generate(new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class), null);
		System.out.println("#"+generate+"#");
	}
	
	@Test
	public void testTemplateError() {
		Template template=new Template();
		String generate;
		
		template.withTemplate("{{T}}{{#if G}}false{{#else}} {{#endif}}Welt");
		SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class);
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");
	}
	
	@Test
	public void testTemplateError2() {
		Template template=new Template();
		String generate;
		
		template.withTemplate("{{T}}{{#if T}}{{T}}{{#endif}}Welt");
		SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>().withKeyValueString("T:Hello", String.class);
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");
	}
	
	@Test
	public void testTemplateError3() {
		Template template=new Template();
		String generate;
		
		template.withTemplate("{{#ifnot methodEnd}}",
			 	  "   {",
			 	  "      return this.{{name}};",
			 	  "   }",
			 	  "{{#endif}}");

		SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>().withKeyValueString("methodEnd:,name:testName", String.class);
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");
	}
	
	@Test
	public void testTemplatePlusIfExtra() {
		Template template=new Template();
		template.withTemplate("{{T}}{{#if G}} {{#endif}}Welt");
		String generate;
		
		SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>().withKeyValueString("T: Hello", String.class);
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");
		
		template.withTemplate("{{T}}{{#if T}} {{#endif}}Welt");
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");

		
		template.withTemplate("{{T}}{{#if G}}false{{#else}} {{#endif}}Welt");
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");

		template.withTemplate("{{T}}{{#ifnot G}} {{#endif}}Welt");
		generate = template.generate(variables, null);
		System.out.println("#"+generate+"#");
		
	}


}
