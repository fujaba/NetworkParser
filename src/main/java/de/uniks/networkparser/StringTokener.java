package de.uniks.networkparser;

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
import java.util.ArrayList;

public class StringTokener extends Tokener {
	private boolean isString = true;

	@Override
	public String nextString(char quote, boolean allowCRLF, boolean allowQuote, boolean mustQuote, boolean nextStep) {

		if (quote=='"') {
			if (getCurrentChar() == quote) {
				isString = true;
			} else {
				isString = !isString;
			}
		} else if (getCurrentChar()=='"') {
			isString = true;
			String sub = "";
			StringBuilder sb = new StringBuilder();
			for (;;) {
				sub = super.nextString(quote, allowCRLF, allowQuote, mustQuote, nextStep);
				sb.append(sub);
				if (sub.length()>0&&!sub.endsWith("\"")) {
					sb.append(",");
				} else {
					break;
				}
			}
			return sb.toString();
		}
		return super.nextString(quote, allowCRLF, allowQuote, mustQuote, nextStep);
	}

	/**
	 * get the () values
	 * @param start Startcharacter
	 * @param end Endcharacter
	 * @return string of values
	 */
	public String getStringPart(Character start, Character end) {
    	int count=1;
    	Character current = null;
    	int pos;
    	if (getCurrentChar()==start) {
    		pos=buffer.position();
    		isString = true;
    	} else {
    		isString = !isString;
    		pos=buffer.position()-1;
    	}
		while (!isEnd()) {
			current = next();
			if (current.compareTo(end)==0) {
				count--;
				if (count==0) {
					next();
					return buffer.substring(pos, buffer.position()-pos);
				}
				continue;
			}
			if (current.compareTo(start)==0) {
				count++;
			}
		}
		return null;
    }

	@Override
	public void parseToEntity(AbstractKeyValueList<?, ?> entity) {
	}

	@Override
	public void parseToEntity(AbstractList<?> entity) {
	}

	public boolean isString() {
		return isString;
	}

	public void setString(boolean isString) {
		this.isString = isString;
	}

	public void setLength(int length) {
		this.buffer.withLength(length);
	}

	public ArrayList<String> getStringList() {
		ArrayList<String> list= new ArrayList<String>();
		String sub;
		do{
			sub=nextString('"', true);
			if (sub.length()>0) {
				if (isString()) {
					list.add("\"" +sub+ "\"");
				} else {
					list.add(sub);
				}
			}
		}while (sub.length()>0);
		return list;
	}

	public String getString(String value) {
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length()-1);
		}
		return value;
	}

	public ArrayList<String> getString(String value, boolean split) {
		ArrayList<String> result = new ArrayList<String>();
		if (value.startsWith("\"") && value.endsWith("\"")) {
			result.add(value.substring(1, value.length()-1));
			return result;
		}
		String[] values = value.split(" ");
		for (String item :values) {
			result.add(item);
		}
		return result;
	}
}
