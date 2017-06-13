package de.uniks.networkparser.parser.generator.java;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class JavaAssociation extends BasicGenerator {

	public JavaAssociation() {
		createTemplate("Declaration", Template.DECLARATION, 
				"{{#template VALUE}}",
				"{{#if {{other.isImplements}}==false}}",
				"   public static final String PROPERTY_{{other.NAME}} = \"{{other.name}}\";","",
		
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {{visibility}} {{modifiers} }{{#if {{other.cardinality}}==1}}{{other.clazz.name}}{{#else}}{{other.clazz.name}}Set{{#endif}} {{other.name}} = null;","",
				"{{#endif}}","",
		
				"{{#foreach {{parent.parent.child}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#import {{item.fullName}}}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"{{#if {{other.cardinality}}==n}}" +
				   "{{#import {{other.clazz.packageName}}.util.{{other.clazz.name}}Set}}" +
				"{{#endif}}" +
				"   public {{modifiers} }{{#if {{other.cardinality}}==1}}{{other.clazz.name}}{{#else}}{{other.clazz.name}}Set{{#endif}} get{{other.Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      return this.{{other.name}};",
				"   }","",
				"{{#endif}}",
				
				"{{#if {{other.cardinality}}==1}}",
				"   public {{modifiers} }boolean set{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      boolean changed = false;",
				"      if (this.{{other.name}} != value) {",
				"         {{other.clazz.name}} oldValue = this.{{other.name}};",
				"{{#if {{type}}==assoc}}",
				"         if (this.{{other.name}} != null) {",
				"            this.{{other.name}} = null;",
				"{{#if {{cardinality}}==1}}",
				"            oldValue.set{{Name}}(null);",
				"{{#else}}",
				"            oldValue.without{{Name}}(this);",
				"{{#endif}}",
				"         }",
				"{{#endif}}",
				"         this.{{other.name}} = value;",
				"{{#if {{type}}==assoc}}",
				"         if (value != null) {",
				"            value.with{{Name}}(this);",
				"         }",
				"{{#endif}}",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"         firePropertyChange(PROPERTY_{{other.NAME}}, oldValue, value);",
				"{{#endif}}",
				"         changed = true;",
				"      }",
				"      return changed;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{other.cardinality}}==1}}",
				"   public {{modifiers} }{{clazz.name}} with{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      this.set{{other.Name}}(value);",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{other.cardinality}}==n}}",
				"   public {{modifiers} }{{clazz.name}} with{{other.Name}}({{other.clazz.name}}... value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      if (value == null) {",
				"         return this;",
				"      }",
				"      for ({{other.clazz.name}} item : value) {",
				"         if (item != null) {",
				"            if (this.{{other.name}} == null) {",
				"               this.{{other.name}} = new {{other.clazz.name}}Set();",
				"            }",
				"            boolean changed = this.{{other.name}}.add(item);",
				"            if (changed)",
				"            {",
				"{{#if {{type}}==assoc}}",
				"               item.with{{Name}}(this);",
				"{{#endif}}",
				"               firePropertyChange(PROPERTY_{{other.NAME}}, null, item);",
				"            }",
				"         }",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{other.cardinality}}==n}}",
				"   public {{modifiers} }{{clazz.name}} without{{other.Name}}({{other.clazz.name}}... value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      for ({{other.clazz.name}} item : value) {",
				"         if (this.{{other.name}} != null && item != null) {",
				"{{#if {{type}}==assoc}}",
				"            if (this.{{other.name}}.remove(item)) {",
				"{{#if {{cardinality}}==1}}",
				"               item.with{{Name}}(null);",
				"{{#else}}",
				"               item.without{{Name}}(this);",
				"{{#endif}}",
				"            }",
				"{{#else}}",
				"            this.{{other.name}}.remove(item);",
				"{{#endif}}",
				"         }",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",

				"{{#ifnot {{other.clazz.type}}==interface}}",
				"{{#ifnot {{other.clazz.modifiers#contains(abstract)}}}}",
				"   public {{modifiers} }{{other.clazz.name}} create{{other.Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      {{other.clazz.name}} value = new {{other.clazz.name}}();",
				"      with{{other.Name}}(value);",
				"      return value;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}{{#endtemplate}}");

	}
	
	@Override
	public Class<?> getTyp() {
		return Association.class;
	}

}
