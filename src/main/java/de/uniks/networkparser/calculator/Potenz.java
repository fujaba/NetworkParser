package de.uniks.networkparser.calculator;

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

public class Potenz implements Operator {
	@Override
	public int getPriority() {
		return RegCalculator.POTENZ;
	}

	@Override
	public double calculate(Double... values) {
		if (values == null || values.length < 2) {
			return 0;
		}
		double result = 0;
		if (values[0] != null) {
			result = values[0];
		}
		if (values[1] < 0) {
			values[1] = values[1] * -1;
			for (int i = 1; i < values[1]; i++) {
				result *= values[0];
			}
			return 1 / result;
		}
		for (int i = 1; i < values[1]; i++) {
			result *= values[0];
		}
		return result;
	}

	@Override
	public String getTag() {
		return "^";
	}

	@Override
	public int getValues() {
		return 2;
	}
}
