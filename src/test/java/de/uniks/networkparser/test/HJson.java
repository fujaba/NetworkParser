package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.json.JsonObject;

public class HJson {

	@Test
	public void testHJson() {
		CharacterBuffer buffer = new CharacterBuffer();
		buffer.withLine("{");
		buffer.withLine("# hjson style comment");
		buffer.withLine("foo1: This is a string value. # part of the string");
		buffer.withLine("foo2: \"This is a string value.\" # a comment");
		buffer.withLine("// js style comment");
		buffer.withLine("bar1: This is a string value. // part of the string");
		buffer.withLine("bar2: \"This is a string value.\" // a comment");
		buffer.withLine("/* js block style comments */foobar1:/* more */This is a string value./* part of the string */");
		buffer.withLine("/* js block style comments */foobar2:/* more */\"This is a string value.\"/* a comment */");
		buffer.withLine("}");
		JsonObject json=new JsonObject().withValue(buffer);
		System.out.println(json);
	}

}
