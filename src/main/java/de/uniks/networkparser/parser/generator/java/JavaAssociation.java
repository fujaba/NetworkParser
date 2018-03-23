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
				      "{{#ifnot {{#OR}}{{item.name}}=={{file.member.name}} {{item.packagename}}=={{file.member.packagename}}{{#ENDOR}}}}" +
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
				"{{#if {{cardinality}}==1}}",
				"               item.set{{Name}}(null);",
				"{{#else}}",
				"               item.without{{Name}}(this);",
				"{{#endif}}",
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
