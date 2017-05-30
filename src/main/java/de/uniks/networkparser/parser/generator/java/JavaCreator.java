package de.uniks.networkparser.parser.generator.java;

import org.sdmlib.simple.model.attribute_b.Person;

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
				
				"   @Override",
				"   public String[] getProperties() {",
				"      return null;",
				"   }","",

				"   @Override",
				"   public Object getValue(Object entity, String attribute) {",
				"      int pos = attribute.indexOf('.');",
			    "      String attrName = attribute;","",
			      
			    "      if (pos > 0)",
			    "      {",
			    "         attrName = attribute.substring(0, pos);",
			    "      }","",
		
				"{{#foreach child}}",
				"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"      if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attrName))",
				"      {",
				"         return (({{name}}) entity).{{#if {{item.type}}==boolean}}is{{#else}}get{{#endif}}{{item.Name}}();",
				"      }","",
				"{{#endif}}",
				"{{#if {{item.className}}==" + Association.class.getName() + "}}",
				"      ",
				"{{#endif}}",
				"{{#endfor}}",
				"      return null;",
				"   }","",
				
				"   @Override",
				"   public boolean setValue(Object entity, String attribute, Object value, String type) {",
				"      return false;",
				"   }","",

				"   @Override",
				"   public Object getSendableInstance(boolean prototyp) {",
				"      return null;",
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
