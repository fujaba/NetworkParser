package de.uniks.networkparser.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;

import org.junit.Test;

public class ReflectionTest {
	
	private ArrayList<String> ignoreMethods=new ArrayList<String>();
	@Test
	public void testReflection() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList<Class<?>> classesForPackage = getClassesForPackage("de.uniks.networkparser");
		StringBuilder error=new StringBuilder();
		StringBuilder success=new StringBuilder();
		int errorCount=0;
		int successCount=0;
		ignoreMethods.add("wait");
		ignoreMethods.add("notify");
		ignoreMethods.add("notifyAll");
		
		error.append("Start\n");
		
		for(Class<?> clazz : classesForPackage) {
			StringBuilder item=new StringBuilder();
			item.append( clazz.getName()+": ");
			Constructor<?>[] constructors = clazz.getConstructors();
			if(clazz.isEnum() ) {
				item.append("ENUM");
				success.append(item.toString()+"\n");
				continue;
			} else if(clazz.isInterface() ) {
				item.append("Interface");
				success.append(item.toString()+"\n");
				continue;
			} else if(Modifier.isAbstract(clazz.getModifiers()) ) {
				item.append("Abstract");
				success.append(item.toString()+"\n");
				continue;
			}
			for(Constructor<?> c : constructors) {
				if(c.getParameterTypes().length==0) {
//					item.append("test :\n");
					Object obj = c.newInstance(new Object[0]);
					StringBuilder itemError=new StringBuilder();
					StringBuilder itemSuccess=new StringBuilder();
					
					for(Method m : clazz.getMethods()){
						if(ignoreMethods.contains(m.getName())) {
							continue;
						}
						if(m.getDeclaringClass().isInterface()) {
							continue;
						}
						Object[] params = getParametersNull(m);
						try {
							// mit Null as Parameter
							m.invoke(obj, params);
							itemSuccess.append("       "+getSignature(m) + " ok\n");
							successCount++;
						}catch(Exception e) {
//							System.out.println(m.getName());
//							e.printStackTrace();
							String line =getLine(e, clazz.getName());
							if(line.length()<1) {
								line = clazz.getName()+".java:1";
							}
							itemError.append("("+line+") : "+getSignature(m) +" "+ e.getCause()+"\n");
							errorCount++;
						}
					}
					// add all Results to List
//					success.append(item.toString());
					success.append(itemSuccess.toString());
					
//					error.append(item.toString());
					error.append(itemError.toString());
				}
			}
//			if(errorCount>100) {
//				break;
//			}
		}
		
		// Write out all Results
		System.err.println(error.toString());
		
		System.err.println(errorCount+ "/" + (errorCount+ successCount));
	}
	
	private Object[] getParametersNull(Method m) {
		int length = m.getParameterTypes().length;
		Object[] objects = new Object[length];
		for (int i = 0; i < length; i++) {
			Class<?> clazz = m.getParameterTypes()[i];
			if (clazz.isPrimitive()) {
				switch (clazz.getName()) {
				case "boolean":
					objects[i] = false;
					break;
				case "byte":
					objects[i] = (byte) 0;
					break;
				case "int":
				case "short":
					objects[i] = 0;
					break;
				case "long":
					objects[i] = 0L;
					break;
				case "char":
					objects[i] = '\u0000';
					break;
				case "float":
					objects[i] = 0.0f;
					break;
				case "double":
					objects[i] = 0.0d;
					break;
				case "String":
					objects[i] = null;
					break;
				}
			}
		}
		return objects;
	}
	
	private String getLine(Exception e, String clazzName) {
		Throwable cause = e.getCause();
		if(cause!=null) {
			String line = getLineFromThrowable(cause, clazzName);
			if(line.length()>0) {
				return line;
			}
		}
		return getLineFromThrowable(e, clazzName);
	}
	
	private String getLineFromThrowable(Throwable e, String clazzName) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		for(StackTraceElement ste : stackTrace) {
			String name = ste.getClassName();
			if(name.startsWith("de.uniks.networkparser") && !name.startsWith("de.uniks.networkparser.test")) {
				return name+".java:"+ste.getLineNumber();
			}
		}
		return "";
	}
	
	private String getSignature(Method m) {
		StringBuilder r = new StringBuilder(m.getName());
		r.append("(");
		Class<?>[] parameterTypes = m.getParameterTypes();
		if(parameterTypes.length>0) {
			r.append(parameterTypes[0].getSimpleName());
		}
		for(int i=1;i<parameterTypes.length;i++) {
			r.append(",");
			r.append(parameterTypes[i].getSimpleName());
		}
		r.append(")");
		return r.toString();
	}
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname,
	        ArrayList<Class<?>> classes) throws ClassNotFoundException {
	    File tmpDirectory;

	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();

	        for (final String file : files) {
	            if (file.endsWith(".class")) {
	                try {
	                    classes.add(Class.forName(pckgname + '.'
	                            + file.substring(0, file.length() - 6)));
	                } catch (final NoClassDefFoundError e) {
	                    // do nothing. this class hasn't been found by the
	                    // loader, and we don't care.
	                }
	            } else if ((tmpDirectory = new File(directory, file))
	                    .isDirectory() && !file.equalsIgnoreCase("test")) {
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
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 * @throws IOException 
	 */
	public static ArrayList<Class<?>> getClassesForPackage(String pckgname)
	        throws ClassNotFoundException, IOException {
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    ClassLoader cld = Thread.currentThread()
                .getContextClassLoader();
	    
	    Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
        for (URL url = null; resources.hasMoreElements()
                && ((url = resources.nextElement()) != null);) {
                checkDirectory(
	                                new File(URLDecoder.decode(url.getPath(),
	                                        "UTF-8")), pckgname, classes);
	    }
        return classes;
	}
}
