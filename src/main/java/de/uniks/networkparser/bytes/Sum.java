package de.uniks.networkparser.bytes;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class Sum extends Checksum {
	private boolean bsd;
	private int order;

	public Sum enableBSD() {
		order=16;
		bsd = true;
		return this;
	}

	public Sum withOrder(int order) {
		this.bsd = false;
		if(order == 8 || order == 16 || order == 24 || order == 32) {
			this.order = order;
		} else {
			this.order = 0;
		}
		return this;
	}

	@Override
	public boolean update(int data) {
		super.update(data);
		if(bsd) {
			value = (value >> 1) + ((value & 1) << 15);
			value += data & 0xFF;
			value &= 0xffff;
		} else {
			value += data & 0xFF;
		}
		return true;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
