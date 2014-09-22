package de.uniks.networkparser.gui.test;

import javafx.scene.layout.Pane;
import de.uniks.networkparser.gui.form.ModelForm;
import de.uniks.networkparser.gui.window.MasterShell;
import de.uniks.networkparser.json.JsonIdMap;

public class FormGUI extends MasterShell {
    public static void main(String[] args) { launch(args); }

	@Override
	protected Pane createContents() {
        JsonIdMap map = new  JsonIdMap();
        map.withCreator(new PersonCreator());
        PersonGUI albert= new PersonGUI().withName("Albert");
        
        ModelForm form = new ModelForm().withDataBinding(map, albert, true);
        withIconPath("iconform.png");
        return form;
	}
}
