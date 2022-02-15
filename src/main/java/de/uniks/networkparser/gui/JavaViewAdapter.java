package de.uniks.networkparser.gui;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * The Interface JavaViewAdapter.
 *
 * @author Stefan
 */
public interface JavaViewAdapter extends ObjectCondition {
	
	/** The Constant SUCCEEDED. */
	public static final String SUCCEEDED = "SUCCEEDED";
	
	/** The Constant DRAGOVER. */
	public static final String DRAGOVER = "Drag_Over";
	
	/** The Constant DRAGDROPPED. */
	public static final String DRAGDROPPED = "Drag_Dropped";
	
	/** The Constant ERROR. */
	public static final String ERROR = "Error";
	
	/** The Constant DRAGEXITED. */
	public static final String DRAGEXITED = "Drag_Exited";
	
	/** The Constant STATE. */
	public static final String STATE = "javafx.concurrent.Worker$State";
	
	/** The Constant FAILED. */
	public static final Object FAILED = "FAILED";

	/**
	 * With owner.
	 *
	 * @param owner the owner
	 * @return the java view adapter
	 */
	public JavaViewAdapter withOwner(JavaBridge owner);

	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the string
	 */
	public String readFile(String file);

	/**
	 * Execute script.
	 *
	 * @param script the script
	 * @return the object
	 */
	public Object executeScript(String script);

	/**
	 * Load.
	 *
	 * @param entity the entity
	 * @return true, if successful
	 */
	public boolean load(Object entity);

	/**
	 * Gets the web view.
	 *
	 * @return the web view
	 */
	public Object getWebView();

	/**
	 * Gets the web engine.
	 *
	 * @return the web engine
	 */
	public Object getWebEngine();

	/**
	 * Load finish.
	 */
	public void loadFinish();

	/**
	 * Enable debug.
	 */
	public void enableDebug();
}
