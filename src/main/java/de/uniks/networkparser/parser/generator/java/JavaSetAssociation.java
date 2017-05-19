package de.uniks.networkparser.parser.generator.java;

import java.util.Collections;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaSetAssociation extends BasicGenerator {

	public JavaSetAssociation() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}",
				"{{#ifnot {{other.isEdge}}}}",
				"{{#import {{file.member.fullName}}}}" +
				"   public {{file.member.name}}Set get{{other.Name}}()",
				"   {",
				"      {{file.member.name}}Set result = new {{file.member.name}}Set();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         result.with(obj.get{{other.Name}}());",
				"      }",
				"      return result;",
				"   }","","",
				
				"{{#import " + ObjectSet.class.getName() + "}}" +
				"{{#if {{other.cardinality}}==n}}{{#import " + Collections.class.getName() + "}}{{#endif}}" +
				"   public {{file.member.name}}Set filter{{other.Name}}(Object value)",
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
				"      {{file.member.name}}Set answer = new {{file.member.name}}Set();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         if ({{#if {{other.cardinality}}==1}}neighbors.contains(obj.get{{other.Name}}()) || (neighbors.isEmpty() && obj.get{{other.Name}}() == null){{#else}}! Collections.disjoint(neighbors, obj.get{{other.Name}}()){{#endif}})",
				"         {",
				"            answer.add(obj);",
				"         }",
				"      }",
				"      return answer;",
				"   }","",
				
				"   public {{file.member.name}}Set with{{other.Name}}({{other.clazz.name}} value)",
				"   {",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         obj.with{{other.Name}}(value);",
				"      }",
				"      return this;",
				"   }","",
				
				"{{#import {{other.clazz.fullName}}}}" +
				"{{#if {{other.cardinality}}==MANY}}",
				"   public {{file.member.name}}Set without{{other.Name}}({{other.clazz.name}} value)",
				"   {",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         obj.without{{other.Name}}(value);",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}{{#endtemplate}}");
	}
	
	@Override
	public Class<?> getTyp() {
		return Association.class;
	}
}
