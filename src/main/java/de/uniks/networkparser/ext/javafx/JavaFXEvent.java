package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class JavaFXEvent {
	private ObjectCondition listerner;
	
	public JavaFXEvent with(ObjectCondition value) {
		this.listerner = value;
		return this;
	}
	
	public void handle(Object event) {
		if(this.listerner != null) {
			this.listerner.update(event);
		}
	}
}
