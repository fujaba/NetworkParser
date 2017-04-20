package de.uniks.template.generator.java;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.template.Template;
import de.uniks.template.generator.BasicGenerator;

public class JavaAttribute extends BasicGenerator{
	
	public JavaAttribute() {
		createTemplate("PROPERTY", Template.FIELD, "   {{propertyVisibility}} {{propertyModifiers}} {{propertyType}} {{PROPERTY_NAME}} = \"{{name}}\";");
		
		Template fieldTemplate = new Template()
				.withTemplate(""
						+ "{{#ifnot {{member.type}}==INTERFACE}}"+
								"{{fieldVisibility}} {{fieldModifiers}}{{#if fieldModifiers}} {{#endif}}{{value}} {{name}} = {{default}};"+
							"{{#endif}}")
				.withType(Template.FIELD);
//				.withCondition("");
		
		Template getMethodTemplate = new Template()
				.withPrevTemplate(fieldTemplate)
				.withTemplate("   {{methodVisibility}} {{getModifiers}}{{#if getModifiers}} {{#endif}}{{value}} {{getName}}{{Name}}(){{methodEnd}}",
						 	  "{{#ifnot methodEnd}}",
						 	  "   {",
						 	  "      return this.{{name}};",
						 	  "   }",
						 	  "{{#endif}}")
				.withType(Template.VALUE);
		
		Template setMethodTemplate = new Template()
				.withPrevTemplate(getMethodTemplate)
				.withTemplate("   {{methodVisibility}} {{setModifiers}}{{#if setModifiers}} {{#endif}}void set{{Name}}({{value}} value){{methodEnd}}",
						 	  "{{#ifnot methodEnd}}",
						 	  "   {",
						 	  "      if (this.{{name}} != value)",
						 	  "      {",
						 	  "         {{value}} oldValue = this.{{name}};",
						 	  "         this.{{name}} = value;",
						 	  "         {{firePropertyChange}}",
						 	  "      }",
						 	  "   }",
						 	  "{{#endif}}")
				.withType(Template.VALUE);
		
		Template withMethodTemplate = new Template()
				.withPrevTemplate(setMethodTemplate)
				.withTemplate("   {{methodVisibility}} {{withModifiers}}{{#if withModifiers}} {{#endif}}{{withReturn}} with{{Name}}({{value}} value){{methodEnd}}",
						      "{{#ifnot methodEnd}}",
						      "   {",
						      "      set{{Name}}(value);",
						      "      return this;",
						      "   }",
						      "{{#endif}}")
				.withType(Template.VALUE);
		
		rootTemplate = propertyTemplate;

		return null;

	}
	
	@Override
	public Class<?> getTyp() {
		return Attribute.class;
	}

	@Override
	public SendableEntityCreator generate(GraphMember item, TextItems parameters) {
		if(item instanceof Attribute == false) {
			return null;
		}
		Attribute element = (Attribute) item;
		return null;
	}

}
