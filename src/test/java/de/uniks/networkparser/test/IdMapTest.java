package de.uniks.networkparser.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.ludo.Ludo;
import de.uniks.networkparser.test.model.ludo.creator.LudoCreator;
import de.uniks.networkparser.test.model.ludo.creator.PlayerCreator;

public class IdMapTest {
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
}
