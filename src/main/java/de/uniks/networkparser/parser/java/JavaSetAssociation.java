package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.parser.Template;

public class JavaSetAssociation extends Template{
	public JavaSetAssociation() {
		this.id = "association";
		this.type = Template.DECLARATION;
		this.withTemplate(
				"{{#template VALUE}}",
				"{{#if {{other.isImplements}}==false}}",
				"{{#import {{file.member.fullName}}}}" +
				"	public {{other.clazz.name}}Set get{{other.Name}}({{other.clazz.name}}... filter) {",
				"		{{other.clazz.name}}Set result = new {{other.clazz.name}}Set();",
				"{{#if {{#feature PATTERN}}}}",
				"		if(listener != null) {",
							"{{#import "+SimpleEvent.class.getName()+"}}",
				"			result.withListener(listener);",
				"			{{file.member.name}}[] children = this.toArray(new {{file.member.name}}[size()]);",
				"			for(int i=0;i<children.length;i++) {",
				"				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].get{{other.Name}}(), filter));",
				"			}",
				"			return result;",
				"		}",
				"{{#endif}}",
				"		if(filter == null || filter.length<1) {",
				"			for ({{file.member.name}} obj : this) {",
								"{{#if {{other.cardinality}}==1}}",
				"				result.add(obj.get{{other.Name}}());",
								"{{#else}}",
				"				result.addAll(obj.get{{other.Name}}());",
								"{{#endif}}",
				"			}",
				"			return result;",
				"		}",
				"		for ({{file.member.name}} obj : this) {",
							// TO ONE CARDINALITY
							"{{#if {{other.cardinality}}==1}}",
				"			{{other.clazz.name}} item = obj.get{{other.Name}}();",
				"			if(item != null) {",
				"				for(int i=0;i<filter.length;i++) {",
				"					if (item.equals(filter[i])) {",
				"						result.add(item);",
				"						break;",
				"					}",
				"				}",
				"			}",
							"{{#else}}",
							// TO MANY CARDINALITY
				"			{{other.clazz.name}}Set item = obj.get{{other.Name}}();",
				"			if(item != null) {",
				"				for(int i=0;i<filter.length;i++) {",
				"					if (item.contains(filter[i])) {",
				"						result.add(filter[i]);",
				"						break;",
				"					}",
				"				}",
				"			}",
							"{{#endif}}",
				"		}",
				"		return result;",
				"	}","","",

				"	public {{file.member.name}}Set with{{other.Name}}({{other.clazz.name}} value) {",
				"		for ({{file.member.name}} obj : this) {",
				"			obj.with{{other.Name}}(value);",
				"		}",
				"		return this;",
				"	}","",

				"{{#import {{other.clazz.fullName}}}}" +
				"{{#if {{other.cardinality}}==MANY}}",
				"	public {{file.member.name}}Set without{{other.Name}}({{other.clazz.name}} value) {",
				"		for ({{file.member.name}} obj : this) {",
				"			obj.without{{other.Name}}(value);",
				"		}",
				"		return this;",
				"	}","",
				"{{#endif}}",
				"{{#endif}}{{#endtemplate}}");
	}
}
