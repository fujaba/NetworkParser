package de.uniks.template.generator.java;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaAttribute extends BasicGenerator{

	public JavaAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template FIELD}}   public static final {{type} }PROPERTY_{{NAME}} = \"{{name}}\";\r\n{{#endtemplate}}"
				//,"{{#template FIELD {{#ifnot {{file.clazz.type}}==INTERFACE}}}}   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} = {{default}}{{#endif}}{{#endtemplate}}"
				 );
		createTemplate("Field", Template.FIELD, "" +
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} = {{default}}{{#endif}};","",
				"{{#endif}}");

		createTemplate("Declaration", Template.DECLARATION, "" +
				"{{#foreach {{member.parent.classmodel.clazzes}}}}" +
				   "{{#ifnot {{item.name}}=={{file.clazz.name}}" +
				      "{{#if {{item.name}}=={{member.type}}}}" +
				         "{{#import item.name}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   {{methodVisibility}} {{getModifiers}}{{#if getModifiers}} {{#endif}}{{value}} {{getName}}{{Name}}(){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      return this.{{name}};",
				"   }","",
				"{{#endif}}",
				"   {{methodVisibility}} {{setModifiers}}{{#if setModifiers}} {{#endif}}void set{{Name}}({{value}} value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      if (this.{{name}} != value)",
				"      {",
				"         {{value}} oldValue = this.{{name}};",
				"         this.{{name}} = value;",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"         firePropertyChange(PROPERTY_{{PROPERTY_NAME}}, oldValue, value);",
				"{{#endif}}",
				"      }",
				"   }","",
				"{{#endif}}",
				"   {{methodVisibility}} {{withModifiers}}{{#if withModifiers}} {{#endif}}{{withReturn}} with{{Name}}({{value}} value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      set{{Name}}(value);",
				"      return this;",
				"   }","",
				"{{#endif}}");
		
	}
	
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}
}
