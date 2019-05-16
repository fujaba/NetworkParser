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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;

/**
 * <p>
 * QR Codes can encode text as bits in one of several modes, and can use
 * multiple modes in one QR Code. This class decodes the bits back into text.
 * </p>
 *
 * <p>
 * See ISO 18004:2006, 6.4.3 - 6.4.7
 * </p>
 *
 * @author Sean Owen
 */
final class DecodedBitStreamParser {

	public static final String SHIFT_JIS = "SJIS";
	public static final String GB2312 = "GB2312";

	/**
	 * See ISO 18004:2006, 6.4.4 Table 5
	 */
	private static final char[] ALPHANUMERIC_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z', ' ', '$', '%', '*', '+', '-', '.', '/', ':' };
	private static final int GB2312_SUBSET = 1;

	private DecodedBitStreamParser() {
	}

	static DecoderResult decode(byte[] bytes, Version version, ErrorCorrectionLevel ecLevel) {
		BitArray bits = new BitArray(bytes);
		CharacterBuffer result = new CharacterBuffer().withLength(50);
		List<byte[]> byteSegments = new ArrayList<byte[]>(1);
		int symbolSequence = -1;
		int parityData = -1;

		boolean fc1InEffect = false;
		Mode mode;
		do {
			// While still another segment to read...
			if (bits.available() < 4) {
				// OK, assume we're done. Really, a TERMINATOR mode should
				// have been recorded here
				mode = Mode.TERMINATOR;
			} else {
				mode = Mode.forBits(bits.readBits(4)); // mode is encoded by
														// 4 bits
			}
			if (mode != Mode.TERMINATOR) {
				if (mode == Mode.FNC1_FIRST_POSITION || mode == Mode.FNC1_SECOND_POSITION) {
					// We do little with FNC1 except alter the parsed result
					// a bit according to the spec
					fc1InEffect = true;
				} else if (mode == Mode.STRUCTURED_APPEND) {
					if (bits.available() < 16) {
						throw new RuntimeException("FormatException");
					}
					// sequence number and parity is added later to the
					// result metadata
					// Read next 8 bits (symbol sequence #) and 8 bits
					// (parity data), then continue
					symbolSequence = bits.readBits(8);
					parityData = bits.readBits(8);
				} else if (mode == Mode.ECI) {
					// Count doesn't apply to ECI
//						int value = parseECIValue(bits);
				} else {
					// First handle Hanzi mode which does not start with
					// character count
					if (mode == Mode.HANZI) {
						// chinese mode contains a sub set indicator right
						// after mode indicator
						int subset = bits.readBits(4);
						int countHanzi = bits.readBits(mode.getCharacterCountBits(version));
						if (subset == GB2312_SUBSET) {
							decodeHanziSegment(bits, result, countHanzi);
						}
					} else {
						// "Normal" QR code modes:
						// How many characters will follow, encoded in this
						// mode?
						int count = bits.readBits(mode.getCharacterCountBits(version));
						if (mode == Mode.NUMERIC) {
							if (decodeNumericSegment(bits, result, count) == false) {
								return null;
							}
						} else if (mode == Mode.ALPHANUMERIC) {
							if (decodeAlphanumericSegment(bits, result, count, fc1InEffect) == false) {
								return null;
							}
						} else if (mode == Mode.BYTE) {
							if (decodeByteSegment(bits, result, count, byteSegments) == false) {
								return null;
							}
						} else if (mode == Mode.KANJI) {
							if (decodeKanjiSegment(bits, result, count) == false) {
								return null;
							}
						} else {
							return null;
						}
					}
				}
			}
		} while (mode != Mode.TERMINATOR);

		return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments,
				ecLevel == null ? null : ecLevel.toString(), symbolSequence, parityData);
	}

	/**
	 * See specification GBT 18284-2000
	 * 
	 * @param bits   result bits
	 * @param result result String
	 * @param count  count of value
	 * @return success
	 */
	private static boolean decodeHanziSegment(BitArray bits, CharacterBuffer result, int count) {
		// Don't crash trying to read more bits than we have available.
		if (bits == null || count * 13 > bits.available()) {
			return false;
		}

		// Each character will require 2 bytes. Read the characters as 2-byte
		// pairs
		// and decode as GB2312 afterwards
		byte[] buffer = new byte[2 * count];
		int offset = 0;
		while (count > 0) {
			// Each 13 bits encodes a 2-byte character
			int twoBytes = bits.readBits(13);
			int assembledTwoBytes = ((twoBytes / 0x060) << 8) | (twoBytes % 0x060);
			if (assembledTwoBytes < 0x003BF) {
				// In the 0xA1A1 to 0xAAFE range
				assembledTwoBytes += 0x0A1A1;
			} else {
				// In the 0xB0A1 to 0xFAFE range
				assembledTwoBytes += 0x0A6A1;
			}
			buffer[offset] = (byte) ((assembledTwoBytes >> 8) & 0xFF);
			buffer[offset + 1] = (byte) (assembledTwoBytes & 0xFF);
			offset += 2;
			count--;
		}

		try {
			result.append(new String(buffer, GB2312));
		} catch (UnsupportedEncodingException ignored) {
			return false;
		}
		return true;
	}

	private static boolean decodeKanjiSegment(BitArray bits, CharacterBuffer result, int count) {
		// Don't crash trying to read more bits than we have available.
		if (bits == null || count * 13 > bits.available()) {
			return false;
		}

		// Each character will require 2 bytes. Read the characters as 2-byte
		// pairs
		// and decode as Shift_JIS afterwards
		byte[] buffer = new byte[2 * count];
		int offset = 0;
		while (count > 0) {
			// Each 13 bits encodes a 2-byte character
			int twoBytes = bits.readBits(13);
			int assembledTwoBytes = ((twoBytes / 0x0C0) << 8) | (twoBytes % 0x0C0);
			if (assembledTwoBytes < 0x01F00) {
				// In the 0x8140 to 0x9FFC range
				assembledTwoBytes += 0x08140;
			} else {
				// In the 0xE040 to 0xEBBF range
				assembledTwoBytes += 0x0C140;
			}
			buffer[offset] = (byte) (assembledTwoBytes >> 8);
			buffer[offset + 1] = (byte) assembledTwoBytes;
			offset += 2;
			count--;
		}
		// Shift_JIS may not be supported in some environments:
		try {
			result.append(new String(buffer, SHIFT_JIS));
		} catch (UnsupportedEncodingException ignored) {
			return false;
		}
		return true;
	}

	private static boolean decodeByteSegment(BitArray bits, CharacterBuffer result, int count,
			Collection<byte[]> byteSegments) {
		// Don't crash trying to read more bits than we have available.
		if (bits == null) {
			return false;
		}
		if (8 * count > bits.available()) {
			return false;
		}

		byte[] readBytes = new byte[count];
		for (int i = 0; i < count; i++) {
			readBytes[i] = (byte) bits.readBits(8);
		}
		result.append(new String(readBytes, Charset.forName(BaseItem.ENCODING)));
		byteSegments.add(readBytes);
		return true;
	}

	private static char toAlphaNumericChar(int value) {
		if (value<0 || value >= ALPHANUMERIC_CHARS.length) {
			return (char)-1;
		}
		return ALPHANUMERIC_CHARS[value];
	}

	private static boolean decodeAlphanumericSegment(BitArray bits, CharacterBuffer result, int count,
			boolean fc1InEffect) {
		// Read two characters at a time
		if (bits == null) {
			return false;
		}
		int start = result.length();
		char character;
		while (count > 1) {
			if (bits.available() < 11) {
				return false;
			}
			int nextTwoCharsBits = bits.readBits(11);
			character = toAlphaNumericChar(nextTwoCharsBits / 45);
			if(character<0) {
				return false;
			}
			result.with(character);
			character = toAlphaNumericChar(nextTwoCharsBits % 45);
			if(character<0) {
				return false;
			}
			count -= 2;
		}
		if (count == 1) {
			// special case: one character left
			if (bits.available() < 6) {
				return false;
			}
			character = toAlphaNumericChar(bits.readBits(6));
			if(character<0) {
				return false;
			}
		}
		// See section 6.4.8.1, 6.4.8.2
		if (fc1InEffect) {
			// We need to massage the result a bit if in an FNC1 mode:
			for (int i = start; i < result.length(); i++) {
				if (result.charAt(i) == '%') {
					if (i < result.length() - 1 && result.charAt(i + 1) == '%') {
						// %% is rendered as %
						result.remove(i + 1);
					} else {
						// In alpha mode, % should be converted to FNC1
						// separator 0x1D
						result.setCharAt(i, (char) 0x1D);
					}
				}
			}
		}
		return true;
	}

	private static boolean decodeNumericSegment(BitArray bits, CharacterBuffer result, int count) {
		// Read three digits at a time
		if(bits == null || result == null) {
			return false;
		}
		char character;  
		while (count >= 3) {
			// Each 10 bits encodes three digits
			if (bits.available() < 10) {
				return false;
			}
			int threeDigitsBits = bits.readBits(10);
			if (threeDigitsBits >= 1000) {
				return false;
			}
			character = toAlphaNumericChar(threeDigitsBits / 100);
			if(character<0) {
				return false;
			}
			result.with(character);
			character = toAlphaNumericChar((threeDigitsBits / 10) % 10);
			if(character<0) {
				return false;
			}
			result.with(character);
			character = toAlphaNumericChar(threeDigitsBits % 10);
			if(character<0) {
				return false;
			}
			result.with(character);
			count -= 3;
		}
		if (count == 2) {
			// Two digits left over to read, encoded in 7 bits
			if (bits.available() < 7) {
				return false;
			}
			int twoDigitsBits = bits.readBits(7);
			if (twoDigitsBits >= 100) {
				return false;
			}
			character = toAlphaNumericChar(twoDigitsBits / 10);
			if(character<0) {
				return false;
			}
			result.with(character);
			character = toAlphaNumericChar(twoDigitsBits % 10);
			if(character<0) {
				return false;
			}
			result.with(character);
		} else if (count == 1) {
			// One digit left over to read
			if (bits.available() < 4) {
				return false;
			}
			int digitBits = bits.readBits(4);
			if (digitBits >= 10) {
				return false;
			}
			character = toAlphaNumericChar(digitBits);
			if(character<0) {
				return false;
			}
			result.with(character);
		}
		return true;
	}
}
