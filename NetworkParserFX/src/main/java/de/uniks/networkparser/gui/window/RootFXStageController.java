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
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RootFXStageController extends FXStageController implements WindowListener{
	private KeyListenerMap listener = new KeyListenerMap(this);

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
		URL location = RootFXStageController.class.getResource(fxmlFile);
		FXMLLoader fxmlLoader;
		if(resources!=null){
			fxmlLoader = new FXMLLoader(location, resources, new JavaFXBuilderFactory());
		}else {
			fxmlLoader = new FXMLLoader(location);
		}
		try {
			this.withPane((Pane) fxmlLoader.load(location.openStream()));
		} catch (IOException e) {
			return null;
		}
		this.withController(fxmlLoader.getController()); 
		return pane;
	}
	
	public RootFXStageController(String fxmlFile, ResourceBundle resources) {
		if(this.getStage() == null){
			this.withStage( new Stage());
		}
		create(fxmlFile, resources);
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
			((StageEvent)value).stageShowing(new WindowEvent(myStage, WindowEvent.WINDOW_SHOWING), myStage);
		}
		
		this.createScene(newPane);
		this.show();
		this.withPane(newPane);
		oldStage.close();
	}
	
	public RootFXStageController withPane(Pane value) {
		this.pane = value;
		this.pane.addEventFilter(KeyEvent.KEY_PRESSED, listener);
		return this;
	}
	
	public RootFXStageController withSize(int width, int height) {
		getStage().setWidth(width);
		getStage().setHeight(height);
		return this;
	}
}
