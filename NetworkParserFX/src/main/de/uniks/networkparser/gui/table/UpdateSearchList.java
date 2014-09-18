package de.uniks.networkparser.gui.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import de.uniks.networkparser.interfaces.SendableEntity;

public class UpdateSearchList implements PropertyChangeListener {
	protected TableComponent tableComponent;

	public UpdateSearchList(TableComponent tableComponent){
		this.tableComponent = tableComponent;
	}
	
	public void addItem(Object item){
		if (item instanceof SendableEntity) {
//			System.out.println("ADD"+item);
			((SendableEntity) item).addPropertyChangeListener(this);
		}
	}
	
	public void removeItem(Object item){
		if (item instanceof SendableEntity) {
//			System.out.println("REM"+item);
			((SendableEntity) item).removePropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		tableComponent.propertyChange(evt);
	}
}
