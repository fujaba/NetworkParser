package de.uniks.networkparser.parser;

import java.util.Iterator;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class ExcelRow.
 *
 * @author Stefan
 */
public class ExcelRow implements Iterable<ExcelCell> {
	private SimpleList<ExcelCell> children;
	private Object parent;
	private String name;
	private int len;

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the excel row
	 */
	public ExcelRow withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * With parent.
	 *
	 * @param parent the parent
	 * @return the excel row
	 */
	public ExcelRow withParent(Object parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public Object getParent() {
		return parent;
	}

	/**
	 * Gets the row pos.
	 *
	 * @return the row pos
	 */
	public int getRowPos() {
		if (this.size() > 0) {
			return first().getReferenz().y;
		}
		return -1;
	}

	private ExcelCell first() {
		if (children == null) {
			return null;
		}
		return children.first();
	}

	/**
	 * Gets the item.
	 *
	 * @param index the index
	 * @return the item
	 */
	public ExcelCell getItem(int index) {
		for (int i = 0; i < this.size(); i++) {
			ExcelCell cell = this.get(i);
			if (cell != null && cell.getReferenz().x == index) {
				return cell;
			}
		}
		return new ExcelCell();
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the excel cell
	 */
	public ExcelCell get(int index) {
		return this.children.get(index);
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public SimpleList<ExcelCell> getChildren() {
		return children;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean add(ExcelCell... values) {
		if (values == null) {
			return false;
		}
		if (children == null) {
			children = new SimpleList<ExcelCell>();
		}
		boolean result = true;
		for (ExcelCell item : values) {
			if (item == null) {
				continue;
			}
			Object content = item.getContent();
			if (content != null) {
				int temp = content.toString().length();
				if (temp > this.len) {
					this.len = temp;
				}
			}
			result = children.add(item) && result;
		}
		return true;
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<ExcelCell> iterator() {
		if (children == null) {
			children = new SimpleList<ExcelCell>();
		}
		return children.iterator();
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the excel row
	 */
	public ExcelRow with(ExcelCell... values) {
		add(values);
		return this;
	}

	/**
	 * Removes the.
	 *
	 * @param i the i
	 * @return the excel cell
	 */
	public ExcelCell remove(int i) {
		ExcelCell result = this.children.remove(i);
		if (this.parent != null) {
			if (parent instanceof AbstractList<?>) {
				AbstractList<?> collection = (AbstractList<?>) parent;
				collection.remove(i);
			}
		}
		return result;
	}

	/**
	 * Copy.
	 *
	 * @param pos the pos
	 * @return the excel cell
	 */
	public ExcelCell copy(int pos) {
		ExcelCell excelCell = this.get(pos);
		if (excelCell == null) {
			return null;
		}
		Object content = this.get(pos).getContent();
		ExcelCell cell = new ExcelCell().withContent(content);
		this.children.add(pos + 1, cell);
		if (this.parent != null) {
			if (parent instanceof AbstractList<?>) {
				AbstractList<?> collection = (AbstractList<?>) parent;
				collection.add(pos, content);
			}
		}
		return cell;
	}

	/**
	 * Gets the content length.
	 *
	 * @return the content length
	 */
	public int getContentLength() {
		return len;
	}

}
