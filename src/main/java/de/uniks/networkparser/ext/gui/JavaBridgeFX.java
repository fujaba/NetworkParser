package de.uniks.networkparser.ext.gui;

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
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
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
		this.resourceHandler = new FileBuffer();
	}

	@Override
	public void addListener(Control c, EventTypes type, String methodName, Object object) {
		addEventListener(c, type, new MethodCallbackListener(object, methodName));
	}

	@SuppressWarnings("unchecked")
	public static void addChildren(Object element, int pos, Object... childrenValues) {
		Object children = ReflectionLoader.calling(element, "getChildren", false, null);
		if (children == null) {
			children = ReflectionLoader.call(element, "getItems");
		}
		if (children != null && children instanceof List<?>) {
			List<Object> childrenList = (List<Object>) children;
			for (Object item : childrenValues) {
				if (pos < 0) {
					childrenList.add(item);
				} else {
					childrenList.add(pos++, item);
				}
			}
		}
	}

	public static void removeChildren(Object element, Object... childrenValues) {
		Object children = ReflectionLoader.calling(element, "getChildren", false, null);
		if (children == null) {
			children = ReflectionLoader.call(element, "getItems");
		}
		if (children != null && children instanceof List<?>) {
			List<?> childrenList = (List<?>) children;
			for (Object item : childrenValues) {
				childrenList.remove(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void setStyle(Object element, boolean clear, String... stylesValues) {
		Object styles = ReflectionLoader.call(element, "getStyleClass");
		if (styles != null && styles instanceof List<?>) {
			List<String> styleList = (List<String>) styles;
			if (clear) {
				styleList.clear();
			}
			for (String item : stylesValues) {
				styleList.add(item);
			}
		}
	}

	public static void removeStyle(Object element, String... stylesValues) {
		Object styles = ReflectionLoader.call(element, "getStyleClass");
		if (styles != null && styles instanceof List<?>) {
			List<?> styleList = (List<?>) styles;
			for (String item : stylesValues) {
				styleList.remove(item);
			}

		}
	}

	public static void addListener(Object element, String method, Class<?> proxyClass, ObjectCondition condition) {
		if(proxyClass != null && proxyClass != Object.class) {
			GUIEvent event = new GUIEvent();
			event.withListener(condition);
			Object proxy = ReflectionLoader.createProxy(event, proxyClass);
			ReflectionLoader.call(element, method, proxyClass, proxy);
		}
	}

	public static Object convert(Control item, boolean clearStyle) {
		if (item instanceof Button) {
			return convertButton((Button) item, clearStyle);
		}
		if (item instanceof Label) {
			return convertLabel((Label) item, clearStyle);
		}
		return null;
	}

	private static Object convertButton(Button button, boolean clearStyle) {
		if (button == null || Os.isJavaFX() == false) {
			return null;
		}
		String value = button.getValue();
		Object javaFXBtn = ReflectionLoader.newInstance(false, ReflectionLoader.BUTTON, value);
		if (javaFXBtn == null) {
			return null;
		}
		List<ObjectCondition> events = button.getEvents(EventTypes.CLICK);
		ChainCondition condition = new ChainCondition();
		condition.with(events);
		condition.withStaticEvent(button);

		GUIEvent javaFXEvent = new GUIEvent();
		javaFXEvent.withListener(condition);
		Object proxy = ReflectionLoader.createProxy(javaFXEvent, ReflectionLoader.EVENTHANDLER);

		ReflectionLoader.call(javaFXBtn, "setOnAction", ReflectionLoader.EVENTHANDLER, proxy);

		ReflectionLoader.call(javaFXBtn, "setFocusTraversable", boolean.class, false);

		setStyle(javaFXBtn, clearStyle, "window-button", "window-" + button.getActionType() + "-button");

		if (value == null) {
			Object stackPane = ReflectionLoader.newInstance(ReflectionLoader.STACKPANE);
			setStyle(stackPane, true, "graphic");

			ReflectionLoader.call(javaFXBtn, "setGraphic", ReflectionLoader.NODE, stackPane);
			ReflectionLoader.call(javaFXBtn, "setMinSize", double.class, 17, double.class, 17);
			ReflectionLoader.call(javaFXBtn, "setPrefSize", double.class, 17, double.class, 17);
		}
		return javaFXBtn;
	}

	private static Object convertLabel(Label label, boolean clearStyle) {
		Object javaFXLabel;
		if (Os.isReflectionTest()) {
			return null;
		}
		if (Label.SPACER.equalsIgnoreCase(label.getType())) {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.REGION);
			Object prio = ReflectionLoader.getField(ReflectionLoader.PRIORITY, "ALWAYS");
			ReflectionLoader.call(ReflectionLoader.HBOX, "setHgrow", ReflectionLoader.NODE, javaFXLabel,
					ReflectionLoader.PRIORITY, prio);
		} else if (Label.TITLE.equalsIgnoreCase(label.getType())) {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.LABEL, label.getValue());
			ReflectionLoader.call(javaFXLabel, "setMaxHeight", double.class, Double.MAX_VALUE);
			setStyle(javaFXLabel, false, "window-title");
		} else {
			javaFXLabel = ReflectionLoader.newInstance(ReflectionLoader.LABEL, label.getValue());
		}

		return javaFXLabel;
	}
}
