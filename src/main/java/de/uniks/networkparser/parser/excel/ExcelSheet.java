package de.uniks.networkparser.parser.excel;

/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in writing, software distributed under the Licence is
distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations under the Licence.
*/
import de.uniks.networkparser.Pos;
import de.uniks.networkparser.list.SimpleList;

/**
 * Metamodell for Excel-Sheet
 * @author Stefan Lindel
 */
public class ExcelSheet extends SimpleList<ExcelRow>{
	public static final String PROPERTY_NAME="name";
	private String name;

	public String getName() {
		return name;
	}

	public boolean setName(String value) {
		if ((this.name == null && value != null) || (this.name != null && this.name.equals(value) == false)) {
			this.name = value;
			return true;
		}
		return false;
	}

	public ExcelSheet withName(String value) {
		setName(value);
		return this;
	}

	public ExcelCell getItem(Pos pos) {
		for(int i=0;i<this.size();i++) {
			ExcelRow row = this.get(i);
			if(row != null && row.getRowPos()==pos.y) {
				return row.getItem(pos.x);
			}
		}
		return new ExcelCell();
	}
}
