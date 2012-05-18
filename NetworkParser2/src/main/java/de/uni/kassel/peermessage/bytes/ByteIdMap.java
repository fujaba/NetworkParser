package de.uni.kassel.peermessage.bytes;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uni.kassel.peermessage.IdMap;
import de.uni.kassel.peermessage.event.BasicMessage;
import de.uni.kassel.peermessage.event.creater.BasicMessageCreator;
import de.uni.kassel.peermessage.interfaces.ByteEntityCreator;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;

public class ByteIdMap extends IdMap{
	public static char SPLITTER = ' ';
	/** The Constant CLASS-VALUE. */
	public static final byte DATATYPE_CLAZZ = 0x22;
	/** The Constant NULL-VALUE. */
	public static final byte DATATYPE_NULL = 0x23;

	/** The Constant CHECKTYPE. */
	public static final byte DATATYPE_CHECK = 0x24;

	/**
	 * SIMPLE TYPES 
	 * The Constant DATATYPE_BYTE.
	 */
	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_SHORT = 0x30;

	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_INTEGER = 0x31;

	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_LONG = 0x32;

	/** The Constant DATATYPE_FLOAT. */
	public static final byte DATATYPE_FLOAT = 0x33;

	/** The Constant DATATYPE_DOUBLE. */
	public static final byte DATATYPE_DOUBLE = 0x34;
	
	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_DATE = 0x35;
		
	public static final byte DATATYPE_BYTE = 0x36;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_UNSIGNEDBYTE = 0x37;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_CHAR = 0x38;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_ENTITY = 0x39;

	
	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_ASSOC = 0x3A;
	
		
	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAY = 0x42;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAYSHORT = 0x43;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAYMID = 0x44;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAYBIG = 0x45;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAYLAST = 0x46;


	/** The Constant DATATYPE_STRING. */
	public static final byte DATATYPE_STRING = 0x52;

	/** The Constant DATATYPE_STRING. */
	public static final byte DATATYPE_STRINGSHORT = 0x53;
	
	/** The Constant DATATYPE_MIDSTRING. */
	public static final byte DATATYPE_STRINGMID = 0x54;

	/** The Constant DATATYPE_STRING. */
	public static final byte DATATYPE_STRINGBIG = 0x55;

	/** The Constant DATATYPE_STRING. */
	public static final byte DATATYPE_STRINGLAST = 0x56;

	
	/** The Constant DATATYPE_LIST. */
	public static final byte DATATYPE_LIST = 0x62;

	/** The Constant DATATYPE_SHORTLIST. */
	public static final byte DATATYPE_LISTSHORT = 0x63;
	
	/** The Constant DATATYPE_MIDLIST. */
	public static final byte DATATYPE_LISTMID = 0x64;

	/** The Constant DATATYPE_BIGLIST. */
	public static final byte DATATYPE_LISTBIG = 0x65;

	/** The Constant DATATYPE_LASTLIST. */
	public static final byte DATATYPE_LISTLAST = 0x66;
	
	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAP = 0x72;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAPSHORT = 0x73;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAPMID = 0x74;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAPBIG = 0x75;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAPLAST = 0x76;

	protected HashMap<Byte, ByteEntityCreator> decoderMap;
	private Decoding decoder;
	private boolean lenCheck;
	private boolean isDynamic;

	public ByteIdMap() {

	}

	public void setCheckLen(boolean checklen) {
		this.lenCheck = checklen;
	}

	@Override
	public boolean addCreator(SendableEntityCreator createrClass) {
		boolean result=super.addCreator(createrClass);
		if (decoderMap == null) {
			decoderMap = new HashMap<Byte, ByteEntityCreator>();
		}
		if(createrClass instanceof ByteEntityCreator){
			ByteEntityCreator byteCreator=(ByteEntityCreator) createrClass;
			if (decoderMap.containsKey(byteCreator.getEventTyp())) {
				return false;
			}
			decoderMap.put(byteCreator.getEventTyp(), byteCreator);
		}else{
			return false;
		}
		return result;
	}
	
	public ByteEntityMessage encode(Object entity) {
		SendableEntityCreator creator;
		if(parent!=null){
			creator = parent.getCreatorClass(entity);
		}else{
			creator=getCreatorClass(entity);
		}
		if (creator == null) {
			return null;
		}
		
		ByteEntityMessage msg=new ByteEntityMessage();
		
		if (creator instanceof BasicMessageCreator) {
			BasicMessage basicEvent = (BasicMessage) entity;
			String value = basicEvent.getValue();
			msg.setFullMsg(value);
			return msg;
		}
		
		msg.setLenCheck(lenCheck);
		
		if(creator instanceof ByteEntityCreator){
			msg.setMsgTyp(((ByteEntityCreator) creator).getEventTyp());
		}else{
			Object reference = creator.getSendableInstance(true);
			msg.setMsgTyp(reference.getClass().getName());
		}
		
		String[] properties = creator.getProperties();
		Object referenceObject = creator.getSendableInstance(true);
		msg.setDynamic(isDynamic);
		
		if (properties != null) {
			for (String property : properties) {
				msg.addChild(creator, entity, referenceObject, property, this);				
			}
		}
		
		
		msg.cleanUp();
		return msg;
	}

	public Object decode(Object bytes) throws RuntimeException {
		if (decoder == null) {
			decoder = new Decoding(this);
		}
		return decoder.decode(bytes);
	}

	public Object decodeHTTP(String bytes) throws RuntimeException {
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

	public ByteEntityCreator getCreatorDecoderClass(byte typ) {
		return decoderMap.get(typ);
	}
	
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		String ret = "";
		for (int i = 0; i < bytes.length; i++) {

			char b = (char) (bytes[i] & 0xFF);
			if (b < 0x10) {
				ret = ret + "0";
			}
			ret = ret + (String) (Integer.toHexString(b)).toUpperCase();
		}
		return ret;

	}

	public static byte[] toByteString(String hexString) {
		String hexVal = "0123456789ABCDEF";
		byte[] out = new byte[hexString.length() / 2];

		int n = hexString.length();

		for (int i = 0; i < n; i += 2) {
			// make a bit representation in an int of the hex value
			int hn = hexVal.indexOf(hexString.charAt(i));
			int ln = hexVal.indexOf(hexString.charAt(i + 1));

			// now just shift the high order nibble and add them together
			out[i / 2] = (byte) ((hn << 4) | ln);
		}

		return out;
	}

	public boolean isDynamic() {
		return isDynamic;
	}

	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}
}
