package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Or;

public class RegTest {
	@Test
	public void testRegEx(){
		String reg="[abc]";
		CharacterBuffer item= new CharacterBuffer();
		item.with(reg);
		ObjectCondition root = parseCurrentChar(item);
		assertNotNull(root);
	}

	public ObjectCondition parseCurrentChar(CharacterBuffer item){
		char ch = item.getCurrentChar();
		if(ch=='['){
			// OR-Item
			Or or = new Or();
			item.skip();
			while (ch!=']'){
				or.with( parseCurrentChar(item));
				ch = item.getChar();
			}
			return or;
		}

		return new Equals().withValue("" +ch);
	}
}
