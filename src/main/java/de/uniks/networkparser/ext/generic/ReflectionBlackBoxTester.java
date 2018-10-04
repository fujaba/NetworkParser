package de.uniks.networkparser.ext.generic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.Timer;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import de.uniks.networkparser.ext.petaf.SimpleTimerTask;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryStepJUnit;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;

public class ReflectionBlackBoxTester {
	public static final String TYPE_NULLVALUE = "null";
	public static final String TYPE_MINVALUE = "min";
	public static final String TYPE_MAXVALUE = "max";
	public static final String TYPE_RANDOMVALUE = "random";
	public static final String TYPE_CUSTOMVALUE = "custom";
	public static final String BLACKBOXTESTER = "backboxtest";
	public static final String INSTANCE = "instance";
	public static final String IGNOREMETHOD = "run";
	public static final String DEFAULTMETHODS = "";

	private SimpleSet<String> tests = new SimpleSet<String>().with(TYPE_NULLVALUE, TYPE_MINVALUE, TYPE_RANDOMVALUE,
			TYPE_CUSTOMVALUE);
	private SimpleKeyValueList<String, SimpleSet<String>> ignoreMethods;
//	private SimpleSet<String> ignoreClazz=new SimpleSet<String>().with("de.uniks.networkparser.NetworkParserLog");
	private int errorCount;
	private int successCount;
	private String packageName;
	private NetworkParserLog logger;
	private ObjectCondition custom;

