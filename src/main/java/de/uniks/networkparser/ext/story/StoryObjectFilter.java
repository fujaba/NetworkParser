package de.uniks.networkparser.ext.story;

/*
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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class StoryObjectFilter extends Filter {
	private SimpleList<Object> elements = new SimpleList<Object>();
	private SimpleKeyValueList<Object, String> ids = new SimpleKeyValueList<Object, String>();
	private SimpleKeyValueList<String, String> images = new SimpleKeyValueList<String, String>();

	public StoryObjectFilter with(Object... elements) {
		this.elements.add(elements);
		return this;
	}

	@Override
	public int convert(Object entity, String property, Object value, IdMap map, int deep) {
		if (elements.contains(value)) {
			return 1;
		}
		return -1;
	}

	public SimpleList<Object> getElements() {
		return elements;
	}

	public SimpleKeyValueList<Object, String> getIds() {
		return ids;
	}

	public SimpleKeyValueList<String, String> getImages() {
		return images;
	}
}
