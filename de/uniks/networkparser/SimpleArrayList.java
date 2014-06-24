package de.uniks.networkparser;

public class SimpleArrayList<V> extends AbstractList<V>{

	@Override
	public AbstractList<V> getNewInstance() {
		return new SimpleArrayList<V>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public AbstractList<V> with(Object... values) {
		if(values==null){
			return this;
		}
		for(Object item : values){
			this.add((V)item);
		}
		return this;
	}

}
