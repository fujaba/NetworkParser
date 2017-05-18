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
				"{{#ifnot {{member.other.typeName}}==edge}}",
				"{{#ifnot {{member.other.typeName}}==generalisation}}",
				"{{#ifnot {{member.other.typeName}}==implements}}",
				"{{#import {{file.member.fullName}}}}" +
				"   public {{file.member.name}}Set get{{member.other.Name}}()",
				"   {",
				"      {{file.member.name}}Set result = new {{file.member.name}}Set();",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         result.with(obj.get{{member.other.Name}}());",
				"      }",
				"      return result;",
				"   }","","",
				
				"{{#import " + ObjectSet.class.getName() + "}}" +
				"{{#if {{member.other.cardinality}}==n}}{{#import " + Collections.class.getName() + "}}{{#endif}}" +
				"   public {{file.member.name}}Set filter{{member.other.Name}}(Object value)",
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
				"         if ({{#if {{member.other.cardinality}}==1}}neighbors.contains(obj.get{{member.other.Name}}()) || (neighbors.isEmpty() && obj.get{{member.other.Name}}() == null){{#else}}! Collections.disjoint(neighbors, obj.get{{member.other.Name}}()){{#endif}})",
				"         {",
				"            answer.add(obj);",
				"         }",
				"      }",
				"      return answer;",
				"   }","",
				
				"   public {{file.member.name}}Set with{{member.other.Name}}({{member.other.clazz.name}} value)",
				"   {",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         obj.with{{member.other.Name}}(value);",
				"      }",
				"      return this;",
				"   }","",
				
				"{{#import {{member.other.clazz.fullName}}}}" +
				"{{#if {{member.other.cardinality}}==MANY}}",
				"   public {{file.member.name}}Set without{{member.other.Name}}({{member.other.clazz.name}} value)",
				"   {",
				"      for ({{file.member.name}} obj : this)",
				"      {",
				"         obj.without{{member.other.Name}}(value);",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}{{#endtemplate}}");
	}
	
	@Override
	public Class<?> getTyp() {
		return Association.class;
	}
}
