package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.ListEntity;

public class JsonRekursivTest {
	 @Test
	 public void testSimple(){
		 ListEntity root= new ListEntity();
		 ListEntity test = new ListEntity();
		 ListEntity child = new ListEntity().withOwner(new ListEntity().withOwner(test));
		 root.withChildren(new ListEntity(), child);
		 root.withChildren(child);
		 child.withChildren(root);
		 IdMap map= new IdMap();
		 map.with(new ListEntity());
		 JsonObject json =map.toJsonObject(root);
		 Assert.assertEquals(1318, json.toString(2).length());

		 IdMap mapDecode= new IdMap();
		 mapDecode.with(new ListEntity());
		 Object rootDecode = mapDecode.decode(json);
		 Assert.assertNotNull(rootDecode);
	 }
}
