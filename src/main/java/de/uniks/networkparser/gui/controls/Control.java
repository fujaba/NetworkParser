package de.uniks.networkparser.gui.controls;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.interfaces.UpdateListener;

public abstract class Control extends SimpleObject {
	/* Constants */
	public static final String PROPERTY = "property";


	public Control() {
		this.addBaseElements(PROPERTY);
	}

	/* Variables */
	protected String property;


	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}


	/**
	 * @param value the property to set
	 * @return success for setting
	 */
	public boolean setProperty(String value) {
		if (this.property != value) {
			this.property = value;
			return true;
		}
		return false;
	}


	public Object getValue(String key) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.property;
		}
		return super.getValue(key);
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			this.property = "" + value;
			return true;
		}
		return super.setValue(key, value);
	}

	private Map<String, List<UpdateListener>> eventListeners = null;


	public void addEventListener(String method, UpdateListener updateListener) {
		if (this.eventListeners == null) {
			this.eventListeners = new LinkedHashMap<>();
		}

		if (!this.eventListeners.containsKey(method)) {
			this.eventListeners.put(method, new LinkedList<>());
		}

		List<UpdateListener> list = this.eventListeners.get(method);

		list.add(updateListener);
	}


	public void fireEvent(String method, Object value) {
		if (!this.eventListeners.containsKey(method)) {
			return;
		}
		List<UpdateListener> list = this.eventListeners.get(method);
		if (list == null) {
			return;
		}

		for (UpdateListener updateListener : list) {
			updateListener.update(value);
		}
	}
}
