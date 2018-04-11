package de.uniks.networkparser.bytes.qr;

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
import java.io.UnsupportedEncodingException;

public class QRTokener {
	private final ReedSolomon rsDecoder;

	// The original table is defined in the table 5 of JISX0510:2004 (p.19).
	private static final int[] ALPHANUMERIC_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 0x00-0x0f
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 0x10-0x1f
			36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, // 0x20-0x2f
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, // 0x30-0x3f
			-1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 0x40-0x4f
			25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, // 0x50-0x5f
	};

	static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

	public QRTokener() {
		rsDecoder = new ReedSolomon(GenericGF.QR_CODE_FIELD_256);
	}

	/**
	 * <p>
	 * Convenience method that can decode a QR Code represented as a 2D array of
	 * booleans. "true" is taken to mean a black module.
	 * </p>
	 *
	 * @param image		booleans representing white/black QR Code modules
	 * @return 			text and bytes encoded within the QR Code
	 * @throws			Exception if something wrong
	 */
	public DecoderResult decode(boolean[][] image) throws Exception {
		int dimension = image.length;
		BitMatrix bits = new BitMatrix(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (image[i][j]) {
					bits.set(j, i);
				}
			}
		}
		return decode(bits);
	}

	/**
	 * <p>
	 * Convenience method that can decode a QR Code represented as a 2D array of
	 * booleans. "true" is taken to mean a black module.
	 * </p>
	 *
	 * @param bytes		bytes representing white/black QR Code modules
	 * @return 			text and bytes encoded within the QR Code
	 * @throws 			Exception if something wrong
	 */
	public DecoderResult decode(byte[][] bytes) throws Exception {
		int dimension = bytes.length;
		BitMatrix bits = new BitMatrix(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (bytes[i][j] > 0) {
					bits.set(j, i);
				}
			}
		}
		return decode(bits);
	}

	/**
	 * <p>
	 * Decodes a QR Code represented as a {@link BitMatrix}. A 1 or "true" is
	 * taken to mean a black module.
	 * </p>
	 *
	 * @param bits			booleans representing white/black QR Code modules
	 * @return 				text and bytes encoded within the QR Code
	 * @throws Exception	if the QR Code cannot be decoded or if error correction fails
	 */
	public DecoderResult decode(BitMatrix bits) throws Exception {
		// Construct a parser and read version, error-correction level
		BitMatrixParser parser = new BitMatrixParser(bits);
		Exception exception = null;
		DecoderResult result = null;
		try {
			return decode(parser);
		} catch (RuntimeException e) {
			exception = e;
		}

		try {

			// Revert the bit matrix
			parser.remask();

			// Will be attempting a mirrored reading of the version and format
			// info.
			parser.setMirror(true);

			// Preemptively read the version.
			parser.readVersion();

			// Preemptively read the format information.
			parser.readFormatInformation();

			/*
			 * Since we're here, this means we have successfully detected some
			 * kind of version and format information when mirrored. This is a
			 * good sign, that the QR code may be mirrored, and we should try
			 * once more with a mirrored content.
			 */
			// Prepare for a mirrored reading.
			parser.mirror();

			result = decode(parser);
			// Success! Notify the caller that the code was mirrored.
			// result.setOther(new QRCodeDecoderMetaData(true));
		} catch (Exception e) {
			// Throw the exception from the original reading
			if (exception != null) {
				throw exception;
			}
			// throw e;
		}
		return result;
	}

	private DecoderResult decode(BitMatrixParser parser) throws Exception {
		Version version = parser.readVersion();
		ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();

		// Read codewords
		byte[] codewords = parser.readCodewords();
		// Separate into data blocks
		DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version, ecLevel);
		if(dataBlocks == null) {
			return null;
		}

		// Count total number of data bytes
		int totalBytes = 0;
		for (DataBlock dataBlock : dataBlocks) {
			totalBytes += dataBlock.getNumDataCodewords();
		}
		byte[] resultBytes = new byte[totalBytes];
		int resultOffset = 0;

		// Error-correct and copy data blocks together into a stream of bytes
		for (DataBlock dataBlock : dataBlocks) {
			byte[] codewordBytes = dataBlock.getCodewords();
			int numDataCodewords = dataBlock.getNumDataCodewords();
			correctErrors(codewordBytes, numDataCodewords);
			for (int i = 0; i < numDataCodewords; i++) {
				resultBytes[resultOffset++] = codewordBytes[i];
			}
		}

		// Decode the contents of that stream of bytes
		return DecodedBitStreamParser.decode(resultBytes, version, ecLevel);
	}

	/**
	 * <p>
	 * Given data and error-correction codewords received, possibly corrupted by
	 * errors, attempts to correct the errors in-place using Reed-Solomon error
	 * correction.
	 * </p>
	 *
	 * @param codewordBytes			data and error correction codewords
	 * @param numDataCodewords		number of codewords that are data bytes
	 * @throws ChecksumException	if error correction fails
	 */
	private void correctErrors(byte[] codewordBytes, int numDataCodewords) {
		int numCodewords = codewordBytes.length;
		// First read into an array of ints
		int[] codewordsInts = new int[numCodewords];
		for (int i = 0; i < numCodewords; i++) {
			codewordsInts[i] = codewordBytes[i] & 0xFF;
		}
		int numECCodewords = codewordBytes.length - numDataCodewords;
		try {
			rsDecoder.decode(codewordsInts, numECCodewords);
		} catch (Exception ignored) {
			throw new RuntimeException("ChecksumInstance");
		}
		// Copy back into array of bytes -- only need to worry about the bytes
		// that were data
		// We don't care about errors in the error-correction codewords
		for (int i = 0; i < numDataCodewords; i++) {
			codewordBytes[i] = (byte) codewordsInts[i];
		}
	}
	// ENCODER

	// The mask penalty calculation is complicated. See Table 21 of
	// JISX0510:2004 (p.45) for details.
	// Basically it applies four rules and summate all penalties.
	private static int calculateMaskPenalty(ByteMatrix matrix) {
		return MaskUtil.applyMaskPenaltyRule1(matrix) + MaskUtil.applyMaskPenaltyRule2(matrix)
				+ MaskUtil.applyMaskPenaltyRule3(matrix) + MaskUtil.applyMaskPenaltyRule4(matrix);
	}

	/**
	 * encoding Content to QRCode
	 * @param content		text to encode
	 * @param ecLevel		error correction level to use
	 * @return representing the encoded QR code {@link QRCode}
	 */
	public QRCode encode(String content, ErrorCorrectionLevel ecLevel) {
		// Determine what character encoding has been specified by the caller,
		// if any
		String encoding = DEFAULT_BYTE_MODE_ENCODING;

		// Pick an encoding mode appropriate for the content. Note that this
		// will not attempt to use
		// multiple modes / segments even if that were more efficient. Twould be
		// nice.
		Mode mode = chooseMode(content, encoding);

		// This will store the header information, like mode and
		// length, as well as "header" segments like an ECI segment.
		BitArray headerBits = new BitArray();

		// Append ECI segment if applicable
		if (mode == Mode.BYTE && !DEFAULT_BYTE_MODE_ENCODING.equals(encoding)) {
			// REMOVE OTHER ENCODING
		}

		// (With ECI in place,) Write the mode marker
		appendModeInfo(mode, headerBits);

		// Collect data within the main segment, separately, to count its size
		// if needed. Don't add it to
		// main payload yet.
		BitArray dataBits = new BitArray();
		appendBytes(content, mode, dataBits, encoding);

		// Hard part: need to know version to know how many bits length takes.
		// But need to know how many
		// bits it takes to know version. First we take a guess at version by
		// assuming version will be
		// the minimum, 1:
		Version.getVersionForNumber(1);
		int provisionalBitsNeeded = headerBits.getSize() + mode.getCharacterCountBits(Version.getVersionForNumber(1))
				+ dataBits.getSize();
		Version provisionalVersion = chooseVersion(provisionalBitsNeeded, ecLevel);

		// Use that guess to calculate the right version. I am still not sure
		// this works in 100% of cases.

		int bitsNeeded = headerBits.getSize() + mode.getCharacterCountBits(provisionalVersion) + dataBits.getSize();
		Version version = chooseVersion(bitsNeeded, ecLevel);

		BitArray headerAndDataBits = new BitArray();
		headerAndDataBits.appendBitArray(headerBits);
		// Find "length" of main segment and write it
		int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
		appendLengthInfo(numLetters, version, mode, headerAndDataBits);
		// Put data together into the overall payload
		headerAndDataBits.appendBitArray(dataBits);

		Version.ECB ecBlock = version.getECBlocksForLevel(ecLevel);
		int numDataBytes = version.getTotalCodewords() - ecBlock.getTotalECCodewords();

		// Terminate the bits properly.
		terminateBits(numDataBytes, headerAndDataBits);

		// Interleave data bits with error correction code.
		BitArray finalBits = interleaveWithECBytes(headerAndDataBits, version.getTotalCodewords(), numDataBytes,
				ecBlock.getNumBlocks());

		QRCode qrCode = new QRCode();

		qrCode.setECLevel(ecLevel);
		qrCode.setMode(mode);
		qrCode.setVersion(version);

		// Choose the mask pattern and set to "qrCode".
		int dimension = version.getDimensionForVersion();
		ByteMatrix matrix = new ByteMatrix(dimension, dimension);
		int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
		qrCode.setMaskPattern(maskPattern);

		// Build the matrix and set it to "qrCode".
		MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
		qrCode.setMatrix(matrix);

		return qrCode;
	}

	/**
	 * @return the code point of the table used in alphanumeric mode or -1 if
	 *			there is no corresponding code in the table.
	 * @param code		AlphanumericCode
	 * @return getAlphanumericCode or -1
	 */
	static int getAlphanumericCode(int code) {
		if (code < ALPHANUMERIC_TABLE.length) {
			return ALPHANUMERIC_TABLE[code];
		}
		return -1;
	}

	/**
	 * Choose the best mode by examining the content. Note that 'encoding' is
	 * used as a hint; if it is Shift_JIS, and the input is only double-byte
	 * Kanji, then we return {@link Mode#KANJI}.
	 * @param content		Contentstring
	 * @param encoding		switch for Shift_JIS
	 * @return 				Mode
	 */
	private static Mode chooseMode(String content, String encoding) {
		if ("Shift_JIS".equals(encoding) && isOnlyDoubleByteKanji(content)) {
			// Choose Kanji mode if all input are double-byte characters
			return Mode.KANJI;
		}
		boolean hasNumeric = false;
		boolean hasAlphanumeric = false;
		for (int i = 0; i < content.length(); ++i) {
			char c = content.charAt(i);
			if (c >= '0' && c <= '9') {
				hasNumeric = true;
			} else if (getAlphanumericCode(c) != -1) {
				hasAlphanumeric = true;
			} else {
				return Mode.BYTE;
			}
		}
		if (hasAlphanumeric) {
			return Mode.ALPHANUMERIC;
		}
		if (hasNumeric) {
			return Mode.NUMERIC;
		}
		return Mode.BYTE;
	}

	private static boolean isOnlyDoubleByteKanji(String content) {
		byte[] bytes;
		try {
			bytes = content.getBytes("Shift_JIS");
		} catch (UnsupportedEncodingException ignored) {
			return false;
		}
		int length = bytes.length;
		if (length % 2 != 0) {
			return false;
		}
		for (int i = 0; i < length; i += 2) {
			int byte1 = bytes[i] & 0xFF;
			if ((byte1 < 0x81 || byte1 > 0x9F) && (byte1 < 0xE0 || byte1 > 0xEB)) {
				return false;
			}
		}
		return true;
	}

	private static int chooseMaskPattern(BitArray bits, ErrorCorrectionLevel ecLevel, Version version,
			ByteMatrix matrix) throws RuntimeException {

		int minPenalty = Integer.MAX_VALUE; // Lower penalty is better.
		int bestMaskPattern = -1;
		// We try all mask patterns to choose the best one.
		for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
			MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
			int penalty = calculateMaskPenalty(matrix);
			if (penalty < minPenalty) {
				minPenalty = penalty;
				bestMaskPattern = maskPattern;
			}
		}
		return bestMaskPattern;
	}

	private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) {
		// In the following comments, we use numbers of Version 7-H.
		for (int versionNum = 1; versionNum <= 40; versionNum++) {
			Version version = Version.getVersionForNumber(versionNum);
			// numBytes = 196
			int numBytes = version.getTotalCodewords();
			// getNumECBytes = 130
			Version.ECB ecBlocks = version.getECBlocksForLevel(ecLevel);
			if(ecBlocks !=null) {
				int numEcBytes = ecBlocks.getTotalECCodewords();
				// getNumDataBytes = 196 - 130 = 66
				int numDataBytes = numBytes - numEcBytes;
				int totalInputBytes = (numInputBits + 7) / 8;
				if (numDataBytes >= totalInputBytes) {
					return version;
				}
			}
		}
		throw new RuntimeException("Data too big");
	}

	/**
	 * Terminate bits as described in 8.4.8 and 8.4.9 of JISX0510:2004 (p.24).
	 * @param numDataBytes			count of Data Bytes
	 * @param bits					result Bits
	 */
	static void terminateBits(int numDataBytes, BitArray bits) {
		int capacity = numDataBytes * 8;
		if (bits.getSize() > capacity) {
			throw new RuntimeException("data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity);
		}
		for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
			bits.appendBit(false);
		}
		// Append termination bits. See 8.4.8 of JISX0510:2004 (p.24) for
		// details.
		// If the last byte isn't 8-bit aligned, we'll add padding bits.
		int numBitsInLastByte = bits.getSize() & 0x07;
		if (numBitsInLastByte > 0) {
			for (int i = numBitsInLastByte; i < 8; i++) {
				bits.appendBit(false);
			}
		}
		// If we have more space, we'll fill the space with padding patterns
		// defined in 8.4.9 (p.24).
		int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
		for (int i = 0; i < numPaddingBytes; ++i) {
			bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);
		}
		if (bits.getSize() != capacity) {
			throw new RuntimeException("Bits size does not equal capacity");
		}
	}

	/**
	 * Get number of data bytes and number of error correction bytes for block
	 * id "blockID". Store the result in "numDataBytesInBlock", and
	 * "numECBytesInBlock". See table 12 in 8.5.1 of JISX0510:2004 (p.30)
	 * @param numTotalBytes			count of all Bytes
	 * @param numDataBytes			count of Data Bytes
	 * @param numRSBlocks			count of Block
	 * @param blockID				position of Block
	 * @param numDataBytesInBlock	List of Data in Block
	 * @param numECBytesInBlock		Error position in Block
	 */
	static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks,
			int blockID, int[] numDataBytesInBlock, int[] numECBytesInBlock) throws RuntimeException {
		if (blockID >= numRSBlocks) {
			throw new RuntimeException("Block ID too large");
		}
		// numRsBlocksInGroup2 = 196 % 5 = 1
		int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
		// numRsBlocksInGroup1 = 5 - 1 = 4
		int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
		// numTotalBytesInGroup1 = 196 / 5 = 39
		int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
		// numTotalBytesInGroup2 = 39 + 1 = 40
		int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
		// numDataBytesInGroup1 = 66 / 5 = 13
		int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
		// numDataBytesInGroup2 = 13 + 1 = 14
		int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
		// numEcBytesInGroup1 = 39 - 13 = 26
		int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
		// numEcBytesInGroup2 = 40 - 14 = 26
		int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
		// Sanity checks.
		// 26 = 26
		if (numEcBytesInGroup1 != numEcBytesInGroup2) {
			throw new RuntimeException("EC bytes mismatch");
		}
		// 5 = 4 + 1.
		if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
			throw new RuntimeException("RS blocks mismatch");
		}
		// 196 = (13 + 26) * 4 + (14 + 26) * 1
		if (numTotalBytes != ((numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1)
				+ ((numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2)) {
			throw new RuntimeException("Total bytes mismatch");
		}

		if (blockID < numRsBlocksInGroup1) {
			numDataBytesInBlock[0] = numDataBytesInGroup1;
			numECBytesInBlock[0] = numEcBytesInGroup1;
		} else {
			numDataBytesInBlock[0] = numDataBytesInGroup2;
			numECBytesInBlock[0] = numEcBytesInGroup2;
		}
	}

	/**
	 * Interleave "bits" with corresponding error correction bytes. On success,
	 * store the result in "result". The interleave rule is complicated. See 8.6
	 * of JISX0510:2004 (p.37) for details.
	 * @param bits			result Bits
	 * @param numTotalBytes	count of all Bytes
	 * @param numDataBytes	count of Data Bytes
	 * @param numRSBlocks	count of Block
	 * @return				result BitArray
	 */
	static BitArray interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes, int numRSBlocks)
			throws RuntimeException {

		// "bits" must have "getNumDataBytes" bytes of data.
		if (bits.getSizeInBytes() != numDataBytes) {
			throw new RuntimeException("Number of bits and data bytes does not match");
		}

		// Step 1. Divide data bytes into blocks and generate error correction
		// bytes for them. We'll
		// store the divided data bytes blocks and error correction bytes blocks
		// into "blocks".
		int dataBytesOffset = 0;
		int maxNumDataBytes = 0;
		int maxNumEcBytes = 0;

		// Since, we know the number of reedsolmon blocks, we can initialize the
		// vector with the number.
		byte[][] datablocks = new byte[numRSBlocks][0];
		byte[][] errorblocks = new byte[numRSBlocks][0];

		for (int i = 0; i < numRSBlocks; ++i) {
			int[] numDataBytesInBlock = new int[1];
			int[] numEcBytesInBlock = new int[1];
			getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, numDataBytesInBlock,
					numEcBytesInBlock);

			int size = numDataBytesInBlock[0];
			byte[] dataBytes = new byte[size];
			bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
			byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
			datablocks[i] = dataBytes;
			errorblocks[i] = ecBytes;
//			blocks.add(new BlockPair(dataBytes, ecBytes));

			maxNumDataBytes = Math.max(maxNumDataBytes, size);
			maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
			dataBytesOffset += numDataBytesInBlock[0];
		}
		if (numDataBytes != dataBytesOffset) {
			throw new RuntimeException("Data bytes does not match offset");
		}

		BitArray result = new BitArray();

		// First, place data blocks.
		for (int i = 0; i < maxNumDataBytes; ++i) {
			for(int z=0; z < datablocks.length;z++) {
				byte[] dataBytes = datablocks[z];
				if (i < dataBytes.length) {
					result.appendBits(dataBytes[i], 8);
				}
			}
		}
		// Then, place error correction blocks.
		for (int i = 0; i < maxNumEcBytes; ++i) {
			for(int z=0; z < errorblocks.length;z++) {
				byte[] ecBytes = errorblocks[z];
				if (i < ecBytes.length) {
					result.appendBits(ecBytes[i], 8);
				}
			}
		}
		if (numTotalBytes != result.getSizeInBytes()) { // Should be same.
			throw new RuntimeException(
					"Interleaving error: " + numTotalBytes + " and " + result.getSizeInBytes() + " differ.");
		}

		return result;
	}

	static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
		int numDataBytes = dataBytes.length;
		int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
		for (int i = 0; i < numDataBytes; i++) {
			toEncode[i] = dataBytes[i] & 0xFF;
		}
		new ReedSolomon(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

		byte[] ecBytes = new byte[numEcBytesInBlock];
		for (int i = 0; i < numEcBytesInBlock; i++) {
			ecBytes[i] = (byte) toEncode[numDataBytes + i];
		}
		return ecBytes;
	}

	/**
	 * Append mode info. On success, store the result in "bits".
	 * @param mode			Mode of encoding
	 * @param bits			target Bits
	 */
	static void appendModeInfo(Mode mode, BitArray bits) {
		bits.appendBits(mode.getBits(), 4);
	}

	/**
	 * Append length info. On success, store the result in "bits".
	 * @param numLetters	Number of Letter
	 * @param version		Version of QRCode
	 * @param mode			Mode of encoding
	 * @param bits			target Bits
	 *
		throws RuntimeException {
	 */
	static void appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) throws RuntimeException {
		int numBits = mode.getCharacterCountBits(version);
		if (numLetters >= (1 << numBits)) {
			throw new RuntimeException(numLetters + " is bigger than " + ((1 << numBits) - 1));
		}
		bits.appendBits(numLetters, numBits);
	}

	/**
	 * Append "bytes" in "mode" mode (encoding) into "bits". On success, store
	 * the result in "bits".
	 *
	 * @param content		The Content
	 * @param mode			Mode of Content example: Text or Binary
	 * @param bits			Result Bits
	 * @param encoding		Encoding String
	 * @throws RuntimeException	if wrong Mode
	 */
	static void appendBytes(String content, Mode mode, BitArray bits, String encoding) throws RuntimeException {
		if (mode == Mode.NUMERIC) {
			appendNumericBytes(content, bits);
		} else if (mode == Mode.ALPHANUMERIC) {
			appendAlphanumericBytes(content, bits);
		} else if (mode == Mode.BYTE) {
			append8BitBytes(content, bits, encoding);
		} else if (mode == Mode.KANJI) {
			appendKanjiBytes(content, bits);
		} else {
			throw new RuntimeException("Invalid mode: " + mode);
		}
	}

	static void appendNumericBytes(CharSequence content, BitArray bits) {
		int length = content.length();
		int i = 0;
		while (i < length) {
			int num1 = content.charAt(i) - '0';
			if (i + 2 < length) {
				// Encode three numeric letters in ten bits.
				int num2 = content.charAt(i + 1) - '0';
				int num3 = content.charAt(i + 2) - '0';
				bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
				i += 3;
			} else if (i + 1 < length) {
				// Encode two numeric letters in seven bits.
				int num2 = content.charAt(i + 1) - '0';
				bits.appendBits(num1 * 10 + num2, 7);
				i += 2;
			} else {
				// Encode one numeric letter in four bits.
				bits.appendBits(num1, 4);
				i++;
			}
		}
	}

	static void appendAlphanumericBytes(CharSequence content, BitArray bits) throws RuntimeException {
		int length = content.length();
		int i = 0;
		while (i < length) {
			int code1 = getAlphanumericCode(content.charAt(i));
			if (code1 == -1) {
				throw new RuntimeException();
			}
			if (i + 1 < length) {
				int code2 = getAlphanumericCode(content.charAt(i + 1));
				if (code2 == -1) {
					throw new RuntimeException();
				}
				// Encode two alphanumeric letters in 11 bits.
				bits.appendBits(code1 * 45 + code2, 11);
				i += 2;
			} else {
				// Encode one alphanumeric letter in six bits.
				bits.appendBits(code1, 6);
				i++;
			}
		}
	}

	static void append8BitBytes(String content, BitArray bits, String encoding) throws RuntimeException {
		byte[] bytes = null;
		try {
			if(encoding != null && encoding.length()>0) {
				bytes = content.getBytes(encoding);
			}
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}
		if(bytes != null) {
			for (byte b : bytes) {
				bits.appendBits(b, 8);
			}
		}
	}

	static void appendKanjiBytes(String content, BitArray bits) throws RuntimeException {
		byte[] bytes;
		try {
			bytes = content.getBytes("Shift_JIS");
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}
		int length = bytes.length;
		for (int i = 0; i < length; i += 2) {
			int byte1 = bytes[i] & 0xFF;
			int byte2 = bytes[i + 1] & 0xFF;
			int code = (byte1 << 8) | byte2;
			int subtracted = -1;
			if (code >= 0x8140 && code <= 0x9ffc) {
				subtracted = code - 0x8140;
			} else if (code >= 0xe040 && code <= 0xebbf) {
				subtracted = code - 0xc140;
			}
			if (subtracted == -1) {
				throw new RuntimeException("Invalid byte sequence");
			}
			int encoded = ((subtracted >> 8) * 0xc0) + (subtracted & 0xff);
			bits.appendBits(encoded, 13);
		}
	}
}