	public static void mainTester(String[] args) {
		Object junitCore = ReflectionLoader.newInstanceStr("org.junit.runner.JUnitCore");
		SimpleSet<Class<?>> testClasses = new SimpleSet<Class<?>>();
		String blackBoxPackage = null;
		String path = "doc/";
		if (junitCore != null) {
			for (String param : args) {
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
					System.out.println("error: " + e.getMessage());
					e.printStackTrace(System.out);
				}
			}
			// Now Check if BaclkBoxTester activ
			if (blackBoxPackage != null) {
				StoryStepJUnit storyStepJUnit = new StoryStepJUnit();
				storyStepJUnit.withPackageName(blackBoxPackage);
				storyStepJUnit.executeBlackBoxTest(path);
			}
		}
	}

	public ReflectionBlackBoxTester() {
		ignoreMethods = new SimpleKeyValueList<String, SimpleSet<String>>();

		withIgnoreClazzes(ReflectionBlackBoxTester.class, "main");

		// Add for Files
		withIgnoreClazzes(Story.class, "dumpHTML", "writeFile");
		withIgnoreClazzes(ErrorHandler.class);
		withIgnoreClazzes(StoryStepJUnit.class, "update");
		ignoreMethods.add(DEFAULTMETHODS,
				new SimpleSet<String>().with("show*", "run", "execute*", "checkSystemTray", "main"));
		// Add for new Threads
//		withIgnoreClazzes(SimpleController.class, "create", "init");
		withIgnoreClazzes(SimpleController.class);
		withIgnoreClazzes(DialogBox.class, "createContent");
		withIgnoreClazzes(JarValidator.class);

		// TEST
//		withIgnoreClazzes(Server_UPD.class);
//		withIgnoreClazzes(Server_Time.class);
//		withIgnoreClazzes(Space.class);
//		withIgnoreClazzes(NodeProxyServer.class);
//		withIgnoreClazzes(NodeProxyTCP.class, "initProxy", "postHTTP", "getHTTP", "getConnection");
//		withIgnoreClazzes(DiagramEditor.class);
//		withIgnoreClazzes(NodeProxyMessages.class);
//		withIgnoreClazzes(NodeProxyBroker.class);
//		withIgnoreClazzes(MQTTMessage.class);
//		withIgnoreClazzes(RabbitMessage.class);
//		withIgnoreClazzes(MessageSession.class);
//		withIgnoreClazzes(JavaAdapter.class);
//		withIgnoreClazzes(JavaBridgeFX.class);
//		withIgnoreClazzes(TimerExecutor.class);
	}

	public ReflectionBlackBoxTester withIgnoreClazzes(Class<?> metaClass, String... methods) {
		String className = metaClass.getName();
		if (methods == null || methods.length < 1) {
			return withIgnoreClazzes(className);
		}
		for (String method : methods) {
			withIgnoreClazzes(className + ":" + method);
		}
		return this;
	}

	public ReflectionBlackBoxTester withIgnoreClazzes(String... values) {
		if (values == null) {
			return this;
		}
		for (String item : values) {
			int pos = item.indexOf(":");
			if (pos < 0) {
				if (this.ignoreMethods.contains(item) == false) {
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

	public static final boolean isTester() {
		String property = System.getProperty("Tester");
		return property != null && "true".equalsIgnoreCase(property);
	}

	public static final boolean setTester() {
		System.setProperty("Tester", "true");
		return true;
	}

	public void test(String packageName, NetworkParserLog logger) throws ClassNotFoundException, IOException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		setTester();
		ArrayList<Class<?>> classesForPackage = getClassesForPackage(packageName);
		errorCount = 0;
		successCount = 0;
		this.packageName = packageName;
		this.logger = logger;
		long start = System.currentTimeMillis();
		Set<Thread> oldThreads = ReflectionLoader.closeThreads(null);
		Timer timer = new Timer();

		for (Class<?> clazz : classesForPackage) {
			SimpleSet<String> methods = this.ignoreMethods.get(clazz.getName());
			if (methods != null && methods.size() < 1) {
//				System.out.println("Ignore:"+clazz.getName());
				continue;
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}
			SimpleTimerTask task = new SimpleTimerTask(Thread.currentThread());
			timer.schedule(task, 2000);
			Object obj = ReflectionLoader.newInstanceSimple(clazz, IGNOREMETHOD);
			if (obj != null) {
				// Show For Ignore DefaultMethods
				SimpleSet<String> defaultMethods = this.ignoreMethods.get(DEFAULTMETHODS);
				if (defaultMethods != null) {
					if (methods == null) {
						methods = new SimpleSet<String>();
					}
					methods.withList(defaultMethods);
				}
				testClass(obj, clazz, methods);
			}
			task.withSimpleExit(null);
		}

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		ReflectionLoader.closeThreads(oldThreads);

		// Write out all Results
		output(this, "Errors: " + errorCount + "/" + (errorCount + successCount), logger,
				NetworkParserLog.LOGLEVEL_INFO, null);
		output(this, "Time: " + (System.currentTimeMillis() - start) + "ms - Thread: " + oldThreads.size() + " -> "
				+ Thread.activeCount(), logger, NetworkParserLog.LOGLEVEL_INFO, null);

	}

	public void testClass(Object obj, Class<?> clazz, SimpleSet<String> ignoreMethods) {
		boolean reg = false;
		if (obj == null) {
			return;
		}
		for (String m : ignoreMethods) {
			if (m != null && m.endsWith("*")) {
				reg = true;
				break;
			}
		}
		Field propertyChangeListener = null;
		try {
			propertyChangeListener = clazz.getDeclaredField("listeners");
			propertyChangeListener.setAccessible(true);
		} catch (Exception e) {
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
			output(this, clazz.getName() + ":" + m.getName(), logger, NetworkParserLog.LOGLEVEL_ERROR, null);

			Object[] call = null;
			m.setAccessible(true);
			// mit Null as Parameter
//			System.out.println(System.currentTimeMillis()+" TEST:"+clazz.getName()+":"+m.getName());
			Class<?>[] parameterTypes = m.getParameterTypes();
			if (tests.contains(TYPE_NULLVALUE)) {
				try {
					call = getParameters(m, parameterTypes, TYPE_NULLVALUE, this);

					m.invoke(obj, call);
					successCount++;
				} catch (Exception e) {
					saveException(e, clazz, m, call);
				}
				// specialcase
				if (propertyChangeListener != null && "addPropertyChangeListener".equals(m.getName())) {
					// So try again
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
			// mit MINVALUE as Parameter
			if (tests.contains(TYPE_MINVALUE)) {
				try {
					call = getParameters(m, parameterTypes, TYPE_MINVALUE, this);
					m.invoke(obj, call);
					successCount++;
				} catch (Exception e) {
					saveException(e, clazz, m, call);
				}
			}
			// mit MAXVALUE as Parameter
			if (tests.contains(TYPE_MAXVALUE)) {
				try {
					call = getParameters(m, parameterTypes, TYPE_MAXVALUE, this);
					m.invoke(obj, call);
					successCount++;
				} catch (Exception e) {
					saveException(e, clazz, m, call);
				}
			}

			// mit RANDOMVALUE as Parameter
			if (tests.contains(TYPE_RANDOMVALUE)) {
				try {
					call = getParameters(m, parameterTypes, TYPE_RANDOMVALUE, this);
//					output(clazz.getName()+"-call: "+m.getName(), logger, NetworkParserLog.LOGLEVEL_ERROR);

					m.invoke(obj, call);
					successCount++;
				} catch (Exception e) {
					saveException(e, clazz, m, call);
				}
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

	}

	public ReflectionBlackBoxTester withTest(String value) {
		this.tests.clear();
		this.tests.add(value);
		return this;
	}

	private void saveException(Exception e, Class<?> clazz, Method m, Object[] call) {
		String line = getLine(packageName, e, clazz.getSimpleName());
		if (line.length() < 1) {
			line = clazz.getName() + ".java:1";
		}
//		String error = "("+line+") : "+clazz.getName()+":"+getSignature(m) +" "+ e.getCause()+":"+getParamtoString(call)+"\n");
//		output(error.toString(), logger, NetworkParserLog.LOGLEVEL_ERROR);

		String shortName = "";
		if (line.lastIndexOf(".") > 0) {
			String[] split = line.split("\\.");
			shortName = line.substring(0, line.lastIndexOf(":") - 4) + m.getName() + "(" + split[split.length - 2] + "."
					+ split[split.length - 1] + ")";
		}
		output(m, "at " + clazz.getName() + ": " + e.getCause() + " " + shortName + " : ", logger,
				NetworkParserLog.LOGLEVEL_ERROR, e);
		errorCount++;
	}

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

	public void output(Object owner, String message, NetworkParserLog logger, int logLevel, Exception e) {
		if (logger != null) {
			logger.log(owner, "output", message, logLevel, e);
		}
	}

	public static Object[] getParameters(Executable m, Class<?>[] parameters, String type, Object owner) {
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
		if (TYPE_CUSTOMVALUE.equals(type)) {
			return getCustomValue(m, parameters, owner);
		}
		return objects;
	}

	private static boolean equalsClass(Class<?> clazz, Class<?>... checkClasses) {
		if (checkClasses == null) {
			return true;
		}
		for (Class<?> check : checkClasses) {
			if (clazz.getName().equals(check.getName())) {
				return true;
			}
		}
		return false;
	}

	private static Object getNullValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (equalsClass(clazz, boolean.class, Boolean.class)) {
				return false;
			}
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return (byte) 0;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return 0;
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
			if (equalsClass(clazz, String.class, CharSequence.class)) {
				return null;
			}
		}
		return null;
	}

	private static Object getMinValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
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
			if (equalsClass(clazz, String.class, CharSequence.class)) {
				return "";
			}
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

	public ObjectCondition getCustom() {
		return custom;
	}

	private static Object getRandomValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (equalsClass(clazz, byte.class, Byte.class)) {
				return 0x50;
			}
			if (equalsClass(clazz, int.class, Integer.class)) {
				return 42;
			}
			if (equalsClass(clazz, short.class, Short.class)) {
				return 2;
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
			if (equalsClass(clazz, String.class, CharSequence.class)) {
				return "Albert";
			}
		} else if (equalsClass(clazz, Class.class)) {
			return Object.class;
		} else if (equalsClass(clazz, Object.class)) {
			return "Albert";
		} else if (equalsClass(clazz, Field.class, Method.class)) {
			return null;
		} else if (equalsClass(clazz, X509Certificate.class)) {
			return null;
		} else if (equalsClass(clazz, File.class)) {
			return new File("");
		} else if (clazz.isArray()) {
			Class<?> arrayClazz = clazz.getComponentType();
			int nrDims = 1 + clazz.getName().lastIndexOf('[');
			int[] dims = new int[nrDims];
			for (int i = 0; i < nrDims; i++) {
				dims[i] = i + 1;
			}
			return Array.newInstance(arrayClazz, dims);
		} else {
			try {
				if (ReflectionLoader.STAGE == clazz) {
					return null;
				}
				if (Throwable.class == clazz) {
					return null;
				}
				return clazz.getConstructor().newInstance();
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
		if (clazz.isPrimitive()) {
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

	private String getLine(String packageName, Exception e, String clazzName) {
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
		StackTraceElement[] stackTrace = e.getStackTrace();
		for (StackTraceElement ste : stackTrace) {
			String name = ste.getClassName();
			if (name.startsWith(packageName) && !name.startsWith(packageName + ".test")) {
				return name + ".java:" + ste.getLineNumber();
			}
		}
		return "";
	}

	private void checkDirectory(File directory, String pckgname, ArrayList<Class<?>> classes)
			throws ClassNotFoundException {
		File tmpDirectory;

		if (directory.exists() && directory.isDirectory()) {
			String[] files = directory.list();
			if (files == null) {
				return;
			}
			for (String file : files) {
				if (file.endsWith(".class")) {
					try {
//						output(pckgname + '.' + file.substring(0, file.length() - 6), null, Net);
						String className = pckgname;
						if (className.length() > 0) {
							className += ".";
						}
						className += file.substring(0, file.length() - 6);
						classes.add(Class.forName(className));
					} catch (Exception e) {
						// do nothing. this class hasn't been found by the loader, and we don't care.
					}
				} else if ((tmpDirectory = new File(directory, file)).isDirectory() && !file.equalsIgnoreCase("test")) {
					checkDirectory(tmpDirectory, pckgname + "." + file, classes);
				}
			}
		}
	}

	/**
	 * Attempts to list all the classes in the specified package as determined by
	 * the context class loader
	 *
	 * @param pckgname the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException if something went wrong
	 * @throws IOException            if something went wrong to read
	 */
	public ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException, IOException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		if (cld == null) {
			return classes;
		}
		Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
		for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
			checkDirectory(new File(URLDecoder.decode(url.getPath(), BaseItem.ENCODING)), pckgname, classes);
		}
		if (classes.size() == 0) {
			Class<?> forName = Class.forName(pckgname);
			if (forName != null) {
				classes.add(forName);
			}
		}
		return classes;
	}

}
