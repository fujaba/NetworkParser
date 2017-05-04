package de.uniks.template.generator.java;

import de.uniks.networkparser.graph.Method;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaMethod extends BasicGenerator {

	public JavaMethod() {
		createTemplate("Method", Template.METHOD,
				"   {{visibility}} {{modifiers} }{{returnType}} {{name}}{{parameter}}{{#if {{file.member.type}}==INTERFACE}};","","{{#else}}",
				"   {",
				"      {{#methodbody}}",
				"   }","",
				"{{#endif}}");
	}

	@Override
	public Class<?> getTyp() {
		return Method.class;
	}
}
