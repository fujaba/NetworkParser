package de.uniks.networkparser.ext.io;

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
import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.StringEntity;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/** SocketMessage 
 * @author Stefan Lindel */
public class SocketMessage implements BaseItem {
	public static final String PROPERTY_FROM = "From: ";
	public static final String PROPERTY_TO = "To";
	public static final String PROPERTY_DATE = "Date: ";
	public static final String PROPERTY_ID = "Message-Id: ";
	public static final String PROPERTY_MIME = "MIME-Version: ";
	public static final String PROPERTY_SUBJECT = "Subject: ";
	public static final String PROPERTY_BOUNDARY = "boundary=";
	public static final String PROPERTY_CONTENTTYPE = "Content-Type: ";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/mixed;";
	public static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8;";
	public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=utf-8;";
	public static final String CONTENT_ENCODING = "Content-Transfer-Encoding: 7bit";

	private String subject;
	private SimpleList<BaseItem> message = new SimpleList<BaseItem>();
	private String id;
	private String mimeVersion = "1.0";
	private DateTimeEntity date;
	private String from;
	private SimpleList<String> to = new SimpleList<String>();
	private SimpleKeyValueList<String, Buffer> attachment = new SimpleKeyValueList<String, Buffer>();
	private String boundary;

	public SocketMessage(String... toAdresses) {
		this.withRecipient(toAdresses);
	}

	public String getContentType() {
		if (isMultiPart()) {
			return CONTENT_TYPE_MULTIPART;
		}
		BaseItem item = null;
		if (this.message.size() > 0) {
			item = this.message.get(0);
		}
		return getContentType(item);
	}

	public String getContentType(BaseItem element) {
		if (element instanceof HTMLEntity) {
			return CONTENT_TYPE_HTML;
		}
		return CONTENT_TYPE_PLAIN;
	}

	public String getHeader(String key) {
		if (PROPERTY_FROM.equalsIgnoreCase(key)) {
			return PROPERTY_FROM + from;
		}
		if (PROPERTY_TO.equalsIgnoreCase(key)) {
			CharacterBuffer values = new CharacterBuffer();
			values.with(PROPERTY_TO).with(": ");
			for (int i = 0; i < to.size(); i++) {
				if (i > 0) {
					values.with(";");
				}
				values.with(to.get(i));
			}
			return values.toString();
		}
		if (PROPERTY_MIME.equalsIgnoreCase(key)) {
			return PROPERTY_MIME + mimeVersion;
		}

		if (PROPERTY_DATE.equalsIgnoreCase(key)) {
			if (this.date == null) {
				this.date = new DateTimeEntity();
			}
			return PROPERTY_DATE + this.date.toString("ddd, d mmm yyyy HH:MM:SS Z (z)");
		}
		if (PROPERTY_ID.equalsIgnoreCase(key)) {
			return PROPERTY_ID + id;
		}
		if (PROPERTY_SUBJECT.equalsIgnoreCase(key)) {
			return PROPERTY_SUBJECT + this.subject;
		}
		if (PROPERTY_CONTENTTYPE.equalsIgnoreCase(key)) {
			return PROPERTY_CONTENTTYPE + this.getContentType();
		}
		if (PROPERTY_BOUNDARY.equalsIgnoreCase(key)) {
			return PROPERTY_BOUNDARY + "\"" + generateBoundaryValue() + "\"";
		}

		return null;
	}

	public String getHeaderFrom(String defaultFrom) {
		if (from == null) {
			this.from = defaultFrom;
		}
		return "MAIL FROM:" + normalizeAddress(from);
	}

	public SimpleList<String> getHeaderTo() {
		SimpleList<String> toList = new SimpleList<String>();
		for (int i = 0; i < to.size(); i++) {
			toList.add("RCPT TO:" + normalizeAddress(to.get(i)));
		}
		return toList;
	}

	public String generateMessageId(String localHost) {
		if (this.id != null || localHost == null) {
			return this.id;
		}
		int at = localHost.lastIndexOf('@');
		if (at >= 0)
			localHost = localHost.substring(at);

		CharacterBuffer s = new CharacterBuffer();

		/* Unique string is <hashcode>.<id>.<currentTime><suffix> */
		String id = MessageSession.nextID();
		s.with(s.hashCode()).with('.').with(id).with('.').with(System.currentTimeMillis()).with(localHost);
		this.id = s.toString();
		return this.id;
	}

