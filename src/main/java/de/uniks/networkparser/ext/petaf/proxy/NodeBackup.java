package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SortedSet;

public class NodeBackup implements Runnable{
	public static final String KEY="Backup";
	private SimpleEventCondition task;
	private boolean runnable;
	protected long sendtime;
	private Space space;
	private SimpleEvent event;

	public void enable() {
		this.runnable = true;
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
		if(task != null && runnable) {
			this.sendtime = System.currentTimeMillis();
			
			task.update(this.event);
			if(this.space != null) {
				SortedSet<NodeProxy> proxies = this.space.getNodeProxies();
				// Add Saving the Datemodell
				for(NodeProxy proxy : proxies ) {
					if(proxy instanceof NodeProxyFileSystem) {
						NodeProxyFileSystem fileSystem = (NodeProxyFileSystem) proxy;
						if(fileSystem.isFullModell()) {
							fileSystem.sending(null);
						}
					}
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
