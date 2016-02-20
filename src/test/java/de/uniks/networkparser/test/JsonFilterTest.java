package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.test.model.FullMessage;
import de.uniks.networkparser.test.model.Location;
import de.uniks.networkparser.test.model.util.FullMessageCreator;
import de.uniks.networkparser.test.model.util.LocationCreator;

public class JsonFilterTest {
	@Test
	public void testJsonFilterRegard(){
		FullMessage fullMessage = new FullMessage();
		fullMessage.setText("Hallo Welt");

		fullMessage.setLocation(new Location(1,2));
		IdMap map= new IdMap();
		map.with(new FullMessageCreator());
		map.with(new LocationCreator());

		// Pre
		assertEquals("Hallo Welt", fullMessage.getText());
		assertEquals("1:2", fullMessage.getLocation().toStringShort());

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"J1.F1\",\"prop\":{\"txt\":\"Hallo Welt\",\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"J1.L2\",\"prop\":{\"x\":1,\"y\":2}}}}", map.toJsonObject(fullMessage).toString());

		// Post
		assertEquals("Hallo Welt", fullMessage.getText());
	}
}
