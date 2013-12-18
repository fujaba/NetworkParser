package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.ByteCreator;

public class InstanceOf implements Condition {
	protected ByteCreator value;
	private String clazzName;

	public InstanceOf(ByteCreator creator) {
		this.value = creator;
	}

	public InstanceOf withClass(Class<?> className){
		this.clazzName = className.getName();
		return this;
	}

	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		return entity.getClass().getName().equals(clazzName);
	}
}
