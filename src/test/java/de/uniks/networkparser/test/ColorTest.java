package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.gui.RGBColor;

public class ColorTest {
	@Test
	public void MergeBlueRed() {
		RGBColor purple = RGBColor.RED.add(RGBColor.BLUE);
		System.out.println(purple);
	}
	
}
