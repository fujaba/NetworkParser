package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;

public class SortedMsg implements SendableEntity, Comparable<SortedMsg>{
	public static final String PROPERTY_ID="number";
	public static final String PROPERTY_CHILD="child";
	public static final String PROPERTY_MSG="msg";
	public static final String PROPERTY_PARENT="parent";
	private int number;
	private SortedMsg child;
	private SortedMsg parent;
	private String msg;

	public int getNumber() {
		return number;
	}

	public SortedMsg withNumber(int id) {
		int oldValue=this.number;
		this.number = id;
		if(id!=oldValue){
			firePropertyChange(SortedMsg.PROPERTY_ID, oldValue, id);
		}
		return this;
	}
	public void updateNumber(int id) {
		this.number = id;
	}

	public SortedMsg getChild() {
		return child;
	}

	public void setChild(SortedMsg value) {

		if(value!=this.child){
			SortedMsg oldValue=this.child;
			this.child = value;
			firePropertyChange(SortedMsg.PROPERTY_CHILD, oldValue, value);
			if(value!=null){
				value.setParent(this);
			}
		}
	}

	public void setParent(SortedMsg value) {
		if(value!=this.parent){
			SortedMsg oldValue=this.parent;
			this.parent = value;
			firePropertyChange(SortedMsg.PROPERTY_PARENT, oldValue, value);
			if(value!=null){
				value.setChild(this);
			}
		}
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			withNumber((Integer) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_CHILD)) {
			setChild((SortedMsg) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_PARENT)) {
			setParent((SortedMsg) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_MSG)) {
			setMsg((String) value);
			return true;
		}
		return false;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			return getNumber();
		}
		if (attribute.equalsIgnoreCase(PROPERTY_CHILD)) {
			return getChild();
		}
		if (attribute.equalsIgnoreCase(PROPERTY_PARENT)) {
			return getParent();
		}
		if (attribute.equalsIgnoreCase(PROPERTY_MSG)) {
			return getMsg();
		}
		return null;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public SortedMsg withMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public SortedMsg getParent() {
		return parent;
	}

	protected PropertyChangeSupport listeners = null;
	
	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		return true;
	}

	public boolean removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(property, listener);
		}
		return true;
	}

	@Override
	public int compareTo(SortedMsg o) {
		if(o.getNumber()==this.number) {
			return 0;
		}
		if(o.getNumber()>this.number) {
			return -1;
		}
		return 1;
	}

	@Override
	public String toString() {
		return this.number+": "+this.msg;
	}
}
