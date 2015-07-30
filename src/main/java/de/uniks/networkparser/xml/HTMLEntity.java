package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.list.SimpleSet;

public class HTMLEntity implements StringItem, BaseItem {
	public static final String PROPERTY_HEADER="head";
	public static final String PROPERTY_BODY="body";
	
	private boolean visible = true;
	private XMLEntity body = new XMLEntity().withTag("body");
	private XMLEntity header = new XMLEntity().withTag("head");

	@Override
	public HTMLEntity withVisible(boolean value) {
		this.visible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public String toString() {
		return this.toString(0);
	}

	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		StringBuilder sb = new StringBuilder();
		if (intent > 0) {
			sb.append("\n");
		}
		sb.append(EntityUtil.repeat(' ', intent));
		sb.append("<html>");
		sb.append(header.toString(indentFactor, intent));
		sb.append(body.toString(indentFactor, intent));
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	public HTMLEntity withAll(Object... values) {
		this.body.withAll(values);
		return this;
	}

	@Override
	public Object getValueItem(Object key) {
		Object result=this.header.getValueItem(key);
		if(result!=null) {
			return result;
		}
		return this.body.getValueItem(key);
	}
	
	public HTMLEntity withHeader(String ref) {
		if(ref==null || ref.length() <4) {
			return this;
		}
		if(ref.substring(ref.length() - 4).equalsIgnoreCase(".css") ) {
			XMLEntity linkTag = new XMLEntity().withTag("link");
			linkTag.withKeyValue("rel", "stylesheet");
			linkTag.withKeyValue("type", "text/css");
			linkTag.withKeyValue("href", ref);
			this.header.addChild(linkTag);
		}
		if(ref.substring(ref.length() - 3).equalsIgnoreCase(".js") ) {
			XMLEntity scriptTag = new XMLEntity().withTag("script").withCloseTag();
			scriptTag.withKeyValue("src", ref);
			this.header.addChild(scriptTag);
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleSet<XMLEntity>();
	}
}
