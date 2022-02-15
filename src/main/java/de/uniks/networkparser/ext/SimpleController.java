package de.uniks.networkparser.ext;

import java.io.BufferedReader;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.gui.GUIEvent;
import de.uniks.networkparser.ext.gui.JavaAdapter;
import de.uniks.networkparser.ext.gui.ModelListenerProperty;
import de.uniks.networkparser.ext.io.StringOutputStream;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class SimpleController.
 *
 * @author Stefan
 */
public class SimpleController implements ObjectCondition, UncaughtExceptionHandler, Runnable {
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "------";
	
	/** The Constant USER. */
	public static final String USER = "USER";
	
	/** The Constant USERNAME. */
	public static final String USERNAME = "USERNAME";
	
	/** The Constant CLOSE. */
	public static final String CLOSE = "close";
	
	/** The Constant SHOWING. */
	public static final String SHOWING = "SHOWING";
	
	/** The Constant CREATING. */
	public static final String CREATING = "CREATING";
	private Object stage;
	private Object rootScene;
	private JavaBridge bridge;
	private boolean firstShow = true;
	protected String icon;
	private String encodingCode = BaseItem.ENCODING;
	private String title;
	private ErrorHandler errorHandler = new ErrorHandler();
	protected Object popupMenu;
	protected Object trayIcon;
	private SimpleList<Object> listener = new SimpleList<Object>();
	private boolean isEclipse = Os.isEclipse();
	private String javaAgent;
	private String mainClass;
	private String outputParameter;
	private SimpleList<String> compilePath;
	private Object controller;
	private Object[] runParams;
	private String runAction;
	private SimpleKeyValueList<Object, SendableEntityCreator> mapping;
	private SimpleList<SendableEntityCreator> controllers;
	private IdMap map;
	private static NetworkParserLog logger = new NetworkParserLog();

	/**
	 * Instantiates a new simple controller.
	 */
	public SimpleController() {
	}

	/**
	 * Instantiates a new simple controller.
	 *
	 * @param primitiveStage the primitive stage
	 */
	public SimpleController(Object primitiveStage) {
		this(primitiveStage, true);
	}

	/**
	 * Instantiates a new simple controller.
	 *
	 * @param primitiveStage the primitive stage
	 * @param init the init
	 */
	public SimpleController(Object primitiveStage, boolean init) {
		withStage(primitiveStage);
		if (init) {
			this.init();
		}
	}

	/**
	 * Show content.
	 *
	 * @param element the element
	 */
	public void showContent(Object element) {
		Object content = this.createContent(element);
		if (content != null) {
			System.setOut(new StringPrintStream());
			this.show(content);
		}
	}

