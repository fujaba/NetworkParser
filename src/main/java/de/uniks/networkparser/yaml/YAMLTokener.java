package de.uniks.networkparser.yaml;

import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class YAMLTokener extends Tokener {
	public final static char DASH = '-';
	public final static String STOPCHARS = "=# ";
	public static final char RETURN='\n';
	private SimpleKeyValueList<Object, SimpleKeyValueList<String, SimpleList<String>>> refs;
	@Override
	public void parseToEntity(EntityList entity) {
		parseLine(0, entity);
	}

	protected int parseLine(int deep, EntityList owner) {
		char c = getCurrentChar();
		int newDeep = 0;
		boolean read=false;
		BaseItem item = null;
		do {
			if(read) {
				c = getChar();
			}
			read=true;
			if(c == SPACE) {
				newDeep++;
				continue;
			}
			if(newDeep<deep) {
				return newDeep;
			}
			if(c == '\r') {
				c = getChar();
			}
			if(c == '\n') {
				// Next Line
				parseLine(newDeep, owner);
				c=0;
				continue;
			}
			// Parsing the CurrentLine
			
			CharacterBuffer buffer = null;
			switch (c) {
				case 0:
					break;
				case ENTER:
					break;
				case '-':
					c = getChar();
					// Must be a Space
					if(c == SPACE) {
						// Collection 
						if(deep <= newDeep) {
							// Add to Current List
							item = owner.getNewList(false);
							
						} else {
							item = owner.getNewList(true);
						}
					}
					// Must be a String so its default
//					break;
				default:
					if(buffer == null) {
						buffer = new CharacterBuffer();
					}
					buffer.with(c);
					break;
			}
		}while(c!=0);
		if(buffer != null) {
			if(item == null) {
				item = owner.getNewList(false);
			}
			if( item instanceof YamlItem) {
				((YamlItem)item).withKey(buffer.toString());
			}
			owner.add(item);
		}
		return newDeep;
	}
	
	// yaml grammar
	// yaml ::= objects*
	// object ::= plainObject | objectList
	// plainObject ::= - type': ' objId\n attr*
	// attr ::= attrName': ' attrValue\n
	// attrValue ::= id | string | '[' attrValue * ']'
	// objectList ::= type colName:* \n key: attrValue* \n*
	// valueRow ::= attrValue* \n
	public Object decode(String yaml) {
		this.buffer = new CharacterBuffer().with(yaml);
		Object root = null;
		refs = new SimpleKeyValueList<Object, SimpleKeyValueList<String, SimpleList<String>>>();
		while (buffer.isEnd() == false) {
			if (DASH != buffer.getCurrentChar()) {
				buffer.printError("'-' expected");
				continue;
			}
			buffer.skipChar(DASH);
			CharacterBuffer key = buffer.nextString();
			if (key.endsWith(":", true)) {
				// usual
				Object returnValue = parseUsualObjectAttrs(key);
				if(root == null) {
					root = returnValue; 
				}
				continue;
			}
			parseObjectTableAttrs(key);
		}
		
		// CHECK IF REF NOT EMPTY
		if(refs.size()>0) {
			for(int o=0;o<refs.size();o++) {
				Object entity = refs.getKeyByIndex(o);
				SendableEntityCreator creator = map.getCreatorClass(entity);
				SimpleKeyValueList<String, SimpleList<String>> entityRefs = refs.getValueByIndex(o);
				for(int r=0;r<entityRefs.size();r++) {
					String attribute = entityRefs.getKeyByIndex(r);
					SimpleList<String> valueRefs = entityRefs.getValueByIndex(r);
					for(String ref : valueRefs) {
						Object value = map.getObject(ref);
						if(value != null) {
							creator.setValue(entity, attribute, value, SendableEntityCreator.NEW);
						}
					}
				}
			}
		}
		return root;
	}


	   private void parseObjectTableAttrs(CharacterBuffer currentToken)
	   {
		// skip column names
		String className = currentToken.toString();

		SendableEntityCreator creator = map.getCreator(className, false);
		currentToken = buffer.nextString();

		SimpleList<String> colNameList = new SimpleList<String>();

		while (currentToken.length() > 0 && currentToken.endsWith(":", true)) {
			colNameList.add(currentToken.rtrim(COLON).toString());
			buffer.skipChar(SPACE);
			if(buffer.getCurrentChar()==RETURN) {
				currentToken = buffer.nextString();
				break;
			}
			currentToken = buffer.nextString();
		}

		while (currentToken.length() > 0 && currentToken.equals("-") == false) {
			String objectId = currentToken.rtrim(COLON).toString();
			currentToken = buffer.nextString();

			Object obj = map.getObject(objectId);
			if(obj == null) {
				obj = creator.getSendableInstance(false);
				map.put(objectId, obj, false);
			}

			// column values
			int colNum = 0;
			while (currentToken.length() > 0 && !currentToken.endsWith(":", true) && currentToken.equals("-") == false) {
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
						if (!value.trim().equals("")) {
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
				System.out.println(currentToken);
				// currentToken = buffer.nextString();
			}
		}
	}

	private Object parseUsualObjectAttrs(CharacterBuffer currentToken) {
		String objectId = currentToken.trimEnd(1).toString();
		String className = buffer.nextString().rtrim().toString();
		currentToken = buffer.nextString();

		SendableEntityCreator creator = map.getCreator(className, false);

		Object obj = map.getObject(objectId);
		if(obj == null) {
			obj = creator.getSendableInstance(false);
			map.put(objectId, obj, false);
		}

		// read attributes
		while (!currentToken.equals("") && !currentToken.equals("-")) {
			String attrName = currentToken.rtrim(':').toString();
			currentToken = buffer.nextString();
			// many values
			while (!currentToken.equals("") && !currentToken.endsWith(":", true) && !currentToken.equals("-")) {
				String attrValue = currentToken.toString();
				setValue(creator, obj, attrName, attrValue);
				currentToken = buffer.nextString();
			}
			if(currentToken.equals("-")) {
				buffer.withLookAHead('-');
			}
		}
		return obj;
	}
	
	private boolean setValue(SendableEntityCreator creator, Object obj, String attrName, String attrValue)  {
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
			if(values != null) {
				SimpleList<String> valueNames = values.get(attrName);
				if(valueNames != null) {
					valueNames.add(attrValue);
					return true;
				}
			}
			creator.setValue(obj, attrName, attrValue, SendableEntityCreator.NEW);
		} catch (Exception e) {
			// maybe a node
			if (values == null) {
				values = new SimpleKeyValueList<String, SimpleList<String>>();
				refs.put(obj, values);
			}
			SimpleList<String> valueNames = values.get(attrName);
			if(valueNames != null) {
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
