package de.uniks.networkparser.ext.javafx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class AWTListener implements ActionListener {
	private ObjectCondition listener;
	
	public AWTListener withListener(ObjectCondition value) {
		this.listener = value;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(listener != null) {
			listener.update(e);
		}
	}

}
