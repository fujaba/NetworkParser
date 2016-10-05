package de.uniks.networkparser.test.javafx;

import java.lang.reflect.Method;

import de.uniks.networkparser.ext.javafx.window.FXStageController;
import de.uniks.networkparser.ext.javafx.window.StageEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimpleController implements StageEvent {
	protected FXStageController controller;
	
	@Override
	public void stageClosing(WindowEvent event, Stage stage, FXStageController controller) {
	}

	@Override
	public void stageShowing(WindowEvent event, Stage stage,
			FXStageController controller) {
		this.controller = controller;
	}
	
	public void init(Object model) {
		if(model != null ) {
			try {
				Method method = this.getClass().getMethod("set"+model.getClass().getSimpleName(), model.getClass());
				method.invoke(this, model);
			} catch (ReflectiveOperationException e) {
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			}
		}
	}
}
