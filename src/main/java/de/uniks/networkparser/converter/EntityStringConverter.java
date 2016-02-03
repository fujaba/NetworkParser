package de.uniks.networkparser.converter;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.json.JsonArray;

public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int intent;
	
	public EntityStringConverter() {
	}

	public EntityStringConverter(int intentFactor, int intent) {
		this.indentFactor = intentFactor;
		this.intent = intent;
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof Entity) {
			return ((Entity) entity).toString(indentFactor, intent);
		}else if(entity instanceof JsonArray) {
			return ((JsonArray) entity).toString(indentFactor, intent);
		}
		if(entity == null) {
			return null;
		}
		return entity.toString(); 
	}
}
