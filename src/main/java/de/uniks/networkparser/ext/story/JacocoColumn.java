package de.uniks.networkparser.ext.story;

/*
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
import java.util.Collection;
import java.util.Comparator;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ext.generic.ReflectionBlackBoxTester;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class JacocoColumn.
 *
 * @author Stefan
 */
public class JacocoColumn implements JacocoColumnListener, Comparator<Object> {
	private static final String COLUMRENDERER = "org.jacoco.report.internal.html.table.IColumnRenderer";
	private static final String PACKAGE = "PACKAGE";
	private static final String BUNDLE = "BUNDLE";
	private static final String SOURCEFILE = "SOURCEFILE";
	private static final String METHOD = "METHOD";
	private Object proxy;
	private static String EMPTY = "";
	private SimpleKeyValueList<String, Integer> value = new SimpleKeyValueList<String, Integer>();
	private SimpleKeyValueList<Object, String> classes = new SimpleKeyValueList<Object, String>();

	/**
	 * Creates the.
	 *
	 * @param logger the logger
	 * @return the jacoco column
	 */
	public static JacocoColumn create(NetworkParserLog logger) {
		JacocoColumn jacocoColumn = new JacocoColumn();
		Class<?> proxyClass = ReflectionLoader.getSimpleClass(COLUMRENDERER);
		if (proxyClass == null) {
			if (!ReflectionBlackBoxTester.isTester() && logger != null) {
			    logger.info(JacocoColumn.class, "create", "NO JACOCO FOUND ON BUILD-PATH");
			}
			return null;
		}
		Object item = ReflectionLoader.createProxy(jacocoColumn, proxyClass);

		jacocoColumn.withProxy(item);
		return jacocoColumn;
	}

	private JacocoColumn withProxy(Object item) {
		this.proxy = item;
		return this;
	}

	/**
	 * Gets the proxy.
	 *
	 * @return the proxy
	 */
	public Object getProxy() {
		return proxy;
	}

	/**
	 * Gets the type.
	 *
	 * @param element the element
	 * @return the type
	 */
	public String getType(Object element) {
		Object call = ReflectionLoader.calling(element, "getElementType", false, null);
		if (call != null) {
			return call.toString();
		}
		return EMPTY;
	}

	/**
	 * Gets the name.
	 *
	 * @param element the element
	 * @return the name
	 */
	public String getName(Object element) {
		Object call = ReflectionLoader.calling(element, "getName", false, null);
		if (call instanceof String) {
			return (String) call;
		}
		return EMPTY;
	}

	/**
	 * 	init(List &lt;? extends ITableItem&gt; items, ICoverageNode total) { 
	 * 	Init JacocoColumn.
	 *
	 * @param items Items
	 * @param total ICoverageNode
	 * @return success
	 */
	public boolean init(Object items, Object total) {
		String type = getType(total);
		if (PACKAGE.equalsIgnoreCase(type)) {
			Collection<?> classes = (Collection<?>) ReflectionLoader.call(total, "getClasses");

			for (Object item : classes) {
				String name = getName(item);
				Collection<?> methods = (Collection<?>) ReflectionLoader.call(item, "getMethods");
				for (Object method : methods) {
					this.classes.add(method, name);
				}
			}
		}
		return true;
	}

	/**
	 * Set Footer.
	 *
	 * @param td HTMLElement
	 * @param total ICoverageNode
	 * @param resources Resources
	 * @param base ReportOutputFolder
	 */
	public void footer(Object td, Object total, Object resources, Object base) {
		String search = getSearch(total);

		Integer no = (Integer) value.getValue(search);
		if (no != null) {
			setText(td, "" + no);
		} else {
			setText(td, "0");
		}
	}

	/**
	 * Sets the text.
	 *
	 * @param item the item
	 * @param text the text
	 */
	public void setText(Object item, String text) {
		ReflectionLoader.calling(item, "text", false, null, text);
	}

	private String getSearch(Object node) {
		String search = getName(node);
		if (BUNDLE.equalsIgnoreCase(getType(node))) {
			search = "";
		}
		int pos = search.indexOf("(");
		if (pos > 0) {
			search = search.substring(0, pos);
		}
		pos = search.indexOf("$");
		if (pos > 0) {
			search = search.substring(0, pos);
		}
		return search;
	}

	/**
	 * Adds the value to list.
	 *
	 * @param key the key
	 * @param no the no
	 */
	public void addValueToList(String key, int no) {
		String fullClass = key.substring(0, key.indexOf(":"));

		/* Calculate all Sums */
		addToPos("", no);
		int pos = fullClass.lastIndexOf(".");
		if (pos > 0) {
			addToPos(fullClass.substring(0, pos), no);
		}
		addToPos(fullClass, no);

		value.add(key, no);
	}

	private void addToPos(String key, int no) {
		int pos = value.indexOf(key);
		if (pos > 0) {
			Integer oldValue = value.getValueByIndex(pos);
			value.put(key, oldValue + no);
			return;
		}
		value.add(key, no);
	}

	/**
	 * Set new Item.
	 *
	 * @param td HTMLElement
	 * @param item ITableItem
	 * @param resources Resources
	 * @param base ReportOutputFolder
	 */
	public void item(Object td, Object item, Object resources, Object base) {
		Object node = ReflectionLoader.calling(item, "getNode", false, null);
		String type = getType(node);
		String search;
		if (SOURCEFILE.equalsIgnoreCase(type)) {
			String name = getName(node);
			int pos = name.lastIndexOf(".");
			search = name.substring(0, pos);
			Object value = this.value.getValue(search);
			if (value != null) {
				setText(td, "" + value);
			}
			return;
		}

		if (METHOD.equalsIgnoreCase(type)) {
			/* Its Methods */
			String className = classes.get(node);

			Integer firstLine = (Integer) ReflectionLoader.call(node, "getFirstLine");
			String name = getName(node);
			if (name.indexOf("(") > 0) {
				name = name.substring(0, name.indexOf("("));
			}
			search = className + ":" + name + ":" + firstLine;
			Object value = this.value.getValue(search);
			if (value instanceof Integer) {
				Integer no = (Integer) value;
				setText(td, no.toString());
			}
			return;
		}

		search = getSearch(node);
		Object value = this.value.getValue(search);
		if (value != null) {
			setText(td, "" + value);
		}
	}

	/**
	 * Gets the comparator.
	 *
	 * @return the comparator
	 */
	public Comparator<Object> getComparator() {
		return this;
	}

	/**
	 * Compare.
	 *
	 * @param o1 the o 1
	 * @param o2 the o 2
	 * @return the int
	 */
	@Override
	public int compare(Object o1, Object o2) {
		return 1;
	}
}
