package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class TestIssues {
	@Test
	public void testRemoveObj_8(){
	   University uni = new University().withName("UniKasselVersitaet");
	   uni.createStudents().withName("Peter");
	   uni.createStudents().withName("Paul");
	   uni.createStudents().withName("Mary");

	   IdMap idMap = UniversityCreator.createIdMap("u");
	   idMap.toJsonArray(uni);
//	   uni.removeYou();
	}
}
