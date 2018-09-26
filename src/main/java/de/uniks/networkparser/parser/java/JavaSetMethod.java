package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.parser.Template;

public class JavaSetMethod extends Template {
	public JavaSetMethod() {
		this.id = "method";
		this.type = METHOD;
		this.withTemplate(
				"{{#foreach {{parameter}}}}" +
					"{{#if {{#AND}}{{item.typeClazz.type}}==class {{file.member.name}}{{#ENDAND}}}}" +
						"{{#import {{item.type(false)}}}}" +
					"{{#endif}}" +
				"{{#endfor}}" +
				"	{{visibility}} {{modifiers} }{{file.member.name}}Set {{name}}{{parameterName}} {",
				"		return {{file.member.name}}Set.EMPTY_SET;",
				"	}","");
	}
}
