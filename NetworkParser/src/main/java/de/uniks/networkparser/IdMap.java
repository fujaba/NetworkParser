package de.uniks.networkparser;

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
import de.uniks.networkparser.interfaces.BaseItem;

public abstract class IdMap extends IdMapEncoder {
	public abstract Object decode(BaseItem value);
	
	public abstract Object decode(String value);

	private boolean caseSensitive = false;

	/**
	 * For setting the Option of checking the CaseSensitive of the Properties
	 *
	 * @param value
	 *            the new Value of CaseSensitive
	 * @return XMLGrammar Instance
	 */
	public IdMap withCaseSensitive(boolean value) {
		this.caseSensitive = value;
		return this;
	}

	/**
	 * @return the CaseSensitive Option
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

}
