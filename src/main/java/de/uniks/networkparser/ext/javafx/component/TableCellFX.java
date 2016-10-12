package de.uniks.networkparser.ext.javafx.component;

import de.uniks.networkparser.Style;
import de.uniks.networkparser.ext.javafx.StyleFX;
import de.uniks.networkparser.ext.javafx.controls.EditControl;
import de.uniks.networkparser.ext.javafx.controls.EditFieldMap;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class TableCellFX extends TableCell<Object, TableCellValue> implements CellEditorElement, EventHandler<MouseEvent> {
	private EditFieldMap fieldMap;
	private Column field;
	private EditControl<? extends Node> control;
	private TableComponent tableComponent;
	private UpdateItemCell updateListener;

	public TableCellFX withEditFieldMap(EditFieldMap fieldMap) {
		this.fieldMap = fieldMap;
		return this;
	}

	@Override
	public TableCellFX withColumn(Column column) {
		this.field = column;
		Style style = this.field.getStyle();
		if(style != null) {
			if(this.field.getStyle().getName() != null) {
				this.getStyleClass().add(this.field.getStyle().getName());
			}else if(this.field.getStyle() instanceof StyleFX){
				this.setStyle(this.field.getStyle().toString());
			}
		}
		if(this.field.isListener()){
			this.setCursor(Cursor.HAND);
		}

		if(field.getStyle()!= null && field.getStyle().getAlignment() != null) {
			GUIPosition alignment = GUIPosition.valueOf( field.getStyle().getAlignment() );
			if(alignment != null) {
				if(alignment==GUIPosition.CENTER) {
					this.setAlignment(Pos.CENTER);
				}else if(alignment==GUIPosition.WEST) {
					this.setAlignment(Pos.CENTER_LEFT);
				}else if(alignment==GUIPosition.EAST) {
					this.setAlignment(Pos.CENTER_RIGHT);
				}
			}
		}
		this.setOnMouseClicked( this);

		return this;
	}

	@Override
	protected void updateItem(TableCellValue item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
			return;
		}
		if(this.updateListener != null && this.updateListener.updateItem(this, item, empty)) {
			return;
		}
		if(item != null && item.getColumn().isListener()) {
			setText(null);
			Button cellButton = new Button();
			String simpleValue = item.getColumn().getAttrName();
			if(simpleValue!= null) {
				cellButton.setText(simpleValue);
			}
			Object entity= item.getItem();
			SendableEntityCreator creator = item.getCreator();
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					item.getColumn().getListener().onAction(entity, creator, 0, 0);
				};
			});
			setGraphic(cellButton);
			return;
		}
		
		final String text = "" + item;
		if (item != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					setText(text);
				}
			});

			if (this.field.getStyle() != null) {
				Style myStyle = this.field.getStyle();
				if (myStyle.getFontFamily() != null
						&& myStyle.getFontSize() != null) {
					setFont(new Font(myStyle.getFontFamily(),
							Integer.valueOf(myStyle.getFontSize())));
				}
				String style = myStyle.toString();
				if(style.startsWith("-")){
					setStyle(style);
				}
			}
		}else if(empty){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					setText("");
				}
			});
		}
	}

//	public boolean isOnAction() {
//		TablePosition<Object, ?> editingCell = getTableView().getEditingCell();
//		int row = editingCell.getRow();
//		Object entity = tableComponent.getElement(row);
//		SendableEntityCreator creator = tableComponent.getCreator(entity);
//
//		return this.field.getListener().onAction(entity, creator, 2, getTableView().getLayoutX(), getTableView().getLayoutY());
//	}

	@Override
	public void startEdit() {
		if(isEditable()) {
			super.startEdit();
			Object value = getItem().getCreator().getValue(getItem().getItem(), this.field.getAttrName());
			FieldTyp typ = fieldMap.getControllForTyp(field, value);
			control = fieldMap.getControl(typ, field, value, this);
			if(control!=null){
				setText(null);
				setGraphic(control.getControl());
			}
		}
	}

	@Override
	public void commitEdit(TableCellValue arg0) {
		super.commitEdit(arg0);
		apply(APPLYACTION.SAVE);
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		cancel();
	}

	@Override
	public void cancel() {
		setText(""+getItem());
		setGraphic(null);
	}

	@Override
	public void apply(APPLYACTION action) {
		Object value = control.getValue(false);
		getItem().getColumn().setValue(this, getItem().getItem(), getItem().getCreator(), value);
		setText(""+value);
		setGraphic(null);
	}

	@Override
	public Object getValue(boolean convert) {
		return control.getValue(convert);
	}

	@Override
	public TableCellFX withValue(Object value) {
		control.withValue(value);
		return this;
	}

	public TableCellFX withTableComponent(TableComponent value) {
		this.tableComponent = value;
		return this;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean setFocus(boolean value) {
		return true;
	}

	@Override
	public void handle(MouseEvent event) {
		if (this.field != null && this.field.isListener()) {
			TablePosition<?, ?> editingCell = getTableView().getFocusModel().getFocusedCell();
			int row = editingCell.getRow();
			Object entity = tableComponent.getElement(row);
			SendableEntityCreator creator = tableComponent.getCreator(entity);

			this.field.getListener().onAction(entity, creator, getTableView().getLayoutX(), getTableView().getLayoutY());
		}
	}

	@Override
	public boolean onActive(boolean value) {
		return false;
	}

	@Override
	public boolean nextFocus() {
		return false;
	}

	public TableCell<Object, TableCellValue> withUpdateListener(UpdateItemCell updateListener) {
		this.updateListener = updateListener;
		return this;
	}

	// @Override
		// public String getToolTipText(Object element) {
		// String altAttribute = column.getAltAttribute();
		// if (altAttribute != null) {
		// if(altAttribute.startsWith("\"")){
		// return altAttribute.substring(1, altAttribute.length()-1);
		// }
		// SendableEntityCreator creatorClass =
		// owner.getMap().getCreatorClass(element);
		// if (creatorClass != null) {
		// String text = ""
		// + creatorClass.getValue(element, altAttribute);
		// if (text.equals("")) {
		// return null;
		// }
		// return text;
		// }
		// }
		// return getTextValue(element);
		// }

}
