package de.uniks.networkparser;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * A factory for creating EntityValue objects.
 * @author Stefan
 */
public class EntityValueFactory {
	
	/**
	 * Gets the cell value.
	 *
	 * @param value the value
	 * @return the cell value
	 */
	public Object getCellValue(Object value) {
		return getCellValue(value, null, null);
	}

	/**
	 * Gets the cell value.
	 *
	 * @param value the value
	 * @param creator the creator
	 * @param property the property
	 * @return the cell value
	 */
	public Object getCellValue(Object value, SendableEntityCreator creator, String property) {
		if (creator != null && property != null) {
			return creator.getValue(value, property);
		}
		return null;
	}
}
