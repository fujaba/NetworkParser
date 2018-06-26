package de.uniks.networkparser.gui.controls;

/*
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
import java.util.Collection;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.SimpleList;

public class Form extends Control {
	/* constants */
	public static final String FORM = "form";

	public static final String METHOD = "method";

	protected String method = "get";

	private SimpleList<Control> elements;

	public Form() {
		super();
		/* Set variables of parent class */
		this.className = FORM;
		this.addBaseElements(METHOD);
		this.addBaseElements(PROPERTY_ELEMENTS);
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
				firePropertyChange(PROPERTY_ELEMENTS, null, control);
			}
		}
		return changed;
	}

	@Override
	public Object getValue(String key) {
		if (METHOD.equals(key)) {
			return this.getMethod();
		}
		else if (PROPERTY_ELEMENTS.equals(key)) {
			return this.getElements();
		}
		return super.getValue(key);
	}

	@Override
	public boolean setValue(String key, Object value) {
		if (METHOD.equals(key)) {
			return this.setMethod(""+value);
		}
		else if (PROPERTY_ELEMENTS.equals(key)) {
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

	@Override
	public Form newInstance() {
		return new Form();
	}
}
