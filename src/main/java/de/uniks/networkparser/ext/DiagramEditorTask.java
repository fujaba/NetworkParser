package de.uniks.networkparser.ext;

import java.lang.Thread.UncaughtExceptionHandler;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.generic.ReflectionLoader;

/**
 * The Class DiagramEditorTask.
 *
 * @author Stefan
 */
public class DiagramEditorTask implements Runnable, UncaughtExceptionHandler {
	
	/** The Constant TYPE_OPEN. */
	public static final String TYPE_OPEN = "open";
	
	/** The Constant TYPE_SCREENDUMP. */
	public static final String TYPE_SCREENDUMP = "screendump";
	
	/** The Constant TYPE_EXIT. */
	public static final String TYPE_EXIT = "exit";
	
	/** The Constant TYPE_EXCEPTION. */
	public static final String TYPE_EXCEPTION = "exception";
	private DiagramEditor editor;
	private boolean wait;
	private Object entity;
	private String type;
	private int width;
	private int height;
	private int value;
	private String msg;

	/**
	 * Creates the exception.
	 *
	 * @param editor the editor
	 * @return the diagram editor task
	 */
	public static DiagramEditorTask createException(DiagramEditor editor) {
		DiagramEditorTask task = new DiagramEditorTask();
		task.type = TYPE_EXCEPTION;
		task.editor = editor;
		return task;
	}

	/**
	 * Creates the exit.
	 *
	 * @param exitCode the exit code
	 * @param msg the msg
	 * @return the diagram editor task
	 */
	public static DiagramEditorTask createExit(int exitCode, String msg) {
		DiagramEditorTask task = new DiagramEditorTask();
		task.type = TYPE_EXIT;
		task.value = exitCode;
		task.msg = msg;
		return task;
	}

	/**
	 * Creates the screen dump.
	 *
	 * @param editor the editor
	 * @return the diagram editor task
	 */
	public static DiagramEditorTask createScreenDump(DiagramEditor editor) {
		DiagramEditorTask task = new DiagramEditorTask();
		task.type = TYPE_SCREENDUMP;
		task.editor = editor;
		return task;
	}

	/**
	 * Creates the open.
	 *
	 * @param editor the editor
	 * @param wait the wait
	 * @param entity the entity
	 * @param width the width
	 * @param height the height
	 * @return the diagram editor task
	 */
	public static DiagramEditorTask createOpen(DiagramEditor editor, boolean wait, Object entity, int width,
			int height) {
		DiagramEditorTask task = new DiagramEditorTask();
		task.type = TYPE_OPEN;
		task.editor = editor;
		task.wait = wait;
		task.entity = entity;
		task.width = width;
		task.height = height;
		return task;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (TYPE_OPEN.equals(this.type)) {
			Object stage = ReflectionLoader.newInstance(ReflectionLoader.STAGE);
			editor.creating(stage, entity, width, height);
			editor.withIcon(IdMap.class.getResource("np.png").toString());
			editor.show(wait);
			return;
		}
		if (TYPE_EXIT.equals(this.type)) {
			if (this.msg != null) {
				NetworkParserLog logger = editor.getLogger();
				if (logger != null) {
					logger.debug(this, "run", this.msg);
				}
			}
			System.exit(this.value);
		}
		if (TYPE_SCREENDUMP.equals(this.type)) {
			if (editor != null) {
				editor.screendump(null);
			}
			return;
		}
	}

	/**
	 * Uncaught exception.
	 *
	 * @param t the t
	 * @param e the e
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (this.editor != null) {
			this.editor.saveException(e);
		}
	}
}