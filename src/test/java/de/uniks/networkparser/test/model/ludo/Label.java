/*
   Copyright (c) 2018 Stefan

   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.

   The Software shall be used for Good, not Evil.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.test.model.ludo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.list.SimpleSet;

public class Label implements SendableEntity {

	// ==========================================================================

	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	@Override
   public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	@Override
   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	@Override
   public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		return true;
	}

	@Override
   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	// ==========================================================================

	public void removeYou() {
		withoutField(this.getField().toArray(new Field[this.getField().size()]));
		firePropertyChange("REMOVE_YOU", this, null);
	}

	// ==========================================================================

	public static final String PROPERTY_NAME = "name";

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		if (!StringUtil.stringEquals(this.name, value)) {

			String oldValue = this.name;
			this.name = value;
			this.firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public Label withName(String value) {
		setName(value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(" ").append(this.getName());
		return result.substring(1);
	}

	/********************************************************************
	 * <pre>
	 *              many                       many
	 * Label ----------------------------------- Field
	 *              label                   field
	 * </pre>
	 */

	public static final String PROPERTY_FIELD = "field";

	private SimpleSet<Field> field = null;

	public SimpleSet<Field> getField() {
		if (this.field == null) {
			return new SimpleSet<Field>().withFlag(SimpleSet.READONLY);
		}
		return this.field;
	}

	public Label withField(Field... value) {
		if (value == null) {
			return this;
		}
		for (Field item : value) {
			if (item != null) {
				if (this.field == null) {
					this.field = new SimpleSet<Field>();
				}

				boolean changed = this.field.add(item);

				if (changed) {
					item.withLabel(this);
					firePropertyChange(PROPERTY_FIELD, null, item);
				}
			}
		}
		return this;
	}

	public Label withoutField(Field... value) {
		for (Field item : value) {
			if ((this.field != null) && (item != null)) {
				if (this.field.remove(item)) {
					item.withoutLabel(this);
					firePropertyChange(PROPERTY_FIELD, item, null);
				}
			}
		}
		return this;
	}

	public Field createField() {
		Field value = new Field();
		withField(value);
		return value;
	}
}
