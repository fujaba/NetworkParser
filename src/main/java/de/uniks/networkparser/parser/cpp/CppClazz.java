package de.uniks.networkparser.parser.cpp;

import de.uniks.networkparser.parser.Template;

/**
 * Template for Generation CPP Classes.
 *
 * @author Stefan Lindel
 */
public class CppClazz extends Template {
	
	/**
	 * Instantiates a new cpp clazz.
	 */
	public CppClazz() {
		this.id = TYPE_CPP + ".clazz";
		this.fileType = "clazz";
	}
}
