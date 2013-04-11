package de.uniks.jism.bytes;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

public class ByteConverterHex extends ByteConverter {
	/**
	 * To hex string.
	 * 
	 * @param bytes the bytes
	 * @return the string
	 */
	@Override
	public String toString(byte[] values, int size) {
		String hexVal = "0123456789ABCDEF";

		StringBuilder returnValue = new StringBuilder(size * 2);
		if (values != null) {
			for (int i = 0; i < size; i++) {
				int value = values[i];
				if (value < 0) {
					value += 256;
				}
				returnValue.append("" + hexVal.charAt(value / 16)
						+ hexVal.charAt(value % 16));
			}
		}
		return returnValue.toString();
	}

	/**
	 * To byte string.
	 * 
	 * @param hexString
	 *            the hex string
	 * @return the byte[]
	 */
	@Override
	public byte[] decode(String value) {
		String hexVal = "0123456789ABCDEF";
		byte[] out = new byte[value.length() / 2];

		int n = value.length();

		for (int i = 0; i < n; i += 2) {
			// make a bit representation in an int of the hex value
			int hn = hexVal.indexOf(value.charAt(i));
			int ln = hexVal.indexOf(value.charAt(i + 1));

			// now just shift the high order nibble and add them together
			out[i / 2] = (byte) ((hn << 4) | ln);
		}
		return out;
	}

}
