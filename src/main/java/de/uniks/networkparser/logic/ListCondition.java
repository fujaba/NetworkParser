package de.uniks.networkparser.logic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class ListCondition.
 *
 * @author Stefan
 */
public abstract class ListCondition implements ParserCondition, SendableEntityCreator {
	
	/** The Constant CHILD. */
	public static final String CHILD = "childs";
	protected Object list;
	protected Object staticEvent;
	protected boolean chain = true;

	/**
	 * With static event.
	 *
	 * @param event the event
	 * @return the list condition
	 */
	public ListCondition withStaticEvent(Object event) {
		this.staticEvent = event;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object evt) {
		if (this.staticEvent != null) {
			evt = this.staticEvent;
		}
		if (evt instanceof PropertyChangeEvent) {
			return updatePCE((PropertyChangeEvent) evt);
		}
		return updateSet(evt);
	}

	/**
	 * Update set.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	public boolean updateSet(Object evt) {
		Set<ObjectCondition> list = getList();
		boolean result = true;
		for (ObjectCondition item : list) {
			if (!item.update(evt)) {
				if (!chain) {
					return false;
				}
				result = false;
			}
		}
		return result;
	}

	/**
	 * Update PCE.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	public boolean updatePCE(PropertyChangeEvent evt) {
		if (list instanceof PropertyChangeListener) {
			((PropertyChangeListener) list).propertyChange(evt);
			return true;
		} else if (list instanceof ObjectCondition) {
			return ((ObjectCondition) list).update(evt);
		}
		SimpleSet<?> collection = (SimpleSet<?>) this.list;

		for (Iterator<?> i = collection.iterator(); i.hasNext();) {
			Object listener = i.next();
			if (listener instanceof ObjectCondition) {
				if (!((ObjectCondition) listener).update(evt)) {
					if (chain) {
						return false;
					}
				}
			} else if (listener instanceof PropertyChangeListener) {
				((PropertyChangeListener) listener).propertyChange(evt);
			}
		}
		return true;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the list condition
	 */
	public ListCondition with(ObjectCondition... values) {
		add((Object[]) values);
		return this;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the list condition
	 */
	public ListCondition with(PropertyChangeListener... values) {
		add((Object[]) values);
		return this;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean add(Object... values) {
		if (values == null || values.length < 1) {
			return false;
		}
		if (values.length == 1 && this.list == null) {
			/* Dont do Chain in Chain */
			if (!(values[0] instanceof ChainCondition)) {
				if (values[0] instanceof PropertyChangeListener || values[0] instanceof ObjectCondition) {
					this.list = values[0];
				}
				return true;
			}
		}
		SimpleSet<?> list;
		if (this.list instanceof SimpleSet<?>) {
			list = (SimpleSet<?>) this.list;
		} else {
			if (values[0] instanceof PropertyChangeListener) {
				list = new SimpleSet<PropertyChangeListener>();
			} else {
				list = new ConditionSet();
			}
			list.with(this.list);
			this.list = list;
		}
		if (list instanceof ConditionSet) {
			for (Object condition : values) {
				if (condition instanceof ChainCondition) {
					ChainCondition cc = (ChainCondition) condition;
					list.withList(cc.getList());
				} else if (condition instanceof ObjectCondition) {
					if (!list.add((ObjectCondition) condition)) {
						return false;
					}
				} else if(condition instanceof ListCondition) {
					ListCondition listCon = (ListCondition) condition;
					SimpleSet<ObjectCondition> list2 = listCon.getList();
					if(list2 != null) {
						for(ObjectCondition con : list2) {
							list.add(con);
						}
					}
				}
			}
			return true;
		}
		return list.add(values);
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public ConditionSet getList() {
		if (this.list instanceof ConditionSet) {
			return (ConditionSet) this.list;
		}
		ConditionSet result = new ConditionSet();
		result.with(this.list);
		return result;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.list = null;
	}

	/**
	 * First.
	 *
	 * @return the object condition
	 */
	public ObjectCondition first() {
		if (this.list instanceof ObjectCondition) {
			return (ObjectCondition) this.list;
		} else if (this.list instanceof SimpleSet<?>) {
			Object first = ((SimpleSet<?>) this.list).first();
			if (first instanceof ObjectCondition) {
				return (ObjectCondition) first;
			}
		}
		return null;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		if (this.list == null) {
			return 0;
		} else if (this.list instanceof Collection<?>) {
			return ((Collection<?>) this.list).size();
		}
		return 1;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		Set<ObjectCondition> templates = getList();
		if (templates.size() > 0) {
			CharacterBuffer buffer = new CharacterBuffer();
			for (ObjectCondition item : templates) {
				buffer.with(item.toString());
			}
			return buffer.toString();
		}
		return super.toString();
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { CHILD };
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof ChainCondition)) {
			return false;
		}
		ChainCondition cc = (ChainCondition) entity;
		if (CHILD.equalsIgnoreCase(attribute)) {
			return cc.getList();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof ChainCondition)) {
			return false;
		}
		ChainCondition cc = (ChainCondition) entity;
		if (CHILD.equalsIgnoreCase(attribute)) {
			cc.add(value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	@Override
	public Object getValue(LocalisationInterface variables) {
		return getList().getAllValue(variables);
	}
}
