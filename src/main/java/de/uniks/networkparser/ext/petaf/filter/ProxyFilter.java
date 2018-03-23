package de.uniks.networkparser.ext.petaf.filter;

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
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class ProxyFilter implements ObjectCondition {
	private SimpleKeyValueList<String, NodeProxy> nodeProxyList = new SimpleKeyValueList<String, NodeProxy>();

	public ProxyFilter(){
	}

	private String getClassName(NodeProxy proxy) {
		if(proxy == null) {
			return null;
		}
		Object reference = proxy.getSendableInstance(true);
		if (reference != null) {
			if (reference instanceof Class<?>) {
				return ((Class<?>)reference).getName();
			} else {
				return reference.getClass().getName();
			}
		}
		return null;
	}

	public ProxyFilter with(NodeProxy... values) {
		if(values == null) {
			return this;
		}
		for(NodeProxy proxy : values) {
			String className = getClassName(proxy);
			if (className != null) {
				this.nodeProxyList.add(className, proxy);
			}
		}
		return this;
	}

	public ProxyFilter without(NodeProxy... values) {
		if(values == null) {
			return this;
		}
		for(NodeProxy proxy : values) {
			String className = getClassName(proxy);
			if (className != null) {
				this.nodeProxyList.remove(className);
			}
		}
		return this;
	}

	/**
	 * Gets the creator classes.
	 *
	 * @param clazz	Clazzname for search
	 * @return return a Creator class for a clazz name
	 */
	public NodeProxy getCreator(String clazz) {
		NodeProxy creator = this.nodeProxyList.get(clazz);
		if (creator != null ) {
			return creator;
		}
		return null;
	}

	@Override
	public boolean update(Object value) {
		SimpleEvent evt = (SimpleEvent) value;
		NodeProxy creator = null;
		if(evt.getModelValue() != null) {
			 creator = getCreator(evt.getModelValue().getClass().getName());
		}
		if(creator == null) {return false;}
		String[] sendableProperties = creator.getUpdateProperties();
		for(String prop : sendableProperties){
			if(prop.equals(evt.getPropertyName())){
				return true;
			}
		}
		return false;
	}
}
