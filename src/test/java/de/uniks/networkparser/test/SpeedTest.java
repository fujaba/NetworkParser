package de.uniks.networkparser.test;

import java.util.Collection;

import org.junit.Test;

import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SpeedList;

public class SpeedTest {
	public static final int SIZE_SMALL = 100000;
	@Test
	public void testSpeed() {
		for(int i=0;i<10;i++) {
			testing(new SpeedList<Integer>());
			testing(new SimpleList<Integer>());
			
		}
	}
	public void testing(Collection<Integer> collection) {
//		long start = System.currentTimeMillis();
		for(int i=0;i<SIZE_SMALL;i++) {
			collection.add(collection.size());
		}
	}
}
