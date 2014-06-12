package de.uniks.networkparser.gui.test;

import de.uniks.networkparser.gui.MasterShell;
import de.uniks.networkparser.gui.form.ModelForm;
import de.uniks.networkparser.json.JsonIdMap;

public class FormGUI extends MasterShell {
    public static void main(String[] args) { launch(args); }

	@Override
	protected void createContents() {
        JsonIdMap map = new  JsonIdMap();
        map.withCreator(new PersonCreator());
        PersonGUI albert= new PersonGUI().withName("Albert");
        
        this.rootPane.getChildren().add(new ModelForm().withDataBinding(map, albert, true));
        withIconPath("iconform.png");
	}
}
