package de.uniks.networkparser.ext;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.ext.generic.JarValidator;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.git.GitRevision;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.javafx.GUIEvent;
import de.uniks.networkparser.ext.javafx.JavaAdapter;
import de.uniks.networkparser.ext.javafx.JavaBridgeFX;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;

public class DiagramEditor extends JavaAdapter implements ObjectCondition {
	private static final String FILE404="<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n<html><head><title>404 Not Found</title></head><body><h1>Not Found</h1><p>The requested URL was not found on this server.</p></body></html>";
	private static final String METHOD_GENERATE="generating";
	private SimpleController controller;
	private Object logic;
	private SimpleEventCondition listener;
	private JavaBridgeFX bridge;
	private String file;
	private final int WIDTH=900;
	private final int HEIGHT=600;
	private boolean autoClose=true;
	private JSEditor jsEditor;

	public static boolean convertToPNG(HTMLEntity entity, String file, int... dimension) {
		return converting(entity, file, true, true, dimension);
	}
	public static boolean convertToPNG(String url, String file, int...dimension) {
		return converting(url, file, true, true, dimension);
	}
	public static boolean convertToPNG(File localFile, String file, int...dimension) {
		return converting(localFile, file, true, true, dimension);
	}
	public static boolean converting(final Object entity, final String file, final boolean wait, final boolean autoClose, int...dimension) {
		final int width, height;
		if(dimension != null && dimension.length>1) {
			width = dimension[0];
			height = dimension[1];
		} else {
			width = -1;
			height = -1;
		}
		final Class<?> launcherClass = ReflectionLoader.getClass("com.sun.javafx.application.LauncherImpl");
		if(launcherClass == null) {
			return false;
		}
		ReflectionLoader.call(launcherClass, "startToolkit");
		if(entity != null && file != null) {
			ReflectionLoader.call(ReflectionLoader.PLATFORM, "setImplicitExit", boolean.class, false);
		}
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object stage = ReflectionLoader.newInstance(ReflectionLoader.STAGE);
				DiagramEditor editor = new DiagramEditor();
				editor.type = DiagramEditor.TYPE_EDITOR;
				if(file != null) {
					editor.file = file;
					editor.type = DiagramEditor.TYPE_CONTENT;
					editor.autoClose = autoClose;
				}
				editor.creating(stage, entity, width, height);
				editor.withIcon(IdMap.class.getResource("np.png").toString());
				editor.show(wait);
			}
		};
		if(wait) {
			JavaAdapter.executeAndWait(runnable);
		} else {
			JavaAdapter.execute(runnable);
		}
		return true;
	}

	public static void main(String[] args) {
		if(args != null && args.length>0 && args[0] != null) {
			if("GIT".equalsIgnoreCase(args[0])) {
				GitRevision revision = new GitRevision();
				try {
					int commit = -1;
					if(args.length>1) {
						try {
							commit = Integer.valueOf(args[1]);
						}catch (Exception e) {
						}
					}
					System.out.println(revision.execute(commit));
				}catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if("JARVALIDATOR".equalsIgnoreCase(args[0])) {
				JarValidator validator = new JarValidator();
				validator.withPath("build/libs");
				for(String item : args) {
					if(item == null) {
						continue;
					}
					item = item.toLowerCase();
					if(item.startsWith("coverage=")) {
						String param = item.substring(9);
						try {
							Integer no = Integer.valueOf(param);
							validator.withMinCoverage(no);
						}catch (Exception e) {
						}
					} else if(item.startsWith("path=")) {
						String param = item.substring(5);
						validator.withPath(param);
					} else if(item.startsWith("root=")) {
						String param = item.substring(5);
						validator.withRootPath(param);
					} else if(item.startsWith("fatjar")) {
						validator.isAnalyseJar = true;
					} else if(item.startsWith("licence")) {
						validator.isLicence = true;
					} else if(item.startsWith("noerror")) {
						validator.isError = false;
					} else if(item.startsWith("nowarning")) {
						validator.isWarning = false;
					} else if(item.startsWith("noinstance")) {
						validator.isInstance = false;
					}
				}
				int exit=0;
				System.out.println("CHECK CC = "+validator.isValidate + "("+validator.getMinCoverage()+")");
				if(validator.isValidate) {
					validator.validate();
					if(validator.analyseReport() == false) {
						System.err.println("CodeCoverage not enough");
						exit = -1;
					}
				}
				if(validator.isAnalyseJar) {
					// Check for Licence
					if(validator.isError) {
						int subExit = validator.searchFiles(System.err);
						if(subExit < 0) {
							exit = subExit;
							System.err.println("FatJar Error");
						}
						if(validator.isValidate && subExit == 0 && validator.isExistFullJar() == false) {
							System.err.println("No FatJar found");
							exit = -1;
						}
					}else {
						validator.searchFiles(System.err);
					}
				}
				if(exit < 0) {
					System.exit(exit);
				}
				return;
			}
			if(args[0].toLowerCase().startsWith("test=")) {
				ReflectionBlackBoxTester.mainTester(args);
				return;
			}
		}
		if(converting(null, null, false, true) == false) {
			// NO JAVAFX Found
			NodeProxyTCP server = NodeProxyTCP.createServer(8080);
			server.withListener(new DiagramEditor());
			if(server.start()) {
				System.out.println("LISTEN ON: "+server.getKey());
				if(ReflectionLoader.DESKTOP != null) {
					Object desktop = ReflectionLoader.call(ReflectionLoader.DESKTOP, "getDesktop");
					if(desktop != null) {
						try {
							ReflectionLoader.call(desktop, "browse", URI.class, new URI("http://"+server.getKey()));
						} catch (URISyntaxException e) {
						}
					}
				}
			}
		}
	}

	public boolean executeWebServer(Message msg) {
		String  request = msg.getMessage().toString();
		if(request.startsWith("GET")) {
			CharacterBuffer path = new CharacterBuffer();
			for(int i=4;i<request.length();i++) {
				if(request.charAt(i) == ' ') {
					break;
				}
				path.with(request.charAt(i));
			}
			if(path.equals("/")) {
				HTMLEntity html = new HTMLEntity();
				html.createScript("classEditor = new ClassEditor(\"board\");", html.getBody());
				html.withHeader("diagram.js");
				html.withHeader("jspdf.min.js");
				html.withHeader("diagramstyle.css");
				String response = html.toString(2);
				writeHTTPResponse(msg, response, false);
			} else if(path.equalsIgnoreCase("/diagram.js")) {
				writeHTTPResponse(msg, FileBuffer.readResource("graph/diagram.js").toString(), false);
			} else if(path.equalsIgnoreCase("/diagramstyle.css")) {
				writeHTTPResponse(msg, FileBuffer.readResource("graph/diagramstyle.css").toString(), false);
			} else if(path.equalsIgnoreCase("/jspdf.min.js")) {
				writeHTTPResponse(msg, FileBuffer.readResource("graph/jspdf.min.js").toString(), false);
			}else {
				writeHTTPResponse(msg, FILE404, true);
			}
		}
		return true;
	}

	private void writeHTTPResponse(Message message, String response, boolean error) {
		if(error) {
			message.write("HTTP/1.1 404 Not Found\n");
		}else {
			message.write("HTTP/1.1 200 OK\n");
		}
		message.write("Date: "+new DateTimeEntity().toGMTString()+"\n");
		message.write("Server: Java\n");
		message.write("Last-Modified: "+new DateTimeEntity().toGMTString()+"\n");
		message.write("Content-Length: "+response.length()+"\n");
		message.write("Connection: Closed\n");
		message.write("Content-Type: text/html\n\n");
		message.write(response);
		Socket session = (Socket) message.getSession();
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean update(Object value) {
		if(value ==null) {
			return false;
		}
		if(value instanceof Message) {
			return executeWebServer((Message) value);
		}
		if(value instanceof SimpleEvent) {
			SimpleEvent evt = (SimpleEvent) value;
			if(JavaViewAdapter.STATE.equalsIgnoreCase(evt.getNewValue().getClass().getName())) {
				if(evt.getNewValue().toString().equals(JavaViewAdapter.SUCCEEDED)) {
					Object win = super.executeScript("window", false);
					ReflectionLoader.call(win, "setMember", String.class, "java", Object.class, this);
					this.changed(evt);

					// Load Editor
					super.executeScript("window['editor'] = new ClassEditor(\"board\");", false);
				}
				return true;
			}
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
//			return onDragDropped(value);
			return onDragExited(value);
		}
		if(value instanceof GUIEvent) {
			GUIEvent evt = (GUIEvent) value;
			EventTypes evtName = evt.getEventType();
			if(EventTypes.KEYPRESS == evtName) {
				if(evt.getCode() == 123) {
					enableDebug();
				}
			}
		}
		
		return false;
	}
	
	public JSEditor getJSEditor() {
		if(this.jsEditor == null) {
			Object JSwin = super.executeScript("window", false);
			Object result  = ReflectionLoader.call(JSwin, "getMember", String.class, "editor");
			jsEditor = new JSEditor(result);
		}
		return jsEditor;
	}

	public void exit() {
		ReflectionLoader.call(ReflectionLoader.PLATFORM, "exit");
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
		DateTimeEntity entity = new DateTimeEntity();
		name = name + "_" + entity.toString("yyyyMMdd_HHmmss") + ".json";
		return FileBuffer.writeFile(name, model.toString())>=0;
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
		if(this.listener != null) {
			SimpleEvent event = new SimpleEvent(model, METHOD_GENERATE, null,null);
			event.with(model);
			if(this.update(event)) {
				return true;
			}
		}
		if(this.logic != null) {
			Object result = ReflectionLoader.call(this.logic, METHOD_GENERATE, JsonObject.class, model);
			if(result instanceof Boolean) {
				return (Boolean) result;
			}
		}
		GraphConverter converter = new GraphConverter();
		ClassModel modelGen = (ClassModel) converter.convertFromJson(model, new ClassModel());
		if(modelGen == null) {
//		if (model.has(GraphConverter.NODES) == false) {
			System.err.println("no Nodes");
			System.out.println("no Nodes");
			return false;
		}
		modelGen.generate("src/main/java");
		return true;
	}

	protected boolean onDragOver(Object event) {
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
				ReflectionLoader.call(event, "acceptTransferModes", ReflectionLoader.TRANSFERMODE, mode);
				getJSEditor().setBoardStyle("OK");
			}else {
				Object mode = ReflectionLoader.getField("NONE", ReflectionLoader.TRANSFERMODE);
				ReflectionLoader.call(event, "acceptTransferModes", ReflectionLoader.TRANSFERMODE, mode);
				getJSEditor().setBoardStyle("Error");
			}
		}
		ReflectionLoader.call(event,"consume");
		return true;
	}
	protected boolean onDragDropped(Object event) {
		List<File> files = getFiles(event);
		if(files != null) {
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
				getJSEditor().importModel(sb.toString());
				break;
			}
		}
		return true;
	}

	protected boolean onDragExited(Object event) {
		getJSEditor().setBoardStyle("dragleave");
		return true;
	}

	protected void saveException(Object value) {
	}

	protected boolean onError(Object event) {
		System.err.println(ReflectionLoader.call(event, "getMessage"));
		return true;
	}

	@SuppressWarnings("unchecked")
	protected List<File> getFiles(Object event) {
		Object db = ReflectionLoader.call(event, "getDragboard");
		if((Boolean) ReflectionLoader.call(db, "hasFiles")) {
			List<File> files = (List<File>) ReflectionLoader.call(db, "getFiles");
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
		boolean loadFile = false;
		//html.createScript("classEditor = new ClassEditor(\"board\");", html.getBody());
		if(TYPE_EDITOR.equalsIgnoreCase(type)) {
			loadFile = true;
			html.withScript(FileBuffer.readResource("graph/diagram.js").toString(), html.getHeader());
			html.withHeader("dagre.min.js");
			html.withHeader("jspdf.min.js");
			html.withHeader("diagramstyle.css");
			FileBuffer.writeFile("dagre.min.js", FileBuffer.readResource("graph/dagre.min.js"), FileBuffer.NONE);
			FileBuffer.writeFile("diagram.js",FileBuffer.readResource("graph/diagram.js"), FileBuffer.NONE);
			FileBuffer.writeFile("jspdf.min.js",FileBuffer.readResource("graph/jspdf.min.js"), FileBuffer.NONE);
			FileBuffer.writeFile("diagramstyle.css",FileBuffer.readResource("graph/diagramstyle.css"), FileBuffer.NONE);
		}
		if(TYPE_EXPORT.equalsIgnoreCase(type)) {
			loadFile = true;
			html.withHeader("dagre.min.js");
			html.withHeader("diagram.js");
			html.withHeader("jspdf.min.js");
			html.withHeader("diagramstyle.css");
			FileBuffer.writeFile("dagre.min.js", FileBuffer.readResource("graph/dagre.min.js"), FileBuffer.NONE);
			FileBuffer.writeFile("diagram.js",FileBuffer.readResource("graph/diagram.js"), FileBuffer.NONE);
			FileBuffer.writeFile("jspdf.min.js",FileBuffer.readResource("graph/jspdf.min.js"), FileBuffer.NONE);
			FileBuffer.writeFile("diagramstyle.css",FileBuffer.readResource("graph/diagramstyle.css"), FileBuffer.NONE);
		} 
		if(TYPE_EXPORTALL.equalsIgnoreCase(type)) {
			// Add external Files
			loadFile = true;
			html.withScript(readFile("graph/dagre.min.js"), html.getHeader());
			html.withScript(readFile("graph/diagram.js"), html.getHeader());
			html.withScript(readFile("graph/jspdf.min.js"), html.getHeader());
			html.withScript(readFile("graph/diagramstyle.css"), html.getHeader());
		}
		if(loadFile) {
			FileBuffer.writeFile("Editor.html", html.toString(), FileBuffer.NONE);
			try {
				String string = new File("Editor.html").toURI().toURL().toString();
				ReflectionLoader.call(webEngine, "load", string);
				return true;
			} catch (MalformedURLException e) {
			}
			return true;
		}
		// Add external Files
		html.withScript(readFile("graph/dagre.min.js"), html.getHeader());
		html.withScript(readFile("graph/diagram.js"), html.getHeader());
		html.withScript(readFile("graph/diagramstyle.css"), html.getHeader());
		ReflectionLoader.call(webEngine, "loadContent", html.toString());
		return true;
	}

	public static DiagramEditor create(Object stage, String... url) {
		DiagramEditor editor = new DiagramEditor();
		editor.creating(stage, url, -1, -1);
		return editor;
	}
	private DiagramEditor creating(Object stage, Object url, int width, int height) {
		if(stage == null) {
			return this;
		}
		if(this.controller == null) {
			SimpleController controller = new SimpleController(stage);
			this.controller = controller;
			this.controller.withListener(this);
		}
		SimpleKeyValueList<String, String> parameterMap = controller.getParameterMap();

		if(parameterMap != null) {
			if(parameterMap.contains(TYPE_EXPORTALL)) {
				this.type = TYPE_EXPORTALL;
			}
		}
		this.registerListener(this);
		this.load(url);
		JavaBridgeFX javaFX = new JavaBridgeFX(null, this, JavaBridgeFX.CONTENT_TYPE_NONE);
		if(width<0) {
			width = WIDTH;
		}
		if(height<0) {
			height = HEIGHT;
		}
		controller.withTitle("ClassdiagrammEditor");
		controller.withSize(width, height);
		controller.withErrorPath("errors");
		this.bridge = javaFX;
		return this;
	}

	public void show(boolean waitFor) {
		controller.show(bridge.getWebView(), waitFor, true);
	}


	public DiagramEditor withListener(Object item) {
		this.logic = item;
		if(item instanceof SimpleEventCondition) {
			this.listener = (SimpleEventCondition) logic;
		}
		return this;
	}

	public DiagramEditor withIcon(String icon) {
		controller.withIcon(icon);
		return this;
	}

	/**
	 * @return the controller
	 */
	public SimpleController getController() {
		return controller;
	}
	
	@Override
	public boolean changed(SimpleEvent evt) {
		if(TYPE_CONTENT.equalsIgnoreCase(type) == false) {
			super.changed(evt);
			return true;
		}
		if(SUCCEEDED.equals(""+evt.getNewValue())) {
			// TEST
			JavaAdapter.execute(new Runnable() {
				@Override
				public void run() {
					screendump(null);
				}
			});
			return true;
		}
		return true;
	}
	
	public void screendump(String nameExtension) {
		Object snapshotParametersClass = ReflectionLoader.getClass("javafx.scene.SnapshotParameters");
		Object writableImageClass = ReflectionLoader.getClass("javafx.scene.image.WritableImage");
		Object image = ReflectionLoader.call(webView, "snapshot", snapshotParametersClass, null, writableImageClass, null);

		Class<?> swingUtil = ReflectionLoader.getClass("javafx.embed.swing.SwingFXUtils");
		Object bufferedImageClass = ReflectionLoader.getClass("java.awt.image.BufferedImage");
		Object bufferedImage = ReflectionLoader.call(swingUtil, "fromFXImage", ReflectionLoader.IMAGE, image, bufferedImageClass, null);
		
		String fileName = this.file;
		if(nameExtension != null) {
			int pos = fileName.indexOf(".");
			if(pos<1) {
				fileName = fileName + nameExtension;
			}else {
				fileName = fileName.substring(0, pos)+"-"+nameExtension+fileName.substring(pos);
			}
		}
		ReflectionLoader.call(ReflectionLoader.IMAGEIO, "write", ReflectionLoader.RENDEREDIMAGE, bufferedImage, String.class, "png", File.class, new File(fileName));
		if(autoClose) {
			controller.close();
		}
	}
	
	public void export(String type, Object value, String name, String context) {
		String typeName = "files"; 
		if("PNG".equalsIgnoreCase(type)) {
			typeName = "Portable Network Graphics";
		} else if("SVG".equalsIgnoreCase(type)) {
			typeName = "Scalable Vector Graphics";
		} else if("JSON".equalsIgnoreCase(type)) {
			typeName = "JavaScript Object Notation";
		} else if("HTML".equalsIgnoreCase(type)) {
			typeName = "Hypertext Markup Language";
		} else if("PDF".equalsIgnoreCase(type)) {
			typeName = "Portable Document Format";
		}
		String file = DialogBox.showFileSaveChooser("Export Diagramm", name, typeName, type, this.controller.getStage());
		if(file != null) {
			if(value instanceof String) {
				FileBuffer.writeFile(file, ((String) value).getBytes());
			}
		}
	}

	public void close() {
		if(controller != null) {
			controller.close();
		}
	}
}
