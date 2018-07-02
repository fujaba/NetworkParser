package de.uniks.networkparser.parser.generator.typescript;

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

public class TypescriptAttribute extends BasicGenerator {
	@Override
	public Class<?> getType() {
		return Attribute.class;
	}

	// TODO adjust java based attribute names to Number, String and boolean
	// TODO fix or statements to allow {{}}== {{}}== pattern
	public TypescriptAttribute() {
		createTemplate("Declaration", Template.DECLARATION,
				"{{#template VALUE}}   static PROPERTY_{{NAME}}: String = \"{{name}}\";","","",
				"{{#foreach {{parent.parent.child}}}}" +
				   //"{{#if {{#OR}}{{item.type}}==class {{item.type}}==interface{{#ENDOR}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#if {{item.name}}=={{type}}}}" +
				            "{{#import {{item.name}}}}" +
				         "{{#endif}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   {{modifiers} }{{name}}: {{type}}{{#ifnot {{file.member.type}}==interface}}{{#if {{value}}}} = {{value}}{{#endif}}{{#endif}};","","","{{#endtemplate}}");
	}
}
