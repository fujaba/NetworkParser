package de.uniks.networkparser.test.javafx;

import org.junit.Test;

import de.uniks.networkparser.ext.DiagramEditor;

public class ConvertWebtest {

	@Test
	public void testGoogle() throws InterruptedException {
		DiagramEditor.convertToPNG("http://www.google.de", "cap.png");
		Thread.sleep(5000);
		System.out.println("HH");
	}
}
