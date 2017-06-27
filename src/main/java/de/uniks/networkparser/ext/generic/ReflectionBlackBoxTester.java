package de.uniks.networkparser.ext.generic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;


import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;

public class ReflectionBlackBoxTester {
	public static final String NULLVALUE="nullValue";
	public static final String MINVALUE="minValue";
	public static final String MAXVALUE="maxValue";
	public static final String RANDOMVALUE="randomValue";
	private SimpleSet<String> tests=new SimpleSet<String>().with(NULLVALUE,MINVALUE,RANDOMVALUE);
	private SimpleKeyValueList<String, SimpleSet<String>> ignoreMethods;
//	private SimpleSet<String> ignoreClazz=new SimpleSet<String>().with("de.uniks.networkparser.NetworkParserLog");
	private int errorCount;
	private int successCount;
	private String packageName;
	private NetworkParserLog logger;
	
	
	public ReflectionBlackBoxTester() {
		ignoreMethods =new SimpleKeyValueList<String, SimpleSet<String>>();
		withIgnoreClazzes("de.uniks.networkparser.NetworkParserLog");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:access");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:generate");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:main");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:save");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:start");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.DiagramEditor:open");
		withIgnoreClazzes("de.uniks.networkparser.ext.story.Story");
		withIgnoreClazzes("de.uniks.networkparser.ext.error.ErrorHandler");
		withIgnoreClazzes("de.uniks.networkparser.ext.javafx.SimpleController:init");
		
