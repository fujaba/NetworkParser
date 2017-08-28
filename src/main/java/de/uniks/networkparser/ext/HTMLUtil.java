package de.uniks.networkparser.ext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.GUIEvent;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.gui.controls.Label;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.xml.HTMLEntity;

public class HTMLUtil {
	public static int BUFFER=100*1024;
	public static final String POST="POST";
	public static final String GET="GET";

	public static HTMLEntity postHTTP(String url, Map<String, String> params) {
		HttpURLConnection conn = getConnection(url, POST);
		CharacterBuffer sb=new CharacterBuffer();
		if(params != null) {
			for(Iterator<Entry<String, String>> i = params.entrySet().iterator();i.hasNext();) {
				Entry<String, String> item = i.next();
				if(sb.length() > 0 ) {
					sb.with('&');
				}
				sb.with(item.getKey(), "=", item.getValue());
			}
		}
		byte[] byteArray = sb.toByteArray();
		conn.setFixedLengthStreamingMode(byteArray.length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try {
			conn.connect();
			OutputStream os = conn.getOutputStream();
			os.write(byteArray);
			return readAnswer(conn);
		} catch (IOException e) {
		}
		
		return null;
	}
	
	private static HttpURLConnection getConnection(String url, String type) {
		HttpURLConnection conn =null;
		try {
			URL remoteURL = new URL(url);
			conn = (HttpURLConnection) remoteURL.openConnection();
			if(POST.equals(type)) {
				conn.setRequestMethod(POST);
				conn.setDoOutput(true);
			} else {
				conn.setRequestMethod(GET);
			}
		} catch (IOException e) {
		}
		return conn;
	}
	
	private static HTMLEntity readAnswer(HttpURLConnection conn) {
		HTMLEntity rootItem=new HTMLEntity();
		try {
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			byte[] messageArray = new byte[BUFFER];
			while (true) {
				int bytesRead = is.read(messageArray, 0, BUFFER);
				if (bytesRead <= 0)
					break; // <======= no more data
				sb.append(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
			}
			rootItem.withValue(sb.toString());
		}catch (IOException e) {
		}
		conn.disconnect();
		return rootItem;
	}
	
	public static HTMLEntity getHTTP(String url) {
		HttpURLConnection conn = getConnection(url, GET);
		return readAnswer(conn);
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
	
	@SuppressWarnings("unchecked")
	public static void addChildren(Object element, int pos, Object... childrenValues) {
		Object children = ReflectionLoader.calling("getChildren", element, false);
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
		Object children = ReflectionLoader.calling("getChildren", element, false);
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
}
