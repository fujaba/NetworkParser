package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uniks.networkparser.ext.Os;

public class OsTest {

	@Test
	public void testOs() {
		Object result = Os.isHeadless(); 
		assertNotNull(result);
	}
}
