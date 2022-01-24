package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.parser.Template;

public class JavaAttribute extends Template {
	public JavaAttribute() {
		this.id = "attribute";
		this.type = DECLARATION;
		this.withTemplate("{{#template VALUE}}	public static final String PROPERTY_{{NAME}} = \"{{name}}\";", "",
				"{{#ifnot {{file.member.type}}==interface}}",
				"	{{annotation}}",
				"	{{visibility}} {{modifiers} }{{type} }{{name}}{{#if {{value}}}} = {{value}}{{#endif}};",
				"",
				"{{#endif}}",
				"",
				"{{#import {{type(false)}}}}" 
			+	"{{#foreach {{parent.parent.child}}}}"
					+ "{{#if {{#and}}{{item.type}}==class {{item.name}}=={{type}}{{#endand}}}}"
						+ "{{#ifnot {{item.name}}=={{file.member.name}}}}" 
							+ "{{#import {{item.fullName}}}}"
						+ "{{#endif}}" 
					+ "{{#endif}}"
			+	"{{#endfor}}"
					
/* Getter */
			+	"	{{annotation(getter)}}",
				"	public {{modifiers} }{{type}} {{#if {{type}}==boolean ?is:get}}{{Name}}(){{#if {{file.member.type}}==interface}};",
				"", 
					"{{#endif}}",
					"{{#ifnot {{file.member.type}}==interface}} {",
					"		return {{this}}.{{name}};",
					"	}",
					"",
					"{{#endif}}",
				"",
/* ADD TO */
				"{{#if {{typecat}}==SET}}",
				"	public boolean add{{Name}}({{type.name}}... values) {",
				"		if(values == null  || values.length < 1) {",
				"			return false;",
				"		}",
				"		if(this.{{name}} == null) {",
				"			this.{{name}} = new {{type}}();",
				"		}",
				"		for(int i=0;i<values.length;i++) {",
				"			this.{{name}}.add(values[i]);",
				"		}",
				"		return true;",
				"	}",
				"",
				"{{#endif}}",

				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"
					+ "	public {{modifiers} }boolean set{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}} {",
				"		if ({{this}}.{{name}} != value) {",
				"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{file.member.type}}==!enum{{#ENDAND}}}}",
				"			{{type}} oldValue = {{this}}.{{name}};",
				"			{{this}}.{{name}} = value;",
				"			firePropertyChange(PROPERTY_{{NAME}}, oldValue, value);",
				"{{#else}}",
				"			{{this}}.{{name}} = value;",
				"{{#endif}}",
				"			return true;", "		}",
				"		return false;", "	}",
				"",
				"{{#endif}}",
				"	public {{modifiers} }{{file.member.name}} with{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}} {",
				"		set{{Name}}(value);",
				"		return this;",
				"	}",
				"",
				"{{#endif}}",
				"",
				"{{#endif}}{{#endtemplate}}");
	}
}
