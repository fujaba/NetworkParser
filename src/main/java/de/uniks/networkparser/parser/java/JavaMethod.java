package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.parser.Template;

/**
 * The Class JavaMethod.
 *
 * @author Stefan
 */
public class JavaMethod extends Template {
	
	/**
	 * Instantiates a new java method.
	 */
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
