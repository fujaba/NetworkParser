package de.uniks.template.generator.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.template.Template;
import de.uniks.template.TemplateInterface;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.generator.BasicGenerator;

public class JavaClazz extends BasicGenerator{

	public JavaClazz() {
		createTemplate("Declaration", Template.DECLARATION, "{{#import "+PropertyChangeListener.class.getName()+"}}"+"{{#import "+PropertyChangeSupport.class.getName()+"}}");
		
		/*createTemplate("Declaration", Template.DECLARATION, "{{#if packageName}}package {{packageName}};{{#endif}}",
				"{{imports}}",
				"{{visibility}} {{modifiers}}{{#if modifiers}} {{#endif}}{{clazzType}} {{name}}{{#if superclasses}} {{superclasses}}{{#endif}}",
				" {",
				"{{#if propertyChange}}"
				+"{{#import "+PropertyChangeListener.class.getName()+"}}"+"{{#import "+PropertyChangeSupport.class.getName()+"}}"+ 
				"   protected PropertyChangeSupport listeners = null;",
				"   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)\",\r\n" + 
				"					 	  \"   {\",\r\n" + 
				"					 	  \"      if (listeners != null) {\",\r\n" + 
				"					 	  \"         listeners.firePropertyChange(propertyName, oldValue, newValue);\",\r\n" + 
				"					 	  \"         return true;\",\r\n" + 
				"					 	  \"      }\",\r\n" + 
				"					 	  \"      return false;\",\r\n" + 
				"					 	  \"   }",
				"   public boolean addPropertyChangeListener(PropertyChangeListener listener)",
				"   {",
				"      if (listeners == null) {", 
				"         listeners = new PropertyChangeSupport(this);", 
				"      }", 
				"      listeners.addPropertyChangeListener(listener);", 
				"      return true;", 
				"   }",
				"   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)",
				"   {",
				"      if (listeners == null) {",
				"         listeners = new PropertyChangeSupport(this);",
				"      }",
				"      listeners.addPropertyChangeListener(propertyName, listener);",
				"      return true;",
				"   }",
				"   public boolean removePropertyChangeListener(PropertyChangeListener listener)",
			 	"   {",
			 	"      if (listeners == null) {",
			 	"         listeners.removePropertyChangeListener(listener);",
			 	"      }",
			 	"      listeners.removePropertyChangeListener(listener);",
			 	"      return true;",
			 	"   }",
			 	"   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)",
			 	"   {",
			 	"      if (listeners != null) {",
			 	"         listeners.removePropertyChangeListener(propertyName, listener);",
			 	"      }",
			 	"      return true;",
			 	"   }"+
			 	"{{#end}}",
			 	"{{attributes}}",
			 	"{{fields}}",
			 	"{{methods}}",
			 	"}");*/
	
		
		createTemplate("imports", Template.TEMPLATE, "{{#foreach {{template.file.headers}}import {{#item}};{{#endfor}}");
	
		this.extension = "java";
	}

	@Override
	public TemplateInterface generate(GraphMember item) {
		return generate(item, null);
	}

	@Override
	public TemplateInterface generate(GraphMember item, TextItems parameters) {
		if(item instanceof Clazz == false) {
			return null;
		}
		TemplateResultFile result = this.executeClazz((Clazz)item, parameters);
		this.executeTemplate(result, parameters, item);
		return result;
	}
}
