package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.parser.Template;

public class JavaAttribute extends Template {
	public JavaAttribute() {
		this.id = "attribute";
		this.type = DECLARATION;
		this.withTemplate("{{#template VALUE}}	public static final String PROPERTY_{{NAME}} = \"{{name}}\";", "",
				// ,"{{#template FIELD {{#ifnot {{file.clazz.type}}==interface}}}}
				// {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} =
				// {{default}}{{#endif}}{{#endtemplate}}"
				"{{#ifnot {{file.member.type}}==interface}}",
				"	{{visibility}} {{modifiers} }{{type} }{{name}}{{#if {{value}}}} = {{value}}{{#endif}};", "",
				"{{#endif}}", "",
				"{{#import {{type(false)}}}}" + "{{#foreach {{parent.parent.child}}}}"
						+ "{{#if {{#and}}{{item.type}}==class {{item.name}}=={{type}}{{#endand}}}}"
						+ "{{#ifnot {{item.name}}=={{file.member.name}}}}" + "{{#import {{item.fullName}}}}"
						+ "{{#endif}}" + "{{#endif}}" + "{{#endfor}}"
						+ "	public {{modifiers} }{{type}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}(){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {", "		return {{this}}.{{name}};",
				"	}", "", "{{#endif}}", "",
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"
						+ "	public {{modifiers} }boolean set{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {",
				"		if ({{this}}.{{name}} != value) {",
				"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{file.member.type}}==!enum{{#ENDAND}}}}",
				"			{{type}} oldValue = {{this}}.{{name}};", "			{{this}}.{{name}} = value;",
				"			firePropertyChange(PROPERTY_{{NAME}}, oldValue, value);", "{{#else}}",
				"			{{this}}.{{name}} = value;", "{{#endif}}", "			return true;", "		}",
				"		return false;", "	}", "", "{{#endif}}",
				"	public {{modifiers} }{{file.member}} with{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {", "		set{{Name}}(value);",
				"		return this;", "	}", "", "{{#endif}}", "", "{{#endif}}{{#endtemplate}}");
	}
}
