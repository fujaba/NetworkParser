package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.CellHandler;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

public class GUITest {
	@Test
	public void testControlToJson() {
		Button button = new Button();
		IdMap map =new IdMap();
		map.withTimeStamp(1);
		JsonObject json = map.toJsonObject(button, Filter.createSimple());
		assertEquals("{\"class\":\"input\",\"id\":\"B1\",\"type\":\"button\"}", json.toString());
	}
	@Test
	public void testGUIPosition() {
		assertEquals(GUIPosition.CENTER.getValue(), "Center");
		assertEquals(GUIPosition.EAST.getValue(), "East");
		assertEquals(GUIPosition.SOUTH.getValue(), "South");
		assertEquals(GUIPosition.WEST.getValue(), "West");
		assertEquals(GUIPosition.NORTH.getValue(), "North");
		assertEquals(10, GUIPosition.values().length);
		assertTrue(GUIPosition.NORTH.equals(GUIPosition.valueOf("NORTH")));
	}

	@Test
	public void testColumn(){
		Column column= new Column();
		assertFalse(column.getListener().onAction(null, null, 0, 0), "Must be False");
		column.withEditable(true);
		assertTrue(column.getListener().onAction(null, null, 0, 0), "Must be True");
	}

	@Test
	public void testColumnSerialization(){
		Column column= new Column().withAttribute("Name").withResizable(true);
		column.withVisible(true);
		column.withMovable(true);
		assertEquals(column.getBrowserId(), GUIPosition.CENTER);

		IdMap map=new IdMap().with(new Column());

		assertEquals("{\"class\":\"de.uniks.networkparser.gui.Column\",\"attribute\":\"Name\"}", map.toJsonObject(column).toString());
	}

	@Test
	public void testColumnListenerTrue(){
		Column column= new Column().withActionHandler(new CellHandler() {

			@Override
			public boolean onAction(Object entity,
					SendableEntityCreator creator, double x, double y) {
				return false;
			}
		});
		assertFalse(column.getListener().onAction(null, null, 0, 0), "Must be False");
	}
}
