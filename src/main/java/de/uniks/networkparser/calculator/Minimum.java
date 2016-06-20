package de.uniks.networkparser.calculator;

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

public class Minimum implements Operator {
	@Override
	public int getPriority() {
		return RegCalculator.FUNCTION;
	}

	@Override
	public double calculate(Double... values) {
		if(values == null) {
			return Double.MAX_VALUE;
		}
		double min=Double.MAX_VALUE;
		for(int i=0;i<values.length;i++) {
			if(values[i] != null && values[i] < min) {
				min = values[i];
			}
		}
		return min;
	}

	@Override
	public String getTag() {
		return "min";
	}

	@Override
	public int getValues() {
		return 2;
	}
}
