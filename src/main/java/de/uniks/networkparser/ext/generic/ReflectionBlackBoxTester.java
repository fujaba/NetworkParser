package de.uniks.networkparser.ext.generic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.ext.Gradle;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.http.HTTPRequest;
import de.uniks.networkparser.ext.petaf.ModelThread;
import de.uniks.networkparser.ext.petaf.SimpleTimerTask;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryStepJUnit;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class ReflectionBlackBoxTester.
 *
 * @author Stefan
 */
public class ReflectionBlackBoxTester {
	
	/** The Constant TYPE_NULLVALUE. */
	public static final String TYPE_NULLVALUE = "null";
	
	/** The Constant TYPE_MINVALUE. */
	public static final String TYPE_MINVALUE = "min";
	
	/** The Constant TYPE_MIDDLEVALUE. */
	public static final String TYPE_MIDDLEVALUE = "middle";
	
	/** The Constant TYPE_MAXVALUE. */
	public static final String TYPE_MAXVALUE = "max";
	
	/** The Constant TYPE_RANDOMVALUE. */
	public static final String TYPE_RANDOMVALUE = "random";
	
	/** The Constant TYPE_CUSTOMVALUE. */
	public static final String TYPE_CUSTOMVALUE = "custom";
	
	/** The Constant BLACKBOXTESTER. */
	public static final String BLACKBOXTESTER = "backboxtest";
	
	/** The Constant INSTANCE. */
	public static final String INSTANCE = "instance";
	
	/** The Constant DEFAULTMETHODS. */
	public static final String DEFAULTMETHODS = "";

	private SimpleSet<String> tests = new SimpleSet<String>().with(TYPE_NULLVALUE, TYPE_MINVALUE, TYPE_RANDOMVALUE,
			TYPE_CUSTOMVALUE, TYPE_MIDDLEVALUE);
	private SimpleKeyValueList<String, SimpleSet<String>> ignoreMethods;
	private int errorCount;
	private int successClazzCount;
	private int errorClazzCount;
	private boolean ignoreClassError;
	private boolean ignoreSimpleException;
	private int successCount;
	private String packageName;
	private NetworkParserLog logger = new NetworkParserLog();
	private ObjectCondition custom;
	private long startTime;
	private int oldThreadCount;
	private int breakByErrorCount = -1;
	private boolean overrideLogger;

	/**
	 * Main tester.
	 *
	 * @param args the args
	 */
	public void mainTester(String[] args) {
		Object junitCore = ReflectionLoader.newInstanceStr("org.junit.runner.JUnitCore");
		SimpleSet<Class<?>> testClasses = new SimpleSet<Class<?>>();
		String blackBoxPackage = null;
		String path = "doc/";
		if (junitCore != null && args != null) {
			for (String param : args) {
				if (param == null) {
					continue;
				}
				if (param.startsWith("test=")) {
					param = param.substring(5);
					String[] clazzes = param.split(",");
					for (String item : clazzes) {
						if (item.startsWith(BLACKBOXTESTER)) {
							int pos = item.indexOf('=');
							if (pos > 0) {
								blackBoxPackage = item.substring(pos + 1);
							} else {
								blackBoxPackage = "";
							}
						}
						Class<?> testClazz = ReflectionLoader.getClass(item);
						if (testClazz != null) {
							testClasses.add(testClazz);
						}
					}
				} else if (param.startsWith("path=")) {
					path = param.substring(5);
				}
			}
			if (testClasses.size() < 1 && blackBoxPackage == null) {
				return;
			}
			Class<? extends Object> itemClass = junitCore.getClass();
			Method method = null;
			try {
			} catch (Exception e) {
				try {
					method = itemClass.getDeclaredMethod("run", Class[].class);
				} catch (Exception e1) {
				}
			}
			if (method != null) {
				Class<?>[] list = testClasses.toArray(new Class<?>[testClasses.size()]);
				try {
					method.invoke(junitCore, new Object[] { list });
				} catch (Exception e) {
					logger.error(ReflectionBlackBoxTester.class, "mainTester", "error: " + e.getMessage(), e);
				}
			}
			/* Now Check if BaclkBoxTester activ */
			if (blackBoxPackage != null) {
				StoryStepJUnit storyStepJUnit = new StoryStepJUnit();
				storyStepJUnit.withPackageName(blackBoxPackage);
				storyStepJUnit.executeBlackBoxTest(path);
			}
		}
	}

