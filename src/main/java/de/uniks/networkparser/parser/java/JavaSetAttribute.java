package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.parser.Template;

public class JavaSetAttribute extends Template {
	public JavaSetAttribute() {
		this.id="attribute";
		this.type=VALUE;
		this.withTemplate(
				"{{#if {{#NOT}}{{modifiers#contains(static)}}{{#ENDNOT}}}}"+
					"{{#import {{type(false)}}}}"+
					"	public {{#listType}} {{#if {{type}}==boolean}}is{{#else}}get{{#endif}}{{Name}}({{type}}... filter) {",
					"		{{#listType}} result = new {{#listType}}();",
					"{{#if {{#feature PATTERN}}}}",
					"		if(listener != null) {",
								"{{#import "+SimpleEvent.class.getName()+"}}",
					"			result.withListener(listener);",
					"			{{file.member.name}}[] children = this.toArray(new {{file.member.name}}[size()]);",
					"			for(int i=0;i<children.length;i++) {",
					"				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].{{namegetter}}(), filter));",
					"			}",
					"			return result;",
					"		}",
					"{{#endif}}",
					"		if(filter == null || filter.length<1) {",
					"			for ({{file.member.name}} obj : this) {",
					"				result.add(obj.{{namegetter}}());",
					"			}",
					"		} else {",
					"			for ({{file.member.name}} obj : this) {",
					"				{{type}} item = obj.{{namegetter}}();",
					"				for(int i=0;i<filter.length;i++) {",
										"{{#if {{#or}}{{type}}==int {{type}}==boolean{{#endor}}}}",
					"					if (filter[i] == item) {"+
										"{{#else}}",
					"					if (filter[i].equals(item)) {"+
										"{{#endif}}",
					"						result.add(item);",
					"						break;",
					"					}",
					"				}",
					"			}",
					"		}",
					"		return result;",
					"	}",
					"",
					"{{#if {{#NOT}}{{type}}==BOOLEAN{{#ENDNOT}}}}" +
					"	public {{file.member.name}}Set filter{{Name}}({{type}} minValue, {{type}} maxValue) {",
					"		{{file.member.name}}Set result = new {{file.member.name}}Set();",
					"		for({{file.member.name}} obj : this) {",
					"			if ({{#if {{type}}==STRING}}minValue.compareTo(obj.get{{Name}}()) <= 0 && maxValue.compareTo(obj.get{{Name}}()) >= 0" +
									"{{#else}}minValue <= obj.get{{Name}}() && maxValue >= obj.get{{Name}}(){{#endif}}) {",
					"				result.add(obj);",
					"			}",
					"		}",
					"		return result;",
					"	}",
					"",
					"",
					"{{#endif}}" +
					"{{#if {{type}}==VALUETYPE}}"+
					"	public {{SetName}} filter{{Value}}({{type}} lower, {{type}} upper) {",
					"		{{SetName}} result = new {{SetName}}();",
					"		for ({{name}} obj : this) {",
					"			if (lower{{#if {{type}}==PRIMITIVE}} <= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) <= 0{{#endif}} && upper{{#if {{member.type}}==PRIMITIVE}} >= obj.get{{Name}}(){{#else}}.compareTo(obj.get{{Name}})) >= 0{{#endif}}) {",
					"				result.add(obj);",
					"			}",
					"		}",
					"		return result;",
					"	}","","",
					"{{#endif}}"+
					"	public {{file.member.name}}Set with{{Name}}({{type}} value) {",
					"		for ({{file.member.name}} obj : this) {",
					"			obj.set{{Name}}(value);",
					"		}",
					"		return this;",
					"	}","" +
				"{{#endif}}");
	}
}
