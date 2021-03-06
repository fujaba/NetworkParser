package de.uniks.networkparser.bytes.qr;

/**
 * <p>
 * Encapsulates a block of data within a QR Code. QR Codes may split their data
 * into multiple blocks, each of which is a unit of data and error-correction
 * codewords. Each is represented by an instance of this class.
 * </p>
 *
 * @author Sean Owen
 */
final class DataBlock {

	private final int numDataCodewords;
	private final byte[] codewords;

	private DataBlock(int numDataCodewords, byte[] codewords) {
		this.numDataCodewords = numDataCodewords;
		this.codewords = codewords;
	}

	/**
	 * <p>
	 * When QR Codes use multiple data blocks, they are actually interleaved. That
	 * is, the first byte of data block 1 to n is written, then the second bytes,
	 * and so on. This method will separate the data into original blocks.
	 * </p>
	 *
	 * @param rawCodewords bytes as read directly from the QR Code
	 * @param version      version of the QR Code
	 * @param ecLevel      error-correction level of the QR Code
	 * @return DataBlocks containing original bytes, "de-interleaved" from
	 *         representation in the QR Code
	 */
	static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version, ErrorCorrectionLevel ecLevel) {
		if (rawCodewords == null || version == null) {
			return null;
		}
		if (rawCodewords.length != version.getTotalCodewords()) {
			return null;
		}

		/*
		 * Figure out the number and size of data blocks used by this version and error
		 * correction level
		 */
		Version.ECB ecBlock = version.getECBlocksForLevel(ecLevel);
		if (ecBlock == null) {
			return null;
		}
		/* First count the total number of data blocks */
		int totalBlocks = ecBlock.getNumBlocks();

		/*
		 * Now establish DataBlocks of the appropriate size and number of data codewords
		 */
		DataBlock[] result = new DataBlock[totalBlocks];
		int numResultBlocks = 0;
		Version.ECB element = ecBlock;
		while (element != null) {
			for (int i = 0; i < element.getCount(); i++) {
				int numDataCodewords = element.getDataCodewords();
				int numBlockCodewords = element.getECCodewordsPerBlock() + numDataCodewords;
				result[numResultBlocks++] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
			}
			element = element.next();
		}

		/*
		 * All blocks have the same amount of data, except that the last n (where n may
		 * be 0) have 1 more byte. Figure out where these start.
		 */
		int shorterBlocksTotalCodewords = result[0].codewords.length;
		int longerBlocksStartAt = result.length - 1;
		while (longerBlocksStartAt >= 0) {
			int numCodewords = result[longerBlocksStartAt].codewords.length;
			if (numCodewords == shorterBlocksTotalCodewords) {
				break;
			}
			longerBlocksStartAt--;
		}
		longerBlocksStartAt++;

		int shorterBlocksNumDataCodewords = shorterBlocksTotalCodewords - ecBlock.getECCodewordsPerBlock();
		/*
		 * The last elements of result may be 1 element longer; first fill out as many
		 * elements as all of them have
		 */
		int rawCodewordsOffset = 0;
		for (int i = 0; i < shorterBlocksNumDataCodewords; i++) {
			for (int j = 0; j < numResultBlocks; j++) {
				result[j].codewords[i] = rawCodewords[rawCodewordsOffset++];
			}
		}
		/* Fill out the last data block in the longer ones */
		for (int j = longerBlocksStartAt; j < numResultBlocks; j++) {
			result[j].codewords[shorterBlocksNumDataCodewords] = rawCodewords[rawCodewordsOffset++];
		}
		/* Now add in error correction blocks */
		int max = result[0].codewords.length;
		for (int i = shorterBlocksNumDataCodewords; i < max; i++) {
			for (int j = 0; j < numResultBlocks; j++) {
				int iOffset = j < longerBlocksStartAt ? i : i + 1;
				result[j].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
			}
		}
		return result;
	}

	int getNumDataCodewords() {
		return numDataCodewords;
	}

	byte[] getCodewords() {
		return codewords;
	}

}
