package de.uniks.networkparser.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class ReflectionTest {
	@Test
	public void testReflection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL).withListener(new OutputCondition());
		tester.test("de.uniks.networkparser", logger);
		Assert.assertNotNull(tester);
		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}

	@Test
	public void testReflectionVersion() throws Exception {
//		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
//		.withLogLevel(NetworkParserLog.LOGLEVEL_INFO);
		tester.test("de.uniks.networkparser.bytes.qr.Version", logger);
//		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}
	@Test
	public void testDataType() throws Exception {
//		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.test.model.TestClass", logger);
//		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}

	@Test
	public void testSQLTOKENER() throws Exception {
//		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		logger.withListener(new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;

//				Object exception = event.getModelValue();
//				Object method = event.getSource();
//				String msg = (String) event.getNewValue();


				Assert.assertNotNull(event);

				return false;
			}
		});
		tester.test("de.uniks.networkparser.ext.sql.SQLTokener", logger);
//		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}

	@Test
	public void testDiagramEditor() throws Exception {
//		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.ext.DiagramEditor", logger);
//		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}

	@Test
	public void testSMTPSession() throws Exception {
//		System.out.println(Thread.activeCount());
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog()
				.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;
				Assert.assertNotNull(event);
//				System.out.println(event.getNewValue());
				// TODO Auto-generated method stub
				return false;
			}
		});
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
		tester.test("de.uniks.networkparser.ext.io.MessageSession", logger);
//		System.out.println(""+System.currentTimeMillis()+" FINISH:"+Thread.activeCount());
	}
}