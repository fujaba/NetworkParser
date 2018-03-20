package de.uniks.networkparser.ext.petaf;

import java.util.function.Supplier;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class GetModel implements Supplier<Object> {
	private String property;
	private Object entity;
	private ModelThread owner;

	public GetModel(ModelThread owner, Object entity, String property) {
		this.owner = owner;
		this.property = property;
	}

	@Override
	public Object get() {
		try{
			IdMap map = this.owner.getMap();
			if(this.entity instanceof String) {
				Object element = map.getObject((String) this.entity);
				if(this.property != null) {
					SendableEntityCreator creator = map.getCreatorClass(entity);
					return creator.getValue(element, property);
				}
				return element;
			}
			SendableEntityCreator creator = map.getCreatorClass(entity);
			return creator.getValue(entity, property);
		}catch(Exception e){
			this.owner.getErrorHandler().saveException(e, false);
		}
		return false;
	}

}
