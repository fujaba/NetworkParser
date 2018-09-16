package de.uniks.networkparser.parser.typescript;

import de.uniks.networkparser.parser.Template;

public class TypescriptAttribute extends Template {
	// TODO adjust java based attribute names to Number, String and boolean
	// TODO fix or statements to allow {{}}== {{}}== pattern
	public TypescriptAttribute() {
		this.id = "attribute";
		this.withTemplate(
				"{{#template VALUE}}   static PROPERTY_{{NAME}}: String = \"{{name}}\";","","",
				"{{#foreach {{parent.parent.child}}}}" +
				   //"{{#if {{#OR}}{{item.type}}==class {{item.type}}==interface{{#ENDOR}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#if {{item.name}}=={{type}}}}" +
				            "{{#import {{item.name}}}}" +
				         "{{#endif}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   {{modifiers} }{{name}}: {{type}}{{#ifnot {{file.member.type}}==interface}}{{#if {{value}}}} = {{value}}{{#endif}}{{#endif}};","","","{{#endtemplate}}");
	}
}
