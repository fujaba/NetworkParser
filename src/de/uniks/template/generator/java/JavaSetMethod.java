package de.uniks.template.generator.java;

import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSetMethod extends BasicGenerator {

	public JavaSetMethod() {
		createTemplate("Declaration", Template.DECLARATION, 
				"   {{visibility}} {{modifiers}}{{#if modifiers}} {{#endif}}{{setName}} {{name}}( {{parameters}} )",
				"   {",
				"      return {{setName}}.EMPTY_SET;",
				"   }","");
	}
	
	@Override
	public Class<?> getTyp() {
		return null;
	}
}
