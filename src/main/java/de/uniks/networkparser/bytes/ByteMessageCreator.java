package de.uniks.networkparser.bytes;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
/**
 * The Class ByteMessageCreator.
 */

public class ByteMessageCreator implements SendableEntityCreatorTag {
	/** The properties. */
	private final String[] properties = new String[] {ByteMessage.PROPERTY_VALUE };

	/*
	 * return the Properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/*
	 * Create new Instance of ByteMessage
	 */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new ByteMessage();
	}

	/* Get the EventType of BasicMessage (0x42) UTF-8 */
	@Override
	public String getTag() {
		return new String(new byte[]{0x42});
	}

	/* Getter for ByteMessage */
	@Override
	public Object getValue(Object entity, String attribute) {
		return ((ByteMessage) entity).get(attribute);
	}

	/* Setter for ByteMessage */
	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((ByteMessage) entity).set(attribute, value);
	}
}
