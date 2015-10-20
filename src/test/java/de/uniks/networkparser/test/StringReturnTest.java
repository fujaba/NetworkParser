package de.uniks.networkparser.test;

import org.junit.Test;

public class StringReturnTest {

	@Test
	public void testExam() {
		System.out.println(System.identityHashCode(TestA()));
	}
	
	private String TestA() {
		String f = TestB();
		System.out.println(System.identityHashCode(f));
		return f;
	}
	private String TestB() {
		String w = ""+2222;
		System.out.println(System.identityHashCode(w));
		return w;
	}
}
