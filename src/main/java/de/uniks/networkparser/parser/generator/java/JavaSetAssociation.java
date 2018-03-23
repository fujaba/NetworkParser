package de.uniks.networkparser.parser.generator.java;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.Collections;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaSetAssociation extends BasicGenerator {
	public JavaSetAssociation() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}",
				"{{#if {{other.isImplements}}==false}}",
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
