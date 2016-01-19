package de.uniks.networkparser.test;


import java.io.PrintStream;

import org.junit.Test;

import de.uniks.networkparser.graph.*;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

public class GenModel {
	public static PrintStream stream=null;	//System.out;
	public static boolean methods=false;
	@Test
	public void showCountsModel() {
		showCounting(Annotation.class);
		showCounting(Association.class);
		showCounting(Attribute.class);
		showCounting(Cardinality.class);
		showCounting(Clazz.class);
		showCounting(ClazzImport.class);
		showCounting(DataType.class);
		showCounting(DataTypeSet.class);
		showCounting(DataTypeMap.class);
		showCounting(Method.class);
		showCounting(Modifier.class);
		showCounting(Parameter.class);
		showCounting(Throws.class);
		showCounting(Value.class);
	}
	
	
	@Test
	public void testModel() {
//		GraphModel model = new GraphList().with("de.uniks");
//		Clazz uni = model.createClazz("Uni");
//		Attribute nameAttribute = uni.createAttribute("name", DataType.STRING);
//		de.uniks.networkparser.graph.Method mainMethod = uni.createMethod("main");
//		boolean printItems=true;
//		System.out.println(getCounting(Cardinality.class, true, true));
		
//		System.out.println("Clazz: "+getCount(uni, true, printItems));
//		System.out.println("Attribute: "+getCount(nameAttribute, true, printItems));
//		System.out.println("Method: "+getCount(mainMethod, true, printItems));
//		Assert.assertNotNull(uni);
//		Assert.assertNotNull(nameAttribute);
//		Assert.assertNotNull(mainMethod);
	}
	
	private String shortName(Class<?> classType) {
		if(classType == null) {
			return null;
		}
		String name = classType.getName();
		int pos = name.lastIndexOf(".");
		if(pos>0) {
			return name.substring(pos+1);
		}
		return name;
	}
	private String getSignature(java.lang.reflect.Method method) {
		StringBuilder sb=new StringBuilder();
		sb.append(shortName(method.getDeclaringClass())+" "+method.getName()+"(");
		java.lang.reflect.Parameter[] parameters = method.getParameters();
		for(int i = 0;i<parameters.length;i++) {
			sb.append(parameters[i].getType());
			if(i<parameters.length - 1) {
				sb.append(",");	
			}
		}
		sb.append(")");
		if(method.getReturnType()!= null) {
			sb.append(" : ");
			sb.append(shortName(method.getReturnType()));
			
		}
		return sb.toString();
	}
	
	public int getCount(Object element, boolean printItems) {
		return getCounting(element.getClass(), printItems);
	}
	public void showCounting(Class<?> element) {
		if(stream != null) {
			stream.println(element.getSimpleName()+": "+getCounting(element, methods));
		}
		
		if(java.lang.reflect.Modifier.isAbstract(element.getModifiers()) ) {
			return;
		}
		SimpleList<String> wrongMethods = new SimpleList<String>();
		java.lang.reflect.Method[] methods = element.getMethods();
		for(int i=0;i<methods.length;i++) {
			if(methods[i].getDeclaringClass() == Object.class || methods[i].getDeclaringClass() == Enum.class) {
				continue;
			}
			if (java.lang.reflect.Modifier.isStatic(methods[i].getModifiers())  || 
					java.lang.reflect.Modifier.isPublic(methods[i].getModifiers()) == false) {
				continue;
			}

			if(element.getName().equals(methods[i].getDeclaringClass().getName())== false) {
				String declaredClass = shortName(methods[i].getDeclaringClass());
				String returnClass = shortName(methods[i].getReturnType());
				if(declaredClass.equals(returnClass)) {
					wrongMethods.add(shortName(element)+" "+getSignature(methods[i]));
				}
			}
		}
		// ERRORS
		for(String item : wrongMethods) {
			System.out.println(item);
		}
	}

	public int getCounting(Class<?> element, boolean printItems) {	
		java.lang.reflect.Method[] methods = element.getMethods();
		SortedSet<String> counts=new SortedSet<String>();
		for(int i=0;i<methods.length;i++) {
			if(methods[i].getDeclaringClass() == Object.class || methods[i].getDeclaringClass() == Enum.class) {
				continue;
			}
			String signature = getSignature(methods[i]);
			if (java.lang.reflect.Modifier.isStatic(methods[i].getModifiers())) {
				counts.with(signature);
			} else if (java.lang.reflect.Modifier.isPublic(methods[i].getModifiers())) {
				if(signature.endsWith(" toString()")==false) {
					counts.with(signature);
				}
			}
		}
		java.lang.reflect.Field[] fields = element.getClass().getFields();
		for(int i=0;i<fields.length;i++) {
			if (java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())) {
				counts.with(fields[i].getName()+":"+fields[i].getType());
			} else if (java.lang.reflect.Modifier.isPublic(fields[i].getModifiers())) {
				counts.with(fields[i].getName()+":"+fields[i].getType());
			}
		}
		if(printItems) {
			for(String item : counts) {
				if(stream != null) {
					stream.println(item);
				}
			}
		}
		return counts.size();
	}
}
