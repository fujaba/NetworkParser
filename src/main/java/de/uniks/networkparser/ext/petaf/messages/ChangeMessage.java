package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.interfaces.BaseItem;

public class ChangeMessage extends ReceivingTimerTask {
	public static final String PROPERTY_TYPE = "change";
	public static final String PROPERTY_ID = "changeid";
	public static final String PROPERTY_PROPERTY = "property";
	public static final String PROPERTY_OLD = "old";
	public static final String PROPERTY_NEW = "new";
	
	private Object entity;
	private Filter filter;
	private String property;
	private Object oldValue;
	private Object newValue;
	
	
	public ChangeMessage() {
		AcceptMessage.props.add(PROPERTY_ID, PROPERTY_PROPERTY, PROPERTY_OLD, PROPERTY_NEW);
	}

	public ChangeMessage withEntity(Object value) {
		this.entity = value;
		return this;
	}
	public ChangeMessage withFilter(Filter filter) {
		this.filter = filter;
		return this;
	}
	
	public ChangeMessage withValue(String property, Object oldValue, Object newValue) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
		return this;
	}
	
	public ChangeMessage withValue(SimpleEvent event) {
		this.property = event.getPropertyName();
		this.oldValue = event.getOldValue();
		this.newValue = event.getNewValue();
		return this;
	}
	
	@Override
	public BaseItem getMessage() {
		if(msg == null && space != null) {
			msg = space.encode(entity, filter);
		}
		return super.getMessage();
	}
	
	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof ChangeMessage == false ) {
			return false;
		}
		ChangeMessage message = (ChangeMessage) entity;
		if(PROPERTY_OLD.equalsIgnoreCase(attribute)) {
			return oldValue;
		}
		if(PROPERTY_NEW.equalsIgnoreCase(attribute)) {
			return newValue;
		}
		if(PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			return property;
		}
		if(entity != null && PROPERTY_ID.equalsIgnoreCase(attribute)) {
			Space space = message.getSpace();
			if(space != null) {
				return space.getId(entity);
			}
		}
		return super.getValue(entity, attribute);
	}
	
	protected void initialize(NodeProxyModel modell) {
		if(modell == null) {
			return;
		}
		if (this.space == null) {
			this.space = modell.getSpace();
		}
		if (this.entity == null) {
			this.entity = modell.getModell();
		}
	}
	
	@Override
	public ChangeMessage getSendableInstance(boolean prototyp) {
		return new ChangeMessage().withFilter(filter);
	}
	
	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}
}
