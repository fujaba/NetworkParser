package de.uniks.networkparser.test;


import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.ColumnListener;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ColumnTest {

	@Test
	public void testColumn(){
		Column column=new Column();
		Assert.assertTrue("Must be True", column.getListener().canEdit(null, null));
		column.withEditable(false);
		Assert.assertFalse("Must be False", column.getListener().canEdit(null, null));
	}
	
	@Test
	public void testColumnListenerTrue(){
		Column column=new Column().withListener(new ColumnListener(){
			@Override
			public boolean canEdit(Object entity, SendableEntityCreator creator) {
				return false;
			}
		});
		Assert.assertFalse("Must be False", column.getListener().canEdit(null, null));
	}
}
