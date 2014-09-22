package de.uniks.networkparser.gui.window;

import javafx.stage.WindowEvent;

public interface StageEvent {
	public void stageClosing(WindowEvent event);
	public void stageShowing(WindowEvent event);
}
