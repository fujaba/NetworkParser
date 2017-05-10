package de.uniks.networkparser.parser.generator.java;

import java.util.Collection;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.interfaces.Condition;
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
				
				"   public static final {{name}}Set EMPTY_SET = new {{name}}Set().withFlag({{name}}Set.READONLY);","",

				"   public String getEntryType()",
				"   {",
				"      return \"{{file.member.fullName}}\";",
				"   }","",
				
				"   @Override",
				"   public {{name}}Set getNewList(boolean keyValue)",
				"   {",
				"      return new {{name}}Set();",
				"   }","","",
				
				"{{#import " + Condition.class.getName() + "}}" +
				"   public {{name}}Set filter(Condition<{{name}}> condition)",
				"   {",
				"      {{name}}Set filterList = new {{name}}Set();",
				"      filterItems(filterList, condition);",
				"      return filterList;",
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
//FIXME		if(features.match(Feature.SETCLASS, clazz) == false) {
//			return null;
//		}
//		if(parameters instanceof templ)
//		parameters.getText("features", null, null);
		
		return super.executeClazz(clazz, parameters, isStandard);
	}
	
//	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
//		if(item instanceof Clazz == false) {
//			return null;
//		}
////		if(features.)
////		// TODO add proper condition for allowing sets
////		// Sets are allowed
////		model.get
//////		if (model.f) {
//////		}
//
////		if()
//		TemplateResultFile result = this.executeClazz((Clazz)item, parameters);
//		this.executeTemplate(result, parameters, item);
//		return result;
//	}
//	
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}
}
