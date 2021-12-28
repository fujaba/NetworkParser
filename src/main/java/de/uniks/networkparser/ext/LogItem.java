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

/** LogItem */
public class LogItem extends SendableItem implements SendableEntityCreator {
	public static final String INCOMING = "Empfange";
	public static final String OUTGOING = "Sende";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_TIMESTAMP = "timestamp";
	public static final String PROPERTY_CURRENTTIME = "currenttime";
	public static final String PROPERTY_THREADNAME = "threadName";
	public static final String PROPERTY_MESSAGE = "message";
	private final String[] properties = new String[] { LogItem.PROPERTY_CURRENTTIME, LogItem.PROPERTY_TIMESTAMP,
			LogItem.PROPERTY_THREADNAME, LogItem.PROPERTY_TYPE, LogItem.PROPERTY_MESSAGE, };

	private static final long STARTTIME = System.currentTimeMillis();

	private String type;
	private long timestamp;
	private long currentTime;
	private String message;
	private String threadName;

	public LogItem(String message, String type) {
		long currentTimeMillis = System.currentTimeMillis();
		this.timestamp = currentTimeMillis - STARTTIME;
		this.currentTime = currentTimeMillis;
		this.threadName = Thread.currentThread().getName();
		this.type = type;
		this.message = message;
	}

	public LogItem(String message) {
		this(message, NetworkParserLog.INFO);
	}

	public LogItem() {
		this("", NetworkParserLog.INFO);
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public String getThreadName() {
		return this.threadName;
	}

	public boolean setThreadName(String value) {
		if(this.threadName != value || (this.threadName != null && this.threadName.equals(value) == false)) {
			String oldValue = this.threadName;
			this.threadName = value;
			firePropertyChange(PROPERTY_THREADNAME, oldValue, value);
			return true;
		}
		return false;
	}

	public LogItem withThreadName(String value) {
		setThreadName(value);
		return this;
	}

	public String getType() {
		return this.type;
	}

	public boolean setType(String value) {
		if(this.type != value || (this.type != null && this.type.equals(value) == false)) {
			String oldValue = this.type;
			this.type = value;
			firePropertyChange(PROPERTY_TYPE, oldValue, value);
			return true;
		}
		return false;
	}

	public LogItem withType(String value) {
		setType(value);
		return this;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean setMessage(String value) {
		if(this.message != value || (this.message != null && this.message.equals(value) == false)) {
			String oldValue = this.message;
			this.message = value;
			firePropertyChange(PROPERTY_MESSAGE, oldValue, value);
			
		}
		return false;
	}

	public LogItem withMessage(String value) {
		setMessage(value);
		return this;
	}

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute == null || entity instanceof LogItem == false) {
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

	public DateTimeEntity getCurrentDate() {
		return new DateTimeEntity().withNewDate(getCurrentTime());
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute == null || entity instanceof LogItem == false) {
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

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new LogItem();
	}
}
