package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.interfaces.BaseItem;

public class ChangeMessage extends Message {
	private Object entity;
	private Filter filter;
	private Space space;

	@Override
	public ChangeMessage withMessage(BaseItem value) {
		super.withMessage(value);
		return this;
	}
	
	ChangeMessage withEntity(Object value) {
		this.entity = value;
		return this;
	}
	ChangeMessage withFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	public static ChangeMessage create(){
		return  new ChangeMessage();
	}
	
	public static ChangeMessage withChange(Object entity){
		ChangeMessage changeMessage = new ChangeMessage();
		changeMessage.withEntity(entity);
		return changeMessage;
	}

	public static ChangeMessage withChange(Object entity, Filter filter){
		ChangeMessage changeMessage = new ChangeMessage();
		changeMessage.withEntity(entity);
		changeMessage.withFilter(filter);
		return changeMessage;
	}
	
	@Override
	public BaseItem getMessage() {
		if(msg == null && space != null) {
			msg = space.encode(entity, filter);
		}
		return super.getMessage();
	}
	
	protected void initialize(NodeProxyModel modell) {
		if(modell == null) {
			return;
		}
		if (this.space == null) {
			this.space = modell.getSpace();
		}
		if (this.entity == null) {
			this.entity = modell.getModell();
		}
	}
}
