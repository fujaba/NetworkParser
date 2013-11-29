package de.uniks.networkparser.gui.test;

import org.junit.Test;

import de.uniks.networkparser.gui.ModelListenerStringProperty;
import javafx.scene.control.TextField;

public class TestModelListener {

	@Test
	public void testField(){
		Person person = new Person();
		TextField field=new TextField();
		field.textProperty().bind(new ModelListenerStringProperty(new PersonCreator(), person, "caption"));
	}
}
