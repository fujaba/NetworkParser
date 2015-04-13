package de.uniks.networkparser.gui.javafx.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

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

		StringTokener stringTokener = new StringTokener();
		stringTokener.withText(searchCriteria.toLowerCase());
		ArrayList<String> stringList = stringTokener.getStringList();
		ArrayList<String> searchList = new ArrayList<String>();
		for (int i = 0; i < stringList.size(); i++) {
			if (stringList.get(i).endsWith("-") && i < stringList.size() - 1) {
				String temp = stringList.get(i);
				temp = temp.substring(0, temp.length() - 1);
				searchList.addAll(stringTokener.getString(temp.trim(), true));
				searchList.add("-" + stringList.get(++i).trim());
			} else {
				searchList.addAll(stringTokener.getString(stringList.get(i),
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
				if(word.indexOf("|")>0){
					String[] orWords=word.split("|");
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