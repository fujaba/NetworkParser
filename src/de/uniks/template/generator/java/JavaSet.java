package de.uniks.template.generator.java;

import java.util.Collection;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSet extends BasicGenerator {

	public JavaSet() {
		fileType = BasicGenerator.FILETYPE_SET;
		
		createTemplate("Declaration", Template.TEMPLATE, 
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}","",
				
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",
				
				"{{#import " + SimpleSet.class.getName() + "}}" +
				"{{#import {{fullName}}}}" +
				"{{visibility}} class {{name}}Set extends SimpleSet<{{name}}>","{","",
				
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
				"      return \"{{packageClassName}}\";",
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
		
		this.addGenerator(new JavaSetAttribute());
		this.addGenerator(new JavaSetAssociation());
		this.addGenerator(new JavaSetMethod());
	}
	
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof Clazz == false) {
			return null;
		}
		TemplateResultFile result = this.executeClazz((Clazz)item, fileType, parameters);
		this.executeTemplate(result, parameters, item);
		return result;
	}
	
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}
	
}
