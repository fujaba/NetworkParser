package de.uniks.template.generator.java;

import de.uniks.networkparser.graph.Association;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaAssociation extends BasicGenerator {

	public JavaAssociation() {
		createTemplate("Declaration", Template.DECLARATION, 
				"{{#template VALUE}}   public static final String PROPERTY_{{other.NAME}} = \"{{other.name}}\";","",
		
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {{visibility}} {{modifiers} }{{#if {{member.other.cardinality}}==1}}{{other.clazz.name}}{{#else}}{{other.clazz.name}}Set{{#endif}} {{other.name}} = null;","",
				"{{#endif}}","",
		
				"{{#foreach {{member.parent.parent.child}}}}" +
				   "{{#if {{item.type}}==class}}" +
				      "{{#ifnot {{item.name}}=={{file.member.name}}}}" +
				         "{{#import {{item.fullName}}}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
//				"{{#if {{member.other.cardinality}}==n}}" +
//				   "{{#import {{member.other.clazz.packageName}}{{member.other.clazz.name}}Set}}" +
//				"{{#endif}}" +
				"   public {{modifiers} }{{#if {{member.other.cardinality}}==1}}{{other.clazz.name}}{{#else}}{{other.clazz.name}}Set{{#endif}} get{{other.Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      return this.{{other.name}};",
				"   }","",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==1}}",
				"   public {{modifiers} }boolean set{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      boolean changed = false;",
				"      if (this.{{other.name}} != value) {",
				"         {{other.clazz.name}} oldValue = this.{{other.name}};",
				"         if (this.{{other.name}} != null) {",
				"            this.{{other.name}} = null;",
				"{{#if {{member.cardinality}}==1}}",
				"            oldValue.set{{Name}}(null);",
				"{{#else}}",
				"            oldValue.without{{Name}}(this);",
				"{{#endif}}",
				"         }",
				"         this.{{other.name}} = value;",
				"         if (value != null) {",
				"            value.with{{Name}}(this);",
				"         }",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"         firePropertyChange(PROPERTY_{{other.NAME}}, oldValue, value);",
				"{{#endif}}",
				"         changed = true;",
				"      }",
				"      return changed;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==1}}",
				"   public {{modifiers} }{{clazz.name}} with{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      this.set{{other.Name}}(value);",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==n}}",
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
				"               item.with{{Name}}(this);",
				"               firePropertyChange(PROPERTY_{{other.NAME}}, null, item);",
				"            }",
				"         }",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==n}}",
				"   public {{modifiers} }{{clazz.name}} without{{other.Name}}({{other.clazz.name}}... value){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      for ({{other.clazz.name}} item : value) {",
				"         if (this.{{other.name}} != null && item != null) {",
				"            if (this.{{other.name}}.remove(item)) {",
				"{{#if {{member.cardinality}}==1}}",
				"               item.with{{Name}}(null);",
				"{{#else}}",
				"               item.without{{Name}}(this);",
				"{{#endif}}",
				"            }",
				"         }",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",

				"   public {{modifiers} }{{other.clazz.name}} create{{other.Name}}(){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}}",
				"   {",
				"      {{other.clazz.name}} value = new {{other.clazz.name}}();",
				"      with{{other.Name}}(value);",
				"      return value;",
				"   }","",
				"{{#endif}}{{#endtemplate}}");

	}
	
	@Override
	public Class<?> getTyp() {
		return Association.class;
	}

}
