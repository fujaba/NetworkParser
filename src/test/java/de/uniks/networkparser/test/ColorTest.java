package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.gui.RGBColor;

public class ColorTest {
	@Test
	public void MergeBlueRed() {
		RGBColor purple = RGBColor.RED.add(RGBColor.BLUE);
		assertEquals("#7F007F", purple.toString());
	}

}
