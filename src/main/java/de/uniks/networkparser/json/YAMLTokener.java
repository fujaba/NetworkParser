package de.uniks.networkparser.json;

import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class YAMLTokener extends Tokener {
	public static final char DASH = '-';
	public static final String STOPCHARS = "=# ";
	public static final char RETURN = '\n';
	private SimpleKeyValueList<Object, SimpleKeyValueList<String, SimpleList<String>>> refs;

	@Override
	public EntityList parseToEntity(BaseItem entity, Object buffer) {
		if (buffer != null && buffer instanceof Buffer && entity instanceof EntityList) {
			EntityList list = (EntityList) entity;
			parseLine(0, list, (Buffer) buffer);
			return list;
		}
		return null;
	}

	protected int parseLine(int deep, EntityList owner, Buffer buffer) {
		if (buffer == null) {
			return 0;
		}
		char c = buffer.getCurrentChar();
		int newDeep = 0;
		boolean read = false;
		BaseItem item = null;
		do {
			if (read) {
				c = buffer.getChar();
			}
			read = true;
			if (c == BufferItem.SPACE) {
				newDeep++;
				continue;
			}
			if (newDeep < deep) {
				return newDeep;
			}
			if (c == '\r') {
				c = buffer.getChar();
			}
			if (c == '\n') {
				/* Next Line */
				parseLine(newDeep, owner, buffer);
				c = 0;
				continue;
			}
			/* Parsing the CurrentLine */

			CharacterBuffer subBuffer = null;
			switch (c) {
			case 0:
				break;
			case ENTER:
				break;
			case '-':
				c = buffer.getChar();
				/* Must be a Space */
				if (c == BufferItem.SPACE) {
					/* Collection */
					if (deep <= newDeep) {
						/* Add to Current List */
						item = owner.getNewList(false);
					} else {
						item = owner.getNewList(true);
					}
				}
				/* Must be a String so its default */
			default:
				if (subBuffer == null) {
					subBuffer = new CharacterBuffer();
				}
				subBuffer.with(c);
				break;
			}
		} while (c != 0);
		if (buffer != null && owner != null) {
			if (item == null) {
				item = owner.getNewList(false);
			}
			if (item instanceof YamlItem) {
				((YamlItem) item).withKey(buffer.toString());
			}
			owner.add(item);
		}
		return newDeep;
	}

	/**
	 * Decoding YAML FILE
	 * 
	 * @param yaml Yaml Text
	 * @return decoded Value
	 * 
	 *         yaml grammar yaml ::= objects* object ::= plainObject | objectList
	 *         plainObject ::= - type': ' objId\n attr* attr ::= attrName': '
	 *         attrValue\n attrValue ::= id | string | '[' attrValue * ']'
	 *         objectList ::= type colName:* \n key: attrValue* \n* valueRow ::=
	 *         attrValue*
	 */
	public Object decode(String yaml) {
		CharacterBuffer buffer = new CharacterBuffer().with(yaml);
		Object root = null;
		refs = new SimpleKeyValueList<Object, SimpleKeyValueList<String, SimpleList<String>>>();
		while (buffer.isEnd() == false) {
			if (DASH != buffer.getCurrentChar()) {
				buffer.printError("'-' expected");
				break;
			}
			buffer.skipChar(DASH);
			CharacterBuffer key = buffer.nextString();
			if (key.endsWith(":", true)) {
				/* usual */
				Object returnValue = parseUsualObjectAttrs(key, buffer);
				if (root == null) {
					root = returnValue;
				}
				continue;
			}
			parseObjectTableAttrs(key, buffer);
		}

		/* CHECK IF REF NOT EMPTY */
		if (refs.size() > 0) {
			for (int o = 0; o < refs.size(); o++) {
				Object entity = refs.getKeyByIndex(o);
				SendableEntityCreator creator = map.getCreatorClass(entity);
				SimpleKeyValueList<String, SimpleList<String>> entityRefs = refs.getValueByIndex(o);
				for (int r = 0; r < entityRefs.size(); r++) {
					String attribute = entityRefs.getKeyByIndex(r);
					SimpleList<String> valueRefs = entityRefs.getValueByIndex(r);
					for (String ref : valueRefs) {
						Object value = map.getObject(ref);
						if (value != null) {
							creator.setValue(entity, attribute, value, SendableEntityCreator.NEW);
						}
					}
				}
			}
		}
		return root;
	}

	private void parseObjectTableAttrs(CharacterBuffer currentToken, Buffer buffer) {
		/* skip column names */
		if (currentToken == null || map == null) {
			return;
		}
		String className = currentToken.toString();

		SendableEntityCreator creator = map.getCreator(className, false);
		currentToken = buffer.nextString();

		SimpleList<String> colNameList = new SimpleList<String>();

		while (currentToken.length() > 0 && currentToken.endsWith(":", true)) {
			colNameList.add(currentToken.rtrim(COLON).toString());
			buffer.skipChar(BufferItem.SPACE);
			if (buffer.getCurrentChar() == RETURN) {
				currentToken = buffer.nextString();
				break;
			}
			currentToken = buffer.nextString();
		}

		while (currentToken.length() > 0 && currentToken.equals("-") == false) {
			String objectId = currentToken.rtrim(COLON).toString();
			currentToken = buffer.nextString();

			Object obj = map.getObject(objectId);
			if (obj == null) {
				obj = creator.getSendableInstance(false);
				map.put(objectId, obj, false);
			}

			/* column values */
			int colNum = 0;
			while (currentToken.length() > 0 && !currentToken.endsWith(":", true)
					&& currentToken.equals("-") == false) {
				String attrName = colNameList.get(colNum);

				if (currentToken.startsWith("[")) {
					String value = currentToken.substring(1);
					if (value.trim().equals("")) {
						value = buffer.nextString().toString();
					}
					setValue(creator, obj, attrName, value);

					while (currentToken.length() > 0 && !currentToken.endsWith("]", true)) {
						currentToken = buffer.nextString();
						value = currentToken.toString();
						if (currentToken.endsWith("]", true)) {
							value = currentToken.substring(0, currentToken.length() - 1);
						}
						if (value.trim().equals("") == false ) {
							setValue(creator, obj, attrName, value);
						}
					}
				} else {
					setValue(creator, obj, attrName, currentToken.toString());
				}
				colNum++;
				currentToken = buffer.nextString();
			}
			if (currentToken.equals("-")) {
				buffer.withLookAHead(DASH);
			}
		}
	}

	private Object parseUsualObjectAttrs(CharacterBuffer currentToken, Buffer buffer) {
		if (buffer == null || currentToken == null || map == null) {
			return null;
		}
		String objectId = currentToken.trimEnd(1).toString();
		String className = buffer.nextString().rtrim().toString();
		currentToken = buffer.nextString();

		SendableEntityCreator creator = map.getCreator(className, false);

		Object obj = map.getObject(objectId);
		if (obj == null) {
			obj = creator.getSendableInstance(false);
			map.put(objectId, obj, false);
		}

		/* read attributes */
		while (currentToken.equals("") == false && !currentToken.equals("-")) {
			String attrName = currentToken.rtrim(':').toString();
			currentToken = buffer.nextString();
			/* many values */
			while (currentToken.equals("") == false && !currentToken.endsWith(":", true) && !currentToken.equals("-")) {
				String attrValue = currentToken.toString();
				setValue(creator, obj, attrName, attrValue);
				currentToken = buffer.nextString();
			}
			if (currentToken.equals("-")) {
				buffer.withLookAHead('-');
			}
		}
		return obj;
	}

	private boolean setValue(SendableEntityCreator creator, Object obj, String attrName, String attrValue) {
		if (map == null) {
			return false;
		}
		Object targetObj = map.getObject(attrValue);
		if (targetObj != null) {
			try {
				creator.setValue(obj, attrName, targetObj, SendableEntityCreator.NEW);
			} catch (Exception e) {
				creator.setValue(obj, attrName, attrValue, SendableEntityCreator.NEW);
			}
			return true;
		}
		SimpleKeyValueList<String, SimpleList<String>> values = refs.get(obj);
		try {
			if (values != null) {
				SimpleList<String> valueNames = values.get(attrName);
				if (valueNames != null) {
					valueNames.add(attrValue);
					return true;
				}
			}
			creator.setValue(obj, attrName, attrValue, SendableEntityCreator.NEW);
		} catch (Exception e) {
			/* maybe a node */
			if (values == null) {
				values = new SimpleKeyValueList<String, SimpleList<String>>();
				refs.put(obj, values);
			}
			SimpleList<String> valueNames = values.get(attrName);
			if (valueNames != null) {
				valueNames.add(attrValue);
			} else {
				valueNames = new SimpleList<String>();
				valueNames.add(attrValue);
				values.put(attrName, valueNames);
			}
		}
		return true;
	}
}