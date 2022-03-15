package de.uniks.networkparser.test.studyrightWithAssigments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Assignment;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.AssignmentSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.RoomSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.UniversitySet;

import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.PatternCondition;

public class PatternObjects {
	@Test
	public void testStudyRightTables() {
		if(Os.isJavaFX() == false) {
			return;
		}
		// some objects
		University studyRight = new University().withName("Study Right");
		Room mathRoom = new Room().withName("wa1337").withTopic("Math").withUniversity(studyRight);
		Room artsRoom = new Room().withName("wa1338").withTopic("Arts").withUniversity(studyRight);
		Room sportsRoom = new Room().withName("wa1339").withTopic("Football").withUniversity(studyRight);
		
		assertNotNull(mathRoom);
		assertNotNull(artsRoom);
		assertNotNull(sportsRoom);

		Assignment integrals = new Assignment().withContent("integrals").withPoints(42).withRoom(mathRoom);
		Assignment matrix = new Assignment().withContent("matrices").withPoints(23).withRoom(mathRoom);
		Assignment drawings = new Assignment().withContent("drawings").withPoints(12).withRoom(artsRoom);
		Assignment sculptures = new Assignment().withContent("sculptures").withPoints(12).withRoom(artsRoom);

		assertNotNull(integrals);
		assertNotNull(matrix);
		assertNotNull(drawings);
		assertNotNull(sculptures);

		Student alice = new Student().withId("m4242").withName("Alice").withUniversity(studyRight).withIn(artsRoom);
		Student bob   = new Student().withId("m2323").withName("Bobby").withUniversity(studyRight).withIn(artsRoom);
		Student carli = new Student().withId("m2323").withName("Carli").withUniversity(studyRight).withIn(mathRoom);

		assertNotNull(alice);
		assertNotNull(bob);
		assertNotNull(carli);

		// OLD
		RoomSet roomsOLD = studyRight.getRooms();
		
//		DiagramEditor.edobs(studyRight, mathRoom,alice);
		
		AssignmentSet assignmentsOLD = roomsOLD.getAssignments();
		double sumOLD = assignmentsOLD.getPoints().sum();
		assertEquals(roomsOLD.size(), 3);
		assertEquals(assignmentsOLD.size(), 4);
		assertEquals(sumOLD, 89.0);

		// some tables
//		PatternCondition patternCondition = PatternCondition.createPatternPair(new studyRight.getRooms());
//		RoomSet rooms = (RoomSet) patternCondition.getRoot();
		UniversitySet set=new UniversitySet();
		set.add(studyRight);

		PatternCondition patternCondition = PatternCondition.createPatternPair(set);
		set = (UniversitySet) patternCondition.getRoot();

		RoomSet rooms = set.getRooms();

		AssignmentSet assignments = rooms.getAssignments();
		double sum = assignments.getPoints().sum();
		assertEquals(rooms.size(), 4);
		assertEquals(assignments.size(), 4);
		assertEquals(sum, 89.0);
//*/
	}
}
