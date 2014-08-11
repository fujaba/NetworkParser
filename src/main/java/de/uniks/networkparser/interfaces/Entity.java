package de.uniks.networkparser.interfaces;

public interface Entity {
	public boolean has(String key);
	public String getString(String key);
	public boolean getBoolean(String key);
	public double getDouble(String key);
	public int getInt(String key);
	public Object get(Object key);
}
