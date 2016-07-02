package de.uniks.networkparser.ext.javafx.component;

/*
NetworkParser
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.ext.javafx.TableList;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class TableFilterView implements ChangeListener<String>{
	private String[] lastSearchCriteriaItems;
	private ArrayList<String> searchProperties = new ArrayList<String>();
	protected TableComponent component;
	private Column updateField;
	protected boolean lastSearchDetails;
	protected String lastSearchCriteria = "##";
	protected TableList sourceFullList;

	public TableFilterView(TableComponent tableComponent) {
		this.component = tableComponent;
		this.sourceFullList = new TableList();
		this.sourceFullList.addPropertyChangeListener(component);
		this.sourceFullList.setIdMap(component.getMap());
	}

	public void setSearchProperties(String... searchProperties) {
		if (searchProperties != null) {
			this.searchProperties.clear();
			for (String item : searchProperties) {
				this.searchProperties.add(item);
			}
		}
	}

	public void refresh() {
		refresh("");
	}

	public void refresh(String searchCriteria) {
		// if search did not change do nothing
		if (searchCriteria == null)
			return; // <========= sudden death
		lastSearchDetails = searchCriteria.contains(lastSearchCriteria);
		lastSearchCriteria = searchCriteria;

		Tokener stringTokener = new Tokener();
		stringTokener.withBuffer(searchCriteria.toLowerCase());
		SimpleList<String> stringList = stringTokener.getStringList();
		ArrayList<String> searchList = new ArrayList<String>();
		for (int i = 0; i < stringList.size(); i++) {
			if (stringList.get(i).endsWith("-") && i < stringList.size() - 1) {
				String temp = stringList.get(i);
				temp = temp.substring(0, temp.length() - 1);
				searchList.addAll(stringTokener.splitStrings(temp.trim(), true));
				searchList.add("-" + stringList.get(++i).trim());
			} else {
				searchList.addAll(stringTokener.splitStrings(stringList.get(i),
						true));
			}
		}
		lastSearchCriteriaItems = searchList.toArray(new String[searchList
				.size()]);

		refreshSearch();
		refreshCounter();
	}

	public void refreshSearch(){
		List<Object> resultList = component.getItems();
		try {
			for(Iterator<Object> iterator = resultList.iterator();iterator.hasNext();){
				if(!matchesSearchCriteria( iterator.next() )){
					iterator.remove();
				}
			}
			if(!lastSearchDetails){
				// and now the other way round
				for(Iterator<Object> iterator = sourceFullList.iterator();iterator.hasNext();){
					Object item = iterator.next();
					if (!resultList.contains(item)) {
						if (matchesSearchCriteria(item)) {
							resultList.add(item);
						}
					}
				}
			}
		}catch(Exception e) {

		}
	}

	public boolean matchesSearchCriteria(Object item) {
		if (lastSearchCriteriaItems == null) {
			return true;
		}
		StringBuilder fullText = new StringBuilder();
		SendableEntityCreator creatorClass = component.getMap()
				.getCreatorClass(item);
		// SEARCH FOR #ID:3
		if (creatorClass == null) {
			return false;
		}
		for (String property : searchProperties) {
			Object value = creatorClass.getValue(item, property);
			if (value != null) {
				fullText.append(" " + value.toString().toLowerCase());
			}
		}

		Boolean matches = true;
		for (String word : lastSearchCriteriaItems) {
			if (!"".equals(word)) {
				if(word.indexOf("\\|")>0){
					String[] orWords=word.split("\\|");
					for(String orWord : orWords) {
						if( !simpleSearch(fullText, orWord, creatorClass, item)) {
							matches = false;
							break;
						}
					}
				}
				if(!simpleSearch(fullText, word, creatorClass, item)) {
					matches = false;
					break;
				}
			}
		}

		return matches;
	}

	private boolean simpleSearch(StringBuilder fullText, String word, SendableEntityCreator creatorClass, Object item) {
		int pos = word.indexOf(":");
		if (word.startsWith("#") && pos > 1) {
			String propString = word.substring(1, pos);

			if (searchProperties.contains(propString)) {
				String value = word.substring(pos + 1);
				Object objValue = creatorClass.getValue(item,
						word.substring(1, pos));
				if (objValue != null) {
					String itemValue = objValue.toString()
							.toLowerCase();
					// Search for simple Property
					if (itemValue.indexOf(value) < 0) {
						return false;
					}
				}
			} else {
				return false;
			}
		} else if (word.startsWith("-") && word.length() > 1) {
			if (fullText.indexOf(word.substring(1)) >= 0) {
				return false;
			}
		} else {
			if (fullText.indexOf(word) < 0) {
				// no this search word is not found in full text
				return false;
			}
		}
		return true;
	}

	public TableFilterView withCounterColumn(Column column) {
		this.updateField = column;
		refreshCounter();
		return this;
	}

	public void refreshCounter() {
		if (updateField != null) {
			TableColumnFX column = component.getColumn(updateField);
			if (column != null) {
				column.UpdateCount();
			}
		}
	}

	@Override
	public void changed(ObservableValue<? extends String> property, String oldValue,
			String newValue) {
		refresh(newValue);
	}

	public boolean addItem(Object item) {
		if(!sourceFullList.contains(item)) {
			sourceFullList.add(item);
			if (matchesSearchCriteria(item)) {
				component.getItems().add(item);
			}
			return true;
		}
		return false;
	}

	public TableList getFullList() {
		return sourceFullList;
	}

	public boolean removeItem(Object item) {
		if(sourceFullList.contains(item)) {
			sourceFullList.remove(item);
			if (matchesSearchCriteria(item)) {
				component.getItems().remove(item);
			}
			return true;
		}
		return false;
	}
}
