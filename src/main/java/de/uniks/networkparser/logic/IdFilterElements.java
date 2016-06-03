package de.uniks.networkparser.logic;

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
import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class IdFilterElements extends SimpleList<Object> implements UpdateListener {
	private UpdateListener condition;

	public IdFilterElements(UpdateListener condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.value(clazzConditon);
	}

	@Override
	public boolean update(Object evt) {
		if(condition!=null) {
			if(condition.update(evt)) {
				return add(((PropertyChangeEvent)evt).getNewValue());
			}
		}
		return false;
	}
}
