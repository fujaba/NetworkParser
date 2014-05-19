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
import java.util.Iterator;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class TableFilterViewFX extends TableFilterView implements ChangeListener<String>{
	public TableFilterViewFX(TableComponentInterface tableComponent) {
		super(tableComponent);
	}

	@Override
	public void changed(ObservableValue<? extends String> property, String oldValue,
			String newValue) {
		refresh(newValue);
	}
	
	@Override
	public void refreshSearch(){
		List<Object> resultList = component.getItems(false);
		for(Iterator<Object> iterator = resultList.iterator();iterator.hasNext();){
			if(!matchesSearchCriteria( iterator.next() )){
				iterator.remove();
			}
		}
		if(!lastSearchDetails){
			List<Object> sourceList = component.getItems(true);
			
			// and now the other way round
			for(Iterator<Object> iterator = sourceList.iterator();iterator.hasNext();){
				Object item = iterator.next();
				if (!resultList.contains(item)) {
					if (matchesSearchCriteria(item)) {
						resultList.add(item);
					}
				}
			}
		}
	}
}
