package de.uniks.networkparser.ext.petaf;

/*
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
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class UpdateModel implements Callable<Object>, Runnable,Supplier<Object> {
	private Object newValue;
	private String property;
	private Object entity;
	private ModelThread owner;

	public UpdateModel(ModelThread owner, Object element, String property, Object newValue) {
		this.owner = owner;
		this.entity = element;
		this.property = property;
		this.newValue = newValue;
	}

	@Override
	public void run() {
		call();
	}

	@Override
	public Object get() {
		return call();
	}

	@Override
	public Object call() {
		try{
			IdMap map = this.owner.getMap();
			if(map == null || this.entity == null) {
				return null;
			}
			SendableEntityCreator creator;
			Object element;

			if(this.entity instanceof String) {
				String name = (String) this.entity;
				
				// Check if name is ClassName or Id
				element = map.getObject(name);
				if(element != null) {
					if(this.property != null) {
						creator = map.getCreatorClass(element);
						if(creator != null) {
							Object value = creator.getValue(element, property);
							if(newValue == null) {
								return value;
							} else {
								// Its Remove
								return creator.setValue(element, property, newValue, SendableEntityCreator.REMOVE);
							}
						}
					}
					return element;
				}
				creator = map.getCreator(name, true);
				// TEST FOR NEW ONE
				element = creator.getSendableInstance(true);
				String newid;
				if(this.newValue instanceof String) {
					newid = (String) this.newValue; 
				} else {
					newid  = map.getId(element, true);
				}
				map.put(newid, element, false);
				return element;
			} else {
				element = this.entity;
				creator = map.getCreatorClass(element);
			}

			// Switch for Add or Delete
			return creator.setValue(element, property, newValue, SendableEntityCreator.NEW);
		}catch(Exception e){
			this.owner.getErrorHandler().saveException(e, false);
		}
		return false;
	}
}
