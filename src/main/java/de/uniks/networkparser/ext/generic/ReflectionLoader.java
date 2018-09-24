package de.uniks.networkparser.ext.generic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class ReflectionLoader {
	public static PrintStream logger = null;
	private static int errorCount;
	public static final Class<?> CHANGELISTENER;
	public static final Class<?> NODE;
	public static final Class<?> OBSERVABLEVALUE;
	public static final Class<?> INVALIDATIONLISTENER;
	public static final Class<?> BINDINGS;

	public static final Class<?> PROPERTY;
	public static final Class<?> SIMPLEOBJECTPROPERTY;
	public static final Class<?> STRINGPROPERTY;
	public static final Class<?> BOOLEANPROPERTY;
	public static final Class<?> INTEGERPROPERTY;
	public static final Class<?> DOUBLEPROPERTY;

	public static final Class<?> DESKTOP;
	public static final Class<?> COLOR;
	public static final Class<?> COLORPICKER;
	public static final Class<?> PAINT;
	public static final Class<?> TEXTFIELD;
	public static final Class<?> COMBOBOX;
	public static final Class<?> LABEL;
	public static final Class<?> CHECKBOX;
	public static final Class<?> RADIOBUTTON;
	public static final Class<?> SYSTEMTRAY;
	public static final Class<?> WEBVIEW;
	public static final Class<?> JSOBJECT;
	public static final Class<?> PSEUDOCLASS;
	public static final Class<?> TOOLBAR;
	public static final Class<?> BUTTON;
	public static final Class<?> EVENTHANDLER;
	public static final Class<?> STACKPANE;
	public static final Class<?> REGION;
	public static final Class<?> HBOX;
	public static final Class<?> VBOX;
	public static final Class<?> PRIORITY;
	public static final Class<?> PARENT;
	public static final Class<?> PANE;
	public static final Class<?> SCENE;
	public static final Class<?> PLATFORM;
	public static final Class<?> PLATFORMIMPL;
	public static final Class<?> STAGE;
	public static final Class<?> SCREEN;
	public static final Class<?> MODALITY;
	public static final Class<?> STAGESTYLE;
	public static final Class<?> POS;
	public static final Class<?> IMAGEVIEW;
	public static final Class<?> IMAGE;
	public static final Class<?> BORDERPANE;
	public static final Class<?> PARAMETER;
	public static final Class<?> TRANSFERMODE;
	public static final Class<?> FILECHOOSERFX;

	public static final Class<?> GIT;
	public static final Class<?> FILEREPOSITORYBUILDER;
	public static final Class<?> REVWALK;
	public static final Class<?> CANONICALTREEPARSER;
	public static final Class<?> FILEMODE;
	public static final Class<?> REPOSITORY;
	public static final Class<?> ANYOBJECTID;

	public static final Class<?> RECTANGLE;
	public static final Class<?> ROBOT;
	public static final Class<?> TOOLKIT;
	public static final Class<?> TOOLKITFX;
	public static final Class<?> DIMENSION;
	public static final Class<?> RENDEREDIMAGE;
	public static final Class<?> IMAGEIO;
	public static final Class<?> ACTIONLISTENER;
	public static final Class<?> MENUITEM;
	public static final Class<?> POPUPMENU;
	public static final Class<?> TRAYICON;
	public static final Class<?> AWTIMAGE;
	public static final Class<?> PROCESSBUILDERREDIRECT;
	public static final Class<?> MANAGEMENTFACTORY;
	public static final Class<?> JFILECHOOSER;
	public static final Class<?> JFRAME;
//	public static final Class<?> DIFFENTRY;
//	public static final Class<?> OBJECTID;
//	public static final Class<?> OBJECTREADER;
//	public static final Class<?> REF;
//	public static final Class<?> REVCOMMIT;

//	public static final Class<?> JUNIT = getClass("org.junit.Assert");

	//EMF
	public static final Class<?> EATTRIBUTE;
	public static final Class<?> ECLASS;
	public static final Class<?> ECLASSIFIER;
	public static final Class<?> EPACKAGE;
	public static final Class<?> EREFERENCE;
	public static final Class<?> EOBJECT;

	static {
		MANAGEMENTFACTORY = getClass("java.lang.management.ManagementFactory");
	}

	static {
		//JAVAFX
		CHANGELISTENER = getClass("javafx.beans.value.ChangeListener");

		if(CHANGELISTENER != null) {
			NODE = getClass("javafx.scene.Node");
			OBSERVABLEVALUE = getClass("javafx.beans.value.ObservableValue");
			INVALIDATIONLISTENER = getClass("javafx.beans.InvalidationListener");
			BINDINGS = getClass("javafx.beans.binding.Bindings");

			PROPERTY = getClass("javafx.beans.property.Property");
			SIMPLEOBJECTPROPERTY = getClass("javafx.beans.property.SimpleObjectProperty");
			STRINGPROPERTY = getClass("javafx.beans.property.StringProperty");
			BOOLEANPROPERTY = getClass("javafx.beans.property.BooleanProperty");
			INTEGERPROPERTY = getClass("javafx.beans.property.IntegerProperty");
			DOUBLEPROPERTY = getClass("javafx.beans.property.DoubleProperty");

			COLOR = getClass("javafx.scene.paint.Color");
			PAINT = getClass("javafx.scene.paint.Paint");
			COLORPICKER = getClass("javafx.scene.control.ColorPicker");
			TEXTFIELD = getClass("javafx.scene.control.TextField");
			COMBOBOX = getClass("javafx.scene.control.ComboBox");
			LABEL = getClass("javafx.scene.control.Label");
			CHECKBOX = getClass("javafx.scene.control.CheckBox");
			RADIOBUTTON = getClass("javafx.scene.control.RadioButton");
			WEBVIEW = getClass("javafx.scene.web.WebView");
			JSOBJECT = getClass("netscape.javascript.JSObject");
			PSEUDOCLASS = getClass("javafx.css.PseudoClass");
			TOOLBAR = getClass("javafx.scene.control.ToolBar");
			BUTTON = getClass("javafx.scene.control.Button");
			EVENTHANDLER = getClass("javafx.event.EventHandler");
			STACKPANE  = getClass("javafx.scene.layout.StackPane");
			REGION = getClass("javafx.scene.layout.Region");
			HBOX = getClass("javafx.scene.layout.HBox");
			VBOX = getClass("javafx.scene.layout.VBox");
			POS = getClass("javafx.geometry.Pos");
			PRIORITY = getClass("javafx.scene.layout.Priority");
			PARENT = getClass("javafx.scene.Parent");
			PANE = getClass("javafx.scene.layout.Pane");
			PLATFORM = getClass("javafx.application.Platform");
			STAGE = getClass("javafx.stage.Stage");
			STAGESTYLE = getClass("javafx.stage.StageStyle");
			MODALITY =getClass("javafx.stage.Modality");
			SCENE = getClass("javafx.scene.Scene");
			SCREEN = getClass("javafx.stage.Screen");
			IMAGEVIEW = getClass("javafx.scene.image.ImageView");
			IMAGE = getClass("javafx.scene.image.Image");
			BORDERPANE = getClass("javafx.scene.layout.BorderPane");
			PARAMETER = getClass("com.sun.javafx.application.ParametersImpl");
			TRANSFERMODE = getClass("javafx.scene.input.TransferMode");
			TOOLKITFX = getClass("com.sun.javafx.tk.Toolkit");
			FILECHOOSERFX = getClass("javafx.stage.FileChooser");
			PLATFORMIMPL = getClass("com.sun.javafx.application.PlatformImpl");
		} else {
			NODE = null;
			OBSERVABLEVALUE = null;
			INVALIDATIONLISTENER = null;
			BINDINGS = null;

			PROPERTY = null;
			SIMPLEOBJECTPROPERTY = null;
			STRINGPROPERTY = null;
			BOOLEANPROPERTY = null;
			INTEGERPROPERTY = null;
			DOUBLEPROPERTY = null;

			COLOR = null;
			COLORPICKER = null;
			PAINT = null;
			TEXTFIELD = null;
			COMBOBOX = null;
			LABEL = null;
			CHECKBOX = null;
			RADIOBUTTON = null;
			WEBVIEW = null;
			JSOBJECT = null;
			PSEUDOCLASS = null;
			TOOLBAR = null;
			BUTTON = null;
			EVENTHANDLER = null;
			STACKPANE = null;
			REGION = null;
			HBOX = null;
			VBOX = null;
			PRIORITY = null;
			PARENT = null;
			PANE = null;
			PLATFORM = null;
			STAGE = null;
			STAGESTYLE = null;
			MODALITY = null;
			SCENE = null;
			SCREEN = null;
			POS = null;
			IMAGEVIEW = null;
			IMAGE = null;
			BORDERPANE = null;
			PARAMETER = null;
			TRANSFERMODE = null;
			TOOLKITFX = null;
			FILECHOOSERFX = null;
			PLATFORMIMPL = null;
		}
	}

	static {
		//AWT
		TOOLKIT = getClass("java.awt.Toolkit");
		if(TOOLKIT != null) {
			SYSTEMTRAY = getClass("java.awt.SystemTray");
			RECTANGLE = getClass("java.awt.Rectangle");
			ROBOT = getClass("java.awt.Robot");
			DIMENSION = getClass("java.awt.Dimension");
			RENDEREDIMAGE = getClass("java.awt.image.RenderedImage");
			IMAGEIO = getClass("javax.imageio.ImageIO");
			ACTIONLISTENER = getClass("java.awt.event.ActionListener");
			MENUITEM = getClass("java.awt.MenuItem");
			POPUPMENU = getClass("java.awt.PopupMenu");
			TRAYICON = getClass("java.awt.TrayIcon");
			AWTIMAGE = getClass("java.awt.Image");
			PROCESSBUILDERREDIRECT = getClass("java.lang.ProcessBuilder.Redirect");
			DESKTOP = getClass("java.awt.Desktop");
			JFILECHOOSER = getClass("javax.swing.JFileChooser");
			JFRAME = getClass("javax.swing.JFrame");
		} else {
			SYSTEMTRAY = null;
			RECTANGLE = null;
			ROBOT = null;
			DIMENSION = null;
			RENDEREDIMAGE = null;
			IMAGEIO = null;
			ACTIONLISTENER = null;
			MENUITEM = null;
			POPUPMENU = null;
			TRAYICON = null;
			AWTIMAGE = null;
			PROCESSBUILDERREDIRECT = null;
			DESKTOP = null;
			JFILECHOOSER = null;
			JFRAME = null;
		}
	}

	static {
		//GIT
		GIT = getClass("org.eclipse.jgit.api.Git");
		if(GIT != null) {
			REVWALK = getClass("org.eclipse.jgit.revwalk.RevWalk");
			FILEMODE = getClass("org.eclipse.jgit.lib.FileMode");
			FILEREPOSITORYBUILDER = getClass("org.eclipse.jgit.storage.file.FileRepositoryBuilder");
			CANONICALTREEPARSER = getClass("org.eclipse.jgit.treewalk.CanonicalTreeParser");
			REPOSITORY = getClass("org.eclipse.jgit.lib.Repository");
			ANYOBJECTID = getClass("org.eclipse.jgit.lib.AnyObjectId");
//			DIFFENTRY = getClass("org.eclipse.jgit.diff.DiffEntry");
//			OBJECTID = getClass("org.eclipse.jgit.lib.ObjectId");
//			OBJECTREADER = getClass("org.eclipse.jgit.lib.ObjectReader");
//			REF = getClass("org.eclipse.jgit.lib.Ref");
//			REVCOMMIT = getClass("org.eclipse.jgit.revwalk.RevCommit");
		} else {
			REVWALK = null;
			FILEREPOSITORYBUILDER = null;
			FILEMODE = null;
			CANONICALTREEPARSER = null;
			REPOSITORY = null;
			ANYOBJECTID = null;
//			DIFFENTRY = null;
//			OBJECTID = null;
//			OBJECTREADER = null;
//			REF = null;
//			REVCOMMIT = null;
		}
	}

	static {
		//EMF
		EPACKAGE = getClass("org.eclipse.emf.ecore.EPackage");
		if(EPACKAGE != null) {
			ECLASS = getClass("org.eclipse.emf.ecore.EClass");
			EATTRIBUTE = getClass("org.eclipse.emf.ecore.EAttribute");
			ECLASSIFIER = getClass("org.eclipse.emf.ecore.EClassifier");
			EREFERENCE = getClass("org.eclipse.emf.ecore.EReference");
			EOBJECT = getClass("org.eclipse.emf.ecore.EObject");
		} else {
			ECLASS = null;
			EATTRIBUTE = null;
			ECLASSIFIER = null;
			EREFERENCE = null;
			EOBJECT = null;
		}
	}
	public static Object newInstance(String className, Object... arguments) {
		try {
			Class<?> clazz = Class.forName(className);
			return newInstance(clazz, arguments);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static Object newInstanceStr(String className, Object... arguments) {
		try {
			Class<?> clazz = Class.forName(className);
			if(arguments != null && arguments.length % 2 == 0) {
				for(int i=0;i<arguments.length;i +=2) {
					if(arguments[i] instanceof String) {
						arguments[i] = Class.forName((String) arguments[i]);
					}
				}
			}
			return newInstance(clazz, arguments);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	
	public static Object newInstanceSimple(Class<?> instance, String... ignoreCreateMethods) {
		if(ignoreCreateMethods != null) {
			for(Method method : instance.getMethods()) {
				String methodName = method.getName();
				for(String m : ignoreCreateMethods) {
					if(methodName.equalsIgnoreCase(m)) {
						return null;
					}
				}
			}
		}
		Constructor<?>[] constructors = instance.getDeclaredConstructors();
		if(constructors == null || constructors.length<1) {
			return ReflectionLoader.newInstance(instance);
		} else {
			for(Constructor<?> con : constructors) {
				try {
					if(Modifier.isPublic(con.getModifiers()) == false) {
						con.setAccessible(true);
//						continue;
					}
					Object[] values = ReflectionBlackBoxTester.getParameters(con, con.getParameterTypes(), ReflectionBlackBoxTester.TYPE_NULLVALUE, null);
					Object newInstance = con.newInstance(values);
					if(newInstance != null) {
						return newInstance;
					}
					break;
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	public static final Set<Thread> closeThreads(Set<Thread> oldThreads) {
		Set<Thread> newThreads = Thread.getAllStackTraces().keySet();
		if(oldThreads != null) {
			try {
				if(oldThreads.size() != newThreads.size()) {
					for(Thread newThread : newThreads) {
						if(oldThreads.contains(newThread)) {
							continue;
						}
						newThread.interrupt();
					}
				}
			}catch(Exception e) {
			}
		}
		return newThreads;
	}
	
	public static Object newInstance(Class<?> instance, Object... arguments) {
		try {
			if(arguments == null) {
				Constructor<?> constructor = instance.getConstructor();
				return constructor.newInstance();
			}
			int len=0;
			int count = arguments.length;
			Class<?>[] methodArguments = null;
			Object[] methodArgumentsValues = null;
			if(arguments.length %2 == 1 || checkValue(arguments)) {
				if(arguments.length == 1 && arguments[0] == null) {
					count =0;
				}else {
					methodArguments=new Class[arguments.length];
					methodArgumentsValues=new Object[arguments.length];
					for(int i=0;i<arguments.length;i++) {
						if(arguments[i] != null) {
							methodArguments[i] = (Class<?>) arguments[i].getClass();
						}else {
							methodArguments[i] = Object.class;
						}
						methodArgumentsValues[i] = arguments[i];
					}
				}
			} else {
				len = arguments.length / 2;
			}
			if(methodArguments == null) {
				methodArguments=new Class[len];
				methodArgumentsValues=new Object[len];
				int pos=0;
				for(int i=0;i<count;i+=2) {
					methodArguments[pos] = (Class<?>) arguments[i];
					methodArgumentsValues[pos] = arguments[i+1];
					pos++;
				}
			}
			Constructor<?> constructor = instance.getDeclaredConstructor(methodArguments);
			return constructor.newInstance(methodArgumentsValues);
		} catch (Exception e) {
			if(logger != null) {
				e.printStackTrace(logger);
			}
		}
		return null;
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name, false, ReflectionLoader.class.getClassLoader());
		} catch (Throwable e) {
			if(logger != null) {
				e.printStackTrace(logger);
			}
		}
		return null;
	}

	public static Object createProxy(Object proxy, Class<?>... proxys){
		return java.lang.reflect.Proxy.newProxyInstance(ReflectionLoader.class.getClassLoader(),
				proxys, new ReflectionInterfaceProxy(proxy));
	}
	public static Object callChain(Object item, String... methodNames) {
		return callChain(item, true, methodNames);
	}

	public static Object callChain(Object item, boolean notify, String... methodNames) {
		if(methodNames == null) {
			return item;
		}
		Object callObj = item;
		for(String method : methodNames) {
			callObj = calling(callObj, method, notify, null);
		}
		return callObj;
	}
	public static boolean isAccessMethod(Object item, String methodName) {
		if(item == null || methodName == null) {
			return false;
		}
		Method method = null;
		try {
			method = item.getClass().getMethod(methodName);
		}catch (Exception e) {
			try {
				method = item.getClass().getDeclaredMethod(methodName);
			}catch (Exception e2) {
				method = null;
			}
		}
		if(method == null) {
			return false;
		}
		return isAccess(method, item);
	}

	public static Object getField(String fieldName, Object item) {
		Class<?> className = null;
		Object itemObj = null;
		if(item instanceof Class<?>) {
			className = (Class<?>) item;
		} else {
			itemObj = item;
			className = item.getClass();
		}
		Field field;
		try {
			field = className.getField(fieldName);
			field.setAccessible(true);
			return field.get(itemObj);
		} catch (Exception e) {
			try {
				field = className.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(itemObj);
			} catch (Exception e2) {
				if(logger != null) {
					e.printStackTrace(logger);
				}
			}
		}
		return null;
	}
	
	public static boolean setField(String fieldName, Object item, Object value) {
		Class<?> className = null;
		Object itemObj = null;
		if(item instanceof Class<?>) {
			className = (Class<?>) item;
		} else {
			itemObj = item;
			className = item.getClass();
		}
		Field field; 
		try {
			field = className.getField(fieldName);
			field.setAccessible(true);
			field.set(itemObj, value);
			return true;
		} catch (Exception e) {
			try {
				field = className.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(itemObj, value);
				return true;
			} catch (Exception e2) {
				if(logger != null) {
					e.printStackTrace(logger);
				}
			}
		}
		return false;
	}

	public static Object call(Object item, String methodName, Object... arguments) {
		return calling(item, methodName, true, null, arguments);
	}

	public static Object callStr(Object item, String methodName, Object... arguments) {
		try {
			if(arguments != null && arguments.length % 2 == 0) {
				for(int i=0;i<arguments.length;i +=2) {
					if(arguments[i] instanceof String) {
						arguments[i] = Class.forName((String) arguments[i]);
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return calling(item, methodName, true, null, arguments);
	}


	@SuppressWarnings("unchecked")
	public static List<Object> callList(Object item, String methodName, Object... arguments) {
		Object returnValue = calling(item, methodName, true, null, arguments);
		if(returnValue == null || returnValue instanceof List<?> == false) {
			return new SimpleList<Object>();
		}
		return (List<Object>)returnValue;
	}


	public static Object calling(Object item, String methodName, boolean notify, Object notifyObject, Object... arguments) {
		if(methodName == null || item == null) {
			return null;
		}
		int len=0;
		Class<?>[] methodArguments = null;
		Object[] methodArgumentsValues = null;
		if(arguments != null) {
			if(arguments.length %2 == 1 || checkValue(arguments)) {
				methodArguments=new Class[arguments.length];
				methodArgumentsValues=new Object[arguments.length];
				for(int i=0;i<arguments.length;i++) {
					if(arguments[i] != null) {
						methodArguments[i] = (Class<?>) arguments[i].getClass();
					}else {
						methodArguments[i] = Object.class;
					}
					methodArgumentsValues[i] = arguments[i];
				}
			} else {
				len = arguments.length / 2;
			}
		}
		if(methodArguments == null) {
			methodArguments=new Class[len];
			methodArgumentsValues=new Object[len];
			int pos=0;
			for(int i=0;i<arguments.length;i+=2) {
				methodArguments[pos] = (Class<?>) arguments[i];
				methodArgumentsValues[pos] = arguments[i+1];
				pos++;
			}
		}
		Method method = null;
		try {
			boolean staticCall =false;
			if(item instanceof Type == false) {
				staticCall = item instanceof Class<?>;
			}
			Class<?> itemClass;
			if(staticCall) {
				itemClass = ((Class<?>) item);
			}else {
				itemClass = item.getClass();
			}
			try {
				try {
					method = itemClass.getMethod(methodName, methodArguments);
				}catch (Exception e) {
					method = itemClass.getDeclaredMethod(methodName, methodArguments);
				}
			}catch (Exception e) {
				if(staticCall == false && item instanceof Class<?>) {
					itemClass = ((Class<?>) item);
					staticCall = true;
					try {
						method = itemClass.getMethod(methodName, methodArguments);
					}catch (Exception e2) {
						method = itemClass.getDeclaredMethod(methodName, methodArguments);
					}
				}
				if(method == null) {
					// next Try Last may be an ...
					if(methodArguments.length>0) {
						Class<?> simpleType = methodArguments[methodArguments.length - 1];
						methodArguments[methodArguments.length - 1] = ReflectionLoader.getClass("[L"+simpleType.getName()+";");
						if(methodArguments[methodArguments.length - 1] != null) {
							Object newValue = Array.newInstance(simpleType, 1);
							Array.set(newValue, 0, methodArgumentsValues[methodArgumentsValues.length - 1]);
							methodArgumentsValues[methodArgumentsValues.length - 1] = newValue;
							if(methodName != null && methodName.length()>0) {
								method = itemClass.getMethod(methodName, methodArguments);
							}
						}
					}
					if(method == null) {
						for(int i=0;i<methodArguments.length;i++) {
							methodArguments[i] = Object.class;
						}
						if(methodName != null && methodName.length()>0) {
							method = itemClass.getMethod(methodName, methodArguments);
						}
					}
					if(method == null) {
						if(methodName != null && methodName.length()>0) {
							method = itemClass.getMethod(methodName, new Class[0]);
						}
					}
				}
			}
			if(method != null) {
				boolean isPublic = Modifier.isPublic(method.getModifiers());
//				boolean isFinal = Modifier.isFinal(method.getModifiers());
				if(staticCall && isPublic) {
					return method.invoke(null, methodArgumentsValues);
				}
//				if(isAccess(method, item) == false || isPublic == false || (isPublic && isFinal)) {
					method.setAccessible(true);
//				}
				return method.invoke(item, methodArgumentsValues);
			}
		} catch (Exception e) {
			if(logger != null && notify) {
				errorCount++;
				e.printStackTrace(logger);
			} else if(notifyObject instanceof ObjectCondition){
				errorCount++;
				((ObjectCondition) notifyObject).update(e);
			} else if(notifyObject instanceof ErrorHandler){
				errorCount++;
				ErrorHandler handler = (ErrorHandler) notifyObject;
				handler.saveException(e);
			} else if(notify && Os.isEclipseAndNoReflection()) {
				errorCount++;
				System.err.println("ErrorCount: "+errorCount+" ("+method+")");
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean checkValue(Object[] arguments) {
		for(int i=0;i<arguments.length;i+=2) {
			if(arguments[i] instanceof Class<?> == false) {
				return true;
			}
			if(arguments[i+1] != null) {
				if(arguments[i+1].getClass().isAssignableFrom((Class<?>)arguments[i]) == false) {
					return false;
				}
			}
		}
		return false;
	}

	private static URLClassLoader initDriver() {
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		URLClassLoader sysloader = URLClassLoader.newInstance(new URL[] {}, systemClassLoader);
		return sysloader;
	}

	private static final URLClassLoader sysloader = initDriver();
	public static Connection loadSQLDriver(String driver, String database) {
		int pos=0;
		if(driver == null || (pos = driver.lastIndexOf(':')) < 0) {
			return null;
		}
		return loadSQLDriver(driver.substring(0, pos), driver.substring(pos + 1), database);
	}

	public static Connection loadSQLDriver(String driver, String host, String database) {
		try {
			if("jdbc:sqlite".equalsIgnoreCase(driver)) {
				File f = new File(host);
				URL url = new URL("file:///" + f.getAbsolutePath());

				Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
				method.setAccessible(true);

				method.invoke(sysloader, url);

				Thread.currentThread().setContextClassLoader(sysloader);

				Method getConnection = DriverManager.class.getDeclaredMethod("getConnection", String.class, Properties.class,
						Class.class);
				getConnection.setAccessible(true);

				Object manager = getConnection.invoke(DriverManager.class, driver+":"+database, new Properties(), null);
				return (Connection) manager;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isAccess(Member member, Object entity) {
		try {
			Method method = member.getClass().getMethod("canAccess", Object.class);
			if(method != null) {
//				field.canAccess(entity)
				return (Boolean) method.invoke(member, entity);
			}
		} catch (Exception e) {
		}
		try {
			Method method = member.getClass().getMethod("isAccessible");
			if(method != null) {
				return (Boolean) method.invoke(member, entity);
			}
		} catch (Exception e) {
		}
		return true;
	}
}
