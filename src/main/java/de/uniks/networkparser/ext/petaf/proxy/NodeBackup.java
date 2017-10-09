package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class NodeBackup implements Runnable{
	private ObjectCondition task;
	private boolean runnable;
	protected long sendtime;
	private Space space;

	public void enable() {
		this.runnable = true;
	}

	public boolean close() {
		runnable = false;
		return true;
	}
	
	public NodeBackup withSpace(Space space) {
		this.space = space;
		return this;
	}

	public void run() {
		if(task != null && runnable) {
			this.sendtime = System.currentTimeMillis();
			
			task.update(space);
			runnable = false;
		}
	}

	public boolean isEnable() {
		return runnable;
	}
}
