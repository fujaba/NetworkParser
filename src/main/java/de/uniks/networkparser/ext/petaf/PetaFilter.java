package de.uniks.networkparser.ext.petaf;

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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PetaFilter extends Filter {
	public static final String UPDATE = "update"; // UPDATE only send basic info
	public static final String ID = "id"; // UPDATE only send basic info
	public static final String ATTRIBUTES = "attributes"; // ATTRIBUTES send all Attributes of Remote Proxy
	public static final String INFO = "info"; // Info send more Infos

	private String typ = UPDATE;
	private String oldTyp;

	public PetaFilter() {
		withFormat(FORMAT_SHORTCLASS);
	}

	@Override
	public void convertProperty(Object entity, String fullProp) {
		super.convertProperty(entity, fullProp);

		if (InfoMessage.PROPERTY_PROXIES.equals(fullProp) && entity instanceof InfoMessage) {
			this.oldTyp = this.typ;
			this.typ = INFO;
		} else if (ConnectMessage.PROPERTY_RECEIVED.equals(fullProp) && entity instanceof ConnectMessage) {
			this.oldTyp = this.typ;
			this.typ = INFO;
		} else if (this.oldTyp != null) {
			this.typ = this.oldTyp;
			this.oldTyp = null;
		}
	}

	@Override
	public String[] getProperties(SendableEntityCreator creator) {
		if (creator instanceof NodeProxy) {
			NodeProxy npCreator = (NodeProxy) creator;
			if (UPDATE.equalsIgnoreCase(typ)) {
				return npCreator.getUpdateProperties();
			} else if (ATTRIBUTES.equalsIgnoreCase(typ)) {
				return npCreator.getProperties();
			} else if (INFO.equalsIgnoreCase(typ)) {
				return npCreator.getInfoProperties();
			} else if (ID.equalsIgnoreCase(typ)) {
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
