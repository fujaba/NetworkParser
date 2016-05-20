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
	public static final String IMAGEFORMAT=" .bmp .jpg .jpeg .png .gif .svg ";

	private XMLEntity body = new XMLEntity().setType("body");
	private XMLEntity header = new XMLEntity().setType("head");

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	public HTMLEntity withEncoding(String encoding) {
		XMLEntity metaTag = new XMLEntity().setType("meta");
		metaTag.withKeyValue("http-equiv", "Content-Type");
		metaTag.withKeyValue("content", "text/html;charset="+encoding);
		this.header.with(metaTag);
		return this;
	}

	public HTMLEntity withTitle(String value) {
		XMLEntity titleTag = new XMLEntity().setType("title").withValue(value);
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
		XMLEntity child = getChild(ref);
		if(child != null) {
			this.header.with(child);
		}
		return this;
	}

	XMLEntity getChild(String ref) {
		XMLEntity child = null;

		if(ref==null) {
			return null;
		}
		int pos = ref.lastIndexOf(".");
		if(pos<0) {
			return null;
		}
		String ext = ref.substring(pos).toLowerCase();
		if(ext.equals(".css") ) {
			child = new XMLEntity().setType("link");
			child.withKeyValue("rel", "stylesheet");
			child.withKeyValue("type", "text/css");
			child.withKeyValue("href", ref);
		} else if(ext.equals(".js") ) {
			child = new XMLEntity().setType("script").withCloseTag();
			child.withKeyValue("src", ref);
		} else if(IMAGEFORMAT.indexOf(" "+ext+" ")>=0) {
			child = new XMLEntity().setType("img").withCloseTag();
			child.withKeyValue("src", ref);
		}
		return child;
	}

	public HTMLEntity withBody(String ref) {
		XMLEntity child = getChild(ref);
		if(child != null) {
			this.body.with(child);
		}
		return this;
	}

	public HTMLEntity withScript(String code) {
		XMLEntity child = new XMLEntity().setType("script").withCloseTag();
		child.withValueItem(code);
		this.body.with(child);
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
			if(child instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity xml = (XMLEntity) child;
			if(xml.getTag().equals(name)) {
				styleElement = xml;
			}
		}
		if( styleElement == null) {
			XMLEntity element = new XMLEntity().setType("style");
			header.with(element);
			styleElement = element;
		}
		styleElement.setValueItem(styleElement.getValue()+"\r\n" + style);
		return this;
	}

	public HTMLEntity withGraph(GraphList value, String path) {
		XMLEntity script = new XMLEntity().setType("script").withKeyValue("type", "text/javascript");
		StringBuilder sb=new StringBuilder();
		sb.append("var json=");
		sb.append( value.toString(new GraphConverter()) );
		sb.append(";"+CRLF);
		sb.append("new Graph(json).layout();");
		script.setValueItem(sb.toString());
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
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.setValueItem("<br />\r\n");
		this.body.withChild(xmlEntity);
		return this;
	}

	public HTMLEntity withText(String text) {
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.setValueItem(text);
		this.body.withChild(xmlEntity);
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
}
