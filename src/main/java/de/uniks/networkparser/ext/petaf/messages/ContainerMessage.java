package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.interfaces.BaseItem;

public class ContainerMessage extends Message {
	private BaseItem item;
	
	public ContainerMessage withItem(BaseItem item) {
		this.item = item;
		return this;
	}
	
	@Override
	public BaseItem getMessage() {
		return this.item;
	}

}
