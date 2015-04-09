package de.uniks.networkparser.gui.javafx.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import de.uniks.networkparser.date.DateTimeEntity;
import de.uniks.networkparser.gui.javafx.Os;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ExcelExporter extends MenuItem implements EventHandler<ActionEvent>{
	private TableComponent tableComponent;

	public ExcelExporter(TableComponent value) {
		super("XLSX");
		this.tableComponent = value;
		setOnAction(this);
	}

	@Override
	public void handle(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Excel File", "*.xlsx"));
		File choice = fileChooser.showSaveDialog(tableComponent.getScene().getWindow());
		if(choice!=null) {
			try {
				if(choice.exists()){
					choice.delete();
				}
				FileOutputStream fos = new FileOutputStream(choice);
				ZipOutputStream zos = new ZipOutputStream(fos);
				
				addToZipFile("[Content_Types].xml", Os.class.getResourceAsStream("excel/ContentTypes.xml"), zos);
				addToZipFile("docProps/app.xml", Os.class.getResourceAsStream("excel/app.xml"), zos);
				addToZipFile("_rels/.rels", Os.class.getResourceAsStream("excel/rels.xml"), zos);
				addToZipFile("xl/_rels/workbook.xml.rels", Os.class.getResourceAsStream("excel/workbook.xml.rels"), zos);
				addToZipFile("xl/workbook.xml", Os.class.getResourceAsStream("excel/workbook.xml"), zos);
				addHeader(zos);
				addData(zos);
				zos.close();
				fos.close();
			} catch (IOException e) {
			}
		}
	}
	
			
	private void addHeader(ZipOutputStream zos) throws IOException {
		ZipEntry zipEntry = new ZipEntry("docProps/core.xml");
		zos.putNextEntry(zipEntry);

		DateTimeEntity entity=new DateTimeEntity();
		String export = entity.toString("yyyy-mm-dd'T'HZ:MM:SS'Z'");
		
		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
		sb.append("<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		sb.append("<dc:creator>"+System.getProperty("user.name")+"</dc:creator>");
		sb.append("<cp:lastModifiedBy>"+System.getProperty("user.name")+"</cp:lastModifiedBy>");
		sb.append("<dcterms:created xsi:type=\"dcterms:W3CDTF\">"+export+"</dcterms:created>");
		sb.append("<dcterms:modified xsi:type=\"dcterms:W3CDTF\">"+export+"</dcterms:modified>");
		sb.append("</cp:coreProperties>");
		byte[] values = sb.toString().getBytes();
		zos.write(values, 0, values.length);
		zos.closeEntry();
	}

	private String convertColumn(int rowCount) {
		StringBuilder sb=new StringBuilder();
		while(rowCount>0) {
			if(rowCount>26){
				int no=rowCount/26;
				sb.append((char)(64+no));
				rowCount -= (no*26);
			}else{
				sb.append((char)(64+rowCount));
				rowCount = 0;
			}
		}
		return  sb.toString();
	}
	
	private String getDataLine(int column, int rowCount, Object data){
		if(data instanceof Number) {
			return "<c r=\""+convertColumn(column)+rowCount+"\"><v>"+data+"</v></c>";
		}
		if(data instanceof Boolean) {
			if((Boolean)data) {
				return "<c r=\""+convertColumn(column)+rowCount+"\" t=\"b\"><v>1</v></c>";
			}
			return "<c r=\""+convertColumn(column)+rowCount+"\" t=\"b\"><v>0</v></c>";
		}
		if(data==null){
			return "<c r=\""+convertColumn(column)+rowCount+"\" t=\"inlineStr\"><is><t></t></is></c>";
		}
		return "<c r=\""+convertColumn(column)+rowCount+"\" t=\"inlineStr\"><is><t>"+data.toString()+"</t></is></c>";
	}
	

	
	private void addData(ZipOutputStream zos) throws IOException {
		ZipEntry zipEntry = new ZipEntry("xl/worksheets/sheet1.xml");
		zos.putNextEntry(zipEntry);
		ArrayList<String> attributes=new ArrayList<String>();
		ArrayList<String> labels=new ArrayList<String>();
		for(Iterator<TableColumnFX> i = tableComponent.getColumnIterator();i.hasNext();) {
			TableColumnFX tableColumn = i.next();
			labels.add(tableColumn.getColumn().getLabelOrAttrName());
			attributes.add(tableColumn.getColumn().getAttrName());
		}
		List<Object> items = tableComponent.getItems();
		
		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" mc:Ignorable=\"x14ac\" xmlns:x14ac=\"http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac\">\n");
		
		sb.append("<dimension ref=\"A1:"+convertColumn(labels.size())+(items.size()+1)+"\" />");
		sb.append("<sheetViews>");
		sb.append("<sheetView tabSelected=\"1\" workbookViewId=\"0\"><selection activeCell=\"A1\" sqref=\"A1\"/></sheetView>");
		sb.append("</sheetViews>");
		sb.append("<sheetFormatPr baseColWidth=\"10\" defaultRowHeight=\"14.4\" x14ac:dyDescent=\"0.3\"/>");
		sb.append("<sheetData>");
		int rowCount=1;
		int column=1;
		
		//header
		sb.append("<row r=\""+rowCount+"\" spans=\"1:"+labels.size()+"\" x14ac:dyDescent=\"0.3\">");
		for(String label : labels) {
			sb.append(getDataLine(column, rowCount, label));
			column++;
		}
		sb.append("</row>");
		
		// data
		rowCount++;
		for(Object item : items) {
			SendableEntityCreator creator = tableComponent.getCreator(item);
			if(creator != null) {
				sb.append("<row r=\""+rowCount+"\">");
				column=1;
				for(String attribute : attributes) {
					Object value = creator.getValue(item, attribute);
					if(value!=null) {
						sb.append(getDataLine(column, rowCount, value));
					}
					column++;
				}
				sb.append("</row>");
				rowCount++;
			}
		}
		sb.append("</sheetData>");
		sb.append("<pageMargins left=\"0.7\" right=\"0.7\" top=\"0.78740157499999996\" bottom=\"0.78740157499999996\" header=\"0.3\" footer=\"0.3\"/>");
		sb.append("</worksheet>");
		byte[] values = sb.toString().getBytes();
		zos.write(values, 0, values.length);
		zos.closeEntry();
	}

	
	public void addToZipFile(String fileName, InputStream fis, ZipOutputStream zos) throws FileNotFoundException, IOException {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.closeEntry();
		fis.close();
	}
}
