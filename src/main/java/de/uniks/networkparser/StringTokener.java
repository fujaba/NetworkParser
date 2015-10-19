package de.uniks.networkparser;

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
import java.util.ArrayList;

import de.uniks.networkparser.interfaces.BufferedBuffer;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class StringTokener extends Tokener {
	private boolean isString = true;
	private int startToken = -1;

	@Override
	public String nextString(boolean allowCRLF, boolean allowQuote,
			boolean mustQuote, boolean nextStep, char... quotes) {

		if (isMatchChar('"', quotes)) {
			if (isMatchChar(getCurrentChar(), quotes)) {
				isString = true;
			} else {
				isString = !isString;
			}
		} else if (getCurrentChar() == '"') {
			isString = true;
			String sub = "";
			StringBuilder sb = new StringBuilder();
			for (;;) {
				sub = super.nextString(allowCRLF, allowQuote, mustQuote,
						nextStep, quotes);
				sb.append(sub);
				if (sub.length() > 0 && !sub.endsWith("\"")) {
					sb.append(",");
				} else {
					break;
				}
			}
			return sb.toString();
		}
		return super.nextString(allowCRLF, allowQuote, mustQuote,
				nextStep, quotes);
	}

	/**
	 * get the () values
	 *
	 * @param start
	 *            Startcharacter
	 * @param end
	 *            Endcharacter
	 * @return string of values
	 */
	public String getStringPart(Character start, Character end) {
		int count = 1;
		Character current = null;
		int pos;
		if (getCurrentChar() == start) {
			pos = buffer.position();
			isString = true;
		} else {
			isString = !isString;
			pos = buffer.position() - 1;
		}
		while (!isEnd()) {
			current = next();
			if (current.compareTo(end) == 0) {
				count--;
				if (count == 0) {
					next();
					return ((BufferedBuffer)this.buffer).substring(pos, buffer.position() - pos);
				}
				continue;
			}
			if (current.compareTo(start) == 0) {
				count++;
			}
		}
		return null;
	}

	@Override
	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
	}

	@Override
	public void parseToEntity(AbstractList<?> entity) {}

	public boolean isString() {
		return isString;
	}

	public StringTokener withString(boolean isString) {
		this.isString = isString;
		return this;
	}

	public StringTokener withLength(int length) {
		((BufferedBuffer)this.buffer).withLength(length);
		return this;
	}

	public ArrayList<String> getStringList() {
		ArrayList<String> list = new ArrayList<String>();
		String sub;
		do {
			sub = nextString(true, '"');
			if (sub.length() > 0) {
				if (isString()) {
					list.add("\"" + sub + "\"");
				} else {
					list.add(sub);
				}
			}
		} while (sub.length() > 0);
		return list;
	}

	public String getString(String value) {
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() - 1);
		}
		return value;
	}

	public ArrayList<String> getString(String value, boolean split) {
		ArrayList<String> result = new ArrayList<String>();
		if (value.startsWith("\"") && value.endsWith("\"")) {
			result.add(value.substring(1, value.length() - 1));
			return result;
		}
		String[] values = value.split(" ");
		for (String item : values) {
			result.add(item);
		}
		return result;
	}
	
	public void startToken() {
		this.startToken = this.buffer.position();
	}
	
	public char skipChar(char... quotes) {
		char c = getCurrentChar();
		if(quotes == null) {
			return c;
		}
		boolean found;
		do {
			found=false;
			for(int i=0;i<quotes.length;i++) {
				if(quotes[i] == c) {
					found = true;
					break;
				}
			}
			if(found == false) {
				break;
			}
			c = next();
		} while(c!=0);
		return c;
	}
	
	public String getToken(String defaultText) {
		if(this.startToken < 0) {
			nextClean(false);
			return defaultText;
		}
		String token = ((BufferedBuffer)this.buffer).substring(startToken, this.buffer.position() - startToken);
		this.startToken = -1;
		nextClean(false);
		return token;
	}

}
