package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class JSEditor {
	private Object editor;

	public JSEditor(Object jsEditor) {
		this.editor = jsEditor;
	}

	public Object getEditor() {
		return editor;
	}

	public void setBoardStyle(String string) {
		execute("setBoardStyle", string);
	}
	
	public void importModel(String model) {
		execute("import", model);
	}
	
	private void execute(String method, Object... args) {
		if(editor != null) {
			ReflectionLoader.call("call", editor, String.class, method, Object[].class, args);
		}
	}
}