	/**
	 * Instantiates a new reflection black box tester.
	 */
	public ReflectionBlackBoxTester() {
		ignoreMethods = new SimpleKeyValueList<String, SimpleSet<String>>();

		withIgnoreClazzes(Gradle.class);
		withIgnoreClazzes(FileClassModel.class);
		/* Add for Files */
		withIgnoreClazzes(Story.class, "dumpHTML", "writeFile");
		withIgnoreClazzes(ErrorHandler.class);
		withIgnoreClazzes(StoryStepJUnit.class, "update");
		withIgnoreClazzes(ModelThread.class);

		ignoreMethods.add(DEFAULTMETHODS,
				new SimpleSet<String>().with("show*", "run*", "start*", "execute*", "consume", "subscribe", "main", "withLogger"));
		/* Add for new Threads */
		withIgnoreClazzes(SimpleController.class, "create", "init");
		withIgnoreClazzes(JarValidator.class);
	}

	/**
	 * With ignore clazzes.
	 *
	 * @param metaClass the meta class
	 * @param methods the methods
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withIgnoreClazzes(Class<?> metaClass, String... methods) {
		if (metaClass == null) {
			return this;
		}
		String className = metaClass.getName();
		if (methods == null || methods.length < 1) {
			return withIgnoreClazzes(className);
		}
		for (String method : methods) {
			withIgnoreClazzes(className + ":" + method);
		}
		return this;
	}

	/**
	 * With ignore clazzes.
	 *
	 * @param values the values
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withIgnoreClazzes(String... values) {
		if (values == null) {
			return this;
		}
		for (String item : values) {
			if (item == null) {
				continue;
			}
			int pos = item.indexOf(":");
			if (pos < 0) {
				if (!this.ignoreMethods.contains(item)) {
					this.ignoreMethods.put(item, new SimpleSet<String>());
				}
			} else {
				String clazz = item.substring(0, pos);
				String method = item.substring(pos + 1);
				SimpleSet<String> methods = this.ignoreMethods.get(clazz);
				if (methods instanceof SimpleSet<?>) {
					methods.add(method);
				} else {
					methods = new SimpleSet<String>().with(method);
					this.ignoreMethods.put(clazz, methods);
				}
			}
		}
		return this;
	}

	/**
	 * Checks if is tester.
	 *
	 * @return true, if is tester
	 */
	public static final boolean isTester() {
		String property = System.getProperty("Tester");
		return property != null;
	}

	/**
	 * Sets the tester.
	 *
	 * @return true, if successful
	 */
	public static final boolean setTester() {
		String property = System.getProperty("Tester");
		if (property == null || property.length() < 1) {
			System.setProperty("Tester", "true");
		}
		return true;
	}
	
	/**
	 * Clear tester.
	 *
	 * @return true, if successful
	 */
	public static final boolean clearTester() {
		String property = System.getProperty("Tester");
		if (property != null && property.length() >0) {
			System.setProperty("Tester", null);
		}
		return true;
	}

