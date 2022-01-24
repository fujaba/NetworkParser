package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;


public class Configuration implements SendableEntityCreatorNoIndex, SendableEntityCreatorTag {
	public static final String PORT ="port";
	public static final String SETTINGS ="settings";
	private static final String[] properties = new String[] {PORT, SETTINGS};
	private SimpleList<SendableEntityCreatorTag> settings;
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
	
	public SimpleList<SendableEntityCreatorTag> getSettings() {
		return settings;
	}
	
	public Configuration withSettings(SimpleList<SendableEntityCreatorTag> settings) {
		this.settings = settings;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setValue(Object element, String property, Object value, String type) {
		if(PORT.equalsIgnoreCase(property)) {
			((Configuration) element).withPort((Integer) value);
			return true;
		}
		if(SETTINGS.equalsIgnoreCase(property)) {
			if(value instanceof SendableEntityCreatorTag) {
				((Configuration) element).withSetting((SendableEntityCreatorTag) value);
			} else if(value instanceof SimpleList<?>) {
				((Configuration) element).withSettings((SimpleList<SendableEntityCreatorTag>) value);
			}
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

	public Configuration withSetting(SendableEntityCreatorTag setting) {
		if(settings == null) {
			settings = new SimpleList<SendableEntityCreatorTag>();
		}
		settings.add(setting);
		return this;
	}
	
	public SendableEntityCreator getSetting(String key) {
		if(settings != null && key != null) {
			for(SendableEntityCreatorTag setting : settings) {
				if(key.equalsIgnoreCase(setting.getTag())) {
					return setting;
				}
			}
		}
		return null;
	}

	@Override
	public String getTag() {
		return "config";
	}
}

