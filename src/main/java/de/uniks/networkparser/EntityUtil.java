package de.uniks.networkparser;

import java.util.Collection;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.bytes.ByteTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;

public class EntityUtil {
	public static final String emfTypes = " EOBJECT EBIG_DECIMAL EBOOLEAN EBYTE EBYTE_ARRAY ECHAR EDATE EDOUBLE EFLOAT EINT EINTEGER ELONG EMAP ERESOURCE ESHORT ESTRING ";

	public static final boolean isEMFType(String tag) {
		return emfTypes.indexOf(" " + ("" + tag).toUpperCase() + " ") >= 0;
	}
	
	public static final void writeByteHeader(ByteBuffer buffer, byte type, int valueLength) {
		if (buffer == null) {
			return;
		}
		if (valueLength > 0) {
			/* Save Type */
			if (type != 0) {
				buffer.put(type);
				if (getSubGroup(type) != ByteTokener.LEN_LAST) {
					int lenSize = getTypeLen(type, valueLength, true);

					if (lenSize == 1) {
						if (type == ByteTokener.DATATYPE_CLAZZNAME || getSubGroup(type) == ByteTokener.LEN_LITTLE) {
							buffer.put((byte) (valueLength + ByteTokener.SPLITTER));
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
		} else {
			buffer.put(ByteTokener.DATATYPE_NULL);
		}
	}

	public static final byte getType(byte group, byte subGroup) {
		return (byte) (group + subGroup);
	}

	public static final byte getType(byte type, int len, boolean isLast) {
		if (isGroup(type)) {
			if (isLast) {
				return getType(type, ByteTokener.LEN_LAST);
			}
			if (len > 32767) {
				return getType(type, ByteTokener.LEN_BIG);
			}
			if (len > 250) {
				return getType(type, ByteTokener.LEN_MID);
			}
			if (len > ByteTokener.SPLITTER) {
				return getType(type, ByteTokener.LEN_SHORT);
			}
			return getType(type, ByteTokener.LEN_LITTLE);
		}
		return type;
	}

	public static final int getTypeLen(byte type, int len, boolean isLast) {
		if (isGroup(type)) {
			int ref = type % 16 - 10;
			if (ref == 0) {
				type = getType(type, len, isLast);
				ref = type % 16 - 10;
			}
			if (ref == ByteTokener.LEN_SHORT || ref == ByteTokener.LEN_LITTLE) {
				return 1;
			}
			if (ref == ByteTokener.LEN_MID) {
				return 2;
			}
			if (ref == ByteTokener.LEN_BIG) {
				return 4;
			}
			return 0;
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAME) {
			return 1;
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAMELONG) {
			return 4;
		}
		return 0;
	}

	public static final boolean isPrimitive(byte type) {
		return ((type >= ByteTokener.DATATYPE_SHORT && type <= ByteTokener.DATATYPE_BYTE)
				|| type <= ByteTokener.DATATYPE_CHAR);
	}

	/**
	 * Check if the Type is type of Group
	 *
	 * @param type the the type of data
	 * @return success
	 */
	public static final boolean isGroup(byte type) {
		return (type & 0x08) == 0x08;
	}

	public static final String getStringType(byte type) {
		if (type == ByteTokener.DATATYPE_NULL) {
			return "DATATYPE_NULL";
		}
		if (type == ByteTokener.DATATYPE_FIXED) {
			return "DATATYPE_FIXED";
		}
		if (type == ByteTokener.DATATYPE_SHORT) {
			return "DATATYPE_SHORT";
		}
		if (type == ByteTokener.DATATYPE_INTEGER) {
			return "DATATYPE_INTEGER";
		}
		if (type == ByteTokener.DATATYPE_LONG) {
			return "DATATYPE_LONG";
		}
		if (type == ByteTokener.DATATYPE_FLOAT) {
			return "DATATYPE_FLOAT";
		}
		if (type == ByteTokener.DATATYPE_DOUBLE) {
			return "DATATYPE_DOUBLE";
		}
		if (type == ByteTokener.DATATYPE_DATE) {
			return "DATATYPE_DATE";
		}
		if (type == ByteTokener.DATATYPE_CLAZZID) {
			return "DATATYPE_CLAZZID";
		}
		if (type == ByteTokener.DATATYPE_CLAZZPACKAGE) {
			return "DATATYPE_CLAZZPACKAGE";
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAME) {
			return "DATATYPE_CLAZZNAME";
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAMELONG) {
			return "DATATYPE_CLAZZNAMELONG";
		}
		if (type == ByteTokener.DATATYPE_CLAZZTYPE) {
			return "DATATYPE_CLAZZTYPE";
		}
		if (type == ByteTokener.DATATYPE_CLAZZTYPELONG) {
			return "DATATYPE_CLAZZTYPELONG";
		}
		if (type == ByteTokener.DATATYPE_BYTE) {
			return "DATATYPE_BYTE";
		}
		if (type == ByteTokener.DATATYPE_UNSIGNEDBYTE) {
			return "DATATYPE_UNSIGNEDBYTE";
		}
		if (type == ByteTokener.DATATYPE_CHAR) {
			return "DATATYPE_CHAR";
		}
		if (type == ByteTokener.DATATYPE_ASSOC) {
			return "DATATYPE_ASSOC";
		}
		if (type == ByteTokener.DATATYPE_ASSOCLONG) {
			return "DATATYPE_ASSOCLONG";
		}
		if (type == ByteTokener.DATATYPE_CLAZZSTREAM) {
			return "DATATYPE_CLAZZSTREAM";
		}

		if (isGroup(type)) {
			byte group = getGroup(type);
			byte subgroup = getSubGroup(type);
			String result;
			if (group == ByteTokener.DATATYPE_BYTEARRAY) {
				result = "DATATYPE_BYTEARRAY";
			} else if (group == ByteTokener.DATATYPE_STRING) {
				result = "DATATYPE_STRING";
			} else if (group == ByteTokener.DATATYPE_LIST) {
				result = "DATATYPE_LIST";
			} else if (group == ByteTokener.DATATYPE_MAP) {
				result = "DATATYPE_MAP";
			} else if (group == ByteTokener.DATATYPE_CHECK) {
				result = "DATATYPE_CHECK";
			} else {
				result = "";
			}

			if (subgroup == ByteTokener.LEN_LITTLE) {
				result += "LITTLE";
			} else if (subgroup == ByteTokener.LEN_SHORT) {
				result += "SHORT";
			} else if (subgroup == ByteTokener.LEN_MID) {
				result += "MID";
			} else if (subgroup == ByteTokener.LEN_BIG) {
				result += "BIG";
			} else if (subgroup == ByteTokener.LEN_LAST) {
				result += "LAST";
			}
			return result;
		}
		return null;
	}

	public static final byte getGroup(byte type) {
		return (byte) ((type / 16) * 16 + 10);
	}

	public static final byte getSubGroup(byte type) {
		return (byte) ((type % 16) - 10);
	}
	
	public static final boolean compareEntity(Entity entityA, Entity entityB) {
		return compareEntity(entityA, entityB, new TextDiff(), null);
	}

	public static final boolean compareEntity(Collection<?> jsonA, Collection<?> jsonB) {
		return compareEntity(jsonA, jsonB, new TextDiff(), null);
	}

	public static final boolean compareEntity(Object entityA, Object entityB, TextDiff diffList, BaseItem sameObject) {
		if (sameObject == null) {
			if (entityA instanceof Entity) {
				sameObject = ((BaseItem) entityA).getNewList(true);
			} else if (entityB instanceof Entity) {
				sameObject = ((BaseItem) entityB).getNewList(true);
			} else if (entityA instanceof BaseItem) {
				sameObject = ((BaseItem) entityA).getNewList(false);
			} else if (entityB instanceof BaseItem) {
				sameObject = ((BaseItem) entityB).getNewList(false);
			} else {
				sameObject = new JsonObject();
			}
		}
		/* Big Check */
		if (entityB == null) {
			if (entityA != null) {
				diffList.with(null, entityA, entityB);
				return false;
			}
			return true;

		}
		if (entityA instanceof Entity && entityB instanceof Entity) {
			Entity elementA = (Entity) entityA;
			Entity elementB = (Entity) entityB;
			for (int i = 0; i < elementA.size(); i++) {
				String key = elementA.getKeyByIndex(i);
				Object valueA = elementA.getValue(key);
				Object valueB = elementB.getValue(key);
				if (valueA == null) {
					if (valueB == null) {
						Object oldValue = elementA.getValue(key);
						if (sameObject != null) {
							sameObject.add(key, oldValue);
						}
						elementA.without(key);
						elementB.without(key);
						i--;
					}
					continue;
				}
				Object oldValue = compareValue(key, valueA, valueB, diffList, sameObject);
				if (oldValue != null) {
					if (sameObject != null) {
						sameObject.add(key, oldValue);
					}
					elementA.without(key);
					elementB.without(key);
					i--;
				}
			}
			/* Other Way */
			for (int i = 0; i < elementB.size(); i++) {
				String key = elementB.getKeyByIndex(i);
				Object valueA = elementA.getValue(key);
				Object valueB = elementB.getValue(key);
				if (valueA == null) {
					/* Its new */
					compareValue(key, valueA, valueB, diffList, sameObject);
				}
			}
			if (elementA.size() > 0 || elementB.size() > 0) {
				return false;
			}
			if (entityA instanceof EntityList && entityB instanceof EntityList) {
				EntityList xmlA = (EntityList) entityA;
				EntityList xmlB = (EntityList) entityB;
				if (xmlA.sizeChildren() != xmlB.sizeChildren()) {
					return false;
				}
				if (xmlA.sizeChildren() < 1) {
					if (entityA instanceof XMLEntity && entityB instanceof XMLEntity) {
						return ((XMLEntity) xmlA).getTag().equals(((XMLEntity) xmlB).getTag());
					}
					return true;
				}
				SimpleList<EntityList> childrenA = new SimpleList<EntityList>();
				SimpleList<EntityList> childrenB = new SimpleList<EntityList>();
				for (int i = 0; i < xmlA.sizeChildren(); i++) {
					childrenA.add(xmlA.getChild(i));
					childrenB.add(xmlB.getChild(i));
				}
				if (compareEntity(childrenA, childrenB) == false) {
					return false;
				}
				if (entityA instanceof XMLEntity && entityB instanceof XMLEntity) {
					return ((XMLEntity) xmlA).getTag().equals(((XMLEntity) xmlB).getTag());
				}
			}
			return true;
		}
		if (entityA instanceof Collection<?> && entityB instanceof Collection<?>) {
			Collection<?> colectionA = (Collection<?>) entityA;
			Collection<?> colectionB = (Collection<?>) entityB;
			Object[] itemsA = colectionA.toArray();
			Object[] itemsB = colectionB.toArray();
			for (int i = 0; i < itemsA.length; i++) {
				Object valueA = itemsA[i];
				Object valueB = null;
				if (itemsB.length > i) {
					valueB = itemsB[i];
				}
				Object oldValue = compareValue(null, valueA, valueB, diffList, sameObject);
				if (itemsB.length <= i) {
					continue;
				}
				if (oldValue != null) {
					colectionA.remove(valueA);
					if (sameObject != null) {
						sameObject.add(oldValue);
					}
					colectionB.remove(valueB);
				}
			}
			/* Other Way */
			itemsB = colectionB.toArray();
			for (int i = colectionA.size(); i < colectionB.size(); i++) {
				Object valueB = itemsB[i];
				/* Its new */
				compareValue(null, null, valueB, diffList, sameObject);
			}
			return colectionA.size() < 1 && colectionB.size() < 1;
		}
		return false;
	}

	protected static final Object compareValue(String key, Object valueA, Object valueB, TextDiff diffList,
			BaseItem sameElement) {
		BaseItem sameObject = null;
		if (diffList == null) {
			return null;
		}
		if (valueA instanceof Entity && valueB instanceof Entity) {
			Entity entityA = (Entity) valueA;
			if (sameElement != null) {
				sameObject = entityA.getNewList(true);
			}
			TextDiff last = diffList.getLast();
			if (compareEntity(entityA, (Entity) valueB, diffList, sameObject)) {
				return sameObject;
			}
			diffList.replaceChild(last, key, valueA, valueB);
			return null;
		} else if (valueA instanceof Collection<?> && valueB instanceof Collection<?>) {
			if (sameElement != null) {
				sameObject = sameElement.getNewList(false);
			}
			TextDiff last = diffList.getLast();
			if (compareEntity((Collection<?>) valueA, (Collection<?>) valueB, diffList, sameObject)) {
				return sameObject;
			}
			diffList.replaceChild(last, key, valueA, valueB);
			return null;
		}
		if (valueA != null && valueA.equals(valueB)) {
			return valueA;
		}
		diffList.createChild(key, valueA, valueB);
		return null;
	}
	
	public static final SimpleList<Pos> getExcelRange(String tag) {
		SimpleList<Pos> range = new SimpleList<Pos>();
		if (tag == null) {
			return range;
		}
		int pos = tag.toUpperCase().indexOf(":");
		if (pos > 0) {
			Pos start = Pos.valueOf(tag.substring(0, pos));
			Pos end = Pos.valueOf(tag.substring(pos + 1));
			Pos step = Pos.create(start.x, start.y);

			while (step.y <= end.y) {
				while (step.x <= end.x) {
					range.add(step);
					step = Pos.create(step.x + 1, step.y);
				}
				step = Pos.create(start.x, step.y + 1);
			}
		} else {
			range.add(Pos.valueOf(tag));
		}
		return range;
	}
}
