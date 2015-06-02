package de.uniks.networkparser.gui.javafx.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class SpinnerIntegerEditControl extends EditControl<NumberSpinner>{
	@Override
	public Object getValue(boolean convert) {
		return getControl().getValue(convert);
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.INTEGER;
	}

	@Override
	public NumberSpinner createControl(Column column) {
		return new NumberSpinner().withOwner(this).withColumn(column);
	}

	@Override
	public SpinnerIntegerEditControl withValue(Object value) {
		this.value = value;
		getControl().withValue(value);
		return this;
	}
}
