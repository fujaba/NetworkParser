package de.uniks.networkparser.calculator;

/*
NetworkParser
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class RegCalculator {
	public static final int LINE = 1;
	public static final int POINT = 2;
	public static final int POTENZ = 3;
	public static final int FUNCTION = 4;
	public static final String BACKETSOPEN = "([{";
	public static final String BACKETSCLOSE = ")]}";
	/** List of Operators */
	private HashMap<String, Operator> operators = new HashMap<String, Operator>();

	/** List of Constants */
	private Map<String, Double> constants = new HashMap<String, Double>();

	public RegCalculator withStandard() {
		withOperator(new Addition());
		withOperator(new Subtract());
		withOperator(new Multiply());
		withOperator(new Division());
		withOperator(new Potenz());
		withOperator(new Mod());
		withOperator(new Minimum());
		withOperator(new Maximum());
		return this;
	}

	public RegCalculator withOperator(Operator value) {
		this.operators.put(value.getTag(), value);
		return this;
	}

	public RegCalculator withConstants(String tag, double value) {
		constants.put(tag, value);
		return this;
	}

	public Double calculate(String formular) {
		Double[] values = calculateFields(formular);
		if (values.length < 1) {
			return null;
		}
		return values[0];
	}

	public Double[] calculateFields(String formular) {
		CharacterBuffer tokener = new CharacterBuffer();
		tokener.with(formular);

		ArrayList<String> parts = new ArrayList<String>();
		int pos;
		if (tokener.getCurrentChar() == '('
				&& tokener.charAt(tokener.length() - 1) == ')') {
			pos = tokener.position();
			String value = tokener.getStringPart('(', ')');
			if (value != null && tokener.position() == tokener.length()) {
				pos++;
				tokener.withStartPosition(pos);
				tokener.withPosition(0);
				tokener.withBufferLength(tokener.length() - 2);
			} else {
				tokener.withPosition(pos);
			}

		}
		Character current = tokener.getCurrentChar();
		boolean defaultMulti = false;
		while (tokener.isEnd() == false) {
			if (current == null) {
				current = tokener.nextClean(defaultMulti);
			}
			if (current == ',') {
				current = null;
				defaultMulti = false;
				continue;
			}

			if ((pos = BACKETSOPEN.indexOf(current)) >= 0) {
				String value = tokener.getStringPart(BACKETSOPEN.charAt(pos),
						BACKETSCLOSE.charAt(pos));
				if (value != null) {
					if (defaultMulti) {
						parts.add("*");
					}
					if (pos > 0) {
						parts.add("(" + value.substring(1, value.length() - 1)
								+ ")");
					} else {
						parts.add(value);
					}
					defaultMulti = true;
					current = null;
					continue;
				}
			}

			StringBuilder sb = new StringBuilder();

			if (Character.isDigit(current) || current == '.') {
				while (Character.isDigit(current) || current == '.') {
					sb.append(current);
					current = tokener.getChar();
				}
				if (defaultMulti) {
					parts.add("*");
				}
				parts.add(sb.toString());
				defaultMulti = true;
				continue;
			}
			if (current != ' ') {
				sb.append(current);
			}
			while (!tokener.isEnd()) {
				if (addOperator(sb.toString(), tokener, parts)) {
					sb = new StringBuilder();
					defaultMulti = false;
					break;
				}
				current = tokener.getChar();
				sb.append(current);
			}
			if (sb.length() > 0) {
				addOperator(sb.toString(), tokener, parts);
				defaultMulti = false;
			}
			current = null;
		}
		if(parts.size()<1) {
			parts.add(""+current);
		}

		// Parsing Funciton & Parsing (
		int z = parts.size() - 1;
		while (z >= 0) {
			pos = parts.get(z).indexOf("(");
			if (pos < 0) {
				// Check for mathematical operators
				if (z > 0) {
					Operator operator = operators.get(parts.get(z - 1));
					if (operator != null && operator.getPriority() == LINE) {
						if (z > 1) {
							// Exist Pre Pre
							Operator preOperator = operators.get(parts
									.get(z - 2));
							if (preOperator == null) {
								z--;
								continue;
							}
						}
						if (operator.getTag().equals("-")) {
							parts.set(z - 1, ""
									+ (Double.valueOf(parts.get(z)) * -1));
						} else {
							parts.set(z - 1, ""
									+ (Double.valueOf(parts.get(z))));
						}
						parts.remove(z);
						z--;
					}

				}
				z--;
				continue;
			}
			if (pos > 0) {
				// Function
				Operator operator = operators.get(parts.get(z).substring(0,
						parts.get(z).indexOf("(")));
				Double[] values = calculateFields(parts.get(z).substring(
						pos + 1, parts.get(z).length() - 1));
				if (operator != null && values.length >= operator.getValues()) {
					parts.set(z, "" + operator.calculate(values));
				}
			}
			parts.set(z, "" + calculate(parts.get(z)));
		}

		// Point and Line Statement
		for (int prio = 3; prio > 0; prio--) {
			for (int i = 0; i < parts.size(); i++) {
				Operator operator = operators.get(parts.get(i));
				if (operator != null && operator.getPriority() == prio) {
					parts.set(
							i - 1,
							""
									+ operator.calculate(new Double[] {
											Double.valueOf(parts.get(i - 1)),
											Double.valueOf(parts.get(i + 1)) }));
					parts.remove(i);
					parts.remove(i);
					i = i - 1;
				}
			}
		}

		Double[] result = new Double[parts.size()];
		for (int i = 0; i < parts.size(); i++) {
			result[i] = Double.valueOf(parts.get(i));
		}
		return result;
	}

	private boolean addOperator(String value, CharacterBuffer tokener,
			ArrayList<String> parts) {
		if (constants.containsKey(value)) {
			// Its constants
			return parts.add("" + constants.get(value));
		} else if (operators.containsKey(value)) {
			if (operators.get(value).getPriority() == FUNCTION) {
				tokener.skip();
				return parts.add(value + tokener.getStringPart('(', ')'));
			}
			return parts.add(value);
		}
		return false;
	}
}
