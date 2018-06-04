package de.uniks.networkparser.parser.generator.java;

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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaClazz extends BasicGenerator {
	public JavaClazz() {
		createTemplate("Declaration", Template.TEMPLATE,

				"{{#template PACKAGE {{packagename}}}}package {{packagename}};{{#endtemplate}}","",

				"{{#template IMPORT}}{{#foreach {{file.headers}}}}","import {{item}};{{#endfor}}{{#endtemplate}}","",

				"{{visibility}} {{modifiers} }{{type}} {{name}}{{#if {{superclazz}}}} extends {{superclazz}}{{#endif}}{{#if {{implements}}}} implements {{implements}}{{#endif}}","{","",

				"{{#if {{#AND}}{{#feature PROPERTYCHANGESUPPORT}} {{type}}!=INTERFACE {{#NOT}}{{type}}==enum{{#ENDNOT}}{{#ENDAND}}}}"
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
					 	"      if (listeners != null) {",
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
					 	"{{#endif}}"+
					
					 	"{{#if {{type}}==enum}}",
					 		"{{#FOREACH {{literal}}},}"+
				 				"{{item.name}}"+
					 			    "{{#IF {{item.value.size}}==>0}}("+
					 				"{{#FOREACH {{item.value}}}}"+
					 				   "{{#IF {{itemPos}}==>0}},{{#ENDIF}}"+
					 				   "{{item}}"+
					 				"{{#ENDFOR}}"+
					 			    "){{#ENDIF}}"+
					 		"{{#ENDFOR}}"+
					 		"{{#IF {{literal.size}}==>0}};{{#ENDIF}}",
					 		"{{name}}("+
					 				"{{#FOREACH {{attribute}}}}"+
					 				  "{{item.type}} {{item.name}}"+
					 				"{{#ENDFOR}}"+
					 				"){"+
					 		"}"+
					 	"{{#ENDIF}}",
					 						
					 	"{{#template TEMPLATEEND}}}{{#endtemplate}}"
				);
//		,
//			 	"{{attributes}}" + "{{fields}}" + "{{methods}}"+
		this.metaModel = true;
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
