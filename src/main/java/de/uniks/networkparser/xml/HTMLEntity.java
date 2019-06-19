package de.uniks.networkparser.xml;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.buffer.CharacterBuffer;
/*
NetworkParser
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
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class HTMLEntity implements BaseItem {
	public static final String PROPERTY_HEADER = "head";
	public static final String PROPERTY_BODY = "body";
	public static final String IMAGEFORMAT = " .bmp .jpg .jpeg .png .gif .svg ";
	public static final String SCRIPT = "script";
	public static final String LINK = "link";
	public static final String KEY_HREF = "href";
	public static final String KEY_SRC = "src";
	public static final String GRAPH = "Graph";
	public static final String CLASSEDITOR = "ClassEditor";
	public static final String[] GRAPHRESOURCES = new String[] { "diagramstyle.css", "diagram.js", "dagre.min.js",
			"jspdf.min.js" };
	public static final String[] CODEESOURCES = new String[] { "highlight.pack.js", "highlightjs-line-numbers.min.js" };

	private XMLEntity body = new XMLEntity().withType("body");
	private XMLEntity header = new XMLEntity().withType("head");

	private Map<String, List<String>> conenctionHeader = new SimpleKeyValueList<String, List<String>>();
	private int statusCode = 200;
	private String statusMessage;
	private boolean plainBody = true;

	public HTMLEntity withPlain(boolean plain) {
		this.plainBody = plain;
		return this;
	}

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
		XMLEntity metaTag = new XMLEntity().withType("meta");
		metaTag.withKeyValue("http-equiv", "Content-Type");
		metaTag.withKeyValue("content", "text/html;charset=" + encoding);
		this.header.with(metaTag);
		return this;
	}

	public HTMLEntity withPageBreak() {
		XMLEntity pageBreak = new XMLEntity().withType("div").withCloseTag();
		pageBreak.put("style", "page-break-before:always");
		this.body.withChild(pageBreak);
		return this;
	}

	public HTMLEntity withTitle(String value) {
		XMLEntity titleTag = new XMLEntity().withType("title").withValue(value);
		this.header.with(titleTag);
		return this;
	}

	public HTMLEntity withScript(CharSequence... value) {
		return withScript(null, value);
	}

	public HTMLEntity withScript(XMLEntity parentNode, CharSequence... values) {
		if (values != null) {
			CharacterBuffer content = new CharacterBuffer();
			if (values.length > 0) {
				content.with(values[0]);
			}
			for (int i = 1; i < values.length; i++) {
				if (values[i] != null) {
					content.with(BaseItem.CRLF + values[i]);
				}
			}

			createScript(content.toString(), parentNode);
		}
		return this;
	}

	public XMLEntity createScript(String value, XMLEntity parentNode) {
		XMLEntity node = new XMLEntity().withType(SCRIPT).withKeyValue("language", "Javascript");
		if (value == null) {
			return node;
		}
		if (value.endsWith(".js") && value.indexOf("\n") < 0) {
			/* May be a Link */
			if (parentNode == null) {
				parentNode = this.header;
			}
			node.withCloseTag();
			node.withKeyValue(KEY_SRC, value);
		} else {
			if (parentNode == null) {
				parentNode = this.body;
			}
			node.withValue(value);
		}
		parentNode.with(node);
		return node;
	}

	public HTMLEntity withStyle(CharSequence value) {
		if (value != null) {
			String style = value.toString();
			XMLEntity headerChild;
			if (style.endsWith(".css")) {
				headerChild = getChild(style);
			} else {
				headerChild = new XMLEntity().withType("style").withValue(value.toString());
			}
			this.header.with(headerChild);
		}
		return this;
	}

	protected String parseItem(EntityStringConverter converter) {
		if (converter == null) {
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
		if (values == null) {
			return false;
		}
		if (values.length == 1 && values[0] instanceof String) {
			XMLTokener tokener = new XMLTokener();
			CharacterBuffer buffer = new CharacterBuffer().with((String) values[0]);
			XMLEntity item = new XMLEntity();
			tokener.parseToEntity((Entity) item, buffer);
			Entity header = item.getElementBy(XMLEntity.PROPERTY_TAG, "header");
			if (header != null && header instanceof XMLEntity) {
				this.header = (XMLEntity) header;
			}
			Entity body = item.getElementBy(XMLEntity.PROPERTY_TAG, "body");
			if (body == null && header == null) {
				this.body = item;
			} else if (body != null && body instanceof XMLEntity) {
				this.body = (XMLEntity) body;
			}
		} else if (values.length % 2 == 0) {
			this.body.with(values);
		} else {
			for (Object item : values) {
				if (item instanceof XMLEntity) {
					this.body.withChild((XMLEntity) item);
				} else if (item instanceof CharacterBuffer) {
					/* try to Merge */
					CharacterBuffer buffer = (CharacterBuffer) item;
					XMLTokener tokener = new XMLTokener();
					tokener.skipHeader(buffer);
					if (buffer.startsWith("<html", buffer.position(), false)) {
						buffer.skipTo('>', false);
						buffer.skip();
						buffer.nextClean(true);
					}
					if (buffer.startsWith("<head", buffer.position(), false)) {
						int end = buffer.indexOf("</head>");
						int length = buffer.length();
						buffer.withLength(end);
						this.header.withValue(tokener, buffer);
						buffer.withLength(length);
						buffer.skipTo('>', false);
						buffer.skip();
						buffer.nextClean(true);
					}
					/* SO PARSE BODY */
					if (plainBody) {
						if (buffer.startsWith("<body", buffer.position(), false)) {
							buffer.skipTo('>', false);
							buffer.skip();
							buffer.nextClean(true);
						}
						int end = buffer.indexOf("</body>");
						if (end > 0) {
							buffer.withLength(end);
						}
						buffer.withStartPosition(buffer.position());
						this.body.withValueItem(buffer.toString());
					} else {
						this.body.withValue(tokener, buffer);
					}
				}
			}
		}
		return true;
	}

	public Object getValue(Object key) {
		Object result = this.header.getValue(key);
		if (result != null) {
			return result;
		}
		return this.body.getValue(key);
	}

	public HTMLEntity withHeader(String ref) {
		XMLEntity child = getChild(ref);
		if (child != null) {
			this.header.with(child);
		}
		return this;
	}

	public HTMLEntity withBase(String path) {
		XMLEntity child = XMLEntity.TAG("base");
		child.add("href", path);
		this.header.with(child);
		return this;
	}

	XMLEntity getChild(String ref) {
		XMLEntity child = null;

		if (ref == null) {
			return null;
		}
		int pos = ref.lastIndexOf(".");
		if (pos > 0) {
			String ext = ref.substring(pos).toLowerCase();
			if (ext.equals(".css")) {
				child = new XMLEntity().withType(LINK);
				child.withKeyValue("rel", "stylesheet");
				child.withKeyValue("type", "text/css");
				child.withKeyValue(KEY_HREF, ref);
			} else if (ext.equals(".js")) {
				child = new XMLEntity().withType(SCRIPT);
				child.withKeyValue(KEY_SRC, ref).withCloseTag();
			} else if (IMAGEFORMAT.indexOf(" " + ext + " ") >= 0) {
				child = new XMLEntity().withType("img").withCloseTag();
				child.withKeyValue(KEY_SRC, ref);
			}
		}
		if (child == null) {
			/* May be blanko Body text */
			child = new XMLEntity().withValueItem(ref);
		}
		return child;
	}

	public HTMLEntity withBody(String ref) {
		XMLEntity child = getChild(ref);
		if (child != null) {
			this.body.with(child);
		}
		return this;
	}

	/**
	 * Create a new Tag as Child of Parent
	 *
	 * @param tag        the new Tag
	 * @param parentNode Optional May be a child of Body or Body or head
	 * @return the created XMLEntity Item
	 */
	public XMLEntity createTag(String tag, XMLEntity... parentNode) {
		XMLEntity parentElement = null;
		if (parentNode != null && parentNode.length > 0) {
			parentElement = parentNode[0];
		}
		if (parentElement == null) {
			parentElement = this.body;
		}
		if (tag == null) {
			return null;
		}
		String[] tags = tag.split("\\.");
		XMLEntity parent = null, child = null, firstChild = null;
		for (int i = tags.length - 1; i >= 0; i--) {
			child = parent;
			parent = new XMLEntity().withType(tags[i]);
			if (child != null) {
				parent.withChild(child);
			} else {
				firstChild = parent;
			}
		}
		parentElement.withChild(parent);
		return firstChild;
	}

	public XMLEntity createTag(String tag, String innerHTML, XMLEntity... parentNode) {
		XMLEntity element = createTag(tag, parentNode);
		if (element != null) {
			element.withValueItem(innerHTML);
		}
		return element;
	}

	public XMLEntity createTable(XMLEntity parentNode, String... labels) {
		XMLEntity table = createTag("table", parentNode);
		if (labels != null && labels.length > 0) {
			table.with("style", labels[0]);
			XMLEntity tr = createTag("tr", table);
			for (int i = 1; i < labels.length; i += 2) {
				createTag("td", tr);
				table.with("style", labels[i]);
				table.withValue(labels[i + 1]);
			}
		}
		return table;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleSet<XMLEntity>();
	}

	public HTMLEntity addStyle(String name, String style) {
		XMLEntity styleElement = null;
		for (int i = 0; i < header.size(); i++) {
			BaseItem child = header.getChild(i);
			if (child instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity xml = (XMLEntity) child;
			if (xml.getTag().equals(name)) {
				styleElement = xml;
			}
		}
		if (styleElement == null) {
			XMLEntity element = new XMLEntity().withType("style");
			header.with(element);
			styleElement = element;
		}
		styleElement.withValue(styleElement.getValue() + "\r\n" + style);
		return this;
	}

	public HTMLEntity withGraph(GraphModel value) {
		URL resource = GraphList.class.getResource("");
		if (resource == null || value == null) {
			return this;
		}
		return withGraph(value, resource.toString());
	}

	public HTMLEntity addResources(boolean importFiles, String name, String content) {
		if (importFiles) {
			this.withScript(this.getHeader(), content);
		} else {
			this.withHeader(name);
		}
		return this;
	}

	public HTMLEntity withGraph(GraphModel value, String resource) {
		String graphPath = GraphUtil.getGraphPath(value);
		if (GRAPH.equals(graphPath) == false && CLASSEDITOR.equals(graphPath) == false) {
			graphPath = GRAPH;
		}
		if (value != null) {
			String graph = value.toString(new GraphConverter());
			return withGraph(graph, resource, graphPath);
		}
		return this;
	}

	public HTMLEntity withGraph(Entity value) {
		URL resource = GraphList.class.getResource("");
		if (resource == null || value == null) {
			return this;
		}
		return withGraph(value.toString(2), resource.toString(), GRAPH);
	}

	public HTMLEntity withGraph(String graph, String path, String editor) {
		XMLEntity script = new XMLEntity().withType(SCRIPT).withKeyValue("type", "text/javascript");
		StringBuilder sb = new StringBuilder();
		sb.append("var json=");
		sb.append(graph);
		sb.append(";" + CRLF);
		sb.append("window['editor'] = new " + editor + "(json).layout();");
		script.withValue(sb.toString());
		add(script);
		if (path != null) {
			/* Add graph-framework - Test for Add Styles */
			SimpleList<String> resources = new SimpleList<String>();
			for (String item : GRAPHRESOURCES) {
				resources.add(item);
			}
			for (int i = 0; i < this.header.sizeChildren(); i++) {
				XMLEntity item = (XMLEntity) this.header.getChild(i);
				String url = null;
				if (LINK.equals(item.getTag())) {
					url = item.getString(KEY_HREF);
				} else if (SCRIPT.equals(item.getTag())) {
					url = item.getString(KEY_SRC);
				}
				if (url != null) {
					int pos = url.lastIndexOf('/');
					if (pos >= 0) {
						url = url.substring(pos + 1);
					}
					resources.remove(url);
				}
			}
			for (String item : resources) {
				withHeader(path + item);
			}
			withEncoding(ENCODING);
		}
		return this;
	}

	public XMLEntity getHeader(String url) {
		if (url == null) {
			return null;
		}
		for (int i = 0; i < this.header.sizeChildren(); i++) {
			XMLEntity item = (XMLEntity) this.header.getChild(i);
			String childURL = null;
			if (LINK.equals(item.getTag())) {
				childURL = item.getString(KEY_HREF);
			} else if (SCRIPT.equals(item.getTag())) {
				childURL = item.getString(KEY_SRC);
			}
			if (childURL != null) {
				int pos = url.lastIndexOf('/');
				if (pos >= 0) {
					childURL = childURL.substring(pos + 1);
				}
				if (url.equals(childURL)) {
					return item;
				}
			}
		}
		return null;
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
		if (converter == null) {
			return null;
		}
		if (converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
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
		if (headerList != null) {
			for (z = 0; z < headerList.sizeChildren(); z++) {
				BaseItem child = headerList.getChild(z);
				if (child instanceof EntityList) {
					item.withChild((EntityList) child);
				}
			}
		}
		if (bodyList != null) {
			for (z = 0; z < bodyList.sizeChildren(); z++) {
				BaseItem child = bodyList.getChild(z);
				if (child instanceof EntityList) {
					item.withChild((EntityList) child);
				}
			}
		}
		return item;
	}

	public BaseItem getElementBy(String key, String value) {
		Entity item = this.header.getElementBy(key, value);
		if (item != null) {
			return item;
		}
		item = this.body.getElementBy(key, value);
		return item;
	}

	public HTMLEntity withValue(String value) {
		XMLEntity htmlPage = new XMLEntity().withValue(value);
		/* All Children possible head and body */
		for (int i = 0; i < htmlPage.sizeChildren(); i++) {
			BaseItem item = htmlPage.getChild(i);
			if (item instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity child = (XMLEntity) item;
			if (PROPERTY_HEADER.equalsIgnoreCase(child.getTag())) {
				this.header = child;
			} else if (PROPERTY_BODY.equalsIgnoreCase(child.getTag())) {
				this.body = child;
			}
		}
		return this;
	}

	public HTMLEntity withStatus(int code, String message) {
		this.statusCode = code;
		this.statusMessage = message;
		return this;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public HTMLEntity withConnectionHeader(String key, String value) {
		SimpleList<String> list = new SimpleList<String>().with(value);
		this.conenctionHeader.put(key, list);
		return this;
	}

	public HTMLEntity withConnectionHeader(Map<String, List<String>> headerFields) {
		if (headerFields == null) {
			return null;
		}
		for (Iterator<String> i = headerFields.keySet().iterator(); i.hasNext();) {
			String key = i.next();
			this.conenctionHeader.put(key, headerFields.get(key));
		}
		return this;
	}

	public Map<String, List<String>> getConnectionHeader() {
		return conenctionHeader;
	}

	public String getConnectionHeader(String key) {
		List<String> list = conenctionHeader.get(key);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public List<String> getConnectionHeaders(String key) {
		return conenctionHeader.get(key);
	}

	public HTMLEntity withoutHeader(String string) {
		if (header != null && string != null) {
			String ext = null;
			int pos = string.lastIndexOf(".");
			if (pos > 0) {
				ext = string.substring(pos).toLowerCase();
			}
			if (ext == null) {
				return this;
			}
			for (int z = 0; z < this.header.sizeChildren(); z++) {
				BaseItem child = this.header.getChild(z);
				if (child instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity entity = (XMLEntity) child;
				Object key = null;
				if (ext.equals(".js") && SCRIPT.equals(entity.getTag())) {
					key = entity.getValue(KEY_SRC);
				}
				if (ext.equals(".css") && LINK.equals(entity.getTag())) {
					key = entity.getValue(KEY_HREF);
				}
				if (key != null && key instanceof String) {
					String k = (String) key;
					if (k.endsWith(string)) {
						this.header.withoutChild(entity);
					}
				}
			}
		}
		return this;
	}
}
