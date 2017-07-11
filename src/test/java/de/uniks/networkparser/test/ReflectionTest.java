package de.uniks.networkparser.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class ReflectionTest {
	@Test
	public void testReflection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
//		.withLogLevel(NetworkParserLog.LOGLEVEL_INFO);
		tester.test("de.uniks.networkparser", logger);
	}
	
	@Test
	public void testReflectionVersion() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
//		.withLogLevel(NetworkParserLog.LOGLEVEL_INFO);
		tester.test("de.uniks.networkparser.bytes.qr.Version", logger);
	}
	@Test
	public void testDataType() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.test.model.TestClass", logger);
	}
	@Test
	public void testSQLTOKENER() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.ext.sql.SQLTokener", logger);
	}
	@Test
	public void testDiagramEditor() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.ext.javafx.DiagramController", logger);
	}
	
	@Test
	public void testSMTPSession() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog()
				.withListener(new ObjectCondition() {
			
			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;
				System.out.println(event.getNewValue());
				// TODO Auto-generated method stub
				return false;
			}
		});
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
		tester.test("de.uniks.networkparser.ext.email.SMTPSession", logger);
	}
}