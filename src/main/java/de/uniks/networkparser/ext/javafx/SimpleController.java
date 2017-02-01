package de.uniks.networkparser.ext.javafx;

import java.lang.reflect.Method;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimpleController implements StageEvent {
	private FXStageController controller;
	protected Stage stage;
	
	@Override
	public void stageClosing(WindowEvent event, Stage stage, FXStageController controller) {
	}

	@Override
	public void stageShowing(WindowEvent event, Stage stage,
			FXStageController controller) {
		this.controller = controller;
		this.stage = stage;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void init(Object model) {
		if(model != null ) {
			try {
				Method method = this.getClass().getMethod("set"+model.getClass().getSimpleName(), model.getClass());
				method.invoke(this, model);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			}
		}
	}

	public FXStageController getController() {
		return controller;
	}
}
