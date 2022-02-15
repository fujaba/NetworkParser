package de.uniks.networkparser.ext;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.JarValidator;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.gui.DialogBox;
import de.uniks.networkparser.ext.gui.GUIEvent;
import de.uniks.networkparser.ext.gui.JavaAdapter;
import de.uniks.networkparser.ext.gui.JavaBridgeFX;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.SimpleTimerTask;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * The Class DiagramEditor.
 *
 * @author Stefan
 */
public class DiagramEditor extends JavaAdapter implements ObjectCondition, Converter {
	private static final String FILE404 = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n<html><head><title>404 Not Found</title></head><body><h1>Not Found</h1><p>The requested URL was not found on this server.</p></body></html>";
	private static final String METHOD_GENERATE = "generating";
	private SimpleController controller;
	private Object logic;
	private SimpleEventCondition listener;
	protected String file;
	private final int WIDTH = 900;
	private final int HEIGHT = 600;
	protected boolean autoClose = true;
	private JSEditor jsEditor;
	private IdMap map;
	private static NetworkParserLog logger = new NetworkParserLog().withListener(new StringPrintStream());
	private static final String EDITOR = "Editor.html";

	private static DiagramEditor instance;

	/**
	 * Edobs.
	 *
	 * @param items the items
	 * @return the diagram editor
	 */
	public static DiagramEditor edobs(Object... items) {
		return edobs(false, items);
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	public NetworkParserLog getLogger() {
		return logger;
	}
	
	/**
	 * Instance.
	 *
	 * @return the diagram editor
	 */
	public static DiagramEditor instance() {
		if(instance == null) {
			instance = new DiagramEditor();
		}
		return instance;
	}

	/**
	 * Edobs.
	 *
	 * @param all the all
	 * @param items the items
	 * @return the diagram editor
	 */
	public static DiagramEditor edobs(boolean all, Object... items) {
		if (items == null) {
			return null;
		}
		if (instance == null) {
			instance().type = TYPE_EDOBS;
		}
		if (instance().map == null) {
			for (Object child : items) {
				if (child instanceof IdMap) {
					instance().map = (IdMap) child;
					break;
				}
			}
			if (instance().map == null)
				instance().map = new IdMap();
		}

		for (Object child : items) {
			if (child instanceof IdMap || child == null) {
				continue;
			}
			SendableEntityCreator creator = instance().map.getCreatorClass(child);
			if (creator == null) {
				String id = child.getClass().getSimpleName();
				id += "." + System.identityHashCode(child);
				creator = new GenericCreator().withItem(child).withId(id);
				instance().map.put(id, child, false);
				instance().map.withCreator(creator);
			} else if (instance().map.getId(child) == null) {
				/* Ups No ID */
				String id = child.getClass().getSimpleName();
				id += "." + System.identityHashCode(child);
				instance().map.put(id, child, false);
			}
		}

		HTMLEntity entity = new HTMLEntity();
		GraphList list = instance().map.toObjectDiagram(items[0]);
		if (all == false) {
			SimpleList<String> ids = new SimpleList<String>();
			for (Object item : items) {
				String id = instance().map.getId(item);
				ids.add(id);
			}
			Clazz[] array = list.getClazzes().toArray();
			SimpleList<Clazz> foundClazz = new SimpleList<Clazz>();
			for (Clazz clazz : array) {
				if (ids.contains(clazz.getId()) == false) {
					list.remove(clazz);
					foundClazz.add(clazz);
				}
			}
			Association[] assocs = list.getAssociations().toArray();
			for (Association assoc : assocs) {
				boolean found = false;
				for (Clazz clazz : foundClazz) {
					if (assoc.getClazz() == clazz || assoc.getOtherClazz() == clazz) {
						found = true;
						break;
					}
				}
				if (found) {
					list.remove(assoc);
				}
			}
		}
		GraphUtil.setGenPath(list, HTMLEntity.CLASSEDITOR);
		FileBuffer resourceHandler = new FileBuffer();
		addGraphType(resourceHandler, entity);
		String graph = list.toString(new GraphConverter());

		StringBuilder sb = new StringBuilder();
		sb.append("var json=");
		sb.append(graph);
		sb.append(";" + BaseItem.CRLF);
		sb.append("window['editor'] = new ClassEditor(json).layout();");
		sb.append("window['editor'].registerListener();");
		entity.withScript(sb.toString());

		String string = null;
		try {
			string = new File("neu.html").toURI().toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		FileBuffer.writeFile("neu.html", entity.toString());
		converting(instance(), string, null, false, false);
		return instance();
	}
	
	/**
	 * Adds the graph type.
	 *
	 * @param resourceHandler the resource handler
	 * @param entity the entity
	 */
	public static void addGraphType(Buffer resourceHandler, HTMLEntity entity) {
		if(entity == null) {
			return;
		}
		if(resourceHandler == null) {
			for(String item : HTMLEntity.GRAPH_RESOURCES) {
				entity.withHeader(item);
			}
			return;
		}
		for(String item : HTMLEntity.GRAPH_RESOURCES) {
			if(item.endsWith(".css")) {
				entity.withStyle(resourceHandler.readResource("graph/"+item));
			}else {
				entity.withScript(entity.getHeader(), resourceHandler.readResource("graph/"+item));
			}
		}
	}
	
	/**
	 * Adds the graph type.
	 *
	 * @param resourceHandler the resource handler
	 * @param entity the entity
	 * @param type the type
	 * @param allExtract the all extract
	 */
	public static void addGraphType(Buffer resourceHandler, HTMLEntity entity, String type, String allExtract) {
		if(entity == null) {
			return;
		}
		for(String item : HTMLEntity.GRAPH_RESOURCES) {
			if(item.endsWith(type)) {
				entity.withHeader(item);
				FileBuffer.writeFile(allExtract+"/"+item, resourceHandler.readResource("graph/"+item));
			}else if(allExtract != null) {
				FileBuffer.writeFile(allExtract+"/"+item, resourceHandler.readResource("graph/"+item));
			}
		}
	}

	/**
	 * Convert to PNG.
	 *
	 * @param entity the entity
	 * @param file the file
	 * @param dimension the dimension
	 * @return true, if successful
	 */
	public static boolean convertToPNG(HTMLEntity entity, String file, int... dimension) {
		return converting(null, entity, file, true, true, dimension);
	}

	/**
	 * Convert to PNG.
	 *
	 * @param url the url
	 * @param file the file
	 * @param dimension the dimension
	 * @return true, if successful
	 */
	public static boolean convertToPNG(String url, String file, int... dimension) {
		return converting(null, url, file, true, true, dimension);
	}

	/**
	 * Convert to PNG.
	 *
	 * @param localFile the local file
	 * @param file the file
	 * @param dimension the dimension
	 * @return true, if successful
	 */
	public static boolean convertToPNG(File localFile, String file, int... dimension) {
		return converting(null, localFile, file, true, true, dimension);
	}

	/**
	 * Converting.
	 *
	 * @param editor the editor
	 * @param entity the entity
	 * @param file the file
	 * @param wait the wait
	 * @param autoClose the auto close
	 * @param dimension the dimension
	 * @return true, if successful
	 */
	public static boolean converting(final DiagramEditor editor, final Object entity, final String file,
			final boolean wait, final boolean autoClose, int... dimension) {
		final int width, height;
		final DiagramEditor editorWindow;
		if (editor == null) {
			editorWindow = new DiagramEditor();
			editorWindow.type = DiagramEditor.TYPE_EDITOR;
		} else {
			editorWindow = editor;
		}
		if (dimension != null && dimension.length > 1) {
			width = dimension[0];
			height = dimension[1];
		} else {
			width = -1;
			height = -1;
		}
		if (SimpleController.startFX() == false) {
			return false;
		}
		if (entity != null && file != null) {
			ReflectionLoader.call(ReflectionLoader.PLATFORM, "setImplicitExit", boolean.class, false);
		}
		if (file != null) {
			editorWindow.file = file;
			editorWindow.type = DiagramEditor.TYPE_CONTENT;
			editorWindow.autoClose = autoClose;
		}
		Runnable runnable = DiagramEditorTask.createOpen(editorWindow, wait, entity, width, height);
		if (wait) {
			JavaAdapter.executeAndWait(runnable);
		} else {
			JavaAdapter.execute(runnable);
		}
		return true;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		NetworkParserLog logger = new NetworkParserLog().withListener(new StringPrintStream());
		if (args != null && args.length > 0 && args[0] != null) {
			if (GitRevision.MAINTAG.equalsIgnoreCase(args[0])) {
				GitRevision revision = new GitRevision();
				try {
					int commit = 1;
					if (args.length > 1) {
						try {
							commit = Integer.parseInt(args[1]);
						} catch (Exception e) { // Empty
						}
					}
					logger.debug(DiagramEditor.class, "main", revision.execute(commit));
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
				return;
			}
			if ("NPM".equalsIgnoreCase(args[0])) {
				if (new Gradle().loadNPM() == false) {
					System.exit(-1);
				}
				return;
			}
			if ("INIT".equalsIgnoreCase(args[0])) {
				if(ReflectionLoader.GIT == null) {
					DiagramEditor.logger("no GIT found");
				}
				String filename = Os.getFilename();
				if (filename == null) {
					System.exit(1);
				}
				int i=1;
				if (!filename.toLowerCase().endsWith(".jar")) {
					if (args.length > i && args[i] != null) {
						filename =args[i].toLowerCase();
					}
					if (!filename.toLowerCase().endsWith(".jar")) {
						System.exit(2);
					}
					i++;
				}
				Gradle gradle = new Gradle();
				String projectName = null;
				if (args.length > i && args[i] != null) {
					projectName = args[i];
				} else {
					System.exit(3);
				}
				DiagramEditor.logger("Init Project: "+projectName);
				i++;
				String licence = "MIT";
				if (args.length > i && args[i] != null && args[i].length() < 10) {
					licence = args[i];
				}
				gradle.withLogger(DiagramEditor.logger);
				boolean success = gradle.initProject(filename, projectName, licence);
				if (success == false) {
					System.exit(-1);
				}
				// GRADLE ININT SHOW IF TEST ADD
				i++;
				if (args.length > i && args[i] != null ) {
					String test = args[i].toLowerCase();
					if("jacoco".equals(test)) {
						gradle.initTest();
						i++;
					}
				}
				
				if (args.length > i && args[i] != null ) {
					String remoteURL = args[i];
					GitRevision revision = new GitRevision();
					revision.withPath(gradle.getProjectPath());
					revision.init(remoteURL);
					logger("GIT Init");
				}
				
				return;
			}
			if (JarValidator.MAINTAG.equalsIgnoreCase(args[0])) {
				JarValidator validator = new JarValidator();
				validator.withPath("build/libs");
				int timeOut = -1;
				for (String item : args) {
					if (item == null) {
						continue;
					}
					item = item.toLowerCase();
					if (item.startsWith("coverage=")) {
						String param = item.substring(9);
						try {
							Integer no = Integer.valueOf(param);
							validator.withMinCoverage(no);
						} catch (Exception e) {
						}
					} else if (item.startsWith("path=")) {
						String param = item.substring(5);
						validator.withPath(param);
					} else if (item.startsWith("root=")) {
						String param = item.substring(5);
						validator.withRootPath(param);
					} else if (item.startsWith("time=")) {
						String param = item.substring(5);
						try {
							timeOut = Integer.parseInt(param) * 1000;
						} catch (Exception e) {
						}
					} else if (item.startsWith("fatjar")) {
						validator.isAnalyseJar = true;
					} else if (item.startsWith("licence")) {
						validator.isLicence = true;
					} else if (item.startsWith("noerror")) {
						validator.isError = false;
					} else if (item.startsWith("nowarning")) {
						validator.isWarning = false;
					} else if (item.startsWith("noinstance=")) {
						/* Filter for Instance of SubPackage */
						validator.instancePackage = item.substring(11);
					} else if (item.startsWith("noinstance")) {
						validator.isInstance = false;
					}
				}
				/* ADD TIME OUT */
				Timer timer = null;
				if (timeOut > 0) {
					logger.debug(null, "main", "FOUND TIMEOUT= " + timeOut);
					timer = new Timer();
					SimpleTimerTask task = new SimpleTimerTask(Thread.currentThread());
					task.withTask(DiagramEditorTask.createExit(1, "TIMEOUT EXIT"));
					timer.schedule(task, timeOut);
				}
				int exit = 0;
				logger.debug(null, "main",
						"CHECK CC = " + validator.isValidate + " (" + validator.getMinCoverage() + ")");
				if (validator.isValidate) {
					validator.validate();
					int result = validator.analyseReport();
					if (result != 0) {

						logger.error(null, "main", "CodeCoverage not enough (" + result + ")");
						exit = -1;
					}
				}
				if (validator.isAnalyseJar) {
					/* Check for Licence */
					if (validator.isError) {
						int subExit = validator.searchFiles(System.err);
						if (subExit < 0) {
							exit = subExit;
							logger.error(null, "main", "FatJar Error");
						}
						if (validator.isValidate && subExit == 0 && validator.isExistFullJar() == false) {
							logger.error(null, "main", "No FatJar found");
							exit = -1;
						}
					} else {
						validator.searchFiles(System.err);
					}
				}
				if (timer != null) {
					timer.cancel();
				}
				if (exit < 0) {
					System.exit(exit);
				}
				return;
			}
			if (args[0].toLowerCase().startsWith("test=")) {
				ReflectionBlackBoxTester tester = new ReflectionBlackBoxTester();
				tester.withLogger(logger);
				tester.mainTester(args);
			}
		}
		if (!converting(null, null, null, false, true)) {
			/* NO JAVAFX Found */
			NodeProxyTCP server = NodeProxyTCP.createServer(8080);
			server.withListener(new DiagramEditor());
			if (server.start()) {
				logger.debug(DiagramEditor.class, "main", "LISTEN ON: " + server.getKey());
				if (ReflectionLoader.DESKTOP != null) {
					Object desktop = ReflectionLoader.call(ReflectionLoader.DESKTOP, "getDesktop");
					if (desktop != null) {
						try {
							ReflectionLoader.call(desktop, "browse", URI.class, new URI("http://" + server.getKey()));
						} catch (URISyntaxException e) { // Empty
						}
					}
				}
			}
		}
	}
	
	private static void logger(String msg) {
		if(DiagramEditor.logger != null) {
			DiagramEditor.logger.info(msg);
		}
	}

	/**
	 * Execute web server.
	 *
	 * @param msg the msg
	 * @return true, if successful
	 */
	public boolean executeWebServer(Message msg) {
		String request = msg.getMessage().toString();
		if (request.startsWith("GET")) {
			CharacterBuffer path = new CharacterBuffer();
			for (int i = 4; i < request.length(); i++) {
				if (request.charAt(i) == ' ') {
					break;
				}
				path.with(request.charAt(i));
			}
			if (path.equals("/")) {
				HTMLEntity html = new HTMLEntity();
				html.createScript("classEditor = new ClassEditor(\"board\");", html.getBody());
				addGraphType(null, html);
				String response = html.toString(2);
				writeHTTPResponse(msg, response, false);
			} else if (path.equalsIgnoreCase("/diagram.js")) {
				writeHTTPResponse(msg, new FileBuffer().readResource("graph/diagram.js").toString(), false);
			} else if (path.equalsIgnoreCase("/diagramstyle.css")) {
			} else if (path.equalsIgnoreCase("/style.css")) {
				writeHTTPResponse(msg, new FileBuffer().readResource("graph/stlye.css").toString(), false);
			} else if (path.equalsIgnoreCase("/jspdf.min.js")) {
				writeHTTPResponse(msg, new FileBuffer().readResource("graph/jspdf.min.js").toString(), false);
			} else if (path.equalsIgnoreCase("/dagre.min.js")) {
				writeHTTPResponse(msg, new FileBuffer().readResource("graph/dagre.min.js").toString(), false);
			} else if (path.equalsIgnoreCase("/favicon.ico")) {
				writeHTTPResponse(msg, new FileBuffer().readResource("favicon.ico").toString(), false);
				
			} else {
				writeHTTPResponse(msg, FILE404, true);
			}
		}
		return true;
	}

	private boolean writeHTTPResponse(Message message, String response, boolean error) {
		if (message == null) {
			return false;
		}
		if (error) {
			message.write("HTTP/1.1 404 Not Found\n");
		} else {
			message.write("HTTP/1.1 200 OK\n");
		}
		message.write("Date: " + new DateTimeEntity().toGMTString() + "\n");
		message.write("Server: Java\n");
		message.write("Last-Modified: " + new DateTimeEntity().toGMTString() + "\n");
		message.write("Content-Length: " + response.length() + "\n");
		message.write("Connection: Closed\n");
		message.write("Content-Type: text/html\n\n");
		message.write(response);
		Socket session = (Socket) message.getSession();
		try {
			if (session != null) {
				session.close();
			}
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Message) {
			return executeWebServer((Message) value);
		}
		if (value instanceof SimpleEvent) {
			SimpleEvent evt = (SimpleEvent) value;
			Object newValue = evt.getNewValue();
			if (newValue != null && JavaViewAdapter.STATE.equalsIgnoreCase(newValue.getClass().getName())) {
				if (newValue.toString().equals(JavaViewAdapter.FAILED)) {
					logger.error(this, "update", evt);
				}
				if (newValue.toString().equals(JavaViewAdapter.SUCCEEDED)) {
					Object win = super.executeScript("window", false);
					ReflectionLoader.call(win, "setMember", String.class, "JavaBridge", Object.class, this);
					this.changed(evt);

					if (TYPE_EDITOR.equalsIgnoreCase(type)) {
						/* Load Editor */
						super.executeScript("window['editor'] = new ClassEditor(\"board\");", false);
					}
				}
				return true;
			}
		}
		String name = (String) ReflectionLoader.callChain(value, "getEventType", "toString");
		if (JavaViewAdapter.DRAGOVER.equalsIgnoreCase(name)) {
			return onDragOver(value);
		}
		if (JavaViewAdapter.DRAGDROPPED.equalsIgnoreCase(name)) {
			return onDragDropped(value);
		}
		if (JavaViewAdapter.ERROR.equalsIgnoreCase(name)) {
			return onError(value);
		}
		if (JavaViewAdapter.DRAGEXITED.equalsIgnoreCase(name)) {
			return onDragExited(value);
		}
		if (value instanceof GUIEvent) {
			GUIEvent evt = (GUIEvent) value;
			EventTypes evtName = evt.getEventType();
			if (EventTypes.KEYPRESS == evtName && evt.getCode() == 123) {
				enableDebug();
			}
		}
		return false;
	}

	/**
	 * Gets the JS editor.
	 *
	 * @return the JS editor
	 */
	public JSEditor getJSEditor() {
		if (this.jsEditor == null) {
			Object JSwin = super.executeScript("window", false);
			Object result = ReflectionLoader.call(JSwin, "getMember", String.class, "editor");
			jsEditor = new JSEditor(result);
		}
		return jsEditor;
	}

	/**
	 * Exit.
	 */
	public void exit() {
		ReflectionLoader.call(ReflectionLoader.PLATFORM, "exit");
	}

	/**
	 * Save.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean save(Object value) {
		JsonObject model;
		if (value instanceof JsonObject) {
			model = (JsonObject) value;
		} else {
			model = new JsonObject().withValue("" + value);
		}
		String name = model.getString("package");
		if (name == null || name.length() < 1) {
			name = "model";
			if (model.size() < 1) {
				return false;
			}
		}
		DateTimeEntity entity = new DateTimeEntity();
		name = name + "_" + entity.toString("yyyyMMdd_HHmmss") + ".json";
		return FileBuffer.writeFile(name, model.toString()) >= 0;
	}

	/**
	 * Log.
	 *
	 * @param value the value
	 */
	public void log(String value) {
		if (this.owner != null) {
			this.owner.logScript(value, 0, this, null);
		}
	}

	/**
	 * Generate.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String generate(String value) {
		try {
			Thread.currentThread().setUncaughtExceptionHandler(DiagramEditorTask.createException(this));
			this.generating(new JsonObject().withValue(value));
		} catch (Exception e) {
			this.saveException(e);
		}
		return "";
	}

	/**
	 * Generating.
	 *
	 * @param model the model
	 * @return true, if successful
	 */
	public boolean generating(JsonObject model) {
		if (this.listener != null) {
			SimpleEvent event = new SimpleEvent(model, METHOD_GENERATE, null, null);
			event.with(model);
			if (this.update(event)) {
				return true;
			}
		}
		if (this.logic != null) {
			Object result = ReflectionLoader.call(this.logic, METHOD_GENERATE, JsonObject.class, model);
			if (result instanceof Boolean) {
				return (Boolean) result;
			}
		}
		GraphConverter converter = new GraphConverter();
		ClassModel modelGen = (ClassModel) converter.convertFromJson(model, new ClassModel());
		if (modelGen == null) {
			logger.error(this, "main", "no Nodes");
			return false;
		}
		modelGen.generate("src/main/java");
		return true;
	}

	protected boolean onDragOver(Object event) {
		List<File> files = getFiles(event);
		if (files != null) {
			boolean error = true;
			for (File fileItem : files) {
				String name = fileItem.getName().toLowerCase();
				if (name.indexOf("json", name.length() - 4) >= 0) {
					error = false;
				}
			}
			if (error) {
				Object mode = ReflectionLoader.getField(ReflectionLoader.TRANSFERMODE, "NONE");
				ReflectionLoader.call(event, "acceptTransferModes", ReflectionLoader.TRANSFERMODE, mode);
				getJSEditor().setBoardStyle("Error");
			} else {
				Object mode = ReflectionLoader.getField(ReflectionLoader.TRANSFERMODE, "COPY");
				ReflectionLoader.call(event, "acceptTransferModes", ReflectionLoader.TRANSFERMODE, mode);
				getJSEditor().setBoardStyle("OK");
			}
		}
		ReflectionLoader.call(event, "consume");
		return true;
	}

	protected boolean onDragDropped(Object event) {
		List<File> files = getFiles(event);
		if (files != null) {
			for (File fileItem : files) {
				StringBuilder sb = new StringBuilder();
				byte[] buf = new byte[1024];
				int read;
				FileInputStream is = null;
				try {
					is = new FileInputStream(fileItem);
					do {
						read = is.read(buf, 0, buf.length);
						if (read > 0) {
							sb.append(new String(buf, 0, read, "UTF-8"));
						}
					} while (read >= 0);
				} catch (IOException e) {
				} finally {
					if (is != null) {
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
		logger.error(this, "onError", ReflectionLoader.call(event, "getMessage"));
		return true;
	}

	@SuppressWarnings("unchecked")
	protected List<File> getFiles(Object event) {
		Object db = ReflectionLoader.call(event, "getDragboard");
		if (db != null) {
			if ((Boolean) ReflectionLoader.call(db, "hasFiles")) {
				List<File> files = (List<File>) ReflectionLoader.call(db, "getFiles");
				return files;
			}
		}
		return null;
	}

	/**
	 * Load.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean load(Object item) {
		boolean result = super.load(item);
		if (result) {
			return result;
		}
		HTMLEntity html = new HTMLEntity();
		boolean loadFile = false;
		boolean includeFiles = false;
		/*
		 * html.createScript("classEditor = new ClassEditor(\"board\");",
		 * html.getBody());
		 */
		if (TYPE_EDITOR.equalsIgnoreCase(type) || TYPE_EXPORT.equalsIgnoreCase(type)) {
			loadFile = true;
		}
		if (TYPE_EXPORTALL.equalsIgnoreCase(type)) {
			loadFile = true;
			includeFiles = true;
		}
		Story.addResource(html, EDITOR, includeFiles);

		if (loadFile) {
			FileBuffer.writeFile(EDITOR, html.toString(), FileBuffer.NONE);
			try {
				String string = new File(EDITOR).toURI().toURL().toString();
				ReflectionLoader.call(webEngine, "load", string);
				return true;
			} catch (MalformedURLException e) {
			}
			return true;
		}
		/* Add external Files */
		ReflectionLoader.call(webEngine, "loadContent", html.toString());
		return true;
	}

	/**
	 * Creates the.
	 *
	 * @param stage the stage
	 * @param url the url
	 * @return the diagram editor
	 */
	public static DiagramEditor create(Object stage, String... url) {
		DiagramEditor editor = new DiagramEditor();
		editor.creating(stage, url, -1, -1);
		return editor;
	}

	protected DiagramEditor creating(Object stage, Object url, int width, int height) {
		if (stage == null) {
			return this;
		}
		if (this.controller == null) {
			this.controller = new SimpleController(stage);
			this.controller.withListener(this);
		}
		SimpleKeyValueList<String, String> parameterMap = controller.getParameterMap();

		if (parameterMap != null) {
			if (parameterMap.contains(TYPE_EXPORTALL)) {
				this.type = TYPE_EXPORTALL;
			}
		}
		this.registerListener(this);
		this.load(url);
		JavaBridgeFX javaFX = new JavaBridgeFX(this.map, this, JavaBridgeFX.CONTENT_TYPE_NONE);
		if (width < 0) {
			width = WIDTH;
		}
		if (height < 0) {
			height = HEIGHT;
		}
		controller.withTitle("ClassdiagrammEditor");
		controller.withSize(width, height);
		controller.withErrorPath("errors");
		this.owner = javaFX;
		return this;
	}

	/**
	 * Show.
	 *
	 * @param waitFor the wait for
	 */
	public void show(boolean waitFor) {
		controller.show(owner.getWebView(), waitFor, true);
	}

	/**
	 * With listener.
	 *
	 * @param item the item
	 * @return the diagram editor
	 */
	public DiagramEditor withListener(Object item) {
		this.logic = item;
		if (item instanceof SimpleEventCondition) {
			this.listener = (SimpleEventCondition) logic;
		}
		return this;
	}

	/**
	 * With icon.
	 *
	 * @param icon the icon
	 * @return the diagram editor
	 */
	public DiagramEditor withIcon(String icon) {
		if (controller != null) {
			controller.withIcon(icon);
		}
		return this;
	}

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	public SimpleController getController() {
		return controller;
	}

	/**
	 * Changed.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean changed(SimpleEvent evt) {
		if (TYPE_CONTENT.equalsIgnoreCase(type) == false) {
			super.changed(evt);
			return true;
		}
		if (SUCCEEDED.equals("" + evt.getNewValue())) {
			/* TEST */
			JavaAdapter.execute(DiagramEditorTask.createScreenDump(this));
			return true;
		}
		return true;
	}

	/**
	 * Screendump.
	 *
	 * @param nameExtension the name extension
	 */
	public void screendump(String nameExtension) {
		Object snapshotParametersClass = ReflectionLoader.getClass("javafx.scene.SnapshotParameters");
		Object writableImageClass = ReflectionLoader.getClass("javafx.scene.image.WritableImage");
		Object image = ReflectionLoader.call(webView, "snapshot", snapshotParametersClass, null, writableImageClass,
				null);
		if (image == null) {
			return;
		}
		Class<?> swingUtil = ReflectionLoader.getClass("javafx.embed.swing.SwingFXUtils");
		Object bufferedImageClass = ReflectionLoader.getClass("java.awt.image.BufferedImage");
		Object bufferedImage = ReflectionLoader.call(swingUtil, "fromFXImage", ReflectionLoader.IMAGE, image,
				bufferedImageClass, null);

		String fileName = this.file;
		if (nameExtension != null) {
			int pos = fileName.indexOf(".");
			if (pos < 1) {
				fileName = fileName + nameExtension;
			} else {
				fileName = fileName.substring(0, pos) + "-" + nameExtension + fileName.substring(pos);
			}
		}
		ReflectionLoader.call(ReflectionLoader.IMAGEIO, "write", ReflectionLoader.RENDEREDIMAGE, bufferedImage,
				String.class, "png", File.class, new File(fileName));
		if (autoClose) {
			controller.close();
		}
	}

	/**
	 * Export.
	 *
	 * @param type the type
	 * @param value the value
	 * @param name the name
	 * @param context the context
	 */
	public void export(String type, Object value, String name, String context) {
		if (this.controller == null || value == null) {
			return;
		}
		String typeName = "files";
		if ("PNG".equalsIgnoreCase(type)) {
			typeName = "Portable Network Graphics";
		} else if ("SVG".equalsIgnoreCase(type)) {
			typeName = "Scalable Vector Graphics";
		} else if ("JSON".equalsIgnoreCase(type)) {
			typeName = "JavaScript Object Notation";
		} else if ("HTML".equalsIgnoreCase(type)) {
			typeName = "Hypertext Markup Language";
		} else if ("PDF".equalsIgnoreCase(type)) {
			typeName = "Portable Document Format";
		}
		String file = DialogBox.showFileSaveChooser("Export Diagramm", name, typeName, type, this.controller.getStage());
		if (file != null && value instanceof String) {
			FileBuffer.writeFile(file, ((String) value).getBytes());
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		if (controller != null) {
			controller.close();
		}
	}

	/**
	 * With logger.
	 *
	 * @param logger the logger
	 * @return the diagram editor
	 */
	public DiagramEditor withLogger(NetworkParserLog logger) {
		DiagramEditor.logger = logger;
		return this;
	}
	
	/**
	 * Method for Save Model to Image.
	 *
	 * @param entity is the Model
	 * @return converted String
	 */
	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof GraphModel == false) {
			return null;
		}
		GraphModel model = (GraphModel) entity;
		HTMLEntity element = new HTMLEntity().withGraph(model);
		String fileName = this.file;
		if (fileName == null) {
			fileName = model.getName();
			if (fileName == null) {
				fileName = "diagram";
			}
			fileName += ".png";
		}
		convertToPNG(element, fileName);
		return fileName;
	}

	/**
	 * Dump.
	 *
	 * @param values the values
	 * @return the diagram editor
	 */
	public static final DiagramEditor dump(String... values) {
		DiagramEditor editor = new DiagramEditor();
		if (values != null && values.length > 0) {
			editor.file = values[0];
		}
		return editor;
	}
}
