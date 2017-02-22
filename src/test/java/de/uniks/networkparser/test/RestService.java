package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class RestService {
	
	@Test
	public void RESTTest() {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withCreator(new RoomCreator());
		
		University uni = new University().withName("Uni Kassel");

		Student stefan = new Student().withName("Stefan");
		stefan.withIn(new Room().withName("MathRoom"));
		uni.withStudents(stefan);
		
		Student albert = new Student().withName("Albert");
		uni.withStudents(albert);

		RESTServiceTask task = new RESTServiceTask(8080, map, uni) ;
		Assert.assertEquals("{\"id\":\"J1.University.1\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"id\":\"J1.Student.2\"},{\"id\":\"J1.Student.3\"}]}}", task.executeRequest("/json/"));

		Assert.assertEquals("[{\"id\":\"J1.Student.2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"id\":\"J1.Room.4\"},\"university\":{\"id\":\"J1.University.1\"}}},{\"id\":\"J1.Student.3\",\"prop\":{\"name\":\"Albert\",\"university\":{\"id\":\"J1.University.1\"}}}]",task.executeRequest("/json/students/"));
		//task.executeRequest
		Assert.assertEquals("{\"id\":\"J1.Student.2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"id\":\"J1.Room.4\"},\"university\":{\"id\":\"J1.University.1\"}}}", task.executeRequest("/json/students[0]"));

		Assert.assertEquals("{\"id\":\"J1.Room.4\",\"prop\":{\"name\":\"MathRoom\",\"students\":[{\"id\":\"J1.Student.2\"}]}}", task.executeRequest("/json/students[0]/in"));

		Assert.assertEquals("{\"id\":\"J1.Student.2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"id\":\"J1.Room.4\"},\"university\":{\"id\":\"J1.University.1\"}}}", task.executeRequest("/json/[J1.Student.2]"));
		
		Assert.assertEquals("{\"id\":\"J1.University.1\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"id\":\"J1.Student.2\"},{\"id\":\"J1.Student.3\"}]}}", task.executeRequest("/json/students[0]/in/students/university/"));
	}
	
	
	public static void main(String[] args) {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withCreator(new RoomCreator());
		
		University uni = new University().withName("Uni Kassel");

		Student stefan = new Student().withName("Stefan");
		stefan.withIn(new Room().withName("MathRoom"));
		uni.withStudents(stefan);
		
		Student albert = new Student().withName("Albert");
		uni.withStudents(albert);
		
		Thread t2 = new Thread( new RESTServiceTask(8080, map, uni) );
		t2.start();
	}
}
