package de.uniks.jism.bytes;


public class ByteConverterBinary extends ByteConverter {
	/**
	 * To Binary string.
	 *
	 * @param bytes the bytes
	 * @return the string
	 */
	@Override
	public String toString(byte[] values, int size) {
		StringBuilder sb=new StringBuilder();
		for(int z=0;z<size;z++){
			byte number = values[z];
			char[] bits = new char[]{'0','0','0','0','0','0','0','0'};
			int i=7;
			while (number > 0) {
				bits[i] = (char) (48+(number % 2));
				number = (byte) (number / 2);
				i--;
		   }
			sb.append(new String(bits));
		}
		return sb.toString();
	}
	
	/**
	 * To byte string.
	 *
	 * @param hexString the hex string
	 * @return the byte[]
	 */
	@Override
	public byte[] decode(String value) {
		byte[] out = new byte[value.length() / 8];

		int n = value.length();

		for (int i = 0; i < n;) {
			int charText=0;
			for(int z=0;z<8;z++){
				charText = charText << ((byte)(value.charAt(i++)-48));
			}
			// now just shift the high order nibble and add them together
			out[i / 8] = (byte) charText;
		}
		return out;
	}

}
