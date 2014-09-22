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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RootFXStageController extends FXStageController{
	public RootFXStageController(Stage newStage){
		this.withStage(newStage);
		this.pane = new BorderPane();
		this.createScene(pane);
	}
	
	public RootFXStageController(String fxmlFile, ResourceBundle resources) throws IOException{
		if(this.getStage() == null){
			this.withStage( new Stage());
		}
		URL location = RootFXStageController.class.getResource(fxmlFile);
		FXMLLoader fxmlLoader;
		if(resources!=null){
			fxmlLoader = new FXMLLoader(location, resources, new JavaFXBuilderFactory());
		}else {
			fxmlLoader = new FXMLLoader(location);
		}
		this.pane = fxmlLoader.load(location.openStream());
		this.withController(fxmlLoader.getController()); 
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
		
		this.createScene(newPane);
		this.show();
		this.pane = newPane;
		oldStage.close();
	}
}
