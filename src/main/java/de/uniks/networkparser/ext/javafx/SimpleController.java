package de.uniks.networkparser.ext.javafx;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class SimpleController implements ObjectCondition{
	public static final String SEPARATOR="------";
	public static final String CLOSE="close";
	private Object application;
	private Object stage;
	private boolean firstShow=true;
	protected String icon;
	private String encodingCode=BaseItem.ENCODING;
	private String debugPort;
	private String title;
	private ErrorHandler errorHandler = new ErrorHandler();
	protected Object popupMenu;
	protected Object trayIcon;
	private SimpleList<Object> listener = new SimpleList<Object>();
	
	public SimpleController(Object primitiveStage) {
		this(primitiveStage, true);
	}
	
	public SimpleController(Object primitiveStage, boolean init) {
		withStage(primitiveStage);
		application = getApplication();
		if(init) {
			this.init();
		}
	}
	
	public SimpleController withStage(Object stage) {
		this.stage = stage;
		GUIEvent proxyHandler=new GUIEvent();
		proxyHandler.withListener(this);
		
		Object proxy = ReflectionLoader.createProxy(proxyHandler, ReflectionLoader.EVENTHANDLER);
		
		ReflectionLoader.call("setOnCloseRequest", stage, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnShowing", stage, ReflectionLoader.EVENTHANDLER, proxy);
		return this;
	}
	
	private Object getApplication() {
		Field params;
		try {
			params = ReflectionLoader.PARAMETER.getDeclaredField("params");
			params.setAccessible(true);
			Object value = params.get(null);
			if(value instanceof Map<?,?>) {
				Map<?,?> map = (Map<?, ?>) value;
				Object[] keys = map.keySet().toArray();
				if(keys.length>0) {
					return keys[keys.length - 1];
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static SimpleController create(Object primaryStage) {
		SimpleController controller = new SimpleController(primaryStage);
		return controller;
	}
	protected void init() {
		String outputRedirect = null;
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
		for (int i = 0; i < params.size(); i++) {
			String key = params.get(i);
			String value = params.getValueByIndex(i);
			if (key.equalsIgnoreCase("debug")) {
				if (value != null) {
					debugPort = value;
				} else {
					debugPort = "4223";
				}
			} else if (key.equalsIgnoreCase("output")) {
				if (value == null) {
					outputRedirect = "INHERIT";
				} else {
					outputRedirect = value;
				}
			} else if (key.equalsIgnoreCase("-?")) {
				System.out.println(getCommandHelp());
				Runtime.getRuntime().exit(1);
			}
		}
		if (debugPort != null) {
			ArrayList<String> items = new ArrayList<String>();
			if (Os.isMac()) {
				items.add(System.getProperty("java.home").replace("\\", "/") + "/bin/java");
			} else {
				items.add("\"" + System.getProperty("java.home").replace("\\", "/") + "/bin/java\"");
			}
	
			items.add("-Xdebug");
			items.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
			items.add("-jar");
			String fileName = new Os().getFilename().toLowerCase();
			items.add(fileName);
	
			ProcessBuilder processBuilder = new ProcessBuilder(items);
			if (outputRedirect != null) {
				if (outputRedirect.equalsIgnoreCase("inherit")) {
					processBuilder.redirectErrorStream(true);
					ReflectionLoader.call("redirectOutput", processBuilder, ReflectionLoader.PROCESSBUILDERREDIRECT, ReflectionLoader.getField("INHERIT", ReflectionLoader.PROCESSBUILDERREDIRECT));
				} else {
					int pos = outputRedirect.lastIndexOf(".");
					if (pos > 0) {
						ReflectionLoader.call("redirectError", processBuilder, File.class, new File(outputRedirect.substring(0, pos) + "_error" + outputRedirect.substring(pos)));
						ReflectionLoader.call("redirectOutput", processBuilder, File.class, new File(outputRedirect.substring(0, pos) + "_stdout" + outputRedirect.substring(pos)));
								
					} else {
						ReflectionLoader.call("redirectError", processBuilder, File.class, new File(outputRedirect + "_error.txt"));
						ReflectionLoader.call("redirectOutput", processBuilder, File.class, new File(outputRedirect + "_stdout.txt"));
					}
				}
			}
			try {
				processBuilder.start();
				Runtime.getRuntime().exit(1);
			} catch (IOException e) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	public SimpleKeyValueList<String, String> getParameterMap() {
		SimpleKeyValueList<String, String> map = new SimpleKeyValueList<String, String>();
		List<String> raw = (List<String>) ReflectionLoader.callChain(application, "getParameters", "getRaw");
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
		return map;
	}

	public void show(Object root, boolean newStage) {
		Object oldStage = null;
		if(newStage) {
			oldStage = this.stage;
			withStage(ReflectionLoader.newInstance(ReflectionLoader.STAGE));
			refreshIcon();
		}
		this.firstShow = false;

		Object scene;
		if(ReflectionLoader.SCENE == null) {
			return;
		}
		if(ReflectionLoader.SCENE.isAssignableFrom(root.getClass())) {
			scene = root;
		} else {
			scene =  ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, root);
		}
		ReflectionLoader.call("setScene", stage, ReflectionLoader.SCENE, scene);
		
		if(root instanceof ObjectCondition) {
			this.withListener((ObjectCondition)root);
		}
		GUIEvent event = new GUIEvent();
		event.withListener(this);
		Object proxy = ReflectionLoader.createProxy(event, ReflectionLoader.EVENTHANDLER);
		ReflectionLoader.call("setOnKeyPressed", scene, ReflectionLoader.EVENTHANDLER, proxy);
		showing();
		if(oldStage != null) {
			ReflectionLoader.call("close", oldStage);
		}
	}
	
	public void show(Object root) {
		show(root, firstShow == false);
	}
	
	public Object getCurrentScene() {
		return ReflectionLoader.call("getScene", stage);
	}
	
	protected void showing() {
		if(this.stage != null) {
			init();
			ReflectionLoader.call("setTitle", this.stage, getTitle());
			if (Os.isEclipse()) {
				ReflectionLoader.call("show", this.stage);
			} else {
				try {
					ReflectionLoader.call("show", this.stage);
				} catch (Exception e) {
					errorHandler.saveException(e, this.stage);
				}
			}
		}
	}
	
	public String getEncodingCode() {
		return encodingCode;
	}

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
	public SimpleController withErrorPath(String value) {
		this.errorHandler.withPath(value);
		if(Thread.getDefaultUncaughtExceptionHandler()==null){
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					SimpleController.this.errorHandler.saveException(e);
				}
			});
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					SimpleController.this.errorHandler.saveException(e);
				}
			});
		}
		return this;
	}

	public void withTitle(String value) {
		this.title = value;
	}

	
	public SimpleController withIcon(String value) {
		return withIcon(value, null);
	}
	public SimpleController withIcon(String value, Class<?> relative) {
		if(relative != null) {
			URL resource = relative.getResource(value);
			if(resource != null) {
				value = resource.toString();
			}
		}
		this.icon = value;
		if( value == null) {
			return this;
		}
		if (this.stage != null) {
			refreshIcon();
		}
		if(this.trayIcon != null) {
			URL iconURL = null;
			try {
				if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
					iconURL = new URL(this.icon);
				}else {
					iconURL = new URL("file:" + this.icon);
				}
				Object toolKit = ReflectionLoader.call("getDefaultToolkit", ReflectionLoader.TOOLKIT);
				Object image = ReflectionLoader.call("getImage", toolKit, URL.class, iconURL);
				Object newImage = ReflectionLoader.call("getScaledInstance", image, int.class, 16, int.class, 16, int.class, 4);
				ReflectionLoader.call("setImage", trayIcon, ReflectionLoader.AWTIMAGE, newImage);
			} catch (MalformedURLException e) {
			}
		}
		return this;
	}
	
	private void refreshIcon() {
		Object image;
		if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
			image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, this.icon);
		} else {
			image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, "file:" + this.icon);
		}
		@SuppressWarnings("unchecked")
		List<Object> icons = (List<Object>) ReflectionLoader.call("getIcons", stage);
		icons.add(image);
	}
	
	public SimpleController withToolTip(String text) {
		if(this.trayIcon != null) {
			ReflectionLoader.call("setToolTip", trayIcon, String.class, text);
		}
		return this;
	}
	
	public SimpleController withSize(double width, double height) {
		ReflectionLoader.call("setWidth", stage, double.class, width);
		ReflectionLoader.call("setHeight", stage, double.class, height);
		return this;
	}
	
	public SimpleController withFullScreen(boolean value) {
		ReflectionLoader.call("setFullScreen", stage, boolean.class, value);
		return this;
	}
	public SimpleController withAlwaysOnTop(boolean value) {
		ReflectionLoader.call("setAlwaysOnTop", stage, boolean.class, value);
		return this;
	}

	public String getTitle() {
		String caption = "";
		String temp = this.title;
		if (temp != null) {
			caption = temp + " ";
		}
		// Add Replacements
		return caption + getVersion() + " (" + System.getProperty("file.encoding") + " - "
				+ System.getProperty("sun.arch.data.model") + "-Bit)";
	}
	
	public Object addTrayMenuItem(String text, ObjectCondition listener) {
		Object item  = ReflectionLoader.newInstance(ReflectionLoader.MENUITEM, String.class, text);

		GUIEvent event = new GUIEvent().withListener(this);
		this.withListener(listener);
		
		
		Object actionListener = ReflectionLoader.createProxy(event, ReflectionLoader.ACTIONLISTENER);
		
		ReflectionLoader.call("addActionListener", item, ReflectionLoader.ACTIONLISTENER, actionListener);
		ReflectionLoader.call("add", getPopUp(), ReflectionLoader.MENUITEM, item);
		return item;
	}
	
	public void addTraySeperator() {
		ReflectionLoader.call("addSeparator", getPopUp());
	}
	
	private Object getPopUp() {
		if(popupMenu == null) {
			popupMenu = ReflectionLoader.newInstance(ReflectionLoader.POPUPMENU);
		}
		return popupMenu;
	}
	
	public Object showTrayIcon(String... labels) {
		if(Os.checkSystemTray() == false) {
			return null;
		}
		if(this.icon != null) {
			URL iconURL;
			try {
				if (this.icon.startsWith("file") || this.icon.startsWith("jar")) {
					iconURL = new URL(this.icon);
				}else {
					iconURL = new URL("file:" + this.icon);
				}
				Object toolKit = ReflectionLoader.call("getDefaultToolkit", ReflectionLoader.TOOLKIT);
				Object image = ReflectionLoader.call("getImage", toolKit, URL.class, iconURL);
				Object newImage = ReflectionLoader.call("getScaledInstance", image, int.class, 16, int.class, 16, int.class, 4);
						
				this.close();
				this.trayIcon = ReflectionLoader.newInstance(ReflectionLoader.TRAYICON, ReflectionLoader.AWTIMAGE, newImage);
				Integer count = (Integer) ReflectionLoader.call("getItemCount", getPopUp());
				if(labels != null) {
					for(String label : labels) {
						if(label != null) {
							this.addTrayMenuItem(label, null);
						}
					}
				}
				if(count < 1) {
					addTrayMenuItem(CLOSE, this);
				}
				
				ReflectionLoader.call("setPopupMenu", trayIcon, ReflectionLoader.POPUPMENU, popupMenu);
				Object systemTray = ReflectionLoader.call("getSystemTray", ReflectionLoader.SYSTEMTRAY);
				ReflectionLoader.call("add", systemTray, ReflectionLoader.TRAYICON,this.trayIcon);
			}catch (Exception e) {
			}
			
		}
		return this.trayIcon;
	}
	
	public void close() {
		if(this.stage != null) {
			ReflectionLoader.call("close", this.stage);
			this.stage = null;
		}
		if(this.trayIcon != null) {
			Object systemTray = ReflectionLoader.call("getSystemTray", ReflectionLoader.SYSTEMTRAY);
			ReflectionLoader.call("remove", systemTray, ReflectionLoader.TRAYICON, this.trayIcon);
			this.trayIcon = null;
		}
	}
	
	public String getVersion() {
		String result = SimpleController.class.getPackage().getImplementationVersion();
		if (result == null) {
			result = "0.42.DEBUG";
		}
		return result;
	}
	
	public SimpleController withListener(ObjectCondition value) {
		this.listener.add(value);
		return this;
	}
	
	public void saveException(Throwable e) {
		this.errorHandler.saveException(e);
	}

	public SimpleController withListener(GUIEvent listener) {
		this.listener.add(listener);
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value == null) {
			return false;
		}
		GUIEvent evt = GUIEvent.create(value);
		if(evt.isSubEventName("java.awt.event.ActionEvent")) {
			if(CLOSE.equals(evt.getId())) {
				this.close();
			}
		}
		for(Object listener : listener) {
			ObjectCondition match = evt.match(listener);
			if(match != null) {
				if(match != null) {
					match.update(evt);
				}
			}
		}
		return true;
	}
	
}
