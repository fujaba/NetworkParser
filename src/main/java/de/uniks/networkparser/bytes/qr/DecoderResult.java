package de.uniks.networkparser.bytes.qr;

import java.util.List;

/**
 * <p>
 * Encapsulates the result of decoding a matrix of bits. This typically applies
 * to 2D barcode formats. For now it contains the raw bytes obtained, as well as
 * a String interpretation of those bytes, if applicable.
 * </p>
 *
 * @author Sean Owen
 */
public final class DecoderResult {

	private final byte[] rawBytes;
	private final String text;
	private final List<byte[]> byteSegments;
	private final String ecLevel;
	private Integer errorsCorrected;
	private Integer erasures;
	private Object other;
	private final int structuredAppendParity;
	private final int structuredAppendSequenceNumber;

	/**
	 * Instantiates a new decoder result.
	 *
	 * @param rawBytes the raw bytes
	 * @param text the text
	 * @param byteSegments the byte segments
	 * @param ecLevel the ec level
	 */
	public DecoderResult(byte[] rawBytes, String text, List<byte[]> byteSegments, String ecLevel) {
		this(rawBytes, text, byteSegments, ecLevel, -1, -1);
	}

	/**
	 * Instantiates a new decoder result.
	 *
	 * @param rawBytes the raw bytes
	 * @param text the text
	 * @param byteSegments the byte segments
	 * @param ecLevel the ec level
	 * @param saSequence the sa sequence
	 * @param saParity the sa parity
	 */
	public DecoderResult(byte[] rawBytes, String text, List<byte[]> byteSegments, String ecLevel, int saSequence,
			int saParity) {
		this.rawBytes = rawBytes;
		this.text = text;
		this.byteSegments = byteSegments;
		this.ecLevel = ecLevel;
		this.structuredAppendParity = saParity;
		this.structuredAppendSequenceNumber = saSequence;
	}

	/**
	 * Gets the raw bytes.
	 *
	 * @return the raw bytes
	 */
	public byte[] getRawBytes() {
		return rawBytes;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the byte segments.
	 *
	 * @return the byte segments
	 */
	public List<byte[]> getByteSegments() {
		return byteSegments;
	}

	/**
	 * Gets the EC level.
	 *
	 * @return the EC level
	 */
	public String getECLevel() {
		return ecLevel;
	}

	/**
	 * Gets the errors corrected.
	 *
	 * @return the errors corrected
	 */
	public Integer getErrorsCorrected() {
		return errorsCorrected;
	}

	/**
	 * Sets the errors corrected.
	 *
	 * @param errorsCorrected the new errors corrected
	 */
	public void setErrorsCorrected(Integer errorsCorrected) {
		this.errorsCorrected = errorsCorrected;
	}

	/**
	 * Gets the erasures.
	 *
	 * @return the erasures
	 */
	public Integer getErasures() {
		return erasures;
	}

	/**
	 * Sets the erasures.
	 *
	 * @param erasures the new erasures
	 */
	public void setErasures(Integer erasures) {
		this.erasures = erasures;
	}

	/**
	 * Gets the other.
	 *
	 * @return the other
	 */
	public Object getOther() {
		return other;
	}

	/**
	 * Sets the other.
	 *
	 * @param other the new other
	 */
	public void setOther(Object other) {
		this.other = other;
	}

	/**
	 * Checks for structured append.
	 *
	 * @return true, if successful
	 */
	public boolean hasStructuredAppend() {
		return structuredAppendParity >= 0 && structuredAppendSequenceNumber >= 0;
	}

	/**
	 * Gets the structured append parity.
	 *
	 * @return the structured append parity
	 */
	public int getStructuredAppendParity() {
		return structuredAppendParity;
	}

	/**
	 * Gets the structured append sequence number.
	 *
	 * @return the structured append sequence number
	 */
	public int getStructuredAppendSequenceNumber() {
		return structuredAppendSequenceNumber;
	}

}