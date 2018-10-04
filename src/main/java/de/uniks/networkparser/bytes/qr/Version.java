/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uniks.networkparser.bytes.qr;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

/**
 * See ISO 18004:2006 Annex D
 *
 * @author Sean Owen
 */
public final class Version {

	/**
	 * See ISO 18004:2006 Annex D. Element i represents the raw version bits that
	 * specify version i + 7
	 */
	private static final int[] VERSION_DECODE_INFO = { 0x07C94, 0x085BC, 0x09A99, 0x0A4D3, 0x0BBF6, 0x0C762, 0x0D847,
			0x0E60D, 0x0F928, 0x10B78, 0x1145D, 0x12A17, 0x13532, 0x149A6, 0x15683, 0x168C9, 0x177EC, 0x18EC4, 0x191E1,
			0x1AFAB, 0x1B08E, 0x1CC1A, 0x1D33F, 0x1ED75, 0x1F250, 0x209D5, 0x216F0, 0x228BA, 0x2379F, 0x24B0B, 0x2542E,
			0x26A64, 0x27541, 0x28C69 };

	private static Version[] VERSIONS;

	private final int versionNumber;
	private final int[] alignmentPatternCenters;
	private final ECB ecBlocks1;
	private final ECB ecBlocks2;
	private final ECB ecBlocks3;
	private final ECB ecBlocks4;
	private final int totalCodewords;

	private Version(int versionNumber, int[] alignmentPatternCenters, String json) {
		this.versionNumber = versionNumber;
		this.alignmentPatternCenters = alignmentPatternCenters;

		JsonObject jsonObject = new JsonObject();
		jsonObject.withFlag(JsonObject.ALLOWDUPLICATE);
		jsonObject.withValue(json);
		this.ecBlocks1 = getECBS(jsonObject, 0);
		this.ecBlocks2 = getECBS(jsonObject, 1);
		this.ecBlocks3 = getECBS(jsonObject, 2);
		this.ecBlocks4 = getECBS(jsonObject, 3);

		int total = 0;
		ECB element = ecBlocks1;
		int codeword = element.getECCodewordsPerBlock();
		while (element != null) {
			total += element.getCount() * (element.getDataCodewords() + codeword);
			element = element.next();
		}
		this.totalCodewords = total;
	}

