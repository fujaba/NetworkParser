package de.uniks.networkparser.test;

import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertNotNull(root);
	}

	
	@Test
	public void testSS()  {
		TreeSet<Integer> treeSet = new TreeSet<>();
		treeSet.add(new Integer(1));
		treeSet.add(new Integer(42));
		treeSet.add(new Integer(23));
		System.out.println(new Integer(1).compareTo(new Integer(42)));
		System.out.println(new Integer(42).compareTo(new Integer(1)));
		
		for(Integer value : treeSet) {
			System.out.println(value);
		}
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
