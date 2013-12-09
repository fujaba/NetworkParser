package de.uniks.networkparser.gui.brush;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import de.uniks.networkparser.gui.Style;

public class RegExRule {
	/**
	 * The compiled pattern.
	 */
	protected Pattern pattern;
	/**
	 * Override Style
	 */
	protected Style style;

	protected HashMap<Integer, Object> groupOperations = new HashMap<Integer, Object>();
	
	public RegExRule(){
		groupOperations.put(0, null);
	}

	/**
	 * Get the compiled pattern
	 * 
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	public Style getStyle() {
		return style;
	}
	
	public void setStyleKey(String value) {
		this.groupOperations.put(0, value);
	}
	
	public String getStyleKey() {
		if (groupOperations.size() == 1) {
			return (String) groupOperations.get(0);
		}
		return null;
	}

	/**
	 * Get the map of group operations. For more details, see
	 * {@link #groupOperations}.
	 * 
	 * @return a copy of the group operations map
	 */
	public Map<Integer, Object> getGroupOperations() {
		return new HashMap<Integer, Object>(groupOperations);
	}
	
	public void addToGroupOperation(Object... values){
		int startIndex=groupOperations.size();
		for(int i=0;i<values.length;i++){
			this.groupOperations.put(startIndex++, values[i]);
		}
	}
}
