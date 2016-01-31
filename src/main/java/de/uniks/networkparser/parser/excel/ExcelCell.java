package de.uniks.networkparser.parser;

import de.uniks.networkparser.gui.Pos;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class ExcelCell extends XMLEntity implements SendableEntityCreatorTag{
	public static final String TAG="c";
	public static final String PROPERTY_STYLE="s";
	public static final String PROPERTY_TYPE="t";
	public static final String PROPERTY_REFERENZ="r";
	public static final String[] PROPERTIES={PROPERTY_STYLE, PROPERTY_TYPE, PROPERTY_REFERENZ};
	private Pos referenz;

	@Override
	public String[] getProperties() {
		return PROPERTIES;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof ExcelCell == false) {
			return null;
		}
		if(PROPERTY_STYLE.equals(attribute)) {
			((ExcelCell)entity).getStyle();
		}
		if(PROPERTY_TYPE.equals(attribute)) {
			((ExcelCell)entity).getType();
		}
		if(PROPERTY_REFERENZ.equals(attribute)) {
			((ExcelCell)entity).getReferenz();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(XMLEntityCreator.ALL.equals(type)) {
			if(value instanceof XMLEntity == false) {
				return false;
			}
			XMLEntity item = (XMLEntity) value;
			ExcelCell parent = (ExcelCell) entity;
			if(item.getValue(PROPERTY_STYLE) != null) {
				parent.withStyle(""+item.getValue(PROPERTY_STYLE));
			}
			if(item.getValue(PROPERTY_TYPE) != null) {
				parent.withType(""+item.getValue(PROPERTY_TYPE));
			}
			if(item.getValue(PROPERTY_REFERENZ) != null) {
				parent.withReferenz(Pos.valueOf(""+item.getValue(PROPERTY_REFERENZ)));
				super.with(PROPERTY_REFERENZ, getReferenz());
			}
			for(XMLEntity child : item.getChildren()) {
				parent.withChild(child);
			}
			return true;
		}
		if(entity instanceof ExcelCell == false) {
			return false;
		}
		if(PROPERTY_STYLE.equals(attribute)) {
			((ExcelCell)entity).withStyle(""+value);
			return true;
		}
		if(PROPERTY_TYPE.equals(attribute)) {
			((ExcelCell)entity).withType(""+value);
			return true;
		}
		if(PROPERTY_REFERENZ.equals(attribute)) {
			((ExcelCell)entity).withReferenz(Pos.valueOf(""+value));
			return true;
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ExcelCell();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	public Pos getReferenz() {
		return referenz;
	}

	public ExcelCell withReferenz(Pos referenz) {
		this.referenz = referenz;
		return this;
	}

	public String getType() {
		return getString(PROPERTY_TYPE);
	}

	public ExcelCell withType(String type) {
		super.setValueItem(PROPERTY_TYPE, type);
		return this;
	}

	public String getStyle() {
		return getString(PROPERTY_STYLE);
	}

	public ExcelCell withStyle(String style) {
		super.setValueItem(PROPERTY_STYLE, style);
		return this;
	}

}
