package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.gui.JavaAdapter;

/** Javascript Editor */
public class JSEditor implements Runnable {
	private String executeScript;
	private Object editor;

	public JSEditor() {
	}

	public JSEditor(Object jsEditor) {
		this.editor = jsEditor;
	}

	public JSEditor withScript(String value) {
		this.executeScript = value;
		return this;
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
		if (editor != null) {
			ReflectionLoader.call(editor, "call", String.class, method, Object[].class, args);
		}
	}

	@Override
	public void run() {
		if (editor instanceof JavaAdapter) {
			((JavaAdapter) editor).executeScript(executeScript);
		}
	}
}
