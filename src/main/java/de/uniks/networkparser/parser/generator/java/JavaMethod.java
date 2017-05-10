package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaMethod extends BasicGenerator {

	public JavaMethod() {
		createTemplate("Method", Template.METHOD,
				"   {{visibility}} {{modifiers} }{{returnType}} {{name}}{{parameter}}{{#if {{file.member.type}}==interface}};","","{{#else}}",
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
