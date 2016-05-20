package de.uniks.networkparser.parser;

import de.uniks.networkparser.buffer.Buffer;

public interface ParseElement {
	public boolean checkMethodsJavaDoc(Buffer buffer, JavaParser parent);
}
