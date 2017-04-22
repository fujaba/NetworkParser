package de.uniks.template.generator.java;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSetAttribute extends BasicGenerator {

	public JavaSetAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"   {{methodVisibility}} {{listType}} get{{Value}}()",
				"   {",
				"      {{listType}} result = new {{listType}}();",
				"      for ({{name}} obj : this)",
				"      {",
				"         result.add(obj.{{getMethodType}}{{Value}}());",
				"      }",
				"      return result;",
				"   }","",

				"   {{methodVisibility}} {{SetName}} filter{{Value}}({{type}} value)",
				"   {",
				"      {{SetName}} result = new {{SetName}}();",
				"      for({{name}} obj : this)",
				"      {",
				"         if ({{#if {{member.type}}==BOOLEAN}} value == obj.is{{Name}}(){{#else}}{{#if {{member.type}}==OBJECT}}value.equals(obj.get{{Name}}()){{#else}}value == obj.get{{Name}}(){{#endif}}{{#endif}}{{filterCondition}})",
				"         {",
				"            result.add(obj);",
				"         }",
				"      }",
				"      return result;",
				"   }","",
				
				"{{#if {{member.type}}==VALUETYPE}}",
				"   {{methodVisibility}} {{SetName}} filter{{Value}}({{type}} lower, {{type}} upper)",
				"   {",
				"      {{SetName}} result = new {{SetName}}();",
				"      for ({{name}} obj : this)",
				"      {",
				"         if (lower{{#if {{member.type}}==PRIMITIVE}} <= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) <= 0{{#endif}} && upper{{#if {{member.type}}==PRIMITIVE}} >= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) >= 0{{#endif}})",
				"         {",
				"            result.add(obj);",
				"         }",
				"      }",
				"      return result;",
				"   }","",
				"{{#endif}}",

				"   {{methodVisibility}} {{SetName}} with{{Value}}({{type}} value)",
				"   {",
				"      for ({{name}} obj : this)",
				"      {",
				"         obj.set{{Value}}(value);",
				"      }",
				"      return this;",
				"   }");
	}
	
	@Override
	public Class<?> getTyp() {
		return null;
	}

	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		return null;
	}

}
