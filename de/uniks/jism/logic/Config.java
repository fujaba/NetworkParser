package de.uniks.jism.logic;

import de.uniks.jism.IdMap;

public interface Config {
	public IdMap getMap();
	public Object getEntity();
	public Object getProperty();
	public Object getValue();
	public boolean isMany();
	public int getDeep();
}
