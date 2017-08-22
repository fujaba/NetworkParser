package de.uniks.networkparser.ext.petaf.filter;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.network.NodeProxy;
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
