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
import java.util.Collection;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.BasicGenerator;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;

public class JavaSet extends BasicGenerator {
	public JavaSet() {

		createTemplate("Declaration", Template.TEMPLATE,
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}","",

				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",

				"{{#import {{fullName}}}}" +
				"{{visibility}} class {{name}}Set extends {{#feature SETCLASS="+SimpleSet.class.getName()+"}}<{{name}}>","{","",

				"   public Class<?> getTypClass()",
				"   {",
				"      return {{name}}.class;",
				"   }","",

				"   public {{name}}Set()",
				"   {",
				"      // empty",
				"   }","",

				"   public {{name}}Set({{name}}... objects)",
				"   {",
				"      for ({{name}} obj : objects)",
				"      {",
				"         this.add(obj);",
				"      }",
				"   }","",

				"   public {{name}}Set(Collection<{{name}}> objects)",
				"   {",
				"      this.addAll(objects);",
				"   }","",
				"{{#if {{templatemodel.features.setclass.classstring}}=="+SimpleSet.class.getName()+"}}"+
				"		public static final {{name}}Set EMPTY_SET = new {{name}}Set().withFlag({{name}}Set.READONLY);"+
				"{{#else}}"+
				"		public static final {{name}}Set EMPTY_SET = new {{name}}Set();"+
				"{{#endif}}"
				,"",

				"   public String getEntryType()",
				"   {",
				"      return \"{{file.member.fullName}}\";",
				"   }","",
				"{{#if {{templatemodel.features.setclass.classstring}}=="+SimpleSet.class.getName()+"}}"+
				"   @Override",
				"{{#endif}}"+
				"   public {{name}}Set getNewList(boolean keyValue)",
				"   {",
				"      return new {{name}}Set();",
				"   }","","",

				"{{#import " + Collection.class.getName() + "}}" +
				"   @SuppressWarnings(\"unchecked\")",
				"   public {{name}}Set with(Object value)",
				"   {",
				"      if (value == null)",
				"      {",
				"         return this;",
				"      }",
				"      else if (value instanceof java.util.Collection)",
				"      {",
				"         this.addAll((Collection<{{name}}>)value);",
				"      }",
				"      else if (value != null)",
				"      {",
				"         this.add(({{name}}) value);",
				"      }",
				"      return this;",
				"   }","",

				"{{#template TEMPLATEEND}}}{{#endtemplate}}");

		this.extension = "java";
		this.path = "util";
		this.postfix = "Set";

		this.addGenerator(new JavaSetAttribute());
		this.addGenerator(new JavaSetAssociation());
		this.addGenerator(new JavaSetMethod());
	}

	@Override
	public TemplateResultFile executeClazz(Clazz clazz, LocalisationInterface parameters, boolean isStandard) {
		FeatureSet features = getFeatures(parameters);
		if(features != null) {
			if(features.match(Feature.SETCLASS, null) == false) {
				return null;
			}
		}
		return super.executeClazz(clazz, parameters, isStandard);
	}

	@Override
	public Class<?> getType() {
		return Clazz.class;
	}
}
