package de.uniks.networkparser.logic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.list.ConditionSet;
import de.uniks.networkparser.list.SimpleSet;

public abstract class ListCondition implements ParserCondition, SendableEntityCreator {
	public static final String CHILD = "childs";
	protected Object list;
	protected Object staticEvent;
	protected boolean chain = true;

	public ListCondition withStaticEvent(Object event) {
		this.staticEvent = event;
		return this;
	}
	@Override
	public boolean update(Object evt) {
		if(this.staticEvent != null) {
			evt = this.staticEvent;
		}
		if(evt instanceof PropertyChangeEvent) {
			return updatePCE((PropertyChangeEvent) evt);
		}
		return updateSet(evt);
	}

	public boolean updateSet(Object evt) {
		Set<ObjectCondition> list = getList();
		boolean result=true;
		for(ObjectCondition item : list) {
			if(item.update(evt) == false) {
				if(chain == false) {
					return false;
				}
				result = false;
			}
		}
		return result;
	}

	public boolean updatePCE(PropertyChangeEvent evt) {
		if(list instanceof PropertyChangeListener) {
			((PropertyChangeListener)list).propertyChange(evt);
			return true;
		} else if(list instanceof ObjectCondition) {
			return ((ObjectCondition)list).update(evt);
		}
		SimpleSet<?> collection = (SimpleSet<?>) this.list;

		for(Iterator<?> i = collection.iterator();i.hasNext();) {
			Object listener = i.next();
			if(listener instanceof ObjectCondition) {
				if(((ObjectCondition)listener).update(evt) == false) {
					if(chain) {
						return false;
					}
				}
			} else if(listener instanceof PropertyChangeListener) {
				((PropertyChangeListener)listener).propertyChange(evt);
			}
		}
		return true;
	}

	public ListCondition with(ObjectCondition... values) {
		add((Object[])values);
		return this;
	}

	public ListCondition with(PropertyChangeListener... values) {
		add((Object[])values);
		return this;
	}

	public boolean add(Object... values) {
		if(values == null || values.length < 1) {
			return false;
		}
		if(values.length == 1 && this.list == null) {
			// Dont do Chain in Chain
			if(values[0] instanceof ChainCondition == false) {
				if(values[0] instanceof PropertyChangeListener || values[0] instanceof ObjectCondition) {
					this.list = values[0];
				}
				return true;
			}
		}
		SimpleSet<?> list;
		if(this.list instanceof SimpleSet<?>) {
			list = (SimpleSet<?>) this.list;
		} else {
			if(values[0] instanceof PropertyChangeListener) {
				list = new SimpleSet<PropertyChangeListener>();
			} else {
				list = new ConditionSet();
			}
			list.with(this.list);
			this.list = list;
		}
		if(list instanceof ConditionSet) {
			for(Object condition : values) {
				if(condition instanceof ChainCondition) {
					ChainCondition cc = (ChainCondition) condition;
					list.withList(cc.getList());
				} else if(condition instanceof ObjectCondition) {
					if(list.add((ObjectCondition)condition) == false) {
						return false;
					}
				}
			}
			return true;
		}
		return list.add(values);
	}

	public ConditionSet getList() {
		if(this.list instanceof ConditionSet) {
			return (ConditionSet)this.list;
		}
		ConditionSet  result = new ConditionSet();
		result.with(this.list);
		return result;
	}

	public void clear() {
		this.list = null;
	}

	public ObjectCondition first() {
		if(this.list instanceof ObjectCondition) {
			return (ObjectCondition) this.list;
		} else if(this.list instanceof SimpleSet<?>) {
			Object first = ((SimpleSet<?>) this.list).first();
			if(first instanceof ObjectCondition) {
				return (ObjectCondition) first;
			}
		}
		return null;
	}

	public int size() {
		if(this.list==null) {
			return 0;
		}else if(this.list instanceof Collection<?>) {
			return ((Collection<?>)this.list).size();
		}
		return 1;
	}

	@Override
	public String toString() {
		Set<ObjectCondition> templates = getList();
		if(templates.size()>0) {
			CharacterBuffer buffer=new CharacterBuffer();
			for(ObjectCondition item : templates) {
				buffer.with(item.toString());
			}
			return buffer.toString();
		}
		return super.toString();
	}

	@Override
	public String[] getProperties() {
		return new String[] {CHILD};
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof ChainCondition == false) {
			return false;
		}
		ChainCondition cc = (ChainCondition) entity;
		if (CHILD.equalsIgnoreCase(attribute)) {
			return cc.getList();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof ChainCondition == false) {
			return false;
		}
		ChainCondition cc = (ChainCondition) entity;
		if (CHILD.equalsIgnoreCase(attribute)) {
			cc.add(value);
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		return getList().getAllValue(variables);
	}
}
