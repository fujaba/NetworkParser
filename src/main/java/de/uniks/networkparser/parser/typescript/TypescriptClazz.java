package de.uniks.networkparser.parser.typescript;

import de.uniks.networkparser.parser.Template;

/**
 * Template for Generation Typescript Classes
 * @author Stefan Lindel
 */
public class TypescriptClazz extends Template {
	/* TODO return superclazzes and interfaces, to add proper imports via short */
	/* clazz name */
	public TypescriptClazz() {
		this.id = TYPE_TYPESCRIPT + ".clazz";
		this.fileType = "clazz";
		this.metaModel = true;
		this.extension = "ts";
		this.withTemplate("{{#template PACKAGE {{packagename}}}}'use strict':", "", "", "{{#endtemplate}}",
				"{{#template IMPORT}}{{#foreach {{file.headers}}}}import { {{item}} } from \"./{{item}}\";", "",
				"{{#endfor}}{{#endtemplate}}", "",
				"{{#if {{type}}==interface}}export interface{{#else}}export default class{{#endif}} {{name}}{{#if {{superclazz}}}} extends {{superclazz}}{{#endif}}{{#if {{implements}}}} implements {{implements}}{{#endif}}",
				"{", "{{#template TEMPLATEEND}}}{{#endtemplate}}");

		this.addTemplate(new TypescriptAttribute(), true);
//		this.addGenerator(new TypesriptAssociation());
//		this.addGenerator(new TypescriptMethod());
	}
}
