package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.GUIConverter;
import de.uniks.networkparser.gui.controls.Control;

public class FXMLConverter {

	@Test
	public void convert() {
		CharacterBuffer fxml = new FileBuffer().readResource("test/javafx/GroupAccount.fxml");
		GUIConverter converter = new GUIConverter();
		Control convert = converter.convert(fxml);
		
		//<VBox alignment="CENTER" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/10.0.1" xmlns:fx="http://javafx.com/fxml/1">
		//	<children>
		//		<HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0">
		//			<children>
		//				<Label text="Counter:"/><Spinner/>
		//	</children></HBox>
		//	<Button mnemonicParsing="false" text="Click"/>
		//	<Label text="0"></Label></children></VBox>
 		Assert.assertNotNull(convert);
	}
}