	/**
	 * Execute.
	 *
	 * @param path the path
	 * @return true, if successful
	 */
	public boolean execute(String... path) {
		try {
			if (path != null && path.length > 0) {
				this.packageName = path[0];
			}
			return test(packageName, null);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Test.
	 *
	 * @param packageName the package name
	 * @param logger the logger
	 * @return true, if successful
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public boolean test(String packageName, NetworkParserLog logger) throws ClassNotFoundException, IOException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String tester = Os.getTester();
		setTester();
		SimpleList<Class<?>> classesForPackage = ReflectionLoader.getClassesForPackage(packageName);
		if (classesForPackage == null) {
			return true;
		}
		errorCount = 0;
		successCount = 0;
		this.packageName = packageName;
		this.logger = logger;
		this.startTime = System.currentTimeMillis();
		Set<Thread> oldThreads = ReflectionLoader.closeThreads(null);
		this.oldThreadCount = oldThreads.size();
		Timer timer = new Timer();
		SimpleSet<String> defaultMethods = this.ignoreMethods.get(DEFAULTMETHODS);

		for (Class<?> clazz : classesForPackage) {
			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}

			SimpleSet<String> methods = getMethods(clazz.getName());
			if (methods != null && methods.size() < 1) {
				continue;
			}
			SimpleTimerTask task = new SimpleTimerTask(Thread.currentThread());
			timer.schedule(task, 2000);
			Object obj = ReflectionLoader.newInstanceSimple(clazz);
			if (obj == null && clazz.isEnum()) {
				continue;
			}
			if (obj == null) {
				obj = ReflectionLoader.newInstanceSimple(clazz);
			}
			if (obj != null) {
				/* Show For Ignore DefaultMethods */

				if (defaultMethods != null) {
					if (methods != null) {
						methods.withList(defaultMethods);
					} else {
						methods = defaultMethods;
					}
				}
				if(!testClass(obj, clazz, methods)) {
					break;
				}
			} else {
				logger.debug(this, "test", "ERROR: DONT INSTANCE: " + clazz.getName());
				output(clazz, "dont instance of " + clazz.getName(), logger, NetworkParserLog.LOGLEVEL_ERROR, null);
			}
			task.withSimpleExit(null);
		}

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (!"gitlab".equalsIgnoreCase(tester)) {
			ReflectionLoader.closeThreads(oldThreads);
		}
		printResult(NetworkParserLog.LOGLEVEL_INFO);
		return true;
	}

	/**
	 * Gets the methods.
	 *
	 * @param className the class name
	 * @return the methods
	 */
	public SimpleSet<String> getMethods(String className) {
		return this.ignoreMethods.get(className);
	}

	/**
	 * Test class.
	 *
	 * @param obj the obj
	 * @param clazz the clazz
	 * @param ignoreMethods the ignore methods
	 * @return true, if successful
	 */
	public boolean testClass(Object obj, Class<?> clazz, SimpleSet<String> ignoreMethods) {
		boolean reg = false;
		Set<Thread> oldThreads = Thread.getAllStackTraces().keySet();
		if (obj == null) {
			return true;
		}
		if (ignoreMethods != null) {
			for (String m : ignoreMethods) {
				if (m != null && m.endsWith("*")) {
					reg = true;
					break;
				}
			}
		}
		Field propertyChangeListener = null;
		try {
			propertyChangeListener = clazz.getDeclaredField("listeners");
			propertyChangeListener.setAccessible(true);
		} catch (Exception e) {
		}
		boolean isValid = true;
		if(this.overrideLogger && this.logger != null) {
			// ADD LOGGER
			try {
				Method withLogger = clazz.getMethod("withLogger", this.logger.getClass());
				withLogger.invoke(obj, this.logger);
			} catch (Exception e) {
			}
			
		}
		
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getDeclaringClass().isInterface()) {
				continue;
			}
			if (ignoreMethods != null && ignoreMethods.contains(m.getName())) {
				continue;
			}
			if (reg) {
				boolean continueFlag = false;
				for (String name : ignoreMethods) {
					if (name != null && name.endsWith("*")) {
						String lowerCase = name.substring(0, name.length() - 1).toLowerCase();
						if (m.getName().toLowerCase().startsWith(lowerCase)) {
							continueFlag = true;
							break;
						}

					}
				}
				if (continueFlag) {
					continue;
				}
			}
			output(this, clazz.getName() + ":" + m.getName(), logger, NetworkParserLog.LOGLEVEL_DEBUG, null);

