package de.uniks.networkparser.ext.email;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.StringEntity;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;


public class EMailMessage {
	public static final String PROPERTY_FROM="From: ";
	public static final String PROPERTY_TO="To";
	public static final String PROPERTY_DATE="Date: ";
	public static final String PROPERTY_ID="Message-Id: ";
	public static final String PROPERTY_MIME="MIME-Version: ";
	public static final String PROPERTY_SUBJECT="Subject: ";
	public static final String PROPERTY_BOUNDARY="boundary=";
	public static final String PROPERTY_CONTENTTYPE="Content-Type: ";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/mixed;";
	public static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8;";
	public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=utf-8;";
	public static final String CONTENT_ENCODING = "Content-Transfer-Encoding: 7bit";
	public static final String CRLF="\r\n";
	private String subject;
	private SimpleList<BaseItem> message = new SimpleList<BaseItem>();
	private String id;
	private String mimeVersion="1.0";
//	private String contentType = "text/html; charset=utf-8";
	private DateTimeEntity date;
	private String from;
	private SimpleList<String> to=new SimpleList<String>();
	private static int counter;
	private SimpleKeyValueList<String, Buffer> attachment = new SimpleKeyValueList<String, Buffer>();
	private String boundary;
	
	public EMailMessage(String... toAdresses) {
		this.withRecipient(toAdresses);
	}
	
	//	private static final String HeaderKeys="Return-Path,"
//			+ "Received,Resent-Date,Resent-From, Resent-Sender,Resent-To,Resent-Cc,Resent-Bcc,Resent-Message-Id,"
//			+ "From,Sender,Reply-To,To,Cc,Bcc," 
//			+ "In-Reply-To,References,Subject,Comments,Keywords,Errors-To,MIME-Version"
//			+ "Content-Type,Content-Transfer-Encoding,Content-MD5,Content-Length,Status"; 
//			
//	private SimpleKeyValueList<String, String> headers=new SimpleKeyValueList<String, String>().withKeyValueString(HeaderKeys, String.class);
	
	public String getContentType() {
		if(isMultiPart()) {
			return CONTENT_TYPE_MULTIPART;
		}
		BaseItem item = null;
		if(this.message.size()>0 ) {
			item = this.message.get(0);
		}
		return getContentType(item);
	}
	
	public String getContentType(BaseItem element) {
		if(element instanceof HTMLEntity) {
			return CONTENT_TYPE_HTML;
		}
		return CONTENT_TYPE_PLAIN;
	}

	public String getHeader(String key) {
		if(PROPERTY_FROM.equalsIgnoreCase(key)) {
			return PROPERTY_FROM + from;		
		}
		if(PROPERTY_TO.equalsIgnoreCase(key)) {
			CharacterBuffer values=new CharacterBuffer();
			values.with(PROPERTY_TO).with(": ");
			for(int i=0;i<to.size();i++) {
				if(i>0) {
					values.with(";");
				}
				values.with(to.get(i));
			}
			return values.toString();
		}
		if(PROPERTY_MIME.equalsIgnoreCase(key)) {
			return PROPERTY_MIME+mimeVersion;
		}
		
		if(PROPERTY_DATE.equalsIgnoreCase(key)) {
			if(this.date == null) {
				this.date = new DateTimeEntity();
			}
			return PROPERTY_DATE+this.date.toString("ddd, d mmm yyyy HH:MM:SS Z (z)");
		}
		if(PROPERTY_ID.equalsIgnoreCase(key)) {
			return PROPERTY_ID+id;
		}
		if(PROPERTY_SUBJECT.equalsIgnoreCase(key)) {
			return PROPERTY_SUBJECT+this.subject;
		}
		if(PROPERTY_CONTENTTYPE.equalsIgnoreCase(key)) {
			return PROPERTY_CONTENTTYPE+this.getContentType();
		}
		if(PROPERTY_BOUNDARY.equalsIgnoreCase(key)) {
			return PROPERTY_BOUNDARY + "\"" + generateBoundaryValue() + "\"";
		}

		return null;
	}
	
	public String getHeaderFrom(String defaultFrom) {
		if(from == null) {
			this.from = defaultFrom;
		}
		return "MAIL FROM:" + normalizeAddress(from);		
	}
	public SimpleList<String> getHeaderTo() {
		SimpleList<String> toList=new SimpleList<String>(); 
		for(int i=0;i<to.size();i++) {
			toList.add("RCPT TO:"+normalizeAddress(to.get(i)));
		}
		return toList;
	}
	
	public String generateMessageId(String localHost) {
		if(this.id != null) {
			return this.id;
		}
		int at = localHost.lastIndexOf('@');
		if (at >= 0)
			localHost = localHost.substring(at);

		CharacterBuffer s = new CharacterBuffer();

		// Unique string is <hashcode>.<id>.<currentTime><suffix>
		s.with(s.hashCode()).with('.').
		with(counter++).with('.').
		with(System.currentTimeMillis()).
		with(localHost);
		this.id = s.toString();
		return this.id;
	}
	
    /**
     * Get a unique value for use in a multipart boundary string.
     *
     * This implementation generates it by concatenating a global
     * part number, a newly created object's <code>hashCode()</code>,
     * and the current time (in milliseconds).
     * @return Boundary String
     */
    public String generateBoundaryValue() {
    	if(this.boundary != null) {
    		return this.boundary;
    	}
    	CharacterBuffer s = new CharacterBuffer();
		long hash = s.hashCode();

		// Unique string is ----=_Part_<part>_<hashcode>.<currentTime>
		s.with("_Part_").with(counter++).with('_').with(hash).with('.').with(System.currentTimeMillis());
		this.boundary = s.toString();
		return this.boundary;
	}
	
	public EMailMessage withSubject(String value) {
		this.subject = value;
		return this;
	}
	
	public EMailMessage withRecipient(String... toAdresses) {
		if(toAdresses == null) {
			return this;
		}
		for(int i=0;i<toAdresses.length;i++) {
			this.to.add(toAdresses[i]);
		}
		return this;
	}
	
	private String normalizeAddress(String value) {
		String returnValue = value.trim();
		if(returnValue.startsWith("<") == false) {
			if(returnValue.endsWith(">")) {
				return "<" + returnValue;
			}
			return "<" + returnValue + ">";
		}
		if(returnValue.endsWith(">")) {
			return returnValue;
		}
		return returnValue + ">";
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	public EMailMessage withMessage(HTMLEntity value) {
		this.message.add(value);
		return this;
	}
	
	public EMailMessage withMessage(String value) {
		BaseItem item = new StringEntity().with(value);
		this.message.add(item);
		return this;
	}
	public EMailMessage withHTMLMessage(String value) {
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
		return this.message.size()>1 || this.attachment.size()>0;
	}

	public void removeToAdress(int pos) {
		this.to.remove(pos);
	}
	
	public EMailMessage withAttachment(String fileName, Buffer buffer) {
		this.attachment.add(fileName, buffer);
		return this;
	}
}
