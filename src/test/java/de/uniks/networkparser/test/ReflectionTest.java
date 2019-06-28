package de.uniks.networkparser.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class ReflectionTest {
	public StringPrintStream output = new StringPrintStream();

	@Test
	public void testReflection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
//		logger.withFlag(NetworkParserLog.LOGLEVEL_ALL).withListener(output);
		logger.withFlag(NetworkParserLog.LOGLEVEL_ERROR).withListener(output);
		logger.withoutFlag(NetworkParserLog.LOGLEVEL_INFO);
		tester.withDisableClassError(true);
		tester.withDisableSimpleException(true);
		tester.breakByErrorCount(100);

		tester.test("de.uniks.networkparser", logger);
		
		
		tester.printResult(NetworkParserLog.LOGLEVEL_ERROR);
	}

	@Test
	public void testReflectionVersion() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		
		tester.test("de.uniks.networkparser.bytes.qr.Version", logger);
	}
	
	@Test
	public void testReflectionStoryStepSourceCode() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		logger.withFlag(NetworkParserLog.LOGLEVEL_ERROR).withListener(output);
		logger.withoutFlag(NetworkParserLog.LOGLEVEL_INFO);

		tester.test("de.uniks.networkparser.ext.story.StoryStepSourceCode", logger);
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
	}

	@Test
	public void testDiagramEditor() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog();
		tester.test("de.uniks.networkparser.ext.DiagramEditor", logger);
	}

	@Test
	public void testJavaBridge() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger = new NetworkParserLog().withListener(new StringPrintStream());
		logger.withoutFlag(NetworkParserLog.LOGLEVEL_INFO);
		tester.withLogger(logger);
		tester.testClass(new JavaBridge(), JavaBridge.class, tester.getMethods(""));
	}

	@Test
	public void testSMTPSession() throws Exception {
		ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
		NetworkParserLog logger=new NetworkParserLog()
				.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				SimpleEvent event = (SimpleEvent) value;
				Assert.assertNotNull(event);
				return false;
			}
		});
//				.withFlag(NetworkParserLog.LOGLEVEL_ALL);
		tester.test("de.uniks.networkparser.ext.io.MessageSession", logger);
	}
	
	@Test
	public void createInstance() {
		Object result;
		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.bytes.qr.Version.class);
		Assert.assertNotNull(result);
		
		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.javafx.ModelListenerProperty.class);
		Assert.assertNotNull(result);

//		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.javafx.DiagramController.class);
//		Assert.assertNotNull(result);

		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.SimpleController.class);
		Assert.assertNotNull(result);

		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.RESTServiceTask.class);
		Assert.assertNotNull(result);

		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.petaf.TimerExecutor.class);
		Assert.assertNotNull(result);

//		result = ReflectionLoader.newInstanceSimple(de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroadCast.class);
//		Assert.assertNotNull(result);
	}
}