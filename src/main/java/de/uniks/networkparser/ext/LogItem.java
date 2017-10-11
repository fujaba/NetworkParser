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

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.petaf.SendableItem;

public class LogItem extends SendableItem {
	public final static String INCOMING = "Empfange";
	public final static String OUTGOING = "Sende";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_TIMESTAMP = "timestamp";
	public static final String PROPERTY_DATE = "date";
	public static final String PROPERTY_THREADNAME = "threadName";
	public static final String PROPERTY_MESSAGE = "message";
	public static final String PROPERTY_CATEGORIE = "categorie";

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

	// ==========================================================================

	public String getThreadName() {
		return this.threadName;
	}

	public boolean setThreadName(String value) {
		if (!(this.threadName == null) ? value == null : this.threadName.equals(value)) {
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

	// ==========================================================================

	public String getType() {
		return this.type;
	}

	public boolean setType(String value) {
		if (!(this.type == null) ? value == null : this.type.equals(value)) {
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

	// ==========================================================================
	public String getMessage() {
		return this.message;
	}

	public boolean setMessage(String value) {
		if (!(this.message == null) ? value == null : this.message.equals(value)) {
			String oldValue = this.message;
			this.message = value;
			firePropertyChange(PROPERTY_MESSAGE, oldValue, value);
			return true;
		}
		return false;
	}

	public LogItem withMessage(String value) {
		setMessage(value);
		return this;
	}
}
