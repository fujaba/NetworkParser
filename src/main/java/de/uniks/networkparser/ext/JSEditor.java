package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.gui.JavaAdapter;

/**
 * Javascript Editor.
 *
 * @author Stefan Lindel
 */
public class JSEditor implements Runnable {
	private String executeScript;
	private Object editor;

	/**
	 * Instantiates a new JS editor.
	 */
	public JSEditor() {
	}

	/**
	 * Instantiates a new JS editor.
	 *
	 * @param jsEditor the js editor
	 */
	public JSEditor(Object jsEditor) {
		this.editor = jsEditor;
	}

	/**
	 * With script.
	 *
	 * @param value the value
	 * @return the JS editor
	 */
	public JSEditor withScript(String value) {
		this.executeScript = value;
		return this;
	}

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	public Object getEditor() {
		return editor;
	}

	/**
	 * Sets the board style.
	 *
	 * @param string the new board style
	 */
	public void setBoardStyle(String string) {
		execute("setBoardStyle", string);
	}

	/**
	 * Import model.
	 *
	 * @param model the model
	 */
	public void importModel(String model) {
		execute("import", model);
	}

	private void execute(String method, Object... args) {
		if (editor != null) {
			ReflectionLoader.call(editor, "call", String.class, method, Object[].class, args);
		}
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (editor instanceof JavaAdapter) {
			((JavaAdapter) editor).executeScript(executeScript);
		}
	}
}
