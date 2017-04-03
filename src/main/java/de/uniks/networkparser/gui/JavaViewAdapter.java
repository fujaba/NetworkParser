package de.uniks.networkparser.gui;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;

public interface JavaViewAdapter extends ObjectCondition {

	public JavaViewAdapter withOwner(JavaBridge owner);
	public String readFile(String file);
	public Object executeScript(String script);
	public boolean load(HTMLEntity entity);
	public Object getWebView();
	public void loadFinish();
}
