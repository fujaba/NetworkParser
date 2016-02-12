package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Or;

public class RegTest {
	@Test
	public void testRegEx(){
		String reg="[abc]";
		CharacterBuffer item= new CharacterBuffer();
		item.with(reg);
		UpdateListener root = parseCurrentChar(item);
		Assert.assertNotNull(root);
	}

	public UpdateListener parseCurrentChar(CharacterBuffer item){
		char ch = item.getCurrentChar();
		if(ch=='['){
			// OR-Item
			Or or = new Or();
			item.skip();
			while (ch!=']'){
				or.add( parseCurrentChar(item));
				ch = item.getChar();
			}
			return or;
		}

		return new Equals().withValue("" +ch);
	}
}
