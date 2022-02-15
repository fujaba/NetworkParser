package de.uniks.networkparser.bytes;

import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;

/**
 * Converter for Byte To String or HEX.
 *
 * @author Stefan Lindel
 */
public class ByteConverter implements Converter {
    /** To simple string.
     * @param values the bytes
     * @return the string
     */
    public String toString(BufferedBuffer values) {
        if (values == null) {
            return null;
        }
        StringBuilder returnValue = new StringBuilder(values.length());
        for (int i = 0; i < values.length(); i++) {
            returnValue.append(values.charAt(i));
        }
        return returnValue.toString();
    }

	/**
	 * To string.
	 *
	 * @param values the values
	 * @return the string
	 */
	public String toString(byte... values) {
		ByteBuffer buffer = new ByteBuffer().with(values);
		return this.toString(buffer);
	}

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @return the string
	 */
	@Override
	public String encode(BaseItem entity) {
		ByteBuffer buffer;
		if (entity instanceof ByteItem) {
			buffer = ((ByteItem) entity).getBytes(true);
		} else if(entity instanceof BufferedBuffer){
		    return toString((BufferedBuffer) entity);
		} else {
			byte[] array = entity.toString().getBytes(Charset.forName(BaseItem.ENCODING));
			buffer = new ByteBuffer().with(array);
		}
		if (buffer != null) {
			return toString(buffer);
		}
		return "";
	}

	/**
     * To byte string.
     *
     * @param value the hex string
     * @return the byte[]
     */
    public byte[] decode(CharSequence value) {
        if (value == null) {
            return null;
        }
        byte[] out = new byte[value.length()];
        int n = value.length();

        for (int i = 0; i < n; i++) {
            out[i] = (byte) value.charAt(i);
        }
        return out;
    }
}