	ECB getECBS(JsonObject jsonObject, int index) {
		if (jsonObject == null || jsonObject.size() < 1) {
			return new ECB(-1, -1, -1);
		}
		int ecCodeBlocks = Integer.valueOf(jsonObject.getKeyByIndex(index));
		JsonArray array = (JsonArray) jsonObject.getValueByIndex(index);
		ECB prev = null;
		ECB first = null;
		for (int z = 0; z < array.size(); z += 2) {
			ECB element = new ECB(ecCodeBlocks, (Integer) array.get(z), (Integer) array.get(z + 1));
			if (prev != null) {
				prev.withNext(element);
			}
			if (first == null) {
				first = element;
			}
			prev = element;
		}
		return first;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public int[] getAlignmentPatternCenters() {
		return alignmentPatternCenters;
	}

	public int getTotalCodewords() {
		return totalCodewords;
	}

	public int getDimensionForVersion() {
		return 17 + 4 * versionNumber;
	}

	public ECB getECBlocksForLevel(ErrorCorrectionLevel ecLevel) {
		if (ecLevel.ordinal() == 0) {
			return ecBlocks1;
		}
		if (ecLevel.ordinal() == 1) {
			return ecBlocks2;
		}
		if (ecLevel.ordinal() == 2) {
			return ecBlocks3;
		}
		if (ecLevel.ordinal() == 3) {
			return ecBlocks4;
		}
		return null;
	}

	/**
	 * <p>
	 * Deduces version information purely from QR Code dimensions.
	 * </p>
	 *
	 * @param dimension dimension in modules
	 * @return Version for a QR Code of that dimension
	 * @throws RuntimeException if dimension is not 1 mod 4
	 */
	public static Version getProvisionalVersionForDimension(int dimension) {
		if (dimension % 4 != 1) {
			throw new RuntimeException("FormatException");
		}
		try {
			return getVersionForNumber((dimension - 17) >> 2);
		} catch (IllegalArgumentException iae) {
			throw new RuntimeException("FormatException");
		}
	}

	public static Version getVersionForNumber(int versionNumber) {
		if (versionNumber < 1 || versionNumber > 40) {
			throw new IllegalArgumentException();
		}
		if (VERSIONS == null) {
			/**
			 * See ISO 18004:2006 6.5.1 Table 9
			 * 
			 * @return Version-Array with Build Polynom
			 */
			VERSIONS = new Version[] { new Version(1, new int[] {}, "{7:[1,19], 10:[1,16], 13:[1,13], 17:[1,9]}"),
					new Version(2, new int[] { 6, 18 }, "{10:[1,34], 16:[1,28], 22:[1,22], 28:[1,16]}"),
					new Version(3, new int[] { 6, 22 }, "{15:[1,55], 26:[1,44], 18:[2,17], 22:[2,13]}"),
					new Version(4, new int[] { 6, 26 }, "{20:[1,80], 18:[2,32], 26:[2,24], 16:[4,9]}"),
					new Version(5, new int[] { 6, 30 }, "{26:[1,108], 24:[2,43], 18:[2,15,2,16], 22:[2,11,2,12]}"),
					new Version(6, new int[] { 6, 34 }, "{18:[2,68], 16:[4,27], 24:[4,19], 28:[4,15]}"),
					new Version(7, new int[] { 6, 22, 38 }, "{20:[2,78], 18:[4,31], 18:[2,14,4,15], 26:[4,13,1,14]}"),
					new Version(8, new int[] { 6, 24, 42 },
							"{24:[2, 97], 22:[2, 38, 2, 39],22:[4, 18, 2, 19],26:[4, 14, 2, 15]}"),
					new Version(9, new int[] { 6, 26, 46 },
							"{30:[2, 116],22:[3, 36,2, 37], 20:[4, 16, 4, 17], 24:[4, 12, 4, 13]}"),
					new Version(10, new int[] { 6, 28, 50 },
							"{18:[2, 68, 2, 69], 26:[4, 43, 1, 44],24: [6, 19, 2, 20], 28:[6, 15,2, 16]}"),
					new Version(11, new int[] { 6, 30, 54 },
							"{20:[4, 81], 30:[1, 50,4, 51], 28:[4, 22,4, 23], 24:[3, 12,8, 13]}"),
					new Version(12, new int[] { 6, 32, 58 },
							"{24:[2, 92,2, 93],22:[6, 36,2, 37], 26:[4, 20,6, 21], 28:[7, 14,4, 15]}"),
					new Version(13, new int[] { 6, 34, 62 },
							"{26:[4, 107], 22:[8, 37,1, 38], 24:[8, 20,4, 21], 22:[12, 11,4, 12]}"),
					new Version(14, new int[] { 6, 26, 46, 66 },
							"{30:[3, 115,1, 116], 24:[4, 40,5, 41], 20:[11, 16,5, 17], 24:[11, 12,5, 13]}"),
					new Version(15, new int[] { 6, 26, 48, 70 },
							"{22:[5, 87,1, 88], 24:[5, 41,5, 42], 30:[5, 24,7, 25], 24:[11, 12,7, 13]}"),
					new Version(16, new int[] { 6, 26, 50, 74 },
							"{24:[5, 98,1, 99], 28:[7, 45,3, 46], 24:[15, 19,2, 20], 30:[3, 15,13, 16]}"),
					new Version(17, new int[] { 6, 30, 54, 78 },
							"{28:[1, 107,5, 108], 28:[10, 46,1, 47], 28:[1, 22,15, 23], 28:[2, 14,17, 15]}"),
					new Version(18, new int[] { 6, 30, 56, 82 },
							"{30:[5, 120,1, 121], 26:[9, 43,4, 44], 28:[17, 22,1, 23], 28:[2, 14,19, 15]}"),
					new Version(19, new int[] { 6, 30, 58, 86 },
							"{28:[3, 113,4, 114], 26:[3, 44,11, 45], 26:[17, 21,4, 22], 26:[9, 13,16, 14]}"),
					new Version(20, new int[] { 6, 34, 62, 90 },
							"{28:[3, 107,5, 108], 26:[3, 41,13, 42], 30:[15, 24,5, 25], 28:[15, 15,10, 16]}"),
					new Version(21, new int[] { 6, 28, 50, 72, 94 },
							"{28:[4, 116,4, 117], 26:[17, 42], 28:[17, 22,6, 23], 30:[19, 16,6, 17]}"),
					new Version(22, new int[] { 6, 26, 50, 74, 98 },
							"{28:[2, 111,7, 112], 28:[17, 46], 30:[7, 24,16, 25], 24:[34, 13]}"),
					new Version(23, new int[] { 6, 30, 54, 78, 102 },
							"{30:[4, 121,5, 122], 28:[4, 47,14, 48], 30:[11, 24,14, 25], 30:[16, 15,14, 16]}"),
					new Version(24, new int[] { 6, 28, 54, 80, 106 },
							"{30:[6, 117,4, 118], 28:[6, 45,14, 46], 30:[11, 24,16, 25], 30:[30, 16,2, 17]}"),
					new Version(25, new int[] { 6, 32, 58, 84, 110 },
							"{26:[8, 106,4, 107], 28:[8, 47,13, 48], 30:[7, 24,22, 25], 30:[22, 15,13, 16]}"),
					new Version(26, new int[] { 6, 30, 58, 86, 114 },
							"{28:[10, 114,2, 115], 28:[19, 46,4, 47], 28:[28, 22,6, 23], 30:[33, 16,4, 17]}"),
					new Version(27, new int[] { 6, 34, 62, 90, 118 },
							"{30:[8, 122,4, 123], 28:[22, 45,3, 46], 30:[8, 23,26, 24], 30:[12, 15,28, 16]}"),
					new Version(28, new int[] { 6, 26, 50, 74, 98, 122 },
							"{30:[3, 117,10, 118], 28:[3, 45,23, 46], 30:[4, 24,31, 25], 30:[11, 15,31, 16]}"),
					new Version(29, new int[] { 6, 30, 54, 78, 102, 126 },
							"{30:[7, 116,7, 117], 28:[21, 45,7, 46], 30:[1, 23,37, 24], 30:[19, 15,26, 16]}"),
					new Version(30, new int[] { 6, 26, 52, 78, 104, 130 },
							"{30:[5, 115,10, 116], 28:[19, 47,10, 48], 30:[15, 24,25, 25], 30:[23, 15,25, 16]}"),
					new Version(31, new int[] { 6, 30, 56, 82, 108, 134 },
							"{30:[13, 115,3, 116], 28:[2, 46,29, 47], 30:[42, 24,1, 25], 30:[23, 15,28, 16]}"),
					new Version(32, new int[] { 6, 34, 60, 86, 112, 138 },
							"{30:[17, 115], 28:[10, 46,23, 47], 30:[10, 24,35, 25], 30:[19, 15,35, 16]}"),
					new Version(33, new int[] { 6, 30, 58, 86, 114, 142 },
							"{30:[17, 115,1, 116], 28:[14, 46,21, 47], 30:[29, 24,19, 25], 30:[11, 15,46, 16]}"),
					new Version(34, new int[] { 6, 34, 62, 90, 118, 146 },
							"{30:[13, 115,6, 116], 28:[14, 46,23, 47], 30:[44, 24,7, 25], 30:[59, 16,1, 17]}"),
					new Version(35, new int[] { 6, 30, 54, 78, 102, 126, 150 },
							"{30:[12, 121,7, 122], 28:[12, 47,26, 48], 30:[39, 24,14, 25], 30:[22, 15,41, 16]}"),
					new Version(36, new int[] { 6, 24, 50, 76, 102, 128, 154 },
							"{30:[6, 121,14, 122], 28:[6, 47,34, 48], 30:[46, 24,10, 25], 30:[2, 15,64, 16]}"),
					new Version(37, new int[] { 6, 28, 54, 80, 106, 132, 158 },
							"{30:[17, 122,4, 123], 28:[29, 46,14, 47], 30:[49, 24,10, 25], 30:[24, 15,46, 16]}"),
					new Version(38, new int[] { 6, 32, 58, 84, 110, 136, 162 },
							"{30:[4, 122,18, 123], 28:[13, 46,32, 47], 30:[48, 24,14, 25], 30:[42, 15,32, 16]}"),
					new Version(39, new int[] { 6, 26, 54, 82, 110, 138, 166 },
							"{30:[20, 117,4, 118], 28:[40, 47,7, 48], 30:[43, 24,22, 25], 30:[10, 15,67, 16]}"),
					new Version(40, new int[] { 6, 30, 58, 86, 114, 142, 170 },
							"{30:[19, 118,6, 119], 28:[18, 47,31, 48], 30:[34, 24,34, 25], 30:[20, 15,61, 16]}") };
		}
		return VERSIONS[versionNumber - 1];
	}

	static Version decodeVersionInformation(int versionBits) {
		int bestDifference = Integer.MAX_VALUE;
		int bestVersion = 0;
		for (int i = 0; i < VERSION_DECODE_INFO.length; i++) {
			int targetVersion = VERSION_DECODE_INFO[i];
			// Do the version info bits match exactly? done.
			if (targetVersion == versionBits) {
				return getVersionForNumber(i + 7);
			}
			// Otherwise see if this is the closest to a real version info bit
			// string
			// we have seen so far
			int bitsDifference = FormatInformation.numBitsDiffering(versionBits, targetVersion);
			if (bitsDifference < bestDifference) {
				bestVersion = i + 7;
				bestDifference = bitsDifference;
			}
		}
		// We can tolerate up to 3 bits of error since no two version info
		// codewords will
		// differ in less than 8 bits.
		if (bestDifference <= 3) {
			return getVersionForNumber(bestVersion);
		}
		// If we didn't find a close enough match, fail
		return null;
	}

	/**
	 * See ISO 18004:2006 Annex E
	 */
	BitMatrix buildFunctionPattern() {
		int dimension = getDimensionForVersion();
		BitMatrix bitMatrix = new BitMatrix(dimension);

		// Top left finder pattern + separator + format
		bitMatrix.setRegion(0, 0, 9, 9);
		// Top right finder pattern + separator + format
		bitMatrix.setRegion(dimension - 8, 0, 8, 9);
		// Bottom left finder pattern + separator + format
		bitMatrix.setRegion(0, dimension - 8, 9, 8);

		// Alignment patterns
		int max = alignmentPatternCenters.length;
		for (int x = 0; x < max; x++) {
			int i = alignmentPatternCenters[x] - 2;
			for (int y = 0; y < max; y++) {
				if ((x == 0 && (y == 0 || y == max - 1)) || (x == max - 1 && y == 0)) {
					// No alignment patterns near the three finder paterns
					continue;
				}
				bitMatrix.setRegion(alignmentPatternCenters[y] - 2, i, 5, 5);
			}
		}

		// Vertical timing pattern
		bitMatrix.setRegion(6, 9, 1, dimension - 17);
		// Horizontal timing pattern
		bitMatrix.setRegion(9, 6, dimension - 17, 1);

		if (versionNumber > 6) {
			// Version info, top right
			bitMatrix.setRegion(dimension - 11, 0, 3, 6);
			// Version info, bottom left
			bitMatrix.setRegion(0, dimension - 11, 6, 3);
		}

		return bitMatrix;
	}

	public String toString() {
		return String.valueOf(versionNumber);
	}

	/**
	 * <p>
	 * Encapsualtes the parameters for one error-correction block in one symbol
	 * version. This includes the number of data codewords, and the number of times
	 * a block with these parameters is used consecutively in the QR code version's
	 * format.
	 * </p>
	 */
	public static final class ECB {
		private final int count;
		private final int dataCodewords;
		private final int ecCodewordsPerBlock;
		private ECB next;

		ECB(int ecCodewordsPerBlock, int count, int dataCodewords) {
			this.ecCodewordsPerBlock = ecCodewordsPerBlock;
			this.count = count;
			this.dataCodewords = dataCodewords;
		}

		public int getECCodewordsPerBlock() {
			return ecCodewordsPerBlock;
		}

		public ECB next() {
			return next;
		}

		public int getCount() {
			return count;
		}

		public int getDataCodewords() {
			return dataCodewords;
		}

		public ECB withNext(ECB value) {
			this.next = value;
			return this;
		}

		public int getNumBlocks() {
			int total = 0;
			ECB element = this;
			while (element != null) {
				total += element.getCount();
				element = element.next();
			}
			return total;
		}

		public int getTotalECCodewords() {
			return ecCodewordsPerBlock * getNumBlocks();
		}
	}
}
