package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.test.model.University;

public class ReflectionModelIdMap {
	@Test
	public void testModel() {
		IdMap map=new IdMap();
		int size = map.getCreators().size();
		GenericCreator.create(map, University.class);
		Assert.assertEquals(size+3, map.getCreators().size());
	}
}
