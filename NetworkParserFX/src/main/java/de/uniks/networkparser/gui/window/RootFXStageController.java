package de.uniks.networkparser.gui.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RootFXStageController extends FXStageController implements WindowListener{
	private KeyListenerMap listener = new KeyListenerMap(this);
	private HashMap<String, Node> nodes=new HashMap<String, Node>(); 
	private Object model;

	public RootFXStageController(Stage newStage){
		this.withStage(newStage);
		this.withPane(new BorderPane());
		this.createScene(pane);
	}
	
	public BorderPane createBorderPane() {
		BorderPane value = new BorderPane();
		this.withPane(value);
		return value;
	}

	public GridPane createGridPane() {
		GridPane value = new GridPane();
		this.withPane(value);
		return value;
	}

	
	public Pane create(String fxmlFile) {
		return create(fxmlFile, null);
	}
	
	public Pane create(String fxmlFile, ResourceBundle resources)  {
		return create(RootFXStageController.class.getResource(fxmlFile), resources);
	}
	public Pane create(URL location, ResourceBundle resources)  {
		FXMLLoader fxmlLoader;
		if(location == null) {
			System.out.println("FXML not found");
			return null;
		}
		if(resources!=null){
			fxmlLoader = new FXMLLoader(location, resources, new JavaFXBuilderFactory());
		}else {
			fxmlLoader = new FXMLLoader(location);
		}
		try {
			this.withPane((Pane) fxmlLoader.load(location.openStream()));
			
		} catch (IOException e) {
			System.out.println("FXML Load Error:" +e.getMessage());
			return null;
		}
		this.withController(fxmlLoader.getController()); 
		return pane;
	}
	
	public Node getElementById(String id) {
		return nodes.get(id);
	}
	
	public RootFXStageController(String fxmlFile, ResourceBundle resources) {
		if(this.getStage() == null){
			this.withStage( new Stage());
		}
		create(fxmlFile, resources);
	}
	
	public RootFXStageController() {
	}

	public Pane getPane() {
		return pane;
	}
	
	public RootFXStageController withCenter(Node value){
		if(pane instanceof BorderPane) {
			((BorderPane)pane).setCenter(value);
		}
		return this;
	}

	public void showNewStage(String fxml, Class<?> path) {
		loadNewStage(fxml, path);
		this.show();
	}
	
	public Stage loadNewStage(String fxml, Class<?> path) {
		Stage oldStage = this.getStage();
		this.withStage(new Stage());
		URL location;
		if(path==null){
			location = this.getClass().getResource(fxml);
		}else{
			location = path.getResource(fxml);
		}
		if(location != null) {
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			Pane value=null;
			try {
				value = (Pane) fxmlLoader.load(location.openStream());
			} catch (IOException e) {
				System.out.println("FXML Load Error:" +e.getMessage());
				return null;
			}
			this.withPane(value);
			this.withController(fxmlLoader.getController()); 
			
			if(value instanceof StageEvent) {
				Stage myStage = getStage();
				((StageEvent)value).stageShowing(new WindowEvent(myStage, WindowEvent.WINDOW_SHOWING), myStage, this);
			}
			this.createScene(value);
		}
		oldStage.close();
		return this.getStage();
	}
	
	public void showNewStage(Node value) {
		Stage oldStage = this.getStage();
		
		this.withStage(new Stage());
		
		Pane newPane;
		if(value instanceof Pane) {
			newPane = (Pane) value;
		}else{
			newPane = new CustomPane(value);
		}
		
		if(value instanceof StageEvent) {
			Stage myStage = getStage();
			((StageEvent)value).stageShowing(new WindowEvent(myStage, WindowEvent.WINDOW_SHOWING), myStage, this);
		}
		
		this.createScene(newPane);
		this.show();
		this.withPane(newPane);
		oldStage.close();
	}
	
	public void initNode(Parent node) {
		for(Node item : node.getChildrenUnmodifiable()) {
			if(item.getId() != null && item.getId().length() > 0 ) {
				this.nodes.put(item.getId(), item);
				if(item instanceof Parent) {
					this.initNode((Parent) item);
				}
			}
		}
	}
	
	public RootFXStageController withPane(Pane value) {
		this.pane = value;
		this.initNode(pane);
		this.pane.addEventFilter(KeyEvent.KEY_PRESSED, listener);
		return this;
	}
	
	public RootFXStageController withSize(int width, int height) {
		getStage().setWidth(width);
		getStage().setHeight(height);
		return this;
	}
	
	public static RootFXStageController load(String fxml) {
		return new RootFXStageController(fxml, null);
	}
	public static RootFXStageController load(String fxml, Class<?> path) {
		RootFXStageController controller = new RootFXStageController();
		controller.create(path.getResource(fxml), null);
		return controller;
	}

	public static RootFXStageController show(Stage stage, String fxml, Class<?> path) {
		RootFXStageController controller = new RootFXStageController();
		controller.create(path.getResource(fxml), null);
		controller.show(stage);
		return controller;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}
	public RootFXStageController withModel(Object model) {
		this.model = model;
		return this;
	}
}
