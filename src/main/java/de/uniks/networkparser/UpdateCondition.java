package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/*
NetworkParser
Copyright (c) 2011 - 2015, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.
*/

/**
 * Condition for Listener for changes in Element (Datamodel) in IdMap
 * Or AtomarCondition with PropertyChange
 * @author Stefan Lindel
 */
public class UpdateCondition implements ObjectCondition {
	private SendableEntityCreator creator;
	private String property;
	private Object owner;
	private ObjectCondition filter;
	
	public UpdateCondition withAtomarListener(ObjectCondition listener) {
		this.filter = listener;
		return this;
	}
	@Override
	public boolean update(Object evt) {
		if(evt instanceof SimpleEvent) {
			SimpleEvent event = (SimpleEvent)evt;
			if(creator != null && property != null) {
				if(event.getNewValue() != null) {
					// CREATE ONE
					creator.setValue(event.getNewValue(), property, owner, SendableEntityCreator.NEW);
				}else {
					creator.setValue(event.getOldValue(), property, owner, SendableEntityCreator.REMOVE);
				}
				return false;
			}
			IdMap map = (IdMap) event.getSource();
			return map.getKey(event.getModelValue()) == null && map.getKey(event.getNewValue()) == null;
		}
		if(evt instanceof PropertyChangeEvent ) {
			return filter.update(evt);
		}
		return false;
	}
	
	public static UpdateCondition create(SendableEntityCreator creator, Object owner, String property) {
		UpdateCondition collectionUpdater = new UpdateCondition();
		collectionUpdater.creator = creator;
		collectionUpdater.property = property;
		collectionUpdater.owner = owner;
		return collectionUpdater;
	}
}