	/**
	 * Creates the content.
	 *
	 * @param element the element
	 * @return the object
	 */
	public Object createContent(Object element) {
		if (element == null) {
			return null;
		}
		try {
			if (element instanceof String) {
				return null;
			}
			return ReflectionLoader.calling(element, "createContent", false, this);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * With stage.
	 *
	 * @param stage the stage
	 * @return the simple controller
	 */
	public SimpleController withStage(Object stage) {
		this.stage = stage;
		if (stage != null && stage.getClass().getName().startsWith("javafx")) {
			GUIEvent proxyHandler = new GUIEvent();
			proxyHandler.withListener(this);
			Object proxy = ReflectionLoader.createProxy(proxyHandler, ReflectionLoader.EVENTHANDLER);

			ReflectionLoader.call(stage, "setOnCloseRequest", ReflectionLoader.EVENTHANDLER, proxy);
			ReflectionLoader.call(stage, "setOnShowing", ReflectionLoader.EVENTHANDLER, proxy);
		}
		return this;
	}

	private Object getApplication() {
		Field params;
		if (Os.isFXThread() == false) {
			return null;
		}
		try {
			params = ReflectionLoader.PARAMETER.getDeclaredField("params");
			params.setAccessible(true);
			Object value = params.get(null);
			if (value instanceof Map<?, ?>) {
				Map<?, ?> map = (Map<?, ?>) value;
				Object[] keys = map.keySet().toArray();
				if (keys.length > 0) {
					return keys[keys.length - 1];
				}
			}
		} catch (Exception e) {
			errorHandler.saveException(e);
		}
		return null;
	}

	/**
	 * Creates the.
	 *
	 * @param primaryStage the primary stage
	 * @return the simple controller
	 */
	public static SimpleController create(Object primaryStage) {
		SimpleController controller = new SimpleController(primaryStage);
		return controller;
	}

	protected Process init() {
		String outputFile = null;
		String debugPort = null;
		if (encodingCode != null && !encodingCode.equalsIgnoreCase(System.getProperty("file.encoding"))) {
			System.setProperty("file.encoding", encodingCode);
			Class<Charset> c = Charset.class;

			java.lang.reflect.Field defaultCharsetField;
			try {
				defaultCharsetField = c.getDeclaredField("defaultCharset");
				defaultCharsetField.setAccessible(true);
				defaultCharsetField.set(null, null);
			} catch (Exception e) {
			}
		}
		SimpleKeyValueList<String, String> params = getParameterMap();
		/* Example
		-Xms<size>        set initial Java heap size
		-Xmx<size>        set maximum Java heap size
		-Xss<size>        set java thread stack size*/
		ArrayList<String> customParams = new ArrayList<String>();
		for (int i = 0; i < params.size(); i++) {
			String key = params.get(i);
			if (key == null) {
				continue;
			}
			String value = params.getValueByIndex(i);
			if (key.equalsIgnoreCase("config")) {
				if (value != null) {
					StartData.setFileName(value);
				}
			} else if (key.equalsIgnoreCase("debug")) {
				if (value != null) {
					debugPort = value;
				} else {
					debugPort = "4223";
				}
			} else if (key.equalsIgnoreCase("output")) {
				if (value != null) {
					outputFile = value;
				} else {
					outputFile = "output.txt";
				}
			} else if (key.equalsIgnoreCase("-?")) {
				logger.debug(this, "init", getCommandHelp());
				Runtime.getRuntime().exit(1);
			} else if (key.startsWith("-")) {
				if (value != null) {
					customParams.add(key + "=" + value);
				} else {
					customParams.add(key);
				}
			}
		}
		/* IF JAVACOMPILE */
		if (compilePath != null) {
			ArrayList<String> items = new ArrayList<String>();
			String javacExecutor;
			if (Os.isMac()) {
				javacExecutor = System.getProperty("java.home").replace("\\", "/") + "/bin/javac";
			} else {
				javacExecutor = System.getProperty("java.home").replace("\\", "/") + "/../bin/javac.exe";
				if (new File(javacExecutor).exists() == false) {
					javacExecutor = System.getProperty("java.home").replace("\\", "/") + "/bin/javac.exe";
				}
			}
			boolean gradle = false;

			if (new File(javacExecutor).exists()) {
				String path = StringUtil.getPath(javacExecutor, "/");
				items.add("\"" + path + "\"");
			} else if ((Os.isMac() || Os.isUnix()) && new File("gradlew").exists()) {
				items.add("./gradlew");
				gradle = true;
			} else if (Os.isWindows() && new File("gradlew.bat").exists()) {
				items.add("./gradlew.bat");
				gradle = true;
			}
			if (gradle) {
				if (this.mainClass != null) {
					items.add(mainClass);
				} else {
					items.add("compileTestJava");
				}
				items.add("-xTest");
			} else {

				items.add("-classpath");
				if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader == false) {
					return null;
				}
				URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

				StringBuilder buf = new StringBuilder();
				for (URL url : urlClassLoader.getURLs()) {
					buf.append(url.getFile()).append(File.pathSeparator);
				}

				items.add(buf.toString());

				/* Path */
				for (String item : compilePath) {
					items.add(item + "\\*.java");
				}
				items.add("-d");
				/* ReflectionLoader.PROCESSBUILDERREDIRECT */
				if (this.outputParameter != null) {
					items.add(this.outputParameter);
				} else {
					items.add("out");
					new File("out").mkdir();
				}
			}
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(items);
				ReflectionLoader.call(processBuilder, "redirectError", File.class, new File("compile_error.txt"));
				ReflectionLoader.call(processBuilder, "redirectOutput", File.class, new File("compile_stdout.txt"));

				Process start = processBuilder.start();
				return start;
			} catch (IOException e) {
				errorHandler.saveException(e);
			}
			return null;
		}
		if (this.javaAgent != null || debugPort != null) {
			ArrayList<String> items = new ArrayList<String>();
			if (Os.isMac()) {
				items.add(System.getProperty("java.home").replace("\\", "/") + "/bin/java");
			} else {
				items.add("\"" + System.getProperty("java.home").replace("\\", "/") + "/bin/java\"");
			}
			if (debugPort != null) {
				items.add("-Xdebug");
				items.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
				/* Now Add Custom Params */
				items.addAll(customParams);

				items.add("-jar");
				String fileName = Os.getFilename().toLowerCase();
				if ("bin".equals(fileName)) {
					/* Eclipse Start Can't run */
					return null;
				}
				items.add(fileName);
			}
			if (this.javaAgent != null) {
				String path = this.errorHandler.getPath();

				String agent = this.javaAgent;
				if (path != null) {
					agent += "=destfile=" + path + "jacoco.exec";

				}

				items.add("-javaagent:" + agent);
				items.add(DiagramEditor.class.getName());
				items.add("test=" + mainClass);

				if (path != null) {
					items.add("path=" + path);
				}
			}
			if (Os.isReflectionTest()) {
				return null;
			}
			ProcessBuilder processBuilder = new ProcessBuilder(items);
			Map<String, String> environment = processBuilder.environment();
			environment.put("CLASSPATH", System.getProperty("java.class.path"));
			/* ReflectionLoader.PROCESSBUILDERREDIRECT */
			if (outputFile == null && this.outputParameter != null) {
				outputFile = this.outputParameter;
			}
			if (isEclipse == false || this.outputParameter != null) {
				if (outputFile != null) {
					if (outputFile.equalsIgnoreCase("inherit")) {
						processBuilder.redirectErrorStream(true);
						ReflectionLoader.call(processBuilder, "redirectOutput", ReflectionLoader.PROCESSBUILDERREDIRECT,
								ReflectionLoader.getField(ReflectionLoader.PROCESSBUILDERREDIRECT, "INHERIT"));
					} else {
						int pos = outputFile.lastIndexOf(".");
						if (pos > 0) {
							ReflectionLoader.call(processBuilder, "redirectError", File.class,
									new File(outputFile.substring(0, pos) + "_error" + outputFile.substring(pos)));
							ReflectionLoader.call(processBuilder, "redirectOutput", File.class,
									new File(outputFile.substring(0, pos) + "_stdout" + outputFile.substring(pos)));
						} else {
							ReflectionLoader.call(processBuilder, "redirectError", File.class,
									new File(outputFile + "_error.txt"));
							ReflectionLoader.call(processBuilder, "redirectOutput", File.class,
									new File(outputFile + "_stdout.txt"));
						}
					}
				}
			}
			try {
				Process start = processBuilder.start();
				if (this.javaAgent != null) {
					return start;
				}
				Runtime.getRuntime().exit(1);
			} catch (IOException e) {
				errorHandler.saveException(e);
			}
		}
		return null;
	}

