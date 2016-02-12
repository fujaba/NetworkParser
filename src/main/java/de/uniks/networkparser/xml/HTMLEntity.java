package de.uniks.networkparser.xml;

import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleSet;

public class HTMLEntity implements BaseItem {
	public static final String PROPERTY_HEADER="head";
	public static final String PROPERTY_BODY="body";

	private XMLEntity body = new XMLEntity().withTag("body");
	private XMLEntity header = new XMLEntity().withTag("head");

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	public HTMLEntity withEncoding(String encoding) {
		XMLEntity metaTag = new XMLEntity().withTag("meta");
		metaTag.withKeyValue("http-equiv", "Content-Type");
		metaTag.withKeyValue("content", "text/html;charset="+encoding);
		this.header.with(metaTag);
		return this;
	}

	public HTMLEntity withTitle(String value) {
		XMLEntity titleTag = new XMLEntity().withTag("title").withValue(value);
		this.header.with(titleTag);
		return this;
	}

	protected String parseItem(EntityStringConverter converter) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		converter.add();
		sb.append(header.toString(converter));
		sb.append(body.toString(converter));
		converter.minus();
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	public HTMLEntity with(Object... values) {
		if(values == null) {
			return this;
		}
		if(values.length % 2 == 0) {
			this.body.with(values);
		} else {
			for(Object item : values) {
				if(item instanceof XMLEntity) {
					this.body.withChild((XMLEntity) item);
				}
			}
		}
		return this;
	}

	public Object getValue(Object key) {
		Object result=this.header.getValue(key);
		if(result!=null) {
			return result;
		}
		return this.body.getValue(key);
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
			this.header.with(linkTag);
		}
		if(ref.substring(ref.length() - 3).equalsIgnoreCase(".js") ) {
			XMLEntity scriptTag = new XMLEntity().withTag("script").withCloseTag();
			scriptTag.withKeyValue("src", ref);
			this.header.with(scriptTag);
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleSet<XMLEntity>();
	}

	public HTMLEntity withGraph(GraphList value) {
		return withGraph(value, null);
	}

	public HTMLEntity addStyle(String name, String style) {
		XMLEntity styleElement = null;
		for(EntityList child : header.getChildren()) {
			if(child  instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity xml = (XMLEntity) child;
			if(xml.getTag().equals(name)) {
				styleElement = xml;
			}
		}
		if( styleElement == null) {
			XMLEntity element = new XMLEntity().withTag("style"); 
			header.with(element);
			styleElement = element; 
		}
		styleElement.withValueItem(styleElement.getValue()+"\r\n" + style);
		return this;
	}

	public HTMLEntity withGraph(GraphList value, String path) {
		XMLEntity script = new XMLEntity().withTag("script").withKeyValue("type", "text/javascript");
		StringBuilder sb=new StringBuilder();
		sb.append("var json=");
		sb.append( value.toString(new GraphConverter()) );
		sb.append(";"+CRLF);
		sb.append("new Graph(json).layout();");
		script.withValueItem(sb.toString());
		with(script);
		if(path != null) {
			// Add graph-framework
			withHeader(path + "diagramstyle.css");
			withHeader(path + "graph.js");
			withHeader(path + "dagre.min.js");
			withHeader(path + "drawer.js");
		}
		return this;
	}

	public HTMLEntity withNewLine() {
		this.body.withChild(new XMLEntity().withValueItem("<br />\r\n"));
		return this;
	}
	public HTMLEntity withText(String text) {
		this.body.withChild(new XMLEntity().withValueItem(text));
		return this;
	}
	
	@Override
	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		if(converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter)converter);
		}
		return converter.encode(this);
	}
	@Override
	public int size() {
		return body.size();
	}
}
