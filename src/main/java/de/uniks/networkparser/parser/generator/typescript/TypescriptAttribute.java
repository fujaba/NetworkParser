package de.uniks.networkparser.parser.generator.typescript;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class TypescriptAttribute extends BasicGenerator {
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}

	// TODO adjust java based attribute names to Number, String and boolean
	// TODO fix or statements to allow {{}}== {{}}== pattern
	public TypescriptAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
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
