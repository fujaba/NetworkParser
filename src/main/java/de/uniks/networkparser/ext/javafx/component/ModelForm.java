package de.uniks.networkparser.ext.javafx.component;
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
import de.uniks.networkparser.ext.javafx.window.KeyListenerMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.CellEditorElement.APPLYACTION;
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
			Node child = i.next();
			if(child instanceof PropertyComposite) {
				PropertyComposite item = (PropertyComposite) child;
				if(item.isFocus() && i.hasNext()) {
					child = i.next();
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
