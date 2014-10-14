package de.uniks.networkparser.gui.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import javafx.scene.control.CheckBox;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;

public class CheckBoxEditControl extends EditControl<CheckBox>{
	@Override
	public Object getValue(boolean convert) {
		return this.control.isSelected();
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.CHECKBOX;
	}

	@Override
	public CheckBoxEditControl withValue(Object value) {
		this.value = value;
		getControl().setSelected((boolean)value);
		return this;
	}

	@Override
	public CheckBox createControl(Column column) {
		return new CheckBox();
	}
}
