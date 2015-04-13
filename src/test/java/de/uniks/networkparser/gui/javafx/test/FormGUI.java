package de.uniks.networkparser.gui.javafx.test;

import javafx.scene.layout.Pane;
import de.uniks.networkparser.gui.javafx.form.ModelForm;
import de.uniks.networkparser.gui.javafx.window.FXStageController;
import de.uniks.networkparser.gui.javafx.window.SimpleShell;
import de.uniks.networkparser.json.JsonIdMap;

public class FormGUI extends SimpleShell {
    public static void main(String[] args) { launch(args); }

	@Override
	protected Pane createContents(FXStageController value, Parameters args) {
        JsonIdMap map = new  JsonIdMap();
        map.withCreator(new PersonCreator());
        PersonGUI albert= new PersonGUI().withName("Albert");
        
        ModelForm form = new ModelForm().withDataBinding(map, albert, true);
        withIcon(this.getClass().getResource("iconform.png").getPath());
        return form;
	}
}