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
//	protected Object oldValue;
	private String property;
	private Object entity;
	private ModelThread owner;
	private String id;

	public UpdateModel(ModelThread owner, Object element, String property, Object newValue) {
		this.owner = owner;
		this.entity = element;
		this.property = property;
//		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public UpdateModel(ModelThread owner, String id, String property) {
		this.owner = owner;
		this.id = id;
		this.property = property;
	}
	
	public UpdateModel withId(String id) {
		this.id = id;
		return this;
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
			SendableEntityCreator creator;
			IdMap map = this.owner.getMap();
			Object element;

			if(this.id != null && this.entity == null) {
				element = map.getObject(this.id);
				if(this.property != null) {
					creator = map.getCreatorClass(entity);
					return creator.getValue(element, property);
				}
				return element;
			}

			if(this.entity instanceof String) {
				String className = (String) this.entity;
				creator = map.getCreator(className, true);
				// TEST FOR NEW ONE
				element = creator.getSendableInstance(true);
				if(this.id == null) {
					this.id = map.getId(element, true);
				}
				map.put(this.id, element, false);
				return element;
			} else {
				if(map == null) {
					return null;
				}
				creator = map.getCreatorClass(entity);
				element = this.entity;
			}
			return creator.setValue(element, property, newValue, SendableEntityCreator.NEW);
		}catch(Exception e){
			this.owner.getErrorHandler().saveException(e, false);
		}
		return false;
	}
}
