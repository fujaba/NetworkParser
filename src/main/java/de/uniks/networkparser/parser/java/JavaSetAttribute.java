package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.parser.Template;

public class JavaSetAttribute extends Template {
	public JavaSetAttribute() {
		this.id = "attribute";
		this.type = VALUE;
		this.withTemplate(
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}" +
					"{{#import {{type(false)}}}}"

					
				+"{{#if {{#feature PATTERN}}}}"
// SWITCH FOR LIST SO NO PARAMETER
				+ "	public {{#listType}} {{#if {{type}}==boolean ? is : get}}{{Name}}({{#if {{typecat}}!=SET ? {{type}}... filter}}) {"
				+"",
				"		{{#listType}} result = new {{#listType}}();",
				"		if(listener != null) {",
				"{{#import " + SimpleEvent.class.getName() + "}}",
				"			result.withListener(listener);",
				"			{{file.member.name}}[] children = this.toArray(new {{file.member.name}}[size()]);",
				"			for(int i=0;i<children.length;i++) {",
				"				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].{{namegetter}}(), {{#if {{typecat}}!=SET ? filter : null}}));",
				"			}",
				"			return result;",
				"		}",
				"{{#if {{typecat}}==SET}}",
				"		for ({{file.member.name}} obj : this) {",
				"			result.add(obj.{{namegetter}}());",
				"		}",
				"{{#else}}",
				"		if(filter == null || filter.length<1) {",
				"			for ({{file.member.name}} obj : this) {",
				"				result.add(obj.{{namegetter}}());",
				"			}",
				"		} else {",
				"			for ({{file.member.name}} obj : this) {",
				"				{{type}} item = obj.{{namegetter}}();",
				"				for(int i=0;i<filter.length;i++) {",
				"{{#if {{#or}}{{type}}==int {{type}}==boolean {{type}}==double {{type}}==float{{#endor}}}}",
				"					if (filter[i] == item) {"
				+ "{{#else}}",
				"					if (filter[i].equals(item)) {"
				+ "{{#endif}}",
				"						result.add(item);",
				"						break;",
				"					}",
				"				}",
				"			}",
				"		}",
				"{{#endif}}",
				"		return result;",
				"	}",
				"{{#endif}}",
				"",
				"{{#if {{type}}!=BOOLEAN {{typecat}}!=SET {{type(false)}}!=java.util.Date}}"
						+ "	public {{file.member.name}}Set filter{{Name}}({{type}} minValue, {{type}} maxValue) {",
				"		{{file.member.name}}Set result = new {{file.member.name}}Set();",
				"		for({{file.member.name}} obj : this) {",
				"			if ({{#if {{type}}==STRING}}minValue.compareTo(obj.get{{Name}}()) <= 0 && maxValue.compareTo(obj.get{{Name}}()) >= 0"
						+"{{#else}}"
						+"{{#if {{type}}==OBJECT}}"
						+"minValue.hashCode() <= obj.get{{Name}}().hashCode() && maxValue.hashCode() >= obj.get{{Name}}().hashCode()"
						+"{{#else}}"
						+"minValue <= obj.get{{Name}}() && maxValue >= obj.get{{Name}}()"
						+"{{#endif}}{{#endif}}) {",
				"				result.add(obj);",
				"			}",
				"		}",
				"		return result;",
				"	}",
				"",
				"",
				"{{#endif}}"+
				"	public {{file.member.name}}Set with{{Name}}({{type}} value) {",
				"		for ({{file.member.name}} obj : this) {",
				"			obj.set{{Name}}(value);",
				"		}",
				"		return this;",
				"	}",
				"" +
				"{{#endif}}");
	}
}
