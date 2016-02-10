package de.uniks.networkparser.xml;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import de.uniks.networkparser.EntityUtil;
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
		return this.toString(0, 0);
	}

	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
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

	protected String toString(int indentFactor, int indent) {
		StringBuilder sb = new StringBuilder();
		if (indent > 0) {
			sb.append("\n");
		}
		sb.append(EntityUtil.repeat(' ', indent));
		sb.append("<html>");
		sb.append(header.toString(indentFactor, indent));
		sb.append(body.toString(indentFactor, indent));
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
		if(converter instanceof EntityStringConverter) {
			EntityStringConverter item = (EntityStringConverter)converter;
			return toString(item.getIndentFactor(), item.getIndent());
		}
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}
	@Override
	public int size() {
		return body.size();
	}
}
