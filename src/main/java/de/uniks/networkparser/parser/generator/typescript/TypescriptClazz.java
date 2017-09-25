package de.uniks.networkparser.parser.generator.typescript;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.parser.generator.BasicGenerator;

public class TypescriptClazz extends BasicGenerator {
	@Override
	public Class<?> getTyp() {
		return Clazz.class;
	}

	public TypescriptClazz() {
		
	}
}
