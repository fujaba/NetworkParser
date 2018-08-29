package de.uniks.networkparser.parser.java;

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
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.parser.BasicGenerator;
import de.uniks.networkparser.parser.Template;

public class JavaSetAttribute extends BasicGenerator {
	public JavaSetAttribute() {
		createTemplate("Declaration", Template.VALUE,
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"+
					"{{#import {{type(false)}}}}"+
					"   public {{#listType}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}()",
					"   {",
					"      {{#listType}} result = new {{#listType}}();",
					"      for ({{file.member.name}} obj : this)",
					"      {",
					"         result.add(obj.{{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}());",
					"      }",
					"      return result;",
					"   }",
				   "",
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
					"   }",
				   "",
				   "{{#if {{#NOT}}{{type}}==BOOLEAN{{#ENDNOT}}}}" +
				   "   public {{file.member.name}}Set filter{{Name}}({{type}} minValue, {{type}} maxValue)",
				   "   {",
				   "      {{file.member.name}}Set result = new {{file.member.name}}Set();",
				   "      for({{file.member.name}} obj : this)",
				   "      {",
				   "         if ({{#if {{type}}==STRING}}minValue.compareTo(obj.get{{Name}}()) <= 0 && maxValue.compareTo(obj.get{{Name}}()) >= 0" +
							"{{#else}}minValue <= obj.get{{Name}}() && maxValue >= obj.get{{Name}}(){{#endif}})",
				   "         {",
				   "            result.add(obj);",
				   "         }",
				   "      }",
				   "      return result;",
				   "   }",
				   "",
				   "",
				   "{{#endif}}" +
					"{{#if {{type}}==VALUETYPE}}"+
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
					"   }","","",
					"{{#endif}}"+
					"   public {{file.member.name}}Set with{{Name}}({{type}} value) {",
					"      for ({{file.member.name}} obj : this)",
					"      {",
					"         obj.set{{Name}}(value);",
					"      }",
					"      return this;",
					"   }","" +
				"{{#endif}}");
	}

	@Override
	public Class<?> getType() {
		return Attribute.class;
	}
}
