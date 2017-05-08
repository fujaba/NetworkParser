package de.uniks.template.generator.java;

import de.uniks.networkparser.graph.Method;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSetMethod extends BasicGenerator {

	public JavaSetMethod() {
		createTemplate("Method", Template.METHOD, 
				"   {{visibility}} {{modifiers} }{{file.member.name}}Set {{name}}( {{parameters}} )",
				"   {",
				"      return {{file.member.name}}Set.EMPTY_SET;",
				"   }","");
	}
	
	@Override
	public Class<?> getTyp() {
		return Method.class;
	}
}
