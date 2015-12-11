package de.uniks.networkparser.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.list.SortedSet;

public class GenModel {

	@Test
	public void testModel() {
		GraphModel model = new GraphList().with("de.uniks");
		Clazz uni = model.createClazz("Uni");
		Attribute nameAttribute = uni.createAttribute("name", DataType.STRING);
		de.uniks.networkparser.graph.Method mainMethod = uni.createMethod("main");
		boolean printItems=true;
//		System.out.println("Clazz: "+getCount(uni, true, printItems));
		System.out.println("Attribute: "+getCount(nameAttribute, true, printItems));
//		System.out.println("Method: "+getCount(mainMethod, true, printItems));
		Assert.assertNotNull(uni);
		Assert.assertNotNull(nameAttribute);
		Assert.assertNotNull(mainMethod);
	}
	private String getSignature(Method method) {
		StringBuilder sb=new StringBuilder();
		sb.append(method.getName()+"(");
		Parameter[] parameters = method.getParameters();
		for(int i = 0;i<parameters.length;i++) {
			sb.append(parameters[i].getType());
			if(i<parameters.length - 1) {
				sb.append(",");	
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public int getCount(Object element, boolean filterObject, boolean printItems) {
		Method[] methods = element.getClass().getMethods();
		SortedSet<String> counts=new SortedSet<String>();
		for(int i=0;i<methods.length;i++) {
			String signature = getSignature(methods[i]);
			if(methods[i].getDeclaringClass() == Object.class) {
				continue;
			}
			if (Modifier.isStatic(methods[i].getModifiers())) {
				counts.with(signature);
			} else if (Modifier.isPublic(methods[i].getModifiers())) {
				counts.with(signature);
			}
		}
		Field[] fields = element.getClass().getFields();
		for(int i=0;i<fields.length;i++) {
			if (Modifier.isStatic(fields[i].getModifiers())) {
				counts.with(fields[i].getName()+":"+fields[i].getType());
			} else if (Modifier.isPublic(fields[i].getModifiers())) {
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
