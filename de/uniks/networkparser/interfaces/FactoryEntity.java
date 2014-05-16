package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.AbstractList;

public interface FactoryEntity {
	public AbstractList<?> getNewArray();

	public BaseItem getNewObject();
}