			Object[] call = null;
			m.setAccessible(true);
			/* mit Null as Parameter */
			Class<?>[] parameterTypes = m.getParameterTypes();
			for (String type : tests) {
				try {
					call = getParameters(m, parameterTypes, type, this);
					if (call != null) {
						if (logger != null) {
							logger.info(this, "CALL", obj.getClass().getName() + ": " + m.getName());
						}
						m.invoke(obj, call);
						successCount++;
					}
				} catch (Exception e) {
					isValid = false;
					if(!saveException(e, clazz, m, call)) {
						return false;
					}
				}
				if (TYPE_NULLVALUE.equals(type)) {
					/* specialcase */
					if ("update".equals(m.getName())) {
						/* So try again */
						try {
							Class<?>[] types = m.getParameterTypes();
							if (types.length == 1) {
								if (types[0] != int.class && types[0] != byte[].class && types[0] != String.class
										&& types[0] != byte.class && types[0] != HTTPRequest.class) {
									m.invoke(obj, new SimpleEvent(this, "TESTER", null, null));
								}
							}
						} catch (Exception e) {
							saveException(e, clazz, m, call);
							isValid = false;
						}
					}
					if (propertyChangeListener != null && "addPropertyChangeListener".equals(m.getName())) {
						/* So try again */
						try {
							propertyChangeListener.set(obj, null);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							m.invoke(obj, call);
							successCount++;
						} catch (Exception e) {
						}
					}
				}
			}
			Set<Thread> newThreads = Thread.getAllStackTraces().keySet();
			if (newThreads.size() > oldThreads.size()) {
				logger.debug(this, "test", "ERROR:" + clazz.getName() + ":" + m.getName());
			}
		}
		for (Field f : clazz.getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object value = f.get(obj);
				if (value == null) {
					output(f, "field null", logger, NetworkParserLog.LOGLEVEL_WARNING, null);
				}
				if (Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				if (value != null) {
					f.set(obj, getNullValue(value.getClass()));
					f.set(obj, value);
				}
			} catch (Exception e) {
			}
		}
		if (obj instanceof SendableEntityCreator) {
			try {
				((SendableEntityCreator) obj).setValue(obj, DEFAULTMETHODS, null, SendableEntityCreator.REMOVE_YOU);
			} catch (Throwable e) {
				Exception e2 = null;
				if (e instanceof Exception) {
					e2 = (Exception) e;
				}
				output(this, "Dont kill: " + obj, logger, NetworkParserLog.LOGLEVEL_WARNING, e2);
			}
		}
		Set<Thread> newThreads = Thread.getAllStackTraces().keySet();
		if (newThreads.size() > oldThreads.size()) {
			logger.debug(this, "test", "ERROR:" + clazz.getName());
		}
		if (isValid) {
			successClazzCount++;
		} else {
			errorClazzCount++;
		}
		return true;
	}

	/**
	 * With test.
	 *
	 * @param value the value
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withTest(String value) {
		this.tests.clear();
		this.tests.add(value);
		return this;
	}

	private boolean saveException(Exception e, Class<?> clazz, Method m, Object[] call) {
		if (clazz == null) {
			return true;
		}
		String line = getLine(packageName, e, clazz.getSimpleName());
		int pos = 1;
		String shortName = "";
		if (line.length() < 1) {
			line = clazz.getName() + ".java:1";
		} else {
			if (line.lastIndexOf(".") > 0) {
				String[] split = line.split("\\.");
				shortName = line.substring(0, line.lastIndexOf(":") - 4) + m.getName() + "(" + split[split.length - 2]
						+ "." + split[split.length - 1] + ")";
				String value = split[split.length - 1];
				if (value.indexOf(":") > 0) {
					pos = Integer.parseInt(value.substring(value.indexOf(":") + 1));
				}
			}
		}
		String causes = "";
		Throwable exception = e;
		if (e.getCause() != null) {
			causes = ": " + e.getCause();
			if (e instanceof InvocationTargetException) {
				exception = ((InvocationTargetException) e).getTargetException();
			}
		} else if (e instanceof InvocationTargetException) {
			exception = ((InvocationTargetException) e).getTargetException();
			causes = ": " + exception.getCause();
		} else if (e.getMessage() != null) {
			causes = ": " + e.getMessage();
		}
		if (!ignoreClassError || pos != 1) {
			/* Check for Exception */
			if (!ignoreSimpleException || exception == null || !(exception instanceof SimpleException)) {
				output(m, "at " + clazz.getName() + causes + " " + shortName, logger, NetworkParserLog.LOGLEVEL_ERROR,
						e);
				errorCount++;
				if(breakByErrorCount>0 && errorCount>=breakByErrorCount) {
					logger.info(this, "saveException", "Error Abort");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the paramto string.
	 *
	 * @param params the params
	 * @return the paramto string
	 */
	public String getParamtoString(Object[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		if (params == null) {
			sb.append(")");
			return sb.toString();
		}
		boolean hasParam = false;
		for (Object item : params) {
			if (hasParam) {
				sb.append(",");
			}
			if (item == null) {
				sb.append("null");
			} else {
				sb.append(item.toString());
			}
			hasParam = true;
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Break by error count.
	 *
	 * @param number the number
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester breakByErrorCount(int number) {
		this.breakByErrorCount = number;
		return this;
	}

	/**
	 * Prints the result.
	 *
	 * @param loglevel the loglevel
	 */
	public void printResult(int loglevel) {
		/* Write out all Results */
		String prefix = "Errors: ";
		if(this.breakByErrorCount>0) {
			prefix = "Errors Abort: ";
		}
		output(this,prefix + errorCount + "/" + (errorCount + successCount) + " " + errorClazzCount + "/"
				+ (errorClazzCount + successClazzCount), logger, loglevel, null);
		if (startTime > 0 && oldThreadCount > 0) {
			output(this, "Time: " + (System.currentTimeMillis() - startTime) + "ms - Thread: " + oldThreadCount + " -> "
					+ Thread.activeCount(), logger, loglevel, null);
		}

	}

	/**
	 * Output.
	 *
	 * @param owner the owner
	 * @param message the message
	 * @param logger the logger
	 * @param logLevel the log level
	 * @param e the e
	 */
	public void output(Object owner, String message, NetworkParserLog logger, int logLevel, Exception e) {
		if (logger != null) {
			logger.log(owner, "output", message, logLevel, e);
		}
	}

	/**
	 * Gets the parameters.
	 *
	 * @param m the m
	 * @param parameters the parameters
	 * @param type the type
	 * @param owner the owner
	 * @return the parameters
	 */
	public static Object[] getParameters(Executable m, Class<?>[] parameters, String type, Object owner) {
		if (parameters == null) {
			return new Object[0];
		}
		int length = parameters.length;
		Object[] objects = new Object[length];
		if (TYPE_NULLVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getNullValue(parameters[i]);
			}
			return objects;

		}
		if (TYPE_MINVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getMinValue(parameters[i]);
			}
			return objects;
		}
		if (TYPE_MAXVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getMaxValue(parameters[i]);
			}
			return objects;
		}
		if (TYPE_RANDOMVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getRandomValue(parameters[i]);
			}
		}
		if (TYPE_MIDDLEVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getMiddleValue(parameters[i]);
			}
		}
		if (TYPE_CUSTOMVALUE.equals(type)) {
			return getCustomValue(m, parameters, owner);
		}
		return objects;
	}

	private static boolean equalsClass(Class<?> clazz, Class<?>... checkClasses) {
		if (checkClasses == null || clazz == null) {
			return true;
		}
		for (Class<?> check : checkClasses) {
			if (check != null && clazz.getName().equals(check.getName())) {
				return true;
			}
		}
		return false;
	}

	private static Object getNullValue(Class<?> clazz) {
		if (clazz != null && clazz.isPrimitive()) {
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return false;
			}
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return (byte) 0;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return (short) 0;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return 0;
			}
			if (equalsClass(clazz, long.class, Long.class)) {
				return 0L;
			}
			if (equalsClass(clazz, char.class, Character.class)) {
				return '\u0000';
			}
			if (equalsClass(clazz, float.class, Float.class)) {
				return 0.0f;
			}
			if (equalsClass(clazz, double.class, Double.class)) {
				return 0.0d;
			}
		} else if (equalsClass(clazz, String.class, CharSequence.class)) {
			return null;
		}
		return null;
	}

	private static Object getMinValue(Class<?> clazz) {
		if (clazz != null && clazz.isPrimitive()) {
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return false;
			}
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return Byte.MIN_VALUE;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return Integer.MIN_VALUE;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return Short.MIN_VALUE;
			}
			if (equalsClass(clazz, long.class, Long.class)) {
				return Long.MIN_VALUE;
			}
			if (equalsClass(clazz, char.class, Character.class)) {
				return Character.MIN_VALUE;
			}
			if (equalsClass(clazz, float.class, Float.class)) {
				return Float.MIN_VALUE;
			}
			if (equalsClass(clazz, double.class, Double.class)) {
				return Double.MIN_VALUE;
			}
		} else if (equalsClass(clazz, String.class, CharSequence.class)) {
			return "";
		}
		return null;
	}

	private static Object[] getCustomValue(Executable exec, Class<?>[] clazz, Object owner) {
		if (clazz == null) {
			return new Object[0];
		}
		Object[] items = new Object[clazz.length];
		if (owner != null && owner instanceof ReflectionBlackBoxTester) {
			ReflectionBlackBoxTester tester = (ReflectionBlackBoxTester) owner;
			ObjectCondition customListener = tester.getCustom();
			if (customListener != null) {
				customListener.update(new SimpleEvent(exec, "parameter", null, items));
				return items;
			}
		}
		return null;
	}

	/**
	 * Gets the custom.
	 *
	 * @return the custom
	 */
	public ObjectCondition getCustom() {
		return custom;
	}

	/**
	 * With custom.
	 *
	 * @param condition the condition
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withCustom(ObjectCondition condition) {
		this.custom = condition;
		return this;
	}

	private static Object getRandomValue(Class<?> clazz) {
		if (clazz != null && clazz.isPrimitive()) {
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return (byte) 0x50;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return 42;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return (short) 2;
			}
			if (equalsClass(clazz, long.class, Long.class)) {
				return 3;
			}
			if (equalsClass(clazz, char.class, Character.class)) {
				return 'g';
			}
			if (equalsClass(clazz, float.class, Float.class)) {
				return 6;
			}
			if (equalsClass(clazz, double.class, Double.class)) {
				return 8;
			}
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return true;
			}
		} else if (equalsClass(clazz, String.class, CharSequence.class)) {
			return "Albert";
		} else if (equalsClass(clazz, Class.class)) {
			return Object.class;
		} else if (equalsClass(clazz, Field.class, Method.class)) {
			return null;
		} else if (equalsClass(clazz, X509Certificate.class)) {
			return null;
		} else if (equalsClass(clazz, File.class)) {
			return new File("");
		} else if (clazz == byte[][].class) {
			return new byte[][] { new byte[] { 1, 2 } };
		} else if (clazz.isArray()) {
			Class<?> arrayClazz = clazz.getComponentType();
			int nrDims = 1 + clazz.getName().lastIndexOf('[');
			int[] dims = new int[nrDims];
			for (int i = 0; i < nrDims; i++) {
				dims[i] = i + 1;
			}
			return Array.newInstance(arrayClazz, dims);
		} else if (equalsClass(clazz, Object.class)) {
			return ReflectionLoader.newInstance(clazz);
		} else {
			try {
				if (ReflectionLoader.STAGE == clazz) {
					return null;
				}
				if (Throwable.class == clazz) {
					return null;
				}
				return ReflectionLoader.newInstanceSimple(clazz);
			} catch (Throwable e) {
				try {
					Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
					ArrayList<Constructor<?>> skipConstructor = new ArrayList<Constructor<?>>();
					for (Constructor<?> c : declaredConstructors) {
						try {
							Object[] call = getParameters(c, c.getParameterTypes(), TYPE_NULLVALUE, null);
							if (ReflectionLoader.isAccess(c, null)) {
								c.setAccessible(true);
								return c.newInstance(call);
							} else {
								skipConstructor.add(c);
							}
						} catch (Throwable e2) {
						}
					}
					for (Constructor<?> c : skipConstructor) {
						try {
							Object[] call = getParameters(c, c.getParameterTypes(), TYPE_NULLVALUE, null);
							c.setAccessible(true);
							return c.newInstance(call);
						} catch (Exception e2) {
						}
					}
				} catch (Throwable e2) {
				}
			}
		}
		return null;
	}

	private static Object getMaxValue(Class<?> clazz) {
		if (clazz != null && clazz.isPrimitive()) {
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return false;
			}
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return Byte.MAX_VALUE;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return Integer.MAX_VALUE;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return Short.MAX_VALUE;
			}
			if (equalsClass(clazz, long.class, Long.class)) {
				return Long.MAX_VALUE;
			}
			if (equalsClass(clazz, char.class, Character.class)) {
				return Character.MAX_VALUE;
			}
			if (equalsClass(clazz, float.class, Float.class)) {
				return Float.MAX_VALUE;
			}
			if (equalsClass(clazz, double.class, Double.class)) {
				return Double.MAX_VALUE;
			}
		}
		return null;
	}

	private static Object getMiddleValue(Class<?> clazz) {
		if (clazz != null && clazz.isPrimitive()) {
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return true;
			}
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return Byte.MAX_VALUE;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return 1000;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return (short) 1000;
			}
			if (equalsClass(clazz, long.class, Long.class)) {
				return (long) 1000;
			}
			if (equalsClass(clazz, char.class, Character.class)) {
				return Character.MAX_VALUE;
			}
			if (equalsClass(clazz, float.class, Float.class)) {
				return (float) 1000;
			}
			if (equalsClass(clazz, double.class, Double.class)) {
				return (double) 1000;
			}
		} else if (equalsClass(clazz, String.class, CharSequence.class)) {
			return "Minions ipsum potatoooo hahaha poopayee tatata bala tu hahaha wiiiii butt po kass para tu. Aaaaaah poulet tikka masala chasy tulaliloo pepete.";
		}
		return null;
	}

	private String getLine(String packageName, Exception e, String clazzName) {
		if (e == null) {
			return null;
		}
		Throwable cause = e.getCause();
		if (cause != null) {
			String line = getLineFromThrowable(packageName, cause, clazzName);
			if (line.length() > 0) {
				return line;
			}
		}
		return getLineFromThrowable(packageName, e, clazzName);
	}

	private String getLineFromThrowable(String packageName, Throwable e, String clazzName) {
		if (e == null) {
			return "";
		}
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (packageName != null) {
			for (StackTraceElement ste : stackTrace) {
				String name = ste.getClassName();
				if (name.startsWith(packageName) && !name.startsWith(packageName + ".test")) {
					return name + ".java:" + ste.getLineNumber();
				}
			}
		}
		return "";
	}

	/**
	 * With logger.
	 *
	 * @param logger the logger
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * With disable class error.
	 *
	 * @param value the value
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withDisableClassError(boolean value) {
		this.ignoreClassError = value;
		return this;
	}

	/**
	 * With disable simple exception.
	 *
	 * @param value the value
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withDisableSimpleException(boolean value) {
		this.ignoreSimpleException = value;
		return this;
	}
	
	/**
	 * With override logger.
	 *
	 * @param value the value
	 * @return the reflection black box tester
	 */
	public ReflectionBlackBoxTester withOverrideLogger(boolean value) {
		this.overrideLogger = value;
		return this;
	}
}
