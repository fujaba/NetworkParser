package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;

public class FeatureTest {
	@Test
	public void testAndroid() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.androidmodel");
		Clazz uni = model.createClazz("University");
		Clazz student = model.createClazz("Student");
		student.withAttribute("name", DataType.STRING);

		uni.withBidirectional(student, "student", Association.MANY, "uni", Association.ONE);


		// Remove Dependency from SDMLib
//		model.withoutFeature(Feature.PATTERNOBJECT);
		// So Only Networkparser is a Dependency, add it per Maven de.uniks:NetworkParser:Core

		// Remove all Dependency
		model.withoutFeature(Feature.SERIALIZATION);
		model.withoutFeature(Feature.SETCLASS);
//		model.getFeature(Feature.SETCLASS).withClazzValue(LinkedHashSet.class);

		model.generate("src/test/java");


	}
}
