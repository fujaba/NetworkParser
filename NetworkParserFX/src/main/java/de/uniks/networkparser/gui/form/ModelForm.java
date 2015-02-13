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
import javafx.geometry.Insets;
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
import de.uniks.networkparser.gui.table.CellEditorElement.APPLYACTION;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.window.KeyListenerMap;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelForm extends BorderPane{
	protected IdMap map;
	private TextItems textClazz= null;
	private Button saveBtn;
	private Button reloadBtn;
	private Object item;
	private HBox actionComposite;
	private VBox items = new VBox(6);
	private KeyListenerMap listener;

	public VBox getItems(){
		return items;
	}
	
	public ModelForm withValue(IdMap map, Object item){
		this.map = map;
		this.item = item;
		SendableEntityCreator creator = map.getCreatorClass(item);
		if(creator != null){
			this.setCenter(items);
		}
		return this;
	}

	
	public ModelForm withDataBinding(IdMap map, Object item, boolean addCommandBtn){
		this.map = map;
		this.item = item;
		textClazz = (TextItems) map.getCreator(TextItems.class.getName(), true);
		
		SendableEntityCreator creator = map.getCreatorClass(item);
		if(creator != null){
			this.setCenter(items);
			withDataBinding(addCommandBtn, creator.getProperties());
		}
		return this;
	}
	public ModelForm withDataBinding(boolean addCommandBtn, String[] fields){
		double max=0;
		for(String property : fields){
			PropertyComposite propertyComposite = new PropertyComposite().withOwner(this).withListener(listener);
			Column column = propertyComposite.getColumn();
			if(this.textClazz!=null){
				column.withLabel(this.textClazz.getText(property, item, this));
				propertyComposite.withLabelOrientation(GUIPosition.WEST);
			}
			column.withAttrName(property);
			
			propertyComposite.withDataBinding(map, item, column);
			getItems().getChildren().add(propertyComposite);
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
			this.saveBtn = new Button(getText(DefaultTextItems.SAVE));
			this.saveBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					save();						
				}
			});
			this.reloadBtn = new Button(getText(DefaultTextItems.RELOAD));
			this.reloadBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					reload();						
				}
			});

			this.withActionComponente(this.saveBtn, this.reloadBtn);
		}
		return this;
	}
	
	public PropertyComposite getPropertyComponente(String item){
		for(Node child : getItems().getChildren()){
			if(child instanceof PropertyComposite){
				PropertyComposite property = (PropertyComposite) child;
				if(property.getColumn().getAttrName().equalsIgnoreCase(item)){
					return property;
				}
			}
		}
		return null;
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
	
	public void setPreSize() {
		double max = 0;
		for (Iterator<Node> iterator = getItems().getChildren().iterator(); iterator
				.hasNext();) {
			Node node = iterator.next();
			if (node instanceof PropertyComposite) {
				double temp = ((PropertyComposite) node).getLabelWidth();
				if (temp > max) {
					max = temp;
				}
			}
		}
		for (Iterator<Node> iterator = getItems().getChildren().iterator(); iterator
				.hasNext();) {
			Node node = iterator.next();
			if (node instanceof PropertyComposite) {
				((PropertyComposite) node).setLabelLength(max);
			}
		}
	}
	
	public ModelForm withActionComponente(Button... buttons){
		if(buttons==null){
			return this;
		}
		if(this.actionComposite==null){
			this.actionComposite = new HBox();
			this.actionComposite.setAlignment(Pos.BASELINE_RIGHT);
			this.actionComposite.setPadding(new Insets(20, 30, 20, 0));
			this.setBottom(actionComposite);
		}
		
		int count = this.actionComposite.getChildren().size();
		
		
		for(Button btn : buttons){
			if(count>0){
				Label empty = new Label();
				empty.setMinWidth(10);
				this.actionComposite.getChildren().add(empty);
			}
			this.actionComposite.getChildren().add(btn);
			count++;
		}
		return this;
	}

	public ModelForm withListener(KeyListenerMap value) {
		this.listener = value;
		return this;
	}
	
	
	public boolean focusnext() {
		for(Iterator<Node> i = getItems().getChildren().iterator();i.hasNext();){
			Node child  = i.next();
			if(child instanceof PropertyComposite) {
				PropertyComposite item = (PropertyComposite) child;
				if(item.isFocus() && i.hasNext()) {
					child  = i.next();
					((PropertyComposite) child).setFocus(true);
				}
			}
		}
		return false;
	}
	
	public ModelForm withDefaultButton(Button value) {
		value.setDefaultButton(true);
		return this;
	}

	public void apply(APPLYACTION action) {
	}
}
