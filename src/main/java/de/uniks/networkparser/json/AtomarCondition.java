package de.uniks.networkparser.json;
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.UpdateListener;

public class AtomarCondition implements UpdateListener{
	private UpdateListener filter;

	public AtomarCondition(UpdateListener listener) {
		this.filter = listener;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof PropertyChangeEvent ) {
			return filter.update(value);
		}
		return false;
	}
}
