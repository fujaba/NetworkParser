package de.uniks.networkparser.ext.javafx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;

public class DiagramEditor extends JavaAdapter {
	public static final String TYPE_EXPORT="EXPORT";
	public static final String TYPE_EXPORTALL="EXPORTALL";
	private String type = TYPE_EXPORT;
	private SimpleController controller;	
	
	@Override
	public boolean update(Object value) {
        if(value ==null) {
            return false;
        }
        if(JavaViewAdapter.STATE.equalsIgnoreCase(value.getClass().getName())) {
            if(value.toString().equals(JavaViewAdapter.SUCCEEDED)) {
                Object win = ReflectionLoader.call("executeScript", webEngine, "window");
                ReflectionLoader.call("setMember", win, String.class, "java", Object.class, this);
            }
            return true;
        }
        String name = (String) ReflectionLoader.callChain(value, "getEventType", "getName");
        if(JavaViewAdapter.DRAGOVER.equalsIgnoreCase(name)) {
            return onDragOver(value);
        }
        if(JavaViewAdapter.DRAGDROPPED.equalsIgnoreCase(name)) {
            return onDragDropped(value);
        }
        if(JavaViewAdapter.ERROR.equalsIgnoreCase(name)) {
            return onError(value);
        }
        if(JavaViewAdapter.DRAGEXITED.equalsIgnoreCase(name)) {
            return onDragExited(value);
        }
        return false;
    }
	
	public void exit() {
		ReflectionLoader.call("exit", ReflectionLoader.PLATFORM);
	}

	public boolean save(Object value) {
		JsonObject model;
		if(value instanceof JsonObject) {
			model = (JsonObject) value;
		}else {
			model = new JsonObject().withValue((String) value);
		}
		String name = model.getString("package");
		if (name == null || name.length() < 1) {
			name = "model";
			if(model.size()<1) {
				return false;
			}
		}
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		name = name + "_" + formatter.format(new Date().getTime()) + ".json";
		return FileBuffer.writeFile(name, model.toString());
	}

	public void log(String value) {
		this.owner.logScript(value, 0, this, null);
	}
	
	public String generate(String value) {
		try {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					DiagramEditor.this.saveException(e);
				}
			});
			this.generating(new JsonObject().withValue(value));
		} catch (Exception e) {
			this.saveException(e);
		}
		return "";
	}
	
	public boolean generating(JsonObject model) {
		if (!model.has("nodes")) {
			System.err.println("no Nodes");
			System.out.println("no Nodes");
			return false;
		}
		JsonObject nodes = model.getJsonObject("nodes");
		ClassModel classModel = new ClassModel(model.getString("package"));
		for (int i = 0; i < nodes.size(); i++) {
			Object item = nodes.getValueByIndex(i);
			if (item instanceof JsonObject) {
				JsonObject node = (JsonObject) item;
				Clazz clazz = classModel.createClazz(node.getString("id"));
				if (node.has("attributes")) {
					JsonArray attributes = node.getJsonArray("attributes");
					for (Object entity : attributes) {
						if (entity instanceof String) {
							String attribute = (String) entity;
							int pos = attribute.indexOf(":");
							if (pos > 0) {
								clazz.createAttribute(attribute.substring(0, pos),
										DataType.create(attribute.substring(pos + 1)));
							}
						}
					}
				}
			}
		}
		// if(model.has("edges")){
		// JsonArray edges = model.getJsonArray("edges");
		// for(Object entity : edges) {
		// if(entity instanceof JsonObject) {
		// JsonObject edge = (JsonObject) entity;
		// JsonObject source = (JsonObject) edge.getJsonObject("source");
		// JsonObject target = (JsonObject) edge.getJsonObject("target");
		// if(edge.getString("typ").equalsIgnoreCase("edge")) {
		// Clazz fromClazz = classModel.getClazz(source.getString("id"));
		// Clazz toClazz = classModel.getClazz(target.getString("id"));
		//
		// fromClazz.withBidirectional(toClazz, target.getString("property"),
		// Cardinality.ONE, source.getString("property"), Cardinality.ONE);
		// }
		// }
		// }
		// }

		// String genModel = classModel.getName() + ".genModel";
		// classModel.getGenerator().testGeneratedCode(type);insertModelCreationCodeHere("gen",
		// genModel, "testGenModel");
		classModel.generate("gen");
		return true;
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
			Object webEngine = owner.getWebView();
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
			Object webEngine = owner.getWebView();
			for(File file : files){
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
		this.owner.executeScript("classEditor.setBoardStyle(\"dragleave\");");
		return true;
	}

	public void saveException(Object value) {
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
	
	// NULL Default
	public boolean load(Object item) {
		boolean result = super.load(item);
		if(result) {
			return result;
		}
		HTMLEntity html = new HTMLEntity();
		html.createScript("classEditor = new ClassEditor(\"board\");", html.getBody());
		if(type.equals(TYPE_EXPORT) || type.equals(TYPE_EXPORTALL)) {
			if(type.equals(TYPE_EXPORT)) {
				html.withHeader("drawer.js");
				html.withHeader("graph.js");
				html.withHeader("diagramstyle.css");
				FileBuffer.writeFile("drawer.js", FileBuffer.readResource("../../graph/drawer.js"));
				FileBuffer.writeFile("graph.js",FileBuffer.readResource("../../graph/graph.js"));
				FileBuffer.writeFile("diagramstyle.css",FileBuffer.readResource("../../graph/diagramstyle.css"));
			} else {
				// Add external Files
				html.withScript(readFile("../../graph/drawer.js"), html.getHeader());
				html.withScript(readFile("../../graph/graph.js"), html.getHeader());
				html.withScript(readFile("../../graph/diagramstyle.css"), html.getHeader());
			}
			FileBuffer.writeFile("Editor.html", html.toString());
			try {
				String string = new File("Editor.html").toURI().toURL().toString();
				ReflectionLoader.call("load", webEngine, string);
				return true;
			} catch (MalformedURLException e) {
			}
			return false;
		}
		// Add external Files
		html.withScript(readFile("../../graph/drawer.js"), html.getHeader());
		html.withScript(readFile("../../graph/graph.js"), html.getHeader());
		html.withScript(readFile("../../graph/diagramstyle.css"), html.getHeader());
		ReflectionLoader.call("loadContent", webEngine, html.toString());
		return true;
	}
	
	public static JavaBridge create(Object stage, String... url) {
		DiagramEditor event = new DiagramEditor();
		SimpleController controller = new SimpleController(stage); 
		event.withController(controller);
		SimpleKeyValueList<String, String> parameterMap = controller.getParameterMap();
		
		if(parameterMap != null) {
			if(parameterMap.contains(TYPE_EXPORTALL)) {
				event.type = TYPE_EXPORTALL;
			}
		}
		event.registerListener(event);
		if(url != null && url.length>0 && url[0] instanceof String) {
			event.load(url[0]);
		}
		JavaBridgeFX javaFX = new JavaBridgeFX(null, event, JavaBridgeFX.CONTENT_TYPE_NONE);
		controller.withTitle("ClassdiagrammEditor");
		controller.withSize(900, 600);
		controller.withErrorPath("errors");
		
		controller.show(javaFX.getWebView());
		return javaFX;
	}

	/**
	 * @return the controller
	 */
	public SimpleController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 * @return ThisComponent
	 */
	public DiagramEditor withController(SimpleController controller) {
		this.controller = controller;
		return this;
	}
}
