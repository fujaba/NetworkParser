package de.uniks.networkparser.gui.javafx.window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.json.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class DiagramEditor extends Application {
	private final String CRLF="\r\n";
    private Scene scene;
	private WebView browser;
	private WebEngine webEngine;
 
    @Override
    public void start(Stage stage) throws MalformedURLException {
        // create scene
        stage.setTitle("ClassdiagrammEditor");
        browser = new WebView();
        webEngine = browser.getEngine();
        
        scene = new Scene(browser, 900, 600, Color.web("#666970"));
        stage.setScene(scene);
        
        copyFile("", "drawer.js");
        copyFile("", "graph.js");
        copyFile("", "diagramstyle.css");
        
        StringBuilder content=new StringBuilder("<html><head>"+CRLF);
        content.append("<script src=\"drawer.js\"></script>"+CRLF);
        content.append("<script src=\"graph.js\"></script>"+CRLF);
        content.append("<link href=\"diagramstyle.css\" rel=\"stylesheet\" type=\"text/css\">"+CRLF);
        content.append("</head><body>"+CRLF);
        content.append("<script language=\"Javascript\">"+CRLF);
        content.append("new ClassEditor(\"board\");"+CRLF);
        content.append("</script></body></html>");
        webEngine.loadContent(content.toString());
        System.out.println(content.toString());
        
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == Worker.State.SUCCEEDED) {
                  stage.setTitle(webEngine.getLocation());
                  System.out.println("called");
                  JSObject win = (JSObject) webEngine.executeScript("window");
                  win.setMember("java", new JavaApp(DiagramEditor.this));
              }				
			}
          });
        stage.show();
    }
    private void copyFile(String targetFolder, String file) {
		File target = new File(targetFolder + file);

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
//		JsonObject model=new JsonObject().withValue((String)value);
//    	JsonArray nodes = model.getJsonArray("nodes");
//    	 ClassModel classModel=new ClassModel(model.getString("package"));
//    	 for(Iterator<Object> i = nodes.iterator();i.hasNext();){
//    		 Object item = i.next();
//    		 if(item instanceof JsonObject) {
//    			 JsonObject node = (JsonObject) item;
//            	 classModel.createClazz(node.getString("id"));
//            	 JsonArray attributes = node.getJsonArray("attributes");
//            	 
//    		 }
//    	 }
//    	 
//    	 classModel.generate("gen");
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
        	this.owner.generate(new JsonObject().withValue((String)value));
        }
    }
}
