package de.uniks.networkparser.test;

import java.io.PrintStream;
import org.junit.Test;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.interfaces.Condition;

public class FilterModell {

	@Test
	public void testModell() {
		Clazz clazz = new Clazz().with("Student");
		clazz.createAttribute("name", DataType.STRING);
		clazz.createAttribute("age", DataType.INT);
		PrintStream output = null; //System.out;
//		output = System.out;
//		SimpleSet<Attribute> filterAttributes = clazz.getAttributes().each(value -> "name".equals(value.getName()));

		AttributeSet filterAttributesAll = clazz.getAttributes();
		for (Attribute attribute : filterAttributesAll) {
			if (output != null) {
				output.println("All: " + attribute.getName());
			}
		}

		AttributeSet filterAttributesA = clazz.getAttributes().filter(Attribute.NAME.equals("name"));
		for(Attribute attribute : filterAttributesA) {
			if(output != null) {
				output.println("Equals: "+attribute.getName());
			}
		}

		AttributeSet filterAttributesB = clazz.getAttributes().hasName("name");
		for(Attribute attribute : filterAttributesB) {
			if(output != null) {
				output.println("Equals: "+attribute.getName());
			}
		}

		AttributeSet filterAttributesC = clazz.getAttributes().filter(Attribute.NAME.not("name")).filter(new Condition<Attribute>() {
			@Override
			public boolean update(Attribute value) {
				return value.getClazz() != null;
			}
		});
		for(Attribute attribute : filterAttributesC) {
			if(output != null) {
				output.println("Not: "+attribute.getName());
			}
		}

//		SimpleSet<Attribute> filterAttributesA = clazz.getAttributes().has(u"name", Attribute.NAME, Condition.EQUALS);
//		SimpleSet<Attribute> filterAttributesB = clazz.getAttributes(StringFilter.equalsIgnoreCase(Attribute.PROPERTY_NAME, "name"));
//		filterAttributesA.get
	}

}
