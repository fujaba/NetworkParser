package de.uniks.networkparser.parser.java;

import java.util.HashMap;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;

/**
 * The Class JavaSetCreator.
 *
 * @author Stefan
 */
public class JavaSetCreator extends Template {
	
	/**
	 * Instantiates a new java set creator.
	 */
	public JavaSetCreator() {
		this.id = TYPE_JAVA + ".set";
		this.extension = "java";
		this.path = "util";
		this.postfix = "Set";
		this.fileType = "clazz";
		this.type = TEMPLATE;
		this.includeSuperValues = true;

		this.withTemplate(
				"{{#template PACKAGE}}{{#if {{packageName}} ?package {{packageName}}.util;}}{{#endtemplate}}",
				"",

				"{{#template IMPORT}}"
						+"{{#foreach {{file.headers}}}}", 
							"import {{item}};"
						+"{{#endfor}}"
				+"{{#endtemplate}}", 
				"{{#import {{fullName}}}}",
				 "{{visibility}} class {{name}}Set extends {{#feature SETCLASS=" + SimpleSet.class.getName()+ "}}<{{name}}> "+
				"{{#if {{#feature SERIALIZATION}}}}" +
					 "{{#import " + SendableEntityCreator.class.getName() + "}}",
						"implements SendableEntityCreator {"+
				"{{#else}}"
					+" {",
					"	public static final String REMOVE = \"rem\";",
				"{{#endif}}"+ 
				"",
/* SendableCreator */
				"	private final String[] properties = new String[] {",
				"{{#foreach childtransitive}}",
					"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
					"		{{name}}.PROPERTY_{{item.NAME}},",
					"{{#endif}}",
					"{{#if {{#and}}{{item.className}}==" + Association.class.getName()
							+ " {{item.other.isGenerate}}{{#endand}}}}",
					"{{#import {{item.other.clazz.fullName}}}}",
					"		{{name}}.PROPERTY_{{item.other.NAME}},",
					"{{#endif}}",
				"{{#endfor}}",
				"{{#if {{#feature DYNAMICVALUES}}}}",
				"	SendableEntityCreator.DYNAMIC",
				"{{#endif}}",
				"	};",
				"",
				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + "}}",
				"	public static final {{name}}Set EMPTY_SET = new {{name}}Set().withFlag({{name}}Set.READONLY);"
						+ "{{#else}}",
				"	private static final long serialVersionUID = 1L;",
				"	public static final {{name}}Set EMPTY_SET = new {{name}}Set();",
				"{{#if {{#feature PATTERN}}}}",
				"	protected ObjectCondition listener;", "",
				"{{#import " + ObjectCondition.class.getName() + "}}"
						+ "	public {{name}}Set withListener(ObjectCondition listener) {",
				"		this.listener = listener;",
				"		return this;",
				"	}",
				"",
				"{{#endif}}",
				"	public {{name}}Set withVisible(boolean value) {",
				"		return this;",
				"	}",
				"{{#endif}}",
				"",

				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + " ?	@Override}}",
				"	public String[] getProperties() {",
				"		return properties;",
				"	}",
				"",

				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + " ?	@Override}}",
				"	public Object getSendableInstance(boolean prototyp) {",
				"{{#if {{#AND}}{{type}}==class {{#NOT}}{{modifiers#contains(abstract)}}{{#ENDNOT}}{{#ENDAND}}}}",
				"		return new {{name}}();",
				"{{#else}}",
				"		return null;",
				"{{#endif}}",
				"	}",
				"",

				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + " ?	@Override}}",
				"	public Object getValue(Object entity, String attribute) {",
				"		if(attribute == null || entity instanceof {{name}} == false) {",
				"			return null;",
				"		}",
				"		{{name}} element = ({{name}})entity;",
				"{{#foreach childtransitive}}",
				"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
				"		if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attribute)) {",
				"			return element.{{#if {{item.type}}==boolean?is:get}}{{item.Name}}();",
				"		}",
				"",
				"{{#endif}}",
				"{{#if {{#and}}{{item.className}}==" + Association.class.getName()
						+ " {{item.other.isGenerate}}{{#endand}}}}",
				"		if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attribute)) {",
				"			return element.get{{item.other.Name}}();",
				"		}",
				"",
				"{{#endif}}",
				"{{#endfor}}",
				"{{#if {{#feature DYNAMICVALUES}}}}",
				"		if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attribute)) {",
				"			return element.getDynamicValues();",
				"		}",
				"		return element.getDynamicValue(attribute);",
				"{{#else}}",
				"		return null;",
				"{{#endif}}",
				"	}", "",

				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + " ?	@Override}}",
				"	public boolean setValue(Object entity, String attribute, Object value, String type) {",
				"		if(attribute == null || entity instanceof {{name}} == false) {",
				"			return false;",
				"		}",
				"{{#debug}}{{#if childtransitive.size()>0}}",
				"		{{name}} element = ({{name}})entity;",
				"		if (REMOVE.equals(type) && value != null) {",
				"			attribute = attribute + type;",
				"		}", "",
				"{{#endif}}{{#foreach childtransitive}}",
					"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
						"{{#ifnot {{item.modifiers#contains(static)}}}}",
						"		if ({{name}}.PROPERTY_{{item.NAME}}.equalsIgnoreCase(attribute)) {",
								"{{#import {{item.type(false)}}}}",
								
								"{{#if {{item.typecat}}==SET}}",
								"			if(value instanceof {{item.type.name}}) {",
								"				return element.add{{item.Name}}(({{item.type.name}}) value);",
								"			}",
/* ADD SWITCH FOR PRIMITIVE VALUES */
								"{{#import java.util.Collection}}"+
									"{{#if item.type.external}}"+
									" 			{{item.type}} newValue = new {{item.type}}();",
									" 			newValue.addAll((Collection<? extends {{item.type.name}}>) value);", 
									"			return element.set{{item.Name}}(newValue);",
									"{{#else}}",
									" 			{{item.type}} newValue = new {{item.type}}().withList((Collection<?>) value);", 
									"			return element.set{{item.Name}}(newValue);",
									"{{#endif}}",
								"{{#else}}",
									"			return element.set{{item.Name}}(({{item.type.objectname}}) value);",
								"{{#endif}}",
								"		}",
								"",
						"{{#endif}}",
					"{{#endif}}",
					"{{#if {{item.className}}==" + Association.class.getName() + "}}",
						"{{#if {{item.other.isGenerate}}}}",
						"		if ({{name}}.PROPERTY_{{item.other.NAME}}.equalsIgnoreCase(attribute)) {",
						"			element.{{#if {{item.other.cardinality}}==1}}set{{#else}}with{{#endif}}{{item.other.Name}}(({{item.other.clazz.name}}) value);",
					"			return true;",
					"		}",
					"",
						"{{#endif}}",
					"{{#endif}}",
				"{{#endfor}}",
				"{{#if {{#feature DYNAMICVALUES}}}}",
				"		element.withDynamicValue(attribute, value);",
				"		return true;", "{{#else}}",
				"		return false;", "{{#endif}}",
				"	}",
				"",
				"{{#if {{#feature SERIALIZATION}}}}",
				"{{#import " + IdMap.class.getName() + "}}" + "	public static IdMap createIdMap(String session) {",
				"		return CreatorCreator.createIdMap(session);",
				"	}",
				"{{#else}}",
				"	private static HashMap<String, Class<?>> modelList = new HashMap<String, Class<?>>();{{#import "+HashMap.class.getName()+"}}",
				"	public static Class<?> getModelType(String property) {",
				"		if(modelList == null) {",
							"           modelList = new HashMap<String, Class<?>>();",
							"{{#foreach childtransitive}}",
							"{{#if {{item.className}}==" + Attribute.class.getName() + "}}",
							"           modelList.put({{name}}.PROPERTY_{{item.NAME}}, {{item.type.container}}.class);",
							"           modelList.put({{name}}.PROPERTY_{{item.NAME}}.toLowerCase(), {{item.type.container}}.class);",
							"{{#endif}}",

								"{{#if {{#and}}{{item.className}}==" + Association.class.getName() + " {{item.other.isGenerate}}{{#endand}}}}",
								"{{#import {{item.other.clazz.fullName}}}}",
								"           modelList.put({{name}}.PROPERTY_{{item.other.NAME}}, {{item.other.clazz.name}}.class);",
								"           modelList.put({{name}}.PROPERTY_{{item.other.NAME}}, {{item.other.clazz.name}}.class);",
								"{{#endif}}",
							"{{#endfor}}",	
					"       }",
					"		return modelList.get(property);",	
				"   }",
				"{{#endif}}",

				"	public Class<?> getTypClass() {",
				"		return {{name}}.class;", "	}",
				"",
				"",
				"",
				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + " ?	@Override}}",
				"	public {{name}}Set getNewList(boolean keyValue) {",
				"		return new {{name}}Set();",
				"	}",
				"",
				"",
				"{{#template TEMPLATEEND}}}{{#endtemplate}}");
		this.addTemplate(new JavaSetAttribute(), true);
		this.addTemplate(new JavaSetAssociation(), true);
		this.addTemplate(new JavaSetMethod(), true);
	}

	/**
	 * Execute clazz.
	 *
	 * @param clazz the clazz
	 * @param parameters the parameters
	 * @param isStandard the is standard
	 * @return the template result file
	 */
	@Override
	public TemplateResultFile executeClazz(Clazz clazz, LocalisationInterface parameters, boolean isStandard) {
		FeatureSet features = getFeatures(parameters);
		if (features != null) {
			if (features.match(Feature.SETCLASS, null) == false) {
				return null;
			}
		}
		return super.executeClazz(clazz, parameters, isStandard);
	}

	protected boolean isValid(GraphMember member, LocalisationInterface parameters) {
		if (super.isValid(member, parameters) == false) {
			return false;
		}
		/* Check for existing Feature Serialization */
		Feature features = getFeature(Feature.SERIALIZATION, member.getClazz());
		return features != null;
	}
}
