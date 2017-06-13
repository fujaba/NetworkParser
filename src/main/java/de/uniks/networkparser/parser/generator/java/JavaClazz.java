package de.uniks.networkparser.parser.generator.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaClazz extends BasicGenerator{

	public JavaClazz() {
		createTemplate("Declaration", Template.TEMPLATE, 
				
				"{{#template PACKAGE {{packagename}}}}package {{packagename}};{{#endtemplate}}","",
				
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",
				
				"{{visibility}} {{modifiers} }{{type}} {{name}}{{#if {{superclazz}}}} extends {{superclazz}}{{#endif}}{{#if {{implements}}}} implements {{implements}}{{#endif}}","{","",

				"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{type}}!=INTERFACE{{#ENDAND}}}}"
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
					 	"{{#endif}}",
					 	"{{#template TEMPLATEEND}}}{{#endtemplate}}"
				);
//		,
//			 	"{{attributes}}" + "{{fields}}" + "{{methods}}"+ 
		this.extension = "java";
		
		this.addGenerator(new JavaAttribute());
		this.addGenerator(new JavaAssociation());
		this.addGenerator(new JavaMethod());
	}

	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}
}
