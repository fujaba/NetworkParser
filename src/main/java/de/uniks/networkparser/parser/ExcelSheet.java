package de.uniks.networkparser.parser;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;

/**
 * Metamodell for Excel-Sheet
 * 
 * @author Stefan Lindel
 */
public class ExcelSheet extends SimpleList<ExcelRow> {
	public static final String PROPERTY_NAME = "name";
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
		for (int i = 0; i < this.size(); i++) {
			ExcelRow row = this.get(i);
			if (row != null && row.getRowPos() == pos.y) {
				return row.getItem(pos.x);
			}
		}
		return new ExcelCell();
	}

	public ExcelRow createRow(Object parent) {
		ExcelRow excelRow = new ExcelRow();
		Pos pos = Pos.create(this.size(), 0);
		excelRow.withParent(parent);
		excelRow.withName(pos.toTag().toString());
		this.add(excelRow);
		return excelRow;
	}

	public ExcelRow getRow(Object source) {
		for (ExcelRow row : this) {
			if (row.getParent() == source) {
				return row;
			}
		}
		return null;
	}

	public String toString() {
		CharacterBuffer buffer = new CharacterBuffer();
		int i;
		SimpleList<SimpleList<ExcelCell>> cells = new SimpleList<SimpleList<ExcelCell>>();
		boolean empty = false;
		for (i = 0; i < this.size(); i++) {
			ExcelRow row = this.get(i);
			String name = row.getName();
			buffer.add(name);
			if (i + 1 < this.size()) {
				int l = name.length();
				while (l <= row.getContentLength()) {
					buffer.add(' ');
					l++;
				}
			}
			SimpleList<ExcelCell> rowCells = row.getChildren();
			if (cells == null) {
				empty = true;
			}
			cells.add(rowCells);
		}
		if (empty) {
			return buffer.toString();
		}
		buffer.add(BaseItem.CRLF);
		if (cells.size() > 0) {
			int count = cells.get(0).size();
			int rows = this.size();
			for (int c = 0; c < count; c++) {
				for (i = 0; i < rows; i++) {
					SimpleList<ExcelCell> row = cells.get(i);
					if (row == null || row.get(c) == null) {
						break;
					}
					Object content = row.get(c).getContent();
					if (content != null) {
						buffer.add(content.toString());
					}
					if (i + 1 < rows) {
						buffer.add(' ');
					}
				}
				buffer.add(BaseItem.CRLF);
			}
		}
		return buffer.toString();
	}

	public int getRowIndex(ExcelRow row) {
		int index = 0;
		for (; index < this.size(); index++) {
			if (this.get(index) == row) {
				return index;
			}
		}
		return -1;
	}

	public ExcelRow getLast() {
		if (size() == 0) {
			return null;
		}
		return get(this.size() - 1);
	}
}
