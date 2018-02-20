package de.uniks.networkparser.parser.generator.typescript;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.parser.Template;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class TypescriptClazz extends BasicGenerator {
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}

	// TODO return superclazzes and interfaces, to add proper imports via short clazz name
	public TypescriptClazz() {
		createTemplate("Declaration", Template.TEMPLATE,
				"{{#template PACKAGE {{packagename}}}}'use strict':","","","{{#endtemplate}}",
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}import { {{item}} } from \"./{{item}}\";","","{{#endfor}}{{#endtemplate}}","",
				"{{#if {{type}}==interface}}export interface{{#else}}export default class{{#endif}} {{name}}{{#if {{superclazz}}}} extends {{superclazz}}{{#endif}}{{#if {{implements}}}} implements {{implements}}{{#endif}}","{",
				"{{#template TEMPLATEEND}}}{{#endtemplate}}");

		this.metaModel = true;
		this.extension = "ts";
		// TODO add classes
		this.addGenerator(new TypescriptAttribute());
//		this.addGenerator(new TypesriptAssociation());
//		this.addGenerator(new TypescriptMethod());
	}
}
