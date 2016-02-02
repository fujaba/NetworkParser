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
import de.uniks.networkparser.list.SimpleList;

public class ExcelRow extends SimpleList<ExcelCell>{
	public int getRowPos() {
		if(this.size()>0) {
			return first().getReferenz().y;
		}
		return -1;
	}
	
	public ExcelCell getItem(int index) {
		for(int i=0;i<this.size();i++) {
			ExcelCell cell = this.get(i);
			if(cell != null && cell.getReferenz().x == index) {
				return cell;
			}
		}
		return new ExcelCell();
	}
}
