package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaSetAttribute extends BasicGenerator {

	public JavaSetAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}   public {{#listType}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}()",
				"   {",
				"      {{#listType}} result = new {{#listType}}();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         result.add(obj.{{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}());",
				"      }",
				"      return result;",
				"   }","",

				"   public {{file.member.name}}Set filter{{Name}}({{type}} value)",
				"   {",
				"      {{file.member.name}}Set result = new {{file.member.name}}Set();",
				"      for({{file.member.name}} obj : this)",
				"      {",
				"         if ({{#if {{type}}==BOOLEAN}} value == obj.is{{Name}}(){{#else}}{{#if {{type}}==OBJECT}}value.equals(obj.get{{Name}}()){{#else}}value == obj.get{{Name}}(){{#endif}}{{#endif}})",
				"         {",
				"            result.add(obj);",
				"         }",
				"      }",
				"      return result;",
				"   }","",
				
				"{{#if {{type#sub(0,19}}==SimpleKeyValueList<}}{{#import " + SimpleKeyValueList.class.getName() + "}}{{#endif}}" +
				"{{#if {{type}}==VALUETYPE}}",
				"   public {{SetName}} filter{{Value}}({{type}} lower, {{type}} upper)",
				"   {",
				"      {{SetName}} result = new {{SetName}}();",
				"      for ({{name}} obj : this)",
				"      {",
				"         if (lower{{#if {{type}}==PRIMITIVE}} <= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) <= 0{{#endif}} && upper{{#if {{member.type}}==PRIMITIVE}} >= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) >= 0{{#endif}})",
				"         {",
				"            result.add(obj);",
				"         }",
				"      }",
				"      return result;",
				"   }","",
				"{{#endif}}",

				"   public {{file.member.name}}Set with{{Name}}({{type}} value)",
				"   {",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         obj.set{{Name}}(value);",
				"      }",
				"      return this;",
				"   }","","","{{#endtemplate}}");
	}
	
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}
}
