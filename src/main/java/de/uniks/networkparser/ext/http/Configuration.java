package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class Configuration implements SendableEntityCreator {
	public static final String PORT ="port";
	public static final String SETTINGS ="settings";
	private static final String[] properties = new String[] {PORT, SETTINGS};
	private SimpleKeyValueList<String, SendableEntityCreator> settings;
	private int port;

	@Override
	public String[] getProperties() {
		return properties;
	}
	
	@Override
	public Object getSendableInstance(boolean arg0) {
		return new Configuration();
	}
	
	@Override
	public Object getValue(Object element, String property) {
		if(PORT.equalsIgnoreCase(property)) {
			return ((Configuration) element).getPort();
		}
		if(SETTINGS.equalsIgnoreCase(property)) {
			return ((Configuration) element).getSettings();
		}
		return null;
	}
	
	public SimpleKeyValueList<String, SendableEntityCreator> getSettings() {
		return settings;
	}
	
	public Configuration withSettings(SimpleKeyValueList<String, SendableEntityCreator> settings) {
		this.settings = settings;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setValue(Object element, String property, Object value, String type) {
		if(PORT.equalsIgnoreCase(property)) {
			((Configuration) element).withPort((int) value);
			return true;
		}
		if(SETTINGS.equalsIgnoreCase(property)) {
			((Configuration) element).withSettings((SimpleKeyValueList<String, SendableEntityCreator>) value);
			return true;
		}
		return false;
	}

	public Integer getPort() {
		return port;
	}

	public Configuration withPort(Integer port) {
		this.port = port;
		return this;
	}
	
	public static final Configuration defaultConfig() {
		return new Configuration().withPort(80);
	}

	public Configuration withSetting(String key, SendableEntityCreator setting) {
		if(settings != null) {
			settings = new SimpleKeyValueList<String, SendableEntityCreator>();
		}
		settings.add(key, setting);
		return this;
	}
	
	public SendableEntityCreator getSetting(String key) {
		if(settings != null) {
			return settings.get(key);
		}
		return null;
	}
}

