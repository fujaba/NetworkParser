package de.uniks.networkparser.gui.table;

import javafx.scene.control.TableCell;
import javafx.scene.text.Font;

import org.sdmlib.serialization.gui.Style;
import org.sdmlib.serialization.gui.table.Column;
import org.sdmlib.serialization.gui.table.TableCellValue;

public class TableCellFX extends TableCell<Object, TableCellValue>{
	private Column column;
	private TableComponent tableComponent;

	public TableCellFX withColumn(Column column) {
		this.column = column;
		return this;
	}
	
	@Override
	protected void updateItem(TableCellValue arg0, boolean arg1) {
		super.updateItem(arg0, arg1);
//		System.out.println(getIndex()+": "+getItem()+"-- "+this.itemProperty());
//		System.out.println(this.tableComponent.getItems());
//		System.out.println(this.tableComponent.getItems().get(getIndex()));
//		System.out.println(tableColumn.getCellData(arg0));
//		System.out.println(tableColumn.getCellObservableValue(arg0));
		if(arg0!=null){
			setText(""+arg0);
			
	    	if(this.column.getStyle()!=null){
	    		Style myStyle = this.column.getStyle();
	    		if(myStyle.getFontFamily()!=null && myStyle.getFontSize()!=null){
	    			setFont(new Font(myStyle.getFontFamily(), Integer.valueOf(myStyle.getFontSize())));
	    		}
	    		setStyle(myStyle.toString());
	    	}
		}
	}
	 
	
	
//	@Override
//    public Color getBackground(Object element) {
//    	return colors.getColor(column.getBackgroundColor());
//    }
//    @Override
//    public Color getForeground(Object element) {
//    	return colors.getColor(column.getForgroundColor());
//    }
//	public Color getForgroundColorActiv() {
//		return colors.getColor(column.getForgroundColorActiv());
//	}
//
//	public Color getBackgroundColorActiv() {
//		return colors.getColor(column.getBackgroundColorActiv());
//	}
//	
//	@Override
//	public String getToolTipText(Object element) {
//		String altAttribute = column.getAltAttribute();
//		if (altAttribute != null) {
//			if(altAttribute.startsWith("\"")){
//				return altAttribute.substring(1, altAttribute.length()-1);
//			}
//			SendableEntityCreator creatorClass = owner.getMap().getCreatorClass(element);
//			if (creatorClass != null) {
//				String text = ""
//						+ creatorClass.getValue(element, altAttribute);
//				if (text.equals("")) {
//					return null;
//				}
//				return text;
//			}
//		}
//		return getTextValue(element);
//	}
	
	public Column getColumn(){
		return column;
	}

	public TableCellFX withTableComponent(
			TableComponent tableComponent) {
		this.tableComponent = tableComponent;
		return this;
	}
}
