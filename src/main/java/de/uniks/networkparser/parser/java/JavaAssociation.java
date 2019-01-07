package de.uniks.networkparser.parser.java;

import java.util.Collection;

import de.uniks.networkparser.parser.Template;

public class JavaAssociation extends Template {
	public JavaAssociation() {
		this.id = "association";
		this.type = DECLARATION;
		this.withTemplate("{{#template VALUE}}",
				"{{#if {{other.isImplements}}==false}}",
				"	public static final String PROPERTY_{{other.NAME}} = \"{{other.name}}\";",
				"",
				"{{#ifnot {{file.member.type}}==interface}}",
				"	{{visibility}} {{modifiers} }"
					+ "{{#if {{other.cardinality}}==1}}"
						+ "{{other.clazz.name}} {{other.name}} = null;"
					+ "{{#else}}"
						+ "{{other.clazz.name}}Set {{other.name}} = null;",
					"{{#endif}}",
					"",
				"{{#endif}}",
				"",

				"{{#if {{other.cardinality}}==1}}"
						+"{{#import {{other.clazz.fullName}}}}",
				"{{#else}}"
					+ "{{#import {{other.clazz.packageName}}.util.{{other.clazz.name}}Set}}",
				"{{#endif}}"
					+ "	public {{modifiers} }{{#if {{other.cardinality}}==1}}{{other.clazz.name}}{{#else}}{{other.clazz.name}}Set{{#endif}} get{{other.Name}}(){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {", "{{#ifnot {{other.cardinality}}==1}}",
				"		if(this.{{other.name}} == null) {",
				"			return {{other.clazz.name}}Set.EMPTY_SET;",
				"		}",
				"{{#endif}}",
				"		return this.{{other.name}};",
				"	}",
				"",
				"{{#endif}}",

				"{{#if {{other.cardinality}}==1}}",
				"	public {{modifiers} }boolean set{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};","",
				"{{#endif}}",
				
				"{{#ifnot {{file.member.type}}==interface}} {",
				"		if (this.{{other.name}} == value) {", "			return false;", "		}",
				"		{{other.clazz.name}} oldValue = this.{{other.name}};", "{{#if {{type}}==assoc}}",
				"		if (this.{{other.name}} != null) {", "			this.{{other.name}} = null;",
				"{{#if {{cardinality}}==1}}", "			oldValue.set{{Name}}(null);", "{{#else}}",
				"			oldValue.without{{Name}}(this);", "{{#endif}}", "		}", "{{#endif}}",
				"		this.{{other.name}} = value;", "{{#if {{type}}==assoc}}", "		if (value != null) {",
				"			value.with{{Name}}(this);", "		}", "{{#endif}}",
				"{{#if {{#feature PROPERTYCHANGESUPPORT}}}}",
				"		firePropertyChange(PROPERTY_{{other.NAME}}, oldValue, value);", "{{#endif}}",
				"		return true;", "	}", "", "{{#endif}}", "{{#endif}}",
// ASSOCIATION ZU 1
				"{{#if {{other.cardinality}}==1}}",
				"	public {{modifiers} }{{clazz.name}} with{{other.Name}}({{other.clazz.name}} value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {", "		this.set{{other.Name}}(value);",
				"		return this;", "	}", "", "{{#endif}}",

				"{{#else}}",
// ASSOCITAION TO MANY
				//MANY HAS BOOLEAN SET(TYPE...) AND WITH(Object...)
				"	public {{modifiers} }boolean set{{other.Name}}({{other.clazz.name}}... values){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}} {",
				"		if (values == null) {",
				"			return true;",
				"		}",
				"		boolean result=true;",
				"		if (this.{{other.name}} == null) {",
				"			this.{{other.name}} = new {{other.clazz.name}}Set();",
				"		}",
				"		for ({{other.clazz.name}} item : values) {",
				"			if (item == null) {",
				"				continue;",
				"			}",
				"{{#if {{cardinality}}==1}}", // IT IS MANY TO ONE SPECIAL CASE !!!
				
//				if(item.setGame(this)) {
//					result = result & this.field.rawAdd(item);
//					firePropertyChange(PROPERTY_FIELD, null, item);

				
				
				
				"			this.{{other.name}}.withVisible(true);",
				"			boolean changed = this.{{other.name}}.add(item);",
				"			this.{{other.name}}.withVisible(false);",
				"			result = result & changed;",
				"			if (changed) {",
							"{{#debug}}{{#if {{type}}==assoc}}",
				"				item.set{{Name}}(this);",
							"{{#endif}}",
				"				firePropertyChange(PROPERTY_{{other.NAME}}, null, item);",
				"			}",
				"		}",
				"		return result;",
				"	}",
				"{{#endif}}",
				"",
				
				"	public {{modifiers} }{{clazz.name}} with{{other.Name}}(Object... values){{#if {{file.member.type}}==interface}};","","{{#endif}}",
				"{{#ifnot {{file.member.type}}==interface}} {",
				"		if (values == null) {",
				"			return this;",
				"		}",
				"		for (Object item : values) {",
				"			if (item == null) {",
				"				continue;",
				"			}",
				"			if (item instanceof Collection<?>) {{{#import "+Collection.class.getName()+"}}",
				"				Collection<?> collection = (Collection<?>) item;",
				"				set{{other.Name}}(collection.toArray(new {{other.clazz.name}}[collection.size()]));",
				"			} else {",
				"				set{{other.Name}}(({{other.clazz.name}}) item);",
				"			}",
				"		}",
				"		return this;",
				"	}",
				"",
				"{{#endif}}",
// MULTI WITH							

				"	public {{modifiers} }{{clazz.name}} without{{other.Name}}({{other.clazz.name}}... value){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {",
				"		if(this.{{other.name}} == null) {", "			return this;", "		}",
				"		for ({{other.clazz.name}} item : value) {", "			if (item != null) {",
				"{{#if {{type}}==assoc}}", "				if (this.{{other.name}}.remove(item)) {",
				"{{#if {{cardinality}}==1}}", "					item.with{{Name}}(null);", "{{#else}}",
				"					item.without{{Name}}(this);", "{{#endif}}", "				}", "{{#else}}",
				"				this.{{other.name}}.remove(item);", "{{#endif}}", "			}", "		}",
				"		return this;", "	}", "", "{{#endif}}", "{{#endif}}",

				"{{#ifnot {{other.clazz.type}}==interface}}", "{{#ifnot {{other.clazz.modifiers#contains(abstract)}}}}",
				"	public {{modifiers} }{{other.clazz.name}} create{{other.Name}}(){{#if {{file.member.type}}==interface}};",
				"", "{{#endif}}", "{{#ifnot {{file.member.type}}==interface}} {",
				"		{{other.clazz.name}} value = new {{other.clazz.name}}();", "		with{{other.Name}}(value);",
				"		return value;", "	}",
				"",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}",
				"{{#endif}}{{#endtemplate}}");
	}
}
