package de.uniks.template.generator.java;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaMethod extends BasicGenerator {

	public JavaMethod() {
		createTemplate("Declaration", Template.DECLARATION, "" +
				"   {{visibility}} {{modifiers}}{{#if modifiers}} {{#endif}}{{returnType}} {{name}}( {{parameters}} ){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      {{body}}",
				"   }","",
				"{{#endif}}");
	}

	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof Method == false) {
			return null;
		}
		Method element = (Method) item;
		return null;
	}

	@Override
	public Class<?> getTyp() {
		return Method.class;
	}
	
}
