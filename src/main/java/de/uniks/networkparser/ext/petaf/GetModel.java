package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.function.Supplier;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * GetModel from NodeProxy
 * @author Stefan Lindel
 */
public class GetModel implements Supplier<Object> {
	private String property;
	private Object entity;
	private ModelThread owner;

	public GetModel(ModelThread owner, Object entity, String property) {
		this.owner = owner;
		this.property = property;
	}

	@Override
	public Object get() {
		try {
			IdMap map = this.owner.getMap();
			if (this.entity instanceof String) {
				Object element = map.getObject((String) this.entity);
				if (this.property != null) {
					SendableEntityCreator creator = map.getCreatorClass(entity);
					return creator.getValue(element, property);
				}
				return element;
			}
			if (map == null) {
				return null;
			}
			SendableEntityCreator creator = map.getCreatorClass(entity);
			return creator.getValue(entity, property);
		} catch (Exception e) {
			this.owner.getErrorHandler().saveException(e, false);
		}
		return false;
	}

}
