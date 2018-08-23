package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.parser.BasicGenerator;
import de.uniks.networkparser.parser.Template;

public class JavaSetAssociation extends BasicGenerator {
	public JavaSetAssociation() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}",
				"{{#if {{other.isImplements}}==false}}",
				"{{#import {{file.member.fullName}}}}" +
				"   public {{other.clazz.name}}Set get{{other.Name}}()",
				"   {",
				"      {{other.clazz.name}}Set result = new {{other.clazz.name}}Set();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
						"{{#if {{other.cardinality}}==1}}",
				"         result.add(obj.get{{other.Name}}());",
						"{{#else}}",
				"         result.addAll(obj.get{{other.Name}}());",
						"{{#endif}}",
				"      }",
				"      return result;",
				"   }","","",

				"{{#import " + ObjectSet.class.getName() + "}}" +
				"   public {{file.member.name}}Set filter{{other.Name}}(Object value)",
				"   {",
				"      ObjectSet neighbors = new ObjectSet().init(value);",
				"      {{file.member.name}}Set answer = new {{file.member.name}}Set();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         if ({{#if {{other.cardinality}}==1}}neighbors.contains(obj.get{{other.Name}}()) || (neighbors.isEmpty() && obj.get{{other.Name}}() == null){{#else}}! neighbors.containsAny(obj.get{{other.Name}}()){{#endif}})",
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
	public Class<?> getType() {
		return Association.class;
	}
}
