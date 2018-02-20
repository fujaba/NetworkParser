package de.uniks.networkparser.parser.generator.java;

import java.util.Collection;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.generator.BasicGenerator;

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
	public Class<?> getTyp() {
		return Clazz.class;
	}
}
