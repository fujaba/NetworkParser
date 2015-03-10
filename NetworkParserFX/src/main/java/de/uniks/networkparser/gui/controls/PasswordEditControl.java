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
import javafx.scene.control.PasswordField;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class PasswordEditControl extends EditControl<PasswordField>{
	@Override
	public Object getValue(boolean convert) {
		return this.control.getText();
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.PASSWORD;
	}

	@Override
	public PasswordEditControl withValue(Object value) {
		this.value = value;
		getControl().setText(""+value);
		return this;
	}

	@Override
	public PasswordField createControl(Column column) {
		return new PasswordField();
	}
}
