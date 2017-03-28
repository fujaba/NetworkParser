package de.uniks.networkparser.xml;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleSet;

public class HTMLEntity implements BaseItem {
	public static final String PROPERTY_HEADER="head";
	public static final String PROPERTY_BODY="body";
	public static final String IMAGEFORMAT=" .bmp .jpg .jpeg .png .gif .svg ";
	public static final String ENCODING_UTF8="utf-8";

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
		if(values.length == 1 && values[0] instanceof String) {
			XMLTokener tokener = new XMLTokener();
			String content = (String) values[0];
			tokener.withBuffer( content );
			XMLEntity item=new XMLEntity();
			tokener.parseToEntity((Entity)item);
			Entity header = item.getElementBy(XMLEntity.PROPERTY_TAG, "header");
			if(header != null && header instanceof XMLEntity) {
				this.header = (XMLEntity) header;
			}
			Entity body = item.getElementBy(XMLEntity.PROPERTY_TAG, "body");
			if(body == null && header == null) {
				this.body = item;
			}else if(body != null && body instanceof XMLEntity) {
				this.body = (XMLEntity) body;
			}
		}else if(values.length % 2 == 0) {
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
		if(pos>0) {
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
		}
		if(child == null) {
			// May be blanko Body text
			child = new XMLEntity().withValueItem(ref);
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
	
	public XMLEntity createBodyTag(String tag, XMLEntity parentNode) {
		String[] tags = tag.split("\\.");
		XMLEntity parent = null, child = null, firstChild = null;
		for(int i=tags.length-1;i>=0;i--) {
			child = parent;
			parent = new XMLEntity().setType(tags[i]);
			if(child != null) {
				parent.withChild(child);
			} else {
				firstChild = parent;
			}
		}
		parentNode.withChild(parent);
		return firstChild;
	}
	public XMLEntity createBodyTag(String tag) {
		return createBodyTag(tag, this.body);
		
	}


	public HTMLEntity withScript(String code) {
		XMLEntity child = new XMLEntity().setType("script").withCloseTag();
		child.with(code);
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
		for(int i=0;i<header.size();i++) {
			EntityList child = header.getChild(i);
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
		styleElement.withValue(styleElement.getValue()+"\r\n" + style);
		return this;
	}

	public HTMLEntity withGraph(GraphList value, String path) {
		XMLEntity script = new XMLEntity().setType("script").withKeyValue("type", "text/javascript");
		StringBuilder sb=new StringBuilder();
		sb.append("var json=");
		sb.append( value.toString(new GraphConverter()) );
		sb.append(";"+CRLF);
		sb.append("new Graph(json).layout();");
		script.withValue(sb.toString());
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
		xmlEntity.withValue("<br />\r\n");
		this.body.withChild(xmlEntity);
		return this;
	}

	public HTMLEntity withText(String text) {
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.withValue(text);
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
	
	@Override
	public int size() {
		return body.size();
	}

	public XMLEntity getElementsBy(String key, String value) {
		XMLEntity item = new XMLEntity();
		EntityList headerList = this.header.getElementsBy(key, value);
		EntityList bodyList = this.body.getElementsBy(key, value);
		int z=0;
		for(z=0;z<headerList.sizeChildren();z++) {
			item.withChild(headerList.getChild(z));
		}
		for(z=0;z<bodyList.sizeChildren();z++) {
			item.withChild(bodyList.getChild(z));
		}
		return item;
	}

	public BaseItem getElementBy(String key, String value) {
		Entity item = this.header.getElementBy(key, value);
		if(item != null) {
			return item;
		}
		item = this.body.getElementBy(key, value);
		return item;
	}
}
