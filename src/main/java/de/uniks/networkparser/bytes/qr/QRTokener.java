package de.uniks.networkparser.bytes.qr;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
	private final ReedSolomon rsDecoder = new ReedSolomon(GenericGF.QR_CODE_FIELD_256);

	/* The original table is defined in the table 5 of JISX0510:2004 (p.19). */
	private static final int[] ALPHANUMERIC_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, /* 0x00-0x0f */
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, /* 0x10-0x1f */
			36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, /* 0x20-0x2f */
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, /* 0x30-0x3f */
			-1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, /* 0x40-0x4f */
			25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, /* 0x50-0x5f */
	};

	static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

	/**
	 * <p>
	 * Convenience method that can decode a QR Code represented as a 2D array of
	 * booleans/bytes. "true" is taken to mean a black module.
	 * </p>
	 *
	 * @param values booleans or bytes representing white/black QR Code modules
	 * @return text and bytes encoded within the QR Code
	 */
	public DecoderResult decode(Object values) {
		if (values == null) {
			return null;
		}

		if (values instanceof BitMatrixParser) {
			BitMatrixParser parser = (BitMatrixParser) values;
			Version version = parser.readVersion();
			FormatInformation readFormatInformation = parser.readFormatInformation();
			if (readFormatInformation == null) {
				return null;
			}
			ErrorCorrectionLevel ecLevel = readFormatInformation.getErrorCorrectionLevel();

			/* Read codewords */
			byte[] codewords = parser.readCodewords();
			/* Separate into data blocks */
			DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version, ecLevel);
			if (dataBlocks == null) {
				return null;
			}

			/* Count total number of data bytes */
			int totalBytes = 0;
			for (DataBlock dataBlock : dataBlocks) {
				totalBytes += dataBlock.getNumDataCodewords();
			}
			byte[] resultBytes = new byte[totalBytes];
			int resultOffset = 0;

			/* Error-correct and copy data blocks together into a stream of bytes */
			for (DataBlock dataBlock : dataBlocks) {
				byte[] codewordBytes = dataBlock.getCodewords();
				int numDataCodewords = dataBlock.getNumDataCodewords();
				correctErrors(codewordBytes, numDataCodewords);
				for (int i = 0; i < numDataCodewords; i++) {
					resultBytes[resultOffset++] = codewordBytes[i];
				}
			}

			/* Decode the contents of that stream of bytes */
			return DecodedBitStreamParser.decode(resultBytes, version, ecLevel);
		}

		if (values instanceof BitMatrix) {
			BitMatrix bits = (BitMatrix) values;
			/* Construct a parser and read version, error-correction level */
			BitMatrixParser parser = new BitMatrixParser(bits);
			DecoderResult result = decode(parser);
			if (result != null) {
				return result;
			}

			/* Revert the bit matrix */
			parser.remask();

			/* Will be attempting a mirrored reading of the version and format info. */
			parser.setMirror(true);

			/* Preemptively read the version. */
			parser.readVersion();

			/* Preemptively read the format information. */
			parser.readFormatInformation();
			/*
			 * Since we're here, this means we have successfully detected some kind of
			 * version and format information when mirrored. This is a good sign, that the
			 * QR code may be mirrored, and we should try once more with a mirrored content.
			 */
			/* Prepare for a mirrored reading. */
			parser.mirror();

			result = decode(parser);
			/* Success! Notify the caller that the code was mirrored. */
			return result;
		}

		int dimension;
		BitMatrix bits = null;
		if (values instanceof byte[][]) {
			byte[][] image = (byte[][]) values;
			dimension = image.length;
			bits = new BitMatrix(dimension);
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					if (image[i][j] > 0) {
						bits.set(j, i);
					}
				}
			}
		} else if (values instanceof boolean[][]) {
			boolean[][] image = (boolean[][]) values;
			dimension = image.length;
			bits = new BitMatrix(dimension);
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					if (image[i][j]) {
						bits.set(j, i);
					}
				}
			}
		}
		if (bits == null) {
			return null;
		}
		return decode(bits);
	}

	/**
	 * <p>
	 * Given data and error-correction codewords received, possibly corrupted by
	 * errors, attempts to correct the errors in-place using Reed-Solomon error
	 * correction.
	 * </p>
	 *
	 * @param codewordBytes    data and error correction codewords
	 * @param numDataCodewords number of codewords that are data bytes
	 * @return success
	 */
	private boolean correctErrors(byte[] codewordBytes, int numDataCodewords) {
		if (codewordBytes == null) {
			return false;
		}
		int numCodewords = codewordBytes.length;
		/* First read into an array of ints */
		int[] codewordsInts = new int[numCodewords];
		for (int i = 0; i < numCodewords; i++) {
			codewordsInts[i] = codewordBytes[i] & 0xFF;
		}
		int numECCodewords = codewordBytes.length - numDataCodewords;
		try {
			rsDecoder.decode(codewordsInts, numECCodewords);
		} catch (Exception ignored) {
			return false; /* ChecksumInstance */
		}
		/*
		 * Copy back into array of bytes -- only need to worry about the bytes that were
		 * data We don't care about errors in the error-correction codewords
		 */
		for (int i = 0; i < numDataCodewords; i++) {
			codewordBytes[i] = (byte) codewordsInts[i];
		}
		return true;
	}

	/**
	 * ENCODER
	 *
	 * @param matrix Matrix to Encode
	 * @return Number The mask penalty calculation is complicated. See Table 21 of
	 *         JISX0510:2004 (p.45) for details. Basically it applies four rules and
	 *         summate all penalties.
	 */
	private static int calculateMaskPenalty(ByteMatrix matrix) {

		int penaltyRule1 = applyMaskPenaltyRule1Internal(matrix, true) + applyMaskPenaltyRule1Internal(matrix, false);

		return penaltyRule1 + applyMaskPenaltyRule2(matrix) + applyMaskPenaltyRule3(matrix)
				+ applyMaskPenaltyRule4(matrix);
	}

	/**
	 * encoding Content to QRCode
	 *
	 * @param content text to encode
	 * @param ecLevel error correction level to use
	 * @return representing the encoded QR code {@link QRCode}
	 */
	public QRCode encode(String content, ErrorCorrectionLevel ecLevel) {
		String encoding = DEFAULT_BYTE_MODE_ENCODING;
		/*
		 * Determine what character encoding has been specified by the caller, if any
		 * Pick an encoding mode appropriate for the content. Note that this will not
		 * attempt to use multiple modes / segments even if that were more efficient.
		 * Twould be nice.
		 */
		Mode mode = chooseMode(content, encoding);

		/*
		 * This will store the header information, like mode and length, as well as
		 * "header" segments like an ECI segment.
		 */
		BitArray headerBits = new BitArray();

		/* Append ECI segment if applicable */
		if (mode == Mode.BYTE && !DEFAULT_BYTE_MODE_ENCODING.equals(encoding)) {
			/* REMOVE OTHER ENCODING */
		}

		/* (With ECI in place,) Write the mode marker */
		appendModeInfo(mode, headerBits);

		/*
		 * Collect data within the main segment, separately, to count its size if
		 * needed. Don't add it to main payload yet.
		 */
		BitArray dataBits = new BitArray();
		appendBytes(content, mode, dataBits, encoding);

		/*
		 * Hard part: need to know version to know how many bits length takes. But need
		 * to know how many bits it takes to know version. First we take a guess at
		 * version by assuming version will be the minimum, 1:
		 */
		Version.getVersionForNumber(1);
		int provisionalBitsNeeded = headerBits.getSize() + mode.getCharacterCountBits(Version.getVersionForNumber(1))
				+ dataBits.getSize();
		Version provisionalVersion = chooseVersion(provisionalBitsNeeded, ecLevel);

		/*
		 * Use that guess to calculate the right version. I am still not sure this works
		 * in 100% of cases.
		 */
		int bitsNeeded = headerBits.getSize() + mode.getCharacterCountBits(provisionalVersion) + dataBits.getSize();
		Version version = chooseVersion(bitsNeeded, ecLevel);
		if (version == null) {
			return null;
		}

		BitArray headerAndDataBits = new BitArray();
		headerAndDataBits.appendBitArray(headerBits);
		/* Find "length" of main segment and write it */
		int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
		appendLengthInfo(numLetters, version, mode, headerAndDataBits);
		/* Put data together into the overall payload */
		headerAndDataBits.appendBitArray(dataBits);

		Version.ECB ecBlock = version.getECBlocksForLevel(ecLevel);
		int numDataBytes = version.getTotalCodewords() - ecBlock.getTotalECCodewords();

		/* Terminate the bits properly. */
		terminateBits(numDataBytes, headerAndDataBits);

		/* Interleave data bits with error correction code. */
		BitArray finalBits = interleaveWithECBytes(headerAndDataBits, version.getTotalCodewords(), numDataBytes,
				ecBlock.getNumBlocks());

		QRCode qrCode = new QRCode();

		qrCode.setECLevel(ecLevel);
		qrCode.setMode(mode);
		qrCode.setVersion(version);

		/* Choose the mask pattern and set to "qrCode". */
		int dimension = version.getDimensionForVersion();
		ByteMatrix matrix = new ByteMatrix(dimension, dimension);
		int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
		qrCode.setMaskPattern(maskPattern);

		/* Build the matrix and set it to "qrCode". */
		buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
		qrCode.setMatrix(matrix);

		return qrCode;
	}

	/**
	 * @return the code point of the table used in alphanumeric mode or -1 if there
	 *         is no corresponding code in the table.
	 * @param code AlphanumericCode
	 */
	static int getAlphanumericCode(int code) {
		if (code >= 0 && code < ALPHANUMERIC_TABLE.length) {
			return ALPHANUMERIC_TABLE[code];
		}
		return -1;
	}

	/**
	 * Choose the best mode by examining the content. Note that 'encoding' is used
	 * as a hint; if it is Shift_JIS, and the input is only double-byte Kanji, then
	 * we return {@link Mode#KANJI}.
	 *
	 * @param content  Contentstring
	 * @param encoding switch for Shift_JIS
	 * @return Mode
	 */
	private static Mode chooseMode(String content, String encoding) {
		if ("Shift_JIS".equals(encoding) && isOnlyDoubleByteKanji(content)) {
			/* Choose Kanji mode if all input are double-byte characters */
			return Mode.KANJI;
		}
		if (content == null) {
			return Mode.BYTE;
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
		if (content == null) {
			return false;
		}
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
			ByteMatrix matrix) {

		int minPenalty = Integer.MAX_VALUE; /* Lower penalty is better. */
		int bestMaskPattern = -1;
		/* We try all mask patterns to choose the best one. */
		for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
			buildMatrix(bits, ecLevel, version, maskPattern, matrix);
			int penalty = calculateMaskPenalty(matrix);
			if (penalty < minPenalty) {
				minPenalty = penalty;
				bestMaskPattern = maskPattern;
			}
		}
		return bestMaskPattern;
	}

	private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) {
		/* In the following comments, we use numbers of Version 7-H. */
		for (int versionNum = 1; versionNum <= 40; versionNum++) {
			Version version = Version.getVersionForNumber(versionNum);
			/* numBytes = 196 */
			int numBytes = version.getTotalCodewords();
			/* getNumECBytes = 130 */
			Version.ECB ecBlocks = version.getECBlocksForLevel(ecLevel);
			if (ecBlocks != null) {
				int numEcBytes = ecBlocks.getTotalECCodewords();
				/* getNumDataBytes = 196 - 130 = 66 */
				int numDataBytes = numBytes - numEcBytes;
				int totalInputBytes = (numInputBits + 7) / 8;
				if (numDataBytes >= totalInputBytes) {
					return version;
				}
			}
		}
		return null;
	}

	/**
	 * Terminate bits as described in 8.4.8 and 8.4.9 of JISX0510:2004 (p.24).
	 *
	 * @param numDataBytes count of Data Bytes
	 * @param bits         result Bits
	 * @return success
	 */
	static boolean terminateBits(int numDataBytes, BitArray bits) {
		int capacity = numDataBytes * 8;
		if (bits == null || bits.getSize() > capacity) {
			return false; /* "data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity */
		}
		for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
			bits.appendBit(false);
		}
		/*
		 * Append termination bits. See 8.4.8 of JISX0510:2004 (p.24) for details. If
		 * the last byte isn't 8-bit aligned, we'll add padding bits.
		 */
		int numBitsInLastByte = bits.getSize() & 0x07;
		if (numBitsInLastByte > 0) {
			for (int i = numBitsInLastByte; i < 8; i++) {
				bits.appendBit(false);
			}
		}
		/*
		 * If we have more space, we'll fill the space with padding patterns defined in
		 * 8.4.9 (p.24).
		 */
		int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
		for (int i = 0; i < numPaddingBytes; ++i) {
			bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);
		}
		return (bits.getSize() == capacity);
	}

	/**
	 * Get number of data bytes and number of error correction bytes for block id
	 * "blockID". Store the result in "numDataBytesInBlock", and
	 * "numECBytesInBlock". See table 12 in 8.5.1 of JISX0510:2004 (p.30)
	 *
	 * @param numTotalBytes       count of all Bytes
	 * @param numDataBytes        count of Data Bytes
	 * @param numRSBlocks         count of Block
	 * @param blockID             position of Block
	 * @param numDataBytesInBlock List of Data in Block
	 * @param numECBytesInBlock   Error position in Block
	 * @return success
	 */
	static boolean getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks,
			int blockID, int[] numDataBytesInBlock, int[] numECBytesInBlock) {
		if (blockID >= numRSBlocks) {
			return false; /* "Block ID too large"; */
		}
		/* numRsBlocksInGroup2 = 196 % 5 = 1 */
		int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
		/* numRsBlocksInGroup1 = 5 - 1 = 4 */
		int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
		/* numTotalBytesInGroup1 = 196 / 5 = 39 */
		int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
		/* numTotalBytesInGroup2 = 39 + 1 = 40 */
		int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
		/* numDataBytesInGroup1 = 66 / 5 = 13 */
		int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
		/* numDataBytesInGroup2 = 13 + 1 = 14 */
		int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
		/* numEcBytesInGroup1 = 39 - 13 = 26 */
		int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
		/* numEcBytesInGroup2 = 40 - 14 = 26 */
		int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
		/* Sanity checks. 26 = 26 */
		if (numEcBytesInGroup1 != numEcBytesInGroup2) {
			return false; /* "EC bytes mismatch" */
		}
		/* 5 = 4 + 1. */
		if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
			return false; /* "RS blocks mismatch"; */
		}
		/* 196 = (13 + 26) * 4 + (14 + 26) * 1 */
		if (numTotalBytes != ((numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1)
				+ ((numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2)) {
			return false; /* "Total bytes mismatch"; */
		}

		if (blockID < numRsBlocksInGroup1) {
			numDataBytesInBlock[0] = numDataBytesInGroup1;
			numECBytesInBlock[0] = numEcBytesInGroup1;
		} else {
			numDataBytesInBlock[0] = numDataBytesInGroup2;
			numECBytesInBlock[0] = numEcBytesInGroup2;
		}
		return true;
	}

	/**
	 * Interleave "bits" with corresponding error correction bytes. On success,
	 * store the result in "result". The interleave rule is complicated. See 8.6 of
	 * JISX0510:2004 (p.37) for details.
	 *
	 * @param bits          result Bits
	 * @param numTotalBytes count of all Bytes
	 * @param numDataBytes  count of Data Bytes
	 * @param numRSBlocks   count of Block
	 * @return result BitArray
	 */
	static BitArray interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes, int numRSBlocks) {
		/* "bits" must have "getNumDataBytes" bytes of data. */
		if (bits == null || bits.getSizeInBytes() != numDataBytes) {
			return null;
		}

		/*
		 * Step 1. Divide data bytes into blocks and generate error correction bytes for
		 * them. We'll store the divided data bytes blocks and error correction bytes
		 * blocks into "blocks".
		 */
		int dataBytesOffset = 0;
		int maxNumDataBytes = 0;
		int maxNumEcBytes = 0;

		/*
		 * Since, we know the number of reedsolmon blocks, we can initialize the vector
		 * with the number.
		 */
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

			maxNumDataBytes = Math.max(maxNumDataBytes, size);
			maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
			dataBytesOffset += numDataBytesInBlock[0];
		}
		if (numDataBytes != dataBytesOffset) {
			return null;
		}

		BitArray result = new BitArray();

		/* First, place data blocks. */
		for (int i = 0; i < maxNumDataBytes; ++i) {
			for (int z = 0; z < datablocks.length; z++) {
				byte[] dataBytes = datablocks[z];
				if (i < dataBytes.length) {
					result.appendBits(dataBytes[i], 8);
				}
			}
		}
		/* Then, place error correction blocks. */
		for (int i = 0; i < maxNumEcBytes; ++i) {
			for (int z = 0; z < errorblocks.length; z++) {
				byte[] ecBytes = errorblocks[z];
				if (i < ecBytes.length) {
					result.appendBits(ecBytes[i], 8);
				}
			}
		}
		if (numTotalBytes != result.getSizeInBytes()) { /* Should be same. */
			return null;
		}

		return result;
	}

	static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
		if (dataBytes == null) {
			return null;
		}
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
	 *
	 * @param mode Mode of encoding
	 * @param bits target Bits
	 * @return success
	 */
	static boolean appendModeInfo(Mode mode, BitArray bits) {
		if (mode == null || bits == null) {
			return false;
		}
		return bits.appendBits(mode.getBits(), 4);
	}

	/**
	 * Append length info. On success, store the result in "bits".
	 *
	 * @param numLetters Number of Letter
	 * @param version    Version of QRCode
	 * @param mode       Mode of encoding
	 * @param bits       target Bits
	 *
	 * @return success
	 */
	static boolean appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) {
		if (version == null || mode == null || bits == null) {
			return false;
		}
		int numBits = mode.getCharacterCountBits(version);
		if (numLetters >= (1 << numBits)) {
			return false;
		}
		bits.appendBits(numLetters, numBits);
		return true;
	}

	/**
	 * Append "bytes" in "mode" mode (encoding) into "bits". On success, store the
	 * result in "bits".
	 *
	 * @param content  The Content
	 * @param mode     Mode of Content example: Text or Binary
	 * @param bits     Result Bits
	 * @param encoding Encoding String
	 * @return success
	 */
	static boolean appendBytes(String content, Mode mode, BitArray bits, String encoding) {
		if (mode == Mode.NUMERIC) {
			return appendNumericBytes(content, bits);
		}
		if (mode == Mode.ALPHANUMERIC) {
			return appendAlphanumericBytes(content, bits);
		}
		if (mode == Mode.BYTE) {
			return append8BitBytes(content, bits, encoding);
		}
		if (mode == Mode.KANJI) {
			return appendKanjiBytes(content, bits);
		}
		return false;
	}

	static boolean appendNumericBytes(CharSequence content, BitArray bits) {
		if (content == null || bits == null) {
			return false;
		}
		int length = content.length();
		int i = 0;
		while (i < length) {
			int num1 = content.charAt(i) - '0';
			if (i + 2 < length) {
				/* Encode three numeric letters in ten bits. */
				int num2 = content.charAt(i + 1) - '0';
				int num3 = content.charAt(i + 2) - '0';
				bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
				i += 3;
			} else if (i + 1 < length) {
				/* Encode two numeric letters in seven bits. */
				int num2 = content.charAt(i + 1) - '0';
				bits.appendBits(num1 * 10 + num2, 7);
				i += 2;
			} else {
				/* Encode one numeric letter in four bits. */
				bits.appendBits(num1, 4);
				i++;
			}
		}
		return true;
	}

	static boolean appendAlphanumericBytes(CharSequence content, BitArray bits) {
		if (content == null || bits == null) {
			return false;
		}
		int length = content.length();
		int i = 0;
		while (i < length) {
			int code1 = getAlphanumericCode(content.charAt(i));
			if (code1 == -1) {
				return false;
			}
			if (i + 1 < length) {
				int code2 = getAlphanumericCode(content.charAt(i + 1));
				if (code2 == -1) {
					return false;
				}
				/* Encode two alphanumeric letters in 11 bits. */
				bits.appendBits(code1 * 45 + code2, 11);
				i += 2;
			} else {
				/* Encode one alphanumeric letter in six bits. */
				bits.appendBits(code1, 6);
				i++;
			}
		}
		return true;
	}

	static boolean append8BitBytes(String content, BitArray bits, String encoding) {
		if (content == null) {
			return false;
		}
		byte[] bytes = null;
		try {
			if (encoding != null && encoding.length() > 0) {
				bytes = content.getBytes(encoding);
			}
		} catch (UnsupportedEncodingException uee) {
			return false;
		}
		if (bytes != null) {
			for (byte b : bytes) {
				bits.appendBits(b, 8);
			}
		}
		return true;
	}

	static boolean appendKanjiBytes(String content, BitArray bits) {
		if (content == null || bits == null) {
			return false;
		}
		byte[] bytes;
		try {
			bytes = content.getBytes("Shift_JIS");
		} catch (UnsupportedEncodingException uee) {
			return false;
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
				return false;
			}
			int encoded = ((subtracted >> 8) * 0xc0) + (subtracted & 0xff);
			bits.appendBits(encoded, 13);
		}
		return true;
	}

	/* MaskUtil - Penalty weights from section 6.8.2.1 */
	private static final int N1 = 3;
	private static final int N2 = 3;
	private static final int N3 = 40;
	private static final int N4 = 10;

	/**
	 * Helper function for applyMaskPenaltyRule1. We need this for doing this
	 * calculation in both vertical and horizontal orders respectively.
	 *
	 * @param matrix       encoding Matrix
	 * @param isHorizontal switch for Limit
	 * @return penalty
	 */
	private static int applyMaskPenaltyRule1Internal(ByteMatrix matrix, boolean isHorizontal) {
		if (matrix == null) {
			return -1;
		}
		int penalty = 0;
		int iLimit = isHorizontal ? matrix.getHeight() : matrix.getWidth();
		int jLimit = isHorizontal ? matrix.getWidth() : matrix.getHeight();
		byte[][] array = matrix.getArray();
		for (int i = 0; i < iLimit; i++) {
			int numSameBitCells = 0;
			int prevBit = -1;
			for (int j = 0; j < jLimit; j++) {
				int bit = isHorizontal ? array[i][j] : array[j][i];
				if (bit == prevBit) {
					numSameBitCells++;
				} else {
					if (numSameBitCells >= 5) {
						penalty += N1 + (numSameBitCells - 5);
					}
					numSameBitCells = 1; /* Include the cell itself. */
					prevBit = bit;
				}
			}
			if (numSameBitCells >= 5) {
				penalty += N1 + (numSameBitCells - 5);
			}
		}
		return penalty;
	}

	/**
	 * Apply mask penalty rule 2 and return the penalty. Find 2x2 blocks with the
	 * same color and give penalty to them. This is actually equivalent to the
	 * spec's rule, which is to find MxN blocks and give a penalty proportional to
	 * (M-1)x(N-1), because this is the number of 2x2 blocks inside such a block.
	 *
	 * @param matrix ByteMatrix
	 * @return penalty
	 */
	static int applyMaskPenaltyRule2(ByteMatrix matrix) {
		if (matrix == null) {
			return -1;
		}
		int penalty = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height - 1; y++) {
			for (int x = 0; x < width - 1; x++) {
				int value = array[y][x];
				if (value == array[y][x + 1] && value == array[y + 1][x] && value == array[y + 1][x + 1]) {
					penalty++;
				}
			}
		}
		return N2 * penalty;
	}

	/**
	 * Apply mask penalty rule 3 and return the penalty. Find consecutive runs of
	 * 1:1:3:1:1:4 starting with black, or 4:1:1:3:1:1 starting with white, and give
	 * penalty to them. If we find patterns like 000010111010000, we give penalty
	 * once.
	 *
	 * @param matrix ByteMatrix
	 * @return penalty
	 */
	private static int applyMaskPenaltyRule3(ByteMatrix matrix) {
		if (matrix == null) {
			return -1;
		}
		int numPenalties = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				byte[] arrayY = array[y]; /* We can at least optimize this access */
				if (x + 6 < width && arrayY[x] == 1 && arrayY[x + 1] == 0 && arrayY[x + 2] == 1 && arrayY[x + 3] == 1
						&& arrayY[x + 4] == 1 && arrayY[x + 5] == 0 && arrayY[x + 6] == 1
						&& (isWhiteHorizontal(arrayY, x - 4, x) || isWhiteHorizontal(arrayY, x + 7, x + 11))) {
					numPenalties++;
				}
				if (y + 6 < height && array[y][x] == 1 && array[y + 1][x] == 0 && array[y + 2][x] == 1
						&& array[y + 3][x] == 1 && array[y + 4][x] == 1 && array[y + 5][x] == 0 && array[y + 6][x] == 1
						&& (isWhiteVertical(array, x, y - 4, y) || isWhiteVertical(array, x, y + 7, y + 11))) {
					numPenalties++;
				}
			}
		}
		return numPenalties * N3;
	}

	private static boolean isWhiteHorizontal(byte[] rowArray, int from, int to) {
		if (rowArray == null) {
			return false;
		}
		from = Math.max(from, 0);
		to = Math.min(to, rowArray.length);
		for (int i = from; i < to; i++) {
			if (rowArray[i] == 1) {
				return false;
			}
		}
		return true;
	}

	private static boolean isWhiteVertical(byte[][] array, int col, int from, int to) {
		if (array == null) {
			return false;
		}
		from = Math.max(from, 0);
		to = Math.min(to, array.length);
		for (int i = from; i < to; i++) {
			if (array[i][col] == 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Apply mask penalty rule 4 and return the penalty. Calculate the ratio of dark
	 * cells and give penalty if the ratio is far from 50%. It gives 10 penalty for
	 * 5% distance.
	 *
	 * @param matrix ByteMatrix
	 * @return penalty
	 */
	private static int applyMaskPenaltyRule4(ByteMatrix matrix) {
		if (matrix == null) {
			return -1;
		}
		int numDarkCells = 0;
		byte[][] array = matrix.getArray();
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		for (int y = 0; y < height; y++) {
			byte[] arrayY = array[y];
			for (int x = 0; x < width; x++) {
				if (arrayY[x] == 1) {
					numDarkCells++;
				}
			}
		}
		int numTotalCells = matrix.getHeight() * matrix.getWidth();
		if (numTotalCells == 0) {
			return -1;
		}
		int fivePercentVariances = Math.abs(numDarkCells * 2 - numTotalCells) * 10 / numTotalCells;
		return fivePercentVariances * N4;
	}

	/* MatrixUtil */
	private static final int[][] POSITION_DETECTION_PATTERN = { { 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1 }, };

	private static final int[][] POSITION_ADJUSTMENT_PATTERN = { { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1 },
			{ 1, 0, 1, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1 }, };

	/*
	 * From Appendix E. Table 1, JIS0510X:2004 (p 71). The table was double-checked
	 * by komatsu.
	 */
	private static final int[][] POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE = { { -1, -1, -1, -1, -1, -1, -1 }, /*
																													 * Version
																													 * 1
																													 */
			{ 6, 18, -1, -1, -1, -1, -1 }, /* Version 2 */
			{ 6, 22, -1, -1, -1, -1, -1 }, /* Version 3 */
			{ 6, 26, -1, -1, -1, -1, -1 }, /* Version 4 */
			{ 6, 30, -1, -1, -1, -1, -1 }, /* Version 5 */
			{ 6, 34, -1, -1, -1, -1, -1 }, /* Version 6 */
			{ 6, 22, 38, -1, -1, -1, -1 }, /* Version 7 */
			{ 6, 24, 42, -1, -1, -1, -1 }, /* Version 8 */
			{ 6, 26, 46, -1, -1, -1, -1 }, /* Version 9 */
			{ 6, 28, 50, -1, -1, -1, -1 }, /* Version 10 */
			{ 6, 30, 54, -1, -1, -1, -1 }, /* Version 11 */
			{ 6, 32, 58, -1, -1, -1, -1 }, /* Version 12 */
			{ 6, 34, 62, -1, -1, -1, -1 }, /* Version 13 */
			{ 6, 26, 46, 66, -1, -1, -1 }, /* Version 14 */
			{ 6, 26, 48, 70, -1, -1, -1 }, /* Version 15 */
			{ 6, 26, 50, 74, -1, -1, -1 }, /* Version 16 */
			{ 6, 30, 54, 78, -1, -1, -1 }, /* Version 17 */
			{ 6, 30, 56, 82, -1, -1, -1 }, /* Version 18 */
			{ 6, 30, 58, 86, -1, -1, -1 }, /* Version 19 */
			{ 6, 34, 62, 90, -1, -1, -1 }, /* Version 20 */
			{ 6, 28, 50, 72, 94, -1, -1 }, /* Version 21 */
			{ 6, 26, 50, 74, 98, -1, -1 }, /* Version 22 */
			{ 6, 30, 54, 78, 102, -1, -1 }, /* Version 23 */
			{ 6, 28, 54, 80, 106, -1, -1 }, /* Version 24 */
			{ 6, 32, 58, 84, 110, -1, -1 }, /* Version 25 */
			{ 6, 30, 58, 86, 114, -1, -1 }, /* Version 26 */
			{ 6, 34, 62, 90, 118, -1, -1 }, /* Version 27 */
			{ 6, 26, 50, 74, 98, 122, -1 }, /* Version 28 */
			{ 6, 30, 54, 78, 102, 126, -1 }, /* Version 29 */
			{ 6, 26, 52, 78, 104, 130, -1 }, /* Version 30 */
			{ 6, 30, 56, 82, 108, 134, -1 }, /* Version 31 */
			{ 6, 34, 60, 86, 112, 138, -1 }, /* Version 32 */
			{ 6, 30, 58, 86, 114, 142, -1 }, /* Version 33 */
			{ 6, 34, 62, 90, 118, 146, -1 }, /* Version 34 */
			{ 6, 30, 54, 78, 102, 126, 150 }, /* Version 35 */
			{ 6, 24, 50, 76, 102, 128, 154 }, /* Version 36 */
			{ 6, 28, 54, 80, 106, 132, 158 }, /* Version 37 */
			{ 6, 32, 58, 84, 110, 136, 162 }, /* Version 38 */
			{ 6, 26, 54, 82, 110, 138, 166 }, /* Version 39 */
			{ 6, 30, 58, 86, 114, 142, 170 }, /* Version 40 */
	};

	/* Type info cells at the left top corner. */
	private static final int[][] TYPE_INFO_COORDINATES = { { 8, 0 }, { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 4 }, { 8, 5 },
			{ 8, 7 }, { 8, 8 }, { 7, 8 }, { 5, 8 }, { 4, 8 }, { 3, 8 }, { 2, 8 }, { 1, 8 }, { 0, 8 }, };

	/* From Appendix D in JISX0510:2004 (p. 67) */
	private static final int VERSION_INFO_POLY = 0x1f25; /* 1 1111 0010 0101 */

	/* From Appendix C in JISX0510:2004 (p.65). */
	private static final int TYPE_INFO_POLY = 0x537;
	private static final int TYPE_INFO_MASK_PATTERN = 0x5412;

	/**
	 * Build 2D matrix of QR Code from "dataBits" with "ecLevel", "version" and
	 * "getMaskPattern". On success, store the result in "matrix" and return true.
	 *
	 * @param dataBits    BitArray
	 * @param ecLevel     ErrorCorrectionLevel
	 * @param version     Version
	 * @param maskPattern Mask
	 * @param matrix      ByteMatrix
	 * @return success
	 */
	private static boolean buildMatrix(BitArray dataBits, ErrorCorrectionLevel ecLevel, Version version,
			int maskPattern, ByteMatrix matrix) {
		if (dataBits == null || ecLevel == null || version == null || matrix == null) {
			return false;
		}
		matrix.clear((byte) -1);

		embedBasicPatterns(version, matrix);
		/* Type information appear with any version. */
		embedTypeInfo(ecLevel, maskPattern, matrix);
		/* Version info appear if version >= 7. */
		maybeEmbedVersionInfo(version, matrix);
		/* Data should be embedded at end. */
		embedDataBits(dataBits, maskPattern, matrix);
		return true;
	}

	/**
	 * Embed basic patterns. On success, modify the matrix and return true. The
	 * basic patterns are: - Position detection patterns - Timing patterns - Dark
	 * dot at the left bottom corner - Position adjustment patterns, if need be
	 *
	 * @param version Version
	 * @param matrix  ByteMatrix
	 */
	private static void embedBasicPatterns(Version version, ByteMatrix matrix) {
		/* Let's get started with embedding big squares at corners. */
		embedPositionDetectionPatternsAndSeparators(matrix);
		/* Then, embed the dark dot at the left bottom corner. */
		embedDarkDotAtLeftBottomCorner(matrix);

		/* Position adjustment patterns appear if version >= 2. */
		maybeEmbedPositionAdjustmentPatterns(version, matrix);
		/* Timing patterns should be embedded after position adj. patterns. */
		embedTimingPatterns(matrix);
	}

	private static boolean embedTimingPatterns(ByteMatrix matrix) {
		if (matrix == null) {
			return false;
		}
		/*
		 * -8 is for skipping position detection patterns (size 7), and two
		 * horizontal/vertical separation patterns (size 1). Thus, 8 = 7 + 1.
		 */
		for (int i = 8; i < matrix.getWidth() - 8; ++i) {
			int bit = (i + 1) % 2;
			/* Horizontal line. */
			if (matrix.get(i, 6) == -1) {
				matrix.set(i, 6, bit);
			}
			/* Vertical line. */
			if (matrix.get(6, i) == -1) {
				matrix.set(6, i, bit);
			}
		}
		return true;
	}

	/**
	 * Embed the lonely dark dot at left bottom corner. JISX0510:2004 (p.46)
	 *
	 * @param matrix Matrix to Check
	 * @return success
	 */
	private static boolean embedDarkDotAtLeftBottomCorner(ByteMatrix matrix) {
		if (matrix == null) {
			return false;
		}
		if (matrix.get(8, matrix.getHeight() - 8) == 0) {
			return false;
		}
		matrix.set(8, matrix.getHeight() - 8, 1);
		return true;
	}

	/**
	 * Embed position adjustment patterns if need be.
	 *
	 * @param version The Verison
	 * @param matrix  ByteMatrix
	 */
	private static void maybeEmbedPositionAdjustmentPatterns(Version version, ByteMatrix matrix) {
		if (version == null || matrix == null) {
			return;
		}
		if (version.getVersionNumber() < 2) { /* The patterns appear if version >= 2 */
			return;
		}
		int index = version.getVersionNumber() - 1;
		int[] coordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index];
		int numCoordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index].length;
		for (int i = 0; i < numCoordinates; ++i) {
			for (int j = 0; j < numCoordinates; ++j) {
				int y = coordinates[i];
				int x = coordinates[j];
				if (x == -1 || y == -1) {
					continue;
				}
				/* If the cell is unset, we embed the position adjustment pattern here. */
				if (matrix.get(x, y) == -1) {
					/*
					 * -2 is necessary since the x/y coordinates point to the center of the pattern,
					 * not the left top corner.
					 */
					embedPositionAdjustmentPattern(x - 2, y - 2, matrix);
				}
			}
		}
	}

	/**
	 * Note that we cannot unify the function with embedPositionDetectionPattern()
	 * despite they are almost identical, since we cannot write a function that
	 * takes 2D arrays in different sizes in C/C++. We should live with the fact.
	 *
	 * @param xStart x Pos Start
	 * @param yStart y Pos Start
	 * @param matrix ByteMatrix
	 * @return success
	 */
	private static boolean embedPositionAdjustmentPattern(int xStart, int yStart, ByteMatrix matrix) {
		if (matrix == null) {
			return false;
		}
		for (int y = 0; y < 5; ++y) {
			for (int x = 0; x < 5; ++x) {
				matrix.set(xStart + x, yStart + y, POSITION_ADJUSTMENT_PATTERN[y][x]);
			}
		}
		return true;
	}

	/**
	 * Embed type information. On success, modify the matrix.
	 *
	 * @param ecLevel     Level
	 * @param maskPattern Mask
	 * @param matrix      ByteMatrix
	 */
	private static void embedTypeInfo(ErrorCorrectionLevel ecLevel, int maskPattern, ByteMatrix matrix) {
		BitArray typeInfoBits = new BitArray();
		makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);

		for (int i = 0; i < typeInfoBits.getSize(); ++i) {
			/*
			 * Place bits in LSB to MSB order. LSB (least significant bit) is the last value
			 * in "typeInfoBits".
			 */
			boolean bit = typeInfoBits.get(typeInfoBits.getSize() - 1 - i);

			/* Type info bits at the left top corner. See 8.9 of JISX0510:2004 (p.46). */
			int x1 = TYPE_INFO_COORDINATES[i][0];
			int y1 = TYPE_INFO_COORDINATES[i][1];
			matrix.set(x1, y1, bit);

			if (i < 8) {
				/* Right top corner. */
				int x2 = matrix.getWidth() - i - 1;
				int y2 = 8;
				matrix.set(x2, y2, bit);
			} else {
				/* Left bottom corner. */
				int x2 = 8;
				int y2 = matrix.getHeight() - 7 + (i - 8);
				matrix.set(x2, y2, bit);
			}
		}
	}

	/**
	 * Make bit vector of type information. On success, store the result in "bits"
	 * and return true. Encode error correction level and mask pattern. See 8.9 of
	 * JISX0510:2004 (p.45) for details.
	 *
	 * @param ecLevel     Level
	 * @param maskPattern Mask
	 * @param bits        BitArray
	 * @return success
	 */
	static boolean makeTypeInfoBits(ErrorCorrectionLevel ecLevel, int maskPattern, BitArray bits) {
		if (ecLevel == null || QRCode.isValidMaskPattern(maskPattern) == false) {
			return false;
		}
		int typeInfo = (ecLevel.getBits() << 3) | maskPattern;
		bits.appendBits(typeInfo, 5);

		int bchCode = calculateBCHCode(typeInfo, TYPE_INFO_POLY);
		bits.appendBits(bchCode, 10);

		BitArray maskBits = new BitArray();
		maskBits.appendBits(TYPE_INFO_MASK_PATTERN, 15);
		bits.xor(maskBits);

		return (bits.getSize() == 15); /* Just in case. */
	}

	/**
	 * Calculate BCH (Bose-Chaudhuri-Hocquenghem) code for "value" using polynomial
	 * "poly". The BCH code is used for encoding type information and version
	 * information. Example: Calculation of version information of 7. f(x) is
	 * created from 7. - 7 = 000111 in 6 bits - f(x) = x^2 + x^1 + x^0 g(x) is given
	 * by the standard (p. 67) - g(x) = x^12 + x^11 + x^10 + x^9 + x^8 + x^5 + x^2 +
	 * 1 Multiply f(x) by x^(18 - 6) - f'(x) = f(x) * x^(18 - 6) - f'(x) = x^14 +
	 * x^13 + x^12 Calculate the remainder of f'(x) / g(x) x^2
	 * __________________________________________________ g(x) )x^14 + x^13 + x^12
	 * x^14 + x^13 + x^12 + x^11 + x^10 + x^7 + x^4 + x^2
	 * -------------------------------------------------- x^11 + x^10 + x^7 + x^4 +
	 * x^2
	 *
	 * The remainder is x^11 + x^10 + x^7 + x^4 + x^2 Encode it in binary:
	 * 110010010100 The return value is 0xc94 (1100 1001 0100)
	 *
	 * Since all coefficients in the polynomials are 1 or 0, we can do the
	 * calculation by bit operations. We don't care if cofficients are positive or
	 * negative.
	 *
	 * @param value Value
	 * @param poly  poly
	 * @return calculated Value
	 */
	static int calculateBCHCode(int value, int poly) {
		if (poly == 0) {
			return -1;
		}
		/*
		 * If poly is "1 1111 0010 0101" (version info poly), msbSetInPoly is 13. We'll
		 * subtract 1 from 13 to make it 12.
		 */
		int msbSetInPoly = findMSBSet(poly);
		value <<= msbSetInPoly - 1;
		/* Do the division business using exclusive-or operations. */
		while (findMSBSet(value) >= msbSetInPoly) {
			value ^= poly << (findMSBSet(value) - msbSetInPoly);
		}
		/* Now the "value" is the remainder (i.e. the BCH code) */
		return value;
	}

	/*
	 * Return the position of the most significant bit set (to one) in the "value".
	 * The most significant bit is position 32. If there is no bit set, return 0.
	 * Examples: - findMSBSet(0) => 0 - findMSBSet(1) => 1 - findMSBSet(255) => 8
	 */
	static int findMSBSet(int value) {
		int numDigits = 0;
		while (value != 0) {
			value >>>= 1;
			++numDigits;
		}
		return numDigits;
	}

	/**
	 * Embed version information if need be. On success, modify the matrix and
	 * return true. See 8.10 of JISX0510:2004 (p.47) for how to embed version
	 * information.
	 *
	 * @param version Version
	 * @param matrix  ByteMatrix
	 */
	private static void maybeEmbedVersionInfo(Version version, ByteMatrix matrix) {
		if (version == null || matrix == null || version.getVersionNumber() < 7) { /* Version info is necessary if */
			/* version >= 7. */
			return; /* Don't need version info. */
		}
		BitArray versionInfoBits = new BitArray();
		makeVersionInfoBits(version, versionInfoBits);

		int bitIndex = 6 * 3 - 1; /* It will decrease from 17 to 0. */
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 3; ++j) {
				/* Place bits in LSB (least significant bit) to MSB order. */
				boolean bit = versionInfoBits.get(bitIndex);
				bitIndex--;
				/* Left bottom corner. */
				matrix.set(i, matrix.getHeight() - 11 + j, bit);
				/* Right bottom corner. */
				matrix.set(matrix.getHeight() - 11 + j, i, bit);
			}
		}
	}

	/**
	 * Make bit vector of version information. On success, store the result in
	 * "bits" and return true. See 8.10 of JISX0510:2004 (p.45) for details.
	 *
	 * @param version Version
	 * @param bits    BitArray
	 * @return success
	 */
	static boolean makeVersionInfoBits(Version version, BitArray bits) {
		if (version == null || bits == null) {
			return false;
		}
		bits.appendBits(version.getVersionNumber(), 6);
		int bchCode = calculateBCHCode(version.getVersionNumber(), VERSION_INFO_POLY);
		bits.appendBits(bchCode, 12);

		return (bits.getSize() == 18); /* Just in case. */
	}

	/**
	 * Embed "dataBits" using "getMaskPattern". On success, modify the matrix and
	 * return true. For debugging purposes, it skips masking process if
	 * "getMaskPattern" is -1. See 8.7 of JISX0510:2004 (p.38) for how to embed data
	 * bits.
	 *
	 * @param dataBits    DataBits
	 * @param maskPattern Mask
	 * @param matrix      ByteMatrix
	 * @return success
	 */
	private static boolean embedDataBits(BitArray dataBits, int maskPattern, ByteMatrix matrix) {
		if (dataBits == null || matrix == null) {
			return false;
		}
		int bitIndex = 0;
		int direction = -1;
		/* Start from the right bottom cell. */
		int x = matrix.getWidth() - 1;
		int y = matrix.getHeight() - 1;
		while (x > 0) {
			/* Skip the vertical timing pattern. */
			if (x == 6) {
				x -= 1;
			}
			while (y >= 0 && y < matrix.getHeight()) {
				for (int i = 0; i < 2; ++i) {
					int xx = x - i;
					/* Skip the cell if it's not empty. */
					if (matrix.get(xx, y) != -1) {
						continue;
					}
					boolean bit;
					if (bitIndex < dataBits.getSize()) {
						bit = dataBits.get(bitIndex);
						++bitIndex;
					} else {
						/*
						 * Padding bit. If there is no bit left, we'll fill the left cells with 0, as
						 * described in 8.4.9 of JISX0510:2004 (p. 24).
						 */
						bit = false;
					}

					/* Skip masking if mask_pattern is -1. */
					if (maskPattern != -1 && getDataMaskBit(maskPattern, xx, y)) {
						bit = !bit;
					}
					matrix.set(xx, y, bit);
				}
				y += direction;
			}
			direction = -direction; /* Reverse the direction. */
			y += direction;
			x -= 2; /* Move to the left. */
		}
		/* All bits should be consumed. */
		return bitIndex == dataBits.getSize();
	}

	/**
	 * Return the mask bit for "getMaskPattern" at "x" and "y". See 8.8 of
	 * JISX0510:2004 for mask pattern conditions.
	 *
	 * @param maskPattern Mask 0-7
	 * @param x           x Position
	 * @param y           y Position
	 * @return success
	 */
	static boolean getDataMaskBit(int maskPattern, int x, int y) {
		int intermediate;
		int temp;
		switch (maskPattern) {
		case 0:
			intermediate = (y + x) & 0x1;
			break;
		case 1:
			intermediate = y & 0x1;
			break;
		case 2:
			intermediate = x % 3;
			break;
		case 3:
			intermediate = (y + x) % 3;
			break;
		case 4:
			intermediate = ((y / 2) + (x / 3)) & 0x1;
			break;
		case 5:
			temp = y * x;
			intermediate = (temp & 0x1) + (temp % 3);
			break;
		case 6:
			temp = y * x;
			intermediate = ((temp & 0x1) + (temp % 3)) & 0x1;
			break;
		case 7:
			temp = y * x;
			intermediate = ((temp % 3) + ((y + x) & 0x1)) & 0x1;
			break;
		default:
			return false;
		}
		return intermediate == 0;
	}

	private static void embedPositionDetectionPattern(int xStart, int yStart, ByteMatrix matrix) {
		if (matrix != null) {
			for (int y = 0; y < 7; ++y) {
				for (int x = 0; x < 7; ++x) {
					matrix.set(xStart + x, yStart + y, POSITION_DETECTION_PATTERN[y][x]);
				}
			}
		}
	}

	private static boolean embedHorizontalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) {
		if (matrix == null) {
			return false;
		}
		for (int x = 0; x < 8; ++x) {
			if (matrix.get(xStart + x, yStart) != -1) {
				return false;
			}
			matrix.set(xStart + x, yStart, 0);
		}
		return true;
	}

	/**
	 * Embed position detection patterns and surrounding vertical/horizontal
	 * separators.
	 *
	 * @param matrix ByteMatrix
	 */
	private static void embedPositionDetectionPatternsAndSeparators(ByteMatrix matrix) {
		/* Embed three big squares at corners. */
		if (matrix == null) {
			return;
		}
		int pdpWidth = POSITION_DETECTION_PATTERN[0].length;
		/* Left top corner. */
		embedPositionDetectionPattern(0, 0, matrix);
		/* Right top corner. */
		embedPositionDetectionPattern(matrix.getWidth() - pdpWidth, 0, matrix);
		/* Left bottom corner. */
		embedPositionDetectionPattern(0, matrix.getWidth() - pdpWidth, matrix);

		/* Embed horizontal separation patterns around the squares. */
		int hspWidth = 8;
		/* Left top corner. */
		embedHorizontalSeparationPattern(0, hspWidth - 1, matrix);
		/* Right top corner. */
		embedHorizontalSeparationPattern(matrix.getWidth() - hspWidth, hspWidth - 1, matrix);
		/* Left bottom corner. */
		embedHorizontalSeparationPattern(0, matrix.getWidth() - hspWidth, matrix);

		/* Embed vertical separation patterns around the squares. */
		int vspSize = 7;
		/* Left top corner. */
		embedVerticalSeparationPattern(vspSize, 0, matrix);
		/* Right top corner. */
		embedVerticalSeparationPattern(matrix.getHeight() - vspSize - 1, 0, matrix);
		/* Left bottom corner. */
		embedVerticalSeparationPattern(vspSize, matrix.getHeight() - vspSize, matrix);
	}

	private static boolean embedVerticalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) {
		if (matrix == null) {
			return false;
		}
		for (int y = 0; y < 7; ++y) {
			if (matrix.get(xStart, yStart + y) != -1) {
				return false;
			}
			matrix.set(xStart, yStart + y, 0);
		}
		return true;
	}
}
