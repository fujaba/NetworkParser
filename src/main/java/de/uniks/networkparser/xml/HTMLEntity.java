package de.uniks.networkparser.xml;
import java.net.URL;

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
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class HTMLEntity implements BaseItem {
	public static final String PROPERTY_HEADER="head";
	public static final String PROPERTY_BODY="body";
	public static final String IMAGEFORMAT=" .bmp .jpg .jpeg .png .gif .svg ";
	public static final String SCRIPT="script";
	public static final String LINK="link";
	public static final String KEY_HREF="href";
	public static final String KEY_SRC="src";

	private XMLEntity body = new XMLEntity().setType("body");
	private XMLEntity header = new XMLEntity().setType("head");

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	public XMLEntity getHeader() {
		return header;
	}
	public XMLEntity getBody() {
		return body;
	}

	public HTMLEntity withEncoding(String encoding) {
		XMLEntity metaTag = new XMLEntity().setType("meta");
		metaTag.withKeyValue("http-equiv", "Content-Type");
		metaTag.withKeyValue("content", "text/html;charset="+encoding);
		this.header.with(metaTag);
		return this;
	}

	public HTMLEntity withPageBreak() {
		XMLEntity pageBreak = new XMLEntity().setType("div").withCloseTag();
		pageBreak.put("style", "page-break-before:always");
		this.body.withChild(pageBreak);
		return this;
	}

	public HTMLEntity withTitle(String value) {
		XMLEntity titleTag = new XMLEntity().setType("title").withValue(value);
		this.header.with(titleTag);
		return this;
	}

	public HTMLEntity withScript(String value, XMLEntity parentNode) {
		createScript(value, parentNode);
		return this;
	}

	public XMLEntity createScript(String value, XMLEntity parentNode) {
		XMLEntity node = new XMLEntity().setType(SCRIPT).withKeyValue("language", "Javascript");
		if(value == null) {
			return node;
		}
		if(value.endsWith(".js") && value.indexOf("\n")<0) {
			// May be a Link
			if(parentNode == null) {
				parentNode = this.header;
			}
			node.withCloseTag();
			node.withKeyValue(KEY_SRC, value);
		} else {
			if(parentNode == null) {
				parentNode = this.body;
			}
			node.withValue(value);
		}
		parentNode.with(node);
		return node;
	}

	public HTMLEntity withHeaderStyle(String value) {
		XMLEntity headerChild = new XMLEntity().setType("style").withValue(value);
		this.header.with(headerChild);
		return this;
	}

	protected String parseItem(EntityStringConverter converter) {
		if(converter == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		converter.add();
		sb.append(header.toString(converter));
		sb.append(body.toString(converter));
		converter.minus();
		sb.append("</html>");
		return sb.toString();
	}

	public HTMLEntity with(Object... values) {
		add(values);
		return this;
	}

	@Override
	public boolean add(Object... values) {
		if(values == null) {
			return false;
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
		return true;
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
				child = new XMLEntity().setType(LINK);
				child.withKeyValue("rel", "stylesheet");
				child.withKeyValue("type", "text/css");
				child.withKeyValue(KEY_HREF, ref);
			} else if(ext.equals(".js") ) {
				child = new XMLEntity().setType(SCRIPT).withCloseTag();
				child.withKeyValue(KEY_SRC, ref);
			} else if(IMAGEFORMAT.indexOf(" "+ext+" ")>=0) {
				child = new XMLEntity().setType("img").withCloseTag();
				child.withKeyValue(KEY_SRC, ref);
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

	/**
	 * Create a new Tag as Child of Parent
	 *
	 * @param tag the new Tag
	 * @param parentNode May be a child of Body or Body or head
	 * @return the created XMLEntity Item
	 */
	public XMLEntity createTag(String tag, XMLEntity parentNode) {
		if(parentNode == null) {
			parentNode = this.body;
		}
		if(tag == null) {
			return null;
		}
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

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleSet<XMLEntity>();
	}

	public HTMLEntity addStyle(String name, String style) {
		XMLEntity styleElement = null;
		for(int i=0;i<header.size();i++) {
			BaseItem child = header.getChild(i);
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

	public HTMLEntity withGraph(GraphModel value) {
		URL resource = GraphList.class.getResource("");
		if(resource == null || value == null) {
			return this;
		}
		String graph = value.toString(new GraphConverter());
		return withGraph(graph, resource.toString());
	}
	public HTMLEntity withGraph(JsonObject value) {
		URL resource = GraphList.class.getResource("");
		if(resource == null || value == null) {
			return this;
		}
		return withGraph(value.toString(2), resource.toString());
	}

	public HTMLEntity withGraph(String graph, String path) {
		XMLEntity script = new XMLEntity().setType(SCRIPT).withKeyValue("type", "text/javascript");
		StringBuilder sb=new StringBuilder();
		sb.append("var json=");
		sb.append( graph );
		sb.append(";"+CRLF);
		sb.append("new Graph(json).layout();");
		script.withValue(sb.toString());
		add(script);
		if(path != null) {
			// Add graph-framework
			// Test for Add Styles
			SimpleList<String> list=new SimpleList<String>().with("diagramstyle.css", "graph.js", "dagre.min.js", "drawer.js");
			for(int i = 0;i<this.header.sizeChildren();i++) {
				XMLEntity item = (XMLEntity) this.header.getChild(i);
				String url = null;
				if(LINK.equals(item.getTag())) {
					url = item.getString(KEY_HREF);
				} else if(SCRIPT.equals(item.getTag())) {
					url = item.getString(KEY_SRC);
				}
				if(url != null) {
					int pos = url.lastIndexOf('/');
					if(pos>=0) {
						url = url.substring(pos+1);
					}
					list.remove(url);
				}
			}
			for(String item : list) {
				withHeader(path + item);
			}
			withEncoding(ENCODING);
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
		int z;
		if(headerList != null) {
			for(z=0;z<headerList.sizeChildren();z++) {
				BaseItem child = headerList.getChild(z);
				if(child instanceof EntityList) {
					item.withChild((EntityList) child);
				}
			}
		}
		if(bodyList != null) {
			for(z=0;z<bodyList.sizeChildren();z++) {
				BaseItem child = bodyList.getChild(z);
				if(child instanceof EntityList) {
					item.withChild((EntityList) child);
				}
			}
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

	public HTMLEntity withValue(String value) {
		XMLEntity htmlPage = new XMLEntity().withValue(value);
		//All Children possible head and body
		for(int i=0;i<htmlPage.sizeChildren();i++) {
			BaseItem item = htmlPage.getChild(i);
			if(item instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity child = (XMLEntity) item;
			if(PROPERTY_HEADER.equalsIgnoreCase(child.getTag())) {
				this.header = child;
			} else if(PROPERTY_BODY.equalsIgnoreCase(child.getTag())) {
				this.body = child;
			}
		}
		return this;
	}
}
