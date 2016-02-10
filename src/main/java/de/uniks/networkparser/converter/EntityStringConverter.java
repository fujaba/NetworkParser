package de.uniks.networkparser.converter;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int indent;
	
	public EntityStringConverter() {
	}

	public EntityStringConverter(int indentFactor, int indent) {
		this.indentFactor = indentFactor;
		this.indent = indent;
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof Entity) {
			return ((Entity) entity).toString(getIndentFactor());
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

	public int getIndent() {
		return indent;
	}
}
