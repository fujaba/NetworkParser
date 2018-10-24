package de.uniks.networkparser.converter;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

public class CodeCityConverter implements Converter {
/*Root := Document ?
	   Document := OPEN ElementNode \* CLOSE
			   ElementNode := OPEN NAME Serial ? AttributeNode \* CLOSE
			   Serial := OPEN ID INTEGER CLOSE
			   AttributeNode := OPEN Name ValueNode \* CLOSE
			   ValueNode := Primitive | Reference | ElementNode
			   Primitive := STRING | NUMBER | Boolean | Unlimited
			   Boolean := TRUE | FALSE
			   Unlimited := NIL
			   Reference := IntegerReference | NameReference
			   IntegerReference := OPEN REF INTEGER CLOSE
			   NameReference := OPEN REF NAME CLOSE
			   OPEN := "("
			   CLOSE := ")"
			   ID := "id:"
			   REF := "ref:"
			   TRUE := "true"
			   FALSE := "false"
			   NAME := letter ( letter | digit ) \* ( "." letter ( letter | digit ) ) \*
			   INTEGER := digit +
			   NUMBER := "-" ? digit + ( "." digit + ) ? ( ( "e" | "E" ) ( "-" | "+" ) ? digit + ) ?
			   STRING := ( "'" \[^'] \* "'" ) +
			   digit := \[0-9] 
			   letter := \[a-zA-Z_]
			Whitespace are the usual suspects, and comments are

			 comment := "\"" \[^"] \* "\""
*/
	@Override
	public String encode(BaseItem entity) {
		CharacterBuffer buffer=new CharacterBuffer();
//		if(entity)
//		buffer.with//		(Moose.Model (sourceLanguage 'Java') (entity 
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void addElement(CharacterBuffer buffer, String key, Object value) {
		
		
	}

}
