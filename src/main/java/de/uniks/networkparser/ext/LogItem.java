/*
   Copyright (c) 2012 Florian

   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.

   The Software shall be used for Good, not Evil.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.ext;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * LogItem Sinle Item of Log.
 *
 * @author Stefan Lindel
 */
public class LogItem extends SendableItem implements SendableEntityCreator {
	
	/** The Constant INCOMING. */
	public static final String INCOMING = "Empfange";
	
	/** The Constant OUTGOING. */
	public static final String OUTGOING = "Sende";
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";
	
	/** The Constant PROPERTY_TIMESTAMP. */
	public static final String PROPERTY_TIMESTAMP = "timestamp";
	
	/** The Constant PROPERTY_CURRENTTIME. */
	public static final String PROPERTY_CURRENTTIME = "currenttime";
	
	/** The Constant PROPERTY_THREADNAME. */
	public static final String PROPERTY_THREADNAME = "threadName";
	
	/** The Constant PROPERTY_MESSAGE. */
	public static final String PROPERTY_MESSAGE = "message";
	private final String[] properties = new String[] { LogItem.PROPERTY_CURRENTTIME, LogItem.PROPERTY_TIMESTAMP,
			LogItem.PROPERTY_THREADNAME, LogItem.PROPERTY_TYPE, LogItem.PROPERTY_MESSAGE, };

	private static final long STARTTIME = System.currentTimeMillis();

	private String type;
	private long timestamp;
	private long currentTime;
	private String message;
	private String threadName;

	/**
	 * Instantiates a new log item.
	 *
	 * @param message the message
	 * @param type the type
	 */
	public LogItem(String message, String type) {
		long currentTimeMillis = System.currentTimeMillis();
		this.timestamp = currentTimeMillis - STARTTIME;
		this.currentTime = currentTimeMillis;
		this.threadName = Thread.currentThread().getName();
		this.type = type;
		this.message = message;
	}

	/**
	 * Instantiates a new log item.
	 *
	 * @param message the message
	 */
	public LogItem(String message) {
		this(message, NetworkParserLog.INFO);
	}

	/**
	 * Instantiates a new log item.
	 */
	public LogItem() {
		this("", NetworkParserLog.INFO);
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Gets the current time.
	 *
	 * @return the current time
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * Gets the thread name.
	 *
	 * @return the thread name
	 */
	public String getThreadName() {
		return this.threadName;
	}

	/**
	 * Sets the thread name.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setThreadName(String value) {
		if(this.threadName != value || (this.threadName != null && !this.threadName.equals(value))) {
			String oldValue = this.threadName;
			this.threadName = value;
			firePropertyChange(PROPERTY_THREADNAME, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With thread name.
	 *
	 * @param value the value
	 * @return the log item
	 */
	public LogItem withThreadName(String value) {
		setThreadName(value);
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setType(String value) {
		if(this.type != value || (this.type != null && !this.type.equals(value))) {
			String oldValue = this.type;
			this.type = value;
			firePropertyChange(PROPERTY_TYPE, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the log item
	 */
	public LogItem withType(String value) {
		setType(value);
		return this;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the message.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setMessage(String value) {
		if(this.message != value || (this.message != null && !this.message.equals(value))) {
			String oldValue = this.message;
			this.message = value;
			firePropertyChange(PROPERTY_MESSAGE, oldValue, value);
			
		}
		return false;
	}

	/**
	 * With message.
	 *
	 * @param value the value
	 * @return the log item
	 */
	public LogItem withMessage(String value) {
		setMessage(value);
		return this;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
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
		if (attribute == null || !(entity instanceof LogItem)) {
			return null;
		}
		int pos = attribute.indexOf('.');
		String attrName = attribute;
		LogItem item = (LogItem) entity;
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}
		if (PROPERTY_TIMESTAMP.equalsIgnoreCase(attribute)) {
			return item.getTimestamp();
		}

		if (PROPERTY_THREADNAME.equalsIgnoreCase(attribute)) {
			return item.getThreadName();
		}

		if (PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			return item.getType();
		}
		if (PROPERTY_MESSAGE.equalsIgnoreCase(attribute)) {
			return item.getMessage();
		}
		if (PROPERTY_CURRENTTIME.equalsIgnoreCase(attribute)) {
			return item.getCurrentDate();
		}
		return null;
	}

	/**
	 * Gets the current date.
	 *
	 * @return the current date
	 */
	public DateTimeEntity getCurrentDate() {
		return new DateTimeEntity().withNewDate(getCurrentTime());
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
		if (attribute == null || !(entity instanceof LogItem)) {
			return false;
		}
		LogItem item = (LogItem) entity;
		if (PROPERTY_TIMESTAMP.equalsIgnoreCase(attribute)) {
			item.timestamp = Long.parseLong(value.toString());
			return true;
		}

		if (PROPERTY_THREADNAME.equalsIgnoreCase(attribute)) {
			item.withThreadName((String) value);
			return true;
		}

		if (PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			item.setType((String) value);
			return true;
		}

		if (PROPERTY_MESSAGE.equalsIgnoreCase(attribute)) {
			item.setMessage((String) value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new LogItem();
	}
}
