package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.interfaces.CellHandler;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;

public class ColumnTest {

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

		JsonIdMap map=(JsonIdMap) new JsonIdMap().with(new Column());

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
