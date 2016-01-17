package de.uniks.networkparser.test;


import org.junit.Test;

import de.uniks.networkparser.list.SortedSet;

public class GenModel {
	@Test
	public void showCountsModel() {
//		showCounting(Annotation.class);
//		showCounting(Association.class);
//		showCounting(Attribute.class);
//		showCounting(Cardinality.class);
//		showCounting(Clazz.class);
//		showCounting(ClazzImport.class);
//		showCounting(DataType.class);
//		showCounting(DataTypeSet.class);
//		showCounting(DataTypeMap.class);
//		showCounting(Method.class);
//		showCounting(Modifier.class);
//		showCounting(Parameter.class);
//		showCounting(Throws.class);
//		showCounting(Value.class);
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
	private String getSignature(java.lang.reflect.Method method) {
		StringBuilder sb=new StringBuilder();
		sb.append(method.getDeclaringClass()+" "+method.getName()+"(");
		java.lang.reflect.Parameter[] parameters = method.getParameters();
		for(int i = 0;i<parameters.length;i++) {
			sb.append(parameters[i].getType());
			if(i<parameters.length - 1) {
				sb.append(",");	
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public int getCount(Object element, boolean printItems) {
		return getCounting(element.getClass(), printItems);
	}
	public void showCounting(Class<?> element) {
		System.out.println(element.getSimpleName()+": "+getCounting(element, false));
	}
	public int getCounting(Class<?> element, boolean printItems) {
		java.lang.reflect.Method[] methods = element.getMethods();
		SortedSet<String> counts=new SortedSet<String>();
		for(int i=0;i<methods.length;i++) {
			String signature = getSignature(methods[i]);
			if(methods[i].getDeclaringClass() == Object.class || methods[i].getDeclaringClass() == Enum.class) {
				continue;
			}
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
				System.out.println(item);	
			}
		}
		return counts.size();
	}
}
