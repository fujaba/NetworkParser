package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.javafx.component.ModelForm;
import de.uniks.networkparser.ext.javafx.window.FXStageController;
import de.uniks.networkparser.ext.javafx.window.SimpleShell;
import javafx.scene.layout.Pane;

public class FormGUI extends SimpleShell {
	public static void main(String[] args) { launch(args); }

	@Override
	protected Pane createContents(FXStageController value, Parameters args) {
		IdMap map = new  IdMap();
		map.with(new PersonCreator());
		PersonGUI albert= new PersonGUI().withName("Albert");

		ModelForm form = new ModelForm().withDataBinding(map, albert, true);
		withIcon(this.getClass().getResource("iconform.png").getPath());
		return form;
	}
}
