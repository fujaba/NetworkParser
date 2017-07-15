package de.uniks.networkparser.ext.javafx;

/*
NetworkParser
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
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class DiagramController extends SimpleController {
	private final static String CRLF = "\r\n";
	private Object browser;
	private Object webEngine;
	private Object logic;

	public DiagramController(Object primitiveStage) {
		super(primitiveStage);
	}
	
	public void show() {
		withTitle("ClassdiagrammEditor");
		withSize(900, 600);

		withErrorPath("errors");
		SimpleKeyValueList<String, String> map = getParameterMap();
		String value = map.get("logic");
		if(value==null) {
			this.logic = this;
		}else {
			try {
				Class<?> clazz = Class.forName(value);
				this.logic = clazz.newInstance();
			} catch (Exception e) {
			}
		}
		browser = ReflectionLoader.newInstance(ReflectionLoader.WEBVIEW);
		webEngine = ReflectionLoader.call("getEngine", browser);
		
		StringBuilder content = new StringBuilder("<html><head>" + CRLF);

		String body = "</head><body>" + CRLF
				+ "<script language=\"Javascript\">classEditor = new ClassEditor(\"board\");</script></body></html>";
		if (map.containsKey("export")) {
			content.append("<script src=\"drawer.js\"></script>" + CRLF);
			content.append("<script src=\"graph.js\"></script>" + CRLF);
			content.append("<link href=\"diagramstyle.css\" rel=\"stylesheet\" type=\"text/css\">" + CRLF);
			content.append(body);
			FileBuffer.writeFile("drawer.js",FileBuffer.readResource("graph/drawer.js"));
			FileBuffer.writeFile("graph.js",FileBuffer.readResource("graph/graph.js"));
			FileBuffer.writeFile("diagramstyle.css",FileBuffer.readResource("graph/diagramstyle.css"));
			FileBuffer.writeFile("Editor.html", content.toString());
			try {
				String string = new File("Editor.html").toURI().toURL().toString();
				ReflectionLoader.call("load", webEngine, string);
			} catch (MalformedURLException e) {
			}
		} else if (map.containsKey("exportall")) {
			// Add external Files
			
			content.append(readFile("graph/drawer.js"));
			content.append(readFile("graph/graph.js"));
			content.append(readFile("graph/diagramstyle.css"));
			content.append(body);
			FileBuffer.writeFile("Editor.html", content.toString());
			try {
				String string = new File("Editor.html").toURI().toURL().toString();
				ReflectionLoader.call("load", webEngine, string);
			} catch (MalformedURLException e) {
			}
		} else {
			// Add external Files
			content.append(readFile("graph/drawer.js"));
			content.append(readFile("graph/graph.js"));
			content.append(readFile("graph/diagramstyle.css"));
			content.append(body);
			ReflectionLoader.call("loadContent", webEngine, content.toString());
		}
		GUIEvent eventListener = new GUIEvent().withListener(new DiagramEvents(webEngine, this));
		Object proxy = ReflectionLoader.createProxy(eventListener, ReflectionLoader.EVENTHANDLER);

		ReflectionLoader.call("setOnError", webEngine, ReflectionLoader.EVENTHANDLER, proxy);
		
		ReflectionLoader.call("setOnDragExited", browser, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnDragOver", browser, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnDragDropped", browser, ReflectionLoader.EVENTHANDLER, proxy);

		Object stateProperty = ReflectionLoader.callChain(webEngine, "getLoadWorker", "stateProperty");
		Object changeProxy = ReflectionLoader.createProxy(eventListener, ReflectionLoader.CHANGELISTENER);
		ReflectionLoader.call("addListener", stateProperty, ReflectionLoader.CHANGELISTENER, changeProxy);
		
		super.show(browser);
	}
	
	private CharacterBuffer readFile(String file) {
		CharacterBuffer readResource = FileBuffer.readResource(file);
		if (file.endsWith(".js")) {
			readResource.withStart("<script language=\"Javascript\">", true);
			readResource.with("</script>", BaseItem.CRLF);
		} else if (file.endsWith(".css")) {
			readResource.withStart("<style>", true);
			readResource.with("</style>", BaseItem.CRLF);
		}
		return readResource;
	}

	public boolean generate(JsonObject model) {
		if(this.logic != null) {
			return (Boolean) ReflectionLoader.call("generate", this.logic, JsonObject.class, model);
		}
		return false;
	}

	public boolean save(JsonObject model) {
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

}
