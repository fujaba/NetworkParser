package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.ModelHistory;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class testFlipBook {

	@Test
	public void testFlipbook() {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator(), new StudentCreator());
		ModelHistory history = ModelHistory.createLocalHistory(map);

		
		University university = new University();
		
		map.toJsonObject(university);
		
		university.setName("Uni Kassel");

		assertEquals(university.getName(), "Uni Kassel");
		
		history.back();
		
		assertNotSame(university.getName(), "Uni Kassel");
		
		history.forward();
		
		assertEquals(university.getName(), "Uni Kassel");
	}
}
