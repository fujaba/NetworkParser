package de.uniks.networkparser.list;
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

import de.uniks.networkparser.buffer.CharacterBuffer;

/**
 * Class for List of Numbers.
 *
 * @author Stefan Lindel NumberList for List of Numbers (Integer Double etc.)
 */
public class NumberList extends SimpleList<Number> {
	
	/**
	 * Sum.
	 *
	 * @return the double
	 */
	public double sum() {
		double result = 0;

		for (Number x : this) {
			result += x.doubleValue();
		}

		return result;
	}

	/**
	 * Max.
	 *
	 * @return the double
	 */
	public double max() {
		double max = Double.MIN_VALUE;

		for (Number x : this) {
			if (x.doubleValue() > max) {
				max = x.doubleValue();
			}
		}

		return max;
	}

	/**
	 * Min.
	 *
	 * @return the double
	 */
	public double min() {
		double min = Double.MAX_VALUE;

		for (Number x : this) {
			if (x.doubleValue() < min) {
				min = x.doubleValue();
			}
		}
		return min;
	}

	/**
	 * Middle.
	 *
	 * @return the double
	 */
	public double middle() {
		double middleValue = 0;
		for (Number x : this) {
			middleValue += x.doubleValue();
		}
		if (this.size() > 0) {
			middleValue = middleValue / this.size();
		}
		return middleValue;
	}

	/**
	 * To string.
	 *
	 * @param seperator the seperator
	 * @return the string
	 */
	public String toString(String seperator) {
		if (this.size < 1) {
			return "";
		}
		CharacterBuffer buf = new CharacterBuffer();
		buf.with(get(0).toString());
		for (int i = 1; i < this.size; i++) {
			buf.with(seperator);
			buf.with(get(i).toString());
		}
		return buf.toString();
	}
}
