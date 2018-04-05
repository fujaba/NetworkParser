package de.uniks.networkparser.test.javafx;

import java.io.File;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.DiagramEditor;
import de.uniks.networkparser.xml.HTMLEntity;

public class ConvertWebtest {

	public void testGoogle() throws InterruptedException {
		DiagramEditor.convertToPNG("http://www.google.de", "build/cap.png");
		DiagramEditor.convertToPNG(new File("doc/Serialization.html"), "build/cap2.png");
		HTMLEntity htmlEntity = new HTMLEntity();
		ClassModel classModel = new ClassModel();
		classModel.createClazz("Person");
		htmlEntity.withGraph(classModel);
		
		DiagramEditor.convertToPNG(htmlEntity, "build/cap3.png");
		
		Thread.sleep(10000);
	}
}
