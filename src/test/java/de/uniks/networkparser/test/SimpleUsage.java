package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.HouseCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class SimpleUsage {
	@Test
	public void testSerialization() throws IOException { 
		// tag::serialization[]
		// Model
		House house=new House(); //<1>
		house.setFloor(4);
		house.setName("University");

		// Serialization
		IdMap map=new IdMap().withCreator(new HouseCreator()); //<2>
		map.withTimeStamp(1);
		JsonObject json = map.toJsonObject(house);
		String string=json.toString(2); //<4>
		// end::serialization[]
		//NO DOKU
		Files.write(new File("src/test/resources/de/uniks/networkparser/test/serialization.json").toPath(), string.getBytes(), StandardOpenOption.CREATE);
		// tag::serialization[]
		
		// Deserialization
		IdMap decodeMap=new IdMap().withCreator(new HouseCreator()); //<3>
		House newHouse = (House) decodeMap.decode(string);

		newHouse.setFloor(42);
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
		map.withListener(new UpdateListener() {
			
			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;
				System.out.println(event.getEntity().toString());
				return true;
			}
		});
		
		System.out.println("Start:");
		JsonObject json = map.toJsonObject(uni);
		System.out.println("Update:");
		
		Student albert = new Student().withName("Albert");
		uni.withStudents(albert);
		// end::PropertyChange[]
	}
}
