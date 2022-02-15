package de.uniks.networkparser.calculator;

import de.uniks.networkparser.interfaces.MathOperator;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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

/**
 * The Class Maximum.
 *
 * @author Stefan
 */
public class Maximum implements MathOperator {
	
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public int getPriority() {
		return RegCalculator.FUNCTION;
	}

	/**
	 * Calculate.
	 *
	 * @param values the values
	 * @return the double
	 */
	@Override
	public double calculate(Double... values) {
		if (values == null) {
			return Double.MIN_VALUE;
		}
		double max = Double.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null && values[i] > max) {
				max = values[i];
			}
		}
		return max;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return "max";
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	@Override
	public int getValues() {
		return 2;
	}
}
