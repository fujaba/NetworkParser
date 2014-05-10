package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.AbstractKeyValueList;

public interface FactoryEntity {
	public AbstractEntityList<?> getNewArray();

	public AbstractKeyValueList<?,?> getNewObject();
}
