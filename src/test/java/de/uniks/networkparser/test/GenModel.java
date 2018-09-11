package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ClassModelBuilder;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.story.StoryStepSourceCode;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeMap;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.Throws;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class GenModel {
	@Test
	public void showCountsModel() {
		int count =0;
		HTMLEntity sdmLib=new HTMLEntity();
		String packageName = GraphUtil.getPackage(Clazz.class);

		ClassModel model;
		model = new ClassModel(packageName);
		count += showCounting(ClassModel.class, sdmLib, model);
		count += showCounting(Clazz.class, sdmLib, model);
		sdmLib.withGraph(model).withPageBreak();

		model = new ClassModel(packageName);
		count += showCounting(Attribute.class, sdmLib, model);
		count += showCounting(Method.class, sdmLib, model);
		count += showCounting(DataType.class, sdmLib, model);
		sdmLib.withGraph(model);

		model = new ClassModel(packageName);
		count += showCounting(DataTypeSet.class, sdmLib, model);
		count += showCounting(DataTypeMap.class, sdmLib, model);
		sdmLib.withGraph(model).withPageBreak();

		model = new ClassModel(packageName);
		count += showCounting(Annotation.class, sdmLib, model);
		count += showCounting(Association.class, sdmLib, model);
		sdmLib.withGraph(model);



		model = new ClassModel(packageName);
		count += showCounting(Modifier.class, sdmLib, model);
		count += showCounting(Parameter.class, sdmLib, model);
		count += showCounting(Throws.class, sdmLib, model);
		sdmLib.withGraph(model);

//		model = new ClassModel();
//		count += showCounting(Value.class, sdmLib, model);
//		sdmLib.withPageBreak().withGraph(model);

		sdmLib.withText("API-Count: "+count);
//		sdmLib.withGraph(model);
		sdmLib.withPageBreak();
		packageName = GraphUtil.getPackage(ClassModelBuilder.class);
		model = new ClassModel(packageName);
		showCounting(ClassModelBuilder.class, sdmLib, model);
		sdmLib.withGraph(model);

		StoryStepSourceCode step = new StoryStepSourceCode();
		step.withCode("ClassModelBuilder builder = new ClassModelBuilder(\"de.uniks.model\");\r\n" + 
				"		\r\n" + 
				"\r\n" + 
				"		Clazz person = builder.buildClass(\"Person\");\r\n" + 
				"		builder.createAttribute(\"name\", DataType.STRING)\r\n" + 
				"			.createAttribute(\"matrikelno\", DataType.INT);\r\n" + 
				"		\r\n" + 
				"		builder.createClass(\"University\").createAttribute(\"name\", DataType.STRING);\r\n" + 
				"		\r\n" + 
				"		builder.createAssociation(\"student\", Association.MANY, person, \"studs\", Association.ONE);\r\n" + 
				"\r\n" + 
				"		\r\n" + 
				"		builder.build();");
		step.addToHTML(sdmLib);

		FileBuffer.writeFile("build/sdmlib.html", sdmLib.toString());
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
			name = name.substring(pos+1);
		}
		if(classType.isArray()) {
			if(name.endsWith(";")) {
				return name.substring(0,name.length() - 1)+"...";
			}
			return name+"[]";
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


	public int showCounting(Class<?> element, HTMLEntity htmlEntity, ClassModel model) {
		Clazz graphClazz = model.createClazz(element.getSimpleName());
		int count = getCounting(element, graphClazz);

		htmlEntity.withText(element.getSimpleName()+": "+count);
		htmlEntity.withNewLine();

		if(java.lang.reflect.Modifier.isAbstract(element.getModifiers()) ) {
			return 0;
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
			System.out.println("ERRORS:"+item);
		}
		return count;
	}

	public static boolean isOverriden(java.lang.reflect.Method parent, java.lang.reflect.Method toCheck) {
	    if (parent.getDeclaringClass().isAssignableFrom(toCheck.getDeclaringClass())
	            && parent.getName().equals(toCheck.getName())) {
	         Class<?>[] params1 = parent.getParameterTypes();
	         Class<?>[] params2 = toCheck.getParameterTypes();
	         if (params1.length == params2.length) {
	             for (int i = 0; i < params1.length; i++) {
	                 if (!params1[i].equals(params2[i])) {
	                     return false;
	                 }
	             }
	             return true;
	         }
	    }
	    return false;
	}

	public int getCounting(Class<?> element, Clazz graphClazz) {
		java.lang.reflect.Method[] methods = element.getMethods();
//		SortedSet<String> counts=new SortedSet<String>(true);
		int no=0;
		SimpleKeyValueList<String, SimpleList<java.lang.reflect.Method>> overridenMethods=new SimpleKeyValueList<String, SimpleList<java.lang.reflect.Method>>();
		for(int i=0;i<methods.length;i++) {
			if(methods[i].getDeclaringClass() == Object.class || methods[i].getDeclaringClass() == Enum.class) {
				continue;
			}
//			String signature = getSignature(methods[i]);
			if (java.lang.reflect.Modifier.isStatic(methods[i].getModifiers())
					|| (java.lang.reflect.Modifier.isPublic(methods[i].getModifiers())
							&& "toString".equalsIgnoreCase(methods[i].getName()) == false)) {
						// ADD IT
						SimpleList<java.lang.reflect.Method> items = overridenMethods.get(methods[i].getName());
						if(items == null) {
							items = new SimpleList<java.lang.reflect.Method>();
							overridenMethods.put(methods[i].getName(), items);
						}

						boolean add = true;
						for(java.lang.reflect.Method m1 : items) {
							if(isOverriden(m1, methods[i])) {
								add = false;
								break;
							}
						}
						if(add == false) {
							continue;
						}
				//				sb.append(shortName(method.getDeclaringClass())+" "++"(");
						Method graphMethod = graphClazz.createMethod(methods[i].getName());
						java.lang.reflect.Parameter[] parameters = methods[i].getParameters();
						for(int p = 0;p<parameters.length;p++) {
							graphMethod.with(new Parameter(DataType.create(shortName(parameters[p].getType()))));
						}
						if(methods[i].getReturnType()!= null) {
							graphMethod.with(DataType.create(shortName(methods[i].getReturnType())));
						}
						items.add(methods[i]);
						no++;
			}
//
//				graphClazz.createMethod(item);
////				counts.add(signature);
//			} else if ({
//				if(signature.endsWith("toString()")==false) {
////					counts.add(signature);
//				}
//			}
		}
		java.lang.reflect.Field[] fields = element.getClass().getFields();
		for(int i=0;i<fields.length;i++) {
			if (java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())) {
				graphClazz.createAttribute(fields[i].getName(), DataType.create(fields[i].getType()));
				no++;
			} else if (java.lang.reflect.Modifier.isPublic(fields[i].getModifiers())) {
				graphClazz.createAttribute(fields[i].getName(), DataType.create(fields[i].getType()));
				no++;
			}
		}
		return no;
	}
}
