package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.http.Configuration;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;
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
		map.withTimeStamp(1);

		University uni = new University().withName("Uni Kassel");

		Student stefan = new Student().withName("Stefan");
		stefan.withIn(new Room().withName("MathRoom"));
		uni.withStudents(stefan);

		Student albert = new Student().withName("Albert");
		uni.withStudents(albert);

		RESTServiceTask task = new RESTServiceTask().createServer(new Configuration().withPort(8080), map, uni);
		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S1\"},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\"}]}}", task.executeRequest("/json/"));

		assertEquals("[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S1\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R3\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\"}}},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Albert\",\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\"}}}]",task.executeRequest("/json/students/"));
		//task.executeRequest
		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S1\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R3\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\"}}}", task.executeRequest("/json/students[0]"));

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R3\",\"prop\":{\"name\":\"MathRoom\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S1\"}]}}", task.executeRequest("/json/students[0]/in"));

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Albert\",\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\"}}}", task.executeRequest("/json/[S2]"));

		assertEquals("HTTP 404", task.executeRequest("/json/students[J1.Student.2]"));

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Albert\",\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\"}}}", task.executeRequest("/json/students[S2]"));

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"root\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S1\"},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\"}]}}", task.executeRequest("/json/students[0]/in/students/university/"));
	}

	public void RESTTestPOST() {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withTimeStamp(1);
		University uni = new University().withName("Uni Kassel");


		Student stefan = new Student().withName("Stefan");
		new RESTServiceTask().createServer(new Configuration().withPort(8080), map, uni);

		IdMap mapB = new IdMap();
		mapB.withCreator(new StudentCreator());
		JsonObject jsonObject = mapB.toJsonObject(stefan);

		assertEquals(uni.getStudents().size(), 0);

		NodeProxyTCP.postHTTP("localhost:8080/students", (BaseItem) jsonObject);

		assertEquals(uni.getStudents().size(), 1);
		Student s = (Student) uni.getStudents().toArray()[0];
		assertEquals(s.getName(), "Stefan");
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

		RESTServiceTask task = new RESTServiceTask();
		task.createServer(new Configuration().withPort(8080), map, uni);
	}
}
