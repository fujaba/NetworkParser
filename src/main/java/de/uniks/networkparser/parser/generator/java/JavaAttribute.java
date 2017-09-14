package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaAttribute extends BasicGenerator{

	public JavaAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}   public static final String PROPERTY_{{NAME}} = \"{{name}}\";","",
				//,"{{#template FIELD {{#ifnot {{file.clazz.type}}==interface}}}}   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} = {{default}}{{#endif}}{{#endtemplate}}"

				"{{#ifnot {{file.member.type}}==interface}}",
				"   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if {{value}}}} = {{value}}{{#endif}};","",
				"{{#endif}}","",
				"{{#import {{type(false)}}}}" +
				"{{#foreach {{parent.parent.child}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#if {{item.name}}=={{type}}}}" + 
				            "{{#import {{item.fullName}}}}" +
				         "{{#endif}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   public {{modifiers} }{{type}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      return {{this}}.{{name}};",
				"   }","",
				"{{#endif}}","",
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"+
					"   public {{modifiers} }void set{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
					"{{#ifnot {{file.member.type}}==interface}}",
					"   {",
					"      if ({{this}}.{{name}} != value)",
					"      {",
					"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{#DEBUG}}{{file.member.type}}==!enum{{#ENDAND}}}}"+
					"         {{type}} oldValue = {{this}}.{{name}};",
					"         {{this}}.{{name}} = value;",
					"         firePropertyChange(PROPERTY_{{NAME}}, oldValue, value);",
					"{{#else}}",
					"         {{this}}.{{name}} = value;",
					"{{#endif}}",
					"      }",
					"   }","",
					"{{#endif}}",
					"   public {{modifiers} }{{file.member}} with{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
					"{{#ifnot {{file.member.type}}==interface}}",
					"   {",
					"      set{{Name}}(value);",
					"      return this;",
					"   }","",
				"{{#endif}}","",
				"{{#endif}}{{#endtemplate}}");
		
	}
	
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}
}
