package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
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
}
