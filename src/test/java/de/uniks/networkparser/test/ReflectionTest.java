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
		int errorCount=0;
		int successCount=0;
		ignoreMethods.add("wait");
		ignoreMethods.add("notify");
		ignoreMethods.add("notifyAll");
		
		error.append("Start: ("+this.getClass().getName()+".java:1) \n");
		
		for(Class<?> clazz : classesForPackage) {
			StringBuilder item=new StringBuilder();
			item.append( clazz.getName()+": ");
			
			Constructor<?>[] constructors = clazz.getConstructors();
			if(clazz.isEnum() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) ) {
				continue;
			}
			for(Constructor<?> c : constructors) {
				if(c.getParameterTypes().length==0) {
//					item.append("test :\n");
					Object obj;
					try{
						obj = c.newInstance(new Object[0]);
					}catch(Exception e) {
						break;
					}
					StringBuilder itemError=new StringBuilder();
					
					for(Method m : clazz.getMethods()){
						if(ignoreMethods.contains(m.getName())) {
							continue;
						}
						if(m.getDeclaringClass().isInterface()) {
							continue;
						}
						try {
							// mit Null as Parameter
							m.invoke(obj, getParametersNull(m));
							successCount++;
							m.invoke(obj, getParametersMinValues(m));
							successCount++;
//							m.invoke(obj, getParametersMaxValues(m));
//							successCount++;
						}catch(Exception e) {
           
							String line =getLine(e, clazz.getSimpleName());
							if(line.length()<1) {
								line = clazz.getName()+".java:1";
							}
							itemError.append("("+line+") : "+clazz.getName()+":"+getSignature(m) +" "+ e.getCause()+"\n");
							String shortName="";
							if(line.lastIndexOf(".")>0) {
								String[] split = line.split("\\.");
								shortName = line.substring(0, line.lastIndexOf(":") - 4) +m.getName()+"("+split[split.length - 2] + "."+split[split.length - 1]+")";
							}
							System.out.println("at "+clazz.getName()+": "+e.getCause()+" "+shortName);
							errorCount++;
						}
					}
					// add all Results to List
					error.append(itemError.toString());
				}
			}
		}
		// Write out all Results
//		System.out.println(error.toString());
		System.err.println(errorCount+ "/" + (errorCount+ successCount));
	}
	
	private Object[] getParametersNull(Method m) {
		int length = m.getParameterTypes().length;
		Object[] objects = new Object[length];
		for (int i = 0; i < length; i++) {
			Class<?> clazz = m.getParameterTypes()[i];
			if (clazz.isPrimitive()) {
				if("boolean".equals(clazz.getName())) {
					objects[i] = false;
				} else if("byte".equals(clazz.getName())) {
					objects[i] = (byte) 0;
				} else if("short".equals(clazz.getName()) || "int".equals(clazz.getName())) {
					objects[i] = 0;
				} else if("long".equals(clazz.getName())) {
					objects[i] = 0L;
				} else if("char".equals(clazz.getName())) {
					objects[i] = '\u0000';
				} else if("float".equals(clazz.getName())) {
					objects[i] = 0.0f;
				} else if("double".equals(clazz.getName())) {
					objects[i] = 0.0d;
				} else if("String".equals(clazz.getName())) {
					objects[i] = null;
				}
			}
		}
		return objects;
	}
	private Object[] getParametersMinValues(Method m) {
		Object[] objects = getParametersNull(m);
		int length = m.getParameterTypes().length;
		for (int i = 0; i < length; i++) {
			Class<?> clazz = m.getParameterTypes()[i];
			if (clazz.isPrimitive()) {
				if("byte".equals(clazz.getName())) {
					objects[i] = Byte.MIN_VALUE;
				} else if("int".equals(clazz.getName())) {
					objects[i] = Integer.MIN_VALUE;
				} else if("short".equals(clazz.getName())) {
					objects[i] = Short.MIN_VALUE;
					break;
				} else if("long".equals(clazz.getName())) {
					objects[i] = Long.MIN_VALUE;
				} else if("char".equals(clazz.getName())) {
					objects[i] = Character.MIN_VALUE;
				} else if("float".equals(clazz.getName())) {
					objects[i] = Float.MIN_VALUE;
				} else if("double".equals(clazz.getName())) {
					objects[i] = Double.MIN_VALUE;
				}
			}
		}
		return objects;
	}
//	private Object[] getParametersMaxValues(Method m) {
//		Object[] objects = getParametersNull(m);
//		int length = m.getParameterTypes().length;
//		for (int i = 0; i < length; i++) {
//			Class<?> clazz = m.getParameterTypes()[i];
//			if (clazz.isPrimitive()) {
//				switch (clazz.getName()) {
//				case "byte":
//					objects[i] = Byte.MAX_VALUE;
//					break;
//				case "int":
//					objects[i] = Integer.MAX_VALUE;
//					break;
//				case "short":
//					objects[i] = Short.MAX_VALUE;
//					break;
//				case "long":
//					objects[i] = Long.MAX_VALUE;
//					break;
//				case "char":
//					objects[i] = Character.MAX_VALUE;
//					break;
//				case "float":
//					objects[i] = Float.MAX_VALUE;
//					break;
//				case "double":
//					objects[i] = Double.MAX_VALUE;
//					break;
//				}
//			}
//		}
//		return objects;
//	}
	
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
	
	private static void checkDirectory(File directory, String pckgname,
	        ArrayList<Class<?>> classes) throws ClassNotFoundException {
	    File tmpDirectory;

	    if (directory.exists() && directory.isDirectory()) {
	        String[] files = directory.list();
	        for (String file : files) {
	            if (file.endsWith(".class")) {
	                try {
//	                	System.out.println(pckgname + '.'
//	                            + file.substring(0, file.length() - 6));
	                    classes.add(Class.forName(pckgname + '.'
	                            + file.substring(0, file.length() - 6)));
	                } catch (NoClassDefFoundError e) {
	                } catch (ExceptionInInitializerError e) {
	                    // do nothing. this class hasn't been found by the loader, and we don't care.
	                }
	            } else if ((tmpDirectory = new File(directory, file))
	                    .isDirectory() && !file.equalsIgnoreCase("test") && !file.equalsIgnoreCase("javafx")) {
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
        for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
                checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
	    }
        return classes;
	}
}