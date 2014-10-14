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
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

public abstract class FXStageController implements StageEvent{
	private Stage stage;
	private Scene scene;
	private Object controller;
	protected Pane pane;

	public void createScene(Pane pane){
		this.scene = new Scene(pane, 600, 400);
		stage.setScene(scene);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public FXStageController withStage(Stage value) {
		this.stage = value;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  stageClosing(we, stage);
	          }
		});
//		stage.setOnHiding(new EventHandler<WindowEvent>() {
//	          public void handle(WindowEvent we) {
//	        	  stageClosing(we, stage);
//	          }
//		});
		stage.setOnShowing(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  stageShowing(we, stage);
	          }
		});
		
		return this;
	}
	
	@Override
	public void stageClosing(WindowEvent event, Stage stage) {
		if(this.pane instanceof StageEvent) {
			((StageEvent)this.pane).stageClosing(event, stage);
		}
	}

	@Override
	public void stageShowing(WindowEvent event, Stage stage) {
		if(this.pane instanceof StageEvent) {
			((StageEvent)this.pane).stageClosing(event, stage);
		}
	}
	
	public Scene getScene() {
		return scene;
	}

	public Object getController() {
		return controller;
	}
	
	public FXStageController withController(Object value){
		this.controller = value;
		return this;
	}
	
	public void close(){
		this.stage.close();
	}
	public void show(){
		this.stage.show();
	}
	public Exception saveScreenShoot(String fullScreenFileName, String windowScreenFileName) {
		// Save Screenshot
		BufferedImage bi;
		try {
			if(fullScreenFileName != null) {
				bi = new Robot().createScreenCapture(new Rectangle(Toolkit
						.getDefaultToolkit().getScreenSize()));
				ImageIO.write(bi, "jpg", new File(fullScreenFileName));
			}
			if(windowScreenFileName != null) {
				bi = new Robot().createScreenCapture(new java.awt.Rectangle(
							((Double)stage.getX()).intValue(), 
							((Double)stage.getY()).intValue(), 
							((Double)stage.getWidth()).intValue(),
							((Double)stage.getHeight()).intValue())); 
				ImageIO.write(bi, "jpg",
						new File(windowScreenFileName));
			}
		} catch (Exception e1) {
			return e1;
		}
		return null;
	}

	public void setTitle(String value) {
		this.stage.setTitle(value);
	}
	
	public FXStageController withIcon(String value){
		if (this.stage != null) {
			if (value.startsWith("file")) {
				stage.getIcons().add(new Image(value));
			} else {
				stage.getIcons().add(new Image("file:" + value));
			}
		}
		return this;
	}
}
