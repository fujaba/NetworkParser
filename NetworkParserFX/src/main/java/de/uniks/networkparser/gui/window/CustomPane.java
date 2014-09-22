package de.uniks.networkparser.gui.window;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

public class CustomPane extends BorderPane implements StageEvent{
	private Node center;

	public CustomPane(Node centerNode) {
		super();
		
		this.center  = centerNode;
		setCenter(centerNode);
	}
		
	@Override
	public void stageClosing(WindowEvent event) {
		if(this.center instanceof StageEvent) {
			((StageEvent)this.center).stageClosing(event);
		}		
	}

	@Override
	public void stageShowing(WindowEvent event) {
		if(this.center instanceof StageEvent) {
			((StageEvent)this.center).stageClosing(event);
		}
	}
}
