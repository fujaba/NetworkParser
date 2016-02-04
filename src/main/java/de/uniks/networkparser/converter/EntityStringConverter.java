package de.uniks.networkparser.converter;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

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
			return ((Entity) entity).toString(getIndentFactor(), intent);
		}else if(entity instanceof BaseItem) {
			return ((BaseItem) entity).toString(this);
		}
		if(entity == null) {
			return null;
		}
		return entity.toString(); 
	}

	public int getIndentFactor() {
		return indentFactor;
	}

	public int getIntent() {
		return intent;
	}
}
