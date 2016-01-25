package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Or;

public class MemoryTest {
	@Test
	public void testOr() {
		// Add Structure
		Or or = new Or();
		for(int i=0;i<100;i++) {
			or.add(BooleanCondition.value(true));
		}
		// Test Memory Size
//		System.out.println("START");
		for(int i=0;i<10000;i++) {
			or.toString();
		}
//		System.out.println("FINISH");
	}
}
