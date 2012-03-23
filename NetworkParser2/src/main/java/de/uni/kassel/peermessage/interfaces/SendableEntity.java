package de.uni.kassel.peermessage.interfaces;

import java.beans.PropertyChangeListener;

public interface SendableEntity {
	 public void addPropertyChangeListener(
             String propertyName,
             PropertyChangeListener listener);
}
