package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.UpdateListener;

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
public class UpdateCondition implements UpdateListener{
	@Override
	public boolean update(Object evt) {
		if(evt instanceof SimpleMapEvent) {
			SimpleMapEvent event = (SimpleMapEvent)evt;
			IdMap map = event.getSource();
			return map.getKey(event.getModelItem()) == null && map.getKey(event.getNewValue()) == null;
		}
		return false;
	}
}
