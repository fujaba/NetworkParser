package de.uniks.networkparser.gui.controls;

import java.util.Collection;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.SimpleList;

public class Form extends Control {
	/* constants */
	public static final String FORM = "form";

	public static final String METHOD = "method";

	public static final String ELEMENTS = "elements";

	protected String method = "get";

	private SimpleList<Control> elements;

	public Form() {
		super();
		/* Set variables of parent class */
		this.className = FORM;
		this.addBaseElements(METHOD);
		this.addBaseElements(ELEMENTS);
	}

	public String getMethod() {
		return method;
	}

	/**
	 * The Submit Method. eg. "get".
	 * 
	 * @param value Set Submit Method: POST, GET
	 * @return return success
	 */
	public boolean setMethod(String value) {
		String oldValue = this.method;
		this.method = value;
		return firePropertyChange(METHOD, oldValue, value);
	}

	public SimpleList<Control> getElements() {
		return elements;
	}

	public Form withElement(Control... elements) {
		addElement(elements);
		return this;
	}
	public boolean addElement(Control... elements) {
		if(elements == null) {
			return false;
		}
		boolean changed = false;
		if (this.elements == null) {
			this.elements = new SimpleList<Control>();
		}
		for (Control control : elements) {
			if(this.elements.add(control)) {
				changed = true;
				firePropertyChange(ELEMENTS, null, control);
			}
		}
		return changed;
	}

	@Override
	public Object getValue(String key) {
		if (METHOD.equals(key)) {
			return this.getMethod();
		}
		else if (ELEMENTS.equals(key)) {
			return this.getElements();
		}
		return super.getValue(key);
	}

	@Override
	public boolean setValue(String key, Object value) {
		if (METHOD.equals(key)) {
			return this.setMethod(""+value);
		}
		else if (ELEMENTS.equals(key)) {
			if(value instanceof Control) {
				return this.addElement((Control)value);
			} else if(value instanceof Control[]) {
				return this.addElement((Control[])value);
			} else if(value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>)value;
				Control[] array = ((Collection<?>) value).toArray(new Control[list.size()]);
				return this.addElement(array);
			}
		}
		return super.setValue(key, value);
	}
	public Form withDataBinding(IdMap map, Object entity, boolean addCommandBtn){
//		this.map = map;
//		this.item = entity;
//		textClazz = (TextItems) map.getCreator(TextItems.class.getName(), true);

//		SendableEntityCreator creator = map.getCreatorClass(item);
//		if(creator != null){
//			this.setCenter(items);
//			withDataBinding(addCommandBtn, creator.getProperties());
//		}
		return this;
	}
}
