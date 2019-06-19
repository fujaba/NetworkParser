package de.uniks.networkparser.ext.petaf;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;

public class ModelChange implements Comparable<ModelChange> {
	/* History-Id */
	public static final String PROPERTY_KEY = "key";

	/* Receiver */
	public static final String PROPERTY_RECEIVER = "receiver";

	/* Json-Change */
	public static final String PROPERTY_CHANGE = "change";

	private String key;
	private BaseItem receiver;
	private BaseItem change;

	@Override
	public String toString() {
		return "" + key + " " + (receiver == null ? "" : receiver.toString());
	}

	public String getFullKey() {
		String format = String.format("%%0%dd", 20);
		return String.format(format, key) + "!" + receiver;
	}

	@Override
	public int compareTo(ModelChange o) {
		if (this.getKey() == null) {
			return -1;
		}
		int result = this.getKey().compareTo(o.getKey());
		if (result == 0) {
			if (this.getReceiver() == null) {
				return -1;
			}
			result = this.getReceiver().toString().compareTo(o.getReceiver().toString());
		}
		return result;
	}

	public String getKey() {
		return key;
	}

	public int getKeyNumber() {
		int result = -1;
		try {
			result = Integer.valueOf(key);
		} catch (Exception e) {
		}
		return result;
	}

	public ModelChange withKey(String key) {
		this.key = key;
		return this;
	}

	public BaseItem getChange() {
		return change;
	}

	public ModelChange withChange(BaseItem value) {
		this.change = value;
		return this;
	}

	public BaseItem getReceiver() {
		return receiver;
	}

	public ModelChange withReceiver(BaseItem value) {
		this.receiver = value;
		return this;
	}

	public Object get(String attrName) {
		if (PROPERTY_KEY.equals(attrName)) {
			return getKey();
		}
		if (PROPERTY_RECEIVER.equals(attrName)) {
			return getReceiver();
		}
		if (PROPERTY_CHANGE.equals(attrName)) {
			return getChange();
		}
		return null;
	}

	public boolean set(String attrName, Object value) {
		if (PROPERTY_KEY.equals(attrName)) {
			withKey((String) value);
			return true;
		}
		if (PROPERTY_RECEIVER.equals(attrName)) {
			withReceiver((JsonObject) value);
			return true;
		}
		if (PROPERTY_CHANGE.equals(attrName)) {
			withChange((JsonObject) value);
			return true;
		}
		return false;
	}
}
