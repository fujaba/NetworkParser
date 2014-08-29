package de.uniks.networkparser.gui.theme;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.util.LinkedHashMap;
import java.util.Map;
import de.uniks.networkparser.gui.Style;

public class Theme {
	/**
	 * The styles of this theme.
	 */
	protected Map<String, Style> styles= new LinkedHashMap<String, Style>();

	  /**
	   * Add style.
	   * @param styleKey the keyword of the style
	   * @param style the style
	   * @return see the return value of {@link Map#put(Object, Object)}
	   */
	  public Style addStyle(String styleKey, Style style) {
	    return styles.put(styleKey, style);
	  }
	 
	  /**
	   * Get the style by keyword.
	   * @param key the keyword
	   * @return if the style related to the {@code key} not exist, the
	   * style of 'plain' will return.
	   */
	  public Style getStyle(String key) {
	    Style returnStyle = styles.get(key);
	    return returnStyle != null ? returnStyle : getStyle(StyleConstants.STYLE_PLAIN);
	  }
}
