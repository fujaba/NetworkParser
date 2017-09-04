package de.uniks.networkparser.yaml;

import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;

public class YAMLTokener extends Tokener {
	public final static String STOPCHARS = "=# ";
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
}