	public XMLEntity toXML(String type) {
		XMLEntity messageXML = XMLEntity.TAG("message");
		if (type == MessageSession.TYPE_FCM) {
			messageXML.add("id", "");
			XMLEntity gcm = messageXML.createChild("gcm");
			gcm.add("xmlns", "google:mobile:data");

			JsonObject container = new JsonObject();
			gcm.add(container);
			if (this.to.size() > 0) {
				container.put("to", this.to.first());
			}
			container.put("message_id", MessageSession.nextID());

			JsonObject data = new JsonObject();
			container.put("data", data);
			if (this.message.size() > 0) {
				data.put("message", this.message.first());
			}

			/*
			 * "time_to_live":"600", "delay_while_idle": true/false,
			 * "delivery_receipt_requested": true/false
			 */
		}
		if (type == MessageSession.TYPE_XMPP) {
			messageXML.add("id", MessageSession.nextID());
			messageXML.add("to", to);
			messageXML.createChild("body").withValueItem(message.toString());
		}
		return messageXML;
	}

	/**
	 * Get a unique value for use in a multipart boundary string.
	 *
	 * This implementation generates it by concatenating a global part number, a
	 * newly created object's <code>hashCode()</code>, and the current time (in
	 * milliseconds).
	 * 
	 * @return Boundary String
	 */
	public String generateBoundaryValue() {
		if (this.boundary != null) {
			return this.boundary;
		}
		CharacterBuffer s = new CharacterBuffer();
		long hash = s.hashCode();

		/* Unique string is ----=_Part_<part>_<hashcode>.<currentTime> */
		String id = MessageSession.nextID();

		s.with("_Part_").with(id).with('_').with(hash).with('.').with(System.currentTimeMillis());
		this.boundary = s.toString();
		return this.boundary;
	}

	public SocketMessage withSubject(String value) {
		this.subject = value;
		return this;
	}

	public SocketMessage withRecipient(String... toAdresses) {
		if (toAdresses == null) {
			return this;
		}
		for (int i = 0; i < toAdresses.length; i++) {
			for(String item : toAdresses[i].split(",")) {
				this.to.add(item);
			}
		}
		return this;
	}

	private String normalizeAddress(String value) {
		if (value == null) {
			return null;
		}
		String returnValue = value.trim();
		if (returnValue.startsWith("<") == false) {
			if (returnValue.endsWith(">")) {
				return "<" + returnValue;
			}
			return "<" + returnValue + ">";
		}
		if (returnValue.endsWith(">")) {
			return returnValue;
		}
		return returnValue + ">";
	}

	public String getSubject() {
		return this.subject;
	}

	public SocketMessage withMessage(HTMLEntity value) {
		this.message.add(value);
		return this;
	}

	public SocketMessage withMessage(String value) {
		BaseItem item = new StringEntity();
		item.add(value);
		this.message.add(item);
		return this;
	}

	public SocketMessage withHTMLMessage(String value) {
		BaseItem item = new HTMLEntity().withBody(value);
		this.message.add(item);
		return this;
	}

	public SimpleList<BaseItem> getMessages() {
		return this.message;
	}

	public SimpleKeyValueList<String, Buffer> getAttachments() {
		return this.attachment;
	}

	public boolean isMultiPart() {
		return this.message.size() > 1 || this.attachment.size() > 0;
	}

	public void removeToAdress(int pos) {
		this.to.remove(pos);
	}

	public SocketMessage withAttachment(String fileName, Buffer buffer) {
		this.attachment.add(fileName, buffer);
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SocketMessage();
	}

	@Override
	public int size() {
		return this.to.size();
	}

	@Override
	public String toString(Converter converter) {
		if (converter instanceof EntityStringConverter) {
			return toString();
		}
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	@Override
	public boolean add(Object... values) {
		if (values != null) {
			for (Object item : values) {
				if (item instanceof String && item != null) {
					this.withRecipient((String) item);
				}
			}
		}
		return true;
	}

	public static SocketMessage create(String message, String... toAdresses) {
		SocketMessage socketMessage = new SocketMessage(toAdresses);
		socketMessage.withMessage(message);
		return socketMessage;
	}

}
