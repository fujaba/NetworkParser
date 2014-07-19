package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.Barbarian;
import de.uniks.networkparser.test.model.creator.BarbarianCreator;
import de.uniks.networkparser.test.model.creator.GameCreator;

public class MinimalJsonTest {

	@Test
	public void testServerJson(){
		String json = "{\"@ts\":\"1368185625179\",\"@src\":\"Barbarian@2b40c3b9\",\"@prop\":\"position\",\"@nv\":\"42\"}";
		
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new BarbarianCreator());
		map.withCreator(new GameCreator());
		map.withGrammar(new EMFGrammar());
		
		Barbarian barbar = (Barbarian) map.decode(new JsonObject().withValue(json));
		
		Assert.assertNotNull(barbar);
		Assert.assertEquals(barbar.getPosition(), 42);
		
		Assert.assertNull(barbar.getGame());
		json = "{\"@ts\":\"1368185625179\",\"@src\":\"Barbarian@2b40c3b9\",\"@prop\":\"game\",\"@nv\":\"Game@55a92d3a\"}";
		
		map.decode(new JsonObject().withValue(json));
		
		Assert.assertNotNull(barbar.getGame());
//		Assert.assertEquals(barbar.getGame(), game);
	}
}
