package de.uniks.networkparser.ext.petaf;

import java.util.Collection;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.GUIEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import javafx.scene.Node;
import javafx.scene.Parent;

public class SimpleReplication implements ObjectCondition {
	private Space space;
	private Object creator;
	private Object tag;
	private Object item;
	private ObjectCondition listener;
	
	public static SimpleReplication bind(SendableEntityCreator creator, Object root, String... tag) {
		Object item = ReflectionLoader.call(creator, "createIdMap", "");
		SimpleReplication binder = new SimpleReplication();
		if(item == null || item instanceof IdMap == false) {
			return binder;
		}
		IdMap map = (IdMap) item;
		binder.getSpace().withMap(map);
		
		String myTag = null;
		if(creator instanceof SendableEntityCreatorTag ) {
			myTag = ((SendableEntityCreatorTag) creator).getTag();
		}else if(tag != null && tag.length>0) {
			myTag = tag[0];
		}
		if(myTag != null) {
			binder.binding(creator, (Parent) root, myTag);
		}
		return binder;
	}
	
	public SimpleReplication binding(SendableEntityCreator creator, Parent root, String tag) {
		//Bind all Children to 
		this.creator = creator;
		this.item = creator.getSendableInstance(false);
		this.tag = tag;
		
		Collection<?> items = root.getChildrenUnmodifiable();
		String[] properties = creator.getProperties();
		for(Object child : items) {
			Node n = (Node) child;
			int i=0;
			for(;i<properties.length;i++) {
				if(properties[i] == null) {
					continue;
				}
				if(properties[i].equals(n.getId())) {
					break;
				}
			}
			if(i<properties.length) {
				// Found
				GUIEvent javaFXEvent = new GUIEvent();
				javaFXEvent.withListener(this);
				Object proxy = ReflectionLoader.createProxy(javaFXEvent, ReflectionLoader.EVENTHANDLER);

				ReflectionLoader.call(n, "setOnKeyReleased", ReflectionLoader.EVENTHANDLER, proxy);
				if(n.getId().equals(this.tag)) {
					// It is Primary Key
					Object focusProperty = ReflectionLoader.call(n, "focusedProperty");
					ReflectionLoader.call(focusProperty, "addListener", ReflectionLoader.CHANGELISTENER, proxy);
				}
			}
		}
		
		// Set new UpdateCondition
		return this;
	}
	
	public boolean isValid() {
		if(this.creator == null) {
			return false;
		}
		if(this.tag == null) {
			return false;
		}
		if(getSpace().getMap() == null) {
			return false;
		}
		return true;
	}
	
	public SimpleReplication withMap(IdMap map) {
		getSpace().withMap(map);
		return this;
	}
	
	public Space getSpace() {
		if(space == null) {
			this.space = new Space();
		}
		return space;
	}

	@Override
	public boolean update(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
