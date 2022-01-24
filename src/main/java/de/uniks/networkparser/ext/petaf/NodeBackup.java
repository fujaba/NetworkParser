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
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyFileSystem;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

/**
 * Node for Backup Modell
 * @author Stefan Lindel
 */
public class NodeBackup implements Runnable {
	public static final String KEY = "Backup";
	private SimpleEventCondition task;
	private boolean runnable;
	protected long sendtime;
	private Space space;
	private SimpleEvent event;
	private SimpleList<NodeProxy> queries;

	public void enable() {
		this.runnable = true;
	}

	public NodeBackup with(NodeProxy... nodeProxies) {
		if (nodeProxies == null) {
			return this;
		}
		if (this.queries == null) {
			this.queries = new SimpleList<NodeProxy>();
		}
		for (NodeProxy proxy : nodeProxies) {
			this.queries.add(proxy);
		}
		return this;
	}

	public boolean close() {
		runnable = false;
		return true;
	}

	public NodeBackup withSpace(Space space) {
		this.space = space;
		this.event = new SimpleEvent(this, KEY, null, space);
		return this;
	}

	public void run() {
		if (task != null && runnable) {
			this.sendtime = System.currentTimeMillis();

			task.update(this.event);
			if (this.space != null) {
				SortedSet<NodeProxy> proxies = this.space.getNodeProxies();
				/* Add Saving the Datemodell */
				for (NodeProxy proxy : proxies) {
					if (proxy instanceof NodeProxyFileSystem) {
						proxy.sending(null);
					}
				}
			}
			if (this.queries != null) {
				for (NodeProxy proxy : this.queries) {
					proxy.sending(null);
				}

			}
			runnable = false;
		}
	}

	public boolean isEnable() {
		return runnable;
	}

	public long getSendtime() {
		return sendtime;
	}

	public NodeBackup withTask(SimpleEventCondition task) {
		this.task = task;
		return this;
	}

	public Space getSpace() {
		return space;
	}
}
