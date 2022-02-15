package de.uniks.networkparser.bytes.qr;

/**
 * QRClass for representation of QR Code.
 *
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
public final class QRCode {
	
	/** The Constant NUM_MASK_PATTERNS. */
	public static final int NUM_MASK_PATTERNS = 8;
	private Mode mode;
	private ErrorCorrectionLevel ecLevel;
	private Version version;
	private int maskPattern;
	private ByteMatrix matrix;

	/**
	 * Instantiates a new QR code.
	 */
	public QRCode() {
		maskPattern = -1;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Gets the EC level.
	 *
	 * @return the EC level
	 */
	public ErrorCorrectionLevel getECLevel() {
		return ecLevel;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Gets the mask pattern.
	 *
	 * @return the mask pattern
	 */
	public int getMaskPattern() {
		return maskPattern;
	}

	/**
	 * Gets the matrix.
	 *
	 * @return the matrix
	 */
	public ByteMatrix getMatrix() {
		return matrix;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(200);
		result.append("<<\n");
		result.append(" mode: ");
		result.append(mode);
		result.append("\n ecLevel: ");
		result.append(ecLevel);
		result.append("\n version: ");
		result.append(version);
		result.append("\n maskPattern: ");
		result.append(maskPattern);
		if (matrix == null) {
			result.append("\n matrix: null\n");
		} else {
			result.append("\n matrix:\n");
			result.append(matrix);
		}
		result.append(">>\n");
		return result.toString();
	}

	/**
	 * Sets the mode.
	 *
	 * @param value the new mode
	 */
	public void setMode(Mode value) {
		mode = value;
	}

	/**
	 * Sets the EC level.
	 *
	 * @param value the new EC level
	 */
	public void setECLevel(ErrorCorrectionLevel value) {
		ecLevel = value;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Sets the mask pattern.
	 *
	 * @param value the new mask pattern
	 */
	public void setMaskPattern(int value) {
		maskPattern = value;
	}

	/**
	 * Sets the matrix.
	 *
	 * @param value the new matrix
	 */
	public void setMatrix(ByteMatrix value) {
		matrix = value;
	}

	/**
	 * Check if "mask_pattern" is valid.
	 * 
	 * @param maskPattern Is Pattern Match
	 * @return valid
	 */
	public static boolean isValidMaskPattern(int maskPattern) {
		return maskPattern >= 0 && maskPattern < NUM_MASK_PATTERNS;
	}
}
