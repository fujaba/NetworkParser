package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaCreator extends BasicGenerator {

	public JavaCreator() {
		
		createTemplate("Declaration", Template.TEMPLATE, 
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}","",
				
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",
				"{{#import " + SendableEntityCreator.class.getName() + "}}" +
				"{{#import {{fullName}}}}",
				"{{visibility}} class {{name}}Creator implements SendableEntityCreator",
				"{","",
				
				"   private final String[] properties = new String[]",
				"   {",
				"{{#foreach child}}",
				"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"      {{name}}.PROPERTY_{{item.NAME}},",
				"{{#endif}}",
				"{{#if {{item.className}}==" + Association.class.getName() + "}}",
				"{{#if {{item.other.isImplements}}==false}}",
				"{{#import {{item.other.clazz.fullName}}}}",
				"      {{name}}.PROPERTY_{{item.other.NAME}},",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endfor}}",
				"   };","",
				
				"   @Override",
				"   public String[] getProperties()",
				"   {",
				"      return properties;",
				"   }","",

				"   @Override",
				"   public Object getSendableInstance(boolean prototyp)",
				"   {",
				"{{#if {{#AND}}{{type}}==class {{#NOT}}{{modifiers#contains(abstract)}}{{#ENDNOT}}{{#ENDAND}}}}",
				"      return new {{name}}();",
				"{{#else}}",
				"      return null;",
				"{{#endif}}",
				"   }","",
				
				"   @Override",
				"   public Object getValue(Object entity, String attribute)",
				"   {",
				"      int pos = attribute.indexOf('.');",
			    "      String attrName = attribute;","",
			      
			    "      if (pos > 0)",
			    "      {",
			    "         attrName = attribute.substring(0, pos);",
			    "      }",
			    "      if(attrName.length()<1) {",
			    "         return null;",
			    "      }","",
		
				"{{#foreach child}}",
				"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"      if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attrName))",
				"      {",
				"         return (({{name}}) entity).{{#if {{item.type}}==boolean}}is{{#else}}get{{#endif}}{{item.Name}}();",
				"      }","",
				"{{#endif}}",
				"{{#if {{item.className}}==" + Association.class.getName() + "}}",
				"{{#if {{item.other.isImplements}}==false}}",
				"      if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attrName))",
				"      {",
				"         return (({{name}}) entity).get{{item.other.Name}}();",
				"      }","",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endfor}}",
				"      return null;",
				"   }","",
				
				"   @Override",
				"   public boolean setValue(Object entity, String attribute, Object value, String type)",
				"   {",
				"      if (SendableEntityCreator.REMOVE.equals(type) && value != null)",
			    "      {",
			    "         attribute = attribute + type;",
			    "      }","",
			    
			    "{{#foreach child}}",
				    "{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
					    "{{#ifnot {{item.modifiers#contains(static)}}}}",
					    "      if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attribute))",
						"      {" +
						"{{#import {{item.type(false)}}}}",
						"         (({{name}}) entity).set{{item.Name}}(({{item.type.name}}) value);",
						"         return true;",
					    "      }","",
					    "{{#endif}}",
				    "{{#endif}}",
			    "{{#if {{item.className}}==" + Association.class.getName() + "}}",
			    "{{#if {{item.other.isImplements}}==false}}",
			    "      if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attribute))",
				"      {",
				"         (({{name}}) entity).{{#if {{item.other.cardinality}}==1}}set{{#else}}with{{#endif}}{{item.other.Name}}(({{item.other.clazz.name}}) value);",
				"         return true;",
			    "      }","",
			    "{{#endif}}",
			    "{{#endif}}",
			    "{{#endfor}}",
				"      return false;",
				"   }","",

				"{{#template TEMPLATEEND}}}{{#endtemplate}}");
		
		this.extension = "java";
		this.path = "util";
		this.postfix = "Creator";
		
	}
	
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}

}
