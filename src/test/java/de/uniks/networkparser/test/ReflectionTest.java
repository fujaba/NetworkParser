package de.uniks.networkparser.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.generic.ReflectionBackBoxTester;

public class ReflectionTest {
	@Test
	public void testReflection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		ReflectionBackBoxTester tester = new ReflectionBackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
//		.withLogLevel(NetworkParserLog.LOGLEVEL_INFO);
		tester.test("de.uniks.networkparser", logger);
	}
}