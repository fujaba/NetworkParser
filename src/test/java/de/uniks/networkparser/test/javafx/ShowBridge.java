package de.uniks.networkparser.test.javafx;


import org.w3c.dom.Document;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class ShowBridge extends Application{
	private Scene scene;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
        // create the scene
        stage.setTitle("Web View");
	     scene = new Scene(new Browser(),750,500, Color.web("#666970"));
	        stage.setScene(scene);
//	        scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
	        stage.show();

		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		
		
//		webEngine.load("http://mySite.com");
		
//		SimpleController controller=new SimpleController(primaryStage);
		
//		WebView webView=new WebView();
		
//		controller.createContent(webView);
		
//		JavaBridgeFX javaBridgeFX = new JavaBridgeFX();
//		controller.createContent(javaBridgeFX);
		
		
	}
	class Browser extends Region {
	    final WebView browser = new WebView();
	    final WebEngine webEngine = browser.getEngine();
	     
	    public Browser() {
	        //apply the styles
	        getStyleClass().add("browser");
	        // load the web page
//	        webEngine.load("http://www.oracle.com/products/index.html");
	        
	        webEngine.getLoadWorker().stateProperty().addListener(
	                (ObservableValue<? extends State> ov, State oldState, 
	                    State newState) -> {
//	                        toolBar.getChildren().remove(toggleHelpTopics);
	                        if (newState == State.SUCCEEDED) {
	                            JSObject win = (JSObject) webEngine.executeScript("window");
	                            win.setMember("java", new JavaApp(Browser.this));
	                        }
	            });
	        
	        //add the web view to the scene
	        getChildren().add(browser);
	        
	        webEngine.load("file://C:/Arbeit/workspace/NetworkParser/diagram/diagram.html");
	        

//	    	if (isDebugging())
    		webEngine.documentProperty().addListener(new ChangeListener<Document>() {
    			@Override
    			public void changed(ObservableValue<? extends Document> prop, 
    					    Document oldDoc, Document newDoc) {
    				enableFirebug(webEngine);
    			}
    		});
	    }
	    
		/**
    	 * Enables Firebug Lite for debugging a webEngine.
    	 * @param engine the webEngine for which debugging is to be enabled.
    	 */
    	private void enableFirebug(final WebEngine engine) {
    		engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}"); 
    	}
	    private Node createSpacer() {
	        Region spacer = new Region();
	        HBox.setHgrow(spacer, Priority.ALWAYS);
	        return spacer;
	    }
	 
	    @Override protected void layoutChildren() {
	        double w = getWidth();
	        double h = getHeight();
	        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
	    }
	 
	    @Override protected double computePrefWidth(double height) {
	        return 750;
	    }
	 
	    @Override protected double computePrefHeight(double width) {
	        return 500;
	    }
	    
	    public boolean generate(JsonObject model) {
	    	// TODO VODOO
	    	
			if(!model.has("nodes")) {
			System.err.println("no Nodes");
			System.out.println("no Nodes");
			return false;
		}
    	JsonObject nodes = model.getJsonObject("nodes");
    	ClassModel classModel=new ClassModel(model.getString("package"));
		for (int i = 0; i < nodes.size(); i++) {
			Object item = nodes.getValueByIndex(i);
			if (item instanceof JsonObject) {
				JsonObject node = (JsonObject) item;
				Clazz clazz = classModel.createClazz(node.getString("id"));
				if(node.has("attributes")) {
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
//		if(model.has("edges")){
//			JsonArray edges = model.getJsonArray("edges");
//			for(Object entity : edges) {
//				if(entity instanceof JsonObject) {
//					JsonObject edge = (JsonObject) entity;
//					JsonObject source = (JsonObject) edge.getJsonObject("source");
//					JsonObject target = (JsonObject) edge.getJsonObject("target");
//					if(edge.getString("typ").equalsIgnoreCase("edge")) {
//						Clazz fromClazz = classModel.getClazz(source.getString("id"));
//						Clazz toClazz = classModel.getClazz(target.getString("id"));
//						
//						fromClazz.withBidirectional(toClazz, target.getString("property"), Cardinality.ONE, source.getString("property"), Cardinality.ONE);
//					}
//				}
//			}
//		}
		
		
// 	   	String genModel = classModel.getName()  + ".genModel";
//    	 classModel.getGenerator().testGeneratedCode(type);insertModelCreationCodeHere("gen", genModel, "testGenModel");
    	 classModel.generate("gen");
		return true;
		}
	}
	public class JavaApp {
		private Browser browser;
		public JavaApp(Browser browser) {
			this.browser = browser;
		}
		
        public void exit() {
            Platform.exit();
        }
        
        public boolean generate(String model) {
        	JsonObject json = new JsonObject().withValue(model);
        	
        	
        	return browser.generate(json);
        }
    }
}
