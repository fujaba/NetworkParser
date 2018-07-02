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
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaAttribute extends BasicGenerator{
	public JavaAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}   public static final String PROPERTY_{{NAME}} = \"{{name}}\";","",
				//,"{{#template FIELD {{#ifnot {{file.clazz.type}}==interface}}}}   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if default}} = {{default}}{{#endif}}{{#endtemplate}}"

				"{{#ifnot {{file.member.type}}==interface}}",
				"   {{visibility}} {{modifiers} }{{type} }{{name}}{{#if {{value}}}} = {{value}}{{#endif}};","",
				"{{#endif}}","",
				"{{#import {{type(false)}}}}" +
				"{{#foreach {{parent.parent.child}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#if {{item.name}}=={{type}}}}" +
				            "{{#import {{item.fullName}}}}" +
				         "{{#endif}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   public {{modifiers} }{{type}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      return {{this}}.{{name}};",
				"   }","",
				"{{#endif}}","",
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"+
					"   public {{modifiers} }void set{{Name}}({{type}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
					"{{#ifnot {{file.member.type}}==interface}}",
					"   {",
					"      if ({{this}}.{{name}} != value)",
					"      {",
					"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{file.member.type}}==!enum{{#ENDAND}}}}"+
					"         {{type}} oldValue = {{this}}.{{name}};",
					"         {{this}}.{{name}} = value;",
					"         firePropertyChange(PROPERTY_{{NAME}}, oldValue, value);",
					"{{#else}}",
					"         {{this}}.{{name}} = value;",
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
				"{{#endif}}","",
				"{{#endif}}{{#endtemplate}}");

	}

	@Override
	public Class<?> getType() {
		return Attribute.class;
	}
}
