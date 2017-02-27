package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

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
		map.getCounter().withTimeStamp(1);
		JsonObject json = map.toJsonObject(button, Filter.SIMPLEFORMAT);
		Assert.assertEquals("{\"class\":\"input\",\"id\":\"B1\",\"timestamp\":\"1\",\"type\":\"button\"}", json.toString());
	}
	@Test
	public void testGUIPosition() {
		Assert.assertEquals(GUIPosition.CENTER.getValue(), "Center");
		Assert.assertEquals(GUIPosition.EAST.getValue(), "East");
		Assert.assertEquals(GUIPosition.SOUTH.getValue(), "South");
		Assert.assertEquals(GUIPosition.WEST.getValue(), "West");
		Assert.assertEquals(GUIPosition.NORTH.getValue(), "North");
		Assert.assertEquals(10, GUIPosition.values().length);
		Assert.assertTrue(GUIPosition.NORTH.equals(GUIPosition.valueOf("NORTH")));
	}

	@Test
	public void testColumn(){
		Column column= new Column();
		Assert.assertFalse("Must be False", column.getListener().onAction(null, null, 0, 0));
		column.withEditable(true);
		Assert.assertTrue("Must be True", column.getListener().onAction(null, null, 0, 0));
	}

	@Test
	public void testColumnSerialization(){
		Column column= new Column().withAttrName("Name").withResizable(true);
		column.withVisible(true);
		column.withMovable(true);
		Assert.assertEquals(column.getBrowserId(), GUIPosition.CENTER);

		IdMap map=new IdMap().with(new Column());

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.gui.Column\",\"attrName\":\"Name\"}", map.toJsonObject(column).toString());
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
		Assert.assertFalse("Must be False", column.getListener().onAction(null, null, 0, 0));
	}
}
