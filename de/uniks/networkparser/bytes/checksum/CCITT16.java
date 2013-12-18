package de.uniks.networkparser.bytes.checksum;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or � as soon they
 will be approved by the European Commission - subsequent
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
/**
 * A class that can be used to compute the CRC-16 of a data stream. This is a
 * 100% Java implementation.
 */

public class CCITT16 extends CRC {
	public CCITT16(){
		value = 0x0000;
	}

	@Override
	public int getPolynom() {
		return 0x1021; // 1000000000000101
	}
	
	public void update(int b) {
		super.update(b);
		
        for (int i = 0; i < 8; i++) {
            boolean bit = ((b   >> (7-i) & 1) == 1);
            boolean c15 = ((value >> 15    & 1) == 1);
            value <<= 1;
            if (c15 ^ bit) value ^= getPolynom();
         }
	}

	@Override
	public boolean isReflect() {
		return true;
	}

	@Override
	public int getOrder() {
		return 16;
	}
}
