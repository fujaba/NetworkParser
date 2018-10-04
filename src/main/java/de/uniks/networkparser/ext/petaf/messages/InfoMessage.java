package de.uniks.networkparser.ext.petaf.messages;

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
import de.uniks.networkparser.ext.petaf.ModelChange;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

public class InfoMessage extends ReceivingTimerTask {
	public static final String PROPERTY_PROXIES = "proxies";
	public static final String PROPERTY_LASTID = "history_id";

	public InfoMessage() {
		InfoMessage.props.add(PROPERTY_PROXIES, PROPERTY_LASTID);
	}

	public static InfoMessage create(Space space) {
		InfoMessage msg = new InfoMessage();
		msg.withSpace(space);
		return msg;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute == null || entity instanceof AcceptMessage == false) {
			return null;
		}
		AcceptMessage message = (AcceptMessage) entity;
		Space space = message.getSpace();
		if (space != null) {
			if (PROPERTY_PROXIES.equalsIgnoreCase(attribute)) {
				SimpleList<NodeProxy> candidates = new SimpleList<NodeProxy>();
				SortedSet<NodeProxy> nodeProxies = space.getNodeProxies();
				for (NodeProxy proxy : nodeProxies) {
					if (proxy.isSendable()) {
						candidates.add(proxy);
					}
				}
				return candidates;
			}
			if (PROPERTY_LASTID.equalsIgnoreCase(attribute)) {
				ModelChange lastModelChange = space.getHistory().getLastModelChange();
				if (lastModelChange != null) {
					return lastModelChange.getKey();
				}
			}
		}
		return super.getValue(entity, attribute);
	}

//	@Override
//	public BaseItem getMessage() {
//		if(msg == null && space != null) {
//			SortedSet<NodeProxy> proxies = space.getNodeProxies();
//
//			IdMap map = getInternMap(space);
//			Tokener tokener = space.getTokener();
//			EntityList list = tokener.newInstanceList();
//			PetaFilter filter = new PetaFilter().withTyp(PetaFilter.INFO);
//			for(NodeProxy proxy : proxies) {
//				list.add(map.encode(proxy, tokener, filter));
//			}
//			msg = list;
//		}
//		return super.getMessage();
//	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InfoMessage();
	}
}
