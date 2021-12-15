package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class Bundle extends GraphEntity {
	private SimpleSet<String> all_implements;
	private SimpleSet<String> all_services;

	@Override
	public Bundle with(String name)
	{
		super.with(name);
		return this;
	}
	
	public Bundle withImplements(String... values) {
		if(this.all_implements == null) {
			this.all_implements = new SimpleSet<String>((Object[])values);
		}
		return this;
	}
	
	public String getBundleName() {
		return name;
	}
	
	public Bundle withSubBundle(Bundle bundle)
	{
		super.withChildren(bundle);
		return this;
	}
	
	public SimpleSet<Bundle> getSubBundles() {
		SimpleSet<Bundle> collection = new SimpleSet<Bundle>();
		if (this.children == null) {
			return collection;
		}
		if (this.children instanceof GraphEntity) {
			collection.add((GraphEntity) this.children);
			return collection;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Bundle) {
					collection.add((Bundle) item);
				}
			}
		}
		return collection;
	}
}
