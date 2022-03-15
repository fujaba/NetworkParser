package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.Os;

public class OsTest {

	@Test
	public void testOs() {
		Object result = Os.isHeadless(); 
		assertNotNull(result);
	}
}
