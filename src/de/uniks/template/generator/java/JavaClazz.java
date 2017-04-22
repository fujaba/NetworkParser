package de.uniks.template.generator.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.template.TemplateResultFile;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaClazz extends BasicGenerator{

	public JavaClazz() {
		createTemplate("Declaration", Template.TEMPLATE, 
				
				"{{#template id=PACKAGE}}{{#if packageName}}package {{packageName}};{{#endif}}{{#endtemplate}}","",
				
				"{{#template id=IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",
				
				"{{visibility}} {{modifiers} }{{type}} {{name}}{{#if superclasses}} {{superclasses}}{{#endif}}","{",

				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}"
						+"{{#import "+PropertyChangeListener.class.getName()+"}}"+"{{#import "+PropertyChangeSupport.class.getName()+"}}"+ 
						"   protected PropertyChangeSupport listeners = null;","",
						
						"   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {", 
						"      if (listeners != null) {", 
						"         listeners.firePropertyChange(propertyName, oldValue, newValue);", 
						"         return true;", 
						"      }", 
						"      return false;", 
						"   }","",
						
						"   public boolean addPropertyChangeListener(PropertyChangeListener listener)",
						"   {",
						"      if (listeners == null) {", 
						"         listeners = new PropertyChangeSupport(this);", 
						"      }", 
						"      listeners.addPropertyChangeListener(listener);", 
						"      return true;", 
						"   }","",
						
						"   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)",
						"   {",
						"      if (listeners == null) {",
						"         listeners = new PropertyChangeSupport(this);",
						"      }",
						"      listeners.addPropertyChangeListener(propertyName, listener);",
						"      return true;",
						"   }","",
						
						"   public boolean removePropertyChangeListener(PropertyChangeListener listener)",
					 	"   {",
					 	"      if (listeners == null) {",
					 	"         listeners.removePropertyChangeListener(listener);",
					 	"      }",
					 	"      listeners.removePropertyChangeListener(listener);",
					 	"      return true;",
					 	"   }","",
					 	
					 	"   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)",
					 	"   {",
					 	"      if (listeners != null) {",
					 	"         listeners.removePropertyChangeListener(propertyName, listener);",
					 	"      }",
					 	"      return true;",
					 	"   }",""+
					 	"{{#endif}}"
				);
//		,
//			 	"{{attributes}}" + "{{fields}}" + "{{methods}}"+ 
		
		createTemplate("Declaration", Template.TEMPLATEEND, "}");
	
		this.extension = "java";
		
		this.addGenerator(new JavaAttribute());
		this.addGenerator(new JavaAssociation());
		this.addGenerator(new JavaMethod());
	}

	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof Clazz == false) {
			return null;
		}
		TemplateResultFile result = this.executeClazz((Clazz)item, parameters);
		this.executeTemplate(result, parameters, item);
		return result;
	}
	
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}
}
