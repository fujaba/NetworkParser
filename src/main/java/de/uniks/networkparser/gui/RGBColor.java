package de.uniks.networkparser.gui;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.buffer.CharacterBuffer;

public class RGBColor {
	public static final RGBColor WHITE = new RGBColor().withValue(255, 255, 255);
	public static final RGBColor TANSPARENT = new RGBColor().withValue(0, 0, 0);
	public static final RGBColor BLACK = new RGBColor().withValue(0, 0, 0);
	public static final RGBColor RED = new RGBColor().withValue(255, 0, 0);
	public static final RGBColor GREEN = new RGBColor().withValue(0, 255, 0);
	public static final RGBColor BLUE = new RGBColor().withValue(0, 0, 255);
	public static final RGBColor PURPLE = new RGBColor().withValue(127.5f, 0, 127.5f);
	public static final RGBColor YELLOW = new RGBColor().withValue(255, 255, 0);
	public static final RGBColor ORANGE = new RGBColor().withValue(255, 127.5f, 0);

	private int argb;
	private float red;
	private float green;
	private float blue;
	private float cyan;
	private float magenta;
	private float yellow;

	public RGBColor withValue(float red, float green, float blue) {
		int a = 1;
		int ia = (int) (255.0 * a);
		int ir = (int) (255.0 * red * a);
		int ig = (int) (255.0 * green * a);
		int ib = (int) (255.0 * blue * a);
		this.argb = (ia << 24) | (ir << 16) | (ig << 8) | (ib << 0);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.calcCMYK();
		return this;
	}

	public static RGBColor create(String value) {
		RGBColor color = new RGBColor();
		if (value == null) {
			return color;
		}
		int red = Integer.valueOf(value.substring(0, 2), 16);
		int green = Integer.valueOf(value.substring(2, 4), 16);
		int blue = Integer.valueOf(value.substring(4, 6), 16);
		color.withValue(red, green, blue);
		return color;
	}

	public static RGBColor create(float red, float green, float blue) {
		RGBColor color = new RGBColor().withValue(red, green, blue);
		return color;
	}

	/**
	 * @return the red
	 */
	public int getRed() {
		return (int) red;
	}

	/**
	 * @param red the red to set
	 * @return thisComponent
	 */
	public RGBColor withRed(int red) {
		this.red = red;
		return this;
	}

	/**
	 * @return the green
	 */
	public int getGreen() {
		return (int) green;
	}

	/**
	 * @param green the green to set
	 * @return thisComponent
	 */
	public RGBColor withGreen(int green) {
		this.green = green;
		return this;
	}

	/**
	 * @return the blue
	 */
	public int getBlue() {
		return (int) blue;
	}

	/**
	 * @param blue the blue to set
	 * @return thisComponent
	 */
	public RGBColor withBlue(int blue) {
		this.blue = blue;
		return this;
	}

	/**
	 * @return the argb
	 */
	public int getArgb() {
		return argb;
	}

	private int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	public RGBColor add(RGBColor second) {
		RGBColor color = new RGBColor();
		float newRed = ((float) (getRed() + second.getRed())) / 2;
		float newGreen = ((float) (getGreen() + second.getGreen())) / 2;
		float newBlue = ((float) (getBlue() + second.getBlue())) / 2;
		color.withValue(newRed, newGreen, newBlue);
		return color;
	}

	private void calcCMYK() {
		int black = min(min(255 - (int) red, 255 - (int) green), 255 - (int) blue);
		if (black != 255) {
			this.cyan = (255 - red - black) / (255 - black);
			this.magenta = (255 - green - black) / (255 - black);
			this.yellow = (255 - blue - black) / (255 - black);
		} else {
			this.cyan = 255 - (int) red;
			this.magenta = 255 - (int) green;
			this.yellow = 255 - (int) blue;
		}
	}

	public RGBColor minus(RGBColor second) {
		RGBColor color = new RGBColor();
		if (getRed() == second.getRed() && getGreen() == second.getGreen() && getBlue() == second.getBlue()) {
			return color;
		}
		color.withRed(getRed() * 2 - second.getRed());
		color.withGreen(getGreen() * 2 - second.getGreen());
		color.withBlue(getBlue() * 2 - second.getBlue());
		return color;
	}

	public static RGBColor minus(String colorA, String colorB) {
		return RGBColor.create(colorA).minus(RGBColor.create(colorB));
	}

	/**
	 * @return the cyan
	 */
	public float getCyan() {
		return cyan;
	}

	/**
	 * @return the magenta
	 */
	public float getMagenta() {
		return magenta;
	}

	/**
	 * @return the yellow
	 */
	public float getYellow() {
		return yellow;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other instanceof RGBColor == false) {
			return false;
		}
		if (other.hashCode() == this.hashCode()) {
			return true;
		}
		RGBColor otherColor = (RGBColor) other;
		if (otherColor.getRed() != getRed()) {
			return false;
		}
		if (otherColor.getGreen() != getGreen()) {
			return false;
		}
		if (otherColor.getBlue() != getBlue()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public String toString() {
		CharacterBuffer buffer = new CharacterBuffer().withBufferLength(7);
		buffer.with("#");
		addHex((int) red, buffer);
		addHex((int) green, buffer);
		addHex((int) blue, buffer);
		return buffer.toString();
	}

	private void addHex(int value, CharacterBuffer buffer) {
		int t = (int) (value / 16);
		int rest = value - t * 16;
		if (t > 9) {
			buffer.with((char) (65 + t - 10));
		} else {
			buffer.with((char) (48 + t));
		}
		if (rest > 9) {
			buffer.with((char) (65 + rest - 10));
		} else {
			buffer.with((char) (48 + rest));
		}
	}
}
