package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;


/**
 * The Class Configuration.
 *
 * @author Stefan
 */
public class Configuration implements SendableEntityCreatorNoIndex, SendableEntityCreatorTag {
	
	/** The Constant PORT. */
	public static final String PORT ="port";
	
	/** The Constant SETTINGS. */
	public static final String SETTINGS ="settings";
	private static final String[] properties = new String[] {PORT, SETTINGS};
	private SimpleList<SendableEntityCreatorTag> settings;
	private int port;

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}
	
	/**
	 * Gets the sendable instance.
	 *
	 * @param arg0 the arg 0
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean arg0) {
		return new Configuration();
	}
	
	/**
	 * Gets the value.
	 *
	 * @param element the element
	 * @param property the property
	 * @return the value
	 */
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
	
	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	public SimpleList<SendableEntityCreatorTag> getSettings() {
		return settings;
	}
	
	/**
	 * With settings.
	 *
	 * @param settings the settings
	 * @return the configuration
	 */
	public Configuration withSettings(SimpleList<SendableEntityCreatorTag> settings) {
		this.settings = settings;
		return this;
	}

	/**
	 * Sets the value.
	 *
	 * @param element the element
	 * @param property the property
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
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

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * With port.
	 *
	 * @param port the port
	 * @return the configuration
	 */
	public Configuration withPort(Integer port) {
		this.port = port;
		return this;
	}
	
	/**
	 * Default config.
	 *
	 * @return the configuration
	 */
	public static final Configuration defaultConfig() {
		return new Configuration().withPort(80);
	}

	/**
	 * With setting.
	 *
	 * @param setting the setting
	 * @return the configuration
	 */
	public Configuration withSetting(SendableEntityCreatorTag setting) {
		if(settings == null) {
			settings = new SimpleList<SendableEntityCreatorTag>();
		}
		settings.add(setting);
		return this;
	}
	
	/**
	 * Gets the setting.
	 *
	 * @param key the key
	 * @return the setting
	 */
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

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return "config";
	}
}

