package de.uniks.networkparser.bytes;

import de.uniks.networkparser.buffer.ByteBuffer;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class ByteUtil {
	public static void writeByteHeader(ByteBuffer buffer, byte typ,
			int valueLength) {
		if (valueLength > 0 ) {
			// Save Typ
			if (typ != 0) {
				buffer.put(typ);
				if (getSubGroup(typ) != ByteIdMap.LEN_LAST) {
					int lenSize = ByteUtil.getTypLen(typ, valueLength, true);

					if (lenSize == 1) {
						if (typ == ByteIdMap.DATATYPE_CLAZZNAME
								|| ByteUtil.getSubGroup(typ) == ByteIdMap.LEN_LITTLE) {
							buffer.put((byte) (valueLength + ByteIdMap.SPLITTER));
						} else {
							buffer.put((byte) valueLength);
						}
					} else if (lenSize == 2) {
						buffer.put((short) valueLength);
					} else if (lenSize == 4) {
						buffer.put((int) valueLength);
					}
				}
			}
		} else if(buffer!=null){
			buffer.put(ByteIdMap.DATATYPE_NULL);
		}
	}

	public static byte[] clone(byte[] entity) {
		byte[] result=new byte[entity.length];
		for(int i=0;i<entity.length;i++) {
			result[i] = entity[i];
		}
		return result;
	}

	public static byte getTyp(byte group, byte subGroup) {
		return (byte) (group + subGroup);
	}

	public static byte getTyp(byte typ, int len, boolean isLast) {
		if (isGroup(typ)) {
			if (isLast) {
				return getTyp(typ, ByteIdMap.LEN_LAST);
			}
			if (len > 32767) {
				return getTyp(typ, ByteIdMap.LEN_BIG);
			}
			if (len > 250) {
				return getTyp(typ, ByteIdMap.LEN_MID);
			}
			if (len > ByteIdMap.SPLITTER) {
				return getTyp(typ, ByteIdMap.LEN_SHORT);
			}
			return getTyp(typ, ByteIdMap.LEN_LITTLE);
		}
		return typ;
	}

	public static int getTypLen(byte typ, int len, boolean isLast) {
		if (isGroup(typ)) {
			int ref = typ % 16 - 10;
			if (ref == 0) {
				typ = getTyp(typ, len, isLast);
				ref = typ % 16 - 10;
			}
			if (ref == ByteIdMap.LEN_SHORT || ref == ByteIdMap.LEN_LITTLE) {
				return 1;
			}
			if (ref == ByteIdMap.LEN_MID) {
				return 2;
			}
			if (ref == ByteIdMap.LEN_BIG) {
				return 4;
			}
			// if (ref == ByteIdMap.LEN_LAST) {
			return 0;
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAME) {
			// || typ == ByteIdMap.DATATYPE_CLAZZTYP add bei ByteList
			return 1;
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAMELONG) {
			return 4;
		}
		// if (typ == ByteIdMap.DATATYPE_CLAZZTYP || typ ==
		// ByteIdMap.DATATYPE_ASSOC) {
		// return 1;
		// }
		// if (typ == ByteIdMap.DATATYPE_CLAZZTYPLONG || typ ==
		// ByteIdMap.DATATYPE_ASSOCLONG) {
		// return 4;
		// }
		return 0;
	}

	public static ByteBuffer getBuffer(int len) {
		if (len < 1) {
			return null;
		}
		ByteBuffer message = ByteBuffer.allocate(len);
		return message;
	}

	public static boolean isPrimitive(byte typ) {
		return ((typ >= ByteIdMap.DATATYPE_SHORT && typ <= ByteIdMap.DATATYPE_BYTE) || typ <= ByteIdMap.DATATYPE_CHAR);
	}

	/**
	 * CHeck if the Typ is typ of Group
	 *
	 * @param typ
	 *			the the typ of data
	 * @return the boolean
	 */
	public static boolean isGroup(byte typ) {
		return (typ & 0x08) == 0x08;
	}

	public static String getStringTyp(byte typ) {
		if (typ == ByteIdMap.DATATYPE_NULL) {
			return "DATATYPE_NULL";
		}
		if (typ == ByteIdMap.DATATYPE_FIXED) {
			return "DATATYPE_FIXED";
		}
		if (typ == ByteIdMap.DATATYPE_SHORT) {
			return "DATATYPE_SHORT";
		}
		if (typ == ByteIdMap.DATATYPE_INTEGER) {
			return "DATATYPE_INTEGER";
		}
		if (typ == ByteIdMap.DATATYPE_LONG) {
			return "DATATYPE_LONG";
		}
		if (typ == ByteIdMap.DATATYPE_FLOAT) {
			return "DATATYPE_FLOAT";
		}
		if (typ == ByteIdMap.DATATYPE_DOUBLE) {
			return "DATATYPE_DOUBLE";
		}
		if (typ == ByteIdMap.DATATYPE_DATE) {
			return "DATATYPE_DATE";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZID) {
			return "DATATYPE_CLAZZID";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZPACKAGE) {
			return "DATATYPE_CLAZZPACKAGE";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAME) {
			return "DATATYPE_CLAZZNAME";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAMELONG) {
			return "DATATYPE_CLAZZNAMELONG";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZTYP) {
			return "DATATYPE_CLAZZTYP";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZTYPLONG) {
			return "DATATYPE_CLAZZTYPLONG";
		}
		if (typ == ByteIdMap.DATATYPE_BYTE) {
			return "DATATYPE_BYTE";
		}
		if (typ == ByteIdMap.DATATYPE_UNSIGNEDBYTE) {
			return "DATATYPE_UNSIGNEDBYTE";
		}
		if (typ == ByteIdMap.DATATYPE_CHAR) {
			return "DATATYPE_CHAR";
		}
		if (typ == ByteIdMap.DATATYPE_ASSOC) {
			return "DATATYPE_ASSOC";
		}
		if (typ == ByteIdMap.DATATYPE_ASSOCLONG) {
			return "DATATYPE_ASSOCLONG";
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZSTREAM) {
			return "DATATYPE_CLAZZSTREAM";
		}

		if (isGroup(typ)) {
			byte group = getGroup(typ);
			byte subgroup = getSubGroup(typ);
			String result = "";
			if (group == ByteIdMap.DATATYPE_BYTEARRAY) {
				result = "DATATYPE_BYTEARRAY";
			} else if (group == ByteIdMap.DATATYPE_STRING) {
				result = "DATATYPE_STRING";
			} else if (group == ByteIdMap.DATATYPE_LIST) {
				result = "DATATYPE_LIST";
			} else if (group == ByteIdMap.DATATYPE_MAP) {
				result = "DATATYPE_MAP";
			} else if (group == ByteIdMap.DATATYPE_CHECK) {
				result = "DATATYPE_CHECK";
			}

			if (subgroup == ByteIdMap.LEN_LITTLE) {
				result += "LITTLE";
			} else if (subgroup == ByteIdMap.LEN_SHORT) {
				result += "SHORT";
			} else if (subgroup == ByteIdMap.LEN_MID) {
				result += "MID";
			} else if (subgroup == ByteIdMap.LEN_BIG) {
				result += "BIG";
			} else if (subgroup == ByteIdMap.LEN_LAST) {
				result += "LAST";
			}
			return result;
		}
		return null;
	}

	public static byte getGroup(byte typ) {
		return (byte) ((typ / 16) * 16 + 10);
	}

	public static byte getSubGroup(byte typ) {
		return (byte) ((typ % 16) - 10);
	}
}
