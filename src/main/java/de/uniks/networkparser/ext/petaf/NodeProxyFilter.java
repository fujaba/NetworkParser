package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class NodeProxyFilter extends Filter {
	
	private MessageTyp typ=MessageTyp.UPDATE;
	@Override
	public String[] getProperties(SendableEntityCreator creator) {
		if(creator instanceof NodeProxy) {
			NodeProxy npCreator = (NodeProxy) creator;
			if(typ == MessageTyp.UPDATE) {
				return npCreator.getUpdateProperties();
			} else if(typ == MessageTyp.ATTRIBUTES) {
				return npCreator.getProperties();
			} else if(typ == MessageTyp.INFO) {
				return npCreator.getInfoProperties();
			}
		}
		return super.getProperties(creator);
	}
	
	public NodeProxyFilter withTyp(MessageTyp typ) {
		this.typ = typ;
		return this;
	}
}
