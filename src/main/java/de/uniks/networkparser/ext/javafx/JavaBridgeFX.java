package de.uniks.networkparser.ext.javafx;

import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.gui.controls.Label;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.ChainCondition;

public class JavaBridgeFX extends JavaBridge {
	public JavaBridgeFX() {
		this(null);
	}

	public JavaBridgeFX(IdMap map) {
		super(map, new JavaAdapter(), CONTENT_TYPE_INCLUDE);
	}
	
	public JavaBridgeFX(IdMap map, JavaViewAdapter webView, String type) {
		super(map, webView, type);
	}

	@Override
	public void addListener(Control c, EventTypes type, String methodName, Object object) {
		addEventListener(c, type, new MethodCallbackListener(object, methodName));
	}
	
	@SuppressWarnings("unchecked")
	public static void addChildren(Object element, int pos, Object... childrenValues) {
		Object children = ReflectionLoader.calling("getChildren", element, false, null);
		if(children == null) {
			children = ReflectionLoader.call("getItems", element);
		}
		if(children != null && children instanceof List<?>) {
			List<Object> childrenList = (List<Object>) children;
			for(Object item : childrenValues) {
				if(pos<0) {
					childrenList.add(item);
				}else {
					childrenList.add(pos++, item);
				}
			}
		}
	}
	public static void removeChildren(Object element, Object... childrenValues) {
		Object children = ReflectionLoader.calling("getChildren", element, false, null);
		if(children == null) {
			children = ReflectionLoader.call("getItems", element);
		}
		if(children != null && children instanceof List<?>) {
			List<?> childrenList = (List<?>) children;
			for(Object item : childrenValues) {
				childrenList.remove(item);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setStyle(Object element, boolean clear, String...stylesValues) {
		Object styles = ReflectionLoader.call("getStyleClass", element);
		if(styles != null && styles instanceof List<?>) {
			List<String> styleList = (List<String>) styles;
			if(clear) {
				styleList.clear();
			}
			for(String item : stylesValues) {
				styleList.add(item);
			}
		}
	}
	
	public static void removeStyle(Object element, String...stylesValues) {
		Object styles = ReflectionLoader.call("getStyleClass", element);
		if(styles != null && styles instanceof List<?>) {
			List<?> styleList = (List<?>) styles;
			for(String item : stylesValues) {
				styleList.remove(item);
			}
			
		}
	}
	
	public static void addListener(Object element, String method, Class<?> proxyClass, ObjectCondition condition) {
		GUIEvent event = new GUIEvent();
		event.withListener(condition);
		Object proxy = ReflectionLoader.createProxy(event, proxyClass);
		ReflectionLoader.call(method, element, proxyClass, proxy);
	}
	
	public static Object convert(Control item, boolean clearStyle) {
		if(item instanceof Button) {
			return convertButton((Button) item, clearStyle);
		}
		if(item instanceof Label) {
			return convertLabel((Label) item, clearStyle);
		}
		return null;
	}

	private static Object convertButton(Button button, boolean clearStyle) {
		String value = button.getValue();
		Object javaFXBtn = ReflectionLoader.newInstance(ReflectionLoader.BUTTON, value);
		List<ObjectCondition> events = button.getEvents(EventTypes.CLICK);
		ChainCondition condition = new ChainCondition();
		condition.with(events);
		condition.withStaticEvent(button);
		
		
		GUIEvent javaFXEvent = new GUIEvent();
		javaFXEvent.withListener(condition);
		Object proxy = ReflectionLoader.createProxy(javaFXEvent, ReflectionLoader.EVENTHANDLER);
		
		ReflectionLoader.call("setOnAction", javaFXBtn, ReflectionLoader.EVENTHANDLER, proxy);
		
		ReflectionLoader.call("setFocusTraversable", javaFXBtn, boolean.class, false);
		
		setStyle(javaFXBtn, clearStyle, "window-button", "window-"+button.getActionType()+"-button");

		if(value == null) {
			Object stackPane = ReflectionLoader.newInstance(ReflectionLoader.STACKPANE);
			setStyle(stackPane, true, "graphic");
			
			ReflectionLoader.call("setGraphic", javaFXBtn, ReflectionLoader.NODE, stackPane);
			ReflectionLoader.call("setMinSize", javaFXBtn, double.class, 17, double.class, 17);
			ReflectionLoader.call("setPrefSize", javaFXBtn,double.class,  17, double.class, 17);
		}
		return javaFXBtn;
	}
	
	private static Object convertLabel(Label label, boolean clearStyle) {
		Object javaFXLabel; 
		if(Label.SPACER.equalsIgnoreCase(label.getType())) {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.REGION);
			Object prio = ReflectionLoader.getField("ALWAYS", ReflectionLoader.PRIORITY) ;
			ReflectionLoader.call("setHgrow", ReflectionLoader.HBOX, ReflectionLoader.NODE, javaFXLabel, ReflectionLoader.PRIORITY, prio);
		} else if(Label.TITLE.equalsIgnoreCase(label.getType())) {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.LABEL, label.getValue());
			ReflectionLoader.call("setMaxHeight", javaFXLabel, double.class, Double.MAX_VALUE);
			setStyle(javaFXLabel, false, "window-title");
		} else {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.LABEL, label.getValue());
		}

		return javaFXLabel;
	}
}
