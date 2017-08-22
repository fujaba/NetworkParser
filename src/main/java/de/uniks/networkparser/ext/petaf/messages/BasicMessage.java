package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.network.Message;
import de.uniks.networkparser.ext.petaf.network.MessageTyp;
import de.uniks.networkparser.ext.petaf.network.NodeProxy;

public class BasicMessage extends Message{
	public MessageTyp type=MessageTyp.UPDATE;
	
	
	public String getMessageId(NodeProxy proxy){
	    return String.format(String.format("%%0%dd", 20), 0)+"!"+proxy.getKey();
	}
}
