package de.uniks.networkparser.ext.javafx;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.error.ErrorHandler;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class SimpleController {
	public static final String SEPARATOR="------";
	public static final String CLOSE="close";
	private Object application;
	private Object stage;
	protected String icon;
	private String encodingCode="UTF-8";
	private String debugPort;
	private String title;
	private ErrorHandler errorHandler = new ErrorHandler();
	protected PopupMenu popupMenu;
	protected TrayIcon trayIcon;
	private JavaFXEvent eventHandler = new JavaFXEvent();
	
	public SimpleController(Object primitiveStage) {
		this.stage = primitiveStage;
		
		Object proxy = ReflectionLoader.createProxy(eventHandler, ReflectionLoader.EVENTHANDLER);
		
		ReflectionLoader.call("setOnCloseRequest", stage, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnShowing", stage, ReflectionLoader.EVENTHANDLER, proxy);
		application = getApplication();
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
					processBuilder.redirectOutput(Redirect.INHERIT);
				} else {
					int pos = outputRedirect.lastIndexOf(".");
					if (pos > 0) {
						processBuilder.redirectError(
								new File(outputRedirect.substring(0, pos) + "_error" + outputRedirect.substring(pos)));
						processBuilder.redirectOutput(
								new File(outputRedirect.substring(0, pos) + "_stdout" + outputRedirect.substring(pos)));
					} else {
						processBuilder.redirectError(new File(outputRedirect + "_error.txt"));
						processBuilder.redirectOutput(new File(outputRedirect + "_stdout.txt"));
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

	public void show(Object root) throws Exception {
		Object scene;
		if(ReflectionLoader.SCENE.isAssignableFrom(root.getClass())) {
			scene = root;
		} else {
			scene =  ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, root);
		}
		ReflectionLoader.call("setScene", stage, ReflectionLoader.PARENT, scene);
		showing();
	}
	
	protected void showing() throws Exception {
		if(this.stage != null) {
			init();
			ReflectionLoader.call("setTitle", this.stage, getTitle());
			try {
				ReflectionLoader.call("show", this.stage, getTitle());
			} catch (Exception e) {
				errorHandler.saveException(e, this.stage);
				if (Os.isEclipse()) {
					throw e;
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
		if (this.stage != null && value != null) {
			Object image;
			if (value.startsWith("file") || value.startsWith("jar")) {
				image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, value);
			} else {
				image = ReflectionLoader.newInstance(ReflectionLoader.IMAGE, "file:" + value);
			}
			@SuppressWarnings("unchecked")
			List<Object> icons = (List<Object>) ReflectionLoader.call("getIcons", stage);
			icons.add(image);
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
	
	public MenuItem addTrayMenuItem(String text, ObjectCondition listener) {
		MenuItem item = new MenuItem(text);
		item.addActionListener(new AWTListener().withListener(listener));
		getPopUp().add(item);
		return item;
	}

	public void addTraySeperator() {
		getPopUp().addSeparator();
	}
	
	private PopupMenu getPopUp() {
		if(popupMenu == null) {
			popupMenu = new PopupMenu();
		}
		return popupMenu;
	}
	
	public TrayIcon showTrayIcon(String... labels) {
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
				Image img = Toolkit.getDefaultToolkit().getImage(iconURL);
				Image newImage = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
				this.trayIcon = new TrayIcon(newImage);
				if(popupMenu.getItemCount()<1) {
					addTrayMenuItem(CLOSE, this.eventHandler.getListener());
				}
				trayIcon.setPopupMenu(popupMenu);
				SystemTray.getSystemTray().add(trayIcon);
			}catch (Exception e) {
			}
			
		}
		return this.trayIcon;
	}
	
	protected String getVersion() {
		String result = SimpleController.class.getPackage().getImplementationVersion();
		if (result == null) {
			result = "0.42.DEBUG";
		}
		return result;
	}
	
	public SimpleController withListener(ObjectCondition value) {
		this.eventHandler.withListener(value);
		return this;
	}
	
	public void saveException(Throwable e) {
		this.errorHandler.saveException(e);
	}
}
