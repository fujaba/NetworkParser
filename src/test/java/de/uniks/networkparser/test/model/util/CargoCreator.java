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

public class CargoCreator implements SendableEntityCreator {
	private final String[] properties = new String[] { Cargo.PROPERTY_NAME, Cargo.PROPERTY_BANK, Cargo.PROPERTY_BOAT, };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Cargo();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		int pos = attrName.indexOf('.');
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}

		if (Cargo.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return ((Cargo) target).getName();
		}

		if (Cargo.PROPERTY_BANK.equalsIgnoreCase(attribute)) {
			return ((Cargo) target).getBank();
		}

		if (Cargo.PROPERTY_BOAT.equalsIgnoreCase(attribute)) {
			return ((Cargo) target).getBoat();
		}

		return null;
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value, String type) {
		if (REMOVE.equals(type) && value != null) {
			attrName = attrName + type;
		}

		if (Cargo.PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			((Cargo) target).withName((String) value);
			return true;
		}

		if (Cargo.PROPERTY_BANK.equalsIgnoreCase(attrName)) {
			((Cargo) target).setBank((Bank) value);
			return true;
		}

		if (Cargo.PROPERTY_BOAT.equalsIgnoreCase(attrName)) {
			((Cargo) target).setBoat((Boat) value);
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
