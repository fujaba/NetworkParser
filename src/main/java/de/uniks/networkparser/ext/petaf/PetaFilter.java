package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PetaFilter extends Filter {
	public static final String UPDATE="update"; // UPDATE only send basic info
	public static final String ID="id"; // UPDATE only send basic info
	public static final String ATTRIBUTES = "attributes"; // ATTRIBUTES send all Attributes of Remote Proxy
	public static final String INFO="info"; // Info send more Infos
	
	private String typ=UPDATE;
	private String oldTyp;
	
	public PetaFilter() {
		withFormat(FORMAT_NULL);
	}
	
	@Override
	public void convertProperty(Object entity, String fullProp) {
		super.convertProperty(entity, fullProp);
		
		if(entity instanceof ConnectMessage && "receiver".equals(fullProp)) {
			this.oldTyp = this.typ;
			this.typ = INFO;
		}else if(this.oldTyp != null) {
			this.typ = this.oldTyp;
			this.oldTyp = null;
		}
	}
	
	@Override
	public String[] getProperties(SendableEntityCreator creator) {
		if(creator instanceof NodeProxy) {
			NodeProxy npCreator = (NodeProxy) creator;
			if(UPDATE.equalsIgnoreCase(typ)) {
				return npCreator.getUpdateProperties();
			} else if(ATTRIBUTES.equalsIgnoreCase(typ)) {
				return npCreator.getProperties();
			} else if(INFO.equalsIgnoreCase(typ)) {
				return npCreator.getInfoProperties();
			} else if(ID.equalsIgnoreCase(typ)) {
				return npCreator.getIDProperties();
			}
		}
		return super.getProperties(creator);
	}
	
	public PetaFilter withTyp(String typ) {
		this.typ = typ;
		return this;
	}
}
