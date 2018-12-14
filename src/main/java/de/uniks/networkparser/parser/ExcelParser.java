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
import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.Pos;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class ExcelParser {
	public static final String ROW = "row";
	public static final String CELL = "c";
	public static final String CELL_TYPE = "t";
	public static final String REF = "ref";
	public static final String CELL_TYPE_REFERENCE = "s";
	public static final char SEMICOLON = ';';

	public ExcelWorkBook parseSheets(CharSequence stringFile, CharSequence... sheetFile) {
		ExcelWorkBook excelWorkBook = new ExcelWorkBook();
		if (sheetFile == null) {
			return excelWorkBook;
		}
		for (CharSequence sheet : sheetFile) {
			excelWorkBook.add(parseSheet(stringFile, sheet));
		}
		return excelWorkBook;
	}

	public ExcelSheet parseSheet(CharSequence stringFile, CharSequence sheetFile) {
		ExcelSheet data = new ExcelSheet();
		SimpleKeyValueList<String, ExcelCell> cells = new SimpleKeyValueList<String, ExcelCell>();
		SimpleKeyValueList<String, String> mergeCellPos = new SimpleKeyValueList<String, String>();

		IdMap map = new IdMap();
		map.add(new ExcelCell());
		XMLTokener tokener = new XMLTokener().withMap(map);
		tokener.withDefaultFactory(EntityCreator.createXML());
		CharacterBuffer buffer = null;
		if (sheetFile instanceof CharacterBuffer) {
			buffer = (CharacterBuffer) sheetFile;
		} else {
			buffer = new CharacterBuffer().with(sheetFile.toString());
		}

		XMLEntity sheet = (XMLEntity) map.decode(tokener, buffer, map.getFilter());
		XMLEntity sharedStrings = null;
		if (stringFile != null) {
			sharedStrings = (XMLEntity) map.decode(stringFile.toString());
		}

		if (sheet != null) {
			EntityList mergeCells = sheet.getElementsBy(XMLEntity.PROPERTY_TAG, "mergeCells");
			// <mergeCells count="1"><mergeCell ref="A2:A3"/></mergeCells>
			if (mergeCells != null) {
				for (int i = 0; i < mergeCells.sizeChildren(); i++) {
					BaseItem mergeCell = mergeCells.getChild(i);
					if (mergeCell == null) {
						continue;
					}
					SimpleList<Pos> excelRange = EntityUtil.getExcelRange(((Entity) mergeCell).getString(REF));
					for (Pos item : excelRange) {
						if (item == null || item.x < 0 || item.y < 0) {
							continue;
						}
						mergeCellPos.add(item.toString(), excelRange.first().toString());
					}
				}
			}
			EntityList sheetData = sheet.getElementsBy(XMLEntity.PROPERTY_TAG, "sheetData");
			if (sheetData != null) {
//				if (rows != null && rows instanceof XMLEntity) {
				for (int i = 0; i < sheetData.sizeChildren(); i++) {
					BaseItem child = sheetData.getChild(i);
					if (child == null || child instanceof XMLEntity == false) {
						continue;
					}
					XMLEntity row = (XMLEntity) child;
					if (ROW.equalsIgnoreCase(row.getTag()) == false) {
						continue;
					}
					ExcelRow dataRow = new ExcelRow();
					// <c r="A1" t="s"><v>2</v></c>
					for (int c = 0; c < row.size(); c++) {
						BaseItem item = row.getChild(c);
						if (item == null || item instanceof ExcelCell == false) {
							continue;
						}
						ExcelCell cell = (ExcelCell) item;
						if (CELL.equalsIgnoreCase(cell.getTag()) == false) {
							continue;
						}
						ExcelCell excelCell = (ExcelCell) cell;
						if (CELL_TYPE_REFERENCE.equalsIgnoreCase(excelCell.getType())) {
							// <v>2</v>
							EntityList element = cell.getChild(0);
							if (element != null) {
								String ref = ((XMLEntity) element).getValue();
								if (sharedStrings != null) {
									XMLEntity refString = (XMLEntity) sharedStrings.getChild(Integer.valueOf(ref));
									String text = ((XMLEntity) refString.getChild(0)).getValue();
									excelCell.setContent(text);
								}
							}
						} else if (excelCell.sizeChildren() < 1) {
							String pos = mergeCellPos.get(excelCell.getReferenz().toString());
							if (pos != null && cells.contains(pos)) {
								ExcelCell firstCell = cells.get(pos);
								excelCell.setReferenceCell(firstCell);
							}
						}
						cells.add(excelCell.getReferenz().toString(), excelCell);
						dataRow.add(excelCell);
					}
					if (dataRow.size() > 0) {
						data.add(dataRow);
					}
				}
			}
//			}
		}
		return data;
	}

	public CharacterBuffer writeCSV(SimpleList<SimpleList<ExcelCell>> data) {
		CharacterBuffer result = new CharacterBuffer();
		if (data == null) {
			return result;
		}
		for (SimpleList<ExcelCell> row : data) {
			boolean first = true;
			for (ExcelCell cell : row) {
				if (!first) {
					result.with(SEMICOLON);
				}
				result.with(cell.getContentAsString());
				first = false;
			}
			result.with(BaseItem.CRLF);
		}
		return result;
	}
	
	public SimpleList<Object> readCSV(Buffer data, SendableEntityCreator creator) {
 		SimpleList<Object> result = new SimpleList<Object>();
		if(data== null || creator == null) {
			return result;
		}
		SimpleList<String> header = new SimpleList<String>();
		CharacterBuffer line = data.readLine();
		if(line == null || line.length() < 1) {
			return result;
		}
		int start=0;
		for(int i=0;i<line.length();i++) {
			if(line.charAt(i) == SEMICOLON) {
				header.add(line.substring(start, i));
				start=i+1;
			}
		}
		do {
			line = data.readLine();
			int column=0;
			start=0;
			//Parsing data
			Object item = creator.getSendableInstance(false);
			for(int i=0;i<line.length();i++) {
				if(line.charAt(i) == SEMICOLON) {
					String value = line.substring(start, i);
					creator.setValue(item, header.get(column), value, SendableEntityCreator.NEW);
					column++;
					if(column>header.size()) {
						break;
					}
					start=i+1;
				}
			}
			result.add(item);
			if(data.isEnd()) {
				break;
			}
		}while(line != null);
		return result;
	}

	private final static String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n";
	private final static String APP = "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\"><TotalTime>0</TotalTime><Application>NetworkParser</Application><DocSecurity>0</DocSecurity><ScaleCrop>false</ScaleCrop><AppVersion>1.42</AppVersion></Properties>";
	private final static String RELS = "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/><Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/><Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/></Relationships>";

	public SimpleKeyValueList<String, String> createExcelContent(ExcelWorkBook content) {
		int id = 4;
		SimpleKeyValueList<String, String> fileContent = new SimpleKeyValueList<String, String>();
		fileContent.add("[Content_Types].xml", getContentTypes(content));
		fileContent.add("docProps/app.xml", HEADER + APP);
		fileContent.add("_rels/.rels", HEADER + RELS);

		fileContent.add("xl/_rels/workbook.xml.rels", this.getHeaderWorkbookRel(content, id));
		fileContent.add("xl/workbook.xml", this.getHeaderWorkbook(content, id));
		fileContent.add("docProps/core.xml", this.getHeader(content));
		for (int i = 0; i < content.size(); i++) {
			ExcelSheet sheet = content.get(i);
			if (sheet == null) {
				continue;
			}
			fileContent.add("xl/worksheets/sheet" + (i + 1) + ".xml", this.getSheet(sheet, id));
		}
		return fileContent;
	}

	private String getContentTypes(ExcelWorkBook content) {
		CharacterBuffer data = new CharacterBuffer().with(HEADER);
		data.with(
				"<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/><Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>");
		for (int i = 1; i <= content.size(); i++) {
			data.with("<Override PartName=\"/xl/worksheets/sheet" + i
					+ ".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
		}
		data.with("</Types>");
		return data.toString();
	}

	private String getHeaderWorkbook(ExcelWorkBook content, int id) {
		CharacterBuffer data = new CharacterBuffer().with(HEADER);
		data.with(
				"<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">\r\n    <sheets>\r\n");
		for (int i = 0; i < content.size(); i++) {
			ExcelSheet sheet = content.get(i);
			if (sheet == null) {
				continue;
			}
			data.with("        <sheet name=\"");
			if (sheet.getName() == null) {
				data.with("Table" + (i + 1));
			} else {
				data.with(sheet.getName());
			}
			data.with("\" sheetId=\"", "" + (i + 1), "\" r:id=\"rId", "" + (i + id), "\"/>\r\n");
		}
		data.with("    </sheets>\r\n</workbook>");

		return data.toString();
	}

	private String getSheet(ExcelSheet sheet, int id) {
		int rowPos;
		CharacterBuffer data = new CharacterBuffer().with(HEADER);
		data.with(
				"<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">\r\n");
		data.with("<sheetData>\r\n");
		for (rowPos = 0; rowPos <= sheet.size(); rowPos++) {
			ExcelRow row = sheet.get(rowPos);
			if (row == null) {
				continue;
			}
			data.with("  <row r=\"" + row.getRowPos() + "\">");
			for (ExcelCell cell : row) {
				data.with(cell.toString());
			}
			data.with("</row>\r\n");
		}
		data.with("</sheetData>");
		data.with("</worksheet>");
		return data.toString();
	};

	private String getHeaderWorkbookRel(ExcelWorkBook content, int id) {
		CharacterBuffer data = new CharacterBuffer().with(HEADER);
		data.with("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
		for (int i = 0; i < content.size(); i++) {
			data.with("<Relationship Id=\"rId", "" + (id + i),
					"\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet",
					"" + (i + 1), ".xml\"/>");
		}
		data.with("</Relationships>");
		return data.toString();
	}

	private String getHeader(ExcelWorkBook content) {
		if (content.getAuthor() == null) {
			content.withAuthor(System.getProperty("user.name"));
		}
		CharacterBuffer data = new CharacterBuffer().with(HEADER);
		DateTimeEntity date = new DateTimeEntity();
		String string = date.toString("yyyy-mm-dd'T'HZ:MM:SS'Z'");
		data.with(
				"<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		data.with("<dc:creator>", content.getAuthor(), "</dc:creator>");
		data.with("<cp:lastModifiedBy>", content.getAuthor(), "</cp:lastModifiedBy>");
		data.with("<dcterms:created xsi:type=\"dcterms:W3CDTF\">", string, "</dcterms:created>");
		data.with("<dcterms:modified xsi:type=\"dcterms:W3CDTF\">", string, "</dcterms:modified>");
		data.with("</cp:coreProperties>");
		return data.toString();
	};
}
