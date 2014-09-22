package de.uniks.networkparser.gui.window;

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
	        	  stageClosing(we);
	          }
		});
		stage.setOnShowing(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  stageShowing(we);
	          }
		});
		
		return this;
	}
	
	@Override
	public void stageClosing(WindowEvent event) {
		if(this.pane instanceof StageEvent) {
			((StageEvent)this.pane).stageClosing(event);
		}
	}

	@Override
	public void stageShowing(WindowEvent event) {
		if(this.pane instanceof StageEvent) {
			((StageEvent)this.pane).stageClosing(event);
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
