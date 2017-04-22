package de.uniks.template.generator.java;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSetAssociation extends BasicGenerator {

	public JavaSetAssociation() {
		createTemplate("Declaration", Template.DECLARATION,
				"   {{methodVisibility}} {{otherSetName}} get{{OtherValue}}()",
				"   {",
				"      {{otherSetName}} result = new {{otherSetName}}();",
				"      for ({{name}} obj : this)",
				"      {",
				"         result.with(obj.get{{OtherValue}}());",
				"      }",
				"      return result;",
				"   }",""
				
				+ "{{#import" + ObjectSet.class + "}}" +
				"   {{methodVisibility}} {{setName}} filter{{OtherValue}}(Object value)",
				"   {",
				"      ObjectSet neighbors = new ObjectSet();",
				"      if (value instanceof Collection)",
				"      {",
				"         neighbors.addAll((Collection<?>) value);",
				"      }",
				"      else",
				"      {",
				"         neighbors.add(value);",
				"      }",
				"      {{setName}} answer = new {{setName}}();",
				"      for ({{name}} obj : this)",
				"      {",
				"         if ({{#if {{member.other.cardinality}}==ONE}}neighbors.contains(obj.get{{member.other.name}}()) || (neighbors.isEmpty() && obj.get{{member.other.name}}() == null){{#else}}! Collections.disjoint(neighbors, obj.get{{member.other.name}}()){{#endif}})",
				"         {",
				"            answer.add(obj);",
				"         }",
				"      }",
				"      return answer;",
				"   }","",
				
				"   {{methodVisibility}} {{setName}} with{{OtherValue}}({{otherName}} value)",
				"   {",
				"      for ({{name}} obj : this)",
				"      {",
				"         obj.with{{OtherValue}}(value);",
				"      }",
				"      return this;",
				"   }","",
				
				"{{#if {{member.other.cardinality}}==MANY}}",
				"   {{methodVisibility}} {{setName}} without{{OtherValue}}({{otherName}} value)",
				"   {",
				"      for ({{name}} obj : this)",
				"      {",
				"         obj.without{{OtherValue}}(value);",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}");
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
