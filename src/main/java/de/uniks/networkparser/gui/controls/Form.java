package de.uniks.networkparser.gui.controls;

import java.util.LinkedList;

import de.uniks.networkparser.list.SimpleList;

public class Form extends Control {
	/* constants */
	public static final String FORM = "form";

	public static final String PROPERTY = "property";

	public static final String METHOD = "method";

	public static final String ELEMENTS = "elements";

	protected String method = "get";

	private SimpleList<Control> elements;


	public Form() {
		super();
		/* Set variables of parent class */
		this.className = FORM;
		this.addBaseElements(PROPERTY);
		this.addBaseElements(ELEMENTS);
	}


	public String getMethod() {
		return method;
	}


	/**
	 * The Submit Method. eg. "get".
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}


	public SimpleList<Control> getElements() {
		if (this.elements == null) {
			this.elements = new SimpleList<>();
		}
		return elements;
	}


	public Form withElement(Control... elements) {
		for (Control control : elements) {
			getElements().add(control);
		}
		return this;
	}


	public void setElements(SimpleList<Control> elements) {
		this.elements = elements;
	}


	@Override
	public Object getValue(String key) {
		if (PROPERTY.equals(key)) {
			return this.property;
		}
		else if (METHOD.equals(key)) {
			return this.getMethod();
		}
		else if (ELEMENTS.equals(key)) {
			return this.elements;
		}
		return super.getValue(key);
	}

}
