package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.GraphPatternMatch;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class GraphPatternTest {
	@Test
	public void testMatch() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());

//		IdMap mapB = new IdMap();
//		mapB.with(new StudentCreator());
//		mapB.with(new UniversityCreator());
		
		University uniA = new University().withName("Uni Kassel");
		
		University uniB = new University().withName("Uni Kassel");
		
		GraphPatternMatch diff = mapA.getDiff(uniA, uniB, false);
		
		Assert.assertEquals(0, diff.size());

	}
}
