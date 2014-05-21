package de.uniks.networkparser.gui.form;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import de.uniks.networkparser.DefaultTextItems;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelForm extends BorderPane{
	private IdMap map;
	private TextItems textClazz= null;
	private Button saveBtn;
	private Button reloadBtn;
	private Object item;
	private HBox actionComposite;
	private VBox items = new VBox();
	

	public VBox getItems(){
		return items;
	}
	
	public ModelForm withDataBinding(IdMap map, Object item, boolean addCommandBtn){
		this.map = map;
		this.item = item;
		textClazz = (TextItems) map.getCreator(TextItems.class.getName(), true);
		
		SendableEntityCreator creator = map.getCreatorClass(item);
		if(creator != null){
			this.setCenter(items);
			
			double max=0;
			for(String property : creator.getProperties()){
				PropertyComposite propertyComposite = new PropertyComposite();
				Column column = propertyComposite.getColumn();
				if(this.textClazz!=null){
					column.withLabel(this.textClazz.getText(property, item, this));
					propertyComposite.withLabelOrientation(GUIPosition.WEST);
				}
				column.withAttrName(property);
				
				propertyComposite.withDataBinding(map, item, column);
				getItems().getChildren().add(propertyComposite);

//				double temp = propertyComposite.getLabelControl();
				double temp = propertyComposite.getLabelWidth();
				if(temp>max){
					max = temp; 
				}
			}
			for(Iterator<Node> iterator = getItems().getChildren().iterator();iterator.hasNext();){
				Node node = iterator.next();
				if(node instanceof PropertyComposite) {
					((PropertyComposite)node).setLabelLength(max);
				}
			}
			
			if(addCommandBtn){
				this.actionComposite = new HBox();
				this.saveBtn = new Button();
				this.actionComposite.getChildren().add(saveBtn);
				this.saveBtn.setText(getText(DefaultTextItems.SAVE));
				this.saveBtn.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						save();						
					}
				});
				this.reloadBtn = new Button();
				this.reloadBtn.setText(getText(DefaultTextItems.RELOAD));
				Label empty = new Label();
				empty.setMinWidth(10);
				this.actionComposite.getChildren().add(empty);
				this.actionComposite.getChildren().add(reloadBtn);
				this.actionComposite.setAlignment(Pos.BASELINE_RIGHT);
				this.reloadBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						reload();						
					}
				});
				
				this.setBottom(actionComposite);
			}
		}
		return this;
	}
	
	private String getText(String label){
		if(this.textClazz!=null){
			return this.textClazz.getText(label, item, this);
		}
		return label;
	}

	public void save(){
		for(Iterator<Node> iterator = getItems().getChildren().iterator();iterator.hasNext();){
			Node node = iterator.next();
			if(node instanceof PropertyComposite) {
				((PropertyComposite)node).save();
			}
		}
	}
	public void reload(){
		for(Iterator<Node> iterator = getItems().getChildren().iterator();iterator.hasNext();){
			Node node = iterator.next();
			if(node instanceof PropertyComposite) {
				((PropertyComposite)node).reload();
			}
		}
	}
	
	
	public void setPreSize(){
	double max=0;
	for(Iterator<Node> iterator = getItems().getChildren().iterator();iterator.hasNext();){
		Node node = iterator.next();
		if(node instanceof PropertyComposite) {
			double temp = ((PropertyComposite)node).getLabelWidth();
			if(temp>max){
				max = temp; 
			}
		}
	}
	for(Iterator<Node> iterator = getItems().getChildren().iterator();iterator.hasNext();){
		Node node = iterator.next();
		if(node instanceof PropertyComposite) {
			((PropertyComposite)node).setLabelLength(max);
		}
	}
}
	
	
	
	
	
	
	
	
	//FIXME
//	private LinkedHashSet<PropertyComposite> properties=new LinkedHashSet<PropertyComposite>();
//	private PropertyComposite currentFocus;
//	private KeyListener keyListener;
//
//	public ModelForm(Composite parent, int style) {
//		super(parent, style);
//		
//		setLayout(new RowLayout(SWT.VERTICAL));
//	}
//	
//	public void setPreSize(){
//		int max=0;
//		
//		for(Iterator<PropertyComposite> iterator = properties.iterator();iterator.hasNext();){
//			int temp = iterator.next().getLabelLength();
//			if(temp>max){
//				max = temp; 
//			}
//		}
//		for(Iterator<PropertyComposite> iterator = properties.iterator();iterator.hasNext();){
//			iterator.next().setLabelLength(max);
//		}
//	}

//	public void finishDataBinding(){
//		for(Iterator<PropertyComposite> iterator = properties.iterator();iterator.hasNext();){
//			iterator.next().setDataBinding(map, item);
//		}	
//	}
//	
//	public void dispose(){
//		for(Iterator<PropertyComposite> iterator = properties.iterator();iterator.hasNext();){
//			iterator.next().dispose();
//		}	
//	}
//	
//	
//	

//
//	public IdMap getMap() {
//		return map;
//	}
//	
//	public Object getItem() {
//		return item;
//	}
//
//	public TextItems getTextClazz() {
//		return textClazz;
//	}
//	
//	public void addProperty(PropertyComposite propertyComposite){
//		this.properties.add(propertyComposite);
//	}
//	
//	public PropertyComposite getProperty(String property){
//		for(Iterator<PropertyComposite> iterator = properties.iterator();iterator.hasNext();){
//			PropertyComposite item = iterator.next();
//			if(item.getProperty()!=null && item.getProperty().equals(property)){
//				return item;
//			}
//		}
//		return null;
//	}
//
//	public boolean focusnext() {
//		if(currentFocus!=null){
//			Iterator<PropertyComposite> iterator = properties.iterator();
//			while(iterator.hasNext()){
//				if(iterator.next()==currentFocus){
//					break;
//				}
//			}
//			if(iterator.hasNext()){
//				return iterator.next().setFocus();
//			}
//			currentFocus = null;
//		}
//		return false;
//	}
//
//	public void onFocus(PropertyComposite propertyComposite) {
//		this.currentFocus=propertyComposite;
//	}
//	
//	public void onFocusLost(PropertyComposite propertyComposite) {
//		if(this.currentFocus==propertyComposite){
//			this.currentFocus=null;
//		}
//	}
//
//	public void onKeyPressed(KeyEvent event) {
//		if(keyListener!=null){
//			keyListener.keyPressed(event);
//		}
//	}
//	public void onKeyReleased(KeyEvent event){
//		if(event.keyCode == SWT.CR && event.stateMask == 0){
//			// ENTER
//			focusnext();
//		}else if(event.keyCode == SWT.ESC && event.stateMask == 0){
//			// EXIT
//		}
//		if(keyListener!=null){
//			keyListener.keyReleased(event);
//		}
//	}
//	public void onKeyTraversed(KeyEvent event){
//		if(event.keyCode == SWT.TAB && event.stateMask == 0){
//			// TAB
//			event.doit=false;
//			System.out.println(event.time);
//			focusnext();
//		}	
//	}
//	@Override
//	public void addKeyListener(KeyListener listener) {
//		super.addKeyListener(listener);
//		this.keyListener = listener;
//	}
}
