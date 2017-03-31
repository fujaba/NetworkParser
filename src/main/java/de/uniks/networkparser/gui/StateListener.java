package de.uniks.networkparser.gui;

public class StateListener {
	private JavaViewAdapter owner;
	public static final String SUCCEEDED = "SUCCEEDED";
	
	
	public StateListener(JavaViewAdapter owner) {
		this.owner = owner;
	}
	public void changed(Object observable, Object oldValue, Object newValue) {
		if(SUCCEEDED.equals(""+newValue)) {
			// FINISH
			this.owner.loadFinish();
		}
	}
}
