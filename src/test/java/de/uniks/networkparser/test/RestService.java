package de.uniks.networkparser.test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class RestService {

	public static void main(String[] args) {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withCreator(new RoomCreator());
		
		University uni = new University();
		uni.withName("Uni Kassel");
		Student stefan = new Student();
		stefan.withName("Stefan");
		
		Room room=new Room().withName("MathRoom");
		stefan.withIn(room);
		
		uni.withStudents(stefan);
		Student albert = new Student().withName("Albert");
		
		uni.withStudents(albert);
		
		Thread t2 = new Thread( new RESTServiceTask(8080, map, uni) );
		t2.start();
		
		//http://localhost:8080/json/
		//http://localhost:8080/json/students/
		//http://localhost:8080/json/students[0]
		//http://localhost:8080/json/students[0]/in
	}
}
