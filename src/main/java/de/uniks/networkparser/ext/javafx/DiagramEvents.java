package de.uniks.networkparser.ext.javafx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class DiagramEvents implements ObjectCondition {

	public static final String DRAGOVER="Drag_Over";
	public static final String DRAGDROPPED="Drag_Dropped";
	public static final String ERROR="Error";
	public static final String DRAGEXITED="Drag_Exited";
	public static final String STATE="javafx.concurrent.Worker$State";
	private Object webEngine;
	private DiagramController controller;
	
	public DiagramEvents(Object webEngine, DiagramController controller) {
		this.webEngine = webEngine;
		this.controller = controller;
	}
	
	@Override
	public boolean update(Object value) {
		if(value ==null) {
			return false;
		}
		if(STATE.equalsIgnoreCase(value.getClass().getName())) {
			if(value.toString()=="SUCCEEDED") {
				Object win = ReflectionLoader.call("executeScript", webEngine, "window");
				ReflectionLoader.call("setMember", win, String.class, "java", Object.class, new DiagrammCallBack(this.controller));
			}
			return true;
		}
		String name = (String) ReflectionLoader.callChain(value, "getEventType", "getName");
		if(DRAGOVER.equalsIgnoreCase(name)) {
			return onDragOver(value);
		}
		if(DRAGDROPPED.equalsIgnoreCase(name)) {
			return onDragDropped(value);
		}
		if(ERROR.equalsIgnoreCase(name)) {
			return onError(value);
		}
		if(DRAGEXITED.equalsIgnoreCase(name)) {
			return onDragExited(value);
		}
		return false;
	}
	
	private boolean onError(Object event) {
		System.err.println(ReflectionLoader.call("getMessage", event));
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private List<File> getFiles(Object event) {
		Object db = ReflectionLoader.call("getDragboard", event);
		if((Boolean) ReflectionLoader.call("hasFiles", db)) {
			List<File> files = (List<File>) ReflectionLoader.call("getFiles", db);
			return files;
		}
		return null;
	}
	private boolean onDragOver(Object event) {
		List<File> files = getFiles(event);
		if(files != null) {
			boolean error=true;
			for(File file:files){
				 String name = file.getName().toLowerCase();
				if(name.indexOf("json", name.length() - 4) >= 0) {
					error = false;
				}
			}
			if(!error) {
				Object mode = ReflectionLoader.getField("COPY", ReflectionLoader.TRANSFERMODE);
				ReflectionLoader.call("acceptTransferModes", event, ReflectionLoader.TRANSFERMODE, mode);
				ReflectionLoader.call("executeScript", webEngine, String.class, "classEditor.setBoardStyle(\"Ok\");");
			}else {
				Object mode = ReflectionLoader.getField("NONE", ReflectionLoader.TRANSFERMODE);
				ReflectionLoader.call("acceptTransferModes", event, ReflectionLoader.TRANSFERMODE, mode);
				ReflectionLoader.call("executeScript", webEngine, String.class, "classEditor.setBoardStyle(\"Error\");");
			}
		}
		ReflectionLoader.call("consume", event);
		return true;
	}
	private boolean onDragDropped(Object event) {
		List<File> files = getFiles(event);
		if(files != null) {
			for(File file:files){
				StringBuilder sb = new StringBuilder();
				byte buf[] = new byte[1024];
				int read;
				FileInputStream is = null;
				try {
					is=new FileInputStream(file);
					do {
						read = is.read(buf, 0, buf.length);
						if (read>0) {
							sb.append(new String(buf, 0, read, "UTF-8"));
						}
					} while (read>=0);
				} catch (IOException e) {
				}finally {
					if(is != null) {
						try {
							is.close();
						} catch (IOException e) {
						}
					}
				}
				ReflectionLoader.call("executeScript", webEngine, String.class, "classEditor.dropFile('"+sb.toString()+"', \""+file.getAbsolutePath()+"\");");
				break;
			}
		}
		return true;
	}
	
	private boolean onDragExited(Object event) {
		ReflectionLoader.call("executeScript", webEngine, "classEditor.setBoardStyle(\"dragleave\");");
		return true;
	}
}
