package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.test.model.ludo.Ludo;
import de.uniks.networkparser.test.model.ludo.Player;
import de.uniks.networkparser.test.model.ludo.creator.LudoCreator;
import de.uniks.networkparser.test.model.ludo.creator.PlayerCreator;

public class IdMapTest {

	@Test
	public void testMap() {
		PlayerCreator playerCreator = new PlayerCreator();
		IdMap map= new IdMap().with(new LudoCreator()).with(playerCreator);
		Ludo ludo = new Ludo();
		Player albert = ludo.createPlayers().withName("Albert");
		for(int i=0;i<500;i++) {
			ludo.createPlayers().withName("Player"+i);
		}
		JsonObject jsonObject = map.toJsonObject(ludo);
		
		SimpleList<Object> typList = map.getTypList(playerCreator);
		System.out.println(typList);
	}
}
