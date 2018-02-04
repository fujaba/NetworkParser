package de.uniks.networkparser.gui;

import de.uniks.networkparser.interfaces.ObjectCondition;

public interface JavaViewAdapter extends ObjectCondition {
	public static final String SUCCEEDED = "SUCCEEDED";
	public static final String DRAGOVER="Drag_Over";
	public static final String DRAGDROPPED="Drag_Dropped";
	public static final String ERROR="Error";
	public static final String DRAGEXITED="Drag_Exited";
    public static final String STATE="javafx.concurrent.Worker$State";
	
	public JavaViewAdapter withOwner(JavaBridge owner);
	public String readFile(String file);
	public Object executeScript(String script);
	public boolean load(Object entity);
	public Object getWebView();
	public Object getWebEngine();
	public void loadFinish();
}
