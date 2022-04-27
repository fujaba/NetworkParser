package de.uniks.networkparser.xml;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphCustomItem;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class HTMLEntity.
 *
 * @author Stefan
 */
public class HTMLEntity implements BaseItem {
	
	/** The Constant PROPERTY_HEADER. */
	public static final String PROPERTY_HEADER = "head";
	
	/** The Constant PROPERTY_BODY. */
	public static final String PROPERTY_BODY = "body";
	
	/** The Constant IMAGEFORMAT. */
	public static final String IMAGEFORMAT = " .bmp .jpg .jpeg .png .gif .svg ";
	
	/** The Constant SCRIPT. */
	public static final String SCRIPT = "script";
	
	/** The Constant LINK. */
	public static final String LINK = "link";
	
	/** The Constant KEY_HREF. */
	public static final String KEY_HREF = "href";
	
	/** The Constant KEY_SRC. */
	public static final String KEY_SRC = "src";
	
	/** The Constant GRAPH. */
	public static final String GRAPH = "Graph";
	
	/** The Constant CLASSEDITOR. */
	public static final String CLASSEDITOR = "ClassEditor";
	
	/** The Constant ACTION. */
	public static final String ACTION = "action";
	
    /** The Constant GRAPH_RESOURCES. */
    //"diagramstyle.css", 
	public static final String[] GRAPH_RESOURCES = new String[] {  "style.css", "diagram.js", "dagre.min.js", "jspdf.min.js" };
	
	/** The Constant CODE_RESOURCES. */
	public static final String[] CODE_RESOURCES = new String[] { "highlight.pack.js", "highlightjs-line-numbers.min.js", };
	
	/** The Constant PROJECT_RESOURCES. */
	public static final String[] PROJECT_RESOURCES = new String[] {"d3.min.js"};

	private XMLEntity body = new XMLEntity().withType("body");
	private XMLEntity header = new XMLEntity().withType("head");

	private Map<String, List<String>> conenctionHeader = new SimpleKeyValueList<String, List<String>>();
	private int statusCode = 200;
	private String statusMessage;
	private boolean plainBody = true;

