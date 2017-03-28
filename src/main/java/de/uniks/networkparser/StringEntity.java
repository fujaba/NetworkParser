package de.uniks.networkparser;

import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

public class StringEntity implements BaseItem{
	private String value;
	@Override
	public String toString() {
		return toString(new EntityStringConverter());
	}
	
	@Override
	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		if(converter instanceof EntityStringConverter) {
			return value;
		}
		return converter.encode(this);
	}
	
	@Override
	public BaseItem with(Object... values) {
		if(values == null) {
			return this;
		}
		if(values.length>0) {
			this.value = (String) values[0];
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new StringEntity();
	}

	@Override
	public int size() {
		if(this.value != null) {
			return 1;
		}
		return 0;
	}
}
