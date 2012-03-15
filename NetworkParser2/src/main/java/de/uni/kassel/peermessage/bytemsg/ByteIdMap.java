package de.uni.kassel.peermessage.bytemsg;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uni.kassel.peermessage.IdMap;
import de.uni.kassel.peermessage.exception.DecodeException;
import de.uni.kassel.peermessage.interfaces.PrimaryEntityCreator;

public class ByteIdMap extends IdMap<PrimaryEntityCreator>{
	protected HashMap<Byte, PrimaryEntityCreator> decoderMap;
	private Encoding encoder;
	private Decoding decoder;
	public static char SPLITTER = ' ';
	private boolean lenCheck;

	public ByteIdMap() {

	}

	public void setCheckLen(boolean checklen) {
		this.lenCheck = checklen;
		if (encoder != null) {
			encoder.setCheckLen(checklen);
		}
	}

	@Override
	public boolean addCreater(PrimaryEntityCreator createrClass) {
		boolean result=super.addCreater(createrClass);
		if (decoderMap == null) {
			decoderMap = new HashMap<Byte, PrimaryEntityCreator>();
		}
		if (decoderMap.containsKey(createrClass.getEventTyp())) {
			return false;
		}
		decoderMap.put(createrClass.getEventTyp(), createrClass);
		return result;
	}
	
	public ByteBuffer encode(Object entity) {
		if (encoder == null) {
			encoder = new Encoding(this, lenCheck);
		}
		return encoder.encode(entity);
	}

	public String encodeHTTP(Object element) {
		ByteBuffer bytes = encode(element);
		if (bytes == null) {
			return null;
		}
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < bytes.limit(); i++) {
			byte value = bytes.get(i);
			if (value <= 32 || value == 127) {
				returnValue.append(SPLITTER);
				returnValue.append((char) (value + SPLITTER + 1));
			} else {
				returnValue.append((char) value);
			}
		}
		return returnValue.toString();
	}

	public Object decode(Object bytes) throws DecodeException {
		if (decoder == null) {
			decoder = new Decoding(this);
		}
		return decoder.decode(bytes);
	}

	public Object decodeHTTP(String bytes) throws DecodeException {
		int len = bytes.length();
		ByteBuffer buffer = ByteBuffer.allocate(len);
		for (int i = 0; i < len; i++) {
			int value = bytes.charAt(i);
			if (value == SPLITTER) {
				value = bytes.charAt(++i);
				buffer.put((byte) (value - SPLITTER - 1));
			} else {
				buffer.put((byte) value);
			}
		}
		int limit = buffer.position();
		buffer.position(0);
		buffer.limit(limit);
		return decode(buffer);
	}
	public PrimaryEntityCreator getCreatorDecoderClass(byte typ) {
		return decoderMap.get(typ);
	}
}
