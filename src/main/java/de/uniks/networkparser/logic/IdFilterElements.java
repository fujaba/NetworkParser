package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class IdFilterElements extends SimpleList<Object> implements UpdateListener {
	private UpdateListener condition;


	public IdFilterElements(UpdateListener condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.value(clazzConditon);
	}

	@Override
	public boolean update(Object evt) {
		if(condition!=null) {
			if(condition.update(evt)) {
				return add(((PropertyChangeEvent)evt).getNewValue());
			}
		}
		return false;
	}
}