//		withIgnoreClazzes("de.uniks.networkparser.ext.javafx:*");
	}
	
	public ReflectionBlackBoxTester withIgnoreClazzes(String... values) {
		if(values == null) {
			return this;
		}
		for(String item : values) {
			int pos = item.indexOf(":");
			if(pos<0) {
				if(this.ignoreMethods.contains(item) == false) {
					this.ignoreMethods.put(item, new SimpleSet<String>());
				}
			}else {
				String clazz = item.substring(0, pos);
				String method = item.substring(pos+1);
				SimpleSet<String> methods = this.ignoreMethods.get(clazz);
				if(methods instanceof SimpleSet<?>) {
					methods.add(method);
				}else {
					methods = new SimpleSet<String>().with(method);
					this.ignoreMethods.put(clazz, methods);
				}
			}
		}
		return this;
	}

	public void test(String packageName, NetworkParserLog logger) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList<Class<?>> classesForPackage = getClassesForPackage(packageName);
		errorCount = 0;
		successCount = 0;
		this.packageName = packageName;
		this.logger = logger;
		
		for(Class<?> clazz : classesForPackage) {
			StringBuilder item=new StringBuilder();
			item.append( clazz.getName()+": ");
			SimpleSet<String> methods = this.ignoreMethods.get(clazz.getName());
			if(methods != null && methods.size()<1) {
				item.append("ignore");
				continue;
			}
			Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			if(Modifier.isAbstract(clazz.getModifiers()) ) {
				continue;
			}
			
			Object obj = null;
			for(Constructor<?> c : constructors) {
				Object[] call = getParameters(c.getParameterTypes(), NULLVALUE);
				c.setAccessible(true);
				try{
					obj = c.newInstance(call);
					if(obj != null) {
						if(obj != null) {
							testClass(obj, clazz, methods);
						}
					}
				}catch (Exception e) {
				}
			}
		}
		// Write out all Results
		output("Errors: "+errorCount+ "/" + (errorCount+ successCount), logger, NetworkParserLog.LOGLEVEL_INFO);
	}
	
	
	private void testClass(Object obj, Class<?> clazz, SimpleSet<String> ignoreMethods) {
		for(Method m : clazz.getDeclaredMethods()) {
			if(m.getDeclaringClass().isInterface()) {
				continue;
			}
			if(ignoreMethods != null && ignoreMethods.contains(m.getName())) {
//			if("main".equals(m.getName()) || "access".equals(m.getName())) {
				continue;
			}
			
//			output(clazz.getName()+":"+m.getName(), logger, NetworkParserLog.LOGLEVEL_ERROR);

			Object[] call = null;
			m.setAccessible(true);
			// mit Null as Parameter
			Class<?>[] parameterTypes = m.getParameterTypes();
			if(tests.contains(NULLVALUE)) {
				try {
					call = getParameters(parameterTypes, NULLVALUE);
					m.invoke(obj, call);
					successCount++;
				}catch(Exception e) {
					saveException(e, clazz, m, call);
				}
			}
			// mit MINVALUE as Parameter
			if(tests.contains(MINVALUE)) {
				try {
					call = getParameters(parameterTypes, MINVALUE);
					m.invoke(obj, call);
					successCount++;
				}catch(Exception e) {
					saveException(e, clazz, m, call);
				}
			}
			// mit MAXVALUE as Parameter
			if(tests.contains(MAXVALUE)) {
				try {
					call = getParameters(parameterTypes, MAXVALUE);
					m.invoke(obj, call);
					successCount++;
				} catch(Exception e) {
					saveException(e, clazz, m, call);
				}
			}
			
			// mit RANDOMVALUE as Parameter
			if(tests.contains(RANDOMVALUE)) {
				try {
					call = getParameters(parameterTypes, RANDOMVALUE);
//					output(clazz.getName()+"-call: "+m.getName(), logger, NetworkParserLog.LOGLEVEL_ERROR);
					m.invoke(obj, call);
					successCount++;
				} catch(Exception e) {
					saveException(e, clazz, m, call);
				}
			}
		}
		for(Field f : clazz.getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object value = f.get(obj);
				if(value == null) {
					output("field null", logger, NetworkParserLog.LOGLEVEL_WARNING);
				}
				if(Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				if(value != null) {
					f.set(obj, getNullValue(value.getClass()));
					f.set(obj, value);
				}
			} catch(Exception e) {
			}
		}
	}
	
	private void saveException(Exception e, Class<?> clazz, Method m, Object[] call) {
		String line =getLine(packageName, e, clazz.getSimpleName());
		if(line.length()<1) {
			line = clazz.getName()+".java:1";
		}
//		String error = "("+line+") : "+clazz.getName()+":"+getSignature(m) +" "+ e.getCause()+":"+getParamtoString(call)+"\n");
//		output(error.toString(), logger, NetworkParserLog.LOGLEVEL_ERROR);

		String shortName="";
		if(line.lastIndexOf(".")>0) {
			String[] split = line.split("\\.");
			shortName = line.substring(0, line.lastIndexOf(":") - 4) +m.getName()+"("+split[split.length - 2] + "."+split[split.length - 1]+")";
		}
		output("at "+clazz.getName()+": "+e.getCause()+" "+shortName+" : ", logger, NetworkParserLog.LOGLEVEL_WARNING);
		errorCount++;
	}

	public String getParamtoString(Object[] params) {
		StringBuilder sb= new StringBuilder();
		sb.append("(");
		if(params == null) {
			sb.append(")");
			return sb.toString();
		}
		boolean hasParam=false;
		for(Object item : params) {
			if(hasParam) {
				sb.append(",");
			}
			if(item == null) {
				sb.append("null");
			}else {
				sb.append(item.toString());
			}
			hasParam=true;
		}
		sb.append(")");
		return sb.toString();
	}

	public void output(String message, NetworkParserLog logger, int logLevel) {
		if(logger != null) {
			logger.log(this, "output", message, logLevel);
		}
	}
	
	private Object[] getParameters(Class<?>[] parameters, String type) {
		int length = parameters.length;
		Object[] objects = new Object[length];
		if(NULLVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getNullValue(parameters[i]);
			}
			return objects;
			
		}
		if(MINVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getMinValue(parameters[i]);
			}
			return objects;
		}
		if(MAXVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getMaxValue(parameters[i]);
			}
			return objects;
		}
		if(RANDOMVALUE.equals(type)) {
			for (int i = 0; i < length; i++) {
				objects[i] = getRandomValue(parameters[i]);
			}
		}
		return objects;
	}

	private boolean euqalsClass(Class<?> clazz, Class<?>... checkClasses) {
		if(checkClasses == null) {
			return true;
		}
		for(Class<?> check : checkClasses) {
			if(clazz.getName().equals(check.getName())) {
				return true;
			}
		}
		return false;
	}

	private Object getNullValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if(euqalsClass(clazz, boolean.class, Boolean.class)) {return false;}
			if(euqalsClass(clazz, byte.class, Byte.class)) {return (byte) 0;}
			if(euqalsClass(clazz, short.class, Short.class)) {return 0;}
			if(euqalsClass(clazz, int.class, Integer.class)) {return 0;}
			if(euqalsClass(clazz, long.class, Long.class)) {return 0L;}
			if(euqalsClass(clazz, char.class, Character.class)) {return '\u0000';}
			if(euqalsClass(clazz, float.class, Float.class)) {return 0.0f;}
			if(euqalsClass(clazz, double.class, Double.class)) {return 0.0d;}
			if(euqalsClass(clazz, String.class, CharSequence.class)) {return null;}
		}
		return null;
	}
	private Object getMinValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if(euqalsClass(clazz, boolean.class, Boolean.class)) {return false;}
			if(euqalsClass(clazz, byte.class, Byte.class)) {return Byte.MIN_VALUE;}
			if(euqalsClass(clazz, int.class, Integer.class)) {return Integer.MIN_VALUE;}
			if(euqalsClass(clazz, short.class, Short.class)) {return Short.MIN_VALUE;}
			if(euqalsClass(clazz, long.class, Long.class)) {return Long.MIN_VALUE;}
			if(euqalsClass(clazz, char.class, Character.class)) {return Character.MIN_VALUE;}
			if(euqalsClass(clazz, float.class, Float.class)) {return Float.MIN_VALUE;}
			if(euqalsClass(clazz, double.class, Double.class)) {return Double.MIN_VALUE;}
			if(euqalsClass(clazz, String.class, CharSequence.class)) {return "";}
		}
		return null;
	}
	
	private Object getRandomValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if(euqalsClass(clazz, byte.class, Byte.class)) {return 0x50;}
			if(euqalsClass(clazz, int.class, Integer.class)) {return 42;}
			if(euqalsClass(clazz, short.class, Short.class)) {return 2;}
			if(euqalsClass(clazz, long.class, Long.class)) {return 3;}
			if(euqalsClass(clazz, char.class, Character.class)) {return 'g';}
			if(euqalsClass(clazz, float.class, Float.class)) {return 6;}
			if(euqalsClass(clazz, double.class, Double.class)) {return 8;}
			if(euqalsClass(clazz, String.class, CharSequence.class)) {return "Albert";}
		} else if(clazz.isArray()) {
			Class<?> arrayClazz = clazz.getComponentType();
			int nrDims = 1 + clazz.getName().lastIndexOf('[');
			int[] dims = new int[nrDims];
			for(int i=0;i<nrDims;i++) {
				dims[i] = i+1;
			}
			return Array.newInstance(arrayClazz, dims);
		} else {
			try {
				if(ReflectionLoader.STAGE == clazz) {
					return null;
				}
				return clazz.newInstance();
			}catch (Exception e) {
					Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
					for(Constructor<?> c : declaredConstructors) {
						try {
							Object[] call = getParameters(c.getParameterTypes(), NULLVALUE);
							c.setAccessible(true);
							return c.newInstance(call);
						} catch (Exception e2) {
						}
					}
			}
		}
		return null;
	}
	private Object getMaxValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if(euqalsClass(clazz, boolean.class, Boolean.class)) {return false;}
			if(euqalsClass(clazz, byte.class, Byte.class)) {return Byte.MAX_VALUE;}
			if(euqalsClass(clazz, int.class, Integer.class)) {return Integer.MAX_VALUE;}
			if(euqalsClass(clazz, short.class, Short.class)) {return Short.MAX_VALUE;}
			if(euqalsClass(clazz, long.class, Long.class)) {return Long.MAX_VALUE;}
			if(euqalsClass(clazz, char.class, Character.class)) {return Character.MAX_VALUE;}
			if(euqalsClass(clazz, float.class, Float.class)) {return Float.MAX_VALUE;}
			if(euqalsClass(clazz, double.class, Double.class)) {return Double.MAX_VALUE;}
		}
		return null;
	}

	private String getLine(String packageName, Exception e, String clazzName) {
		Throwable cause = e.getCause();
		if(cause!=null) {
			String line = getLineFromThrowable(packageName, cause, clazzName);
			if(line.length()>0) {
				return line;
			}
		}
		return getLineFromThrowable(packageName, e, clazzName);
	}

	private String getLineFromThrowable(String packageName, Throwable e, String clazzName) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		for(StackTraceElement ste : stackTrace) {
			String name = ste.getClassName();
			if(name.startsWith(packageName) && !name.startsWith(packageName+".test")) {
				return name+".java:"+ste.getLineNumber();
			}
		}
		return "";
	}

	private void checkDirectory(File directory, String pckgname,
			ArrayList<Class<?>> classes) throws ClassNotFoundException {
		File tmpDirectory;

		if (directory.exists() && directory.isDirectory()) {
			String[] files = directory.list();
			for (String file : files) {
				if (file.endsWith(".class")) {
					try {
//						output(pckgname + '.' + file.substring(0, file.length() - 6), null, Net);
						classes.add(Class.forName(pckgname + '.'
								+ file.substring(0, file.length() - 6)));
					} catch (NoClassDefFoundError e) {
					} catch (ExceptionInInitializerError e) {
						// do nothing. this class hasn't been found by the loader, and we don't care.
					}
				} else if ((tmpDirectory = new File(directory, file))	
                    .isDirectory() && !file.equalsIgnoreCase("test") ) {

					checkDirectory(tmpDirectory, pckgname + "." + file, classes);
				}
			}
		}
	}
	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 *
	 * @param pckgname
	 *			the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *			 if something went wrong
	 * @throws IOException
	 * 			if something went wrong to read
	 */
	public ArrayList<Class<?>> getClassesForPackage(String pckgname)
			throws ClassNotFoundException, IOException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader cld = Thread.currentThread()
				.getContextClassLoader();

		Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
		for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
				checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
		}
		if(classes.size() == 0) {
			Class<?> forName = Class.forName(pckgname);
			if(forName != null) {
				classes.add(forName);
			}
		}
		return classes;
	}

}
