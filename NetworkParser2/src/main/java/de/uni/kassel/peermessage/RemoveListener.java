package de.uni.kassel.peermessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class RemoveListener implements PropertyChangeListener{
	private IdMap map;

	public RemoveListener(IdMap map){
		this.map=map;
	}
	public boolean exeucte(Object entity){
		return map.remove(entity);
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
//FIXME		if(evt.getNewValue()==null&&evt.getPropertyName().equals(PROPERTY)){
//			String key = map.getKey(evt.getOldValue());
//			if(key!=null){
//				exeucte(evt.getOldValue());
//			}
//		}
	}
}