	/**
	 * Gets the parameter map.
	 *
	 * @return the parameter map
	 */
	@SuppressWarnings("unchecked")
	public SimpleKeyValueList<String, String> getParameterMap() {
		SimpleKeyValueList<String, String> map = new SimpleKeyValueList<String, String>();
		if (Os.isJUnitTest()) {
			return map;
		}
		List<String> raw = (List<String>) ReflectionLoader.callChain(getApplication(), "getParameters", "getRaw");
		if (raw != null) {
			for (String item : raw) {
				if (item.startsWith("--")) {
					item = item.substring(2);
				}
				int pos = item.indexOf(":");
				int posEnter = item.indexOf("=");
				if (posEnter > 0 && (posEnter < pos || pos == -1)) {
					pos = posEnter;
				}
				if (pos > 0) {
					map.add(item.substring(0, pos), item.substring(pos + 1));
				} else {
					map.add(item, null);
				}
			}
		}
		return map;
	}

	/**
	 * Gets the user name.
	 *
	 * @param defaultName the default name
	 * @return the user name
	 */
	public String getUserName(String... defaultName) {
		SimpleKeyValueList<String, String> parameterMap = getParameterMap();
		for (int i = 0; i < parameterMap.size(); i++) {
			String key = parameterMap.getKeyByIndex(i);
			if (USER.equalsIgnoreCase(key)) {
				return parameterMap.get(i);
			} else if (USERNAME.equalsIgnoreCase(key)) {
				return parameterMap.get(i);
			}
		}
		if (defaultName != null && defaultName.length > 0 && defaultName[0] instanceof String) {
			return defaultName[0];
		}
		return "";
	}

	/**
	 * Show.
	 *
	 * @param root the root
	 * @param wait the wait
	 * @param newStage the new stage
	 */
	public void show(Object root, boolean wait, boolean newStage) {
		if (!Os.isFXThread()) {
			this.runParams = new Object[] { root, wait, newStage };
			this.runAction = SHOWING;
			JavaAdapter.executeAndWait(this);
			return;
		}
		showing(root, wait, newStage);
	}

