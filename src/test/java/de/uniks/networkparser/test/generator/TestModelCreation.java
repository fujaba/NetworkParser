package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Literal;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Parameter;

public class TestModelCreation {

	/**
	 *
	 * @see <a href='../../../../../../doc/CreateEntireModel.html'>CreateEntireModel.html</a>
	*/
	@Test
	public void testCreateEntireModel() {
		if(Os.isGenerator() == false) {
			return;
		}

		Story story = new Story();

		ClassModel model = new ClassModel("de.uniks.networkparser.test.model.modelling_a");

		// Classes
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");
		Clazz teacher = model.createClazz("Teacher");
		Clazz room = model.createClazz("Room");
		Clazz enumStudent = model.createClazz("StudentEnum").enableEnumeration(new Literal("STUDENT").withValue(42));
		Clazz roomInterface = model.createClazz("roomInterface").enableInterface();

		// Attributes
		person.withAttribute("name", DataType.STRING)
			.withAttribute("age", DataType.INT);
		pupil.withAttribute("credits", DataType.INT);
		teacher.withAttribute("rank", DataType.STRING);
		roomInterface.withAttribute("number", DataType.INT);

		// Methods
		Method teach = teacher.createMethod("teach");
		teach.with(DataType.STRING)
			.withBody("		String teachResult = \"greatResult\";\n"
					+ "		return teachResult;\n");

		person.withMethod("think", DataType.VOID);
		person.withMethod("dontThink", DataType.VOID, new Parameter(DataType.BOOLEAN));
		pupil.withMethod("read", DataType.STRING);

		// Super Classes
		pupil.withSuperClazz(person);
		teacher.withSuperClazz(person);

		// implemented Intefaces
		room.withSuperClazz(roomInterface);

		// Associations
		room.withBidirectional(person, "persons", Association.MANY, "room", Association.ONE);
		room.withBidirectional(pupil, "currentPupils", Association.MANY, "currentRoom", Association.ONE);
		room.withBidirectional(teacher, "currentTeacher", Association.ONE, "currentRoom", Association.ONE);
		pupil.withBidirectional(teacher, "teacher", Association.ONE, "pupils", Association.MANY);

/*FIXME		Assert.assertEquals(1, enumStudent.getAttributes().size());
		model.generate("src/test/java");

		story.addDiagram(model);

		story.dumpHTML();
*/
	}

}
