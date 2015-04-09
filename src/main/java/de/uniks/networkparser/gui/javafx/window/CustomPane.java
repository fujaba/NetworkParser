package de.uniks.networkparser.gui.javafx.window;

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
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CustomPane extends BorderPane implements StageEvent{
	private Node center;
	private Stage stage;

	public CustomPane(Node centerNode) {
		super();
		
		this.center  = centerNode;
		setCenter(centerNode);
	}
		
	@Override
	public void stageClosing(WindowEvent event, Stage stage, FXStageController controller) {
		if(this.center instanceof StageEvent) {
			((StageEvent)this.center).stageClosing(event, stage, controller);
		}		
	}

	@Override
	public void stageShowing(WindowEvent event, Stage stage, FXStageController controller) {
		this.stage = stage;
		if(this.center instanceof StageEvent) {
			((StageEvent)this.center).stageShowing(event, stage,controller);
		}
	}

	public Stage getStage() {
		return stage;
	}
}
