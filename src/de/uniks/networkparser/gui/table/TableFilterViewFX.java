package de.uniks.networkparser.gui.table;

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
			if(!matchesSearchCriteria(iterator.next())){
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
