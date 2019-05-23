package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.IdMap;
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
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.Template;

public class JavaCreator extends Template {
	public JavaCreator() {
		this.id = TYPE_JAVA + ".creator";
		this.extension = "java";
		this.fileType = "clazz";
		this.path = "util";
		this.postfix = "Creator";
		this.type = TEMPLATE;

		this.withTemplate(
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}",
				"",

				"{{#template IMPORT}}{{#foreach {{file.headers}}}}", "import {{item}};{{#endfor}}{{#endtemplate}}", "",
				"{{#import " + SendableEntityCreator.class.getName() + "}}" + "{{#import {{fullName}}}}",
				"{{visibility}} class {{name}}Creator implements SendableEntityCreator", "{", "",
				"   private final String[] properties = new String[]", "   {",
				"{{#foreach child}}",
					"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
					"      {{name}}.PROPERTY_{{item.NAME}},", "{{#endif}}",
					"{{#if {{item.className}}==" + Association.class.getName() + "}}",
						"{{#if {{item.other.isGenerate}}}}",
							"{{#import {{item.other.clazz.fullName}}}}",
							"      {{name}}.PROPERTY_{{item.other.NAME}},",
						"{{#endif}}",
					"{{#endif}}",
				"{{#endfor}}",
				"{{#if {{#feature DYNAMICVALUES}}}}", "      SendableEntityCreator.DYNAMIC", "{{#endif}}", "   };", "",

				"   @Override", "   public String[] getProperties()", "   {", "      return properties;", "   }", "",

				"   @Override", "   public Object getSendableInstance(boolean prototyp)", "   {",
				"{{#if {{#AND}}{{type}}==class {{#NOT}}{{modifiers#contains(abstract)}}{{#ENDNOT}}{{#ENDAND}}}}",
				"      return new {{name}}();", "{{#else}}", "      return null;", "{{#endif}}", "   }", "",

				"   @Override", "   public Object getValue(Object entity, String attribute)", "   {",
				"      if(attribute == null || entity instanceof {{name}} == false) {", "          return null;",
				"      }", "      {{name}} element = ({{name}})entity;", "{{#foreach child}}",
				"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"      if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attribute))", "      {",
				"         return element.{{#if {{item.type}}==boolean}}is{{#else}}get{{#endif}}{{item.Name}}();",
				"      }", "", "{{#endif}}", "{{#if {{item.className}}==" + Association.class.getName() + "}}",
				"{{#if {{item.other.isGenerate}}}}",
				"      if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attribute))", "      {",
				"         return element.get{{item.other.Name}}();", "      }", "", "{{#endif}}", "{{#endif}}",
				"{{#endfor}}", "{{#if {{#feature DYNAMICVALUES}}}}",
				"      if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attribute)) {",
				"          return element.getDynamicValues();", "      }",
				"      return element.getDynamicValue(attribute);", "{{#else}}", "      return null;", "{{#endif}}",
				"   }", "",

				"   @Override",
				"   public boolean setValue(Object entity, String attribute, Object value, String type)", "   {",
				"      if(attribute == null || entity instanceof {{name}} == false) {", "          return false;",
				"      }", "      {{name}} element = ({{name}})entity;",
				"      if (SendableEntityCreator.REMOVE.equals(type) && value != null)", "      {",
				"         attribute = attribute + type;", "      }", "",

				"{{#foreach child}}", "{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"{{#ifnot {{item.modifiers#contains(static)}}}}",
				"      if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attribute))",
				"      {" + "{{#import {{item.type(false)}}}}",
				"         element.set{{item.Name}}(({{item.type.name}}) value);", "         return true;", "      }",
				"", "{{#endif}}", "{{#endif}}", "{{#if {{item.className}}==" + Association.class.getName() + "}}",
				"{{#if {{item.other.isGenerate}}}}",
				"      if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attribute))", "      {",
				"         element.{{#if {{item.other.cardinality}}==1}}set{{#else}}with{{#endif}}{{item.other.Name}}(({{item.other.clazz.name}}) value);",
				"         return true;", "      }", "", "{{#endif}}", "{{#endif}}", "{{#endfor}}",
				"{{#if {{#feature DYNAMICVALUES}}}}", "      element.withDynamicValue(attribute, value);",
				"      return true;", "{{#else}}", "      return false;", "{{#endif}}", "   }", "", "",
				"{{#import " + IdMap.class.getName() + "}}" + "   public static IdMap createIdMap(String session) {",
				"      return CreatorCreator.createIdMap(session);", "   }",
				"{{#template TEMPLATEEND}}}{{#endtemplate}}");
	}

	protected boolean isValid(GraphMember member, LocalisationInterface parameters) {
		if (super.isValid(member, parameters) == false) {
			return false;
		}
		// Check for existing Feature Serialization
		Feature features = getFeature(Feature.SERIALIZATION, member.getClazz());
		return features != null;
	}
}
