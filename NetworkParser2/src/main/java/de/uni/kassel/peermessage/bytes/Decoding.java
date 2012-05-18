package de.uni.kassel.peermessage.bytes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.uni.kassel.peermessage.event.ByteMessage;
import de.uni.kassel.peermessage.event.UnknownMessage;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;

public class Decoding {
	private ByteIdMap parent;

	public Decoding(ByteIdMap parent){
		this.parent=parent;
	}

	public Object decode(Object value) throws RuntimeException {
		if (value instanceof ByteBuffer) {
			return decode((ByteBuffer) value);
		} else if (value instanceof byte[]) {
			return decode(ByteBuffer.wrap((byte[]) value));
		}
		return null;
	}

	public Object decode(ByteBuffer in) throws RuntimeException {
		if (in.remaining() < 1)
			throw new RuntimeException("DecodeExpeption - Remaining:" + in.remaining());

		Object entity = null;
		byte typ = in.get();
		SendableEntityCreator eventCreater=null;
		if(typ==ByteIdMap.DATATYPE_CLAZZ){
			String clazz = (String) getDecodeObject(ByteIdMap.DATATYPE_STRING, in);
			eventCreater = parent.getCreatorClasses(clazz);
		}else{
			eventCreater = parent.getCreatorDecoderClass(typ);
		}
		if (eventCreater == null) {
			UnknownMessage e = new UnknownMessage();
			e.set(ByteMessage.PROPERTY_VALUE, in.array());
			entity = e;
		} else {
			entity = eventCreater.getSendableInstance(false);
			String[] properties = eventCreater.getProperties();
			if (properties != null) {
				for (String property : properties) {
					if (in.remaining() < 1) {
						break;
					}
					Byte typValue = in.get();
					if (typValue == ByteIdMap.DATATYPE_CHECK) {
						int len = in.getInt();
						if (len > 0) {
							if (in.remaining() != len)
								throw new RuntimeException("DecodeExpeption - Remaining:" + in.remaining() + "!="+len);
						}
						typValue = in.get();
					}
					Object value = getDecodeObject(typValue, in);
					if (value != null) {
						eventCreater.setValue(entity, property, value);
					}
				}
			}
		}
		return entity;
	}

	public Object getDecodeObject(Byte typValue, ByteBuffer in) {
		if (in.remaining() < 1) {
			return null;
		} else if (typValue == ByteIdMap.DATATYPE_BYTE) {
			return in.get();
		} else if (typValue == ByteIdMap.DATATYPE_CHAR) {
			return in.getChar();
		} else if (typValue == ByteIdMap.DATATYPE_SHORT) {
			return in.getShort();
		} else if (typValue == ByteIdMap.DATATYPE_INTEGER) {
			return in.getInt();
		} else if (typValue == ByteIdMap.DATATYPE_LONG) {
			return in.getLong();
		} else if (typValue == ByteIdMap.DATATYPE_FLOAT) {
			return in.getFloat();
		} else if (typValue == ByteIdMap.DATATYPE_DOUBLE) {
			return in.getDouble();
		} else if (typValue == ByteIdMap.DATATYPE_STRINGSHORT) {
			byte len = (byte)(in.get()-ByteIdMap.SPLITTER);
			byte[] value = new byte[len];
			in.get(value);
			return new String(value);
		} else if (typValue == ByteIdMap.DATATYPE_STRING) {
			byte len = in.get();
			byte[] value = new byte[len];
			in.get(value);
			return new String(value);
		} else if (typValue == ByteIdMap.DATATYPE_STRINGMID) {
			short len = in.getShort();
			byte[] value = new byte[len];
			in.get(value);
			return new String(value);
		} else if (typValue == ByteIdMap.DATATYPE_STRINGBIG) {
			int len = in.getInt();
			byte[] value = new byte[len];
			in.get(value);
			return new String(value);
		} else if (typValue == ByteIdMap.DATATYPE_STRINGLAST) {
			byte[] value = new byte[in.remaining()];
			in.get(value);
			return new String(value);
		} else if (typValue == ByteIdMap.DATATYPE_DATE) {
			int value = in.getInt();
			Date newValue = new Date(value);
			return newValue;
		} else if (typValue == ByteIdMap.DATATYPE_BYTEARRAYSHORT) {
			byte len = (byte)(in.get()-ByteIdMap.SPLITTER);
			byte[] value = new byte[len];
			in.get(value);
			return value;
		} else if (typValue == ByteIdMap.DATATYPE_BYTEARRAY) {
			byte len = in.get();
			byte[] value = new byte[len];
			in.get(value);
			return value;
		} else if (typValue == ByteIdMap.DATATYPE_BYTEARRAYMID) {
			short len = in.getShort();
			byte[] value = new byte[len];
			in.get(value);
			return value;
		} else if (typValue == ByteIdMap.DATATYPE_BYTEARRAYBIG) {
			int len = in.getInt();
			byte[] value = new byte[len];
			in.get(value);
			return value;
		} else if (typValue == ByteIdMap.DATATYPE_BYTEARRAYLAST) {
			byte[] value = new byte[in.remaining()];
			in.get(value);
			return value;
		} else if (typValue == ByteIdMap.DATATYPE_LIST) {
			short len = in.getShort();
			int pos = in.position();
			ArrayList<Object> list = new ArrayList<Object>();
			while (in.remaining() > 0 && in.position() <= pos + len) {
				Byte subType = in.get();
				Object entity = getDecodeObject(subType, in);
				if (entity != null) {
					list.add(list);
				}
			}
			return list;
		} else if (typValue == ByteIdMap.DATATYPE_MAP) {
			short len = in.getShort();
			int pos = in.position();
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			while (in.remaining() > 0 && in.position() <= pos + len) {
				Byte subType = in.get();
				Object key = getDecodeObject(subType, in);
				if (key != null) {
					subType = in.get();
					Object value = getDecodeObject(subType, in);
					if (key != null) {
						map.put(key, value);
					}
				}
			}
			return map;
		}
		return null;
	}
}
