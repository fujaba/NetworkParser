package de.uniks.networkparser.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.util.HouseCreator;

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
}
