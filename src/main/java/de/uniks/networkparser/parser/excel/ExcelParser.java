package de.uniks.networkparser.parser;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.gui.Pos;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLSimpleIdMap;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class ExcelParser {
	public final String ROW="row";
	public final String CELL="c";
	public final String CELL_TYPE="t";
	public final String REF="ref";
	public final String CELL_TYPE_REFERENCE="s";
	public final char SEMICOLON=';';

	public SimpleList<SimpleList<ExcelCell>> parse(CharSequence sheetFile, CharSequence stringFile) {
		SimpleList<SimpleList<ExcelCell>> data = new SimpleList<SimpleList<ExcelCell>>();
		SimpleKeyValueList<String, ExcelCell> cells=new SimpleKeyValueList<String, ExcelCell>();
		SimpleKeyValueList<String, String> mergeCellPos=new SimpleKeyValueList<String, String>();

		XMLSimpleIdMap map=new XMLSimpleIdMap();
		map.with(new ExcelCell());
		XMLTokener tokener = new XMLTokener().withBuffer(sheetFile.toString());
		XMLEntity sheet = (XMLEntity) map.decode(tokener, new XMLEntityCreator());
		XMLEntity sharedStrings = null;
		if(stringFile != null) {
			sharedStrings = (XMLEntity) map.decode(stringFile.toString());
		}

		if(sheet != null) {
			XMLEntity mergeCells = sheet.getChild("mergeCells", true);
			//<mergeCells count="1"><mergeCell ref="A2:A3"/></mergeCells>
			if(mergeCells != null) {
				for(XMLEntity mergeCell : mergeCells.getChildren()) {
					SimpleList<Pos> excelRange = EntityUtil.getExcelRange(mergeCell.getString(REF));
					for(Pos item : excelRange) {
						if(item == null || item.x <0 || item.y <0) {
							continue;
						}
						mergeCellPos.add(item.toString(), excelRange.first().toString());
					}
				}
			}
			XMLEntity sheetData = sheet.getChild("sheetData", true);
			for(XMLEntity row : sheetData.getChildren()) {
				if(ROW.equalsIgnoreCase(row.getTag()) == false) {
					continue;
				}
				SimpleList<ExcelCell> dataRow = new SimpleList<ExcelCell>();
				data.add(dataRow);
				// <c r="A1" t="s"><v>2</v></c>
				for(XMLEntity cell : row.getChildren()) {
					if(CELL.equalsIgnoreCase(cell.getTag()) == false) {
						continue;
					}
					if(cell instanceof ExcelCell == false) {
						continue;
					}
					ExcelCell excelCell=(ExcelCell) cell;
					if(CELL_TYPE_REFERENCE.equalsIgnoreCase(excelCell.getType())) {
//						<v>2</v>
						try {
							String ref = cell.getChildren().first().getValueItem();
							if(sharedStrings!= null) {
								XMLEntity refString = (XMLEntity) sharedStrings.getChildren().get(Integer.valueOf(ref));
								String text = refString.getChildren().first().getValueItem();
								excelCell.withValueItem(text);
							}
						}catch (Exception e) {
						}
					}else if(excelCell.getChildrenCount() < 1) {
						String pos = mergeCellPos.get(excelCell.getReferenz().toString());
						if(pos != null && cells.contains(pos)) {
							ExcelCell firstCell = cells.get(pos);
							excelCell.withValueItem(firstCell.getValueItem());
						}
					}
					cells.add(excelCell.getReferenz().toString(), excelCell);
					dataRow.add(excelCell);
				}
			}
		}
		return data;
	}
	public CharacterBuffer writeCSV( SimpleList<SimpleList<ExcelCell>> data) {
		CharacterBuffer result = new CharacterBuffer();
		if(data == null) {
			return result;
		}
		for(SimpleList<ExcelCell> row : data) {
			boolean first=true;
			for(ExcelCell cell : row) {
				if(!first) {
					result.with(SEMICOLON);
				}
				result.with(cell.getValueItem());
				first=false;
			}
			result.with(BaseItem.CRLF);
		}
		return result;
	}
}
