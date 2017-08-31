package de.uniks.networkparser.ext.generic;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectionLoader {
	public static PrintStream logger = null;

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

//	public static final Class<?> DIFFENTRY;
//	public static final Class<?> OBJECTID;
//	public static final Class<?> OBJECTREADER;
//	public static final Class<?> REF;
//	public static final Class<?> REVCOMMIT;
	
	
//	public static final Class<?> JUNIT = getClass("org.junit.Assert");
	
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
	
	public static Object newInstance(Class<?> instance, Object... arguments) {
		try {
			if(arguments == null) {
				return instance.newInstance();
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
		}
		return null;
	}
	
	public static Object createProxy(Object proxy, Class<?>... proxys){
		return java.lang.reflect.Proxy.newProxyInstance(ReflectionLoader.class.getClassLoader(),
				proxys, new ReflectionInterfaceProxy(proxy));
	}
	public static Object callChain(Object item, String... methodNames) {
		if(methodNames == null) {
			return item;
		}
		Object callObj = item;
		for(String method : methodNames) {
			callObj = calling(method, callObj, true);
		}
		return callObj;
	}
	public static Object getField(String fieldName, Object item) {
		Class<?> className = null;
		if(item instanceof Class<?>) {
			className = (Class<?>) item;
		} else {
			className = item.getClass();
		}
		Field field;
		try {
			field = className.getField(fieldName);
			return field.get(null);
		} catch (Exception e) {
			if(logger != null) {
				e.printStackTrace(logger);
			}
		}
		return null;
	}

	public static Object call(String methodName, Object item, Object... arguments) {
		return calling(methodName, item, true, arguments);
	}
	public static Object calling(String methodName, Object item, boolean notify, Object... arguments) {
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
			Method method = null;
			try {
				method = itemClass.getMethod(methodName, methodArguments);
			}catch (Exception e) {
				if(staticCall == false) {
					itemClass = ((Class<?>) item);
					method = itemClass.getMethod(methodName, methodArguments);
				}
				if(method == null) {
					for(int i=0;i<methodArguments.length;i++) {
						methodArguments[i] = Object.class;
					}
					method = itemClass.getMethod(methodName, methodArguments);
					if(method == null) {
						method = itemClass.getMethod(methodName, new Class[0]);
					}
				}
			}
			if(method != null) {
				if(staticCall) {
					return method.invoke(null, methodArgumentsValues);
				}
				method.setAccessible(true);
				return method.invoke(item, methodArgumentsValues);
			}				
		} catch (Exception e) {
			if(logger != null && notify) {
				e.printStackTrace(logger);
			} else {
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
}
