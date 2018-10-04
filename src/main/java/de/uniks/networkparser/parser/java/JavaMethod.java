package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.parser.Template;

public class JavaMethod extends Template {
	public JavaMethod() {
		this.id = "method";
		this.type = METHOD;
		this.withTemplate("{{#foreach {{parameter}}}}"
				+ "{{#if {{#AND}}{{item.typeClazz.type}}==class {{#NOT}}{{item.packagename}}=={{file.member.packagename}}{{#ENDNOT}}{{#ENDAND}}}}"
				+ "{{#import {{item.type(false)}}}}" + "{{#endif}}" + "{{#endfor}}"
				+ "   {{visibility}} {{modifiers} }{{returnType}} {{name}}{{parameterName}}{{#if {{file.member.type}}==interface}};",
				"", "{{#else}}", "{{#methodbody}}", "", "{{#endif}}");
	}
}
