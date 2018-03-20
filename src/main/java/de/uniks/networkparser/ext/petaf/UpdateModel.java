package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class UpdateModel implements Runnable {
	private Object newValue;
	protected Object oldValue;
	private String property;
	private Object entity;
	private ModelThread owner;
	private String id;

	public UpdateModel(ModelThread owner, Object element, String property, Object oldValue, Object newValue) {
		this.owner = owner;
		this.entity = element;
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public UpdateModel withId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public void run() {
		try{
			SendableEntityCreator creator;
			IdMap map = this.owner.getMap();
			Object element;
			if(this.entity instanceof String) {
				String className = (String) this.entity;
				creator = map.getCreator(className, true);
				// TEST FOR NEW ONE
				element = creator.getSendableInstance(true);
				map.put(this.id, element, false);
			} else {
				creator = map.getCreatorClass(entity);
				element = this.entity;
			}
			creator.setValue(element, property, newValue, SendableEntityCreator.NEW);
		}catch(Exception e){
			this.owner.getErrorHandler().saveException(e, false);
		}
	}

}
