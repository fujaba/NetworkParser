package de.uni.kassel.peermessage.bytes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.uni.kassel.peermessage.event.ByteMessage;
import de.uni.kassel.peermessage.event.UnknownMessage;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;

public class Decoding {
	public Object decode(Object value, ByteIdMap parent) throws RuntimeException {
		if (value instanceof ByteBuffer) {
			return decode((ByteBuffer) value, parent);
		} else if (value instanceof byte[]) {
			return decode(ByteBuffer.wrap((byte[]) value), parent);
		}
		return null;
	}

	public Object decode(ByteBuffer in, ByteIdMap parent) throws RuntimeException {
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
		} else if (typValue == ByteIdMap.DATATYPE_DATE) {
			int value = in.getInt();
			Date newValue = new Date(value);
			return newValue;
		} else {
			byte group=getTyp(typValue, ByteIdMap.DATATYPE_STRING);
			if(group==ByteIdMap.DATATYPE_STRING||
					group==ByteIdMap.DATATYPE_BYTEARRAY||
					group==ByteIdMap.DATATYPE_LIST||
					group==ByteIdMap.DATATYPE_MAP){
				byte subgroup=getTyp(ByteIdMap.DATATYPE_STRING, typValue);
				byte[] values;
				int len=0;
				if(subgroup == ByteIdMap.DATATYPE_STRINGSHORT) {
					len = in.get()-ByteIdMap.SPLITTER;
				}else if(subgroup == ByteIdMap.DATATYPE_STRING) {
					len = in.get();
				} else if (subgroup == ByteIdMap.DATATYPE_STRINGMID) {
					len = in.getShort();
				} else if (subgroup == ByteIdMap.DATATYPE_STRINGBIG) {
					len = in.getInt();
				} else if (subgroup == ByteIdMap.DATATYPE_STRINGLAST) {
					len = in.remaining();
				}
				values = new byte[len];
				in.get(values);
				if(group==ByteIdMap.DATATYPE_STRING){
					return new String(values);
				} else if (group == ByteIdMap.DATATYPE_BYTEARRAY) {
					return values;
				} else if (group == ByteIdMap.DATATYPE_LIST) {
					ByteBuffer child=ByteBuffer.wrap((byte[]) values);
					ArrayList<Object> list = new ArrayList<Object>();
					while (child.remaining() > 0) {
						Byte subType = child.get();
						Object entity = getDecodeObject(subType, child);
						if (entity != null) {
							list.add(entity);
						}
					}
					return list;
				} else if (typValue == ByteIdMap.DATATYPE_MAP) {
					ByteBuffer child=ByteBuffer.wrap((byte[]) values);
					HashMap<Object, Object> map = new HashMap<Object, Object>();
					while (child.remaining() > 0) {
						Byte subType = child.get();
						Object key = getDecodeObject(subType, child);
						if (key != null) {
							subType = child.get();
							Object value = getDecodeObject(subType, child);
							if (key != null) {
								map.put(key, value);
							}
						}
					}
					return map;
				}
			}
		}
		return null;
	}
	private byte getTyp(byte group, byte subgroup){
		byte returnValue=(byte) ((group/16)*16);
		return (byte) (returnValue+(subgroup%16));
	}
}
