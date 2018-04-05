/*
   Copyright (c) 2014 zuendorf 
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.ferryman.Bank;
import de.uniks.networkparser.test.model.ferryman.Boat;
import de.uniks.networkparser.test.model.ferryman.Cargo;
import de.uniks.networkparser.test.model.ferryman.River;

public class BoatCreator implements SendableEntityCreator {
	private final String[] properties = new String[] { Boat.PROPERTY_RIVER, Boat.PROPERTY_BANK, Boat.PROPERTY_CARGO, };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Boat();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		int pos = attrName.indexOf('.');
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}

		if (Boat.PROPERTY_RIVER.equalsIgnoreCase(attribute)) {
			return ((Boat) target).getRiver();
		}

		if (Boat.PROPERTY_BANK.equalsIgnoreCase(attribute)) {
			return ((Boat) target).getBank();
		}

		if (Boat.PROPERTY_CARGO.equalsIgnoreCase(attribute)) {
			return ((Boat) target).getCargo();
		}

		return null;
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value, String type) {
		if (REMOVE.equals(type) && value != null) {
			attrName = attrName + type;
		}

		if (Boat.PROPERTY_RIVER.equalsIgnoreCase(attrName)) {
			((Boat) target).setRiver((River) value);
			return true;
		}

		if (Boat.PROPERTY_BANK.equalsIgnoreCase(attrName)) {
			((Boat) target).setBank((Bank) value);
			return true;
		}

		if (Boat.PROPERTY_CARGO.equalsIgnoreCase(attrName)) {
			((Boat) target).setCargo((Cargo) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String sessionID) {
		IdMap map = new IdMap().withSession(sessionID);
		map.with(new RiverCreator());
		map.with(new BoatCreator());
		map.with(new BankCreator());
		map.with(new CargoCreator());
		map.withTimeStamp(1);
		return map;
	}
}
