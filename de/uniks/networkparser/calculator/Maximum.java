package de.uniks.networkparser.calculator;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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

public class Maximum implements Operator {
	@Override
	public int getPriority() {
		return RegCalculator.FUNCTION;
	}

	@Override
	public double calculate(Double[] values) {
		double max=values[0];
		for(int i=1;i<values.length;i++){
			if(values[i]>max){
				max=values[i];
			}
		}
		return max;
	}

	@Override
	public String getTag() {
		return "max";
	}

	@Override
	public int getValues() {
		return 2;
	}
}
