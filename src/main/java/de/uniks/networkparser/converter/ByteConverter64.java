package de.uniks.networkparser.converter;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class ByteConverter64 extends ByteConverter {
//	private static final int BYTEPERATOM = 3;
	private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/','=' };
	private static final byte PADDING = 127;
	// ENCODE
	@Override
	public String toString(BufferedBuffer values) {
		int i,j,k;
		CharacterBuffer buffer=new CharacterBuffer();
		values.back();
		while(values.isEnd() == false) {
			i = values.getByte();
			j = values.getByte();
			k = values.getByte();
			buffer.with(pem_array[(i >>> 2 & 0x3F)]);
			buffer.with(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			buffer.with(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
			buffer.with(pem_array[(k & 0x3F)]);
		}
		return buffer.toString();
	}
	
	private static byte[] pem_convert_array = null;
	private void initPEMArray() {
		pem_convert_array = new byte[256];
		for (int i = 0; i < 255; i++) {
			pem_convert_array[i] = -1;
		}
		for (int i = 0; i < pem_array.length; i++) {
				pem_convert_array[pem_array[i]] = ((byte)i);
		}
		pem_convert_array['='] = PADDING;
	}
	
	@Override
	public byte[] decode(String value) {
		if(value == null) {
			return new byte[0];
		}
		if(pem_convert_array == null) {
			initPEMArray();
		}
		byte[] bytes = value.getBytes();
		int i;
		byte[] result = null;
		for(i=bytes.length - 1;i>=bytes.length - 3;i--) {
			byte c = pem_convert_array[value.charAt(i)];
			if(c == PADDING) {
				continue;
			}
			if(c == -1) {
				result = new byte[bytes.length*3/4];
			}
			break;
        }
		i++;
		if(result == null && (value.length() - i) >2) {
			result = new byte[bytes.length*3/4];
		} else {
			result = new byte[bytes.length*3/4 - bytes.length + i];
		}
		int pos=0;
		for(i=0;i<bytes.length-7;i+=4) {
			int n = pem_convert_array[(bytes[i+3] & 0xFF)];
			int m = pem_convert_array[(bytes[i+2] & 0xFF)];
			int k = pem_convert_array[(bytes[i+1] & 0xFF)];
			int j = pem_convert_array[(bytes[i+0] & 0xFF)];
			result[pos++] = (byte)(j << 2 & 0xFC | k >>> 4 & 0x3);
			result[pos++] = (byte)(k << 4 & 0xF0 | m >>> 2 & 0xF);
			result[pos++] = (byte)(m << 6 & 0xC0 | n & 0x3F);
		}
		int n = pem_convert_array[(bytes[i+3] & 0xFF)];
		int m = pem_convert_array[(bytes[i+2] & 0xFF)];
		int k = pem_convert_array[(bytes[i+1] & 0xFF)];
		int j = pem_convert_array[(bytes[i+0] & 0xFF)];
		result[pos++] = (byte)(j << 2 & 0xFC | k >>> 4 & 0x3);
		if(pos<result.length) {
			result[pos++] = (byte)(k << 4 & 0xF0 | m >>> 2 & 0xF);
			if(pos<result.length) {
				result[pos++] = (byte)(m << 6 & 0xC0 | n & 0x3F);
			}
		}
		return result;
	}
}