	private void showing(Object root, boolean wait, boolean newStage) {
		Object oldStage = null;
		if (newStage) {
			oldStage = this.stage;
			withStage(ReflectionLoader.newInstance(ReflectionLoader.STAGE));
			showIcon();
		}
		this.firstShow = false;
		if (stage == null) {
			return;
		}
		Object scene;
		if (root == null) {
			root = rootScene;
		}
		if (ReflectionLoader.SCENE == null || root == null) {
			return;
		}

		if (ReflectionLoader.SCENE.isAssignableFrom(root.getClass())) {
			scene = root;
		} else if (root instanceof JavaBridge) {
			this.bridge = (JavaBridge) root;
			JavaViewAdapter adapter = this.bridge.getViewAdapter();
			Object webView = adapter.getWebView();
			scene = ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, webView);
		} else {
			scene = ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, root);
		}
		ReflectionLoader.call(stage, "setScene", ReflectionLoader.SCENE, scene);

		if (root instanceof ObjectCondition) {
			this.withListener((ObjectCondition) root);
		}
		GUIEvent event = new GUIEvent();
		event.withListener(this);
		Object proxy = ReflectionLoader.createProxy(event, ReflectionLoader.EVENTHANDLER);
		ReflectionLoader.call(scene, "setOnKeyPressed", ReflectionLoader.EVENTHANDLER, proxy);
		showing(wait);
		if (oldStage != null) {
			ReflectionLoader.call(oldStage, "close");
		}
	}

	/**
	 * Show.
	 *
	 * @param root the root
	 */
	public void show(Object root) {
		show(root, false, firstShow == false);
	}

	/**
	 * Show.
	 */
	public void show() {
		show(null, false, firstShow == false);
	}

	/**
	 * Gets the current scene.
	 *
	 * @return the current scene
	 */
	public Object getCurrentScene() {
		return ReflectionLoader.call(stage, "getScene");
	}

	/**
	 * Gets the stage.
	 *
	 * @return the stage
	 */
	public Object getStage() {
		return stage;
	}

	protected void showing(boolean wait) {
		if (this.stage != null) {
			init();
			ReflectionLoader.call(this.stage, "setTitle", getTitle());
			if (Os.isEclipse()) {
				if (wait) {
					ReflectionLoader.calling(this.stage, "showAndWait", true, this.errorHandler);
				} else {
					ReflectionLoader.calling(this.stage, "show", true, this.errorHandler);
				}
			} else {
				try {
					if (wait) {
						ReflectionLoader.calling(this.stage, "showAndWait", true, this.errorHandler);
					} else {
						ReflectionLoader.calling(this.stage, "show", true, this.errorHandler);
					}
				} catch (Exception e) {
					errorHandler.saveException(e, this.stage, true);
				}
			}
		}
	}

	/**
	 * Start.
	 *
	 * @return the int
	 */
	public int start() {
		Process p = this.init();
		if (p == null) {
			return -1;
		}
		try {
			return p.waitFor();
		} catch (InterruptedException e) {
		}
		return -1;
	}

	/**
	 * With agent.
	 *
	 * @param agent the agent
	 * @param backBoxTester the back box tester
	 * @param mainClass the main class
	 * @return the simple controller
	 */
	public SimpleController withAgent(String agent, String backBoxTester, String... mainClass) {
		this.javaAgent = agent;
		CharacterBuffer testClasses = new CharacterBuffer();
		if (mainClass != null) {
			for (String test : mainClass) {
				if (testClasses.length() > 0) {
					testClasses.with(',');
				}
				testClasses.with(test);
			}
		}
		if (backBoxTester != null) {
			if (testClasses.length() > 0) {
				testClasses.with(',');
			}
			testClasses.add("backboxtest=" + backBoxTester);
		}
		this.mainClass = testClasses.toString();
		return this;
	}

	/**
	 * With agent.
	 *
	 * @param agent the agent
	 * @param backBoxTester the back box tester
	 * @param mainClass the main class
	 * @return the simple controller
	 */
	public SimpleController withAgent(String agent, boolean backBoxTester, String... mainClass) {
		if (backBoxTester) {
			return withAgent(agent, "", mainClass);
		}
		return withAgent(agent, null, mainClass);
	}

	/**
	 * With output.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withOutput(String value) {
		this.outputParameter = value;
		return this;
	}

	/**
	 * Gets the encoding code.
	 *
	 * @return the encoding code
	 */
	public String getEncodingCode() {
		return encodingCode;
	}

	/**
	 * With encoding code.
	 *
	 * @param value the value
	 */
	public void withEncodingCode(String value) {
		this.encodingCode = value;
	}

	protected String getCommandHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("Help for the Commandline - ");
		sb.append(getTitle());
		sb.append("\n\n");

		sb.append("Debug\t\tDebug with <port> for debugging. Default is 4223\n");
		sb.append("Output\t\tOutput the debug output in standard-outputstream or file\n");

		return sb.toString();
	}

	/**
	 * With eclipse.
	 *
	 * @param enableThrows the enable throws
	 * @return the simple controller
	 */
	public SimpleController withEclipse(boolean enableThrows) {
		this.isEclipse = enableThrows;
		return this;
	}

	/**
	 * With main.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withMain(String value) {
		this.mainClass = value;
		return this;
	}

	/**
	 * With error path.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withErrorPath(String value) {
		this.errorHandler.withURL(value);
		if (isEclipse == false) {
			if (Thread.getDefaultUncaughtExceptionHandler() == null) {
				Thread.setDefaultUncaughtExceptionHandler(this);
				Thread.currentThread().setUncaughtExceptionHandler(this);
			}
			System.setOut(new StringPrintStream(this.errorHandler, false));
			System.setErr(new StringPrintStream(this.errorHandler, true));
		}
		return this;

	}

	/**
	 * With title.
	 *
	 * @param value the value
	 */
	public void withTitle(String value) {
		this.title = value;
	}

	/**
	 * With icon.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withIcon(String value) {
		return withIcon(value, null);
	}

	/**
	 * With icon.
	 *
	 * @param value the value
	 * @param relative the relative
	 * @return the simple controller
	 */
	public SimpleController withIcon(String value, Class<?> relative) {
		if (Os.isReflectionTest()) {
			return this;
		}
		if (relative != null) {
			URL resource = relative.getResource(value);
			if (resource != null) {
				value = resource.toString();
			}
		}
		this.icon = value;
		if (value == null) {
			return this;
		}
		if (this.stage != null) {
			showIcon();
		}
		if (this.trayIcon != null) {
			URL iconURL = null;
			try {
				if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
					iconURL = new URL(this.icon);
				} else {
					iconURL = new URL("file:" + this.icon);
				}
				Object toolKit = ReflectionLoader.call(ReflectionLoader.TOOLKIT, "getDefaultToolkit");
				Object image = ReflectionLoader.call(toolKit, "getImage", URL.class, iconURL);
				Object newImage = ReflectionLoader.call(image, "getScaledInstance", int.class, 16, int.class, 16,
						int.class, 4);
				ReflectionLoader.call(trayIcon, "setImage", ReflectionLoader.AWTIMAGE, newImage);
			} catch (MalformedURLException e) {
			}
		}
		return this;
	}

	private void showIcon() {
		Object image;
		if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
			image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, this.icon);
		} else {
			image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, "file:" + this.icon);
		}
		@SuppressWarnings("unchecked")
		List<Object> icons = (List<Object>) ReflectionLoader.call(stage, "getIcons");
		icons.add(image);
	}

	/**
	 * With tool tip.
	 *
	 * @param text the text
	 * @return the simple controller
	 */
	public SimpleController withToolTip(String text) {
		if (this.trayIcon != null) {
			ReflectionLoader.call(trayIcon, "setToolTip", String.class, text);
		}
		return this;
	}

	/**
	 * With size.
	 *
	 * @param width the width
	 * @param height the height
	 * @return the simple controller
	 */
	public SimpleController withSize(double width, double height) {
		ReflectionLoader.call(stage, "setWidth", double.class, width);
		ReflectionLoader.call(stage, "setHeight", double.class, height);
		return this;
	}

	/**
	 * With full screen.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withFullScreen(boolean value) {
		ReflectionLoader.call(stage, "setFullScreen", boolean.class, value);
		return this;
	}

	/**
	 * With always on top.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withAlwaysOnTop(boolean value) {
		ReflectionLoader.call(stage, "setAlwaysOnTop", boolean.class, value);
		return this;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		String caption = "";
		String temp = this.title;
		if (temp != null) {
			caption = temp + " ";
		}
		/* Add Replacements */
		return caption + getVersion() + " (" + System.getProperty("file.encoding") + " - "
				+ System.getProperty("sun.arch.data.model") + "-Bit)";
	}

	/**
	 * Adds the tray menu item.
	 *
	 * @param text the text
	 * @param listener the listener
	 * @return the object
	 */
	public Object addTrayMenuItem(String text, ObjectCondition listener) {
		Object item = ReflectionLoader.newInstance(ReflectionLoader.MENUITEM, String.class, text);

		GUIEvent event = new GUIEvent().withListener(this);
		this.withListener(listener);

		Object actionListener = ReflectionLoader.createProxy(event, ReflectionLoader.ACTIONLISTENER);

		ReflectionLoader.call(item, "addActionListener", ReflectionLoader.ACTIONLISTENER, actionListener);
		ReflectionLoader.call(getPopUp(), "add", ReflectionLoader.MENUITEM, item);
		return item;
	}

	/**
	 * Adds the tray seperator.
	 */
	public void addTraySeperator() {
		ReflectionLoader.call(getPopUp(), "addSeparator");
	}

	private Object getPopUp() {
		if (popupMenu == null) {
			popupMenu = ReflectionLoader.newInstance(ReflectionLoader.POPUPMENU);
		}
		return popupMenu;
	}

	/**
	 * Show tray icon.
	 *
	 * @param labels the labels
	 * @return the object
	 */
	public Object showTrayIcon(String... labels) {
		if (Os.checkSystemTray() == false) {
			return null;
		}
		if (this.icon != null && this.icon.length() > 0) {
			URL iconURL;
			try {
				if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
					iconURL = new URL(this.icon);
				} else {
					iconURL = new URL("file:" + this.icon);
				}
				Object toolKit = ReflectionLoader.call(ReflectionLoader.TOOLKIT, "getDefaultToolkit");
				Object image = ReflectionLoader.call(toolKit, "getImage", URL.class, iconURL);
				Object newImage = ReflectionLoader.call(image, "getScaledInstance", int.class, 16, int.class, 16,
						int.class, 4);

				this.close();
				this.trayIcon = ReflectionLoader.newInstance(ReflectionLoader.TRAYICON, ReflectionLoader.AWTIMAGE,
						newImage);
				Integer count = (Integer) ReflectionLoader.call(getPopUp(), "getItemCount");
				if (labels != null) {
					for (String label : labels) {
						if (label != null) {
							this.addTrayMenuItem(label, null);
						}
					}
				}
				if (count < 1) {
					addTrayMenuItem(CLOSE, this);
				}

				ReflectionLoader.call(trayIcon, "setPopupMenu", ReflectionLoader.POPUPMENU, popupMenu);
				Object systemTray = ReflectionLoader.call(ReflectionLoader.SYSTEMTRAY, "getSystemTray");
				ReflectionLoader.call(systemTray, "add", ReflectionLoader.TRAYICON, this.trayIcon);
			} catch (Exception e) {
			}

		}
		return this.trayIcon;
	}

	/**
	 * Close.
	 */
	public void close() {
		if (this.stage != null) {
			ReflectionLoader.call(this.stage, "close");
			this.stage = null;
		}
		if (this.trayIcon != null) {
			Object systemTray = ReflectionLoader.call(ReflectionLoader.SYSTEMTRAY, "getSystemTray");
			ReflectionLoader.call(systemTray, "remove", ReflectionLoader.TRAYICON, this.trayIcon);
			this.trayIcon = null;
		}
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public static String getVersion() {
		String result = SimpleController.class.getPackage().getImplementationVersion();
		if (result == null) {
			result = "0.42.DEBUG";
		}
		return result;
	}

	/**
	 * With listener.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withListener(ObjectCondition value) {
		this.listener.add(value);
		return this;
	}

	/**
	 * Save exception.
	 *
	 * @param e the e
	 */
	public void saveException(Throwable e) {
		this.errorHandler.saveException(e);
	}

	/**
	 * With listener.
	 *
	 * @param listener the listener
	 * @return the simple controller
	 */
	public SimpleController withListener(GUIEvent listener) {
		this.listener.add(listener);
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Throwable) {
			/* CallBack */
			saveException((Throwable) value);
			return true;
		}
		GUIEvent evt = GUIEvent.create(value);
		if (evt.isSubEventName("java.awt.event.ActionEvent")) {
			if (CLOSE.equals(evt.getId())) {
				this.close();
			}
		}
		for (Object listener : listener) {
			ObjectCondition match = evt.match(listener);
			if (match != null) {
				if (match != null && match != this) {
					match.update(evt);
				}
			}
		}
		return true;
	}

	/**
	 * Creates the.
	 *
	 * @return the simple controller
	 */
	public static SimpleController create() {
		return new SimpleController(null);
	}

	/**
	 * Creates the FX.
	 *
	 * @return the simple controller
	 */
	public static SimpleController createFX() {
		if (Os.isReflectionTest()) {
			return null;
		}
		SimpleController controller = new SimpleController(null);
		controller.runLater(controller, ReflectionLoader.PANE, ReflectionLoader.SCENE);
		return controller;
	}

	/**
	 * Start FX.
	 *
	 * @return true, if successful
	 */
	public static boolean startFX() {
		final Class<?> launcherClass = ReflectionLoader.getClass("com.sun.javafx.application.LauncherImpl");
		if (launcherClass == null) {
			return false;
		}
		if (Os.isJavaFX() == false) {
			return false;
		}
		ReflectionLoader.call(launcherClass, "startToolkit");
		return true;

	}

	/**
	 * Hide.
	 */
	public void hide() {
		if (this.stage != null) {
			ReflectionLoader.call(this.stage, "hide");
		}
	}

	/**
	 * Creates the.
	 *
	 * @param bridge the bridge
	 * @param listener the listener
	 * @param exitOnClose the exit on close
	 * @param wait the wait
	 * @return the simple controller
	 */
	public static SimpleController create(JavaBridge bridge, final ObjectCondition listener, boolean exitOnClose,
			boolean wait) {
		final SimpleController controller = new SimpleController(null);
		final Class<?> launcherClass = ReflectionLoader.getClass("com.sun.javafx.application.LauncherImpl");
		controller.withBridge(bridge);
		if (launcherClass == null) {
			/* NO JAVAFX */
			NodeProxyTCP server = NodeProxyTCP.createServer(8080);
			server.withListener(listener);
			if (server.start()) {
			    if(SimpleController.logger != null) {
			        SimpleController.logger.info("LISTEN ON: " + server.getKey());
			    }
				if (ReflectionLoader.DESKTOP != null) {
					Object desktop = ReflectionLoader.call(ReflectionLoader.DESKTOP, "getDesktop");
					if (desktop != null) {
						try {
							ReflectionLoader.call(desktop, "browse", URI.class, new URI("http://" + server.getKey()));
						} catch (Exception e) {
						}
					}
				}
			}
			return controller;
		}
		ReflectionLoader.call(launcherClass, "startToolkit");
		if (exitOnClose == false) {
			ReflectionLoader.call(ReflectionLoader.PLATFORM, "setImplicitExit", boolean.class, false);
		}

		controller.withListener(listener);

		if (wait) {
			JavaAdapter.executeAndWait(controller);
		} else {
			JavaAdapter.execute(controller);
		}
		return controller;
	}

	/**
	 * With FXML.
	 *
	 * @param fxmlFile the fxml file
	 * @param fromClass the from class
	 * @return the simple controller
	 */
	public SimpleController withFXML(String fxmlFile, Class<?>... fromClass) {
		Class<?> path = null;
		if (fromClass != null && fromClass.length > 0) {
			path = fromClass[0];
		}
		if (path == null) {
			path = SimpleController.class;
		}
		if (Os.isReflectionTest()) {
			return this;
		}
		create(path.getResource(fxmlFile), null);

		/* NOW MAPPING */
		if (mapping != null) {
			for (int i = 0; i < mapping.size(); i++) {
				Object key = mapping.getKeyByIndex(i);
				SendableEntityCreator creator = mapping.getValueByIndex(i);
				Object controller = null;
				if (controllers == null) {
					controllers = new SimpleList<SendableEntityCreator>();
				}
				if (key instanceof String) {
					Object pane = ReflectionLoader.call(this.rootScene, "lookup", "#" + key);
					if (pane != null) {
						if (controllers.contains(creator)) {
							controller = creator.getSendableInstance(false);
						} else {
							controller = creator;
						}
						creator.setValue(controller, ModelListenerProperty.PROPERTY_VIEW, pane,
								SendableEntityCreator.NEW);
					}
				} else if (key instanceof SimpleEvent) {
					SimpleEvent event = (SimpleEvent) key;
					Object pane = ReflectionLoader.call(this.rootScene, "lookup", "#" + event.getPropertyName());
					if (pane != null) {
						if (controllers.contains(creator)) {
							controller = creator.getSendableInstance(false);
						} else {
							controller = creator;
						}
						if (map != null) {
							SendableEntityCreator creatorClass = map.getCreatorClass(event.getSource());
							creator.setValue(controller, ModelListenerProperty.PROPERTY_CREATOR, creatorClass,
									SendableEntityCreator.NEW);
						}
						creator.setValue(controller, ModelListenerProperty.PROPERTY_PROPERTY, event.getNewValue(),
								SendableEntityCreator.NEW);
						creator.setValue(controller, ModelListenerProperty.PROPERTY_MODEL, event.getSource(),
								SendableEntityCreator.NEW);
						/* NOW SET BIDIRECTIONAL */
						creator.setValue(controller, ModelListenerProperty.PROPERTY_VIEW, pane,
								SendableEntityCreator.NEW);
					}
				}
				controllers.add(controller);
			}
		}
		return this;
	}

	/**
	 * Creates the.
	 *
	 * @param location the location
	 * @param resources the resources
	 * @return the object
	 */
	public Object create(URL location, ResourceBundle resources) {
		Object fxmlLoader;
		if (location == null) {
			System.err.println("FXML not found");
			return null;
		}
		if (resources != null) {
			Object builder = ReflectionLoader.newInstance("javafx.fxml.JavaFXBuilderFactory");
			Class<?> builderClass = ReflectionLoader.getClass("javafx.util.BuilderFactory");
			fxmlLoader = ReflectionLoader.newInstance("javafx.fxml.FXMLLoader", java.net.URL.class, location,
					java.util.ResourceBundle.class, resources, builderClass, builder);
		} else {
			fxmlLoader = ReflectionLoader.newInstance("javafx.fxml.FXMLLoader", java.net.URL.class, location);
		}
		if (ReflectionLoader.logger == null) {
			ReflectionLoader.logger = new PrintStream(System.err);
		}
		try {
			this.rootScene = ReflectionLoader.call(fxmlLoader, "load", InputStream.class, location.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.withController(ReflectionLoader.call(fxmlLoader, "getController"));
		return rootScene;
	}

	private SimpleController withController(Object value) {
		this.controller = value;
		return this;
	}

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	public Object getController() {
		return controller;
	}

	/**
	 * Run later.
	 *
	 * @param runnable the runnable
	 * @param values the values
	 */
	public void runLater(Runnable runnable, Object... values) {
		runParams = values;
		runAction = CREATING;
		JavaAdapter.executeAndWait(runnable);
	}

	/**
	 * With bridge.
	 *
	 * @param value the value
	 * @return the simple controller
	 */
	public SimpleController withBridge(JavaBridge value) {
		this.bridge = value;
		return this;
	}

	/**
	 * Gets the bridge.
	 *
	 * @return the bridge
	 */
	public JavaBridge getBridge() {
		return bridge;
	}

	/**
	 * Execute process.
	 *
	 * @param values the values
	 * @return the character buffer
	 */
	public static CharacterBuffer executeProcess(String... values) {
		return executeProcess(new CharacterBuffer(), values);
	}

	/**
	 * Execute process.
	 *
	 * @param command the command
	 * @param values the values
	 * @return the character buffer
	 */
	public static CharacterBuffer executeProcess(CharacterBuffer command, String... values) {
		CharacterBuffer result = new CharacterBuffer();
		if (values == null) {
			return result;
		}
		try {
			String line;
			if (command == null) {
				command = new CharacterBuffer();
			}

			ArrayList<String> commands = null;
			boolean found = false;
			if (Os.isWindows()) {
				command.with("cmd.exe /c ");
			} else {
				if (values.length > 0 && values[0] != null) {
					if ((values[0].startsWith("/") || values[0].startsWith("\\")) == false) {
						values[0] = "./" + values[0];
					}
					/* Check if File Exist */
					found = new File(values[0]).exists();
				}
				if (found == false) {
					commands = new ArrayList<String>();
					commands.add("/bin/sh");
					commands.add("-c");
				}
			}
			Process p;
			/* So now add executeCommand to String */
			if (found == false) {
				command.with('"');
				if (values.length > 0 && values[0] != null) {
					if (commands != null) {
						if ((values[0].startsWith("/") || values[0].startsWith("\\"))) {
							command.with(values[0]);
						} else {
							command.with("./" + values[0]);
						}
					} else {
						command.with(values[0]);
					}
				}
				int i = 1;
				for (; i < values.length; i++) {
					if (values[i] != null) {
						command.with(" " + values[i]);
					}
				}
				command.with('"');

				if (commands != null) {
					/* Its Mac */
					commands.add(command.toString());
					p = Runtime.getRuntime().exec(commands.toArray(new String[commands.size()]));
					command.clear();
					command.with(commands.get(0));
					for (i = 1; i < commands.size(); i++) {
						command.with(" " + commands.get(i));
					}
				} else {
					p = Runtime.getRuntime().exec(command.toString());
					command.with(values[0]);
					for (i = 1; i < values.length; i++) {
						command.with(" " + values[i]);
					}
				}
			} else {
				p = Runtime.getRuntime().exec(values);
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = input.readLine()) != null) {
				result.withLine(line);
			}
			CharacterBuffer errorString = new CharacterBuffer();
			while ((line = error.readLine()) != null) {
				errorString.withLine(line);
			}
			if (errorString.size() > 0) {
				result.withLine("ERROR: ");
				result.with(errorString.toString());
			}
			input.close();
			error.close();
		} catch (Exception err) {
			StringOutputStream stream = new StringOutputStream();
			err.printStackTrace(new PrintStream(stream));
			result.with(stream.toString());
		}
		return result;
	}

	/**
	 * With package name.
	 *
	 * @param packageName the package name
	 * @param excludes the excludes
	 * @return the simple controller
	 */
	public SimpleController withPackageName(String packageName, String... excludes) {
		if (compilePath == null && packageName != null && packageName.length() > 0) {
			if ((packageName.endsWith("/") || packageName.endsWith("\\")) == false) {
				packageName += "/";
			}
			compilePath = new SimpleList<String>();
			String root = new File(".").getAbsolutePath();

			File file = new File(packageName);
			visitPath(file, root, 0, 10, excludes);
		}
		return this;
	}

	/**
	 * Visit path.
	 *
	 * @param file the file
	 * @param root the root
	 * @param deep the deep
	 * @param maxDeep the max deep
	 * @param excludes the excludes
	 */
	public void visitPath(File file, String root, int deep, int maxDeep, String... excludes) {
		if (file == null || root == null || deep > maxDeep) {
			return;
		}
		boolean add = false;
		File[] listFiles = file.listFiles();
		if (listFiles == null) {
			return;
		}
		for (File child : listFiles) {
			if (child.isDirectory()) {
				visitPath(child, root, deep + 1, maxDeep, excludes);
				continue;
			}
			if (child.getName().toLowerCase().endsWith(".java")) {
				add = true;
			}
		}
		if (add) {
			String path = file.getPath();
			if (path.startsWith(root)) {
				path = path.substring(root.length() + 1);
			}
			if (excludes != null) {
				for (String item : excludes) {
					if (path.startsWith(item)) {
						add = false;
						break;
					}
				}
			}
			if (add) {
				compilePath.add(path);
			}
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
		this.errorHandler.saveException(e);
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		boolean showBridge = true;
		if (this.runParams != null && this.runParams.length > 0) {
			if (CREATING.equalsIgnoreCase(runAction)) {
				Class<?> object = (Class<?>) this.runParams[0];
				Object pane = null;
				if (ReflectionLoader.PANE.isAssignableFrom(object)) {
					pane = ReflectionLoader.newInstance(object);
					object = (Class<?>) this.runParams[1];
				}
				if (ReflectionLoader.SCENE.isAssignableFrom(object)) {
					this.rootScene = ReflectionLoader.newInstance(object, ReflectionLoader.PARENT, pane);
				}
				if (ReflectionLoader.STAGE.isAssignableFrom(object)) {
					this.stage = ReflectionLoader.newInstance(object);
					return;
				}
				return;
			} else if (SHOWING.equalsIgnoreCase(runAction)) {
				this.showing(this.runParams[0], (Boolean) this.runParams[1], (Boolean) this.runParams[2]);
			}
		}
		Object stage = ReflectionLoader.newInstance(ReflectionLoader.STAGE);
		this.withStage(stage);
		this.withIcon(IdMap.class.getResource("np.png").toString());
		if (showBridge) {
			this.show(this.getBridge());
		}
	}

	/**
	 * With map.
	 *
	 * @param key the key
	 * @param modelMapping the model mapping
	 * @return the model listener property
	 */
	public ModelListenerProperty withMap(String key, Object... modelMapping) {
		ModelListenerProperty propertyPrototype = new ModelListenerProperty();
		withMap(propertyPrototype, key, modelMapping);
		return propertyPrototype;
	}

	/**
	 * With map.
	 *
	 * @param controller   Creator for Controller
	 * @param key          Key of GUI
	 * @param modelMapping Mapping for Mapping GUI
	 * @return ThisComponent
	 */
	public SimpleController withMap(SendableEntityCreator controller, String key, Object... modelMapping) {
		if (controller == null || key == null) {
			return this;
		}
		if (mapping == null) {
			mapping = new SimpleKeyValueList<Object, SendableEntityCreator>();
		}
		Object mapKey = key;
		if (modelMapping != null && modelMapping.length > 0) {
			if (modelMapping[0] != null) {
				String property = null;
				if (modelMapping.length > 1 && modelMapping[1] instanceof String) {
					property = (String) modelMapping[1];
				}
				SimpleEvent event = new SimpleEvent(modelMapping[0], key, null, property);
				mapKey = event;
			}
		}
		this.mapping.put(mapKey, controller);
		return this;
	}

	/**
	 * Gets the controllers.
	 *
	 * @return the controllers
	 */
	public SimpleList<SendableEntityCreator> getControllers() {
		return controllers;
	}

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the simple controller
	 */
	public SimpleController withMap(IdMap map) {
		this.map = map;
		return this;
	}
}
