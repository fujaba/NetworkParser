package de.uniks.networkparser.graph;

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
public class Visibility

{
	public static final Visibility PUBLIC = new Visibility("public");
	public static final Visibility PACKAGE = new Visibility("");
	public static final Visibility PROTECTED = new Visibility("protected");
	public static final Visibility PRIVATE = new Visibility("private");

	public static final Visibility FINAL = new Visibility(" final");
	public static final Visibility ABSTRACT = new Visibility(" abstract");
	public static final Visibility STATIC = new Visibility(" static");

	private String value;

	Visibility(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Visibility withValue(String value) {
		this.value = value;
		return this;
	}

	public static Visibility ref(String value) {
		return new Visibility(value);
	}

	public static Visibility ref(Visibility... value) {
		Visibility first = PUBLIC;
		String seconds = "";
		for (Visibility item : value) {
			if (item == PUBLIC || item == PACKAGE || item == PROTECTED
					|| item == PRIVATE) {
				first = item;
				continue;
			}
			seconds += item.getValue();
		}
		return new Visibility(first + seconds);
	}

	public boolean same(Visibility other) {
		return this.getValue().equalsIgnoreCase(other.getValue());
	}

	public boolean has(Visibility other) {
		return this.getValue().contains(other.getValue());
	}

	@Override
	public String toString() {
		return this.value;
	}
}
