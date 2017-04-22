package de.uniks.template.generator.java;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.template.generator.BasicGenerator;
import de.uniks.template.generator.Template;

public class JavaAssociation extends BasicGenerator {

	public JavaAssociation() {
		createTemplate("Property", Template.FIELD, "   {{propertyVisibility}} {{propertyModifiers}} {{propertyType}} {{PROPERTY_NAME}} = \"{{otherName}}\";","");
		
		createTemplate("Field", Template.FIELD, "" + 
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {{fieldVisibility}} {{fieldModifiers}}{{#if fieldModifiers}} {{#endif}}{{#if {{member.cardinality}}==ONE}}{{other}}{{#else}}{{other}}Set{{#endif}} {{otherName}} = {{default}};","",
				"{{#endif}}");
		
		createTemplate("Declaration", Template.DECLARATION, "" +
				"{{#foreach {{member.parent.classmodel.clazzes}}}}" +
				   "{{#ifnot {{item.name}}=={{file.clazz.name}}" +
				      "{{#if {{item.name}}=={{member.type}}}}" +
				         "{{#import item.name}}" +
				      "{{#endif}}" +
				   "{{#endif}}" +
				"{{#endfor}}" +
				"   {{methodVisibility}} {{getModifiers}}{{#if modifiers}} {{#endif}}{{#if {{member.cardinality}}==ONE}}{{other}}{{#else}}{{other}}Set{{#endif}} get{{OtherName}}(){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      return this.{{otherName}};",
				"   }","",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==ONE}}",
				"   {{methodVisibility}} {{setModifiers}}{{#if setModifiers}} {{#endif}}boolean set{{OtherName}}({{other}} value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      boolean changed = false;",
				"      if (this.{{otherName}} != value) {",
				"         {{other}} oldValue = this.{{otherName}};",
				"         if (this.{{otherName}} != null) {",
				"            this.{{otherName}} = null;",
				"{{#if {{member.cardinality}}==ONE}}",
				"            oldValue.set{{Name}}(null);",
				"{{#else}}",
				"            oldValue.without{{Name}}(this);",
				"{{#endif}}",
				"         }",
				"         this.{{otherName}} = value;",
				"         if (value != null) {",
				"            value.with{{Name}}(this);",
				"         }",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"         firePropertyChange(PROPERTY_{{PROPERTY_NAME}}, oldValue, value);",
				"{{#endif}}",
				"         changed = true;",
				"      }",
				"      return changed;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==ONE}}",
				"   {{methodVisibility}} {{withOneModifiers}}{{#if withOneModifiers}} {{#endif}}{{source}} with{{OtherName}}({{other}} value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      this.set{{OtherName}}(value);",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==MANY}}",
				"   {{methodVisibility}} {{withManyModifiers}}{{#if withManyModifiers}} {{#endif}}{{source}} with{{OtherName}}({{other}}... value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      if (value == null) {",
				"         return this;",
				"      }",
				"      for ({{other}} item : value) {",
				"         if (item != null) {",
				"            if (this.{{otherName}} == null) {",
				"{{#if {{member.other.cardinality}}==ONE}}",
				"               this.{{otherName}} = new {{other}}();",
				"{{#else}}",
				"               this.{{otherName}} = new {{other}}Set();",
				"{{#endif}}",
				"            }",
				"            boolean changed = this.{{otherName}}.add(item);",
				"            if (changed)",
				"            {",
				"               item.with{{Name}}(this);",
				"               firePropertyChange(PROPERTY_{{PROPERTY_NAME}}, null, item);",
				"            }",
				"         }",
				"      }",
				"      return this;",
				"   }","",
				"{{#endif}}",
				"{{#endif}}",
				
				"{{#if {{member.other.cardinality}}==MANY}}",
				"   {{methodVisibility}} {{withoutModifiers}}{{#if withoutModifiers}} {{#endif}}{{source}} without{{OtherName}}({{other}}... value){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      for ({{other}} item : value) {",
				"         if (this.{{otherName}} != null && item != null) {",
				"            if (this.{{otherName}}.remove(item)) {",
				"{{#if {{member.other.cardinality}}==ONE}}",
				"               item.set{{Name}}(null);",
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

				"   {{methodVisibility}} {{createModifiers}}{{#if createModifiers}} {{#endif}}{{other}} create{{OtherName}}(){{#if {{file.clazz.type}}==INTERFACE}};","","{{#endif}}",
				"{{#ifnot {{file.clazz.type}}==INTERFACE}}",
				"   {",
				"      {{other}} value = new {{other}}();",
				"      with{{OtherName}}(value);",
				"      return value;",
				"   }","",
				"{{#endif}}");

	}
	
	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof Association == false) {
			return null;
		}
		Association element = (Association) item;
		return null;
	}
	
	@Override
	public Class<?> getTyp() {
		return Association.class;
	}

}
