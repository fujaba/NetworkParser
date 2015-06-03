package de.uniks.networkparser.interfaces;

public abstract class SendableEntityCreatorWrapper implements SendableEntityCreator, SendableEntityCreatorNoIndex{

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return false;
	}

	public abstract Object newInstance(BaseItem item);

}
