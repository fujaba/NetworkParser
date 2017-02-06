package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelForm extends Control{

	private IdMap map;
	private Object item;

	public ModelForm withDataBinding(IdMap map, Object entity, boolean addCommandBtn){
		this.map = map;
		this.item = entity;
//		textClazz = (TextItems) map.getCreator(TextItems.class.getName(), true);

		SendableEntityCreator creator = map.getCreatorClass(item);
		if(creator != null){
//			this.setCenter(items);
//			withDataBinding(addCommandBtn, creator.getProperties());
		}
		return this;
	}
}
