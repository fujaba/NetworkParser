package de.uniks.networkparser.test.model;

public class JabberBindMessage {
	public static final String PROPERTY_ID="id";
	public static final String PROPERTY_TYPE="type";
	public static final String PROPERTY_BINDXMLNS="&bind?xmlns";
	public static final String PROPERTY_RESOURCE="&bind&resource";
	public static final String PROPERTY_JID="&bind&jid";
	
	private String resource;
	private String id;
	private String type="set";
	private String jid;
	private final String xmlns="urn:ietf:params:xml:ns:xmpp-bind";
	
	public JabberBindMessage(){
		
	}
	public JabberBindMessage(String resource, String id, String jid){
		if(jid!=null&&jid.length()>0){
			this.jid=jid;
		}
		this.resource=resource;
		this.id=id;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String value) {
		this.type=value;
	}
	public String getXmlns() {
		return xmlns;
	}
	
	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			return getId();
		} else if (attribute.equalsIgnoreCase(PROPERTY_TYPE)) {
			return getType();
		} else if (attribute.equalsIgnoreCase(PROPERTY_RESOURCE)) {
			return getResource();
		} else if (attribute.equalsIgnoreCase(PROPERTY_JID)) {
			return getJid();
		} else if (attribute.equalsIgnoreCase(PROPERTY_BINDXMLNS)) {
			return getXmlns();
		}
			
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			setId((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_TYPE)) {
			setType((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_RESOURCE)) {
			setResource((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_JID)) {
			setJid((String) value);
			return true;
		}
		return false;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}

}
