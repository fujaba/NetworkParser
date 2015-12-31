package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.SimpleConditionValue;
import de.uniks.networkparser.logic.SimpleValues;

public class RegTest {
	@Test
	public void testRegEx(){
		String reg="[abc]";
		StringTokener item= new StringTokener();
		item.withBuffer(reg);
		Condition<SimpleValues> root = parseCurrentChar(item);
		Assert.assertNotNull(root);
	}

	public SimpleConditionValue parseCurrentChar(StringTokener item){
		char ch = item.getCurrentChar();
		if(ch=='['){
			// OR-Item
			Or or = new Or();
			item.next();
			while (ch!=']'){
				or.add( parseCurrentChar(item));
				ch = item.next();
			}
			return or;
		}
		
		return new Equals().withValue("" +ch);
	}
}
