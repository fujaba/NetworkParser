package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SpeedList;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.HouseCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class SimpleUsage {
	@Test
	public void testSerialization() {
		// tag::serialization[]
		Story story= new Story();
		story.addSourceCode(SimpleUsage.class);
		// Model
		House house=new House(); //<1>
		house.setFloor(4);
		house.setName("University");

		// Serialization
		IdMap map=new IdMap().withCreator(new HouseCreator()); //<2>
		map.withTimeStamp(1);
		JsonObject json = map.toJsonObject(house);
		String string=json.toString(2); //<4>

		// Deserialization
		IdMap decodeMap=new IdMap().withCreator(new HouseCreator()); //<3>
		House newHouse = (House) decodeMap.decode(string);

		newHouse.setFloor(42);
		story.finish();

		story.addDescription("1", "Custom Model");
		story.addDescription("2", "Serialization");
		story.addDescription("3", "Deserialization");
		story.addDescription("4", string);

		story.writeToFile();
		
		
		// end::serialization[]
	}

	@Test
	public void testUpdateChange() throws IOException {
		// tag::PropertyChange[]
		// Model
		University uni=new University(); //<1>
		uni.setName("Uni Kassel");



		// Serialization
		IdMap map=new IdMap().withCreator(new UniversityCreator(), new StudentCreator()); //<2>
		map.withTimeStamp(1);
		// Add UpdateListener
		map.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;
				assertNotNull(event);
				return true;
			}
		});

		JsonObject json = map.toJsonObject(uni);
		assertNotNull(json);

		Student albert = new Student().withName("Albert");
		uni.withStudents(albert);
		// end::PropertyChange[]
	}
	
	@Test
	public void testSpeedList() {
		SpeedList<Integer> list=new SpeedList<Integer>();
		int i=1;
		while(i<=1000) {
			list.add(i);
			i++;
		}
		list.pack();
		while(i<=1000000) {
			list.add(i); 
			i++;
		}
		
	}
}
