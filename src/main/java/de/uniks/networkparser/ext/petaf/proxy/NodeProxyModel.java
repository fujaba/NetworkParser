package de.uniks.networkparser.ext.petaf.proxy;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;

public class NodeProxyModel extends NodeProxy {
	private Object root;
	private String id;
	private NodeProxyModel nextModel;

	public NodeProxyModel(Object root) {
		this.root = root;
		withType(NodeProxy.TYPE_IN);
	}

	@Override
	public String getKey() {
		if (space == null) {
			return null;
		}
		return getId();
	}

	public String getId() {
		if (this.id != null) {
			return this.id;
		}
		this.id = this.space.getKey(root);
		return id;
	}

	@Override
	public boolean close() {
		return false;
	}

	public Object getModel() {
		return root;
	}

	@Override
	protected boolean initProxy() {
		return true;
	}

	@Override
	public boolean isSendable() {
		return false;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new NodeProxyModel(null);
	}

	@Override
	public NodeProxy initSpace(Space space) {
		super.initSpace(space);

		// serialize model
		IdMap map = space.getMap();
		map.put("root", getModel(), true);

//		Object modell = getModell();
//		BaseItem value = this.space.encode(modell, null);
//		String data = value.toString();

		return this;
	}

	public NodeProxyModel setNextModel(NodeProxyModel model) {
		this.nextModel = model;
		if (model == null) {
			return this;
		}
		model.setNextModel(null);
		return model;
	}

	public NodeProxyModel nextModel() {
		return this.nextModel;
	}
}
