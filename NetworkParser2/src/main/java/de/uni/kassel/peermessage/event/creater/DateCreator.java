package de.uni.kassel.peermessage.event.creater;

import java.util.Date;

import de.uni.kassel.peermessage.interfaces.NoIndexCreator;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;

public class DateCreator implements SendableEntityCreator, NoIndexCreator{
	public static final String VALUE="value";
	@Override
	public String[] getProperties() {
		return new String[]{VALUE};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Date();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(VALUE.equals(attribute)){
			return ((Date)entity).getTime();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value) {
		if(VALUE.equals(attribute)){
			((Date)entity).setTime((long) value);;
			return true;
		}
		return false;
	}

}
