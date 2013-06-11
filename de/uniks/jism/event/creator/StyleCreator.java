package de.uniks.jism.event.creator;

import de.uniks.jism.Style;
import de.uniks.jism.interfaces.SendableEntityCreator;

public class StyleCreator implements SendableEntityCreator{
	private static final String[] props=new String[]{Style.PROPERTY_BOLD, Style.PROPERTY_ITALIC, Style.PROPERTY_FONTFAMILY, 
													Style.PROPERTY_FONTSIZE, Style.PROPERTY_FORGROUND, Style.PROPERTY_BACKGROUND,
													Style.PROPERTY_UNDERLINE, Style.PROPERTY_ALIGNMENT};

	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Style();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Style)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((Style)entity).set(attribute, value);
	}

}
