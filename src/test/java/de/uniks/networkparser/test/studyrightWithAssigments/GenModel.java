/*
   Copyright (c) 2013 ulno (http://contact.ulno.net)

   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.

   The Software shall be used for Good, not Evil.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.test.studyrightWithAssigments;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryStepSourceCode;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Parameter;

public class GenModel {
	/**
	 * @see <a href=
	 *      '../../../../../../../../doc/StudyRightWithAssignmentsClassGeneration.html'>StudyRightWithAssignmentsClassGeneration.html</a>
	 */
	@Test
	public void testStudyRightWithAssignmentsClassGeneration() {
		if(Os.isGenerator()== false) {
			return;
		}
		/*
		 * This file will generate that necessary classes and class diagram for the
		 * StudyRight with Assignments example in the Story Driven Modeling book
		 */

		Story story = new Story();

		// ============================================================
		story.addText("1. generate class University");

		StoryStepSourceCode code = story.addSourceCode(GenModel.class,
				StoryStepSourceCode.CURRENTPOSITION);
		ClassModel model = new ClassModel("org.sdmlib.test.examples.studyrightWithAssignments.model");

		Clazz universityClass = model.createClazz("University").withAttribute("name", DataType.STRING);
		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.addDiagram(model);

		// ============================================================
		story.addText("2. generate class Student");

		code = story.addSourceCode(GenModel.class, StoryStepSourceCode.CURRENTPOSITION);
		Clazz studentClass = model.createClazz("Student").withAttribute("name", DataType.STRING)
				.withAttribute("id", DataType.STRING).withAttribute("assignmentPoints", DataType.INT)
				.withAttribute("motivation", DataType.INT).withAttribute("credits", DataType.INT);
		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.addDiagram(model);

		// ============================================================
		story.addText("3. add University --> Student association");

		// Association universityToStudent =
		code = story.addSourceCode(GenModel.class, StoryStepSourceCode.CURRENTPOSITION);
		universityClass.withBidirectional(studentClass, "students", Association.MANY, "university", Association.ONE);
		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.addDiagram(model);

		// ============================================================
		story.addText("4. add University --> Room association");

		code = story.addSourceCode(GenModel.class, StoryStepSourceCode.CURRENTPOSITION);
		Clazz roomClass = model.createClazz("Room").withAttribute("name", DataType.STRING)
				.withAttribute("topic", DataType.STRING).withAttribute("credits", DataType.INT);

		roomClass.withMethod("findPath", DataType.STRING, new Parameter(DataType.INT).with("motivation"));

		// Association universityToRoom =
		universityClass.createBidirectional(roomClass, "rooms", Association.MANY, "university", Association.ONE)
				.with(AssociationTypes.AGGREGATION);

		// Association doors =
		roomClass.withBidirectional(roomClass, "doors", Association.MANY, "doors", Association.MANY);

		// Association studentsInRoom =
		studentClass.withBidirectional(roomClass, "in", Association.ONE, "students", Association.MANY);
		studentClass.withBidirectional(studentClass, "friends", Association.MANY, "friends", Association.MANY);

		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.addDiagram(model);

		// ============================================================
		story.addText("5. add assignments:");

		code = story.addSourceCode(GenModel.class, StoryStepSourceCode.CURRENTPOSITION);
		Clazz assignmentClass = model.createClazz("Assignment").withAttribute("content", DataType.STRING)
				.withAttribute("points", DataType.INT)
				.withBidirectional(roomClass, "room", Association.ONE, "assignments", Association.MANY);

		studentClass.withBidirectional(assignmentClass, "done", Association.MANY, "students", Association.MANY);
		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.addDiagram(model);

		studentClass.withBidirectional(studentClass, "friends", Association.MANY, "friends", Association.MANY);

		// some more classes for model navigation tests
		studentClass.withBidirectional(studentClass, "friends", Association.MANY, "friends", Association.MANY);

		model.createClazz("TeachingAssistant").withSuperClazz(studentClass)
				.withBidirectional(roomClass, "room", Association.ONE, "tas", Association.MANY)
				.withAttribute("certified", DataType.BOOLEAN);

		Clazz presidentClass = model.createClazz("President");
		universityClass.createBidirectional(presidentClass, "president", Association.ONE, "university", Association.ONE)
				.with(AssociationTypes.AGGREGATION);

		// ============================================================
		story.addText("6. generate class source files.");

//		model.removeAllGeneratedCode("src/test/java");

		model.setAuthorName("zuendorf");
		code = story.addSourceCode(GenModel.class, StoryStepSourceCode.CURRENTPOSITION);
//      model.generate("src/test/java"); // usually don't specify anything here, then it goes into src
		code.withEnd(StoryStepSourceCode.CURRENTPOSITION);

		story.dumpHTML();
//TODO TEST DUSPOTATUIONs		model.getGenerator().withFeature(Feature.getStandAlone().with(Feature.PATTERN));
		model.getGenerator().testGeneratedCode("src/test/java");
	}

}
