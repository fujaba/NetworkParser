package de.uniks.template.generator.java;

import java.util.Collection;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaSet extends BasicGenerator {

	public JavaSet() {
		createTemplate("Declaration", Template.DECLARATION, 
				"{{#template PACKAGE}}{{#if packageName}}package {{packageName}};{{#endif}}{{#endtemplate}}","",
				
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}",""
				
				+ "{{#import " + SimpleSet.class.getName() + "}}" +
				"{{visibility}} class {{setName}} extends SimpleSet<{{name}}>","{","",
				
				"   protected Class<?> getTypClass()",
				"   {",
				"      return {{name}}.class;",
				"   }","",
				
				"   public {{setName}}()",
				"   {",
				"      // empty",
				"   }","",
				
				"   public {{setName}}({{name}}... objects)",
				"   {",
				"      for ({{name}} obj : objects)",
				"      {",
				"         this.add(obj);",
				"      }",
				"   }","",

				"   public {{setName}}(Collection<{{name}}> objects)",
				"   {",
				"      this.addAll(objects);",
				"   }","",
				
				"   public static final {{setName}} EMPTY_SET = new {{setName}}().withFlag({{setName}}.READONLY);","",

				"   public String getEntryType()",
				"   {",
				"      return \"{{packageClassName}}\";",
				"   }","",
				
				"   @Override",
				"   public {{setName}} getNewList(boolean keyValue)",
				"   {",
				"      return new {{setName}}();",
				"   }","",
				
				"   public {{setName}} filter(Condition<{{name}}> condition)",
				"   {",
				"      {{setName}} filterList = new {{setName}}();",
				"      filterItems(filterList, condition);",
				"      return filterList;",
				"   }",""
				
				+ "{{#import " + Collection.class.getName() + "}}" + 
				"   public {{setName}} with(Object value)",
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
				"   }","");
		
		createTemplate("Declaration", Template.TEMPLATEEND, "}");
		
		this.extension = "java";
		
		this.addGenerator(new JavaSetAttribute());
		this.addGenerator(new JavaSetAssociation());
		this.addGenerator(new JavaSetMethod());
	}
	
	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		return null;
	}
	
	@Override
	public Class<?> getTyp() {
		return null;
	}
	
}
