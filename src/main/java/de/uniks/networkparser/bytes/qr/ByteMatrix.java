package de.uniks.networkparser.bytes.qr;

/**
 * JAVAPORT: The original code was a 2D array of ints, but since it only ever
 * gets assigned -1, 0, and 1, I'm going to use less memory and go with bytes.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ByteMatrix {
	
	/** The bytes. */
	private final byte[][] bytes;

    /** The width. */
    private final int width;
    
	/**
	 * Instantiates a new byte matrix.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public ByteMatrix(int width, int height) {
	    if(width>=0 && height >= 0) {
	        bytes = new byte[height][width];
	        this.width = 0;
	    }else {
	        bytes = new byte[0][0];
	        this.width = 0;
	    }
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return bytes.length;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the byte
	 */
	public byte get(int x, int y) {
		if (y >= 0 && x >= 0 && bytes.length > y && bytes[y].length > x) {
			return bytes[y][x];
		}
		return 0;
	}

	/**
	 * Gets the array.
	 *
	 * @return an internal representation as bytes, in row-major order. array[y][x]
	 *         represents point (x,y)
	 */
	public byte[][] getArray() {
		return bytes;
	}

	/**
	 * Sets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean set(int x, int y, byte value) {
		if (y >= 0 && x >= 0 && bytes.length > y && bytes[y].length > x) {
			bytes[y][x] = value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean set(int x, int y, int value) {
		if (y >= 0 && x >= 0 && bytes.length > y && bytes[y].length > x) {
			bytes[y][x] = (byte) value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean set(int x, int y, boolean value) {
		if (y >= 0 && x >= 0 && bytes.length > y && bytes[y].length > x) {
			bytes[y][x] = (byte) (value ? 1 : 0);
			return true;
		}
		return false;
	}

	/**
	 * Clear.
	 *
	 * @param value the value
	 */
	public void clear(byte value) {
		for (int y = 0; y < getHeight(); ++y) {
			for (int x = 0; x < width; ++x) {
				bytes[y][x] = value;
			}
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(2 * width * getHeight() + 2);
		for (int y = 0; y < getHeight(); ++y) {
			for (int x = 0; x < width; ++x) {
				switch (bytes[y][x]) {
				case 0:
					result.append(" 0");
					break;
				case 1:
					result.append(" 1");
					break;
				default:
					result.append("  ");
					break;
				}
			}
			result.append('\n');
		}
		return result.toString();
	}
}
