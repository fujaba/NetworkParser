package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.GraphPatternMatch;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class GraphPatternTest {
	@Test
	public void testMatch() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());

		University uniA = new University().withName("Uni Kassel");
		University uniB = new University().withName("Uni Kassel");
		
		GraphPatternMatch diff = mapA.getDiff(uniA, uniB, false);
		
		Assert.assertEquals(0, diff.size());
	}

	@Test
	public void testMatchWithWrongName() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());

		University uniA = new University().withName("Uni Kassel");
		University uniB = new University().withName("Uni Paderborn");
		
		GraphPatternMatch diff = mapA.getDiff(uniA, uniB, true);
		
		Assert.assertEquals(1, diff.size());
	}
	@Test
	public void testMatchWithStudents() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());

		University uniA = new University().withName("Uni Kassel");
		University uniB = new University().withName("Uni Paderborn");
		uniB.withStudents(new Student().withName("Alex"));
		
		GraphPatternMatch diff = mapA.getDiff(uniA, uniB, true);
		
		Assert.assertEquals(2, diff.size());
	}
	@Test
	public void testMatchWithCicle() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());
		mapA.with(new RoomCreator());

		University uni = new University().withName("Uni Paderborn");
		Student student = new Student().withName("Alex");
		Room room = new Room().withName("Mathroom").withStudents(student);
		uni.withStudents(student);
		uni.withRooms(room);
		
		University uniB = new University().withName("Uni Kassel");
		Student studentB = new Student().withName("Stefan");
		Room roomB = new Room().withName("Mathroom").withStudents(studentB);
		uniB.withRooms(roomB);

		GraphPatternMatch diff = mapA.getDiff(uni, uniB, true);
		
		Assert.assertEquals(3, diff.size());
	}
}
