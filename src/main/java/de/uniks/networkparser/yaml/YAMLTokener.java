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
			buffer.nextClean(true);
			CharacterBuffer key = buffer.nextString();
			if (key.endsWith(":", true)) {
				// usual
				parseUsualObjectAttrs(key);
				continue;
			} else {
				parseObjectTableAttrs(key);
				continue;
			}
		}
		return root;
	}


	   private void parseObjectTableAttrs(CharacterBuffer key)
	   {
//	      // skip column names
//	      String className = currentToken;
//	      
//	      SendableEntityCreator creator = getCreator(className);
//	      nextToken();
//	      
//	      ArrayList<String> colNameList = new ArrayList<String>();
//
//	      while ( ! "".equals(currentToken) && lookAheadToken.endsWith(":"))
//	      {
//	         String colName = stripColon(currentToken);
//	         colNameList.add(colName);
//	         nextToken();
//	      }
//	      
//	      while ( ! "".equals(currentToken) && ! "-".equals(currentToken))
//	      {
//	         String objectId = stripColon(currentToken);
//	         nextToken();
//
//	         Object obj = objIdMap.get(objectId);
//	         
//	         // column values
//	         int colNum = 0;
//	         while ( ! "".equals(currentToken) && ! currentToken.endsWith(":") && ! "-".equals(currentToken))
//	         {
//	            String attrName = colNameList.get(colNum);
//	            
//	            if (currentToken.startsWith("["))
//	            {
//	               String value = currentToken.substring(1);
//	               if (value.trim().equals(""))
//	               {
//	                  value = nextToken();
//	               }
//	               setValue(creator, obj, attrName, value);
//	               
//	               while (! "".equals(currentToken) && ! currentToken.endsWith("]") )
//	               {
//	                  nextToken();
//	                  value = currentToken;
//	                  if (currentToken.endsWith("]"))
//	                  {
//	                     value = currentToken.substring(0, currentToken.length()-1);
//	                  }
//	                  if ( ! value.trim().equals(""))
//	                  {
//	                     setValue(creator, obj, attrName, value);
//	                  }
//	               }
//	            }
//	            else
//	            {
//	               setValue(creator, obj, attrName, currentToken);
//	            }
//	            colNum++;
//	            nextToken();
//	         }
//	      }
	   }

	private void parseUsualObjectAttrs(CharacterBuffer key) {
		String objectId = key.trimEnd(1).toString();
		buffer.nextClean(true);
		String className = buffer.nextString().rtrim().toString();
		buffer.nextClean(true);
		CharacterBuffer currentToken = buffer.nextString();

		SendableEntityCreator creator = map.getCreator(className, false);

		Object obj = map.getObject(objectId);
		if(obj == null) {
			obj = creator.getSendableInstance(false);
			map.put(objectId, obj, false);
		}

		// read attributes
		while (!currentToken.equals("") && !currentToken.equals("-")) {
			String attrName = currentToken.rtrim(':').toString();
			buffer.nextClean(true);
			currentToken = buffer.nextString();
			// many values
			while (!currentToken.equals("") && !currentToken.endsWith(":", true) && !currentToken.equals("-")) {
				String attrValue = currentToken.toString();
				setValue(creator, obj, attrName, attrValue);
				buffer.nextClean(true);
				currentToken = buffer.nextString();
			}
			if(currentToken.equals("-")) {
				buffer.withLookAHead('-');
			}
		}
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
