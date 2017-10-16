package de.uniks.networkparser.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.ludo.Ludo;
import de.uniks.networkparser.test.model.ludo.Player;
import de.uniks.networkparser.test.model.ludo.creator.LudoCreator;
import de.uniks.networkparser.test.model.ludo.creator.PlayerCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;

public class IdMapTest 
{
   @Test
   public void testIdMapRemove()
   {
      PlayerCreator playerCreator = new PlayerCreator();
      
      Player tom = new Player();
      
      IdMap map = new IdMap().with(playerCreator);
      
      JsonObject jsonObject = map.toJsonObject(tom);
      
      jsonObject.remove("class");
      
      jsonObject.withValue("id", "42");
      
      String fortyTwo = jsonObject.getString("id");
      
      Assert.assertEquals("The answer ", "42", fortyTwo);
      
   }
	@Test
	public void testMap() {
		PlayerCreator playerCreator = new PlayerCreator();
		IdMap map= new IdMap().with(new LudoCreator()).with(playerCreator);
		map.withTimeStamp(1);
		Ludo ludo = new Ludo();
		for(int i=0;i<450;i++) {
			ludo.createPlayers().withName("Player"+i);
		}
		map.toJsonObject(ludo);

		SimpleList<Object> typList = map.getTypList(playerCreator);
		Assert.assertEquals(450, typList.size());
	}

	@Test
	public void testReflectionModelIdMap() {
		IdMap map=new IdMap();
		int size = map.getCreators().size();
		GenericCreator.create(map, University.class);
		Assert.assertEquals(size+3, map.getCreators().size());
	}
	@Test
	public void testWrapper() {
		SendableEntityCreatorWrapper wrapper =new SendableEntityCreatorWrapper(){
			@Override
			public String[] getProperties() {
				return new String[]{"VALUE"};
			}

			@Override
			public Object getValue(Object entity, String attribute) {
				return ((Date)entity).getTime();
			}

			@Override
			public Object getSendableInstance(boolean prototyp) {
				return new Date();
			}

			@Override
			public Object newInstance(Entity item) {
				Object value = item.getValue("VALUE");
				if(value instanceof Long) {
					return new Date((long) value);
				}
				return null;
			}
		};
		Date date = new Date();
		wrapper.setValue(date, "VALUE", 200, SendableEntityCreator.NEW); 
	}
	
	@Test
	public void testReuseIdMap() {
		Student alice = new Student().withName("alice");
		
		IdMap idMap = new IdMap().withCreator(new StudentCreator());
		JsonArray jsonArray = idMap.toJsonArray(alice);
		JsonObject jsonObject = (JsonObject) jsonArray.get(0);
		String id = (String) jsonObject.get(IdMap.ID);
		
		IdMap newIdMap = new IdMap().with(idMap);
		
		jsonArray = newIdMap.toJsonArray(alice);
		jsonObject = (JsonObject) jsonArray.get(0);
		
		String newId = (String) jsonObject.get(IdMap.ID);

		Assert.assertEquals(id, newId);
	}
	
}
