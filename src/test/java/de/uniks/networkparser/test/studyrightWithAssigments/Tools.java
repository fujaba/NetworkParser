package de.uniks.networkparser.test.studyrightWithAssigments;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class Tools {

	@Test
	public void test() {
		CharacterBuffer buffer = FileBuffer.readFile("build/checkStyle.xml");
		XMLEntity root = new XMLEntity().withValue(buffer);
//		int count = 0;
		SimpleKeyValueList<String, Integer> errors=new SimpleKeyValueList<String, Integer>();
		for(int f=0;f<root.sizeChildren();f++) {
			XMLEntity file = (XMLEntity) root.getChild(f);
			for(int e=0;e<file.sizeChildren();e++) {
				XMLEntity error = (XMLEntity) file.getChild(e);
				errors.increment(error.getString("message"));
//				count++;
			}
		}
//		for(int i=0;i<errors.size();i++) {
//			if(errors.getValueByIndex(i)>5) {
//				System.out.println(errors.getKeyByIndex(i)+": "+errors.getValueByIndex(i));
//			}
//		}
		
//		System.out.println("ERRORS: "+count);
	}
}
