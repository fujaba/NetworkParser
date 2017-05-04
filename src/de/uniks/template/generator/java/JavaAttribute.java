package de.uniks.template.generator.java;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaAttribute extends BasicGenerator{

	public JavaAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}   public static final String PROPERTY_{{NAME}} = \"{{name}}\";","",
				//,"{{#template FIELD {{#ifnot {{file.clazz.type}}==interface}}}}   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} = {{default}}{{#endif}}{{#endtemplate}}"

				"{{#ifnot {{file.member.type}}==interface}}",
				"   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if {{value}}}} = {{value}}{{#endif}};","",
				"{{#endif}}","",

				"{{#foreach {{member.parent.classmodel.clazzes}}}}" +
				   "{{#ifnot {{item.name}}=={{file.clazz.name}}" +
				      "{{#if {{item.name}}=={{member.type}}}}" +
				         "{{#import item.name}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   public {{modifiers} }{{type}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      return this.{{name}};",
				"   }","",
				"{{#endif}}",
				"   public {{modifiers} }void set{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      if (this.{{name}} != value)",
				"      {",
				"         {{type}} oldValue = this.{{name}};",
				"         this.{{name}} = value;",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"         firePropertyChange(PROPERTY_{{NAME}}, oldValue, value);",
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
				"{{#endif}}{{#endtemplate}}");
		
	}
	
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}
}
