package de.uniks.networkparser.ext.petaf.messages;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.UpdateCondition;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * Change Model Message.
 *
 * @author Stefan Lindel
 */
public class ChangeMessage extends ReceivingTimerTask {
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "change";
	
	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "changeid";
	
	/** The Constant PROPERTY_PROPERTY. */
	public static final String PROPERTY_PROPERTY = "property";
	
	/** The Constant PROPERTY_OLD. */
	public static final String PROPERTY_OLD = "old";
	
	/** The Constant PROPERTY_NEW. */
	public static final String PROPERTY_NEW = "new";
	
	/** The Constant PROPERTY_CHANGECLASS. */
	public static final String PROPERTY_CHANGECLASS = "changeclass";

	private Object entity;
	private Filter filter;
	private String property;
	private String id;
	private Object oldValue;
	private Object newValue;

	/**
	 * Instantiates a new change message.
	 */
	public ChangeMessage() {
		ChangeMessage.props.add(PROPERTY_ID, PROPERTY_PROPERTY, PROPERTY_OLD, PROPERTY_NEW, PROPERTY_CHANGECLASS);
	}

	/**
	 * With entity.
	 *
	 * @param value the value
	 * @return the change message
	 */
	public ChangeMessage withEntity(Object value) {
		this.entity = value;
		return this;
	}

	/**
	 * With filter.
	 *
	 * @param filter the filter
	 * @return the change message
	 */
	public ChangeMessage withFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * With value.
	 *
	 * @param property the property
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return the change message
	 */
	public ChangeMessage withValue(String property, Object oldValue, Object newValue) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
		return this;
	}

	/**
	 * With value.
	 *
	 * @param event the event
	 * @return the change message
	 */
	public ChangeMessage withValue(SimpleEvent event) {
		this.property = event.getPropertyName();
		this.oldValue = event.getOldValue();
		this.newValue = event.getNewValue();
		this.entity = event.getModelValue();
		return this;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public BaseItem getMessage() {
		if (msg == null && space != null) {
			if (property == null) {
				msg = space.encode(entity, filter);
			}
		}
		return super.getMessage();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Run task.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean runTask() {
		if (this.id == null || this.space == null) {
			return false;
		}
		IdMap map = this.space.getMap();
		Object element = map.getObject(this.id);
		SendableEntityCreator creator = null;
		if (element == null) {
			if (this.entity instanceof String) {
				String className = (String) this.entity;
				creator = map.getCreator(className, true);
				element = creator.getSendableInstance(true);
				map.put(this.id, element, false);
				space.createModel(element);
			}
		} else {
			creator = map.getCreatorClass(element);
		}
		if (element != null && creator != null) {
			Object currentValue = creator.getValue(element, this.property);
			if ((currentValue == null && this.oldValue == null)
					|| (currentValue != null && currentValue.equals(this.oldValue))) {
				UpdateCondition changeMessage = UpdateCondition.createUpdateCondition();
				changeMessage.withAcumulateTarget(element, creator, this.property);
				space.suspendNotification(changeMessage);
				creator.setValue(element, property, this.newValue, SendableEntityCreator.UPDATE);
				space.resetNotification();
			}
		}
		return super.runTask();
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute == null || !(entity instanceof ChangeMessage)) {
			return false;
		}
		ChangeMessage message = (ChangeMessage) entity;
		if (PROPERTY_OLD.equalsIgnoreCase(attribute)) {
			return message.oldValue;
		}
		if (PROPERTY_NEW.equalsIgnoreCase(attribute)) {
			return message.newValue;
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			return message.property;
		}
		if (PROPERTY_CHANGECLASS.equalsIgnoreCase(attribute)) {
			if (message.entity == null) {
				return null;
			}
			return message.entity.getClass().getName();
		}
		if (entity != null && PROPERTY_ID.equalsIgnoreCase(attribute)) {
			Space space = message.getSpace();
			if (space != null) {
				return space.getId(message.entity);
			}
		}
		return super.getValue(entity, attribute);
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute == null || !(entity instanceof ChangeMessage)) {
			return false;
		}
		ChangeMessage message = (ChangeMessage) entity;
		if (PROPERTY_OLD.equalsIgnoreCase(attribute)) {
			message.oldValue = value;
			return true;
		}
		if (PROPERTY_NEW.equalsIgnoreCase(attribute)) {
			message.newValue = value;
			return true;
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			message.property = (String) value;
			return true;
		}
		if (PROPERTY_ID.equalsIgnoreCase(attribute)) {
			message.id = (String) value;
			return true;
		}
		if (PROPERTY_CHANGECLASS.equalsIgnoreCase(attribute)) {
			message.entity = (String) value;
			return true;
		}
		return super.setValue(entity, attribute, value, type);
	}

	protected void initialize(NodeProxyModel modell) {
		if (modell == null) {
			return;
		}
		if (this.space == null) {
			this.space = modell.getSpace();
		}
		if (this.entity == null) {
			this.entity = modell.getModel();
		}
	}

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public ChangeMessage getSendableInstance(boolean prototyp) {
		return new ChangeMessage().withFilter(filter);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}

	/**
	 * Gets the new value.
	 *
	 * @return the new value
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
}