	/**
	 * With plain.
	 *
	 * @param plain the plain
	 * @return the HTML entity
	 */
	public HTMLEntity withPlain(boolean plain) {
		this.plainBody = plain;
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @return the string
	 */
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public XMLEntity getHeader() {
		return header;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public XMLEntity getBody() {
		return body;
	}
	
	/**
	 * Gets the body json object.
	 *
	 * @return the body json object
	 */
	public JsonObject getBodyJsonObject() {
		return new JsonObject().withValue(body.getValue());
	}

	/**
	 * With encoding.
	 *
	 * @param encoding the encoding
	 * @return the HTML entity
	 */
	public HTMLEntity withEncoding(String encoding) {
	    createChild("meta", this.getHeader(), "http-equiv", "Content-Type", "content", "text/html;charset=" + encoding);
		return this;
	}

	/**
	 * With page break.
	 *
	 * @return the HTML entity
	 */
	public HTMLEntity withPageBreak() {
		XMLEntity pageBreak = new XMLEntity().withType("div").withCloseTag();
		pageBreak.put("style", "page-break-before:always");
		this.body.withChild(pageBreak);
		return this;
	}

	/**
	 * With title.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withTitle(String value) {
		XMLEntity titleTag = new XMLEntity().withType("title").withValue(value);
		this.header.with(titleTag);
		return this;
	}

	/**
	 * With script.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withScript(CharSequence... value) {
		return withScript(null, value);
	}

	/**
	 * With script.
	 *
	 * @param parentNode the parent node
	 * @param values the values
	 * @return the HTML entity
	 */
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

	/**
	 * Creates the script.
	 *
	 * @param value the value
	 * @param parentNode the parent node
	 * @return the XML entity
	 */
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

	/**
	 * With style.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
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

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the HTML entity
	 */
	public HTMLEntity with(Object... values) {
		add(values);
		return this;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
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
						buffer.nextClean();
					}
					if (buffer.startsWith("<head", buffer.position(), false)) {
						int end = buffer.indexOf("</head>");
						int length = buffer.length();
						buffer.withLength(end);
						this.header.withValue(tokener, buffer);
						buffer.withLength(length);
						buffer.skipTo('>', false);
						buffer.skip();
						buffer.nextClean();
					}
					/* SO PARSE BODY */
					if (plainBody) {
						if (buffer.startsWith("<body", buffer.position(), false)) {
							buffer.skipTo('>', false);
							buffer.skip();
							buffer.nextClean();
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

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(Object key) {
		Object result = this.header.getValue(key);
		if (result != null) {
			return result;
		}
		return this.body.getValue(key);
	}

	/**
	 * With header.
	 *
	 * @param ref the ref
	 * @return the HTML entity
	 */
	public HTMLEntity withHeader(String ref) {
		XMLEntity child = getChild(ref);
		if (child != null) {
			this.header.with(child);
		}
		return this;
	}

	/**
	 * With base.
	 *
	 * @param path the path
	 * @return the HTML entity
	 */
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

	/**
	 * With body.
	 *
	 * @param ref the ref
	 * @return the HTML entity
	 */
	public HTMLEntity withBody(String ref) {
		XMLEntity child = getChild(ref);
		if (child != null) {
			this.body.with(child);
		}
		return this;
	}

    /**
     * Create a new Tag as Child of Body Parent.
     *
     * @param tag    the new Tag
     * @param values Optional May be a child of Body or Body or head
     * @return the created XMLEntity Item
     */
    public XMLEntity createChild(String tag, String... values) {
        return createChild(tag, null, values);
    }

    /**
     * Create a new Tag as Child of Parent with HTML-Value.
     *
     * @param tag    Tag of Children can be a Refernce
     * @param parentNode the parent node
     * @param values Attribute of Children
     * @return ThisComponent
     */
    public XMLEntity createChild(String tag, XMLEntity parentNode, String... values) {
        if (parentNode == null) {
            parentNode = this.body;
        }
        if (tag == null) {
            return null;
        }
        String[] tags = tag.split("\\.");
        for (int i = 0; i < tags.length; i++) {
            if(i==tags.length - 1) {
                // LAST One
                parentNode = parentNode.createChild(tags[i], values);
            } else {
                parentNode = parentNode.createChild(tags[i]);
            }
        }
        return parentNode;
    }
	
	/**
	 * Creates the table.
	 *
	 * @param parentNode the parent node
	 * @param labels the labels
	 * @return the XML entity
	 */
	public XMLEntity createTable(XMLEntity parentNode, String... labels) {
		XMLEntity table = createChild("table", parentNode);
		if (labels != null && labels.length > 0) {
			table.with("style", labels[0]);
			XMLEntity tr = createChild("tr", table);
			for (int i = 1; i < labels.length; i += 2) {
			    createChild("td", tr);
				table.with("style", labels[i]);
				table.withValue(labels[i + 1]);
			}
		}
		return table;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleSet<XMLEntity>();
	}

	/**
	 * Adds the style.
	 *
	 * @param name the name
	 * @param style the style
	 * @return the HTML entity
	 */
	public HTMLEntity addStyle(String name, String style) {
		XMLEntity styleElement = null;
		for (int i = 0; i < header.size(); i++) {
			BaseItem child = header.getChild(i);
			if (!(child instanceof XMLEntity)) {
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

	/**
	 * With graph.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withGraph(GraphModel value) {
		URL resource = GraphList.class.getResource("");
		if (resource == null || value == null) {
			return this;
		}
		GraphSimpleSet children = GraphUtil.getChildren(value);
		for(GraphMember item : children) {
		  if(item instanceof GraphCustomItem && GraphModel.PROPERTY_EXTERNAL.equals(item.getName())) {
		    if(GraphUtil.isGenerate(item)) {
		      return withGraph(value, null);
		    }
		  }
		}
		return withGraph(value, resource.toString());
	}

	/**
	 * Adds the resources.
	 *
	 * @param importFiles the import files
	 * @param name the name
	 * @param content the content
	 * @return the HTML entity
	 */
	public HTMLEntity addResources(boolean importFiles, String name, String content) {
		if (importFiles) {
			this.withScript(this.getHeader(), content);
		} else {
			this.withHeader(name);
		}
		return this;
	}

	/**
	 * With graph.
	 *
	 * @param value the value
	 * @param resource the resource
	 * @return the HTML entity
	 */
	public HTMLEntity withGraph(GraphModel value, String resource) {
		String graphPath = GraphUtil.getGraphPath(value);
		if (!GRAPH.equals(graphPath) && !CLASSEDITOR.equals(graphPath)) {
			graphPath = GRAPH;
		}
		if (value != null) {
			String graph = value.toString(new GraphConverter());
			return withGraph(graph, resource, graphPath);
		}
		return this;
	}

	/**
	 * With graph.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withGraph(Entity value) {
		URL resource = GraphList.class.getResource("");
		if (resource == null || value == null) {
			return this;
		}
		return withGraph(value.toString(2), resource.toString(), GRAPH);
	}

	/**
	 * With graph.
	 *
	 * @param graph the graph
	 * @param path the path
	 * @param editor the editor
	 * @return the HTML entity
	 */
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
			for (String item : GRAPH_RESOURCES) {
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

	/**
	 * Gets the header.
	 *
	 * @param url the url
	 * @return the header
	 */
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

	/**
	 * With new line.
	 *
	 * @return the HTML entity
	 */
	public HTMLEntity withNewLine() {
		XMLEntity child = new XMLEntity().withType("br").withCloseTag();
		child.withValue(BaseItem.CRLF);
		this.body.withChild(child);
		return this;
	}

	/**
	 * With text.
	 *
	 * @param text the text
	 * @return the HTML entity
	 */
	public HTMLEntity withText(String text) {
		XMLEntity child = new XMLEntity().withValue(text);
		this.body.withChild(child);
		return this;
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
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

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return body.size();
	}

	/**
	 * Gets the elements by.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the elements by
	 */
	public XMLEntity getElementsBy(String key, String value) {
		XMLEntity item = new XMLEntity();
		addChildren(item, this.header.getElementsBy(key, value));
		addChildren(item, this.body.getElementsBy(key, value));
		return item;
	}
	
	private void addChildren(XMLEntity item, EntityList list) {
		if (item != null && list != null) {
		    if(list.sizeChildren() == 0 && list.size() > 0) {
		        item.add(list);
		    }else {
    			for (int z = 0; z < list.sizeChildren(); z++) {
    				BaseItem child = list.getChild(z);
    				if (child instanceof EntityList) {
    					item.withChild(child);
    				}
    			}
		    }
		}
	}

	/**
	 * Gets the element by.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the element by
	 */
	public BaseItem getElementBy(String key, String value) {
		Entity item = this.header.getElementBy(key, value);
		if (item != null) {
			return item;
		}
		item = this.body.getElementBy(key, value);
		return item;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withValue(String value) {
		XMLEntity htmlPage = new XMLEntity().withValue(value);
		/* All Children possible head and body */
		for (int i = 0; i < htmlPage.sizeChildren(); i++) {
			BaseItem item = htmlPage.getChild(i);
			if (!(item instanceof XMLEntity)) {
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

	/**
	 * With status.
	 *
	 * @param code the code
	 * @param message the message
	 * @return the HTML entity
	 */
	public HTMLEntity withStatus(int code, String message) {
		this.statusCode = code;
		this.statusMessage = message;
		return this;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * With connection header.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the HTML entity
	 */
	public HTMLEntity withConnectionHeader(String key, String value) {
		SimpleList<String> list = new SimpleList<String>().with(value);
		this.conenctionHeader.put(key, list);
		return this;
	}

	/**
	 * With connection header.
	 *
	 * @param headerFields the header fields
	 * @return the HTML entity
	 */
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

	/**
	 * Gets the connection header.
	 *
	 * @return the connection header
	 */
	public Map<String, List<String>> getConnectionHeader() {
		return conenctionHeader;
	}

	/**
	 * Gets the connection header.
	 *
	 * @param key the key
	 * @return the connection header
	 */
	public String getConnectionHeader(String key) {
		List<String> list = conenctionHeader.get(key);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Gets the connection headers.
	 *
	 * @param key the key
	 * @return the connection headers
	 */
	public List<String> getConnectionHeaders(String key) {
		return conenctionHeader.get(key);
	}

	/**
	 * Without header.
	 *
	 * @param string the string
	 * @return the HTML entity
	 */
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
				if (!(child instanceof XMLEntity)) {
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

	/**
	 * With action button.
	 *
	 * @param label the label
	 * @param actionValue the action value
	 * @param url the url
	 * @return the XML entity
	 */
	public XMLEntity withActionButton(String label, String actionValue, String... url) {
		XMLEntity actionTag = createChild("form").withKeyValue("method", "post");
		if(url != null && url.length>0) {
			actionTag.withKeyValue("action", url[0]);
		}
		actionTag.createChild("input", "type", "submit", "value", label, "class", "button");
		actionTag.createChild("input", "type", "hidden", "id", ACTION, "name", ACTION, "value", actionValue);
		return actionTag;
	}
	
	/**
	 * Creates the input.
	 *
	 * @param label the label
	 * @param name the name
	 * @param value the value
	 * @return the base item
	 */
	public BaseItem createInput(String label, String name, Object value) {
		XMLEntity parent = new XMLEntity().withType("div").withKeyValue("class", "inputgroup");
		parent.createChild("label", "for", name).withValueItem(label);
		
		if(value instanceof String && ((String) value).length()>20 && !name.equalsIgnoreCase("password")) {
		    parent.createChild("textarea", "name", name,  (String)value);
            return parent;
        }
		XMLEntity input = parent.createChild("input", "name", name);
		if(value instanceof Number) {
			input.withKeyValue("type", "number");
		}
		if(value instanceof Date || value instanceof DateTimeEntity) {
			input.withKeyValue("type", "date");
		}
		if(value instanceof String) {
			input.withKeyValue("type", "text");
			if(name.equalsIgnoreCase("password")) {
			    input.withKeyValue("type", "password");
			}
		}
		input.setValueItem("value", value);
		return parent;
	}
	
	public BaseItem createToast(String text, String... style) {
	    String styleClass = "success";
	    if(style != null && style.length>0) {
	        styleClass = style[0];
	    }
	    XMLEntity parentElement = createChild("div", "class", "toast "+ styleClass);
	    parentElement.createChild("span", "class", "content", text);
        parentElement.createChild("button", "type", "button", "onclick", "this.parentNode.style.display='none'", "x");
        return parentElement;
	}
	
    public BaseItem createToastError(Object text) {
        XMLEntity parentElement = createChild("div", "class", "toast  failure");
        if (text instanceof String) {
            parentElement.createChild("span", "class", "content", "" + text);
        } else if (text instanceof Error) {
            parentElement.createChild("span", "class", "content", "" + ((Error) text).getMessage());
        }
        parentElement.createChild("button", "type", "button", "onclick", "this.parentNode.style.display='none'", "x");
        return parentElement;
    }
}
