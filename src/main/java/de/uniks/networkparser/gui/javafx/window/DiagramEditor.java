package de.uniks.networkparser.gui.javafx.window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;

import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class DiagramEditor extends SimpleShell {
	private final String CRLF="\r\n";
	private WebView browser;
	private WebEngine webEngine;
 
	@Override
	protected Parent createContents(FXStageController controller, Parameters args) {
		controller.withTitle("ClassdiagrammEditor");
		controller.withSize(900, 600);
		
		this.enableError("errors");
		
        browser = new WebView();
        webEngine = browser.getEngine();
        
        SimpleKeyValueList<String, String> map = getParameterMap();
        StringBuilder content=new StringBuilder("<html><head>"+CRLF);
        
        String body = "</head><body>"+CRLF + "<script language=\"Javascript\">new ClassEditor(\"board\");</script></body></html>"; 
        if(map.containsKey("export")) {
        	content.append("<script src=\"drawer.js\"></script>"+CRLF);
        	content.append("<script src=\"graph.js\"></script>"+CRLF);
        	content.append("<link href=\"diagramstyle.css\" rel=\"stylesheet\" type=\"text/css\">"+CRLF);
        	content.append(body);
        	copyFile("drawer.js");
            copyFile("graph.js");
            copyFile("diagramstyle.css");
            writeFile("Editor.html", content.toString());
            try {
				webEngine.load(new File("Editor.html").toURI().toURL().toString());
			} catch (MalformedURLException e) {
			}
        }else if(map.containsKey("exportall")) {
        	// Add external Files 
        	content.append(readFile("drawer.js"));
        	content.append(readFile("graph.js"));
        	content.append(readFile("diagramstyle.css"));
        	content.append(body);
			writeFile("Editor.html", content.toString());
			try {
				webEngine.load(new File("Editor.html").toURI().toURL().toString());
			} catch (MalformedURLException e) {
			}
        }else {
        	// Add external Files 
        	content.append(readFile("drawer.js"));
        	content.append(readFile("graph.js"));
        	content.append(readFile("diagramstyle.css"));
        	content.append(body);
            webEngine.loadContent(content.toString());
        }
      webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == Worker.State.SUCCEEDED) {
					System.out.println("called: " + webEngine.getLocation());
					JSObject win = (JSObject) webEngine.executeScript("window");
					win.setMember("java", new JavaApp(DiagramEditor.this));
				}
			}
        });
		return browser;
	}
    
    protected void writeFile(String file, String content) {
    	File target=new File(file);
        if (!target.exists()) {
			try {
				target.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(target);
			byte[] bytes = content.getBytes();
			out.write(bytes, 0, bytes.length);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
    }
    
    protected StringBuilder readFile(String file) {
    	InputStream is = GraphList.class.getResourceAsStream(file);
    	StringBuilder sb=new StringBuilder();
    	if(file.endsWith(".js")) {
    		sb.append("<script language=\"Javascript\">"+CRLF);
    	} else if(file.endsWith(".css")) {
    		sb.append("<style>"+CRLF);
    	}
		if (is != null) {
			final int BUFF_SIZE = 5 * 1024; // 5KB
			final byte[] buffer = new byte[BUFF_SIZE];
			try {
			while (true) {
				int count;
					count = is.read(buffer);
					if (count == -1)
						break;
					sb.append(new String(buffer, 0, count));
				}
				is.close();
			} catch (IOException e) {
			}
		}
		if(file.endsWith(".js")) {
    		sb.append("</script>"+CRLF);
    	} else if(file.endsWith(".css")) {
    		sb.append("</style>"+CRLF);
    	}
		return sb;
    }
    protected void copyFile(String file) {
		File target = new File(file);

		InputStream is = GraphList.class.getResourceAsStream(file);

		if (is != null) {
			final int BUFF_SIZE = 5 * 1024; // 5KB
			final byte[] buffer = new byte[BUFF_SIZE];

			try {
				if (!target.exists()) {
					target.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(target);

				while (true) {
					int count = is.read(buffer);
					if (count == -1)
						break;
					out.write(buffer, 0, count);
				}
				out.close();
				is.close();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}
    
	public static void main(String[] args) {
		launch(args);
	}
	
	public void generate(JsonObject model) {
	}
	
	public class JavaApp {
        private DiagramEditor owner;
		public JavaApp(DiagramEditor owner) {
        	this.owner = owner;
		}

		public void exit() {
        	System.out.println("Exit");
            Platform.exit();
        }
        
        public void generate(Object value) {
        	try {
        		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
    				public void uncaughtException(Thread t, Throwable e) {
    					JavaApp.this.owner.saveException(e);
    				}
    			});
        		this.owner.generate(new JsonObject().withValue((String)value));
        	}catch(RuntimeException e){
				this.owner.saveException(e);
			}catch(Exception e){
				this.owner.saveException(e);
			}catch(Throwable e){
				this.owner.saveException(e);
			}
        }
    }
}
