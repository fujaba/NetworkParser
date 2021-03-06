package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
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

		RESTServiceTask task = new RESTServiceTask(8080, map, uni) ;
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\"},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S3\"}]}}", task.executeRequest("/json/"));

		Assert.assertEquals("[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R4\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\"}}},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S3\",\"prop\":{\"name\":\"Albert\",\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\"}}}]",task.executeRequest("/json/students/"));
		//task.executeRequest
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R4\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\"}}}", task.executeRequest("/json/students[0]"));

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R4\",\"prop\":{\"name\":\"MathRoom\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\"}]}}", task.executeRequest("/json/students[0]/in"));

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R4\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\"}}}", task.executeRequest("/json/[S2]"));

		Assert.assertEquals("HTTP 404", task.executeRequest("/json/students[J1.Student.2]"));

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\",\"prop\":{\"name\":\"Stefan\",\"in\":{\"class\":\"de.uniks.networkparser.test.model.Room\",\"id\":\"R4\"},\"university\":{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\"}}}", task.executeRequest("/json/students[S2]"));

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.University\",\"id\":\"U1\",\"prop\":{\"name\":\"Uni Kassel\",\"students\":[{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S2\"},{\"class\":\"de.uniks.networkparser.test.model.Student\",\"id\":\"S3\"}]}}", task.executeRequest("/json/students[0]/in/students/university/"));
	}

	public void RESTTestPOST() {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withTimeStamp(1);
		University uni = new University().withName("Uni Kassel");


		Student stefan = new Student().withName("Stefan");
//		RESTServiceTask task = new RESTServiceTask(8080, map, uni);
		Thread t2 = new Thread( new RESTServiceTask(8080, map, uni) );
		t2.start();

		IdMap mapB = new IdMap();
		mapB.withCreator(new StudentCreator());
		JsonObject jsonObject = mapB.toJsonObject(stefan);

		Assert.assertEquals(uni.getStudents().size(), 0);

		NodeProxyTCP.postHTTP("localhost:8080/students", jsonObject);

		Assert.assertEquals(uni.getStudents().size(), 1);
		Student s = (Student) uni.getStudents().toArray()[0];
		Assert.assertEquals(s.getName(), "Stefan");
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
