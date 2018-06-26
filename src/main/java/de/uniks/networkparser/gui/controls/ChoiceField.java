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

public class ChoiceField extends Input<String> {
	/*
	 * Constants
	 */
	protected static final String CHECKBOX = "checkbox";

	protected static final String RADIO = "radio";

	protected static final String CHECKED = "checked";

	protected static final String VALUE = "value";

	public static final String ON = "on";

	public static final String OFF = "on";

	private boolean checked = false;


	public ChoiceField(boolean multi) {
		super();
		this.addBaseElements(CHECKED);
		if (multi) {
			this.type = RADIO;
		}
		else {
			this.type = CHECKBOX;
		}
	}

	public boolean isChecked() {
		return checked;
	}


	public boolean setChecked(boolean checked) {
		boolean oldValue = this.checked;
		this.checked = checked;
		boolean changed = firePropertyChange(CHECKED, oldValue, checked);
		if (changed) {
			this.setValue((checked) ? "on" : "off");
		}
		return changed;
	}


	@Override
	public boolean setValue(String value) {
		String oldValue = this.value;
		this.value = value;
		boolean changed = firePropertyChange(VALUE, oldValue, value);
		if (changed) {
			this.setChecked(ON.equals(value));
		}
		return changed;
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (CHECKED.equals(key)) {
			return this.setChecked(Boolean.valueOf("" + value));
		}
		return super.setValue(key, value);
	}


	@Override
	public Object getValue(String key) {
		if (CHECKED.equals(key)) {
			return (checked) ? CHECKED : null;
		}
		return super.getValue(key);
	}
	
	@Override
	public ChoiceField newInstance() {
		return new ChoiceField(false);
	}
}
