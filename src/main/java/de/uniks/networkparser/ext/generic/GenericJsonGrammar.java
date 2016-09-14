package de.uniks.networkparser.ext.generic;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class GenericJsonGrammar extends SimpleGrammar {
	@Override
	public SendableEntityCreator getSuperCreator(IdMap map, boolean searchForSuperCreator, Object modelItem) {
		if(modelItem == null && !searchForSuperCreator) {
			return null;
		}
		Class<?> search;
		if(modelItem instanceof Class<?>) {
			search = (Class<?>) modelItem;
		}else if(modelItem != null) {
			search = modelItem.getClass();
		} else {
			return null;
		}
		for(Iterator<SendableEntityCreator> i =map.iterator();i.hasNext();){
			SendableEntityCreator item = i.next();
			Object prototyp = item.getSendableInstance(true);
			if(prototyp instanceof Class<?>) {
				if(((Class<?>)prototyp).isAssignableFrom(search)){
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		Object entity = creator.getSendableInstance(prototype);
		if(entity instanceof Class<?> == false || className == null){
			return entity;
		}
		try {
			Class<?> forName = Class.forName(className);
			return forName.newInstance();
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	@Override
	protected Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
