package de.uniks.networkparser.parser.java;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.TemplateResultFile;

/**
 * The Class JavaSet.
 *
 * @author Stefan
 */
public class JavaSet extends Template {
	
	/**
	 * Instantiates a new java set.
	 */
	public JavaSet() {
		this.id = TYPE_JAVA + ".set";
		this.extension = "java";
		this.path = "util";
		this.postfix = "Set";
		this.fileType = "clazz";
		this.type = TEMPLATE;

		this.withTemplate(
				"{{#template PACKAGE}}{{#if {{packageName}}}}package {{packageName}}.util;{{#endif}}{{#endtemplate}}",
				"",

				"{{#template IMPORT}}{{#foreach {{file.headers}}}}", "import {{item}};{{#endfor}}{{#endtemplate}}", "",

				"{{#import {{fullName}}}}" + "{{visibility}} class {{name}}Set extends {{#feature SETCLASS="
						+ SimpleSet.class.getName() + "}}<{{name}}> {",
				"",

				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + "}}"
						+ "	public static final {{name}}Set EMPTY_SET = new {{name}}Set().withFlag({{name}}Set.READONLY);"
						+ "{{#else}}" + "	private static final long serialVersionUID = 1L;",
				"	public static final {{name}}Set EMPTY_SET = new {{name}}Set();", "{{#if {{#feature PATTERN}}}}",
				"	protected ObjectCondition listener;", "",
				"{{#import " + ObjectCondition.class.getName() + "}}"
						+ "	public {{name}}Set withListener(ObjectCondition listener) {",
				"		this.listener = listener;", "		return this;", "	}", "", "{{#endif}}",
				"	public {{name}}Set withVisible(boolean value) {",
				"		return this;", "	}",
				"{{#endif}}",
				"",
				"	public Class<?> getTypClass() {",
				"		return {{name}}.class;",
				"	}",
				"",
				"",
				"{{#if {{templatemodel.features.setclass.classstring}}==" + SimpleSet.class.getName() + "}}"
					+ "	@Override","",
				"{{#endif}}" +
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
}
